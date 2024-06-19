/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.tests.selfSignup;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.orgmgt.OrgMgtConstants;
import com.wso2.choreo.integration.common.orgmgt.OrgMgtUtils;
import com.wso2.choreo.integration.common.scim.ScimUtils;
import com.wso2.choreo.integration.common.usermgt.UserMgtUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.orgmgt.ApprovalRequestList;
import com.wso2.choreo.integration.models.orgmgt.ApprovalStatus;
import com.wso2.choreo.integration.models.orgmgt.SelfSignupConfig;
import com.wso2.choreo.integration.models.scim.EmailInfo;
import com.wso2.choreo.integration.models.scim.Name;
import com.wso2.choreo.integration.models.scim.UserCreateRequest;
import com.wso2.choreo.integration.models.scim.UserCreateResponse;
import com.wso2.choreo.integration.models.usermgt.RegisterEnterpriseUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;

public class ChoreoSelfSignup extends TestNGCitrusSpringSupport {

    private static final String SELF_SIGNUP_USER_FAMILY_NAME = "selfSignupUserFamilyName";
    private static final String SELF_SIGNUP_USER_GIVEN_NAME = "selfSignupUserGivenName";

    private String selfSignupOrgUuid;
    private String selfSignupOrgHandle;
    private String selfSignupUserEmail;
    private String selfSignupUserPassword;
    private String selfSignupAdminEmail;
    private String selfSignupAdminPassword;
    private String adminUserAccessToken;
    private String selfSignupUserIdpId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ChoreoSelfSignupTests() throws Exception {
        selfSignupOrgUuid = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_UUID);
        selfSignupOrgHandle = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE);
        selfSignupUserEmail = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_USER_EMAIL);
        selfSignupUserPassword = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_USER_PASSWORD);
        selfSignupAdminEmail = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_ADMIN_EMAIL);
        selfSignupAdminPassword = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_ADMIN_PASSWORD);
        adminUserAccessToken = TestContext.getSelfSignupTestAdminUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void enableAutoApproval_ChoreoSelfSignupTests() throws Exception {

        SelfSignupConfig selfSignupConfigRequest = SelfSignupConfig.builder()
                .orgUuid(selfSignupOrgUuid)
                .isEnabled(true)
                .isAutoApprovalEnabled(true)
                .isCustom(false).build();
        SelfSignupConfig selfSignupConfigResponse = OrgMgtUtils.updateSelfSignupConfig(this, citrusClients,
                adminUserAccessToken, selfSignupOrgUuid, selfSignupConfigRequest);
        Assert.assertTrue(selfSignupConfigResponse.getIsAutoApprovalEnabled());
    }

    @Test(dependsOnMethods = {"enableAutoApproval_ChoreoSelfSignupTests"})
    @CitrusTest
    public void selfSignupWhenAutoApprovalEnabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupAdminUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupAdminEmail,
                selfSignupAdminPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoAdminToken = selfSignupAdminUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .emails(Arrays.asList(EmailInfo.builder()
                        .primary(true).value(selfSignupUserEmail).build()))
                .name(Name.builder()
                        .familyName(SELF_SIGNUP_USER_FAMILY_NAME).givenName(SELF_SIGNUP_USER_GIVEN_NAME).build())
                .password(selfSignupUserPassword)
                .userName("DEFAULT/" + selfSignupUserEmail).build();
        UserCreateResponse userCreateResponse = ScimUtils.createUser(this, citrusClients,
                asgardeoAdminToken, selfSignupOrgHandle, userCreateRequest);
        Assert.assertNotNull(userCreateResponse.getId());

        TokenHandler selfSignupUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupUserEmail,
                selfSignupUserPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoUserToken = selfSignupUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        RegisterEnterpriseUserResponse registerEnterpriseUserResponse = UserMgtUtils.addEnterpriseUser(this,
                citrusClients, asgardeoUserToken);
        selfSignupUserIdpId = registerEnterpriseUserResponse.getIdpId();
    }

    @Test(dependsOnMethods = {"selfSignupWhenAutoApprovalEnabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void signInSelfSignupUserWhenAutoApprovalEnabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupUserEmail,
                selfSignupUserPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoUserToken = selfSignupUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        UserMgtUtils.validateUser(this, citrusClients, asgardeoUserToken, HttpStatus.OK);
    }

    @Test(dependsOnMethods = {"signInSelfSignupUserWhenAutoApprovalEnabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void deleteSelfSignupUserWhenAutoApprovalEnabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupAdminUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupAdminEmail,
                selfSignupAdminPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoAdminToken = selfSignupAdminUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        // The user is deleted to reuse the same user email
        ScimUtils.deleteUser(this, citrusClients, asgardeoAdminToken, selfSignupOrgHandle, selfSignupUserIdpId);
    }

    @Test(dependsOnMethods = {"deleteSelfSignupUserWhenAutoApprovalEnabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void disableAutoApproval_ChoreoSelfSignupTests() throws Exception {

        SelfSignupConfig selfSignupConfigRequest = SelfSignupConfig.builder()
                .orgUuid(selfSignupOrgUuid)
                .isEnabled(true)
                .isAutoApprovalEnabled(false)
                .isCustom(false).build();
        SelfSignupConfig selfSignupConfigResponse = OrgMgtUtils.updateSelfSignupConfig(this, citrusClients,
                adminUserAccessToken, selfSignupOrgUuid, selfSignupConfigRequest);
        Assert.assertFalse(selfSignupConfigResponse.getIsAutoApprovalEnabled());
    }

    @Test(dependsOnMethods = {"disableAutoApproval_ChoreoSelfSignupTests"})
    @CitrusTest
    public void selfSignupWhenAutoApprovalDisabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupAdminUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupAdminEmail,
                selfSignupAdminPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoAdminToken = selfSignupAdminUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .emails(Arrays.asList(EmailInfo.builder()
                        .primary(true).value(selfSignupUserEmail).build()))
                .name(Name.builder()
                        .familyName(SELF_SIGNUP_USER_FAMILY_NAME).givenName(SELF_SIGNUP_USER_GIVEN_NAME).build())
                .password(selfSignupUserPassword)
                .userName("DEFAULT/" + selfSignupUserEmail).build();
        UserCreateResponse userCreateResponse = ScimUtils.createUser(this, citrusClients,
                asgardeoAdminToken, selfSignupOrgUuid, userCreateRequest);
        Assert.assertNotNull(userCreateResponse.getId());

        TokenHandler selfSignupUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupUserEmail,
                selfSignupUserPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoUserToken = selfSignupUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        RegisterEnterpriseUserResponse registerEnterpriseUserResponse = UserMgtUtils.addEnterpriseUser(this,
                citrusClients, asgardeoUserToken);
        selfSignupUserIdpId = registerEnterpriseUserResponse.getIdpId();
    }

    @Test(dependsOnMethods = {"selfSignupWhenAutoApprovalDisabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void getApprovalRequests_ChoreoSelfSignupTests() throws Exception {

        ApprovalRequestList approvalRequestList = OrgMgtUtils.getApprovalRequests(this, citrusClients,
                adminUserAccessToken, selfSignupOrgUuid);
        Assert.assertNotNull(approvalRequestList);
        Assert.assertTrue(approvalRequestList.getList().size() > 0);
    }

    @Test(dependsOnMethods = {"getApprovalRequests_ChoreoSelfSignupTests"})
    @CitrusTest
    public void approveRequest_ChoreoSelfSignupTests() throws Exception {

        ApprovalStatus approvalRequestStatus = ApprovalStatus.builder()
                .orgUuid(selfSignupOrgUuid)
                .userIdpId(selfSignupUserIdpId)
                .status(OrgMgtConstants.ApprovalStatus.APPROVED.getValue()).build();
        ApprovalStatus approvalStatus = OrgMgtUtils.updateApprovalRequestStatus(this, citrusClients,
                adminUserAccessToken, selfSignupOrgUuid, approvalRequestStatus);
        Assert.assertEquals(approvalStatus.getStatus(), OrgMgtConstants.ApprovalStatus.APPROVED);
    }

    @Test(dependsOnMethods = {"approveRequest_ChoreoSelfSignupTests"})
    @CitrusTest
    public void signInApprovedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupUserEmail,
                selfSignupUserPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoUserToken = selfSignupUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        UserMgtUtils.validateUser(this, citrusClients, asgardeoUserToken, HttpStatus.OK);
    }

    @Test(dependsOnMethods = {"signInApprovedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void deleteApprovedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupAdminUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupAdminEmail,
                selfSignupAdminPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoAdminToken = selfSignupAdminUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        // The user is deleted to reuse the same user email
        ScimUtils.deleteUser(this, citrusClients, asgardeoAdminToken, selfSignupOrgHandle, selfSignupUserIdpId);
    }

    @Test(dependsOnMethods = {"deleteApprovedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void selfSignupToGetRejectedWhenAutoApprovalDisabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupAdminUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupAdminEmail,
                selfSignupAdminPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoAdminToken = selfSignupAdminUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .emails(Arrays.asList(EmailInfo.builder()
                        .primary(true).value(selfSignupUserEmail).build()))
                .name(Name.builder()
                        .familyName(SELF_SIGNUP_USER_FAMILY_NAME).givenName(SELF_SIGNUP_USER_GIVEN_NAME).build())
                .password(selfSignupUserPassword)
                .userName("DEFAULT/" + selfSignupUserEmail).build();
        UserCreateResponse userCreateResponse = ScimUtils.createUser(this, citrusClients,
                asgardeoAdminToken, selfSignupOrgUuid, userCreateRequest);
        Assert.assertNotNull(userCreateResponse.getId());

        TokenHandler selfSignupUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupUserEmail,
                selfSignupUserPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoUserToken = selfSignupUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        RegisterEnterpriseUserResponse registerEnterpriseUserResponse = UserMgtUtils.addEnterpriseUser(this,
                citrusClients, asgardeoUserToken);
        selfSignupUserIdpId = registerEnterpriseUserResponse.getIdpId();
    }

    @Test(dependsOnMethods = {"selfSignupToGetRejectedWhenAutoApprovalDisabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void rejectRequest_ChoreoSelfSignupTests() throws Exception {

        ApprovalStatus approvalRequestStatus = ApprovalStatus.builder()
                .orgUuid(selfSignupOrgUuid)
                .userIdpId(selfSignupUserIdpId)
                .status(OrgMgtConstants.ApprovalStatus.REJECTED.getValue()).build();
        ApprovalStatus approvalStatus = OrgMgtUtils.updateApprovalRequestStatus(this, citrusClients,
                adminUserAccessToken, selfSignupOrgUuid, approvalRequestStatus);
        Assert.assertEquals(approvalStatus.getStatus(), OrgMgtConstants.ApprovalStatus.REJECTED);
    }

    @Test(dependsOnMethods = {"rejectRequest_ChoreoSelfSignupTests"})
    @CitrusTest
    public void signInRejectedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupUserEmail,
                selfSignupUserPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoUserToken = selfSignupUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        UserMgtUtils.validateUser(this, citrusClients, asgardeoUserToken, HttpStatus.UNAUTHORIZED);
    }

    @Test(dependsOnMethods = {"signInRejectedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests"})
    @CitrusTest
    public void deleteRejectedSelfSignupUserWhenAutoApprovalDisabled_ChoreoSelfSignupTests() throws Exception {

        TokenHandler selfSignupAdminUserTokenHandler = new TokenHandler.Builder(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE),
                selfSignupAdminEmail,
                selfSignupAdminPassword)
                .asgardeoClientId(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID))
                .asgardeoClientSecret(
                        Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET))
                .cpAppClientId(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_ID))
                .cpAppClientSecret(Configuration.getConfig(ConfigDefinition.CP_APP_CLIENT_SECRET)).build();
        String asgardeoAdminToken = selfSignupAdminUserTokenHandler.getAsgardeoUserToken(
                Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE));
        // The user is deleted to reuse the same user email
        ScimUtils.deleteUser(this, citrusClients, asgardeoAdminToken, selfSignupOrgHandle, selfSignupUserIdpId);
    }
}
