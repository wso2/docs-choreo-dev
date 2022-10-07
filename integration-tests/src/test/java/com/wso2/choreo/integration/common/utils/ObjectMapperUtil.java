package com.wso2.choreo.integration.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;

import java.util.HashMap;

public class ObjectMapperUtil {
    private static final Gson GSON = new Gson();

    public static String mapToString(HashMap<String, Object> objectMap) throws JsonProcessingException {
        ObjectMapper componentObjectMapper = new ObjectMapper();
        return componentObjectMapper.writeValueAsString(objectMap);
    }

    public  static String mapToGraphQLQuery(String query) throws JsonProcessingException {
        ObjectMapper componentObjectMapper = new ObjectMapper();
        HashMap<String, String > objectMap = new HashMap<>() {
            {
                put("query", query);
            }
        };
        return componentObjectMapper.writeValueAsString(objectMap);
    }

    public static <T> T mapStringToObject(Class<T> type, String jsonString, String val) {

        if (jsonString.contains("data") && val != null && !val.equals("")) {

            JsonElement je = new JsonParser().parse(jsonString).getAsJsonObject().
                    getAsJsonObject("data").getAsJsonObject(val);
            return GSON.fromJson(je, type);
        }
        return GSON.fromJson(jsonString, type);
    }

    public static <T> T[] mapToCollection(Class<T[]> tClass, String jsonString,String val){
        if (jsonString.contains("data") && val != null && !val.equals("")) {

            JsonElement je = new JsonParser().parse(jsonString).getAsJsonObject().
                    getAsJsonObject("data").getAsJsonArray(val);
            return GSON.fromJson(je, tClass);
        }
        return GSON.fromJson(jsonString, tClass);
    }

}


