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

package com.wso2.choreo.integration.common.managedAuthentication;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wso2.choreo.integration.apis.component.Component;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.testng.Assert;

import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.external.ManagedAuth;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.configurationservice.ConfigServiceUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ReleaseIdNotFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.CallbackUrlFormats;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.ConfigGroupNames;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.ManagedAuthConfigKeys;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.DefaultKeyGenRequest;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.KeyGenRequestKeys;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.KeySetConfigKeys;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.Subdomains;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.WebAppConfig;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.WebAppRepo;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.configservice.ConfigurationGroup;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;

public class ManagedAuthenticationUtils {

    /**
     * Create Webapp.
     *
     * @param runner       Citrus test runner
     * @param citrusClients Map of Endpoints and Citrus http clients
     * @param project       ChoreoProject instance
     * @return ChoreoComponent instance
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws TokenRetrievalException 
     * @throws Exception if an error occurs during component creation
     */
    public static ChoreoComponent createWebAppComponent(TestNGCitrusSpringSupport runner, Map<Endpoints, 
            HttpClient> citrusClients, String accessToken, ChoreoProject project) throws Exception {
    
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        GraphqlDTO.ByocWebAppsConfig webAppsConfig = GraphqlDTO.ByocWebAppsConfig.builder()
                .dockerContext(WebAppRepo.DOCKER_CONTEXT)
                .srcGitRepoUrl(WebAppRepo.URL)
                .webAppType(WebAppConfig.WEB_APP_TYPE)
                .webAppBuildCommand(WebAppConfig.BUILD_COMMAND)
                .webAppPackageManagerVersion(WebAppConfig.PACKAGE_MANAGER_VERSION)
                .webAppOutputDirectory(WebAppConfig.OUTPUT_DIRECTORY)
                .build();
        GraphqlDTO dto = ComponentUtils.createWebappComponentRequest(componentName, project, webAppsConfig);
        ChoreoComponent component = ComponentUtils.createComponent(runner, citrusClients, accessToken,
                dto, ComponentFlavour.WEBAPP);
        dto.setComponentId(component.getId());
        dto.setLatestVersionId(component.getLatestApiVersion().getId());
        String runId = GraphQL.getRunId(runner, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, dto);
        Component.waitForComponentBuildDeployComplete(runner, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, project.getId(), component.getId(), runId, 50);
        Assert.assertNotNull(component);
        return component;
    }

    /**
     * Build and Deploy Webapp.
     *
     * @param runner Citrus test runner
     * @param citrusClients Map of Endpoints and Citrus http clients
     * @param accessToken Access token for authentication
     * @param project ChoreoProject instance
     * @return ComponentDeploymentStatusDTO instance
     * @throws Exception if an error occurs during build or deploy
     */
    public static ComponentDeploymentStatusDTO buildAndDeployWebAppComponent(TestNGCitrusSpringSupport runner, 
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component, 
            List<Environment> componentEnvironments) throws Exception {
    
        return ComponentUtils.deployComponent(runner, citrusClients, accessToken, component, componentEnvironments,
            ComponentFlavour.WEBAPP);
    }

    /**
     * Deploy already Built Webapp Component.
     *
     * @param runner Citrus test runner
     * @param citrusClients Map of Endpoints and Citrus http clients
     * @param accessToken Access token for authentication
     * @param component ChoreoComponent instance
     * @param componentEnvironments List of Environment instances
     * @return ComponentDeploymentStatusDTO instance
     * @throws Exception if an error occurs during deployment
     */
    public static ComponentDeploymentStatusDTO deployBuiltWebAppComponent(TestNGCitrusSpringSupport runner, 
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component, 
            List<Environment> componentEnvironments) throws Exception {
    
            Commit latestCommit = ComponentUtils.getLatestCommit(runner, citrusClients, accessToken, component);
            return ComponentUtils.deployBuiltComponent(runner, citrusClients, accessToken, component, latestCommit, 
                componentEnvironments);
    }

