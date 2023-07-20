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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.component.Component;
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

import static com.wso2.choreo.integration.config.Constant.AppType.MI_API_SERVICE;

public class TestIntegrationRestComponentWithVulnerable extends TestNGCitrusSpringSupport {

    public static final String MI_REST_API = "miApiService";
    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String runId;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private String githubOrg;

    private static ChoreoComponent testComponent;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup_TestMIIntegrationsWithVulnerableJars()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);

        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void createComponent_TestMIIntegrationsWithVulnerableJars() throws Exception {

        // Creating component
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        final String repoName = "ipaas-mi-vulnerable-integration";
        final String repoBranch = "main";
        final String projectPath = "";

        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .apiName(componentName.toLowerCase())
                .orgId(Integer.parseInt(orgId))
                .orgHandler(orgHandle)
                .displayName(componentName)
                .componentType(MI_REST_API)
                .projectId(projectId)
                .srcGitRepoUrl(srcGitHubURL)
                .repositorySubPath(projectPath)
                .repositoryBranch(repoBranch)
                .build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = { "createComponent_TestMIIntegrationsWithVulnerableJars" })
    @CitrusTest
    public void componentRetrieval_TestMIIntegrationsWithVulnerableJars() throws Exception {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = { "componentRetrieval_TestMIIntegrationsWithVulnerableJars" })
    @CitrusTest
    public void componentDeployment_TestMIIntegrationsWithVulnerableJars() throws Exception {

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

    @Test(dependsOnMethods = { "componentDeployment_TestMIIntegrationsWithVulnerableJars" })
    @CitrusTest
    public void deploymentStatusByVersion_TestMIIntegrationsWithVulnerableJars() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).latestVersionId(versionId).build();
        GraphQL.getDeploymentStatusOfFailureByVersion(this, choreoProjectsTestClient, accessToken, dto);
        runId = GraphQL.getRunId(this, choreoProjectsTestClient, accessToken, dto);
        Assert.assertNotNull(runId);
    }

    @Test(dependsOnMethods = { "deploymentStatusByVersion_TestMIIntegrationsWithVulnerableJars" })
    @CitrusTest
    public void checkVulnarabilityScan_TestMIIntegrationsWithVulnerableJars() {
        JsonArray stepJsonArray = Component.getDeploymentBuildSteps(this, choreoProjectsTestClient, accessToken, projectId, componentId, runId);
        for (JsonElement element : stepJsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            String stepName = jsonObject.get("name").getAsString();
            if ("Library (Trivy) vulnerability scan".equalsIgnoreCase(stepName)) {
                String conclusion = jsonObject.get("conclusion").getAsString();
                Assert.assertEquals(conclusion, "failure");
                return;
            }
        }
        Assert.fail("'Library (Trivy) vulnerability scan' step should be present.");
    }

}
