package com.wso2.choreo.integration.tests.anomalyDetector;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class implements Runnable and is used to invoke the passthrough component for the backend failure
 * anomaly detection test
 */
public class InvokePassthroughComponent implements Runnable {

    private String postEndpoint;
    private String authorizationBearerToken;
    private String requestBody;

    InvokePassthroughComponent(String postEndpoint, String authorizationBearerToken, String requestBody){
        this.postEndpoint = postEndpoint;
        this.authorizationBearerToken = authorizationBearerToken;
        this.requestBody = requestBody;
    }

    @Override
    public void run() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                                 .uri(URI.create(postEndpoint))
                                 .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                 .header("Authorization", "Bearer " + authorizationBearerToken)
                                 .header("accept", "application/json")
                                 .header("Content-Type", "*/*")
                                 .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
  }
}
