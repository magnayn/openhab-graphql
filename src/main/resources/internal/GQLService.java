/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Application;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.http.HttpHeader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles Hue scenes via the automation subsystem and the corresponding REST interface
 *
 * @author Nigel Magnay - Initial contribution
 */
@NonNullByDefault
@Component(immediate = true, service = GQLService.class)
public class GQLService implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(GQLService.class);
    public static final String CONFIG_PID = "org.openhab.graphql";
    public static final String RESTAPI_PATH = "/graphql";
    public static final String REST_APP_NAME = "GraphQL";

    public GQLService() {

    }

    @PreMatching
    public class RequestInterceptor implements ContainerRequestFilter {
        @NonNullByDefault({})
        @Override
        public void filter(ContainerRequestContext requestContext) {

            /**
             * Jetty returns 415 on any GET request if a client sends the Content-Type header.
             * This is a workaround - stripping it away in the preMatching stage.
             */
            if (HttpMethod.GET.equals(requestContext.getMethod())
                    && requestContext.getHeaders().containsKey(HttpHeader.CONTENT_TYPE.asString())) {
                requestContext.getHeaders().remove(HttpHeader.CONTENT_TYPE.asString());
            }
        }
    }

    public class LogAccessInterceptor implements ContainerResponseFilter {
        @NonNullByDefault({})
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
            /*
             * if (!logger.isDebugEnabled()) {
             * return;
             * }
             */
            logger.debug("REST request {} {}", requestContext.getMethod(), requestContext.getUriInfo().getPath());
            logger.debug("REST response: {}", responseContext.getEntity());
        }
    }

    private final ContainerRequestFilter requestCleaner = new RequestInterceptor();

    /**
     * The Jax-RS application that starts up all REST activities.
     * It registers itself as a Jax-RS Whiteboard service and all Jax-RS resources that are targeting REST_APP_NAME will
     * start up.
     */
    @JaxrsName(REST_APP_NAME)
    private class RESTapplication extends Application {
        private String root;

        RESTapplication(String root) {
            this.root = root;
        }

        @NonNullByDefault({})
        @Override
        public Set<Object> getSingletons() {
            return Set.of(gqlServer, accessInterceptor, requestCleaner);
        }

        Dictionary<String, String> serviceProperties() {
            Dictionary<String, String> dict = new Hashtable<>();
            dict.put(JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE, root);
            return dict;
        }
    }

    private final LogAccessInterceptor accessInterceptor = new LogAccessInterceptor();
    @Reference
    protected @NonNullByDefault({}) GQLServer gqlServer;
    private @Nullable ServiceRegistration<?> eventHandler;
    private @Nullable ServiceRegistration<Application> restService;

    @Activate
    protected void activate(BundleContext bc) {
        logger.info("GraphQLService activated");

        handleEvent(null);

        /*
         * Dictionary<String, Object> properties = new Hashtable<>();
         * properties.put(EventConstants.EVENT_TOPIC, ConfigStore.EVENT_ADDRESS_CHANGED);
         * eventHandler = bc.registerService(EventHandler.class, this, properties);
         * if (cs.isReady()) {
         * handleEvent(null);
         * }
         */
    }

    // Don't restart the service on config change
    @Modified
    protected void modified() {
    }

    @Deactivate
    protected void deactivate() {
        unregisterEventHandler();

        ServiceRegistration<Application> localRestService = restService;
        if (localRestService != null) {
            localRestService.unregister();
        }
    }

    @Override
    public void handleEvent(@Nullable Event event) {
        unregisterEventHandler();

        ServiceRegistration<Application> localRestService = restService;
        if (localRestService == null) {
            RESTapplication app = new RESTapplication(RESTAPI_PATH);
            BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
            restService = context.registerService(Application.class, app, app.serviceProperties());
            logger.info("GraphQL avialable under {}", RESTAPI_PATH);
        }
    }

    private void unregisterEventHandler() {
        ServiceRegistration<?> localEventHandler = eventHandler;
        if (localEventHandler != null) {
            try {
                localEventHandler.unregister();
                eventHandler = null;
            } catch (IllegalStateException e) {
                logger.debug("EventHandler already unregistered", e);
            }
        }
    }
}
