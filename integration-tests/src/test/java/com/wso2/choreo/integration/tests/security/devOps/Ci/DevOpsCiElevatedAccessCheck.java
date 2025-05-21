package com.wso2.choreo.integration.tests.security.devOps.Ci;

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

public class DevOpsCiElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private static String componentId;
    private static String orgId;
    private static String projectId;
    private static String tokenId;

    @BeforeClass
    public void setup_DevOpsCiElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_COMPONENT_ID);
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        tokenId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_TOKEN_ID);
    }

    @Test
    @CitrusTest
    public void getToken_DevOpsCiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens?organization_id=" + orgId +
                "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetToken,
                accessToken);
    }

    @Test
    @CitrusTest
    public void revokeToken_DevOpsCiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForRevokeToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens/" + tokenId
                + "/revoke?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrlForRevokeToken,
                accessToken);
    }

    @Test
    @CitrusTest
    public void tokenRegenerate_DevOpsCiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForTokenRegenerate = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens/" + tokenId + "/regenerate?" +
                "organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForTokenRegenerate,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void postToken_DevOpsCiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateToken.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForPostToken,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteToken_DevOpsCiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens?organization_id=" + orgId +
                "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrlForDeleteToken,
                accessToken);
    }
}
