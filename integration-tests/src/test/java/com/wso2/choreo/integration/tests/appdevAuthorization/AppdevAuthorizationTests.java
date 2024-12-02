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

package com.wso2.choreo.integration.tests.appdevAuthorization;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationConstants;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationConstants.TestGroupData;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationConstants.TestPermissionData;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationConstants.TestRoleData;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.ListRolesResponseDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.RoleGroupMappingResponseDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class AppdevAuthorizationTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ChoreoProject testProject;
    private String devEnvironmentId;
    private String createdRoleId;

    @Test
    @CitrusTest
    public void createTestProject_AppdevAuthorizationTests() throws Exception {

        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        testProject = ComponentUtils.createProject(this, citrusClients, userAccessToken,
                AppdevAuthorizationConstants.TEST_PROJECT_REGION);

        Assert.assertNotNull(testProject.getId());
    }

    @Test(dependsOnMethods = "createTestProject_AppdevAuthorizationTests")
    @CitrusTest
    public void getEnvironmentsList_AppdevAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .orgUuid(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID)).build();

        List<Environment> environments = GraphQL.getEnvironments(this, appServiceClient, accessToken, graphqlDTO);

        environments.forEach(environment -> {
            if (environment.getName().equals(AppdevAuthorizationConstants.DEV_ENVIRONMENT_NAME)) {
                devEnvironmentId = environment.getTemplateId();
            }
        });

        Assert.assertNotNull(devEnvironmentId);
    }

    @Test(dependsOnMethods = "getEnvironmentsList_AppdevAuthorizationTests")
    @CitrusTest
    public void createRoleWithPermissions_AppdevAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        CreateRoleResponseDTO createdRole = AppdevAuthorizationUtils.createRoleWithPermissions(this, appServiceClient,
                accessToken, devEnvironmentId, testProject.getId(), TestRoleData.NAME, TestPermissionData.UPDATED_PERMISSION);

        Assert.assertNotNull(createdRole);
        Assert.assertEquals(createdRole.getName(), TestRoleData.NAME);

        createdRoleId = createdRole.getId();
    }

    @Test(dependsOnMethods = "createRoleWithPermissions_AppdevAuthorizationTests")
    @CitrusTest
    public void listRolesInProject_AppdevAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ListRolesResponseDTO rolesListResponse = AppdevAuthorizationUtils.getRolesInProject(this, appServiceClient,
                testProject.getId());

        Assert.assertNotNull(rolesListResponse);
        Assert.assertTrue(rolesListResponse.getRoles().size() > 0);
    }

    @Test(dependsOnMethods = "listRolesInProject_AppdevAuthorizationTests")
    @CitrusTest
    public void updateRole_AppdevAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        CreateRoleResponseDTO updatedRole = AppdevAuthorizationUtils.updateRole(this, appServiceClient,
                createdRoleId, devEnvironmentId, testProject.getId(), TestRoleData.UPDATED_NAME,
                TestPermissionData.UPDATED_PERMISSION);

        Assert.assertNotNull(updatedRole);
        Assert.assertEquals(updatedRole.getName(), TestRoleData.UPDATED_NAME);
    }

    @Test(dependsOnMethods = "updateRole_AppdevAuthorizationTests")
    @CitrusTest
    public void mapRoleToGroup_AppdevAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        RoleGroupMappingResponseDTO mapRoleToGroupResponse = AppdevAuthorizationUtils.mapGroupsToRole(this,
                appServiceClient, accessToken, createdRoleId, List.of(TestGroupData.NAME));

        Assert.assertNotNull(mapRoleToGroupResponse);
        Assert.assertTrue(mapRoleToGroupResponse.getRoleGroupMappings().getGroups().size() > 0);
    }

    @Test(dependsOnMethods = "mapRoleToGroup_AppdevAuthorizationTests")
    @CitrusTest
    public void deleteRole_AppdevAuthorizationTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        AppdevAuthorizationUtils.deleteRole(this, appServiceClient, createdRoleId);

        ListRolesResponseDTO rolesListResponse = AppdevAuthorizationUtils.getRolesInProject(this, appServiceClient,
                testProject.getId());

        Assert.assertNotNull(rolesListResponse);
        Assert.assertTrue(rolesListResponse.getRoles().stream().noneMatch(role -> role.getId().equals(createdRoleId)));
    }

}
