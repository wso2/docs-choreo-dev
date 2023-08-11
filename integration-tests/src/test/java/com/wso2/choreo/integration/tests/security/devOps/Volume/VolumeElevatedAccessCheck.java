package com.wso2.choreo.integration.tests.security.devOps.Volume;

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
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class VolumeElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String volumeId;
    private static String projectId;
    private static String envId;
    private static String appEnvId;
    private static String vhost;
    private static String envName;
    private static String orgIntId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_VolumeElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_ID);
        vhost = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_VHOST);
        envName = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_NAME);
        orgIntId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_INT_ID);
        volumeId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_VOLUME_ID);
        appEnvId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_APP_ENV_ID);
    }

    @Test
    @CitrusTest
    public void getVolume_VolumeElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetVolume = Constant.DEVOPS_VOLUME + volumeId + "?organization_id="
                + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetVolume).
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
    public void deleteVolume_VolumeElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteVolume = Constant.DEVOPS_VOLUME + volumeId + "?organization_id="
                + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteVolume).
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
    public void createVolume_VolumeElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateVolume = Constant.DEVOPS_VOLUME + "/?organization_id="
                + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("app_env_id", appEnvId);
        params.put("env_id", envId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateVolume.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateVolume).
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
    public void listVolumes_VolumeElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForListVolumes = Constant.DEVOPS_VOLUME + "/?organization_id="
                + orgId + "&project_id=" + projectId + "&environment_id=" + envId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForListVolumes).
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
