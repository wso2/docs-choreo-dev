package com.wso2.choreo.integration.tests.security.integrationComponents;

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

public class IntegrationComponentsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String orgHandler;
    private static String projectId;
    private static String componentHandler;
    private static String componentId;
    private static String releaseId;
    private static String devEnvironmentId;
    private static String orgUuid;
    private static String oasFilePath;
    private static String dockerfilePath;
    private static String dockerContext;
    private static String srcGitRepoUrl;
    private static String repositorySubPath;
    private static String componentName;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_DevportalElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgHandler = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_COMMENT_ID);
        componentHandler = Configuration.getSecurityConfig(SecurityConfigDefinition.IC_COMPONENT_HANDLER);
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.IC_COMPONENT_ID);
        releaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.IC_COMPONENT_ID);
        devEnvironmentId = Configuration.getSecurityConfig(SecurityConfigDefinition.IC_ENV_ID);
        componentName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_NAME);
        srcGitRepoUrl = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SRC_GIT_REPO_URL);
        repositorySubPath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_REPOSITORY_SUB_PATH);
        oasFilePath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_OAS_FILE_PATH);
        dockerfilePath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DOCKER_FILE_PATH);
        dockerContext = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DOCKER_CONTEXT);

    }

    @Test
    @CitrusTest
    public void createMIComponent_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.PROJECTS_GRAPHQL;
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("componentName", componentName);
        params.put("srcGitRepoUrl", srcGitRepoUrl);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createMIComponent.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createMIEventComponent_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.PROJECTS_GRAPHQL;
        Map<String, String> params = new HashMap<>();
        params.put("componentName", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("srcGitRepoUrl", srcGitRepoUrl);
        params.put("repositorySubPath", repositorySubPath);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createMIComponent.mustache", params);
        body = ObjectMapperUtil.mapToGraphQLQuery(body);
        SecurityUtils.elevatedAccessCheckForForbiddenPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createEnvVariable_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = "/devops/1.0.0/api/v1/components/integration/" + componentId + "/release/" +
                releaseId + "/environment-variables?project_id=" + projectId + "&env_id=" + devEnvironmentId +
                "&organization_id=" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createSecret.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createSecret_DevportalElevatedAccessCheck() throws Exception {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = "/devops/1.0.0/api/v1/components/integration/" + componentId + "/release/" +
                releaseId + "/secrets?project_id=" + projectId + "&env_id=" + devEnvironmentId +
                "&organization_id=" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createSecret.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }
}
