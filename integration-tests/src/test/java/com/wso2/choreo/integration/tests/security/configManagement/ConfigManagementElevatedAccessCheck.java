package com.wso2.choreo.integration.tests.security.configManagement;

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

import java.util.HashMap;
import java.util.Map;

public class ConfigManagementElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUuid;
    private static String projectUuid;
    private static String devEnvUuid;
    private static String devConfigUuid;
    private static String devConfigValueRef;
    private static String devConfigID;
    private static String componentId;
    private static String componentVersion;
    private static String componentName;
    private static String commitHash;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ConfigManagementElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        projectUuid = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_PROJECT_UUID);
        devEnvUuid = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_DEV_ENV_UUID);
        devConfigUuid = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_DEV_CONF_UUID);
        devConfigValueRef = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_DEV_CONF_VALUE_REF);
        devConfigID = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_DEV_CONF_ID);
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_COMP_ID);
        componentVersion = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_COMP_VERSION);
        componentName = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_COMP_NAME);
        commitHash = Configuration.getSecurityConfig(SecurityConfigDefinition.CONF_MGT_COMMIT_HASH);
    }

    @Test
    @CitrusTest
    public void listGlobalConfigs_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/globalconfigurations";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void createGlobalConfig_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/globalconfigurations";
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        params.put("PROJECT_UUID", projectUuid);
        params.put("DEV_ENV_UUID", devEnvUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/configManagement/" +
                "createGlobalConfig.mustache", params);

        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateGlobalConfig_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/globalconfigurations/" +
                devConfigUuid;
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        params.put("PROJECT_UUID", projectUuid);
        params.put("DevConfigUUID", devConfigUuid);
        params.put("DEV_ENV_UUID", devEnvUuid);
        params.put("DevConfigValueRef", devConfigValueRef);
        params.put("DevConfigID", devConfigID);
        String body = MessageUtils.generateStringFromTemplate("templates/configManagement/" +
                "updateGlobalConfig.mustache", params);
        SecurityUtils.elevatedAccessCheckForPatchRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getParticularGlobalConfigData_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid +
                "/globalconfigurations/environments/" + devEnvUuid + "/configdata?configUuid=" + devConfigUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteGlobalConfig_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid +
                "/globalconfigurations/" + devConfigUuid + "/environment/" + devEnvUuid;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getAllGlobalConfigData_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid +
                "/globalconfigurations/environments/" + devEnvUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfiguration_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid +
                "/components/" + componentId + "/envs/" + devEnvUuid + "/" + componentVersion +
                "/configurations?component_name=" + componentName + "&commit_hash=" + commitHash;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void createConfiguration_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/components/" +
                componentId + "/envs/" + devEnvUuid + "/" + componentVersion +"/configurations";
        Map<String, String> params = new HashMap<>();
        params.put("COMP_NAME", componentName);
        params.put("COMMIT_HASH", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/configManagement/" +
                "createConfiguration.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateConfiguration_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/components/" +
                componentId + "/envs/" + devEnvUuid + "/" + componentVersion +"/configurations";
        Map<String, String> params = new HashMap<>();
        params.put("COMMIT_HASH", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/configManagement/" +
                "updateConfiguration.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteConfiguration_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/components/" +
                componentId + "/envs/" + devEnvUuid + "/" + componentVersion +"/configurations";
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteAllConfigurations_ConfigManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrl = Constant.CONF_MGT_SUFFIX + orgUuid + "/projects/" + projectUuid + "/components/" +
                componentId + "/configurations";
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }
}
