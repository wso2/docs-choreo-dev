package com.wso2.choreo.integration.tests.integrationComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestCreateIntegrationRestComponentFromRoot extends TestNGCitrusSpringSupport {

    public static final String MI_REST_API = "miRestApi";
    public static final String API_INVOCATION_REQUEST_URI = "/HelloWorld";
    public static final String REST_API_EXPECTED_RESPONSE = "{\"Hello\":\"Integration\"}";
    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private String githubOrg;

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
    public void setup_TestCreateIntegrationRestComponentFromRoot()
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
    public void createComponent_TestCreateIntegrationRestComponentFromRoot() throws IOException {

        // Creating component
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        final String repoName = "synaps-api-project-sample";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().apiName(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).componentType(MI_REST_API).
                projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath("").
                repositoryBranch(repoBranch).build();
        componentHandler = GraphQL.createIntegrationComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationRestComponentFromRoot() throws IOException {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveIntegrationComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationRestComponentFromRoot() throws Exception {

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
        GraphQL.deployIntegrationComponent(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void deploymentStatusByVersion_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        String versionId = testComponent.getLatestApiVersion().getId();
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).latestVersionId(versionId).build();
        GraphQL.getDeploymentStatusByVersion(this, choreoProjectsTestClient, accessToken, dto);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersion_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentDeploymentStatus_TestCreateIntegrationRestComponentFromRoot() throws Exception {

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

        GraphQL.getComponentDeploymentStatus(this, choreoTestClient, accessToken, dto, responseParams);
    }

    @Test(dependsOnMethods = {"componentDeploymentStatus_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void invokeAPIDev_TestCreateIntegrationRestComponentFromRoot() throws NoLatestApiVersionFoundException, IOException {

        String latestVersionId = testComponent.getLatestApiVersion().getId();

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle).orgUuid(orgUUID)
                .versionId(latestVersionId).componentType(MI_REST_API).build();

        //Get Invoke URL and API ID
        Map<String, String> invokeUrlApiId = GraphQL.getInvokeUrlApiId(this, choreoTestClient, accessToken, graphqlDTO);
        String invokeUrlDev = invokeUrlApiId.get(Constant.INVOKE_URL);
        String apiId = invokeUrlApiId.get(Constant.API_ID);

        String apiKey = GraphQL.getApiKey(this, choreoTestClientForSTS, accessToken, apiId, orgUUID);

        GraphQL.invokeApi(this, apiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI, REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void componentPromotionToProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {
        // Retrieve the latest component.
        testComponent = GraphQL.getComponentDetails(projectId, componentHandler, accessToken);
        String latestApiVersionId = testComponent.getLatestApiVersion().getId();
        String releaseIdForEnvironment = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);
        String latestAppEnvId = testComponent.getLatestAppEnvId(Constant.PROD_ENVIRONMENT);

        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).apiVersionId(latestApiVersionId).
                sourceReleaseId(releaseIdForEnvironment).targetEnvironmentId(latestAppEnvId).build();
        GraphQL.promoteComponent(this, choreoTestClient, accessToken, dto);

        testComponent.waitForComponentDeploymentSuccess(accessToken, orgHandle, orgUUID, latestApiVersionId,
                latestAppEnvId);
    }

    @Test(dependsOnMethods = {"componentPromotionToProd_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void invokeAPIProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        final InvokeInformation invokeInformation = testComponent.getInvokeInformation(accessToken, MI_REST_API,
                Constant.Environment.Production.name());
        final String prodApiKey = testComponent.getAPIKeyForInvoke(accessToken, invokeInformation.getApiId())
                .replace("\"", "");
        final String invokeUrlProd = invokeInformation.getInvokeUrl();
        GraphQL.invokeApi(this, prodApiKey, invokeUrlProd, API_INVOCATION_REQUEST_URI, REST_API_EXPECTED_RESPONSE);
    }

    @Test(dependsOnMethods = {"invokeAPIProd_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void undeployComponentDev_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        String devReleaseId = GraphQL.componentDeployment(testComponent, Constant.DEV_ENVIRONMENT, accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle)
                .componentType(MI_REST_API).releaseId(devReleaseId).build();
        GraphQL.stopDeployment(this, choreoTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void undeployComponentProd_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        String prodReleaseId = GraphQL.componentDeployment(testComponent, Constant.PROD_ENVIRONMENT, accessToken)
                .getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle)
                .componentType(MI_REST_API).releaseId(prodReleaseId).build();
        GraphQL.stopDeployment(this, choreoTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentProd_TestCreateIntegrationRestComponentFromRoot"})
    @CitrusTest
    public void deleteComponent_TestCreateIntegrationRestComponentFromRoot() throws Exception {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(orgHandle)
                .projectId(projectId).build();
        GraphQL.deleteComponent(this, choreoTestClient, accessToken, graphqlDTO);
    }
}
