package com.wso2.choreo.integration.tests.security.devOps.Environments;

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

public class EnvironmentsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String envId;
    private static String orgId;
    private static String projectId;
    private static String secretId;
    private static String appEnvId;
    private static String configMapId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_EnvironmentsElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        envId = Configuration.getConfig(ConfigDefinition.DEVOPS_ENV_ID);
        orgId = Configuration.getConfig(ConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getConfig(ConfigDefinition.DEVOPS_PROJECT_ID);
        secretId = Configuration.getConfig(ConfigDefinition.DEVOPS_SECRET_ID);
        appEnvId = Configuration.getConfig(ConfigDefinition.DEVOPS_APP_ENV_ID);
        configMapId = Configuration.getConfig(ConfigDefinition.DEVOPS_CONFIG_MAP_ID);
    }

    @Test
    @CitrusTest
    public void getStorageClasses_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetStorageClasses = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/storage-classes?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetStorageClasses).
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
    public void getSecretById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSecretById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret/" + secretId + "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetSecretById).
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
    public void updateSecretById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateSecretById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret/" + secretId + "?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("secret_id", secretId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("env_id", envId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateSecretById.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateSecretById).
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
    public void deleteSecretById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteSecretById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret/" + secretId + "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteSecretById).
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
    public void getSecretInEnvironment_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForSecretInEnvironment = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForSecretInEnvironment).
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
    public void createSecretInEnvironment_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateSecretInEnvironment = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("secret_id", secretId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("env_id", envId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateSecretById.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateSecretInEnvironment).
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
    public void getConfigMapById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigMapById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetConfigMapById).
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
    public void updateConfigMapById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateConfigMapById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("configmap_id", configMapId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("env_id", envId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateConfigMapById.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateConfigMapById).
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
    public void deleteConfigMapById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteConfigMapById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteConfigMapById).
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
    public void createConfigMapInEnvironment_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateConfigMapInEnvironment = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("env_id", envId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateConfigMapInEnvironment.mustache",
                        params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateConfigMapInEnvironment).
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
    public void getConfigMapInEnvironment_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigMapInEnvironment = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForGetConfigMapInEnvironment).
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
