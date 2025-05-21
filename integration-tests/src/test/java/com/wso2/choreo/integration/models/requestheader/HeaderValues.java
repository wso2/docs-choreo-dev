package com.wso2.choreo.integration.models.requestheader;


import java.util.HashMap;
import java.util.Map;

public class HeaderValues {


    private static final Map<String, String> headerValues = new HashMap<>();



    public HeaderValues setValues(String key, String value) {
        headerValues.put(key, value);
        return this;
    }

    public Map<String, String> getHeaderValues() {
        return headerValues;
    }



}
