package org.openhab.io.graphql.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.openhab.io.graphql.GQLSystem;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import graphql.ExecutionInput;
import graphql.ExecutionResult;

@NonNullByDefault
@WebSocket(maxIdleTime = Integer.MAX_VALUE)
public class GQLWebSocket {
    private final Logger logger = LoggerFactory.getLogger(GQLWebSocket.class);

    private final AtomicReference<Subscription> subscriptionRef = new AtomicReference<>();

    private @Nullable Session session;

    private GQLSystem system;

    public GQLWebSocket(GQLSystem system) {
        logger.info("GQLWebSocket()");
        this.system = system;
    }

    /**
     * Regular constructor for Thing and Discover handler
     *
     * @param thingName Thing/Service name
     * @param thingTable
     * @param deviceIp IP address for the device
     */

    /**
     * Web Socket is connected, lookup thing and create connectLatch to synchronize first sendMessage()
     *
     * @param session Newly created WebSocket connection
     */
    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.info("GQLWebSocket::onConnect()");
        if (session.getRemoteAddress() == null) {
            logger.debug("Invalid inbound WebSocket connect");
            session.close(StatusCode.ABNORMAL, "Invalid remote IP");
            return;
        }
        var v = session.getUpgradeRequest().getProtocolVersion();
        // session.getUpgradeResponse().setAcceptedSubProtocol(v);

        /*
         * session.getUpgradeResponse().addHeader("Sec-WebSocket-Version", v);
         * session.getUpgradeResponse().addHeader("Sec-WebSocket-Protocol", "graphql-transport-ws");
         */
        this.session = session;
    }

    /**
     * Inbound WebSocket message
     *
     * @param session WebSpcket session
     * @param query Textial API message
     */
    @OnWebSocketMessage
    public void onText(Session session, String query) throws IOException {
        logger.info("Websocket said {}", query);

        Gson gson = new GsonBuilder().create();
        Map json = gson.fromJson(query, Map.class);

        if (json.get("type").equals("connection_init")) {
            session.getRemote().sendString("{\"type\":\"connection_ack\"}");
            return;
        }

        if (json.get("type").equals("ping")) {
            session.getRemote().sendString("{\"type\":\"pong\"}");
            return;
        }

        if (json.get("type").equals("subscribe")) {

            final String id = (String) json.get("id");

            QueryParameters parameters = QueryParameters.from((Map) json.get("payload"));

            ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(parameters.getQuery())
                    .variables(parameters.getVariables()).operationName(parameters.getOperationName()).build();

            //
            // In order to have subscriptions in graphql-java you MUST use the
            // SubscriptionExecutionStrategy strategy.
            //

            ExecutionResult executionResult = system.getGraphQL().execute(executionInput);

            Publisher<ExecutionResult> stockPriceStream = executionResult.getData();

            stockPriceStream.subscribe(new org.reactivestreams.Subscriber<ExecutionResult>() {

                @Override
                public void onSubscribe(@NonNullByDefault({}) org.reactivestreams.Subscription sub) {
                    subscriptionRef.set(sub);
                    request(1);
                }

                @Override
                public void onNext(ExecutionResult er) {
                    logger.info("Sending stick price update");
                    try {
                        Gson gson = new GsonBuilder().create();

                        Map update = new HashMap<String, Object>();
                        update.put("id", id);
                        update.put("type", "next");
                        update.put("payload", er);

                        session.getRemote().sendString(gson.toJson(update));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    request(1);
                }

                @Override
                public void onError(@NonNullByDefault({}) Throwable throwable) {
                    logger.info("Subscription threw an exception", throwable);
                    session.close();
                }

                @Override
                public void onComplete() {
                    logger.info("Subscription complete");
                    session.close();
                }
            });
        }
    }

    private void request(int n) {
        Subscription subscription = subscriptionRef.get();
        if (subscription != null) {
            subscription.request(n);
        }
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    /**
     * Web Socket closed, notify thing handler
     *
     * @param statusCode StatusCode
     * @param reason Textual reason
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        logger.info("GQLWebSocket::onClose()");
    }

    /**
     * WebSocket error handler
     *
     * @param cause WebSocket error/Exception
     */
    @OnWebSocketError
    public void onError(Throwable cause) {
        logger.info("GQLWebSocket::onError()");
        logger.error("error", cause);
    }
}
