package com.wso2.choreo.integration.tests.security.apim.ProxyDeployer;

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

public class ApimProxyDeployerElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String componentId;
    private static String versionId;
    private static String envId;
    private static String proxyBuildId;
    private static String orgUuid;
    private static String revisionId;
    private static String apiUuid;
    private static String requestId;
    private static String projectId;
    private static String prodEnvId;
    private static String prodBuildId;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_COMPONENT_ID);
        versionId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_VERSION_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_ENV_ID);
        proxyBuildId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_PROXY_BUILD_ID);
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        apiUuid = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_API_UUID);
        revisionId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_REVISION_ID);
        requestId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_REQUEST_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_PROJECT_ID);
        prodEnvId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_PROD_ENV_ID);
        proxyBuildId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_PROD_BUILD_ID);
    }

    @Test
    @CitrusTest
    public void getProxyDeployments_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/deployments?environmentId=" + envId + "&accessMode=external";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void initiateProxyDeployment_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/initiate-deployment?environmentId=" + envId + "&accessMode=external";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, "",
                accessToken);
    }

    @Test
    @CitrusTest
    public void getBuilds_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/builds?limit=20";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getSingleBuild_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/builds/" + proxyBuildId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getBuildStatus_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/builds/" + proxyBuildId + "/status";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void deployService_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/deploy-service?buildId=" + proxyBuildId + "&environmentId=" + envId + "&accessMode=external";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, "",
                accessToken);
    }

    @Test
    @CitrusTest
    public void undeploy_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/un-deploy?environmentId=" + envId + "&buildId=" + proxyBuildId + "&accessMode=external";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, "",
                accessToken);
    }

    @Test
    @CitrusTest
    public void redeploy_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/re-deploy?environmentId=" + envId + "&buildId=" + proxyBuildId + "&accessMode=external";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, "",
                accessToken);
    }

    @Test
    @CitrusTest
    public void deploySettings_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/deploy-settings?environmentId=" + envId + "&buildId=" + proxyBuildId + "&description=&revisionId=" +
                revisionId + "&apiId=" + apiUuid + "&accessMode=external&isDevEnv=true";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, "",
                accessToken);
    }

    @Test
    @CitrusTest
    public void getDeploymentStatus_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/deployment-status?requestId=" + requestId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void listEnvironments_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = "/devops/1.0.0/api/v1/organizations/" + orgUuid + "/environments?organization_id=" +
                orgUuid + "&project_id=" + projectId + "&include=environment_clusters";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getReleaseIDs_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/releaseIds?organizationId=" +
                orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void changeAccessibility_ApimProxyDeployerElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String requestUrl = Constant.PROXY_DEPLOYER_URL + "/components/" + componentId + "/versions/" + versionId +
                "/change-accessibility?accessMode=internal";
        Map<String, String> params = new HashMap<>();
        params.put("devEnvId", envId);
        params.put("devBuildId", proxyBuildId);
        params.put("prodEnvId", prodEnvId);
        params.put("prodBuildId", prodBuildId);
        String body = MessageUtils.generateStringFromTemplate("templates/api-proxy/" +
                "changeAccessibility.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }
}
