package org.openhab.io.graphql.scalar;

import java.time.DateTimeException;
import java.time.temporal.TemporalAccessor;

import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.util.Kit;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class Base64Scalar {
    public static final GraphQLScalarType INSTANCE;

    private Base64Scalar() {
    }

    static {
        Coercing<byte[], String> coercing = new Coercing<byte[], String>() {
            public String serialize(Object input) throws CoercingSerializeException {
                Object temporalAccessor;
                if (input instanceof TemporalAccessor) {
                    temporalAccessor = (TemporalAccessor) input;
                } else {
                    if (!(input instanceof String)) {
                        throw new CoercingSerializeException(
                                "Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '"
                                        + Kit.typeName(input) + "'.");
                    }

                    // temporalAccessor = this.parseLocalDate(input.toString(), CoercingSerializeException::new);
                }

                try {
                    // return DateScalar.DATE_FORMATTER.format((TemporalAccessor)temporalAccessor);
                    return null;
                } catch (DateTimeException var4) {
                    throw new CoercingSerializeException(
                            "Unable to turn TemporalAccessor into full date because of : '" + var4.getMessage() + "'.");
                }
            }

            public byte[] parseValue(Object input) throws CoercingParseValueException {
                Object temporalAccessor;
                if (input instanceof TemporalAccessor) {
                    temporalAccessor = (TemporalAccessor) input;
                } else {
                    if (!(input instanceof String)) {
                        throw new CoercingParseValueException(
                                "Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '"
                                        + Kit.typeName(input) + "'.");
                    }

                    // temporalAccessor = this.parseLocalDate(input.toString(), CoercingParseValueException::new);
                }

                try {
                    // return LocalDate.from((TemporalAccessor)temporalAccessor);
                    return null;
                } catch (DateTimeException var4) {
                    throw new CoercingParseValueException(
                            "Unable to turn TemporalAccessor into full date because of : '" + var4.getMessage() + "'.");
                }
            }

            public byte[] parseLiteral(Object input) throws CoercingParseLiteralException {
                if (!(input instanceof StringValue)) {
                    throw new CoercingParseLiteralException(
                            "Expected AST type 'StringValue' but was '" + Kit.typeName(input) + "'.");
                } else {
                    // return this.parseLocalDate(((StringValue)input).getValue(), CoercingParseLiteralException::new);
                    return null;
                }
            }

            public Value<?> valueToLiteral(Object input) {
                String s = this.serialize(input);
                return StringValue.newStringValue(s).build();
            }
        };
        INSTANCE = GraphQLScalarType.newScalar().name("Base64").description("Base64 encoding").coercing(coercing)
                .build();
    }
}
