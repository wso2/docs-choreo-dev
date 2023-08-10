package com.wso2.choreo.integration.tests.security.devOps.ApiV1ComponentsIntegration;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration extends TestNGCitrusSpringSupport {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private static String componentId;
    private static String orgId;
    private static String projectId;
    private static String releaseId;
    private static String envId;
    private static String appEnvId;
    private static String integrationComponentId;
    private static String integrationCpReleaseId;

    private static String integrationCpSecretId;

    @BeforeClass
    public void setup_ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getConfig(ConfigDefinition.DEVOPS_COMPONENT_ID);
        orgId = Configuration.getConfig(ConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getConfig(ConfigDefinition.DEVOPS_PROJECT_ID);
        releaseId = Configuration.getConfig(ConfigDefinition.DEVOPS_RELEASE_ID);
        envId = Configuration.getConfig(ConfigDefinition.DEVOPS_ENV_ID);
        appEnvId = Configuration.getConfig(ConfigDefinition.DEVOPS_APP_ENV_ID);
        integrationComponentId = Configuration.getConfig(ConfigDefinition.DEVOPS_INTEGRATION_COMPONENT_ID);
        integrationCpReleaseId = Configuration.getConfig(ConfigDefinition.DEVOPS_INTEGRATION_CP_RELEASE_ID);
        integrationCpSecretId = Configuration.getConfig(ConfigDefinition.DEVOPS_INTEGRATION_CP_SECRET_ID);
    }

    @Test
    @CitrusTest
    public void getPaths_ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetPaths = Constant.DEVOPS_INTEGRATION +
                "paths?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetPaths).
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
    public void getReleaseSecrets_ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetReleaseSecrets = Constant.DEVOPS_INTEGRATION +
                "/" + componentId + "/release/" + releaseId + "/secrets?organization_id=" + orgId +
                "&project_id=" + projectId + "&env_id=" + envId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetReleaseSecrets).
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
    public void putReleaseSecrets_ChoreoElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPutReleaseSecrets = Constant.DEVOPS_INTEGRATION +
                "/" + componentId + "/release/" + releaseId + "/secrets?organization_id=" + orgId +
                "&project_id=" + projectId + "&env_id=" + envId;
        Map<String, String> params = new HashMap<>();
        params.put("env_id", envId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPutSecrets.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForPutReleaseSecrets).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void getEnvironmentVariables_ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvironmentVariables = Constant.DEVOPS_INTEGRATION +
                "/" + integrationComponentId + "/release/" + integrationCpReleaseId + "/environment-variables?" +
                "organization_id=" + orgId + "&project_id=" + projectId + "&env_id=" + envId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetEnvironmentVariables).
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
    public void putEnvironmentVariables_ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetEnvironmentVariables = Constant.DEVOPS_INTEGRATION +
                "/" + integrationComponentId + "/release/" + integrationCpReleaseId + "/environment-variables?" +
                "organization_id=" + orgId + "&project_id=" + projectId + "&env_id=" + envId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPutEnvironmentVariables.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForGetEnvironmentVariables).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void deleteSecrets_ChoreoElevatedAccessCheck_ApiV1ComponentsIntegration() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteSecrets = Constant.DEVOPS_INTEGRATION +
                "/secrets?organization_id=" + orgId + "&project_id=" + projectId + "&env_id=" + envId +
                "&secret_id=" + integrationCpSecretId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteSecrets).
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
