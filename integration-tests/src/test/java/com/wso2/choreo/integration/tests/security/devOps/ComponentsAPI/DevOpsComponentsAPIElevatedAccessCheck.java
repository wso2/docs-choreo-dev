package com.wso2.choreo.integration.tests.security.devOps.ComponentsAPI;

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

public class DevOpsComponentsAPIElevatedAccessCheck extends TestNGCitrusSpringSupport {
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
    public void setup_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_ID);
        vhost = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_VHOST);
        envName = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_NAME);
        orgIntId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_INT_ID);
        volumeId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_VOLUME_ID);
        appEnvId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_APP_ENV_ID);
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_COMPONENT_ID);
        releaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_RELEASE_ID);
        hpaId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_HPA_ID);
        metricId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_METRIC_ID);
        containerId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_CONTAINER_ID);
        volumeMountId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_VOLUME_MOUNT_ID);
        healthCheckId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_HEALTH_CHECK_ID);
        configMapId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_CONFIG_MAP_ID);
        mountId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_MOUNT_ID);
        imageId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_IMAGE_ID);
        imageRegistryId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_IMAGE_REGISTRY_ID);
    }

    @Test
    @CitrusTest
    public void getVolumeMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetVolume = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/volume-mount?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetVolume,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getSvcConfig_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSvcConfig = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/svc-config?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetSvcConfig,
                accessToken);
    }

    @Test
    @CitrusTest
    public void updateHpaMetric_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForUpdateHpaMetric,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteHpaMetric_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric/" + metricId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrlForDeleteHpaMetric,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getHpaMetric_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric?organization_id=" + orgId +
                "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetHpaMetric,
                accessToken);
    }

    @Test
    @CitrusTest
    public void createHpaMetric_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateHpaMetric = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa/" + hpaId + "/metric?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        params.put("hpa_id", hpaId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateHpaMetric.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForCreateHpaMetric,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateHpa_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateHpa, body,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getHpa_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetHpa = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetHpa,
                accessToken);
    }

    @Test
    @CitrusTest
    public void createHpa_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateHpa = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/hpa?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("app_env_id", appEnvId);
        params.put("organization_id", orgId);
        params.put("project_id", projectId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateHpa.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForCreateHpa, body,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getHealthCheck_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetHealthCheck = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/health-check?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetHealthCheck,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getDeployHistory_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDeployHistory = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/deploy-history?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetDeployHistory,
                accessToken);
    }

    @Test
    @CitrusTest
    public void redeployRelease_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForRedeployRelease = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/deploy-deployment?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForRedeployRelease,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void getDeployConfig_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDeployConfig = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/"
                + releaseId + "/deploy-config?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetDeployConfig,
                accessToken);
    }

    @Test
    @CitrusTest
    public void createVolumeMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForCreateVolumeMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/volume-mount?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("app_volume_id", volumeId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreateVolumeMount.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForCreateVolumeMount,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateVolumeMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateVolumeMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/volume-mount/" + volumeMountId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForUpdateVolumeMount.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateVolumeMount,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteVolumeMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteVolumeMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/volume-mount/" + volumeMountId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForDeleteVolumeMount, accessToken);
    }

    @Test
    @CitrusTest
    public void createHealthCheck_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForCreateHealthCheck,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateHealthCheck_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateHealthCheck,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteHealthCheck_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteHealthCheck = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/health-check/" + healthCheckId + "?organization_id=" +
                orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForDeleteHealthCheck, accessToken);
    }

    @Test
    @CitrusTest
    public void createConfigMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForCreateConfigMount,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/config-mount?organization_id=" + orgId +
                "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetConfigMount,
                accessToken);
    }

    @Test
    @CitrusTest
    public void updateConfigMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateConfigMount,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteConfigMount_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateConfigMount = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/container/" + containerId + "/config-mount/" + mountId + "?organization_id=" + orgId +
                "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForUpdateConfigMount, accessToken);
    }

    @Test
    @CitrusTest
    public void updateContainer_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateContainer,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateCdpWebappShortUrl_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateCdpWebappShortUrl = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "/cdp-webapp-short-url?organization_id=" + orgId + "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForPostValidateVhost.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateCdpWebappShortUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getReleaseById_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetReleaseById = Constant.DEVOPS_COMPONENTS_API + componentId + "/release/" +
                releaseId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetReleaseById,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentId_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentId = Constant.DEVOPS_COMPONENTS_API + componentId +
                "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetComponentId,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getByociWebhookRegistries_DevOpsComponentsAPIElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetByociWebhookRegistries = Constant.DEVOPS_COMPONENTS_API +
                "/byoci/webhook/registries?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetByociWebhookRegistries, accessToken);
    }
}
