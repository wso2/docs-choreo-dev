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

package com.wso2.choreo.integration.tests.appdevsts;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.DataCleaner;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationConstants;
import com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationUtils;
import com.wso2.choreo.integration.common.appdevUserManagement.AppdevUserManagementUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.keysetmanagement.KeysetManagementConstants;
import com.wso2.choreo.integration.common.keysetmanagement.KeysetManagementUtils;
import com.wso2.choreo.integration.common.oauth.OAuthConstants.STSEndpoints;
import com.wso2.choreo.integration.common.oauth.OAuthUtils;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.appdevAuthorization.CreateRoleResponseDTO;
import com.wso2.choreo.integration.models.appdevUserManagement.CreateUserStoreResponseDTO;
import com.wso2.choreo.integration.models.appdevUserManagement.User;
import com.wso2.choreo.integration.models.appdevUserManagement.UserStore;
import com.wso2.choreo.integration.models.devops.Dataplane;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplate;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplatesListDTO;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.keymanager.OAuthAppUpdateResponseDTO;
import com.wso2.choreo.integration.models.oauth.TokenResponseDTO;
import com.wso2.choreo.integration.models.oauth.WellKnownResponseDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.wso2.choreo.integration.common.appdevAuthorization.AppdevAuthorizationConstants.TestPermissionData.UPDATED_PERMISSION;

/**
 * This class contains test cases related to App Dev STS operations using the external consumer component.
 */
