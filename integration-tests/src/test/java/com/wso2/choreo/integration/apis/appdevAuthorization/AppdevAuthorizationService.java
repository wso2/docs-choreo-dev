/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.appdevAuthorization;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.appdevAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.ListRolesResponseDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.RoleGroupMappingResponseDTO;
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

public class AppdevAuthorizationService {

    private static String APPDEV_AUTHZ_BASE_PATH = "authz-mgt/v1.0";

    /**
     * Create a role with permissions.
     *
     * @param runner            Citrus test runner
     * @param client            Citrus http client
     * @param accessToken       Access token
     * @param createRoleRequest CreateRoleRequest object
     * @return CreateRoleResponseDTO object
     * @throws IOException             If an error occurs while reading the response
     */
    public static CreateRoleResponseDTO createRoleWithPermissions(TestActionRunner runner, HttpClient client,
                                                                  String accessToken, HashMap<String, Object> createRoleRequest)
            throws IOException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(createRoleRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getRolesEndpoint())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
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
                                    if (code != HttpStatus.CREATED.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        CreateRoleResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        CreateRoleResponseDTO.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), CreateRoleResponseDTO.class);
    }

    /**
     * Get roles in a project.
     *
     * @param runner    Citrus test runner
     * @param client    Citrus http client
     * @param projectId Project ID
     * @return ListRolesResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static ListRolesResponseDTO getRolesInProject(TestActionRunner runner, HttpClient client,
            String projectId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        URIBuilder uriBuilder = new URIBuilder(getRolesEndpoint());
        uriBuilder.addParameter("filter", getFilterString("projectId", "eq", projectId));

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(getRolesEndpoint())
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
                                        ListRolesResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ListRolesResponseDTO.class);
                                        if (response.getRoles() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ListRolesResponseDTO.class);
    }

    /**
     * Update a role.
     *
     * @param runner            Citrus test runner
     * @param client            Citrus http client
     * @param roleId            Role ID
     * @param updateRoleRequest UpdateRoleRequest object
     * @return CreateRoleResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateRoleResponseDTO updateRole(TestActionRunner runner, HttpClient client,
            String roleId, HashMap<String, Object> updateRoleRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(updateRoleRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(getRolesEndpoint() + "/" + roleId)
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
                                        CreateRoleResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        CreateRoleResponseDTO.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), CreateRoleResponseDTO.class);
    }

    /**
     * Delete a role.
     *
     * @param runner Citrus test runner
     * @param client Citrus http client
     * @param roleId Role ID
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static void deleteRole(TestActionRunner runner, HttpClient client, String roleId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .delete(getRolesEndpoint() + "/" + roleId)
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
     * Map groups to a role.
     *
     * @param runner           Citrus test runner
     * @param client           Citrus http client
     * @param accessToken      Access token
     * @param mapGroupsRequest MapGroupsRequest object
     * @return RoleGroupMappingResponseDTO object
     * @throws IOException             If an error occurs while reading the response
     */
    public static RoleGroupMappingResponseDTO mapGroupsToRole(TestActionRunner runner, HttpClient client,
                                                              String accessToken,
                                                              HashMap<String, Object> mapGroupsRequest)
            throws IOException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(mapGroupsRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getRoleGroupMappingEndpoint())
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
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
                                        RoleGroupMappingResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        RoleGroupMappingResponseDTO.class);
                                        if (response.getRoleGroupMappings() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), RoleGroupMappingResponseDTO.class);
    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getRolesEndpoint() {

        return APPDEV_AUTHZ_BASE_PATH + "/roles";
    }

    private static String getRoleGroupMappingEndpoint() {

        return APPDEV_AUTHZ_BASE_PATH + "/update-role-group-mappings";
    }

    private static String getFilterString(String key, String operator, String value) {

        return key + "+" + operator + "+" + value;
    }
}
