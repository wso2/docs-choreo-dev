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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * A class to represent a Choreo component API version
 */
public class ApiVersion {
    private static final String STS_ENDPOINT = Configuration.STS_ENDPOINT;
    private static final HttpClient client = HttpClient.newHttpClient();
    private String apiVersion;
    private String proxyName;
    private String proxyUrl;
    private String proxyId;
    private String id;
    private String state;
    private boolean latest;
    private List<AppEnvVersion> appEnvVersions = new ArrayList<>();

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
                .concat(organizationUuid).concat("&apiId=").concat(proxyId).concat("&action=")
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

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    public String getProxyId() {
        return proxyId;
    }

    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public List<AppEnvVersion> getAppEnvVersions() {
        return appEnvVersions;
    }

    public void setAppEnvVersions(List<AppEnvVersion> appEnvVersions) {
        this.appEnvVersions = appEnvVersions;
    }

}
