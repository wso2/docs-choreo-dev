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
import com.wso2.choreo.integration.models.appdevAuthorization.RoleGroupMappingResponseDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Utility class for Appdev Authorization related tests.
 */
public class AppdevAuthorizationUtils {

    /**
     * Create a role with permissions.
     *
     * @param runner            Citrus test runner
     * @param client            Citrus http client
     * @param createRoleRequest CreateRoleRequest object
     * @return CreateRoleResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateRoleResponseDTO createRoleWithPermissions(TestActionRunner runner, HttpClient client,
            HashMap<String, Object> createRoleRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevAuthorizationService.createRoleWithPermissions(runner, client, createRoleRequest);
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

        return AppdevAuthorizationService.updateRole(runner, client, roleId, updateRoleRequest);
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
     * @param runner           Citrus test runner
     * @param client           Citrus http client
     * @param mapGroupsRequest MapGroupsRequest object
     * @return RoleGroupMappingResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static RoleGroupMappingResponseDTO mapGroupsToRole(TestActionRunner runner, HttpClient client,
            HashMap<String, Object> mapGroupsRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevAuthorizationService.mapGroupsToRole(runner, client, mapGroupsRequest);
    }
}
