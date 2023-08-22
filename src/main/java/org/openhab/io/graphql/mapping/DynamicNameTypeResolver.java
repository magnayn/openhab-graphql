package org.openhab.io.graphql.mapping;

import java.util.Objects;

import org.openhab.io.graphql.mapping.wrapper.IClassName;

import graphql.schema.TypeResolver;
import graphql.schema.idl.TypeRuntimeWiring;

public class DynamicNameTypeResolver {

    public static final TypeRuntimeWiring THING_INTERFACE = typeWiring("ThingInterface");
    public static final TypeRuntimeWiring ITEM = typeWiring("Item");
    public static final TypeRuntimeWiring STATE = typeWiring("State");
    public static final TypeRuntimeWiring ITEM_METADATA = typeWiring("ItemMetadata");

    public static final TypeRuntimeWiring EVENT_WIRING = typeWiring("Event");
    public static final TypeRuntimeWiring ITEM_EVENT_WIRING = typeWiring("ItemEvent");

    public static String getTypename(Class<?> graphqlType) {
        String[] parts = graphqlType.toString().split("\\.");
        String className = parts[parts.length - 1];
        if (className.startsWith("Graphql")) {
            return className.substring("Graphql".length());
        }
        throw new IllegalArgumentException(String.format("Provided type %s is not a GraphQL type", graphqlType));
    }

    private static TypeRuntimeWiring typeWiring(String name) {
        return TypeRuntimeWiring.newTypeWiring(name).typeResolver(DynamicNameTypeResolver.typeResolver()).build();
    }

    public static TypeResolver typeResolver() {
        return env -> {
            var object = env.getObject();
            if (object instanceof IClassName) {
                var name = ((IClassName) env.getObject()).get__typename();
                return Objects.requireNonNull(env.getSchema().getObjectType(name));
            }
            String className = env.getObject().getClass().getSimpleName();
            String graphqlTypename = className.replace("Graphql", "");
            return Objects.requireNonNull(env.getSchema().getObjectType(graphqlTypename));
        };
    }
}
