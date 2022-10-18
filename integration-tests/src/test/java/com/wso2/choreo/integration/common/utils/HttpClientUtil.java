package com.wso2.choreo.integration.common.utils;

import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HttpClientUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpClientUtil.class.getName());

    private static Response getHttpClient(HttpUriRequest request) {
        String responseBody = null;
        int statusCode = 0;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build(); CloseableHttpResponse response = httpClient.execute(request)) {
            statusCode = response.getStatusLine().getStatusCode();
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOGGER.warning(e.getLocalizedMessage());
        }
        return Response.builder().res(responseBody).statusCode(statusCode).build();
    }

    private static Response sendRequest(HttpUriRequest request, int attemptsTillSuccess) {
        Response response = null;
        int statusCode = 0;
        if (attemptsTillSuccess > 0) {
            for (int i = 0; i < attemptsTillSuccess; i++) {
                response = getHttpClient(request);
                statusCode = response.getStatusCode();
                try {
                    System.out.println(i);
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            if ((200 <= statusCode && statusCode <= 206) || (300 <= statusCode && statusCode <= 304)) {
                return response;
            }


        } else {
            response = getHttpClient(request);
        }
        return response;
    }

    public static Response httpGET(String url, String accessToken, String apiKey, int attemptsTillSuccess) {
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        return sendRequest(request, attemptsTillSuccess);
    }

    public static Response httpPOST(String url, String payload, String accessToken, String apiKey, int attemptsTillSuccess) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        request.setEntity(new StringEntity(payload));
        return sendRequest(request, attemptsTillSuccess);
    }

    public static Response httpPUT(String url, String payload, String accessToken, String apiKey, int attemptsTillSuccess) throws IOException {
        HttpPut request = new HttpPut(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        request.setEntity(new StringEntity(payload));
        return sendRequest(request, attemptsTillSuccess);
    }
}
