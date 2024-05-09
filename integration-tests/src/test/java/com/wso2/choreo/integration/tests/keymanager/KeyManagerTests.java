/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 * 
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.tests.keymanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.configservice.ConfigServiceUtils;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.keymanager.KeyManagerUtils;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.AppGwKeysetConfigNames;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.DEFAULT_CHOREO_ENVIRONMENTS;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.DefaultConfigGroups;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.TestProjectData;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.configservice.ConfigGroup;
import com.wso2.choreo.integration.models.configservice.Configuration;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import com.wso2.choreo.integration.models.keymanager.ConfigUpdateResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class KeyManagerTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ChoreoProject testProject;
    private ChoreoComponent testComponent;
    private ComponentDeploymentStatusDTO testComponentDeploymentStatus;
    private Environment devEnvironment;
    private Environment prodEnvironment;
    private InvokeInformation webappInvokeInfo;
    private KeyGenResponseDTO generatedKeys;
    private KeyManager externalKeyManager;
    private String devEnvClientId;

    // Setup a project for testing
    @Test
    @CitrusTest
    public void createTestProject_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String projectHandler = NameGenerator.generateThreadUniqueName();
        String projectName = NameGenerator.generateUniqueName(Constant.TEST_PROJECT_NAME_PREFIX);
        testProject = GraphQL.createProject(this, appServiceClient, TestProjectData.REGION, userAccessToken,
                projectName, projectHandler);
        Assert.assertNotNull(testProject.getId());
    }

    @Test
    @CitrusTest
    public void createTestComponent_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        // Create a webapp component

        // Get the environment data
        getEnvironmentData();
    }

    @Test
    @CitrusTest
    public void generateKeysetsInComponent_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        // Keys will be generated for the dev environment as the component is deployed
        // in the same environment.
        KeyGenResponseDTO keyGenResponse = KeyManagerUtils.generateKeys(this, appServiceClient,
                testComponent.getProjectId(), testComponent.getId(), devEnvironment.getId(),
                getWebAppInvokeInfo().getInvokeUrl());

        Assert.assertNotNull(keyGenResponse.getClientId());
        Assert.assertNotNull(keyGenResponse.getClientSecret());

        generatedKeys = keyGenResponse;
    }

    @Test
    @CitrusTest
    public void regenerateKeysetsInComponent_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        KeyGenResponseDTO regeneratedKeys = KeyManagerUtils.regenerateKeys(this, appServiceClient,
                testComponent.getProjectId(), testComponent.getId(), devEnvironment.getId(),
                generatedKeys.getClientId());

        Assert.assertNotNull(regeneratedKeys.getClientId());
        Assert.assertNotNull(regeneratedKeys.getClientSecret());

        generatedKeys = regeneratedKeys;
    }

    @Test
    @CitrusTest
    public void updateOAuthAppConfiguration_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        int modifiedAppTokenExpiry = 1000;
        int modifiedRefreshTokenExpiry = 2000;
        int modifiedUserTokenExpiry = 3000;
        boolean isPublicClient = true;

        HashMap<String, Object> updateRequest = new HashMap<>() {
            {
                put("callbackUrls", List.of(getWebAppInvokeInfo().getInvokeUrl()));
                put("grantTypes", List.of("authorization_code", "refresh_token"));
                put("pkceMandatory", true);
                put("appTokenExpiry", modifiedAppTokenExpiry);
                put("publicClient", isPublicClient);
                put("refreshTokenExpiry", modifiedRefreshTokenExpiry);
                put("userTokenExpiry", modifiedUserTokenExpiry);
            }
        };

        ConfigUpdateResponseDTO updatedApp = KeyManagerUtils.updateOAuthAppConfiguration(this, appServiceClient,
                generatedKeys.getClientId(), updateRequest);

        Assert.assertNotNull(updatedApp);
        Assert.assertEquals(updatedApp.getAppTokenExpiry(), modifiedAppTokenExpiry);
        Assert.assertEquals(updatedApp.getRefreshTokenExpiry(), modifiedRefreshTokenExpiry);
        Assert.assertEquals(updatedApp.getUserTokenExpiry(), modifiedUserTokenExpiry);
        Assert.assertEquals(updatedApp.isPublicClient(), isPublicClient);
    }

    @Test
    @CitrusTest
    public void regenerateKeysAfterAppUpdate_KeyManagerTests() throws Exception {
        regenerateKeysetsInComponent_KeyManagerTests();
    }

    @Test
    @CitrusTest
    public void listIdpsInDevEnvironment_KeyManagerTests() throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        List<KeyManager> keyManagers = KeyManagerUtils.getKeyManagers(this, appServiceClient,
                devEnvironment.getId());

        assertNotNull(keyManagers);
        Assert.assertTrue(keyManagers.size() > 0);

        setExternalKeyManager(keyManagers);
    }

    @Test
    @CitrusTest
    public void mapOnlyClientIdToExternalIdp_KeyManagerTests() throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String externalIdpClientId = UUID.randomUUID().toString();

        HashMap<String, Object> externalIdpMapping = new HashMap<>() {
            {
                put("clientId", externalIdpClientId);
                put("idpId", externalKeyManager.getId());
            }
        };

        KeyManagerUtils.addExternalIdpKeys(this, appServiceClient, testComponent.getProjectId(),
                testComponent.getId(), devEnvironment.getId(), externalIdpMapping);

        String clientId = getConfigValueFromGroup(this, appServiceClient, DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_ID, devEnvironment.getId());
        String clientSecret = getConfigValueFromGroup(this, appServiceClient, DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_SECRET, devEnvironment.getId());

        Assert.assertEquals(clientId, externalIdpClientId);
        Assert.assertNull(clientSecret);
    }

    @Test
    @CitrusTest
    public void mapClientIdAndClientSecretToExternalIdp_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String externalIdpClientId = UUID.randomUUID().toString();
        String externalIdpClientSecret = UUID.randomUUID().toString();

        HashMap<String, Object> externalIdpMapping = new HashMap<>() {
            {
                put("clientId", externalIdpClientId);
                put("clientSecret", externalIdpClientSecret);
                put("idpId", externalKeyManager.getId());
            }
        };

        KeyManagerUtils.addExternalIdpKeys(this, appServiceClient, testComponent.getProjectId(),
                testComponent.getId(), devEnvironment.getId(), externalIdpMapping);

        String clientId = getConfigValueFromGroup(this, appServiceClient, DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_ID, devEnvironment.getId());
        String clientSecret = getConfigValueFromGroup(this, appServiceClient, DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_SECRET, devEnvironment.getId());

        Assert.assertEquals(clientId, externalIdpClientId);
        Assert.assertEquals(clientSecret, externalIdpClientSecret);

        devEnvClientId = clientId;
    }

    @Test
    @CitrusTest
    public void mapExistingClientIdToAnotherEnvironment_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        HashMap<String, Object> externalIdpMapping = new HashMap<>() {
            {
                put("clientId", devEnvClientId);
                put("idpId", externalKeyManager.getId());
            }
        };

        String status = KeyManagerUtils.addConflictingExternalIdpKeys(this, appServiceClient,
                testComponent.getProjectId(),
                testComponent.getId(), prodEnvironment.getId(), externalIdpMapping);

        Assert.assertEquals(status, HttpStatus.CONFLICT.getReasonPhrase());
    }

    private String getConfigValueFromGroup(TestActionRunner runner, HttpClient client, String configGroup,
            String keyName, String environmentUuid) throws TokenRetrievalException, IOException, URISyntaxException {

        List<ConfigGroup> configGroupsList = ConfigServiceUtils.getConfigGroupsInComponent(runner, client,
                testComponent.getProjectId(), testComponent.getId());

        ConfigGroup appGatewayKeysetGroup = configGroupsList.stream()
                .filter(group -> group.getGroupName().equals(configGroup))
                .findFirst().orElse(null);

        ConfigGroup keysetGroupWithValues = ConfigServiceUtils.getConfigGroupsWithValues(runner, client,
                appGatewayKeysetGroup.getGroupUuid());

        return readConfigValue(keysetGroupWithValues, keyName, environmentUuid);
    }

    private void getEnvironmentData() throws Exception {

        if (testComponent == null) {
            throw new Exception("Component is not created yet");
        }

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getEnvironments(this, citrusClients, accessToken,
                testComponent);
        Environment developmentEnvironment = environments.stream()
                .filter(env -> env.getChoreoEnv().equals(DEFAULT_CHOREO_ENVIRONMENTS.DEV)).findFirst()
                .orElse(null);
        Environment productionEnvironment = environments.stream()
                .filter(env -> env.getChoreoEnv().equals(DEFAULT_CHOREO_ENVIRONMENTS.PROD)).findFirst()
                .orElse(null);

        devEnvironment = developmentEnvironment;
        prodEnvironment = productionEnvironment;
    }

    private InvokeInformation getWebAppInvokeInfo() throws Exception {

        if (webappInvokeInfo != null) {
            return webappInvokeInfo;
        }

        if (testComponent == null || devEnvironment == null) {
            throw new Exception("Component or environment is not created yet");
        }

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        InvokeInformation invokeInfo = testComponent.getInvokeInformation(accessToken, testComponent.getComponentType(),
                devEnvironment.getId());

        webappInvokeInfo = invokeInfo;

        return invokeInfo;
    }

    private void setExternalKeyManager(List<KeyManager> keyManagersList) {
        KeyManager externalKm = keyManagersList.stream().filter(keyManager -> {
            return keyManager.getType().equals(KeyManagerConstants.ASGARDEO_KM_TYPE)
                    && !keyManager.getName().startsWith(KeyManagerConstants.DEFAULT_KM_NAME_PREFIX);
        }).findFirst().orElse(null);

        if (externalKeyManager == null) {
            throw new RuntimeException("External Key Manager not found");
        }
        externalKeyManager = externalKm;
    }

    private static String readConfigValue(ConfigGroup configGroup, String keyName, String environmentUuid) {
        Configuration configuration = configGroup.getConfigurations().stream()
                .filter(config -> config.getKey().equals(keyName))
                .findFirst().orElse(null);

        return configuration.getValues().stream()
                .filter(value -> value.getEnvironmentUuid().equals(environmentUuid))
                .findFirst().orElse(null).getValue();
    }
}
