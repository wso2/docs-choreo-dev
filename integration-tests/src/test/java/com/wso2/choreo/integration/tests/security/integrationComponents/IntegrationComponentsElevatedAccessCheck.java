package com.wso2.choreo.integration.tests.security.integrationComponents;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
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
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createMIComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void retreiveComponent_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.PROJECTS_GRAPHQL;
        Map<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("componentHandler", componentHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "retreiveComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createMIEventComponent_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrl = Constant.PROJECTS_GRAPHQL;
        Map<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("componentHandler", componentHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "retreiveComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createBYOCComponent_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.PROJECTS_GRAPHQL;
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createBYOCComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createEnvVariable_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = "/devops/1.0.0/api/v1/components/integration/" + componentId + "/release/" +
                releaseId + "/environment-variables?project_id=" + projectId + "&env_id=" + devEnvironmentId +
                "&organization_id=" + orgUuid;
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "createBYOCComponent.mustache", params);
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

    @Test
    @CitrusTest
    public void usageInsightsListEnvironments_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrl = Constant.INSIGHTS_SUFFIX;
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "usageInsightsListEnvironments.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void insights_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrl = Constant.INSIGHTS_SUFFIX;
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("devEnvironmentId", devEnvironmentId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/integrationComponents/" +
                "insights.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }
}
