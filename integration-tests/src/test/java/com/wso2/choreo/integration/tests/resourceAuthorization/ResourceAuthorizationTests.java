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
package com.wso2.choreo.integration.tests.resourceAuthorization;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzConstants;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzConstants.RoleGroupMappingLevels;
import com.wso2.choreo.integration.common.ResourceAuthz.ResourceAuthzConstants.TestProjectData;
import com.wso2.choreo.integration.common.exceptions.ProjectRetrievalException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateGroupResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleGroupMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO.GroupAssociation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class ResourceAuthorizationTests extends TestNGCitrusSpringSupport {

        private static final Logger log = LogManager.getLogger(ResourceAuthorizationTests.class);

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private CreateGroupResponseDTO createdTestGroup;
    private CreateRoleResponseDTO createdTestRole;
    private CreateRoleResponseDTO createdProjectViewOrgManageRole;
    private ChoreoProject projectA;
    private ChoreoProject projectX;
    private ChoreoProject projectY;
    private ChoreoProject projectZ;

    @BeforeClass
    public void setup_ResourceAuthorizationTests() throws TokenRetrievalException, IOException, URISyntaxException {
        if (TestContext.getResourceAuthzTestUserTokenHandler().getTestTokenForCPAPIs().equals(Constant.BEARER_PREFIX)) {
                throw new SkipException("Skipping Resource Authorization tests as the tests are run " 
                        + "with a user provided token.");
        }
    }

    // Setup a project for testing
    @Test
    @CitrusTest
    public void createProjectA_ResourceAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String projectHandler = NameGenerator.generateThreadUniqueName();
        String projectName = NameGenerator.generateUniqueName(Constant.TEST_PROJECT_NAME_PREFIX);
        projectA = GraphQL.createProject(this, appServiceClient, TestProjectData.REGION, userAccessToken,
                projectName, projectHandler);
        Assert.assertNotNull(projectA.getId());

        log.info("[Test] Project A: " + projectA.getId());
    }

    // Test 1

    // Step 1: Create a test group
    @Test
    @CitrusTest
    public void createTestGroup_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        createdTestGroup = ResourceAuthzUtils.createTestGroup(this, appServiceClient);

        Assert.assertNotNull(createdTestGroup.getUuid());
    }

    // Step 2: Create a test role
    @Test
    @CitrusTest
    public void createTestRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        createdTestRole = ResourceAuthzUtils.createRole(this, appServiceClient,
                ResourceAuthzConstants.TestRoleData.ROLE_DISPLAY_NAME_BASE,
                ResourceAuthzConstants.TestRoleData.ROLE_DESCRIPTION,
                ResourceAuthzConstants.TestRoleData.PERMISSIONS);
        Assert.assertNotNull(createdTestRole.getUuid());
    }

    // Step 3: Assign the test role to the test group
    @Test(dependsOnMethods = { "createTestGroup_ResourceAuthorizationTests",
            "createTestRole_ResourceAuthorizationTests", "createProjectA_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignRoleToGroup_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        List<String> roleUUIDs = List.of(createdTestRole.getUuid());
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        RoleGroupMappingResponseDTO response = ResourceAuthzUtils.assignRolesToGroup(this, appServiceClient,
                createdTestGroup.getHandle(), "", RoleGroupMappingLevels.ORG, roleUUIDs);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getRoleAssociations().size(), 1);
    }

    // Step 4: Assign the test user to the test group
    @Test(dependsOnMethods = { "assignRoleToGroup_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignUserToGroup_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ResourceAuthzUtils.assignUserToGroup(this, appServiceClient, createdTestGroup.getHandle(),
                List.of(Configuration.getConfig(ConfigDefinition.RESOURCE_AUTHZ_USER_UUID)));
    }

    // Step 5: Get the user token and check if the user is in the test group
    @Test(dependsOnMethods = { "assignUserToGroup_ResourceAuthorizationTests" })
    @CitrusTest
    public void getUserToken_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        String testUserToken = TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs();
        Assert.assertNotNull(testUserToken);

        String accessToken = testUserToken.split(" ")[1];
        Assert.assertNotNull(accessToken);

        JsonNode decodedPayload = ResourceAuthzUtils.getDecodedToken(accessToken);
        Assert.assertNotNull(decodedPayload);

        String groupsList = decodedPayload.get("groups").toString();
        List<String> groups = new ObjectMapper().readValue(groupsList, List.class);
        Assert.assertTrue(groups.contains(createdTestGroup.getUuid()));
    }

    // Step 6: Remove the test group from the test role
    @Test(dependsOnMethods = { "getUserToken_ResourceAuthorizationTests" })
    @CitrusTest
    public void removeGroupFromOrgLevelTestRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        GroupAssociation groupAssociation = new GroupAssociation();
        groupAssociation.setGroupUUID(createdTestGroup.getUuid());
        groupAssociation.setMappingLevel(RoleGroupMappingLevels.ORG.toString());
        groupAssociation.setGroupDisplayName(createdTestGroup.getDisplayName());
        groupAssociation.setGroupHandle(createdTestGroup.getHandle());
        groupAssociation.setMappedResourceUUID("");

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GroupRoleMappingResponseDTO removeGroupResponse = ResourceAuthzUtils.removeGroupFromRole(this,
                appServiceClient,
                createdTestRole.getHandle(), groupAssociation);

        Assert.assertNotNull(removeGroupResponse);
    }

    // Test 2

    // Step 1: Assign the test group to the developer role at organization level
    @Test(dependsOnMethods = { "getUserToken_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignGroupToOrgLevelDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        // Assign developer role to the group in ORG level
        GroupRoleMappingResponseDTO roleMappingresponse = ResourceAuthzUtils.assignGroupToRole(this,
                appServiceClient,
                "", ResourceAuthzConstants.DEVELOPER_ROLE_HANDLE, RoleGroupMappingLevels.ORG,
                List.of(createdTestGroup.getUuid()));
        Assert.assertNotNull(roleMappingresponse);
    }

    // Step 2: Fetch projects with developer role
    @Test(dependsOnMethods = { "removeGroupFromOrgLevelTestRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        String testUserAccessToken = TestContext.getResourceAuthzTestUserTokenHandler()
                .refetchTestTokenForCPAPIs();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(testUserAccessToken);

        Assert.assertTrue(projectsList.size() > 0);
    }

    // Test 3

    // Step 1: Remove test group from developer role at organization level
    @Test(dependsOnMethods = { "fetchProjectsWithDeveloperRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void removeGroupFromOrgLevelDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        // Remove group from developer role at organization level
        GroupAssociation groupAssociation = new GroupAssociation();
        groupAssociation.setGroupUUID(createdTestGroup.getUuid());
        groupAssociation.setMappingLevel(RoleGroupMappingLevels.ORG.toString());
        groupAssociation.setGroupDisplayName(createdTestGroup.getDisplayName());
        groupAssociation.setGroupHandle(createdTestGroup.getHandle());
        groupAssociation.setMappedResourceUUID("");

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GroupRoleMappingResponseDTO removeGroupResponse = ResourceAuthzUtils.removeGroupFromRole(this,
                appServiceClient,
                ResourceAuthzConstants.DEVELOPER_ROLE_HANDLE, groupAssociation);

        Assert.assertNotNull(removeGroupResponse);
    }

    // Step 2: Setup project X, Y, Z for testing
    @Test(dependsOnMethods = { "removeGroupFromOrgLevelDeveloperRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void setupProjectsForTesting_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization
                .getProjectsList(TestContext.getTestUserTokenHandler().refetchTestTokenForCPAPIs());
        projectX = projectsList.get(0);
        projectY = projectsList.get(1);
        projectZ = projectsList.get(2);

        Assert.assertNotNull(projectX);
        Assert.assertNotNull(projectY);
        Assert.assertNotNull(projectZ);

        log.info("[Test] Project X: " + projectX.getId());
        log.info("[Test] Project Y: " + projectY.getId());
        log.info("[Test] Project Z: " + projectZ.getId());
    }

    // Step 3: Assign developer role to the test group at project X level
    @Test(dependsOnMethods = { "setupProjectsForTesting_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignDeveloperRoleAtProjectXLevel_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String developerRoleId = ResourceAuthzUtils.getRoleByHandle(this, appServiceClient,
                ResourceAuthzConstants.DEVELOPER_ROLE_HANDLE).getUuid();

        RoleGroupMappingResponseDTO mappingResponse = ResourceAuthzUtils.assignRolesToGroup(this, appServiceClient,
                createdTestGroup.getHandle(), projectX.getId(), RoleGroupMappingLevels.PROJECT,
                List.of(developerRoleId));

        Assert.assertNotNull(mappingResponse);
    }

    // Step 4: Fetch projects with developer role at project X level
    @Test(dependsOnMethods = { "assignDeveloperRoleAtProjectXLevel_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithProjectXLevelDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        String testUserAccessToken = TestContext.getResourceAuthzTestUserTokenHandler()
                .refetchTestTokenForCPAPIs();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(testUserAccessToken);

        Assert.assertTrue(projectsList.size() > 0, "No projects found");
        // User should only see project X
        Assert.assertTrue(projectsList.stream()
                .anyMatch(project -> project.getId().equals(projectX.getId())), "Project X not found");
    }

    // Test 4

    // Step 1: Fetch project Y with developer role at project X level
    @Test(dependsOnMethods = { "fetchProjectsWithProjectXLevelDeveloperRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(
                TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs());

        Assert.assertTrue(projectsList.size() > 0);
        // User should not see project Y
        Assert.assertFalse(projectsList.stream()
                .anyMatch(project -> project.getId().equals(projectY.getId())));
    }

    // Step 2: Fetch components in project Y with developer role at project X level
    @Test(dependsOnMethods = { "fetchProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchComponentsInProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException,
            ProjectRetrievalException {

        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs();
        List<ChoreoComponent> projectYComponents = ResourceAuthzUtils.getProjectComponentsFromUnauthorizedProject(this,
                cpProjectsClient, projectY.getId(), accessToken);
        // User should not see any components in project Y
        Assert.assertTrue(projectYComponents.isEmpty());
    }

    // Test 5

    // Step 1: Assign developer role to the test group at project Z level
    @Test(dependsOnMethods = { "fetchProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignDeveloperRoleAtProjectZLevel_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String developerRoleId = ResourceAuthzUtils.getRoleByHandle(this, appServiceClient,
                ResourceAuthzConstants.DEVELOPER_ROLE_HANDLE).getUuid();

        RoleGroupMappingResponseDTO mappingResponse = ResourceAuthzUtils.assignRolesToGroup(this, appServiceClient,
                createdTestGroup.getHandle(), projectZ.getId(), RoleGroupMappingLevels.PROJECT,
                List.of(developerRoleId));

        Assert.assertNotNull(mappingResponse);
    }

    // Step 2: Fetch projects with developer role at project X and Z level
    @Test(dependsOnMethods = { "assignDeveloperRoleAtProjectZLevel_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithDeveloperRoleInProjectXAndZ_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(
                TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs());

        Assert.assertTrue(projectsList.size() > 0);
        // User should see project X and Z
        Assert.assertTrue(projectsList.stream()
                .allMatch(project -> project.getId().equals(projectX.getId())
                        || project.getId().equals(projectZ.getId())));
    }

    // Test 6

    // Step 1: Create a role with project view and org manage permissions
    @Test(dependsOnMethods = { "fetchProjectsWithDeveloperRoleInProjectXAndZ_ResourceAuthorizationTests" })
    @CitrusTest
    public void createRoleWithProjectViewAndOrgPermissions_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        createdProjectViewOrgManageRole = ResourceAuthzUtils.createRole(this, appServiceClient,
                ResourceAuthzConstants.ProjectViewAndOrgManageRoleData.ROLE_DISPLAY_NAME_BASE,
                ResourceAuthzConstants.ProjectViewAndOrgManageRoleData.ROLE_DESCRIPTION,
                ResourceAuthzConstants.ProjectViewAndOrgManageRoleData.PERMISSIONS);
        Assert.assertNotNull(createdProjectViewOrgManageRole.getUuid());
    }

    // Step 2: Assign the role to the test group at project A level
    @Test(dependsOnMethods = { "createRoleWithProjectViewAndOrgPermissions_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignProjectViewAndOrgPermissionRoleToGroupInProjectA_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        List<String> roleUUIDs = List.of(createdProjectViewOrgManageRole.getUuid());
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        RoleGroupMappingResponseDTO response = ResourceAuthzUtils.assignRolesToGroup(this, appServiceClient,
                createdTestGroup.getHandle(), projectA.getId(), RoleGroupMappingLevels.PROJECT, roleUUIDs);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getRoleAssociations().size(), 1);
    }

    // Step 3: Fetch projects with project view and org manage role in project A
    @Test(dependsOnMethods = { "assignProjectViewAndOrgPermissionRoleToGroupInProjectA_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithAdminRoleInProjectsAXZ_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(
                TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs());

        Assert.assertTrue(projectsList.size() > 0);
        // User should see project A, X and Z
        Assert.assertTrue(projectsList.stream()
                .allMatch(project -> project.getId().equals(projectA.getId())
                        || project.getId().equals(projectX.getId())
                        || project.getId().equals(projectZ.getId())));
    }

    // Step 4: Delete project A
    @Test(dependsOnMethods = { "fetchProjectsWithAdminRoleInProjectsAXZ_ResourceAuthorizationTests" })
    @CitrusTest
    public void deleteProjectA_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        String userAccessToken = TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs();
        boolean isProjectDeleted = testOrganization.deleteProjectInOrganization(userAccessToken, projectA.getId());

        // User should not be able to delete Project A
        Assert.assertFalse(isProjectDeleted);
    }
}
