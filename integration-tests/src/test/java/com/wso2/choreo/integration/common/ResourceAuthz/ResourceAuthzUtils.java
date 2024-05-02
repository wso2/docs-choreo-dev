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
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleGroupMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO.GroupAssociation;
import com.wso2.choreo.integration.models.resourceAuthorization.Permission;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class ResourceAuthzUtils {

    public static CreateGroupResponseDTO createTestGroup(TestActionRunner runner, HttpClient client)
            throws TokenRetrievalException, IOException, URISyntaxException {
        HashMap<String, Object> groupData = new HashMap<>() {
            {
                put("displayName", ResourceAuthzConstants.TestGroupData.GROUP_NAME + getCurrentDate());
                put("description", ResourceAuthzConstants.TestGroupData.GROUP_DESCRIPTION);
            }
        };
        return ResourceAuthorizationService.createGroup(runner, client, groupData);
    }

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

    public static GetRoleResponseDTO getRoleByHandle(TestActionRunner runner, HttpClient client, String roleHandle)
            throws TokenRetrievalException, IOException, URISyntaxException {
        return ResourceAuthorizationService.getRoleByHandle(runner, client, roleHandle);
    }

    public static void assignUserToGroup(TestActionRunner runner, HttpClient client, String roleHandle,
            List<String> userIds) throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> userIdList = new HashMap<>() {
            {
                put("userIds", userIds);
            }
        };
        ResourceAuthorizationService.assignUserToGroup(runner, client, roleHandle, userIdList);
    }

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

    public static List<ChoreoComponent> getComponentsInProject(TestActionRunner runner, HttpClient client,
            String projectId, String accessToken) throws TokenRetrievalException, IOException, URISyntaxException {

        return GraphQL.getProjectComponents(runner, client, projectId, accessToken);
    }

    private static String getCurrentDate() {
        return Long.toString(Instant.now().getEpochSecond());
    }
}
