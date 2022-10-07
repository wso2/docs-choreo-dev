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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.GraphQLException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ControlPlaneAPIs {
    private final static Logger log = LoggerFactory.getLogger(ControlPlaneAPIs.class);

    public static JsonObject callGraphQL(String accessToken, String gqlQuery) throws GraphQLException {
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", gqlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

            String choreoCpProjectsEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_CP_PROJECTS_ENDPOINT);
            HttpPost request = new HttpPost(choreoCpProjectsEndpoint.concat("/graphql"));

            request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

            StringEntity requestEntity = new StringEntity(
                    requestBody,
                    ContentType.APPLICATION_JSON);
            request.setEntity(requestEntity);

            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                log.debug(responseBody);
                if (statusCode != HttpStatus.SC_OK) {
                    throw new GraphQLException(statusCode, responseBody);
                }

                return new JsonParser().parse(responseBody).getAsJsonObject();
            }
        } catch (IOException e) {
            throw new GraphQLException(e);
        }
    }

    public  static void waitForComponentCreationSuccess(String accessToken, String choreoOrgHandle, String projectId,
                                                String componentId)
            throws ComponentCreationStatusCheckException,
            ComponentCreationTimeoutException {
        String choreoEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);
        String requestURI = choreoEndpoint.concat("/orgs/" + choreoOrgHandle + "/projects/" + projectId + "/components/" + componentId + "/init/status");



        HttpGet request = new HttpGet(requestURI);

        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);



        for (int i = 0; i < 15; ++i) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode == HttpStatus.SC_OK) {
                    JsonObject dataJsonObject = new JsonParser().parse(responseBody).getAsJsonObject().
                            getAsJsonObject("data");

                    String creationStatus =
                            dataJsonObject.get("status").isJsonNull() ? "" : dataJsonObject.get("status").getAsString();
                    if (creationStatus.equals("completed")) {
                        return;
                    }
                }
            } catch (IOException e) {
                throw new ComponentCreationStatusCheckException(e);
            }

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new ComponentCreationStatusCheckException(e);
            }
        }

        throw new ComponentCreationTimeoutException();
    }
}