    /**
     * Generate Keyset.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param component ChoreoComponent instance
     * @param environment Environment instance
     * @return KeyGenResponseDTO instance
     * @throws ReleaseIdNotFoundException if release ID is not found
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static KeyGenResponseDTO GenerateKeyset(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent component, Environment environment) throws ReleaseIdNotFoundException, 
            TokenRetrievalException, IOException, URISyntaxException {
    
        String releaseId = component.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        return ComponentUtils.generateKeys(runner, appServiceClient, component.getProjectId(), component.getId(), 
            environment.getId(), getTestKeygenRequest(releaseId));
    }

    /**
     * Get Keyset Configuration group.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param component ChoreoComponent instance
     * @return ConfigurationGroup instance
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static ConfigurationGroup getKeySetConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA) throws TokenRetrievalException, IOException, URISyntaxException {
    
        return getConfigGroupByName(runner, appServiceClient, componentA, ConfigGroupNames.KEY_SET_CONFIG_GROUP_NAME);
    }

    /**
     * Validate Keyset Configuration has been updated by checking for non-blank client ID.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param environment Environment instance
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void validateKeySetConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment environment) throws TokenRetrievalException, IOException, 
            URISyntaxException {

        ConfigurationGroup keySetConfigs = getKeySetConfig(runner, appServiceClient, componentA);

        Assert.assertFalse(keySetConfigs.getConfigurationValue(KeySetConfigKeys.CLIENT_ID, 
            environment.getTemplateId()).orElse("").isBlank());
    }

    /**
     * Get Managed Authentication Configuration group.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @return ConfigurationGroup instance
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static ConfigurationGroup getManagedAuthConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient,
            ChoreoComponent componentA) throws TokenRetrievalException, IOException, URISyntaxException {

        return getConfigGroupByName(runner, appServiceClient, componentA, 
            ConfigGroupNames.MANAGED_AUTH_CONFIG_GROUP_NAME);
    }

    /**
     * Set Managed Authentication Configuration values.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param environment Environment instance
     * @param configs Map of config values to be set
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void setManagedAuthConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment environment, Map<String, String> configs) 
            throws TokenRetrievalException, IOException, URISyntaxException {

        ConfigurationGroup managedAuthConfigGroup = getManagedAuthConfig(runner, appServiceClient, componentA);

        configs.forEach((key, value) -> {
            managedAuthConfigGroup.setConfigurationValue(key, value, environment.getTemplateId());
        });

        ConfigServiceUtils.updateConfigGroup(runner, appServiceClient, managedAuthConfigGroup);
    }

    /**
     * Validate Managed Authentication Configuration contaiins the expected values.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param environment Environment instance
     * @param expectedConfigs Map of expected config values
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void validateManagedAuthConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment environment, Map<String, String> expectedConfigs) 
            throws TokenRetrievalException, IOException, URISyntaxException {

        ConfigurationGroup managedAuthConfigs = getManagedAuthConfig(runner, appServiceClient, componentA);

        expectedConfigs.forEach((key, value) -> {
            Assert.assertEquals(managedAuthConfigs.getConfigurationValue(key, environment.getTemplateId()).get(), value);
        });
    }

    /**
     * Set Managed Authentication status.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param environment Environment instance
     * @param status Boolean value to set the status
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void setManagedAuthStatus(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment environment, Boolean managedAuthStatus) 
            throws TokenRetrievalException, IOException, URISyntaxException {

        setManagedAuthConfig(runner, appServiceClient, componentA, environment, 
            Map.of(ManagedAuthConfigKeys.IS_APP_GATEWAY_CONFIGURED, managedAuthStatus.toString()));
    }

    /**
     * Validate Managed Authentication status.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param environment Environment instance
     * @param expectedStatus Expected status
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void validateManagedAuthStatus(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment environment, Boolean expectedStatus) throws 
            TokenRetrievalException, IOException, URISyntaxException {

        validateManagedAuthConfig(runner, appServiceClient, componentA, environment, 
            Map.of(ManagedAuthConfigKeys.IS_APP_GATEWAY_CONFIGURED, expectedStatus.toString()));
    }

    /**
     * Validate Managed Authentication Configuration propagation to the Dataplane.
     *
     * @param webAppBaseUrl Base URL of the webapp
     * @throws ClientProtocolException if an error occurs while executing the HTTP request
     * @throws IOException if an error occurs while reading the response
     */
    public static void validateConfigPropagation(String webAppBaseUrl) throws ClientProtocolException, IOException {
        String location = ManagedAuth.initiateManagedAuthLoginFlow(webAppBaseUrl, new HashMap<>());
        List<NameValuePair> params = URLEncodedUtils.parse(location, Charset.forName("UTF-8"));
        String redirectUrl = params.stream().filter(param -> param.getName().equals("redirect_uri")).findFirst().get().getValue();

        Assert.assertEquals(webAppBaseUrl + "/auth/login/callback", redirectUrl);
    }

    private static ConfigurationGroup getConfigGroupByName(TestNGCitrusSpringSupport runner, 
            HttpClient appServiceClient, ChoreoComponent componentA, String groupName) throws TokenRetrievalException, 
            IOException, URISyntaxException {

        List<ConfigurationGroup> configGroupsList = ConfigServiceUtils.getConfigGroupsInComponent(runner, 
            appServiceClient, componentA.getProjectId(), componentA.getId());

        String configGroupUuid = configGroupsList.stream()
                .filter(group -> group.getGroupName().equals(groupName))
                .findFirst()
                .get()
                .getGroupUuid();

        return ConfigServiceUtils.getConfigGroupsWithValues(runner, appServiceClient, configGroupUuid);
    }

    private static HashMap<String, Object> getTestKeygenRequest(String releaseId) {

        String webAppDomain =  Configuration.getConfig(ConfigDefinition.CHOREO_US_DP_URL)
            .replace(Subdomains.CHOREO_APIS, Subdomains.CHOREO_APPS);

        return new HashMap<>() {
            {
                put(KeyGenRequestKeys.APP_TOKEN_EXPIRY, DefaultKeyGenRequest.APP_TOKEN_EXPIRY);
                put(KeyGenRequestKeys.CALLBACK_URLS, List.of(
                    String.format(CallbackUrlFormats.LOGIN_CALLBACK_URL, releaseId, webAppDomain),
                    String.format(CallbackUrlFormats.LOGOUT_CALLBACK_URL, releaseId, webAppDomain)
                ));
                put(KeyGenRequestKeys.GRANT_TYPES, DefaultKeyGenRequest.GRANT_TYPES);
                put(KeyGenRequestKeys.PKCE_MANDATORY, DefaultKeyGenRequest.PKCE_MANDATORY);
                put(KeyGenRequestKeys.PUBLIC_CLIENT, DefaultKeyGenRequest.PUBLIC_CLIENT);
                put(KeyGenRequestKeys.REFRESH_TOKEN_EXPIRY, DefaultKeyGenRequest.REFRESH_TOKEN_EXPIRY);
                put(KeyGenRequestKeys.USER_TOKEN_EXPIRY, DefaultKeyGenRequest.USER_TOKEN_EXPIRY);
            }
        };
    }   
}
