package com.wso2.choreo.integration.tests.security.devOps.ComponentsAPI;

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

public class ComponentsAPIElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String volumeId;
    private static String projectId;
    private static String envId;
    private static String appEnvId;
    private static String vhost;
    private static String envName;
    private static String orgIntId;
    private static String componentId;
    private static String releaseId;
    private static String hpaId;
    private static String metricId;
    private static String containerId;
    private static String volumeMountId;
    private static String healthCheckId;
    private static String configMapId;
    private static String mountId;
    private static String imageId;
    private static String imageRegistryId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ComponentsAPIElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getConfig(ConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getConfig(ConfigDefinition.DEVOPS_PROJECT_ID);
        envId = Configuration.getConfig(ConfigDefinition.DEVOPS_ENV_ID);
        vhost = Configuration.getConfig(ConfigDefinition.DEVOPS_VHOST);
        envName = Configuration.getConfig(ConfigDefinition.DEVOPS_ENV_NAME);
        orgIntId = Configuration.getConfig(ConfigDefinition.DEVOPS_ORG_INT_ID);
        volumeId = Configuration.getConfig(ConfigDefinition.DEVOPS_VOLUME_ID);
        appEnvId = Configuration.getConfig(ConfigDefinition.DEVOPS_APP_ENV_ID);
        componentId = Configuration.getConfig(ConfigDefinition.DEVOPS_COMPONENT_ID);
        releaseId = Configuration.getConfig(ConfigDefinition.DEVOPS_RELEASE_ID);
        hpaId = Configuration.getConfig(ConfigDefinition.DEVOPS_HPA_ID);
        metricId = Configuration.getConfig(ConfigDefinition.DEVOPS_METRIC_ID);
        containerId = Configuration.getConfig(ConfigDefinition.DEVOPS_CONTAINER_ID);
        volumeMountId = Configuration.getConfig(ConfigDefinition.DEVOPS_VOLUME_MOUNT_ID);
        healthCheckId = Configuration.getConfig(ConfigDefinition.DEVOPS_HEALTH_CHECK_ID);
        configMapId = Configuration.getConfig(ConfigDefinition.DEVOPS_CONFIG_MAP_ID);
        mountId = Configuration.getConfig(ConfigDefinition.DEVOPS_MOUNT_ID);
        imageId = Configuration.getConfig(ConfigDefinition.DEVOPS_IMAGE_ID);
        imageRegistryId = Configuration.getConfig(ConfigDefinition.DEVOPS_IMAGE_REGISTRY_ID);
    }

    @Test
    @CitrusTest
    public void getVolumeMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetVolume = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/volume-mount?organization_id=" + orgId + "&project_id=" + projectId;
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
    public void getSvcConfig_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSvcConfig = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/svc-config?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetSvcConfig).
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
    public void updateHpaMetric_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric/" + metricId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("metric_id", metricId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("hpa_id", hpaId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateHpaMetric.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateHpaMetric).
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
    public void deleteHpaMetric_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric/" + metricId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteHpaMetric).
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
    public void getHpaMetric_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetHpaMetric).
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
    public void createHpaMetric_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("hpa_id", hpaId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateHpaMetric.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateHpaMetric).
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
    public void updateHpa_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateHpa = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("hpa_id", hpaId);
        params.put("app_env_id", appEnvId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateHpa.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateHpa).
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
    public void getHpa_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetHpa = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetHpa).
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
    public void createHpa_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateHpa = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("app_env_id", appEnvId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateHpa.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateHpa).
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
    public void getHealthCheck_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetHealthCheck = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/health-check?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetHealthCheck).
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
    public void getDeployHistory_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDeployHistory = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/deploy-history?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetDeployHistory).
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
    public void redeployRelease_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForRedeployRelease = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/deploy-deployment?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForRedeployRelease).
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
    public void getDeployConfig_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDeployConfig = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/deploy-config?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetDeployConfig).
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
    public void createVolumeMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateVolumeMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/volume-mount?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("app_volume_id", volumeId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateVolumeMount.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateVolumeMount).
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
    public void updateVolumeMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateVolumeMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/volume-mount/" + volumeMountId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateVolumeMount.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForUpdateVolumeMount).
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
    public void deleteVolumeMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteVolumeMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/volume-mount/" + volumeMountId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteVolumeMount).
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
    public void createHealthCheck_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateHealthCheck = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/health-check?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("container_id", containerId);
        params.put("app_env_id", appEnvId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateHealthCheck.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateHealthCheck).
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
    public void updateHealthCheck_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateHealthCheck = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/health-check/" + healthCheckId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("container_id", containerId);
        params.put("app_env_id", appEnvId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateHealthCheck.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForUpdateHealthCheck).
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
    public void deleteHealthCheck_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteHealthCheck = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/health-check/" + healthCheckId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForDeleteHealthCheck).
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
    public void createConfigMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateConfigMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/config-mount?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("configmap_id", configMapId);
        params.put("app_env_id", appEnvId);
        params.put("container_id", containerId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateConfigMount.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForCreateConfigMount).
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
    public void getConfigMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/config-mount?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetConfigMount).
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
    public void updateConfigMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateConfigMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/config-mount/" + mountId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("mount_id", mountId);
        params.put("app_env_id", appEnvId);
        params.put("container_id", containerId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateConfigMount.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateConfigMount).
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
    public void deleteConfigMount_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateConfigMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/config-mount/" + mountId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                delete(requestUrlForUpdateConfigMount).
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
    public void updateContainer_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateContainer = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("image_registry_id", imageRegistryId);
        params.put("image_id", imageId);
        params.put("app_env_id", appEnvId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateContainer.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateContainer).
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
    public void updateCdpWebappShortUrl_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateCdpWebappShortUrl = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/cdp-webapp-short-url?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                put(requestUrlForUpdateCdpWebappShortUrl).
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
    public void getReleaseById_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetReleaseById = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetReleaseById).
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
    public void getComponentId_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentId = Constant.DEVOPS_COMPONENTS_API + componentId +
                "?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetComponentId).
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
    public void getByociWebhookRegistries_ComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetByociWebhookRegistries = Constant.DEVOPS_COMPONENTS_API +
                "/byoci/webhook/registries?organization_id=" + orgId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetByociWebhookRegistries).
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
