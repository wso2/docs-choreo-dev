package com.wso2.choreo.integration.common.choreoproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * A class to represent Choreo API
 */
public class ChoreoApi {
    private static final String STS_ENDPOINT = Configuration.STS_ENDPOINT;
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String id;

    public ChoreoApi(String id) {
        this.id = id;
    }

    /**
     * Update the lifecycle state of the Choreo API
     *
     * @param accessToken      OAuth token to invoke the Chorea backend
     * @param organizationUuid Choreo organization UUID
     * @param toState          The API lifecycle state
     */
    public void changeApiLifeCycle(String accessToken, String organizationUuid, Constant.apiLIifCycleState toState)
            throws
            IOException, InterruptedException, ApiLifecycleChangeException {
        String requestURI = STS_ENDPOINT.concat("/api/am/publisher/v2/apis/change-lifecycle?organizationId=")
                .concat(organizationUuid).concat("&apiId=").concat(id).concat("&action=")
                .concat(String.valueOf(toState));
        HashMap<String, String> requestBodyMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new ApiLifecycleChangeException(statusCode, response.body());
        }
    }
}
