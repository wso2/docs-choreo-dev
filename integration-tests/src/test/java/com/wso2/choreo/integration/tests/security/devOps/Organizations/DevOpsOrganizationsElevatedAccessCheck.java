package com.wso2.choreo.integration.tests.security.devOps.Organizations;

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

public class DevOpsOrganizationsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String projectId;
    private static String envId;
    private static String vhost;
    private static String envName;
    private static String orgIntId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_ID);
        vhost = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_VHOST);
        envName = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_NAME);
        orgIntId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_INT_ID);
    }

    @Test
    @CitrusTest
    public void getSubscription_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSubscription = Constant.DEVOPS_ORGANIZATIONS + orgId + "/subscription";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetSubscription,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getDataplanes_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDataplanes = Constant.DEVOPS_ORGANIZATIONS + orgId + "/projects/" +
                projectId + "/dataplanes?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetDataplanes,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getEnvironmentsFromEnvId_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvironmentsFromEnvId = Constant.DEVOPS_ORGANIZATIONS + orgId + "/environments/" +
                envId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetEnvironmentsFromEnvId, accessToken);
    }

    @Test
    @CitrusTest
    public void getEnvCritical_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvCritical = Constant.DEVOPS_ORGANIZATIONS + orgId +
                "/environments/env-critical?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetEnvCritical,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getEnvironments_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvironments = Constant.DEVOPS_ORGANIZATIONS + orgId +
                "/environments?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetEnvironments,
                accessToken);
    }

    @Test
    @CitrusTest
    public void postValidateVhost_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostValidateVhost = Constant.DEVOPS_ORGANIZATIONS + orgId +
                "/apim/environments/validate-vhost?organization_id=" + orgId + "&project_id=" + projectId +
                "&vhost=" + vhost;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForPostValidateVhost,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void postValidateName_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostValidateName = Constant.DEVOPS_ORGANIZATIONS + orgId +
                "/apim/environments/validate-name?organization_id=" + orgId + "&projectId=" + projectId +
                "&name=" + envName;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForPostValidateName,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void getApimEnvironments_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetApimEnvironments = Constant.DEVOPS_ORGANIZATIONS + orgId +
                "/apim/environments?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetApimEnvironments, accessToken);
    }

    @Test
    @CitrusTest
    public void getProjects_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetProjects = Constant.DEVOPS_ORGANIZATIONS + orgIntId +
                "/projects?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetProjects,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getEnvironmentTemplates_DevOpsOrganizationsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvironmentTemplates = Constant.DEVOPS_ORGANIZATIONS + orgIntId +
                "/environment-templates?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetEnvironmentTemplates, accessToken);
    }
}
