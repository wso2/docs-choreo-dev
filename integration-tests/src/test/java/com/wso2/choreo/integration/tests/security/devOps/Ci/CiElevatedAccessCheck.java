package com.wso2.choreo.integration.tests.security.devOps.Ci;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class CiElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private static String componentId;
    private static String orgId;
    private static String projectId;
    private static String tokenId;

    @BeforeClass
    public void setup_CiElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_COMPONENT_ID);
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        tokenId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_TOKEN_ID);
    }

    @Test
    @CitrusTest
    public void getToken_CiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetToken).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void revokeToken_CiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForRevokeToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens/" + tokenId
                + "/revoke?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForRevokeToken).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void tokenRegenerate_CiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForTokenRegenerate = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens/" + tokenId + "/regenerate?" +
                "organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForTokenRegenerate).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void postToken_CiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForPostToken).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void deleteToken_CiElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteToken = Constant.DEVOPS_CI +
                "/component/" + componentId + "/tokens?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteToken).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }
}
