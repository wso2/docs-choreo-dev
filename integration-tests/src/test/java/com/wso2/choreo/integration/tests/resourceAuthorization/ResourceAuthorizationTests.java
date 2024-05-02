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
import com.wso2.choreo.integration.models.resourceAuthorization.CreateGroupResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.RoleGroupMappingResponseDTO;
import com.wso2.choreo.integration.models.resourceAuthorization.GroupRoleMappingResponseDTO.GroupAssociation;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class ResourceAuthorizationTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private CreateGroupResponseDTO createdTestGroup;
    private CreateRoleResponseDTO createdTestRole;
    private ChoreoProject projectA;
    private ChoreoProject projectX;
    private ChoreoProject projectY;
    private ChoreoProject projectZ;

    @Test
    @CitrusTest
    public void createTestGroup_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        createdTestGroup = ResourceAuthzUtils.createTestGroup(this, appServiceClient);

        Assert.assertNotNull(createdTestGroup.getUuid());
    }

    @Test
    @CitrusTest
    public void createTestRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        createdTestRole = ResourceAuthzUtils.createRole(this, appServiceClient,
                ResourceAuthzConstants.TestRoleData.ROLE_DISPLAY_NAME,
                ResourceAuthzConstants.TestRoleData.ROLE_DESCRIPTION,
                ResourceAuthzConstants.TestRoleData.PERMISSIONS);
        Assert.assertNotNull(createdTestRole.getUuid());
    }

    @Test
    @CitrusTest
    public void createProjectA_ResourceAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String userAccessToken = TestContext.getTestUserTokenHandler().refetchTestTokenForCPAPIs();
        String projectHandler = NameGenerator.generateThreadUniqueName();
        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(userAccessToken);
        projectA = GraphQL.createProject(this, appServiceClient, TestProjectData.REGION, userAccessToken,
                ResourceAuthzConstants.TestProjectData.PROJECT_NAME, projectHandler);
        Assert.assertNotNull(projectA.getId());
    }

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

    @Test(dependsOnMethods = { "assignRoleToGroup_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignUserToGroup_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ResourceAuthzUtils.assignUserToGroup(this, appServiceClient, createdTestGroup.getHandle(),
                List.of(ResourceAuthzConstants.TEST_USER_ID));
    }

    @Test(dependsOnMethods = { "assignUserToGroup_ResourceAuthorizationTests" })
    @CitrusTest
    public void getUserToken_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        String testUserToken = TestContext.getResourceAuthzTestUserTokenHandler().getTestTokenForCPAPIs();
        Assert.assertNotNull(testUserToken);

        String accessToken = testUserToken.split(" ")[1];
        Assert.assertNotNull(accessToken);

        JsonNode decodedPayload = ResourceAuthzUtils.getDecodedToken(accessToken);
        Assert.assertNotNull(decodedPayload);

        String groupsList = decodedPayload.get("groups").toString();
        List<String> groups = new ObjectMapper().readValue(groupsList, List.class);
        Assert.assertTrue(groups.contains(createdTestGroup.getUuid()));
    }

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

    @Test(dependsOnMethods = { "assignGroupToOrgLevelDeveloperRole_ResourceAuthorizationTests" })
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
    }

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

    @Test(dependsOnMethods = { "assignDeveloperRoleAtProjectXLevel_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithProjectXLevelDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        String testUserAccessToken = TestContext.getResourceAuthzTestUserTokenHandler()
                .refetchTestTokenForCPAPIs();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(testUserAccessToken);

        Assert.assertTrue(projectsList.size() > 0);
        Assert.assertTrue(projectsList.stream()
                .anyMatch(project -> project.getId().equals(projectX.getId())));

    }

    @Test(dependsOnMethods = { "fetchProjectsWithProjectXLevelDeveloperRole_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(
                TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs());

        // User should not see project Y
        Assert.assertTrue(projectsList.size() > 0);
        Assert.assertFalse(projectsList.stream()
                .anyMatch(project -> project.getId().equals(projectY.getId())));
    }

    // @Test(dependsOnMethods = {
    // "fetchProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests" })
    // @CitrusTest
    // public void
    // fetchComponentsInProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests()
    // throws TokenRetrievalException, IOException, URISyntaxException,
    // ProjectRetrievalException {

    // HttpClient cpProjectsClient =
    // citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
    // String accessToken =
    // TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs();
    // List<ChoreoComponent> projectYComponents =
    // ResourceAuthzUtils.getComponentsInProject(this,
    // cpProjectsClient, projectY.getId(), accessToken);

    // Assert.assertTrue(projectYComponents.isEmpty());
    // }

    @Test(dependsOnMethods = { "fetchProjectYWithProjectXDeveloperRole_ResourceAuthorizationTests" }) // TODO:
                                                                                                      // Change this
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

    @Test(dependsOnMethods = { "assignDeveloperRoleAtProjectZLevel_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithDeveloperRoleInProjectXAndZ_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(
                TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs());

        Assert.assertTrue(projectsList.size() > 0);
        Assert.assertTrue(projectsList.stream()
                .allMatch(project -> project.getId().equals(projectX.getId())
                        || project.getId().equals(projectZ.getId())));
    }

    @Test(dependsOnMethods = { "fetchProjectsWithDeveloperRoleInProjectXAndZ_ResourceAuthorizationTests" })
    @CitrusTest
    public void assignAdminRoleAtProjectALevel_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String adminRoleId = ResourceAuthzUtils.getRoleByHandle(this, appServiceClient,
                ResourceAuthzConstants.ADMIN_ROLE_HANDLE).getUuid();

        RoleGroupMappingResponseDTO mappingResponse = ResourceAuthzUtils.assignRolesToGroup(this, appServiceClient,
                createdTestGroup.getHandle(), projectA.getId(), RoleGroupMappingLevels.PROJECT,
                List.of(adminRoleId));

        Assert.assertNotNull(mappingResponse);
    }

    @Test(dependsOnMethods = { "assignAdminRoleAtProjectALevel_ResourceAuthorizationTests" })
    @CitrusTest
    public void fetchProjectsWithAdminRoleInProjectsAXZ_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        testOrganization.clearProjects();
        List<ChoreoProject> projectsList = testOrganization.getProjectsList(
                TestContext.getResourceAuthzTestUserTokenHandler().refetchTestTokenForCPAPIs());

        Assert.assertTrue(projectsList.size() > 0);
        Assert.assertTrue(projectsList.stream()
                .allMatch(project -> project.getId().equals(projectA.getId())
                        || project.getId().equals(projectX.getId())
                        || project.getId().equals(projectZ.getId())));

    }

    @Test(dependsOnMethods = { "fetchProjectsWithAdminRoleInProjectsAXZ_ResourceAuthorizationTests" })
    @CitrusTest
    public void deleteProjectA_ResourceAuthorizationTests()
            throws TokenRetrievalException, IOException, URISyntaxException, ProjectRetrievalException {

        ChoreoOrganization testOrganization = TestContext.getTestOrg();
        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        boolean isProjectDeleted = testOrganization.deleteProjectInOrganization(userAccessToken, projectA.getId());

        Assert.assertFalse(isProjectDeleted);
    }
}
