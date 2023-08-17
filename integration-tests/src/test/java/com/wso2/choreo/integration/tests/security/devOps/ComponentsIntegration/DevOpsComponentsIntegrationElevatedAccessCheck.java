package com.wso2.choreo.integration.tests.security.devOps.ComponentsIntegration;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class DevOpsComponentsIntegrationElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private static String componentId;
    private static String orgId;
    private static String projectId;
    private static String releaseId;
    private static String envId;
    private static String appEnvId;
    private static String integrationComponentId;
    private static String integrationCpReleaseId;

    private static String integrationCpSecretId;

    @BeforeClass
    public void setup_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_COMPONENT_ID);
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        releaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_RELEASE_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_ID);
        appEnvId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_APP_ENV_ID);
        integrationComponentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_INTEGRATION_COMPONENT_ID);
        integrationCpReleaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_INTEGRATION_CP_RELEASE_ID);
        integrationCpSecretId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_INTEGRATION_CP_SECRET_ID);
    }

    @Test
    @CitrusTest
    public void getPaths_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetPaths = Constant.DEVOPS_INTEGRATION +
                "paths?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetPaths,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getReleaseSecrets_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetReleaseSecrets = Constant.DEVOPS_INTEGRATION +
                "/" + componentId + "/release/" + releaseId + "/secrets?organization_id=" + orgId +
                "&project_id=" + projectId + "&env_id=" + envId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetReleaseSecrets,
                accessToken);
    }

    @Test
    @CitrusTest
    public void putReleaseSecrets_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPutReleaseSecrets = Constant.DEVOPS_INTEGRATION +
                "/" + componentId + "/release/" + releaseId + "/secrets?organization_id=" + orgId +
                "&project_id=" + projectId + "&env_id=" + envId;
        Map<String, String> params = new HashMap<>();
        params.put("env_id", envId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPutSecrets.mustache", params);
       SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForPutReleaseSecrets,
               body, accessToken);
    }

    @Test
    @CitrusTest
    public void getEnvironmentVariables_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvironmentVariables = Constant.DEVOPS_INTEGRATION +
                "/" + integrationComponentId + "/release/" + integrationCpReleaseId + "/environment-variables?" +
                "organization_id=" + orgId + "&project_id=" + projectId + "&env_id=" + envId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetEnvironmentVariables, accessToken);
    }

    @Test
    @CitrusTest
    public void putEnvironmentVariables_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPutEnvironmentVariables = Constant.DEVOPS_INTEGRATION +
                "/" + integrationComponentId + "/release/" + integrationCpReleaseId + "/environment-variables?" +
                "organization_id=" + orgId + "&project_id=" + projectId + "&env_id=" + envId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPutEnvironmentVariables.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPutEnvironmentVariables, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteSecrets_DevOpsComponentsIntegrationElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteSecrets = Constant.DEVOPS_INTEGRATION +
                "/secrets?organization_id=" + orgId + "&project_id=" + projectId + "&env_id=" + envId +
                "&secret_id=" + integrationCpSecretId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrlForDeleteSecrets,
                accessToken);
    }
}
