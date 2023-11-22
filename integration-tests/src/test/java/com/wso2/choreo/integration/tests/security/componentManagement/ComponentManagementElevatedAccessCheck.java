package com.wso2.choreo.integration.tests.security.componentManagement;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ComponentManagementElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String orgUuid;
    private static String orgHandler;
    private static String projectName;
    private static String projectId;
    private static String componentName;
    private static String ballerinaVersion;
    private static String componentId;
    private static String srcGitRepoUrl;
    private static String repositorySubPath;
    private static String requestUrl;
    private static String componentType;
    private static String oasFilePath;
    private static String dockerfilePath;
    private static String dockerContext;
    private static String componentHandler;
    private static String versionId;
    private static String envId;
    private static String branch;
    private static String sha;
    private static String shaDate;
    private static String releaseId;
    private static String repoName;
    private static String commitHash;
    private static String apiId;
    private static String gitOrgHandle;
    private static String testUserId;
    private static String secretRef;
    private static String bitbucketOrgName;
    private static String appPwd;
    private static String credentialID;
    private static String credentialName;
    private static String targetEnvironmentId;
    private static String runID;
    private static String endpointId;
    private static String apiDefinitionPath;
    private static String displayName;
    private static String apiContext;
    private static String visibility;
    private static String buildId;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ComponentManagementElevatedAccessCheck() throws Exception {
        requestUrl = Constant.COMPONENT_MGT_SUFFIX;
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgHandler = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        projectName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_PROJECT_NAME);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_PROJECT_ID);
        componentName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_NAME);
        ballerinaVersion = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BALLERINA_VERSION);
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_ID);
        srcGitRepoUrl = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SRC_GIT_REPO_URL);
        repositorySubPath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_REPOSITORY_SUB_PATH);
        componentType = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_TYPE);
        oasFilePath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_OAS_FILE_PATH);
        dockerfilePath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DOCKER_FILE_PATH);
        dockerContext = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DOCKER_CONTEXT);
        componentHandler = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_HANDLER);
        versionId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_VERSION_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_ENV_ID);
        branch = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BRANCH);
        sha = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SHA);
        shaDate = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SHA_DATE);
        releaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_RELEASE_ID);
        repoName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_REPO_NAME);
        commitHash = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMMIT_HASH);
        apiId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_API_ID);
        gitOrgHandle = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_GIT_ORG_HANDLE);
        testUserId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_TEST_USER_ID);
        secretRef = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SECRET_REF);
        bitbucketOrgName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BITBUCKET_ORG_NAME);
        appPwd = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_APP_PWD);
        credentialID = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_CREDENTIAL_ID);
        credentialName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_CREDENTIAL_NAME);
        targetEnvironmentId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_TARGET_ENV_ID);
        runID = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_RUN_ID);
        endpointId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_ENDPOINT_ID);
        apiDefinitionPath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_API_DEF_PATH);
        displayName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DISPLAY_NAME);
        apiContext = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_API_CONTEXT);
        visibility = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_VISIBILITY);
        buildId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BUILD_ID);
    }

    @Test
    @CitrusTest
    public void createProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("projectName", projectName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createProject.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getProjects_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getProjects.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void listProjects_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "listProjects.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentName", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("ballerinaVersion", ballerinaVersion);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createComponent.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "updateComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("orgId", orgId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "updateProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createIntegrationComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentName", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("srcGitRepoUrl", srcGitRepoUrl);
        params.put("repositorySubPath", repositorySubPath);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createIntegrationComponent.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createByocComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("name", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("componentType", componentType);
        params.put("oasFilePath", oasFilePath);
        params.put("dockerfilePath", dockerfilePath);
        params.put("dockerContext", dockerContext);
        params.put("srcGitRepoUrl", srcGitRepoUrl);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createBYOCcomponent.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void componentDetails_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("componentHandler", componentHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentDetails.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void listAllComponents_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "listAllComponents.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void envDetails_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getEnvironments.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    // disabled until the unauthorized error codes are handled
    // https://github.com/wso2-enterprise/choreo/issues/24660
    @Test(enabled = false)
    @CitrusTest
    public void deployComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("latestVersionId", versionId);
        params.put("devEnvIdToDeploy", envId);
        params.put("branch", branch);
        params.put("sha", sha);
        params.put("shaDate", shaDate);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deployComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void manuallyDeployToDev_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("orgUuid", orgUuid);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("environmentId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentDeployment.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getBuildsByVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/maxApiRevisions/" +
                "query_build_by_version_payload.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void commitHistory_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("branch", branch);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "commitHistoryBranch.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void handleDisableAutoBuild_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("envId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "handleDisableAutoBuild.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getCommonCredentials_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "commonCredentials.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getDeploymentStatusByVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("latestVersionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deploymentStatusByVersion.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void stopDeployment_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("releaseId", releaseId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "stopDeployment.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getRepoMetadataGH_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("repoName", repoName);
        params.put("branch", branch);
        params.put("repositorySubPath", repositorySubPath);
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "repoMetadata.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void repoBranchListGH_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "repoBranchList.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentWebhook_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentWebhook.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigurationCommitMappings_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigurationCommitMappings = "/component-mgt/1.0.0/orgs/" + orgHandler + "/projects/" + projectId + "/components/" +
                componentId + "/versions/" + versionId + "/commits/" + commitHash + "/configurable-commit-mapping";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetConfigurationCommitMappings, accessToken);
    }

    @Test
    @CitrusTest
    public void getBranchListInComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "branchListInComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("orgUuid", orgUuid);
        params.put("componentId", componentId);
        params.put("componentType", componentType);
        params.put("apiId", apiId);
        params.put("branch", branch);
        String body = MessageUtils.generateStringFromTemplate("templates/byor/" +
                "graphqlQueryForNewVersionCreation.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigurations_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        requestUrl = "/config-mgt/1.0.0/orgs/" + orgHandler + "/projects/" + projectId + "/components/" + componentId +
                "/envs/" + envId + "/" + versionId + "/configurations?component_name=" + componentName +
                "&commit_hash=" + commitHash;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

   @Test
   @CitrusTest
   public void configGeneration_ComponentManagementElevatedAccessCheck() throws Exception {
       HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
       String requestUrlConfigGeneration = "/component-mgt/1.0.0/orgs/" + orgHandler + "/projects/" + projectId +
               "/triggers/configurable-generation";
       Map<String, String> params = new HashMap<>();
       params.put("componentId", componentId);
       params.put("versionId", versionId);
       params.put("branch", branch);
       params.put("commitHash", commitHash);
       String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
               "configGeneration.mustache", params);
       SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlConfigGeneration, body, accessToken);
   }

    @Test
    @CitrusTest
    public void getRepoContents_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetRepoContents = "/component-utils/1.0.0/repositories/" + gitOrgHandle + "/" + repoName + "/branches/main/contents?"
                + "userId=" + testUserId;
        SecurityUtils.elevatedAccessCheckForForbiddenGetRequests(this, choreoCPTestClient, requestUrlForGetRepoContents,
                accessToken);
    }

    @Test
    @CitrusTest
    public void componenPullRequests_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getComponentPullRequests.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void autobuildTrigger_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "autobuildTrigger.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void gitTokenPermissions_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("bitbucketOrgName", bitbucketOrgName);
        params.put("appPwd", appPwd);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "gitTokenPermissionBB.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createCommonCredential_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("bitbucketOrgName", bitbucketOrgName);
        params.put("appPwd", appPwd);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "commonCredential.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getUserReposGitHub_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("secretRef", secretRef);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "userReposGitHub.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void projectComponentLabels_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "projectComponentLabels.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateCommonCredential_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("credentialID", credentialID);
        params.put("credentialName", credentialName);
        params.put("orgUuid", orgUuid);
        params.put("bitbucketOrgName", bitbucketOrgName);
        params.put("appPwd", appPwd);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "updateCommonCredential.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getCellDiagram_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("commitHash", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "cellDiagram.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void scanResult_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "scanResult.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void userRepoStatus_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "userRepoStatus.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void userRepoValidation_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "userRepoValidation.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void redeployDeployment_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("releaseId", releaseId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "redeployDeployment.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void buildsByVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("devEnvId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/maxApiRevisions/" +
                "query_build_by_version_payload.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void distinctComponentTypeCount_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "distinctComponentTypeCount.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void invokeUrls_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "invokeUrls.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void invokeUrl_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("devEnvId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "invokeUrl.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void invokeInformation_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "invokeInformation.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void isValidNonEmptyRepo_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("gitOrgHandle", gitOrgHandle);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "isValidNonEmptyRepo.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void repoDirectoryList_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("gitOrgHandle", gitOrgHandle);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "repoDirectoryList.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void generateEndpoints_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        params.put("commitHash", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "generateEndpoints.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void componentEndpoints_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        String body = MessageUtils.generateStringFromTemplate("templates/endpoints/" +
                "GetEndpoints.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void promoteEndpoints_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        params.put("targetEnvironmentId", targetEnvironmentId);
        String body = MessageUtils.generateStringFromTemplate("templates/endpoints/" +
                "PromoteComponentEndpoints.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void promote_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("apiVersionId", versionId);
        params.put("sourceReleaseId", releaseId);
        params.put("targetEnvironmentId", targetEnvironmentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "promote.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentCountByOrgId_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentCountByOrgId = "/component-mgt/1.0.0/orgs/" + orgId + "/component-count";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetComponentCountByOrgId, accessToken);
    }

    @Test
    @CitrusTest
    public void getProxyDeployment_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("orgUuid", orgUuid);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("environmentId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getProxyDeploymentDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void handleConfigInit_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "handleConfigInit.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void buildLogs_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("runID", runID);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "buildLogs.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateComponentEndpoint_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        params.put("endpointId", endpointId);
        params.put("displayName", displayName);
        params.put("apiContext", apiContext);
        params.put("apiDefinitionPath", apiDefinitionPath);
        params.put("visibility", visibility);
        String body = MessageUtils.generateStringFromTemplate("templates/endpoints/" +
                "UpdateEndpoint.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void runPod_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForRunPod = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" + projectId +
        "/components/" + componentId + "/releases/" + releaseId + "/run-pod";
        String body = "";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForRunPod, body,
                accessToken);
    }
    
    @Test
    @CitrusTest
    public void getActionRunLogs_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetActionRunLogs = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" +
                projectId + "/components/" + componentId + "/runs/" + runID + "/logs";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetActionRunLogs,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentInitStatus_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentInitStatus = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" +
                projectId + "/components/" + componentId + "/init/status";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetComponentInitStatus, accessToken);
    }

    @Test
    @CitrusTest
    public void getFileContent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentInitStatus = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" +
                projectId + "/components/" + componentId + "/file/content?commitHash=" + commitHash +
                "&dirPath=";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetComponentInitStatus, accessToken);
    }

    @Test
    @CitrusTest
    public void componentEndpointApiDefinition_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("endpointId", endpointId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentEndpointApiDefinition.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deleteComponent.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deleteProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void build_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("buildId", buildId);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("commitHash", commitHash);
        params.put("devEnvId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "build.mustache", params);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }
}
