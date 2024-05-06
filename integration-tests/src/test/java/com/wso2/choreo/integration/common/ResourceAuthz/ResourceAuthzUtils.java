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

package com.wso2.choreo.integration.common.ResourceAuthz;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.resourceAuthorization.ResourceAuthorizationService;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzConstants.RoleGroupMappingLevels;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateGroupResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GetRoleResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupListResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleGroupMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleList;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO.GroupAssociation;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupWithUsersDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.Permission;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleAssociation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for resource authorization related tests.
 */
public class ResourceAuthzUtils {

    /**
     * Create a test group
     *
     * @param runner Citrus test runner
     * @param client Citrus http client
     * @return CreateGroupResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static CreateGroupResponseDTO createTestGroup(TestActionRunner runner, HttpClient client)
            throws TokenRetrievalException, IOException, URISyntaxException {
        HashMap<String, Object> groupData = new HashMap<>() {
            {
                put("displayName", ResourceAuthzConstants.TestGroupData.GROUP_NAME_BASE + getCurrentDate());
                put("description", ResourceAuthzConstants.TestGroupData.GROUP_DESCRIPTION);
            }
        };
        return ResourceAuthorizationService.createGroup(runner, client, groupData);
    }

    /**
     * Create a role
     *
     * @param runner          Citrus test runner
     * @param client          Citrus http client
     * @param roleName        role name
     * @param roleDescription role description
     * @param permissions     list of permissions
     * @return CreateRoleResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static CreateRoleResponseDTO createRole(TestActionRunner runner, HttpClient client, String roleName,
            String roleDescription, List<Permission> permissions)
            throws TokenRetrievalException, IOException, URISyntaxException {
        HashMap<String, Object> roleData = new HashMap<>() {
            {
                put("description", roleDescription);
                put("displayName", roleName + getCurrentDate());
                put("permissions", permissions);
            }
        };
        return ResourceAuthorizationService.createRole(runner, client, roleData);
    }

    /**
     * Assign roles to a group
     *
     * @param runner             Citrus test runner
     * @param client             Citrus http client
     * @param groupHandle        group handle
     * @param mappedResourceUUID mapped resource UUID
     * @param mappingLevel       mapping level
     * @param roleUUIDs          list of role UUIDs
     * @return RoleGroupMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static RoleGroupMappingResponseDTO assignRolesToGroup(TestActionRunner runner, HttpClient client,
            String groupHandle, String mappedResourceUUID, RoleGroupMappingLevels mappingLevel, List<String> roleUUIDs)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> roleGroupMappingData = new HashMap<>() {
            {
                put("mappedResourceUUID", mappedResourceUUID);
                put("mappingLevel", mappingLevel.toString());
                put("roleUUIDs", roleUUIDs);
            }
        };
        return ResourceAuthorizationService.assignRoleToGroup(runner, client, groupHandle, roleGroupMappingData);
    }

    /**
     * Get a role by handle
     *
     * @param runner     Citrus test runner
     * @param client     Citrus http client
     * @param roleHandle role handle
     * @return GetRoleResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static GetRoleResponseDTO getRoleByHandle(TestActionRunner runner, HttpClient client, String roleHandle)
            throws TokenRetrievalException, IOException, URISyntaxException {
        return ResourceAuthorizationService.getRoleByHandle(runner, client, roleHandle);
    }

    /**
     * Assign users to a group
     *
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param groupHandle Group handle
     * @param userIds     List of user IDs
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static void assignUserToGroup(TestActionRunner runner, HttpClient client, String groupHandle,
            List<String> userIds) throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> userIdList = new HashMap<>() {
            {
                put("userIds", userIds);
            }
        };
        ResourceAuthorizationService.assignUserToGroup(runner, client, groupHandle, userIdList);
    }

    /**
     * Assign groups to a role
     *
     * @param runner             Citrus test runner
     * @param client             Citrus http client
     * @param mappedResourceUUID mapped resource UUID
     * @param roleHandle         role handle
     * @param mappingLevel       mapping level
     * @param groupUUIDs         list of group UUIDs
     * @return GroupRoleMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static GroupRoleMappingResponseDTO assignGroupToRole(TestActionRunner runner, HttpClient client,
            String mappedResourceUUID, String roleHandle, RoleGroupMappingLevels mappingLevel, List<String> groupUUIDs)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> groupData = new HashMap<>() {
            {
                put("mappedResourceUUID", mappedResourceUUID);
                put("mappingLevel", mappingLevel.toString());
                put("groupUUIDs", groupUUIDs);
            }
        };
        return ResourceAuthorizationService.assignGroupsToRole(runner, client, roleHandle, groupData);
    }

    /**
     * Remove a group from a role
     *
     * @param runner           Citrus test runner
     * @param client           Citrus http client
     * @param roleHandle       role handle
     * @param groupAssociation group association
     * @return GroupRoleMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static GroupRoleMappingResponseDTO removeGroupFromRole(TestActionRunner runner, HttpClient client,
            String roleHandle, GroupAssociation groupAssociation)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> groupData = new HashMap<>() {
            {
                put("groupAssociations", List.of(groupAssociation));
            }
        };
        return ResourceAuthorizationService.removeGroupFromRole(runner, client, roleHandle, groupData);
    }

    /**
     * Get the decoded token
     *
     * @param encodedToken encoded token
     * @return JsonNode
     */
    public static JsonNode getDecodedToken(String encodedToken) {

        String[] splitToken = encodedToken.split("\\.");
        if (splitToken.length == 3) {
            String decodedToken = new String(Base64.getDecoder().decode(splitToken[1]), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readTree(decodedToken);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error occurred while decoding the token", e);
            }
        }
        return null;
    }

