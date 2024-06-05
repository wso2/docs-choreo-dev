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

package com.wso2.choreo.integration.apis.appdevUserManagement;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.appdevUserManagement.CreateUserStoreResponseDTO;
import com.wso2.choreo.integration.models.appdevUserManagement.UserStore;
import com.wso2.choreo.integration.models.appdevUserManagement.UsersListResponseDTO;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Service class for Appdev User Management
 */
public class AppdevUserManagementService {

    private static String APPDEV_USER_STORE_MGT_BASE_PATH = "user-store-mgt/v1.0/user-stores";

    /**
     * Create a user store in an environment
     * 
     * @param runner                 Citrus test runner
     * @param client                 Citrus http client
     * @param environmentId          Environment ID
     * @param createUserStoreRequest Request body to create user store
     * @return CreateUserStoreResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateUserStoreResponseDTO createUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String environmentId, LinkedMultiValueMap<String, Object> createUserStoreRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {
        
        AtomicReference<String> responseDTO = new AtomicReference<>();

        URIBuilder uriBuilder = new URIBuilder(APPDEV_USER_STORE_MGT_BASE_PATH);
        uriBuilder.addParameter("orgId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("associateToEnv", environmentId);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(createUserStoreRequest),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        CreateUserStoreResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        CreateUserStoreResponseDTO.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), CreateUserStoreResponseDTO.class);
    }

    /**
     * Delete a user store in an environment
     * 
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param userStoreId User store ID
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static void deleteUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String userStoreId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder(getUserStoreURL(userStoreId));
        uriBuilder.addParameter("orgId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .delete(uriBuilder.build().toString())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.NO_CONTENT)
                                .message()));
    }

    /**
     * Re-create a user store in an environment
     * 
     * @param runner                 Citrus test runner
     * @param client                 Citrus http client
     * @param userStoreId            User store ID
     * @param createUserStoreRequest Request body to create user store
     * @return CreateUserStoreResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateUserStoreResponseDTO reCreateUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String userStoreId, LinkedMultiValueMap<String, Object> createUserStoreRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        URIBuilder uriBuilder = new URIBuilder(getUserStoreURL(userStoreId));
        uriBuilder.addParameter("orgId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));

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
                                .contentType(String.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(createUserStoreRequest),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        CreateUserStoreResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        CreateUserStoreResponseDTO.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), CreateUserStoreResponseDTO.class);
    }

    /**
     * List user stores in an environment
     * 
     * @param runner         Citrus test runner
     * @param client         Citrus http client
     * @param environmentId  Environment ID
     * @return List of UserStore objects
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static List<UserStore> listUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        URIBuilder uriBuilder = new URIBuilder(getUserStoreAssociationsEndpoint());
        uriBuilder.addParameter("orgId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        uriBuilder.addParameter("environmentId", environmentId);

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
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        List<UserStore> response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        new TypeReference<List<UserStore>>() {
                                                        });
                                        if (response.size() == 0) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), new TypeReference<List<UserStore>>() {
        });
    }

    /**
     * List users in a user store
     * 
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param userStoreId User store ID
     * @return UsersListResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static UsersListResponseDTO listUsersInUserStore(TestActionRunner runner, HttpClient client,
            String userStoreId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        URIBuilder uriBuilder = new URIBuilder(getUsersEndpoint(userStoreId));
        uriBuilder.addParameter("orgId", Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));

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
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        UsersListResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        UsersListResponseDTO.class);
                                        if (response.getResources().size() == 0) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), UsersListResponseDTO.class);
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getUserStoreURL(String userStoreId) {
        return APPDEV_USER_STORE_MGT_BASE_PATH + "/" + userStoreId;
    }

    private static String getUserStoreAssociationsEndpoint() {
        return APPDEV_USER_STORE_MGT_BASE_PATH + "/associations";
    }

    private static String getUsersEndpoint(String usersStoreId) {
        return APPDEV_USER_STORE_MGT_BASE_PATH + "/" + usersStoreId + "/users";
    }

}
