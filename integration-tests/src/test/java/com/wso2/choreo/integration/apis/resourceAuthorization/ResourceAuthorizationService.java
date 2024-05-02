package com.wso2.choreo.integration.apis.resourceAuthorization;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateGroupResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GetRoleResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleGroupMappingResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Service class for resource authorization related operations.
 */
public class ResourceAuthorizationService extends ControlPlaneAPI {

    private static String USER_MGT_BASE_PATH = "user-mgt/1.0.0/orgs/";

    /**
     * Create a group in the Choreo organization
     *
     * @param runner        Citrus test runner
     * @param client        Citrus HTTP client
     * @param groupRequest  Group request
     * @return CreateGroupResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static CreateGroupResponseDTO createGroup(TestActionRunner runner, HttpClient client,
            HashMap<String, Object> groupRequest) throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(groupRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getGroupsEndpoint())
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
                                        CreateGroupResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        CreateGroupResponseDTO.class);
                                        if (response.getUuid() == null || response.getDisplayName() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), CreateGroupResponseDTO.class);
    }

    /**
     * Create a role in the Choreo organization
     *
     * @param runner        Citrus test runner
     * @param client        Citrus HTTP client
     * @param roleRequest   Role request
     * @return CreateRoleResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static CreateRoleResponseDTO createRole(TestActionRunner runner, HttpClient client,
            HashMap<String, Object> roleRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(roleRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getRoleEndpoint())
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
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    try {
                                        CreateRoleResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        CreateRoleResponseDTO.class);
                                        if (response.getUuid() == null || response.getDisplayName() == null) {
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
     * Assign a role to a group in the Choreo organization
     *
     * @param runner                    Citrus test runner
     * @param client                    Citrus HTTP client
     * @param groupHandle                Group handle
     * @param roleGroupMappingRequest    Role group mapping request
     * @return RoleGroupMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static RoleGroupMappingResponseDTO assignRoleToGroup(TestActionRunner runner, HttpClient client,
            String groupHandle, HashMap<String, Object> roleGroupMappingRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(roleGroupMappingRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getRoleGroupMappingEndpoint(groupHandle))
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
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    try {
                                        RoleGroupMappingResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        RoleGroupMappingResponseDTO.class);
                                        if (response.getGroupUUID() == null
                                                || response.getRoleAssociations().isEmpty()) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), RoleGroupMappingResponseDTO.class);
    }

    /**
     * Assign a user to a group in the Choreo organization
     *
     * @param runner        Citrus test runner
     * @param client        Citrus HTTP client
     * @param roleHandle    Role handle
     * @param userIds       User IDs
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static void assignUserToGroup(TestActionRunner runner, HttpClient client, String roleHandle,
            HashMap<String, Object> userIds)
            throws TokenRetrievalException, IOException, URISyntaxException {

        String requestBody = ObjectMapperUtil.mapToString(userIds);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getUserGroupMappingEndpoint(roleHandle))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)));

    }

    /**
     * Get a role by handle in the Choreo organization
     *
     * @param runner        Citrus test runner
     * @param client        Citrus HTTP client
     * @param roleHandle    Role handle
     * @return GetRoleResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static GetRoleResponseDTO getRoleByHandle(TestActionRunner runner, HttpClient client, String roleHandle)
            throws TokenRetrievalException, IOException, URISyntaxException {
        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(getRoleEndpoint() + "/" + roleHandle)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getAccessToken())
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    try {
                                        GetRoleResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(), GetRoleResponseDTO.class);
                                        if (response.getUuid() == null || response.getDisplayName() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), GetRoleResponseDTO.class);

    }

    /**
     * Remove a group from a role in the Choreo organization
     *
     * @param runner                    Citrus test runner
     * @param client                    Citrus HTTP client
     * @param roleHandle                Role handle
     * @param groupAssociationRequest   Group association request
     * @return GroupRoleMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static GroupRoleMappingResponseDTO removeGroupFromRole(TestActionRunner runner, HttpClient client,
            String roleHandle, HashMap<String, Object> groupAssociationRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        String requestBody = ObjectMapperUtil.mapToString(groupAssociationRequest);
        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getRoleEndpoint() + "/" + roleHandle + "/remove-groups")
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
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    try {
                                        GroupRoleMappingResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        GroupRoleMappingResponseDTO.class);
                                        if (response.getOrgUUID() == null || response.getRoleUUID() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), GroupRoleMappingResponseDTO.class);

    }

    /**
     * Assign groups to a role in the Choreo organization
     *
     * @param runner                    Citrus test runner
     * @param client                    Citrus HTTP client
     * @param roleHandle                Role handle
     * @param groupRoleMappingRequest   Group role mapping request
     * @return GroupRoleMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws URISyntaxException       if URI syntax is invalid
     */
    public static GroupRoleMappingResponseDTO assignGroupsToRole(TestActionRunner runner, HttpClient client,
            String roleHandle,
            HashMap<String, Object> groupRoleMappingRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        String requestBody = ObjectMapperUtil.mapToString(groupRoleMappingRequest);
        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(getRoleEndpoint() + "/" + roleHandle + "/assign-groups")
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
                                .type(MessageType.JSON)
                                .validate((message, context) -> {
                                    try {
                                        GroupRoleMappingResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        GroupRoleMappingResponseDTO.class);
                                        if (response.getOrgUUID() == null || response.getRoleUUID() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), GroupRoleMappingResponseDTO.class);

    }

    private static String getAccessToken() throws TokenRetrievalException, IOException, URISyntaxException {

        return TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    private static String getGroupsEndpoint() {

        return USER_MGT_BASE_PATH + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE) + "/groups";
    }

    private static String getRoleEndpoint() {

        return USER_MGT_BASE_PATH + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE) + "/roles";
    }

    private static String getUserGroupMappingEndpoint(String roleHandle) {

        return USER_MGT_BASE_PATH + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE)
                + "/groups/" + roleHandle + "/members";
    }

    private static String getRoleGroupMappingEndpoint(String groupHandle) {

        return USER_MGT_BASE_PATH + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE)
                + "/groups/" + groupHandle + "/assign-roles";
    }

}
