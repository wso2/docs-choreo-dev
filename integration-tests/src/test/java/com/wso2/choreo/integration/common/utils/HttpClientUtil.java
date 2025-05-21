package com.wso2.choreo.integration.common.utils;

import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.requestheader.HeaderValues;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Map;


public class HttpClientUtil {

    private static final Logger log = LogManager.getLogger(HttpClientUtil.class);

    private static Response sendRequest(HttpUriRequest request) {

        String responseBody = null;
        int statusCode = 0;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build(); CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            statusCode = response.getStatusLine().getStatusCode();
            if (entity != null) {
                responseBody = EntityUtils.toString(entity);
                log.info(request.getURI().toString(),responseBody,statusCode);
            }
        } catch (IOException e) {
         log.error(e.getMessage());
        }
        return Response.builder().res(responseBody).statusCode(statusCode).build();
    }

    public static Response httpGET(String url, String accessToken, String apiKey) {
        HttpGet request = new HttpGet(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        return sendRequest(request);
    }

    public static Response httpPOST(String url, String payload, String accessToken, String apiKey) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        request.setEntity(new StringEntity(payload));
        return sendRequest(request);
    }

    public static Response httpPOSTFormData(String url, Map<String, String>  payload, String accessToken, String apiKey) {
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader("API-Key", apiKey);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        if(!payload.isEmpty()){
            for (String key: payload.keySet()){
                entityBuilder.addPart(key, new StringBody(payload.get(key), ContentType.MULTIPART_FORM_DATA));
            }
        }
        request.setEntity(entityBuilder.build());
        return sendRequest(request);
    }

    public static Response httpPUT(String url, String payload, String accessToken, String apiKey) throws IOException {
        HttpPut request = new HttpPut(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        request.setEntity(new StringEntity(payload));
        return sendRequest(request);
    }

    public static Response httpPUT(String url, HttpEntity payload, HeaderValues headerValues)  {
        HttpPut request = new HttpPut(url);
        setHeader(request, headerValues);
        request.setEntity(payload);
        return sendRequest(request);
    }

    public static Response httpDELETE(String url, String accessToken, String apiKey) {
        HttpDelete request = new HttpDelete(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);
        return sendRequest(request);
    }

    private static void setHeader(HttpUriRequest request, HeaderValues headerValues) {
        Map<String, String> values = headerValues.getHeaderValues();
        values.keySet().forEach(k -> request.setHeader(k, values.get(k)));
    }
}