    /**
     * Get project components from an unauthorized project
     *
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param projectId   project ID
     * @param accessToken access token
     * @return List of ChoreoComponent
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static List<ChoreoComponent> getProjectComponentsFromUnauthorizedProject(TestActionRunner runner,
            HttpClient client,
            String projectId, String accessToken) throws TokenRetrievalException, IOException, URISyntaxException {

        return GraphQL.getProjectComponentsFromUnauthorizedProject(runner, client, projectId, accessToken);
    }

    /**
     * Get the list of groups
     *
     * @return GroupListResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static GroupListResponseDTO getGroups()
            throws TokenRetrievalException, IOException, URISyntaxException {

        return ResourceAuthorizationService.getGroupsList();
    }

    /**
     * Get the list of roles in a group
     *
     * @param groupHandle group handle
     * @return RoleGroupMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static RoleGroupMappingResponseDTO getRolesInGroup(String groupHandle)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return ResourceAuthorizationService.getRolesInAGroup(groupHandle);
    }

    /**
     * Remove a role from a group for cleaning up
     *
     * @param roleHandle              role handle
     * @param roleAssociationRequest  role association request
     * @return RoleGroupMappingResponseDTO
     * @throws TokenRetrievalException if token retrieval fails
     * @throws IOException             if an IO error occurs when sending or
     *                                 receiving request
     * @throws URISyntaxException      if the URI is invalid
     */
    public static RoleGroupMappingResponseDTO removeRoleFromGroupForCleanup(String roleHandle,
            List<RoleAssociation> roleAssociationRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> roleData = new HashMap<>() {
            {
                put("roleAssociations", roleAssociationRequest);
            }
        };

        return ResourceAuthorizationService.removeRoleFromGroup(roleHandle, roleData);
    }

    /**
     * Get the list of users in a group
     *
     * @param groupHandle group handle
     * @return GroupWithUsersDTO
     * @throws IOException 
     * @throws TokenRetrievalException 
     * @throws URISyntaxException 
     */
    public static GroupWithUsersDTO getGroupMembers(String groupHandle)
            throws URISyntaxException, TokenRetrievalException, IOException {

        return ResourceAuthorizationService.getGroupMembers(groupHandle);
    }

    /**
     * Remove a member from a group
     *
     * @param groupHandle group handle
     * @param memberUuid  member UUID
     * @throws URISyntaxException 
     * @throws TokenRetrievalException 
     * @throws IOException 
     */
    public static void removeMemberFromGroup(String groupHandle, String memberUuid)
            throws URISyntaxException, TokenRetrievalException, IOException {

        ResourceAuthorizationService.removeMemberFromGroup(groupHandle, memberUuid);
    }

    /**
     * Delete a group
     *
     * @param groupHandle group handle
     * @throws URISyntaxException 
     * @throws TokenRetrievalException 
     * @throws IOException 
     */
    public static void deleteGroup(String groupHandle) throws URISyntaxException, TokenRetrievalException, IOException {
        ResourceAuthorizationService.deleteGroup(groupHandle);
    }

    /**
     * Get the list of roles
     *
     * @return RoleList
     * @throws URISyntaxException 
     * @throws TokenRetrievalException 
     * @throws IOException 
     */
    public static RoleList getRoleList() throws URISyntaxException, TokenRetrievalException, IOException {
        return ResourceAuthorizationService.getRoleList();
    }

    /**
     * Delete a role
     *
     * @param roleHandle role handle
     * @throws URISyntaxException 
     * @throws TokenRetrievalException 
     * @throws IOException 
     */
    public static void deleteRole(String roleHandle) throws URISyntaxException, TokenRetrievalException, IOException {
        ResourceAuthorizationService.deleteRole(roleHandle);
    }

    private static String getCurrentDate() {
        return Long.toString(Instant.now().getEpochSecond());
    }
}
