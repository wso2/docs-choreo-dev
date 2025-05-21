package com.wso2.choreo.integration.tests.security.apim.Publisher;

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

public class ApimPublisherElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUuid;
    private static String apiUuid;
    private static String componentId;
    private static String versionId;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ApimPublisherElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        apiUuid = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_API_UUID);
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_COMPONENT_ID);
        versionId = Configuration.getSecurityConfig(SecurityConfigDefinition.APIM_VERSION_ID);
    }

    @Test
    @CitrusTest
    public void createApimApi_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis?organizationId=" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/apimanager/publisher/" +
                "createApimAPI.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getApimApi_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void updateApi_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis?organizationId=" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/apimanager/publisher/" +
                "updateApi.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getApimDeployments_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/deployments?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getSwagger_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/swagger?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getSubscriptions_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/subscriptions?organizationId=" + orgUuid +
                "&apiId=" + apiUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void createRevision_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/revisions?organizationId=" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/apimanager/publisher/" +
                "createRevision.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getRevisions_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/revisions?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getOprationPolicies_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/operation-policies?limit=100&organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void changeLifecycle_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/change-lifecycle?organizationId=" + orgUuid + "&apiId=" +
                apiUuid + "&action=Publish";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, null,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getLifecycleHistory_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/lifecycle-history?organizationId=" +
                orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void postDocuments_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/documents?organizationId=" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/apimanager/publisher/" +
                "postDocuments.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getDocuments_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/documents?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getThrottlingPolicies_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/throttling-policies/subscription?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void generateKey_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "/generate-key?organizationId=" + orgUuid +
                "&keyType=Development";
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/apimanager/publisher/" +
                "generateKey.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getEnvironmentKeys_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid +
                "/environments/dev-us-east-azure/keys?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteApimApi_ApimPublisherElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrl = Constant.PUBLISHER_URL + "/apis/" + apiUuid + "?organizationId=" + orgUuid;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }
}
