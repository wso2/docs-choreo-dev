package com.wso2.choreo.integration.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapperUtil {
    private static final Gson GSON = new Gson();

    public static String mapToString(HashMap<String, Object> objectMap) throws JsonProcessingException {
        ObjectMapper componentObjectMapper = new ObjectMapper();
        return componentObjectMapper.writeValueAsString(objectMap);
    }

    public static<T>  String mapObjectToString(T object){

        return  GSON.toJson(object);
    }

    public static String mapToGraphQLQuery(String query) throws JsonProcessingException {
        ObjectMapper componentObjectMapper = new ObjectMapper();
        HashMap<String, String> objectMap = new HashMap<>() {
            {
                put("query", query);
            }
        };
        return componentObjectMapper.writeValueAsString(objectMap);
    }

    public static <T> T mapStringToObject(Class<T> type, String jsonString, String val) {

        if (jsonString.contains("\"data\"") && val != null && !val.equals("")) {

            JsonElement je = new JsonParser().parse(jsonString).getAsJsonObject().
                    getAsJsonObject("data").getAsJsonObject(val);
            return GSON.fromJson(je, type);
        }
        return GSON.fromJson(jsonString, type);
    }

    public static <T> T[] mapToCollection(Class<T[]> tClass, String jsonString, String val) {
        if (jsonString.contains("data") && val != null && !val.equals("")) {

            JsonElement je = new JsonParser().parse(jsonString).getAsJsonObject().
                    getAsJsonObject("data").getAsJsonArray(val);
            return GSON.fromJson(je, tClass);
        }
        return GSON.fromJson(jsonString, tClass);
    }

    public static <T> T[] mapDataToCollection(Class<T[]> tClass, String jsonString, String val) {
        JsonElement je = new JsonParser().parse(jsonString).getAsJsonObject().
                getAsJsonArray(val);
        return GSON.fromJson(je, tClass);
    }


    public static<T> String mapObjectToString(String template, T dto) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(template);
        Writer writer = new StringWriter();
        mustache.execute(writer, dto).flush();
        return writer.toString();
    }

    public static String mapObjectToString(String template, Map<String, String> params) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(template);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

}
