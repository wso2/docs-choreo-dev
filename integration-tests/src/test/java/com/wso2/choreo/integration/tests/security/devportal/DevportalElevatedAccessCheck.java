package com.wso2.choreo.integration.tests.security.devportal;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DevportalElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String appId;
    private static String keyManager;
    private static String keyMappingId;
    private static String consumerKey;
    private static String consumerSecret;
    private static String keyType;
    private static String apiId;
    private static String throttlingPolicy;
    private static String appName;
    private static String ownerId;
    private static String subscriptionId;
    private static String commentId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_DevportalElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        appId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_APP_ID);
        keyManager = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_KEY_MANAGER);
        keyMappingId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_KEY_MAPPING_ID);
        consumerKey = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_CONSUMER_KEY);
        consumerSecret = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_CONSUMER_SECRET);
        keyType = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_KEY_TYPE);
        apiId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_API_ID);
        throttlingPolicy = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_THROTTLING_POLICY);
        appName = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_APP_NAME);
        ownerId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_OWNER_ID);
        subscriptionId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_SUBSCRIPTION_ID);
        commentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVPORTAL_COMMENT_ID);
    }

    @Test
    @CitrusTest
    public void createApp_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForCreateApp = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "?organizationId=" + orgId;
        String body = MessageUtils.
                generateStringFromTemplate("templates/devportal/queryForCreateApp.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForCreateApp, body,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getAppList_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetAppList = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "?organizationId=" + orgId + "&limit=2147483647";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetAppList,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getAppDetails_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetAppDetails = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "/" + appId + "?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetAppDetails,
                accessToken);
    }

    @Test
    @CitrusTest
    public void deleteApp_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetAppDetails = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "/" + appId + "?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrlForGetAppDetails,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getKeyManagers_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetKeyManagers = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/key-managers?organizationId=" +
                orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetKeyManagers,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getOauthKeys_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetKeyManagers = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "/" + appId + "/oauth-keys?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetKeyManagers,
                accessToken);
    }

    @Test
    @CitrusTest
    public void generateKeys_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGenerateKeys = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "/" + appId + "/generate-keys?organizationId=" + orgId;
        Map<String, String> params = new HashMap<>();
        params.put("key_manager", keyType);
        params.put("key_manager", keyManager);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devportal/queryForGenerateKeys.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForGenerateKeys,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateKeys_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForUpdateKeys = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
                "/" + appId + "/oauth-keys/" + keyMappingId + "?organizationId=" + orgId;
        Map<String, String> params = new HashMap<>();
        params.put("key_manager", keyManager);
        params.put("key_mapping_id", keyMappingId);
        params.put("consumer_key", consumerKey);
        params.put("consumer_secret", consumerSecret);
        params.put("key_type", keyType);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devportal/queryForUpdateKeys.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateKeys, body,
                accessToken);
    }

    @Test
    @CitrusTest
    public void addSubscription_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForAddSubscriptions = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/subscriptions?organizationId=" +
                orgId;
        Map<String, String> params = new HashMap<>();
        params.put("application_id", appId);
        params.put("api_id", apiId);
        params.put("throttling_policy", throttlingPolicy);
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForAddSubscription.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForAddSubscriptions,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateApp_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForUpdateApp = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS + "/" +
                appId + "?organizationId=" + orgId;
        Map<String, String> params = new HashMap<>();
        params.put("application_id", appId);
        params.put("application_name", appName);
        params.put("throttling_policy", throttlingPolicy);
        params.put("owner_id", ownerId);
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForUpdateApp.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateApp,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void getAPIs_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetAPIs = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetAPIs,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getThumbnail_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetThumbnails = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/thumbnail?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetThumbnails,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getApiDetails_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetApiDetails = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetApiDetails,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getApiComments_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetApiComments = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/comments?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetApiComments,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getApiRatings_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetApiRatings = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/ratings?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetApiRatings,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getApiDocuments_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetApiDocuments = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/documents?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetApiDocuments,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getApiSwagger_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetApiSwagger = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/swagger?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetApiSwagger,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getSDKDetails_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetSDKDetails = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/sdk-gen/languages?organizationId=" +
                orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetSDKDetails,
                accessToken);
    }

    @Test
    @CitrusTest
    public void updateUserRatingForAPI_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForUpdateUserRatingForAPI = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/user-rating?organizationId=" + orgId;
        Map<String, String> params = new HashMap<>();
        params.put("api_id", apiId);
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForUpdateUserRating.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateUserRatingForAPI, body, accessToken);
    }

    @Test
    @CitrusTest
    public void generateToken_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGenerateToken = Constant.DEVPORTAL_ENDPOINT_SUFFIX +
                Constant.DEVPORTAL_APPLICATIONS + "/" + appId + "/oauth-keys/" + keyMappingId +
                "/generate-token?organizationId=" + orgId ;
        Map<String, String> params = new HashMap<>();
        params.put("consumer_secret", consumerSecret);
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForGenerateToken.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForGenerateToken, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateSubscription_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGenerateToken = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/subscriptions/" + subscriptionId +
                "?organizationId=" + orgId ;
        Map<String, String> params = new HashMap<>();
        params.put("application_id", appId);
        params.put("api_id", apiId);
        params.put("throttling_policy", throttlingPolicy);
        params.put("requested_throttling_policy", throttlingPolicy);
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForUpdateSubscription.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForGenerateToken, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getAPITags_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetAPITags = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/tags?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetAPITags,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getAPICategories_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetAPICategories = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/api-categories?organizationId=" +
                orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetAPICategories,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getSubscriptions_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForGetSubscriptions = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/subscriptions?apiId=" +
                apiId + "&organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetSubscriptions,
                accessToken);
    }

    @Test
    @CitrusTest
    public void addComment_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForAddComment = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/comments?organizationId=" + orgId;
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForAddComment.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForAddComment,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void editComment_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForEditComment = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/comments/" + commentId + "?organizationId=" + orgId;
        String body = MessageUtils.generateStringFromTemplate("templates/devportal/" +
                "queryForAddComment.mustache", null);
        SecurityUtils.elevatedAccessCheckForPatchRequests(this, choreoCPTestClient, requestUrlForEditComment,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteComment_DevportalElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String requestUrlForDeleteComment = Constant.DEVPORTAL_ENDPOINT_SUFFIX + "/apis/" + apiId +
                "/comments/" + commentId + "?organizationId=" + orgId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrlForDeleteComment,
                accessToken);
    }
}
