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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.configurationservice.ConfigServiceUtils;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.keymanager.KeyManagerUtils;
import com.wso2.choreo.integration.common.oauth.OAuthConstants;
import com.wso2.choreo.integration.common.oauth.OAuthUtils;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.AppGwKeysetConfigNames;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.ClientCredentialsAuthFlowParams;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.DefaultChoreoEnvironments;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.DefaultConfigGroups;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.ExternalIdpMappingParams;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.KeyGenerationRequestParams;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.ModifiedOAuthAppConfig;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.TestIdpDefaults;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.TestKeyGenRequestData;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.TestProjectData;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.configservice.ConfigurationGroup;
import com.wso2.choreo.integration.models.devops.Dataplane;
import com.wso2.choreo.integration.models.devops.EnvironmentWithClusters;
import com.wso2.choreo.integration.models.configservice.Configuration;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.keymanager.OAuthAppUpdateResponseDTO;
import com.wso2.choreo.integration.models.oauth.ClientCredentialsResponseDTO;
import com.wso2.choreo.integration.models.keymanager.DetailedKeyManager;
import com.wso2.choreo.integration.models.keymanager.IdpAddRequestDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KMCertificate;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tests for Key Manager related operations.
 */
public class KeyManagerTests extends TestNGCitrusSpringSupport {

    private static final Logger log = LogManager.getLogger(KeyManagerTests.class);

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private static final String WEBAPP_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/choreo-samples";
    private static final String WEBAPP_COMPONENT_DOCKER_CONTEXT = "react-single-page-app/";

    private ChoreoProject testProject;
    private ChoreoComponent testComponent;
    private Environment devEnvironment;
    private Environment prodEnvironment;
    private KeyGenResponseDTO generatedKeys;
    private DetailedKeyManager externalKeyManager;
    private String devEnvClientId;

    @BeforeClass
    public void setUp() throws URISyntaxException, TokenRetrievalException, IOException {

        // Setup - Add an external IdP if not already added

        List<KeyManager> keyManagers = KeyManagerUtils.getKeyManagersListAsAdmin();

        Optional<KeyManager> testKeyManager = keyManagers.stream()
                .filter(keyManager -> keyManager.getName().equals(KeyManagerConstants.TestIdpDefaults.NAME))
                .findFirst();

        if (testKeyManager.isEmpty()) {
            IdpAddResponseDTO response = KeyManagerUtils.addExternalIdp(getExternalIdpPayload());
            if (response != null) {
                log.info("External IdP added successfully");
            }
        }
    }

    // Setup a project for testing Key Manager related operations
    @Test
    @CitrusTest
    public void createTestProject_KeyManagerTests() throws Exception {

        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        testProject = ComponentUtils.createProject(this, citrusClients, userAccessToken, TestProjectData.REGION);

        Assert.assertNotNull(testProject.getId());
    }

    // Create a webapp component in the test project
    @Test(dependsOnMethods = { "createTestProject_KeyManagerTests" })
    @CitrusTest
    public void createTestComponent_KeyManagerTests() throws Exception {

        String userAccessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        GraphqlDTO.ByocWebAppsConfig webAppsConfig = GraphqlDTO.ByocWebAppsConfig.builder()
                .dockerContext(WEBAPP_COMPONENT_DOCKER_CONTEXT)
                .srcGitRepoUrl(WEBAPP_COMPONENT_REPO_URL)
                .webAppType("React")
                .webAppBuildCommand("npm run build")
                .webAppPackageManagerVersion("18")
                .webAppOutputDirectory("/build")
                .build();
        GraphqlDTO componentCreationRequestDTO = ComponentUtils.createWebappComponentRequest(componentName, testProject,
                webAppsConfig);
        ChoreoComponent webappComponent = ComponentUtils.createComponent(this, citrusClients, userAccessToken,
                componentCreationRequestDTO, ComponentFlavour.WEBAPP);

        Assert.assertNotNull(webappComponent.getId());
        testComponent = webappComponent;

        setEnvironmentData();
    }

