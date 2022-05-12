package com.wso2.choreo.integration.common.choreoproject;

import com.wso2.choreo.integration.common.email.EmailUtils;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

/**
 * A class to represent a Rest API Choreo component
 */
public class RestApiChoreoComponent extends ChoreoComponent {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final static Logger log = LoggerFactory.getLogger(RestApiChoreoComponent.class);


    /**
     * Invoke the application for a given number of iterations
     *
     * @param accessToken   OAuth token to invoke the Chorea backend
     * @param componentType type of the component
     * @param environment   environment of the deployment
     * @param count         number of times application need to be called
     */
    public void invokeGetApplication(String accessToken, String componentType, String environment, int count) throws
            ComponentInvokeInformationCheckException, NoLatestApiVersionFoundException, IOException,
            InterruptedException, GenerateAPIKeyCheckException, ApiKeyNotFoundException, InvokeInformationNotFoundException, InvokeAPICheckException {
        InvokeInformation invokeInformation = getInvokeInformation(accessToken, componentType, environment);
        String requestURI = invokeInformation.getInvokeUrl()
                .concat("/")
                .concat("greeting")
                .concat("?name=testUser");
        // Escaping the quotations
        String apiKey = getAPIKeyForInvoke(accessToken, invokeInformation.getApiId()).replace("\"","");
        int iteration = 0;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .GET()
                .header("API-Key", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON)
                .build();
        while (iteration < count) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                // Adding a sleep for invocation, otherwise upstream connect error occurs
                log.debug("API is not deployed yet, and waiting to retry");
                Thread.sleep(3000);
                continue;
            }
            if (statusCode != HttpStatus.OK.value()) {
                throw new InvokeAPICheckException(statusCode, response.body());
            }
            iteration++;
        }
    }
}
