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

import java.util.Map;

public class OrgMgtCustomDomainsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
    }

//    @Test
//    @CitrusTest
//    public void saveOrganizationThemeToDB_OrgMgtCustomDomainsElevatedAccessCheck() throws Exception {
//        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
//        String requestUrlForSaveOrganizationThemeToDB = Constant.DEVPORTAL_ENDPOINT_SUFFIX + Constant.DEVPORTAL_APPLICATIONS +
//                "?organizationId=" + orgId;
//        String body = MessageUtils.
//                generateStringFromTemplate("templates/devportal/queryForCreateApp.mustache", null);
//        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForCreateApp, body,
//                accessToken);
//    }
}
