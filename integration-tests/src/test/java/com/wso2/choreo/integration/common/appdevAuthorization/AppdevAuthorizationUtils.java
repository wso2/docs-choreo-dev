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

package com.wso2.choreo.integration.common.appdevAuthorization;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.appdevAuthorization.AppdevAuthorizationService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.appdevAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.ListRolesResponseDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.Permission;
import com.wso2.choreo.integration.models.appdevAuthorization.RoleGroupMappingResponseDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for Appdev Authorization related tests.
 */
public class AppdevAuthorizationUtils {

    /**
     * Create a role with permissions.
     *
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param accessToken Access token
     * @param envId       Environment ID
     * @param projectId   Project ID
     * @param name        Role name
     * @param permission  Permission
     * @return CreateRoleResponseDTO object
     * @throws IOException             If an error occurs while reading the response
     */
    public static CreateRoleResponseDTO createRoleWithPermissions(TestActionRunner runner, HttpClient client,
                                                                  String accessToken, String envId,
                                                                  String projectId, String name, String permission)
            throws IOException {

        return AppdevAuthorizationService.createRoleWithPermissions(runner, client, accessToken,
                getRoleRequest(envId, projectId, name, permission));
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

        return AppdevAuthorizationService.getRolesInProject(runner, client, projectId);
    }

    /**
     * Update a role.
     *
     * @param runner     Citrus test runner
     * @param client     Citrus http client
     * @param roleId     Role ID
     * @param envId      Environment ID
     * @param projectId  Project ID
     * @param name       Role name
     * @param permission Permission
     * @return CreateRoleResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateRoleResponseDTO updateRole(TestActionRunner runner, HttpClient client,
            String roleId, String envId, String projectId, String name, String permission)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevAuthorizationService.updateRole(runner, client, roleId,
                getRoleRequest(envId, projectId, name, permission));
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

        AppdevAuthorizationService.deleteRole(runner, client, roleId);
    }

    /**
     * Map groups to a role.
     *
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param accessToken Access token
     * @param roleId      Role ID
     * @param groups      List of groups
     * @return RoleGroupMappingResponseDTO object
     * @throws IOException             If an error occurs while reading the response
     */
    public static RoleGroupMappingResponseDTO mapGroupsToRole(TestActionRunner runner, HttpClient client,
                                                              String accessToken, String roleId,
                                                              List<String> groups)
            throws IOException {

        HashMap<String, Object> mapGroupsRequest = new HashMap<>();
        mapGroupsRequest.put("groups", groups);
        mapGroupsRequest.put("roleId", roleId);
        return AppdevAuthorizationService.mapGroupsToRole(runner, client, accessToken, mapGroupsRequest);
    }

    private static HashMap<String, Object> getRoleRequest(String envId, String projectId, String name,
                                                          String permission) {

        HashMap<String, Object> roleRequest = new HashMap<>();
        roleRequest.put("name", name);
        roleRequest.put("description", AppdevAuthorizationConstants.TestRoleData.DESCRIPTION);
        roleRequest.put("projectId", projectId);

        Permission permissionObj = Permission.builder()
                .environmentId(envId)
                .name(permission)
                .build();
        roleRequest.put("permissions", List.of(permissionObj));
        return roleRequest;
    }
}
