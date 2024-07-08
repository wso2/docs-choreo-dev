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
import com.wso2.choreo.integration.common.orgmgt.OrgMgtUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.orgmgt.ApprovalRequestList;
import com.wso2.choreo.integration.models.orgmgt.SelfSignupConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class ChoreoSelfSignup extends TestNGCitrusSpringSupport {

    private String selfSignupOrgUuid;
    private String adminUserAccessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ChoreoSelfSignupTests() throws Exception {
        selfSignupOrgUuid = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_UUID);
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
    public void getApprovalRequests_ChoreoSelfSignupTests() throws Exception {

        ApprovalRequestList approvalRequestList = OrgMgtUtils.getApprovalRequests(this, citrusClients,
                adminUserAccessToken, selfSignupOrgUuid);
        Assert.assertNotNull(approvalRequestList);
        Assert.assertTrue(approvalRequestList.getList().size() > 0);
    }
}
