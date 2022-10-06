package com.wso2.choreo.integration.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.choreoproject.responses.CreateComponent;

import java.util.HashMap;

public class ObjectMapperUtil {
    private static final Gson GSON = new Gson();

    public static String mapToString(HashMap<String, Object> objectMap) throws JsonProcessingException {
        ObjectMapper componentObjectMapper = new ObjectMapper();
        return componentObjectMapper.writeValueAsString(objectMap);
    }

    public static <T> T mapStringToObject(Class<T> type, String jsonString, String val) {

        if (jsonString.contains("data")) {

            JsonElement je = new JsonParser().parse(jsonString).getAsJsonObject().
                    getAsJsonObject("data").getAsJsonObject(val);
            return GSON.fromJson(je, type);
        }
        return GSON.fromJson(jsonString, type);
    }
}


