/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */


package com.wso2.choreo.integration.common.choreoproject;

import com.wso2.choreo.integration.common.exceptions.APIKeyGenerationCheckException;
import com.wso2.choreo.integration.common.exceptions.ApiKeyNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ComponentInvokeInformationCheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.http.HttpClient;

/**
 * A class to represent a Rest API Choreo component
 */
public class RestApiChoreoComponent extends ChoreoComponent {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Logger log = LogManager.getLogger(RestApiChoreoComponent.class);

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
            InterruptedException, APIKeyGenerationCheckException, ApiKeyNotFoundException, InvokeInformationNotFoundException, InvokeAPICheckException {
      InvokeInformation invokeInformation = getInvokeInformation(accessToken, componentType, environment);
        String requestURI = invokeInformation.getInvokeUrl();
        if (requestURI == null) {
            throw new InvokeInformationNotFoundException();
        }
        requestURI = requestURI.concat("/")
                .concat("greeting")
                .concat("?name=testUser");
        // Escaping the quotations
        String apiKey = getAPIKeyForInvoke(accessToken, invokeInformation.getApiId(),
                environment).replace("\"", "");
        int iteration = 0;
        HttpGet request = new HttpGet(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);

        while (iteration < count) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                    // Adding a sleep for invocation, otherwise upstream connect error occurs
                    log.debug("API is not deployed yet, and waiting to retry");
                    Thread.sleep(3000);
                    continue;
                }
                if (statusCode != HttpStatus.OK.value()) {
                    throw new InvokeAPICheckException(statusCode, responseBody);
                }
                // Waiting 2 seconds to avoid choreo extenstion sampling
                if (iteration % 2 == 0) {
                    Thread.sleep(1000);
                }
                iteration++;
            }
        }
    }
}
