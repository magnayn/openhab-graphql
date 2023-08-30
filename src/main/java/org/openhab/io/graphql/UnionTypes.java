package org.openhab.io.graphql;

import org.openhab.io.graphql.mapping.DynamicNameTypeResolver;
import org.openhab.io.graphql.model.GraphqlExecuteCommandResult;

import graphql.schema.idl.TypeRuntimeWiring;

public class UnionTypes {

    public static final TypeRuntimeWiring EXECUTE_COMMAND_RESULT = unionWiringForType(
            GraphqlExecuteCommandResult.class);

    private static TypeRuntimeWiring unionWiringForType(Class<?> unionType) {
        return TypeRuntimeWiring.newTypeWiring(DynamicNameTypeResolver.getTypename(unionType))
                .typeResolver(DynamicNameTypeResolver.typeResolver()).build();
    }
}
