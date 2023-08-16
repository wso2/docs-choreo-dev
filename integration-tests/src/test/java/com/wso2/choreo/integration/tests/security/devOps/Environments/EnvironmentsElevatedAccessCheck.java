package com.wso2.choreo.integration.tests.security.devOps.Environments;

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
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ENV_ID);
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        secretId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_SECRET_ID);
        appEnvId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_APP_ENV_ID);
        configMapId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_CONFIG_MAP_ID);
    }

    @Test
    @CitrusTest
    public void getStorageClasses_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetStorageClasses = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/storage-classes?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetStorageClasses,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getSecretById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetSecretById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret/" + secretId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetSecretById,
                accessToken);
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateSecretById,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteSecretById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteSecretById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret/" + secretId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForDeleteSecretById, accessToken);
    }

    @Test
    @CitrusTest
    public void getSecretInEnvironment_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForSecretInEnvironment = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/secret?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForSecretInEnvironment,
                accessToken);
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
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForCreateSecretInEnvironment, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigMapById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigMapById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetConfigMapById,
                accessToken);
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
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient, requestUrlForUpdateConfigMapById,
                body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteConfigMapById_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteConfigMapById = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForDeleteConfigMapById, accessToken);
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
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForCreateConfigMapInEnvironment, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigMapInEnvironment_EnvironmentsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigMapInEnvironment = Constant.DEVOPS_ENVIRONMENTS +
                envId + "/configmap/" + configMapId + "?organization_id=" + orgId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetConfigMapInEnvironment, accessToken);
    }
}
