package org.openhab.io.graphql.servlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class QueryParameters {

    private String query;
    private String operationName;
    private Map<String, Object> variables = Collections.emptyMap();

    public static QueryParameters from(Map json) {
        QueryParameters parameters = new QueryParameters();

        parameters.query = (String) json.get("query");
        parameters.operationName = (String) json.get("operationName");
        parameters.variables = getVariables(json.get("variables"));
        return parameters;
    }

    public String getQuery() {
        return query;
    }

    public String getOperationName() {
        return operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public static QueryParameters from(String queryMessage) {
        Gson gson = new GsonBuilder().create();
        Map json = gson.fromJson(queryMessage, Map.class);
        return from(json);
    }

    private static Map<String, Object> getVariables(Object variables) {
        if (variables instanceof Map) {
            Map<?, ?> inputVars = (Map) variables;
            Map<String, Object> vars = new HashMap<>();
            inputVars.forEach((k, v) -> vars.put(String.valueOf(k), v));
            return vars;
        }
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(String.valueOf(variables), Map.class);
    }
}
