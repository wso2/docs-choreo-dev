/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package com.wso2.choreo.integration.apis.devops;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.message.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.devops.Dataplane;
import com.wso2.choreo.integration.models.devops.EnvironmentWithClustersListDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Implements Devops Portal API calls and their response validations.
 */
@Log4j2
public class DevopsPortalApi extends ControlPlaneAPI {

    private static final String DEVOPS_ENDPOINT = THEME_EP + "/devops/1.0.0/api/v1";

    /**
     * Create environment variables map for component. Updates if already exists
     *
     * @param runner Test action runner
     * @param accessToken Access token
     * @param varMap Environment Variables Map
     * @param componentId Component ID
     * @param environmentId Environment ID which the variables are applied to
     * @param releaseId Release ID
     * @param orgUuid Organization UUID
     * @param projectId Project ID
     * @return Created or updated environment variable map
     */
    public static Map<String, String> createUpdateEnvVariables(TestActionRunner runner, String accessToken,
                                                               Map<String, String> varMap, String componentId,
                                                               String environmentId, String releaseId, String orgUuid,
                                                               String projectId) {

        final Map<String, String> envVariableMap = new HashMap<>();
        final String url = "/components/integration/" + componentId + "/release/" + releaseId + "/environment-variables";
        Map<String, Map<String, String>> payloadMap = new HashMap<>();
        payloadMap.put("data", varMap);
        String payload = ObjectMapperUtil.mapObjectToString(payloadMap);

        runner.$(repeatOnError()
                .until("i = 12")
                .index("i")
                .autoSleep(5000)
                .actions((http().client(DEVOPS_ENDPOINT)
                .send()
                .put(url)
                .queryParam("organization_id", orgUuid)
                .queryParam("project_id", projectId)
                .queryParam("env_id", environmentId)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)))));

        runner.$(http()
                .client(DEVOPS_ENDPOINT)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createIntegrationComponent/environment_variable_response.json"))
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data");
                    Gson gson = new Gson();
                    Map<String, String> map = (Map<String, String>) gson.fromJson(component, Map.class);
                    envVariableMap.putAll(map);
                }));
        return envVariableMap;
    }

    /**
     * Retrieves the environment variable map for the given component
     * @param runner Test action runner
     * @param accessToken Access token
     * @param componentId Component ID
     * @param environmentId Environment ID
     * @param releaseId Release ID
     * @param orgUuid Organization UUID
     * @param projectId Project ID
     * @return Environment Variable Map
     */
    public static Map<String, String> getEnvVariables(TestActionRunner runner, String accessToken, String componentId,
                                                      String environmentId, String releaseId, String orgUuid,
                                                      String projectId) {

        final Map<String, String> envVariableMap = new HashMap<>();
        final String url = "/components/integration/" + componentId + "/release/" + releaseId + "/environment-variables";

        runner.$(repeatOnError()
                .until("i = 12")
                .index("i")
                .autoSleep(5000)
                .actions((http().client(DEVOPS_ENDPOINT).send().get(url)
                                .queryParam("organization_id", orgUuid)
                                .queryParam("project_id", projectId)
                                .queryParam("env_id", environmentId)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)))));

        runner.$(http()
                .client(DEVOPS_ENDPOINT)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createIntegrationComponent/environment_variable_response.json"))
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data");
                    Gson gson = new Gson();
                    Map<String, String> map = (Map<String, String>) gson.fromJson(component, Map.class);
                    envVariableMap.putAll(map);
                }));
        return envVariableMap;
    }

    /**
     * Create or update secret for component
     * @param runner Test action runner
     * @param accessToken Access token
     * @param varMap Environment Variables Map
     * @param componentId Component ID
     * @param environmentId Environment ID which the variables are applied to
     * @param releaseId Release ID
     * @param orgUuid Organization UUID
     * @param projectId Project ID
     * @return Created or updated secrets
     */
    public static JsonArray createOrUpdateMiSecret(TestActionRunner runner, String accessToken,
            Map<String, String> varMap, String componentId,
            String environmentId, String releaseId, String orgUuid,
            String projectId) {

        final JsonArray secrets = new JsonArray();

        final String url = "/components/integration/" + componentId + "/release/" + releaseId + "/secrets";
        Map<String, Map<String, String>> payloadMap = new HashMap<>();
        payloadMap.put("data", varMap);
        String payload = ObjectMapperUtil.mapObjectToString(payloadMap);

        runner.$(repeatOnError()
                .until("i = 12")
                .index("i")
                .autoSleep(5000)
                .actions((http().client(DEVOPS_ENDPOINT)
                        .send()
                        .put(url)
                        .queryParam("organization_id", orgUuid)
                        .queryParam("project_id", projectId)
                        .queryParam("env_id", environmentId)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(payload)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)))));

        runner.$(http()
                .client(DEVOPS_ENDPOINT)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createIntegrationComponent/environment_variable_response.json"))
                .validate((message, context) -> {
                    secrets.addAll(new JsonParser().parse((String) message.getPayload()).getAsJsonArray());
                }));
        return secrets;
    }

    /**
     * Retrieves dataplanes in an organization
     * @param runner Test action runner
     * @param accessToken Access token
     * @param orgUuid Organization UUID
     * @return List of dataplanes
     */
    public static List<Dataplane> getDataplaneList(TestActionRunner runner, String accessToken, String orgUuid)
                    throws JsonMappingException, JsonProcessingException {
        
        String url = "/organizations/" + orgUuid + "/dataplanes";

        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions((http().client(DEVOPS_ENDPOINT)
                        .send()
                        .get(url)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)))));

        runner.$(http()
                .client(DEVOPS_ENDPOINT)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    try {
                                List<Dataplane> response = new ObjectMapper()
                                        .readValue(message.getPayload().toString(),
                                        new TypeReference<List<Dataplane>>(){}
                                        );
                                if (response.size() == 0) {
                                throw new RuntimeException("No dataplanes available");
                                }
                                responseDTO.set(message.getPayload(String.class));
                        } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                        }
                }));

        return new ObjectMapper()
                .readValue(responseDTO.get(), new TypeReference<List<Dataplane>>(){});
    }

    /**
     * Retrieves environments with clusters in an organization
     * @param runner Test action runner
     * @param accessToken Access token
     * @param orgUuid Organization UUID
     * @param projectUuid Project UUID
     * @return List of environments with clusters
     */
    public static EnvironmentWithClustersListDTO getEnvironmentsWithClusters(TestActionRunner runner, String accessToken,
                    String orgUuid, String projectUuid) throws JsonMappingException, JsonProcessingException {
        
        String url = "/organizations/" + orgUuid + "/environments";

        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions((http().client(DEVOPS_ENDPOINT)
                        .send()
                        .get(url)
                        .queryParam("organization_id", orgUuid)
                        .queryParam("project_id", projectUuid)
                        .queryParam("include", "environment_clusters")
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)))));

        runner.$(http()
                .client(DEVOPS_ENDPOINT)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    try {
                        EnvironmentWithClustersListDTO response = new ObjectMapper()
                                        .readValue(message.getPayload().toString(),
                                        EnvironmentWithClustersListDTO.class
                                        );
                                if (response.getData().size() == 0) {
                                throw new RuntimeException("No environments available");
                                }
                                responseDTO.set(message.getPayload(String.class));
                        } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                        }
                }));

        return new ObjectMapper()
                .readValue(responseDTO.get(), EnvironmentWithClustersListDTO.class);
    }

    

    public static void configureWebappShortUrl(TestActionRunner runner, String accessToken, String componentId,
                                              String releaseId, String orgUuid, String projectId, String shortUrl) {

        final String url = "/components/" + componentId + "/release/" + releaseId + "/cdp-webapp-short-url";
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("short_url_name", shortUrl);
        String payload = ObjectMapperUtil.mapObjectToString(payloadMap);

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(5000)
                .actions((http().client(DEVOPS_ENDPOINT)
                        .send()
                        .put(url)
                        .queryParam("organization_id", orgUuid)
                        .queryParam("project_id", projectId)
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(payload)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)))));

        runner.$(http()
                .client(DEVOPS_ENDPOINT)
                .receive()
                .response(HttpStatus.OK));
    }
}
