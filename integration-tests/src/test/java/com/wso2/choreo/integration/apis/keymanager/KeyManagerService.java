/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 * 
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.apis.keymanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.keymanager.ConfigUpdateResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManagerListResponseDTO;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class KeyManagerService {

    private static String COMPONENT_MANAGE_BASE_PATH = "component-mgt/1.0.0/orgs/";
    private static String APIM_APPDEV_BASE_PATH = "apim-appdev/v1.0/sts";
    private static String KEY_MANAGER_BASE_PATH = "api/am/publisher/v3/key-managers";

    public static KeyGenResponseDTO generateKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, HashMap<String, Object> keyGenRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(keyGenRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getKeyGenURL(projectId, componentId, environmentId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyGenResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyGenResponseDTO.class);
                                        if (response.getClientId() == null || response.getClientSecret() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyGenResponseDTO.class);
    }

    public static ConfigUpdateResponseDTO updateKeysetConfigurations(TestActionRunner runner, HttpClient client,
            String oAuthAppId, HashMap<String, Object> configUpdateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(configUpdateRequest);

        String url = getConfigUpdateURL(oAuthAppId);
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        ConfigUpdateResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ConfigUpdateResponseDTO.class);
                                        if (response.getClientId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ConfigUpdateResponseDTO.class);
    }

    public static KeyGenResponseDTO regenerateKeysets(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, String oAuthAppId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        String url = getKeyRegenerateURL(projectId, componentId, environmentId, oAuthAppId);
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("project_id", projectId);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyGenResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyGenResponseDTO.class);
                                        if (response.getClientId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyGenResponseDTO.class);
    }

    public static KeyManagerListResponseDTO getKeyManagers(TestActionRunner runner, HttpClient client,
            String environmentId) throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        URIBuilder uriBuilder = new URIBuilder(KEY_MANAGER_BASE_PATH);
        uriBuilder.addParameter("organizationId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("environmentId", environmentId);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        KeyManagerListResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        KeyManagerListResponseDTO.class);
                                        if (response.getList() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), KeyManagerListResponseDTO.class);
    }

    public static void addExternalIdpKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, HashMap<String, Object> keyMappingRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        String requestBody = ObjectMapperUtil.mapToString(keyMappingRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getKeyMappingEndpointURL(projectId, componentId, environmentId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()));
    }

    public static String addConflictingExternalIdpKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, HashMap<String, Object> keyMappingRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        String requestBody = ObjectMapperUtil.mapToString(keyMappingRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getKeyMappingEndpointURL(projectId, componentId, environmentId))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CONFLICT)
                                .message()));

        return HttpStatus.CONFLICT.getReasonPhrase();
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getKeyGenURL(String projectId, String componentId, String environmentId) {

        return getKeyManagerCommonURL(projectId, componentId, environmentId) + "/generate";
    }

    private static String getKeyRegenerateURL(String projectId, String componentId, String environmentId,
            String oAuthAppId) {
        return getKeyManagerCommonURL(projectId, componentId, environmentId) + "/generate/" + oAuthAppId;
    }

    private static String getKeyManagerCommonURL(String projectId, String componentId, String environmentId) {
        return COMPONENT_MANAGE_BASE_PATH + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE)
                + "/projects/" + projectId + "/components/" + componentId + "/environments/" + environmentId
                + "/key-sets";
    }

    private static String getKeyMappingEndpointURL(String projectId, String componentId, String environmentId) {

        return getKeyManagerCommonURL(projectId, componentId, environmentId) + "/map";
    }

    private static String getConfigUpdateURL(String oAuthAppId) {

        return APIM_APPDEV_BASE_PATH + "/oauth-applications/" + oAuthAppId;
    }

}