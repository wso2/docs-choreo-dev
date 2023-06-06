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
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestIntegrationMISecretAndCert extends TestNGCitrusSpringSupport {

    public static final String MI_REST_API = "miRestApi";
    public static final String API_INVOCATION_REQUEST_URI = "/restapiwithsecret/getsecret";
    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private String githubOrg;
    private List<Environment> environments;

    private static ChoreoComponent testComponent;

    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup() throws Exception {

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
    public void createComponent() throws Exception {

        // Creating component
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        final String repoName = "ipaas-mi-secret-and-cert-test";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .apiName(componentName.toLowerCase())
                .orgId(Integer.parseInt(orgId))
                .orgHandler(orgHandle)
                .displayName(componentName)
                .componentType(MI_REST_API)
                .projectId(projectId)
                .srcGitRepoUrl(srcGitHubURL)
                .repositorySubPath("")
                .repositoryBranch(repoBranch)
                .build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = { "createComponent" })
    @CitrusTest
    public void componentRetrieval() throws Exception {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .projectId(projectId)
                .componentHandler(componentHandler)
                .build();
        testComponent = GraphQL.retrieveComponent(this, choreoTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = { "componentRetrieval" })
    @CitrusTest
    public void createSecret() throws Exception {

        Map<String, String> varMap = new HashMap<>();
        varMap.put("user_pass", "user_pass_!@#$%");
        varMap.put("db_pass", "db_pass_!@#$%");
        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        JsonArray createdSecrets =
                DevopsPortalApi.createOrUpdateMiSecret(this, accessToken, varMap, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdSecrets.size(), varMap.size());
        for (int i = 0; i < createdSecrets.size(); i++) {
            Assert.assertTrue(varMap.containsKey(createdSecrets.get(i).getAsJsonObject().get("name").getAsString()));
        }
    }

    @Test(dependsOnMethods = { "createSecret" })
    @CitrusTest
    public void componentDeployment() throws Exception {
        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
        componentId = testComponent.getId();
        ApiVersion apiVersion = testComponent.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        String devEnvIdToDeploy = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String branch = testComponent.getRepository().getBranch();

        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .componentId(componentId)
                .latestVersionId(latestVersionId)
                .devEnvIdToDeploy(devEnvIdToDeploy)
                .branch(branch)
                .sha(latestCommitSha)
                .shaDate("")
                .build();

        // Deploy component
        GraphQL.deployComponent(this, choreoTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = { "componentDeployment" })
    @CitrusTest
    public void deploymentStatusByVersion() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();
        GraphqlDTO dto = GraphqlDTO.builder()
                .componentId(componentId)
                .latestVersionId(versionId)
                .build();
        GraphQL.getDeploymentStatusByVersion(this, choreoProjectsTestClient, accessToken, dto);
    }

    @Test(dependsOnMethods = { "deploymentStatusByVersion" })
    @CitrusTest
    public void componentDeploymentStatus() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();
        String devEnvIdToDeploy = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);

        GraphqlDTO dto = GraphqlDTO.builder()
                .componentId(componentId)
                .orgHandler(orgHandle)
                .orgUuid(orgUUID)
                .versionId(versionId)
                .environmentId(devEnvIdToDeploy)
                .build();

        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", devEnvIdToDeploy);
        responseParams.put("sha", latestCommitSha);
        responseParams.put("versionId", versionId);

        GraphQL.getComponentDeploymentStatus(this, choreoTestClient, accessToken, dto, responseParams);
    }

    @Test(dependsOnMethods = { "componentDeploymentStatus" })
    @CitrusTest
    public void invokeAPIDev() throws Exception {

        final InvokeInformation invokeInformation = testComponent.getInvokeInformation(accessToken, MI_REST_API,
                Constant.Environment.Development.name());
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, testComponent);
        final String devApiKey = testComponent.getAPIKeyForInvoke(accessToken, invokeInformation.getApiId(),
                        environments.get(0).getName()).replace("\"", "");
        String invokeUrlDev = invokeInformation.getInvokeUrl();
        String res = "{\n" + "\"Info\": \"Integration with secrets\",\n" + "\"data\": [{\n" + "\"secret_key\": " +
                "\"user_pass\",\n" + "\"secret_value\": \"user_pass_!@#$%\"\n" + "},\n" + "{\n" + "\"secret_key\": " +
                "\"db_pass\",\n" + "\"secret_value\": \"db_pass_!@#$%\"\n" + "}\n" + "]\n" + "}";
        ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI, res);
    }

    @Test(dependsOnMethods = { "invokeAPIDev" })
    @CitrusTest
    public void undeployComponentDev() throws Exception {

        String devReleaseId = GraphQL.componentDeployment(testComponent, Constant.DEV_ENVIRONMENT, accessToken)
                .getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .componentId(componentId)
                .orgHandler(orgHandle)
                .componentType(MI_REST_API)
                .releaseId(devReleaseId)
                .build();
        GraphQL.stopDeployment(this, choreoTestClient, accessToken, graphqlDTO);
    }

}
