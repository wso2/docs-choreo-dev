package com.wso2.choreo.integration.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.apache.http.HttpHeaders;

public class AccessTokenHandler {
    private String clientId;
    private String clientSecret;

    public AccessTokenHandler(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Obtain a test token to invoke an exposed API
     * 
     * @return Test Token
     * @throws IOException
     * @throws InterruptedException
     */
    public String getTestToken() throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        String authorizationBasicToken = Base64.getEncoder().encodeToString(clientId.concat(":").concat(clientSecret).getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Configuration.STS_ENDPOINT.concat(Constant.TOKEN_ENDPOINT_SUFFIX)))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .header(HttpHeaders.AUTHORIZATION, "Basic ".concat(authorizationBasicToken))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseBody = new JsonParser().parse(response.body()).getAsJsonObject();
        return responseBody.get("access_token").toString().replaceAll("\"", "");
    }
    
}