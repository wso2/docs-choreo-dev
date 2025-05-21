package com.wso2.choreo.integration.tests.security.organizationManagement.OrgApi;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class OrgMgtOrgApiElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUuid;
    private static String approvalRequestId;
    private static String idpId;
    private static String orgName;
    private static String orgHandle;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_OrgMgtOrgApiElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgName = Configuration.getSecurityConfig(SecurityConfigDefinition.ORG_MGT_ORG_NAME);
    }

    @Test
    @CitrusTest
    public void getOrganizationByOrgHandle_OrgMgtOrgApiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetOrg = Constant.ORG_API_SUFFIX + orgHandle;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetOrg,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getOrgsMetaData_OrgMgtOrgApiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetOrgsMetaData = "/orgs/1.0.0/orgs-metadata?orghandle=" + orgHandle;
        SecurityUtils.successfulCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetOrgsMetaData,
                accessToken);
    }
}
