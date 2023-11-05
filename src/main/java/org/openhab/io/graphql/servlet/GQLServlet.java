package org.openhab.io.graphql.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.openhab.io.graphql.GQLSystem;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import graphql.ExecutionInput;
import io.swagger.v3.core.util.Json;

@NonNullByDefault
@WebServlet(name = "GQLServlet", urlPatterns = { "/graphql" })
@Component(service = HttpServlet.class, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class GQLServlet extends WebSocketServlet {
    private static final long serialVersionUID = -1210354558091063207L;
    private final Logger logger = LoggerFactory.getLogger(GQLServlet.class);

    private final GQLSystem system;
    private @NonNullByDefault({}) BundleContext bundleContext;

    @Activate
    public GQLServlet(ComponentContext componentContext, @Reference GQLSystem system) {
        this.system = system;
        this.bundleContext = componentContext.getBundleContext();
    }

    @Deactivate
    protected void deactivate() {

    }

    /**
     * Servlet handler. Shelly1: http request, Shelly2: WebSocket call
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse resp)
            throws ServletException, IOException, IllegalArgumentException {

        /*
         * return response.encoding(StandardCharsets.UTF_8.name()) //
         * .header(
         * .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
         * .header("Access-Control-Allow-Credentials", "true")
         * .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
         * .header("Access-Control-Max-Age", "1209600");
         */

        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            resp.addHeader("Sec-WebSocket-Protocol", "graphql-transport-ws");
            super.service(request, resp);
            return;
        }

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods", "*");
        resp.addHeader("Access-Control-Allow-Headers", "*");

        if (request.getMethod().equalsIgnoreCase("GET")) {
            processPlaygrounds(resp);
            return;
        }

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            resp.setStatus(200);
            return;
        }

        var body = request.getReader().lines().collect(Collectors.joining());

        var graphql = system.getGraphQL();
        // .queryExecutionStrategy(AsyncExecutionStrategy(DebugDataFetcherExceptionHandler())).build()
        Gson gson = new GsonBuilder().create();
        Map data = gson.fromJson(body, Map.class);

        if (data == null) {
            logger.warn("No data provided");
            logger.warn(body);
            return;
        }

        ExecutionInput.Builder ex = new ExecutionInput.Builder();

        ex.query(data.get("query").toString());

        if (data.containsKey("variables")) {
            ex.variables((Map<String, Object>) data.get("variables"));
        }

        if (data.containsKey("operationName") && data.get("operationName") != null) {
            ex.operationName(data.get("operationName").toString());
        }

        var executionResult = graphql.execute(ex);

        /*
         * builder -> builder
         * .query(query)
         * .variables(variables)
         * // .operationName(op)
         * // .context(context)
         * }
         * return executionResult.await()
         */

        var result = executionResult.toSpecification();

        String json = Json.mapper().writeValueAsString(result);
        resp.setStatus(200);
        resp.getWriter().write(json);
    }

    private void processPlaygrounds(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().append(getGraphqlPlaygroundsContent());
        resp.getWriter().close();
    }

    /*
     * @Override
     * public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
     * {
     * response.getWriter().println("HTTP GET method not implemented.");
     * }
     */

    /**
     * WebSocket: register Shelly2RpcSocket class
     */
    @Override
    public void configure(@Nullable WebSocketServletFactory factory) {
        if (factory != null) {
            factory.getPolicy().setIdleTimeout(15000);
            factory.setCreator(new Shelly2WebSocketCreator(system));
            factory.register(GQLWebSocket.class);
        }
    }

    private String getGraphqlPlaygroundsContent() throws IOException {
        final URL index = bundleContext.getBundle().getEntry("/playgrounds/index.html");

        try (InputStream inputStream = index.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static class Shelly2WebSocketCreator implements WebSocketCreator {
        private final Logger logger = LoggerFactory.getLogger(Shelly2WebSocketCreator.class);

        GQLSystem system;

        public Shelly2WebSocketCreator(GQLSystem system) {

            this.system = system;
        }

        @Override
        public Object createWebSocket(@Nullable ServletUpgradeRequest req, @Nullable ServletUpgradeResponse resp) {
            logger.debug("WebSocket: Create socket from servlet");
            return new GQLWebSocket(system);
        }
    }
}
