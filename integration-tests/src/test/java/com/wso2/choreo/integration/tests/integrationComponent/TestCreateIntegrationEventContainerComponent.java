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
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestCreateIntegrationEventContainerComponent extends TestNGCitrusSpringSupport {

    public static final String CONTAINERIZED_EVENT_HANDLER = "byocWebhook";

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
    public void setup_TestCreateIntegrationEventComponent()
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
    public void createComponent_TestCreateIntegrationEventComponent() throws Exception {

        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        final String repoName = "ipaas-containerized-rabbitmq-listener";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().apiName(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).componentType(CONTAINERIZED_EVENT_HANDLER).
                projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath("").
                repositoryBranch(repoBranch).dockerfilePath("Dockerfile").build();
        componentHandler = GraphQL.createBYOCEventTriggeredComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationEventComponent() throws Exception {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void createEnvVariables_TestCreateIntegrationEventComponent() throws Exception {

        varMap.put("HOST", "localhost");
        varMap.put("VHOST", "localhost");
        varMap.put("USERNAME", "test-user");
        varMap.put("PASSWORD", "pass");
        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("HOST"), varMap.get("HOST"));
    }

    @Test(dependsOnMethods = {"createEnvVariables_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationEventComponent() throws Exception {

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

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationEventComponent"})
    @CitrusTest
    public void deploymentStatusByVersion_TestCreateIntegrationEventComponent() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();

        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).latestVersionId(versionId).build();
        GraphQL.getDeploymentStatusByVersion(this, choreoProjectsTestClient, accessToken, dto);
    }
}
