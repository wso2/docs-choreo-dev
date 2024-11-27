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
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.keysetmanagement.KeysetManagementConstants;
import com.wso2.choreo.integration.common.keysetmanagement.KeysetManagementUtils;
import com.wso2.choreo.integration.common.oauth.OAuthUtils;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.devops.Dataplane;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplate;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplatesListDTO;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.keymanager.OAuthAppUpdateResponseDTO;
import com.wso2.choreo.integration.models.oauth.TokenResponseDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;


/**
 * This class contains test cases related to App Dev STS operations using the external consumer component.
 */
public class AppDevSTSTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private int orgId;
    private String orgHandle;
    private String orgUuid;
    private String accessToken;
    private Dataplane dataplane;
    private EnvironmentTemplate environment;
    private ChoreoProject testProject;
    private ChoreoComponent testComponent;
    private KeyGenResponseDTO generatedKeys;
    private HttpClient stsClient;

    @BeforeClass
    public void setup_AppDevSTSTests() throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }
    @Test
    @CitrusTest
    public void populateContext_AppDevSTSTests() throws Exception {


        List<Dataplane> dataplanes = DevopsPortalApi.getDataplaneList(this, accessToken, orgUuid);
        Assert.assertNotNull(dataplanes);
        dataplane = dataplanes.get(0);
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

    // Setup a project for testing App Dev related operations
    @Test(dependsOnMethods = {"populateContext_AppDevSTSTests"})
    @CitrusTest
    public void createTestProject_AppDevSTSTests() throws Exception {

        testProject = ComponentUtils.createProject(this, citrusClients, accessToken,
                KeysetManagementConstants.TestProjectData.REGION, orgId, orgHandle);

        Assert.assertNotNull(testProject.getId());
    }

    // Create a webapp component in the test project
    @Test(dependsOnMethods = { "createTestProject_AppDevSTSTests" })
    @CitrusTest
    public void createTestComponent_AppDevSTSTests() throws Exception {

        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);

        GraphqlDTO componentCreationRequestDTO = ComponentUtils.createExternalConsumerComponentRequest(componentName,
                testProject, orgId, orgHandle);

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

        KeyGenResponseDTO keyGenResponse = ComponentUtils.generateKeys(this, appServiceClient,
                testComponent.getProjectId(), testComponent.getId(), environment.getId().toString(),
                KeysetManagementUtils.getAppGenRequest(), "externalConsumer");
        Assert.assertNotNull(keyGenResponse.getClientId());
        generatedKeys = keyGenResponse;
    }

    // Test 1.2 - Update OAuth App configuration
    @Test(dependsOnMethods = { "generateKeysetsInComponent_AppDevSTSTests" })
    @CitrusTest
    public void updateOAuthAppConfiguration_AppDevSTSTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        OAuthAppUpdateResponseDTO updatedApp = KeysetManagementUtils.updateOAuthAppConfiguration(this, appServiceClient,
                orgUuid, environment.getId().toString(), generatedKeys.getClientId(),
                KeysetManagementUtils.getAppUpdateRequest());

        Assert.assertNotNull(updatedApp);
        Assert.assertEquals(updatedApp.getAppTokenExpiry(), KeysetManagementConstants.ModifiedOAuthAppConfig.APP_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.getRefreshTokenExpiry(),
                KeysetManagementConstants.ModifiedOAuthAppConfig.REFRESH_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.getUserTokenExpiry(),
                KeysetManagementConstants.ModifiedOAuthAppConfig.USER_TOKEN_EXPIRY);
        Assert.assertEquals(updatedApp.isPublicClient(), KeysetManagementConstants.ModifiedOAuthAppConfig.IS_PUBLIC_CLIENT);
    }

    // Test 1.3 - Regenerate keysets in the component
    @Test(dependsOnMethods = { "updateOAuthAppConfiguration_AppDevSTSTests" })
    @CitrusTest
    public void regenerateKeysetsInComponent_AppDevSTSTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        KeyGenResponseDTO regeneratedKeys = ComponentUtils.regenerateKeys(this,
                appServiceClient,
                testComponent.getProjectId(), testComponent.getId(), environment.getId().toString(),
                generatedKeys.getClientId(), "externalConsumer");

        Assert.assertNotNull(regeneratedKeys.getClientId());
        Assert.assertEquals(regeneratedKeys.getClientId(), generatedKeys.getClientId());
        Assert.assertNotEquals(regeneratedKeys.getClientSecret(), generatedKeys.getClientSecret());

        generatedKeys = regeneratedKeys;
    }

    // Test 2.1 - Invoke Client Credentials Auth Flow and get an access token
    @Test(dependsOnMethods = { "regenerateKeysetsInComponent_AppDevSTSTests" })
    @CitrusTest
    public void invokeClientCredentialsAuthFlowAndGetAccessToken_AppDevSTSTests() throws Exception {

        Thread.sleep(2 * 60 * 1000);  // wait for cache invalidation

        TokenResponseDTO clientCredentialsResponse = OAuthUtils.invokeClientCredentialsAuthFlow(this,
                stsClient, generatedKeys.getClientId(), generatedKeys.getClientSecret());

        Assert.assertNotNull(clientCredentialsResponse.getAccess_token());
    }


    private HttpClient createStsHttpClient() {

        String stsBaseUrl = "https://" + orgUuid + "-" + environment.getDnsPrefix() + "." + dataplane.getStsDefaultDomain();
        return OAuthUtils.createStsClient(stsBaseUrl);
    }
}