    // Test 1 - Generate Keys with Choreo Built-In Identity Provider

    // Test 1.1 - Generate Keysets in the component
    @Test(dependsOnMethods = { "createTestComponent_KeyManagerTests" })
    @CitrusTest
    public void generateKeysetsInComponent_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        KeyGenResponseDTO keyGenResponse = ComponentUtils.generateKeys(this, appServiceClient,
                testComponent.getProjectId(), testComponent.getId(), devEnvironment.getId(),
                getTestKeygenRequest());

        String clientId = getConfigValueFromGroup(this, appServiceClient,
                DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_ID, devEnvironment.getTemplateId());

        Assert.assertNotNull(clientId);

        generatedKeys = keyGenResponse;
    }

    // Test 1.2 - Update OAuth App configuration
    @Test(dependsOnMethods = { "generateKeysetsInComponent_KeyManagerTests" })
    @CitrusTest
    public void updateOAuthAppConfiguration_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        OAuthAppUpdateResponseDTO updatedApp = KeyManagerUtils.updateOAuthAppConfiguration(this, appServiceClient,
                generatedKeys.getClientId(), getKeyManagerUpdateTestRequest());

        Assert.assertNotNull(updatedApp);
        Assert.assertEquals(updatedApp.getAppTokenExpiry(), ModifiedOAuthAppConfig.APP_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.getRefreshTokenExpiry(),
                ModifiedOAuthAppConfig.REFRESH_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.getUserTokenExpiry(),
                ModifiedOAuthAppConfig.USER_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.isPublicClient(), ModifiedOAuthAppConfig.IS_PUBLIC_CLIENT);
    }

    // Test 1.3 - Regenerate keysets in the component
    @Test(dependsOnMethods = { "updateOAuthAppConfiguration_KeyManagerTests" })
    @CitrusTest
    public void regenerateKeysetsInComponent_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        KeyGenResponseDTO regeneratedKeys = ComponentUtils.regenerateKeys(this,
                appServiceClient,
                testComponent.getProjectId(), testComponent.getId(), devEnvironment.getId(),
                generatedKeys.getClientId());

        String clientId = getConfigValueFromGroup(this, appServiceClient,
                DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_ID, devEnvironment.getTemplateId());

        Assert.assertNotNull(clientId);

        generatedKeys = regeneratedKeys;
    }

    // Test 1.4 - Invoke Client Credentials Auth Flow and get an access token
    @Test(dependsOnMethods = { "regenerateKeysetsInComponent_KeyManagerTests" })
    @CitrusTest
    public void invokeClientCredentialsAuthFlowAndGetAccessToken_KeyManagerTests() throws Exception {

        String tokenEndpointURL = getTokenEndpointURL(devEnvironment.getChoreoEnv(),
                getClusterDomain(this, devEnvironment));

        HashMap<String, Object> oAuthClientCredentialsRequest = new HashMap<>() {
            {
                put(ClientCredentialsAuthFlowParams.GRANT_TYPE, OAuthConstants.CLIENT_CREDENTIALS_GRANT_TYPE);
                put(ClientCredentialsAuthFlowParams.SCOPE, OAuthConstants.DEFAULT_CLIENT_CREDENTIALS_SCOPES);
            }
        };

        Thread.sleep(2 * 60 * 1000);  // wait for cache invalidation

        ClientCredentialsResponseDTO clientCredentialsResponse = OAuthUtils.invokeClientCredentialsAuthFlow(this,
                tokenEndpointURL, generatedKeys.getClientId(), generatedKeys.getClientSecret(),
                oAuthClientCredentialsRequest);

        Assert.assertNotNull(clientCredentialsResponse.getAccess_token());
    }

    // Test 2 - Map keys from third party IDP

    // Test 2.1 - List IDPs in the development environment
    @Test(dependsOnMethods = { "invokeClientCredentialsAuthFlowAndGetAccessToken_KeyManagerTests" })
    @CitrusTest
    public void listIdpsInDevEnvironment_KeyManagerTests() throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.STS_ENDPOINT);

        List<DetailedKeyManager> keyManagers = KeyManagerUtils.getKeyManagersAsPublisher(this,
                appServiceClient,
                devEnvironment.getId());

        Assert.assertNotNull(keyManagers);
        Assert.assertTrue(keyManagers.size() > 0);

        setExternalKeyManager(keyManagers);
    }

    // Test 2.2 - Map only client ID to external IDP
    @Test(dependsOnMethods = { "listIdpsInDevEnvironment_KeyManagerTests" })
    @CitrusTest
    public void mapOnlyClientIdToExternalIdp_KeyManagerTests() throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String externalIdpClientId = UUID.randomUUID().toString();

        HashMap<String, Object> externalIdpMapping = new HashMap<>() {
            {
                put(ExternalIdpMappingParams.CLIENT_ID, externalIdpClientId);
                put(ExternalIdpMappingParams.IDP_ID, externalKeyManager.getId());
            }
        };

        ComponentUtils.addExternalIdpKeys(this, appServiceClient,
                testComponent.getProjectId(),
                testComponent.getId(), devEnvironment.getId(), externalIdpMapping, HttpStatus.CREATED);

        String clientId = getConfigValueFromGroup(this, appServiceClient,
                DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_ID, devEnvironment.getTemplateId());

        Assert.assertEquals(clientId, externalIdpClientId);
    }

    // Test 2.3 - Map client ID and client secret to external IDP
    @Test(dependsOnMethods = { "mapOnlyClientIdToExternalIdp_KeyManagerTests" })
    @CitrusTest
    public void mapClientIdAndClientSecretToExternalIdp_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String externalIdpClientId = UUID.randomUUID().toString();
        String externalIdpClientSecret = UUID.randomUUID().toString();

        HashMap<String, Object> externalIdpMapping = new HashMap<>() {
            {
                put(ExternalIdpMappingParams.CLIENT_ID, externalIdpClientId);
                put(ExternalIdpMappingParams.CLIENT_SECRET, externalIdpClientSecret);
                put(ExternalIdpMappingParams.IDP_ID, externalKeyManager.getId());
            }
        };

        ComponentUtils.addExternalIdpKeys(this, appServiceClient,
                testComponent.getProjectId(),
                testComponent.getId(), devEnvironment.getId(), externalIdpMapping, HttpStatus.CREATED);

        String clientId = getConfigValueFromGroup(this, appServiceClient,
                DefaultConfigGroups.APP_GW_KEYSETS,
                AppGwKeysetConfigNames.CLIENT_ID, devEnvironment.getTemplateId());

        // Note: client secret will not be returned by the API.

        Assert.assertEquals(clientId, externalIdpClientId);

        devEnvClientId = clientId;
    }

    // Test 2.4 - Map existing client ID to another environment
    @Test(dependsOnMethods = { "mapClientIdAndClientSecretToExternalIdp_KeyManagerTests" })
    @CitrusTest
    public void mapExistingClientIdToAnotherEnvironment_KeyManagerTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        HashMap<String, Object> externalIdpMapping = new HashMap<>() {
            {
                put(ExternalIdpMappingParams.CLIENT_ID, devEnvClientId);
                put(ExternalIdpMappingParams.IDP_ID, externalKeyManager.getId());
            }
        };

        ComponentUtils.addExternalIdpKeys(this,
                appServiceClient,
                testComponent.getProjectId(),
                testComponent.getId(), prodEnvironment.getId(), externalIdpMapping, HttpStatus.CONFLICT);

    }

    private String getConfigValueFromGroup(TestActionRunner runner, HttpClient client, String configGroup,
            String keyName, String environmentUuid) throws TokenRetrievalException, IOException, URISyntaxException {

        List<ConfigurationGroup> configGroupsList = ConfigServiceUtils.getConfigGroupsInComponent(runner, client,
                testComponent.getProjectId(), testComponent.getId());

        ConfigurationGroup appGatewayKeysetGroup = configGroupsList.stream()
                .filter(group -> group.getGroupName().equals(configGroup))
                .findFirst().orElse(null);

        ConfigurationGroup keysetGroupWithValues = ConfigServiceUtils.getConfigGroupsWithValues(runner, client,
                appGatewayKeysetGroup.getGroupUuid());

        return readConfigValue(keysetGroupWithValues, keyName, environmentUuid);
    }

    private void setEnvironmentData() throws Exception {

        if (testComponent == null) {
            throw new Exception("Component is not created yet");
        }

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getEnvironments(this, citrusClients, accessToken,
                testComponent);
        Environment developmentEnvironment = environments.stream()
                .filter(env -> env.getChoreoEnv().equals(DefaultChoreoEnvironments.DEV)).findFirst()
                .orElse(null);
        Environment productionEnvironment = environments.stream()
                .filter(env -> env.getChoreoEnv().equals(DefaultChoreoEnvironments.PROD)).findFirst()
                .orElse(null);

        devEnvironment = developmentEnvironment;
        prodEnvironment = productionEnvironment;
    }

    private void setExternalKeyManager(List<DetailedKeyManager> keyManagersList) {

        DetailedKeyManager externalKm = keyManagersList.stream().filter(keyManager -> {
            return keyManager.getName().equals(TestIdpDefaults.NAME);
        }).findFirst().orElse(null);

        if (externalKm == null) {
            throw new RuntimeException("External Key Manager not found");
        }
        externalKeyManager = externalKm;
    }

    private static String readConfigValue(ConfigurationGroup configGroup, String keyName, String environmentUuid) {
        Configuration configuration = configGroup.getConfigurations().stream()
                .filter(config -> config.getKey().equals(keyName))
                .findFirst().orElse(null);

        return configuration.getValues().stream()
                .filter(value -> value.getEnvironmentUuid().equals(environmentUuid))
                .findFirst().orElse(null).getValue();
    }

    private static IdpAddRequestDTO getExternalIdpPayload()
            throws TokenRetrievalException, IOException, URISyntaxException {

        KMCertificate certificate = KMCertificate.builder().type(TestIdpDefaults.CERT_TYPE)
                .value(TestIdpDefaults.JWKS_ENDPOINT).build();

        IdpAddRequestDTO request = IdpAddRequestDTO.builder()
                .additionalProperties(null)
                .alias(TestIdpDefaults.ALIAS)
                .authorizeEndpoint(TestIdpDefaults.AUTHORIZE_ENDPOINT)
                .certificates(certificate)
                .consumerKeyClaim(TestIdpDefaults.CONSUMER_KEY_CLAIM)
                .description(TestIdpDefaults.DESCRIPTION)
                .enabled(true)
                .issuer(TestIdpDefaults.ISSUER_ENDPOINT)
                .logoutEndpoint(TestIdpDefaults.LOGOUT_ENDPOINT)
                .name(TestIdpDefaults.NAME)
                .revokeEndpoint(TestIdpDefaults.REVOKE_ENDPOINT)
                .scopesClaim(TestIdpDefaults.SCOPES_CLAIM)
                .tokenEndpoint(TestIdpDefaults.TOKEN_ENDPOINT)
                .tokenType(TestIdpDefaults.TOKEN_TYPE)
                .type(TestIdpDefaults.TYPE)
                .wellKnownEndpoint(TestIdpDefaults.WELLKNOWN_ENDPOINT)
                .build();

        return request;
    }

    private static HashMap<String, Object> getTestKeygenRequest() {
        return new HashMap<>() {
            {
                put(KeyGenerationRequestParams.APP_TOKEN_EXPIRY, TestKeyGenRequestData.APP_TOKEN_EXPIRY);
                put(KeyGenerationRequestParams.CALLBACK_URLS, TestKeyGenRequestData.CALLBACK_URLS);
                put(KeyGenerationRequestParams.GRANT_TYPES, TestKeyGenRequestData.GRANT_TYPES);
                put(KeyGenerationRequestParams.PKCE_MANDATORY, TestKeyGenRequestData.PKCE_MANDATORY);
                put(KeyGenerationRequestParams.PUBLIC_CLIENT, TestKeyGenRequestData.IS_PUBLIC_CLIENT);
                put(KeyGenerationRequestParams.REFRESH_TOKEN_EXPIRY, TestKeyGenRequestData.REFRESH_TOKEN_EXPIRY);
                put(KeyGenerationRequestParams.USER_TOKEN_EXPIRY, TestKeyGenRequestData.USER_TOKEN_EXPIRY);
            }
        };
    }

    private static HashMap<String, Object> getKeyManagerUpdateTestRequest() {
        return new HashMap<>() {
            {
                put(KeyGenerationRequestParams.CALLBACK_URLS, ModifiedOAuthAppConfig.CALLBACK_URLS);
                put(KeyGenerationRequestParams.GRANT_TYPES, ModifiedOAuthAppConfig.GRANT_TYPES);
                put(KeyGenerationRequestParams.PKCE_MANDATORY, true);
                put(KeyGenerationRequestParams.APP_TOKEN_EXPIRY, ModifiedOAuthAppConfig.APP_TOKEN_EXPIRY);
                put(KeyGenerationRequestParams.PUBLIC_CLIENT, ModifiedOAuthAppConfig.IS_PUBLIC_CLIENT);
                put(KeyGenerationRequestParams.REFRESH_TOKEN_EXPIRY, ModifiedOAuthAppConfig.REFRESH_TOKEN_EXPIRY);
                put(KeyGenerationRequestParams.USER_TOKEN_EXPIRY, ModifiedOAuthAppConfig.USER_TOKEN_EXPIRY);
            }
        };
    }

    private static String getTokenEndpointURL(String choreoEnvironment, String region) {
        return "https://" + TestContext.getTestOrg().getOrgUUID() + "-" + choreoEnvironment + "." + region + "."
                + OAuthConstants.DEFAULT_CHOREO_STS_DOMAIN + "/" + OAuthConstants.DEFAULT_TOKEN_ENDPOINT;
    }

    private String getClusterDomain(TestActionRunner runner, Environment environment)
            throws JsonMappingException, JsonProcessingException, TokenRetrievalException, IOException,
            URISyntaxException {

        List<EnvironmentWithClusters> environmentListWithClusters = DevopsPortalApi.getEnvironmentsWithClusters(runner,
                TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(),
                TestContext.getTestOrg().getOrgUUID(), testProject.getId()).getData();

        EnvironmentWithClusters environmentWithClusters = environmentListWithClusters.stream()
                .filter(env -> env.getChoreo_env().equals(environment.getChoreoEnv()))
                .findFirst().orElse(null);

        String clusterId = environmentWithClusters.getEnvironment_clusters().stream()
                .filter(cluster -> cluster.getEnvironment_id().equals(environment.getId()))
                .findFirst().orElse(null).getCluster_id();

        List<Dataplane> dataplaneList = DevopsPortalApi.getDataplaneList(runner,
                TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(),
                TestContext.getTestOrg().getOrgUUID());

        Dataplane dataplane = dataplaneList.stream()
                .filter(dp -> dp.getId().equals(clusterId.toUpperCase()))
                .findFirst().orElse(null);

        String vhostName = dataplane.getExternalGatewayVirtualHost();

        String[] domainFrags = vhostName.split("\\.");
        if (domainFrags.length >= 2) {
            domainFrags = Arrays.copyOf(domainFrags, domainFrags.length - 2);
        }

        return String.join(".", domainFrags);
    }
}
