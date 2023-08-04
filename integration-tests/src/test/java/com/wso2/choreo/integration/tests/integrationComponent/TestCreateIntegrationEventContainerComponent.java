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
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.graphql.CreateByocComponentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestCreateIntegrationEventContainerComponent extends TestNGCitrusSpringSupport {

    public static final String CONTAINERIZED_EVENT_HANDLER = "byocWebhook";
    public static final String WS_URL = "wss://ws.postman-echo.com/raw/";

    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private String githubOrg;

    private final Map<String, String> varMap = new HashMap<>();

    private static ChoreoComponent testComponent;

    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @Autowired
    private HttpClient choreoTestClientForGithub;

    @Autowired
    private HttpClient choreoTestClientForSTS;

    @BeforeClass
    public void setup_TestCreateIntegrationEventContainerComponent()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);

        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void createComponent_TestCreateIntegrationEventContainerComponent() throws Exception {

        String componentName = "event-listener".concat(String.valueOf(new Date().getTime()));
        final String repoName = "ipaas-containerized-event-listener";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().name(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).componentType(CONTAINERIZED_EVENT_HANDLER).
                projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath("").
                repositoryBranch(repoBranch).dockerfilePath("Dockerfile").build();
        Optional<CreateByocComponentResponseDTO> byocComponent = GraphQL.createBYOCComponent(this,
                choreoProjectsTestClient, graphqlDTO, accessToken);
        componentHandler = byocComponent.get().getHandle();
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationEventContainerComponent() throws Exception {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void createEnvVariables_TestCreateIntegrationEventContainerComponent() throws Exception {

        varMap.put("WS_URL", WS_URL);
        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("WS_URL"), varMap.get("WS_URL"));
    }

    @Test(dependsOnMethods = {"createEnvVariables_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationEventContainerComponent() throws Exception {

        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
        componentId = testComponent.getId();
        ApiVersion apiVersion = testComponent.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        String devEnvIdToDeploy = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String branch = testComponent.getRepository().getBranch();

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).latestVersionId(latestVersionId)
                .devEnvIdToDeploy(devEnvIdToDeploy).branch(branch).sha(latestCommitSha).shaDate("").build();

        // Deploy component
        GraphQL.deployComponent(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void deploymentStatusByVersion_TestCreateIntegrationEventContainerComponent() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();

        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).latestVersionId(versionId).build();
        GraphQL.getDeploymentStatusByVersion(this, choreoProjectsTestClient, accessToken, dto);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersion_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentDeploymentStatus_TestCreateIntegrationEventContainerComponent() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();
        String devEnvIdToDeploy = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);

        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle).orgUuid(orgUUID)
                .versionId(versionId).environmentId(devEnvIdToDeploy).build();

        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", devEnvIdToDeploy);
        responseParams.put("sha", latestCommitSha);
        responseParams.put("versionId", versionId);

        GraphQL.getComponentDeploymentStatus(this, choreoProjectsTestClient, accessToken, dto, responseParams);
    }

    @Test(dependsOnMethods = {"componentDeploymentStatus_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void createEnvVariablesInProd_TestCreateIntegrationEventContainerComponent() throws Exception {

        varMap.put("WS_URL", WS_URL);
        String prodEnvId = testComponent.getLatestAppEnvId(Constant.PROD_ENVIRONMENT);
        String prodReleaseId = testComponent.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        prodEnvId, prodReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("WS_URL"), varMap.get("WS_URL"));
    }

    @Test(dependsOnMethods = {"createEnvVariablesInProd_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentPromotionToProd_TestCreateIntegrationEventContainerComponent() throws Exception {
        // Retrieve the latest component.
        testComponent = GraphQL.getComponentDetails(this, choreoProjectsTestClient, projectId, componentHandler, accessToken);
        String latestApiVersionId = testComponent.getLatestApiVersion().getId();
        String releaseIdForEnvironment = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);
        String latestAppEnvId = testComponent.getLatestAppEnvId(Constant.PROD_ENVIRONMENT);

        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).apiVersionId(latestApiVersionId).
                sourceReleaseId(releaseIdForEnvironment).targetEnvironmentId(latestAppEnvId).build();
        GraphQL.promoteComponent(this, choreoProjectsTestClient, accessToken, dto);

        testComponent.waitForComponentDeploymentSuccess(accessToken, orgHandle, orgUUID, latestApiVersionId,
                latestAppEnvId);
    }

    @Test(dependsOnMethods = {"componentPromotionToProd_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void undeployComponentDev_TestCreateIntegrationEventContainerComponent() throws Exception {

        String devReleaseId = GraphQL.componentDeployment(testComponent, Constant.DEV_ENVIRONMENT, accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle)
                .componentType(CONTAINERIZED_EVENT_HANDLER).releaseId(devReleaseId).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }
}
