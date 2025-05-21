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

package com.wso2.choreo.integration.apis.configurationservice;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.configservice.ConfigurationGroup;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Service class for Choreo configuration service.
 */
public class ConfigurationService {

    private static final String CONFIG_SVC_BASE_PATH = "config-svc/v1.0/configs";
    private static final String DEFAULT_CONFIG_SVC_USER_GROUP = "internal_user";

    /**
     * Get configuration groups in a component.
     *
     * @param runner     Citrus test runner
     * @param client     Citrus http client
     * @param projectId  Project ID
     * @param componentId Component ID
     * @return List of configuration groups
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException              If an error occurs while reading the response
     * @throws URISyntaxException       If an error occurs while building the URI
     */
    public static List<ConfigurationGroup> getConfigGroupsInComponent(TestActionRunner runner, HttpClient client,
            String projectId, String componentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        URIBuilder uriBuilder = new URIBuilder(getConfigGroupsEndpoint());
        uriBuilder.addParameter("projectId", projectId);
        uriBuilder.addParameter("componentId", componentId);
        uriBuilder.addParameter("type", DEFAULT_CONFIG_SVC_USER_GROUP);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        List<ConfigurationGroup> response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                new TypeReference<List<ConfigurationGroup>>(){}
                                                );
                                        if (response.size() == 0) {
                                            throw new RuntimeException("No config groups available for the component");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));
                                return new ObjectMapper()
                .readValue(responseDTO.get(), new TypeReference<List<ConfigurationGroup>>(){});

    }

    /**
     * Get configuration group with values.
     *
     * @param runner   Citrus test runner
     * @param client   Citrus http client
     * @param groupUuid Group UUID
     * @return Configuration group with values
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException              If an error occurs while reading the response
     * @throws URISyntaxException       If an error occurs while building the URI
     */
    public static ConfigurationGroup getConfigGroupsWithValues(TestActionRunner runner, HttpClient client,
            String groupUuid) throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(getConfigGroupsEndpoint() + "/" + groupUuid)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        ConfigurationGroup response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ConfigurationGroup.class);
                                        if (response.getGroupUuid() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ConfigurationGroup.class);
    }

    public static ConfigurationGroup updateConfigGroup(TestActionRunner runner, HttpClient client, 
            ConfigurationGroup updatedConfigGroup) throws TokenRetrievalException, IOException, URISyntaxException {
        AtomicReference<String> responseDTO = new AtomicReference<>();

        String requestBody = ObjectMapperUtil.mapObjectToString(updatedConfigGroup);
        
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(getConfigGroupsEndpoint() + "/" + updatedConfigGroup.getGroupUuid())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        ConfigurationGroup response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ConfigurationGroup.class);
                                        if (response.getGroupUuid() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ConfigurationGroup.class);
    }

    public static ConfigurationGroup createConfigGroup(TestActionRunner runner, HttpClient client, ConfigurationGroup configurationGroup)
        throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapObjectToString(configurationGroup);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getConfigGroupsEndpoint())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate(((message, testContext) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.CREATED.value()){
                                        throw new ValidationException("Unexpected HTTP Response Status Code:" + code);
                                    }
                                    try {
                                        ConfigurationGroup response =  new ObjectMapper()
                                                .readValue(message.getPayload().toString(), ConfigurationGroup.class);
                                        if (response.getGroupUuid() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }))
                )
        );

        return new ObjectMapper().readValue(responseDTO.get(), ConfigurationGroup.class);
    }

    public static void deleteConfigGroup(TestActionRunner runner, HttpClient client, String configurationGroupId)
            throws TokenRetrievalException, IOException, URISyntaxException {
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .delete(getConfigGroupsEndpoint() + "/" + configurationGroupId)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                )
        );
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getConfigGroupsEndpoint() {
        return CONFIG_SVC_BASE_PATH + "/groups";
    }
}
