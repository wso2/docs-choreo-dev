package com.wso2.choreo.integration.tests.byoc;

import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.response.Response;

public class TestHelper {

    public static Movie[] getMovies(String invokeURL, String apiKey) {
        String apiInvocationRequestURI = invokeURL+ "/movies";
        Response response = HttpClientUtil.httpGET(apiInvocationRequestURI, "", apiKey);
        return ObjectMapperUtil.mapToCollection(Movie[].class, response.getRes(), "");
    }

    static class Movie {
        int id;
        String name;
        double ratings;
    }
}