public class AppDevSTSTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private int orgId;
    private String orgHandle;
    private String orgUuid;
    private Dataplane dataplane;
    private EnvironmentTemplate environment;
    private ChoreoProject testProject;
    private ChoreoComponent testComponent;
    private KeyGenResponseDTO generatedKeys;
    private TokenResponseDTO tokenResponseDTO;
    private HttpClient stsClient;
    private User user;

    @BeforeClass
    public void setup_AppDevSTSTests() throws Exception {

        String token = System.getProperty("Token");
        if (StringUtils.isNotBlank(token)) {
            // Manual mode. Make sure the org have the new app dev sts.
            orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
            orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
            orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        } else {
            // Pipeline mode
            orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_APP_DEV_STS_ORG_UUID);
            orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_APP_DEV_STS_ORG_HANDLE);
            orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_APP_DEV_STS_ORG_ID));
            if (StringUtils.isEmpty(orgUuid) || StringUtils.isEmpty(orgHandle) || orgId == 0) {
                log.warn("App Dev STS Org is not provided. AppDevSTS tests will not be executed.");
                throw new SkipException("Skipping App Dev STS tests as the App Dev STS org is not provided.");
            }
        }
        DataCleaner.removeOldProjectData(new ChoreoOrganization(orgHandle, orgId, orgUuid));
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(orgHandle);
        List<UserStore> userStores = AppdevUserManagementUtils.getAllUserStores(accessToken, orgUuid);
        if (userStores != null && !userStores.isEmpty()) {
            log.info("Number of user stores: " + userStores.size());
            for (UserStore userStore : userStores) {
                try {
                    AppdevUserManagementUtils.deleteUserStore(accessToken, orgUuid, userStore.getUserStoreId());
                } catch (Exception e) {
                    log.error("Error occurred while deleting user store: " + userStore.getUserStoreId(), e);
                }
            }
        }
    }

    @Test
    @CitrusTest
    public void populateContext_AppDevSTSTests() throws Exception {

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(orgHandle);
        List<Dataplane> dataPlanes = DevopsPortalApi.getDataplaneList(this, accessToken, orgUuid);
        Assert.assertNotNull(dataPlanes);
        dataplane = dataPlanes.get(0);
        Assert.assertNotNull(dataplane);

        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        Assert.assertNotNull(environmentTemplates, "Environment templates list is empty");
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (StringUtils.equalsIgnoreCase(environmentTemplate.getClusterId().toString(), dataplane.getId())) {
                environment = environmentTemplate;
                break;
            }
        }
        Assert.assertNotNull(environment);
        stsClient = createStsHttpClient();
    }

    // Set up a project for testing App Dev related operations
    @Test(dependsOnMethods = {"populateContext_AppDevSTSTests"})
    @CitrusTest
    public void createTestProject_AppDevSTSTests() throws Exception {

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(orgHandle);
        testProject = ComponentUtils.createProject(this, citrusClients, accessToken,
                KeysetManagementConstants.TestProjectData.REGION, orgId, orgHandle);

        Assert.assertNotNull(testProject.getId());
    }

    // Create an external consumer component in the test project
    @Test(dependsOnMethods = { "createTestProject_AppDevSTSTests" })
    @CitrusTest
    public void createTestComponent_AppDevSTSTests() throws Exception {

        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);

        GraphqlDTO componentCreationRequestDTO = ComponentUtils.createExternalConsumerComponentRequest(componentName,
                testProject, orgId, orgHandle);

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(orgHandle);
        ChoreoComponent component = ComponentUtils.createComponent(this, citrusClients, accessToken,
                componentCreationRequestDTO, ComponentFlavour.EXTERNAL_CONSUMER);

        Assert.assertNotNull(component.getId());
        testComponent = component;
    }


    // Test 1 - Generate Keys with Choreo Built-In Identity Provider

    // Test 1.1 - Generate Keysets in the component
    @Test(dependsOnMethods = { "createTestComponent_AppDevSTSTests" })
    @CitrusTest
    public void generateKeysetsInComponent_AppDevSTSTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        KeyGenResponseDTO keyGenResponse = ComponentUtils.generateKeys(this, appServiceClient, orgHandle,
                environment.getId().toString(), testComponent.getProjectId(), testComponent.getId(),
                Constant.displayType.externalConsumer.name(), KeysetManagementUtils.getAppGenRequest());
        Assert.assertNotNull(keyGenResponse.getClientId());
        generatedKeys = keyGenResponse;
    }

    // Test 1.2 - Update OAuth App configuration
    @Test(dependsOnMethods = { "generateKeysetsInComponent_AppDevSTSTests" })
    @CitrusTest
    public void updateOAuthAppConfiguration_AppDevSTSTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(orgHandle);

        OAuthAppUpdateResponseDTO updatedApp = KeysetManagementUtils.updateOAuthAppConfiguration(this,
                appServiceClient, accessToken, orgUuid, environment.getId().toString(), generatedKeys.getClientId(),
                KeysetManagementUtils.getAppUpdateRequest());

        Assert.assertNotNull(updatedApp);
        Assert.assertEquals(updatedApp.getAppTokenExpiry(),
                KeysetManagementConstants.ModifiedOAuthAppConfig.APP_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.getRefreshTokenExpiry(),
                KeysetManagementConstants.ModifiedOAuthAppConfig.REFRESH_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.getUserTokenExpiry(),
                KeysetManagementConstants.ModifiedOAuthAppConfig.USER_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.isPublicClient(),
                KeysetManagementConstants.ModifiedOAuthAppConfig.IS_PUBLIC_CLIENT);
    }

    // Test 1.3 - Regenerate keysets in the component
    @Test(dependsOnMethods = { "updateOAuthAppConfiguration_AppDevSTSTests" })
    @CitrusTest
    public void regenerateKeysetsInComponent_AppDevSTSTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        KeyGenResponseDTO regeneratedKeys = ComponentUtils.regenerateKeys(this, appServiceClient,
                orgHandle, environment.getId().toString(), testComponent.getProjectId(), testComponent.getId(),
                Constant.displayType.externalConsumer.name(), generatedKeys.getClientId());

        Assert.assertNotNull(regeneratedKeys.getClientId());
        Assert.assertEquals(regeneratedKeys.getClientId(), generatedKeys.getClientId());
        Assert.assertNotEquals(regeneratedKeys.getClientSecret(), generatedKeys.getClientSecret());

        generatedKeys = regeneratedKeys;
    }

    // Test Runtime flows
    // Test 2.0 - Populate AppDev user mgt and authorization management.
    @Test(dependsOnMethods = { "regenerateKeysetsInComponent_AppDevSTSTests" })
    @CitrusTest
    public void prepareForRuntimeTests_AppDevSTSTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(orgHandle);

        // Creates user
        CreateUserStoreResponseDTO createdUserStore = AppdevUserManagementUtils.createUserStoreInEnvironment(
                this, appServiceClient, accessToken, orgUuid, environment.getId().toString(), "testUserStore");

        Assert.assertNotNull(createdUserStore);

        user = new User();
        user.setUsername("integrationTestUser");
        user.setPassword("yLekKbfL");
        user.setGroups(List.of("manager", "engineering"));
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john1@acme.org");

        // Create authorization config
        CreateRoleResponseDTO createdRole = AppdevAuthorizationUtils.createRoleWithPermissions(this,
                appServiceClient, accessToken, environment.getId().toString(), testProject.getId(),
                AppdevAuthorizationConstants.TestRoleData.NAME, UPDATED_PERMISSION);
        Assert.assertNotNull(createdRole);
        Assert.assertNotNull(createdRole.getId());
        AppdevAuthorizationUtils.mapGroupsToRole(this, appServiceClient, accessToken, createdRole.getId(),
                List.of(user.getGroups().get(0)));
    }

    // Test 2.2 - Invoke Well Known Endpoint
    @Test(dependsOnMethods = { "prepareForRuntimeTests_AppDevSTSTests" })
    @CitrusTest
    public void invokeWellKnownEndpoint_AppDevSTSTests() {

        WellKnownResponseDTO responseDTO = OAuthUtils.invokeWellKnownEndpoint(this, stsClient);
        Assert.assertNotNull(responseDTO);
        Assert.assertEquals(responseDTO.getIssuer(), getStsBaseUrl() + STSEndpoints.TOKEN);
        Assert.assertEquals(responseDTO.getAuthorization_endpoint(), getStsBaseUrl() + STSEndpoints.AUTHORIZE);
        Assert.assertEquals(responseDTO.getEnd_session_endpoint(), getStsBaseUrl() + STSEndpoints.LOGOUT);

    }

    // Test 2.2 - Invoke Client Credentials Auth Flow and get an access token
    @Test(dependsOnMethods = { "invokeWellKnownEndpoint_AppDevSTSTests" })
    @CitrusTest
    public void invokeClientCredentialsAuthFlowAndGetAccessToken_AppDevSTSTests() throws Exception {

        TokenResponseDTO tokenResponseDTO = OAuthUtils.invokeClientCredentialsAuthFlow(this,
                stsClient, generatedKeys.getClientId(), generatedKeys.getClientSecret());

        Assert.assertNotNull(tokenResponseDTO.getAccess_token());
        Assert.assertEquals(tokenResponseDTO.getExpires_in(), generatedKeys.getAppTokenExpiry() - 1);
        SignedJWT token = SignedJWT.parse(tokenResponseDTO.getAccess_token());
        JWTClaimsSet jwtClaimsSet = token.getJWTClaimsSet();
        Assert.assertEquals(jwtClaimsSet.getIssuer(), getStsBaseUrl() + STSEndpoints.TOKEN);
    }

    // Test 2.3 - Invoke Auth Code Flow and get an access token
    @Test(dependsOnMethods = { "invokeClientCredentialsAuthFlowAndGetAccessToken_AppDevSTSTests" })
    @CitrusTest
    public void invokeAuthCodeFlowAndGetAccessToken_AppDevSTSTests() throws Exception {

        tokenResponseDTO = OAuthUtils.testAuthCodeFlow(this, stsClient, generatedKeys.getClientId(),
                generatedKeys.getClientSecret(), generatedKeys.getCallbackUrls().get(0),
                UPDATED_PERMISSION, user.getUsername(), user.getPassword());

        validateToken(tokenResponseDTO);
    }

    // Test 2.4 - Refresh the access token
    @Test(dependsOnMethods = { "invokeAuthCodeFlowAndGetAccessToken_AppDevSTSTests" })
    @CitrusTest
    public void invokeRefreshTokenFlowAndGetAccessToken_AppDevSTSTests() throws Exception {

        TokenResponseDTO refreshedToken = OAuthUtils.invokeRefreshTokenFlow(this, stsClient,
                generatedKeys.getClientId(), generatedKeys.getClientSecret(),
                UPDATED_PERMISSION, tokenResponseDTO.getRefresh_token());

        validateToken(refreshedToken);
    }

    private void validateToken(TokenResponseDTO tokenResponseDTO) throws ParseException {

        Assert.assertNotNull(tokenResponseDTO.getAccess_token());
        Assert.assertEquals(tokenResponseDTO.getExpires_in(), generatedKeys.getUserTokenExpiry() - 1);
        Assert.assertTrue(tokenResponseDTO.getScope().contains(UPDATED_PERMISSION));
        SignedJWT token = SignedJWT.parse(tokenResponseDTO.getAccess_token());
        JWTClaimsSet jwtClaimsSet = token.getJWTClaimsSet();
        Assert.assertEquals(jwtClaimsSet.getSubject(), user.getUsername());
        Assert.assertEquals(jwtClaimsSet.getClaim("email"), user.getEmail());
        Assert.assertEquals(jwtClaimsSet.getIssuer(), getStsBaseUrl() + STSEndpoints.TOKEN);
        Assert.assertTrue(jwtClaimsSet.getClaim("scope").toString().contains(UPDATED_PERMISSION));


        SignedJWT idToken = SignedJWT.parse(tokenResponseDTO.getId_token());
        JWTClaimsSet idTokenClaimsSet = idToken.getJWTClaimsSet();
        Assert.assertEquals(idTokenClaimsSet.getSubject(), user.getUsername());
        Assert.assertEquals(idTokenClaimsSet.getClaim("email"), user.getEmail());
        Assert.assertEquals(idTokenClaimsSet.getIssuer(), getStsBaseUrl() + STSEndpoints.TOKEN);
        Assert.assertNull(idTokenClaimsSet.getClaim("scope"));
    }

    private HttpClient createStsHttpClient() {

        String stsBaseUrl = getStsBaseUrl();
        return OAuthUtils.createStsClient(stsBaseUrl);
    }

    private String getStsBaseUrl() {

        return "https://" + orgUuid + "-" + environment.getDnsPrefix() + "." + dataplane.getStsDefaultDomain();
    }
}
