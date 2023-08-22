package com.wso2.choreo.integration.tests.security.OrganizationManagement.CustomDomains;

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

public class OrgMgtCustomDomainsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUuid;
    private static String approvalRequestId;
    private static String idpId;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        approvalRequestId = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_APPROVAL_REQ_ID);
        idpId = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_IDP_ID);
    }

    @Test
    @CitrusTest
    public void saveOrganizationThemeToDB_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForSaveOrganizationThemeToDB = Constant.ORG_MGT_SUFFIX + orgUuid + Constant.DEFAULT_THEME;
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement" +
                "/saveOrganizationThemeToDB.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForSaveOrganizationThemeToDB, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getOrganizationThemeByThemeName_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetOrganizationTheme = Constant.ORG_MGT_SUFFIX + orgUuid + Constant.DEFAULT_THEME;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetOrganizationTheme, accessToken);
    }

    @Test
    @CitrusTest
    public void changeLiveThemeConfiguration_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForChangeLiveThemeConfig = Constant.ORG_MGT_SUFFIX + orgUuid + Constant.DEFAULT_THEME +
                "/change-live?action=upload";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForChangeLiveThemeConfig, "", accessToken);
    }

    @Test
    @CitrusTest
    public void resetThemeConfigurationToDefault_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForResetThemeConfig = Constant.ORG_MGT_SUFFIX + orgUuid + Constant.DEFAULT_THEME;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForResetThemeConfig, accessToken);
    }

    @Test
    @CitrusTest
    public void getEnterpriseConfigsByOrgUUID_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnterpriseConfigs = Constant.ORG_MGT_SUFFIX + orgUuid + "/enterprise-login/config";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetEnterpriseConfigs, accessToken);
    }

    @Test
    @CitrusTest
    public void getSelfSignupConfigsByOrganizationUUID_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSelfSignupConfigs = Constant.ORG_MGT_SUFFIX + orgUuid + "/self-signup/config";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetSelfSignupConfigs, accessToken);
    }

    @Test
    @CitrusTest
    public void updateSelfSignupConfigsByOrganizationUUID_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateSelfSignupConfigs = Constant.ORG_MGT_SUFFIX + orgUuid + "/self-signup/config";
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement" +
                "/updateSelfSignupConfigs.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateSelfSignupConfigs, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getSelfSignupRequestsByOrganizationUUID_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSelfSignupRequests = Constant.ORG_MGT_SUFFIX + orgUuid + "/self-signup/approval-requests";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetSelfSignupRequests, accessToken);
    }

    @Test
    @CitrusTest
    public void getSelfSignupRequestByOrganizationUUIDandRequestId_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSelfSignupRequests = Constant.ORG_MGT_SUFFIX + orgUuid +
                "/self-signup/approval-requests/" + approvalRequestId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetSelfSignupRequests, accessToken);
    }

    @Test
    @CitrusTest
    public void updateTheStatusOfApprovalRequest_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateStatus = Constant.ORG_MGT_SUFFIX + orgUuid +
                "/self-signup/approval-requests/change-status";
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        params.put("SELF_SIGNUP_USER_IDP_ID", idpId);
        String body = MessageUtils.generateStringFromTemplate("templates/orgManagement" +
                "/updateTheStatusOfApprovalRequest.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateStatus, body, accessToken);
    }
}
