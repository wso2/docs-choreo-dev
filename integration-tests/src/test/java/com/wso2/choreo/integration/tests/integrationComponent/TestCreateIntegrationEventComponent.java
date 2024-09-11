/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.integrationComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.apis.component.Component;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.DeploymentStatusByVersionFailureException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MI Event Triggered components integration test with environment variable support
 */
public class TestCreateIntegrationEventComponent extends TestNGCitrusSpringSupport {

    public static final String MI_EVENT_HANDLER = "miEventHandler";

    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private static String componentHandler;
    private String githubOrg;

    private final Map<String, String> varMap = new HashMap<>();

    private static ChoreoComponent testComponent;

    @Autowired
    private Map<Endpoints, HttpClient> citrusClients;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    private ComponentDeploymentStatusDTO componentDeploymentStatusDTO;

    @BeforeClass
    public void setup_TestCreateIntegrationEventComponent()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);

        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void createProject_TestCreateIntegrationEventComponent() throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, 
            Constant.region.US.toString());
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"createProject_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void createComponent_TestCreateIntegrationEventComponent() throws Exception {
        String componentName = "IntegrationEventComponent".concat(String.valueOf(new Date().getTime()));
        final String repoName = "ipaas-mi-event-triggered";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().apiName(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
            orgHandler(orgHandle).displayName(componentName).componentType(MI_EVENT_HANDLER).
            projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath("").
            repositoryBranch(repoBranch).build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationEventComponent() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
        GraphqlDTO dto = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        dto.setComponentId(testComponent.getId());
        dto.setLatestVersionId(testComponent.getLatestApiVersion().getId());
        String runId = GraphQL.getRunId(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, dto);
        Component.waitForComponentBuildDeployComplete(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, projectId, testComponent.getId(), runId, 50);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void createEnvVariables_TestCreateIntegrationEventComponent() throws Exception {

        varMap.put("host", "localhost");
        varMap.put("user", "test-user");
        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("host"), varMap.get("host"));
    }

    @Test(dependsOnMethods = {"createEnvVariables_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void getEnvVariables_TestCreateIntegrationEventComponent() throws Exception {

        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        Map<String, String> envVariables =
                DevopsPortalApi.getEnvVariables(this, accessToken, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(envVariables.size(), varMap.size());
        Assert.assertEquals(envVariables.get("user"), varMap.get("user"));
    }

    @Test(dependsOnMethods = {"getEnvVariables_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void updateEnvVariables_TestCreateIntegrationEventComponent() throws Exception {

        varMap.put("vhost", "virtualhost");
        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("host"), varMap.get("host"));
        Assert.assertEquals(createdEnvVariables.get("vhost"), varMap.get("vhost"));
    }

    @Test(dependsOnMethods = {"updateEnvVariables_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationEventComponent() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                testComponent);
        componentDeploymentStatusDTO = ComponentUtils.deployAndValidateBuiltComponent(this, citrusClients, accessToken, testComponent,
                environments);
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void undeployComponentDev_TestCreateIntegrationEventComponent() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
                .componentType(MI_EVENT_HANDLER).releaseId(componentDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }
}
