package com.wso2.choreo.integration.common.managedAuthentication;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
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
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.DefaultManagedAuthConfigValues;
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
     * Generate Keyset.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param component ChoreoComponent instance
     * @param devEnvironment Environment instance
     * @return KeyGenResponseDTO instance
     * @throws ReleaseIdNotFoundException if release ID is not found
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static KeyGenResponseDTO GenerateKeyset(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent component, Environment devEnvironment) throws ReleaseIdNotFoundException, 
            TokenRetrievalException, IOException, URISyntaxException {
    
        String releaseId = component.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        return ComponentUtils.generateKeys(runner, appServiceClient, component.getProjectId(), component.getId(), 
            devEnvironment.getId(), getTestKeygenRequest(releaseId));
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
     * @param devEnvironment Environment instance
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void validateKeySetConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment devEnvironment) throws TokenRetrievalException, IOException, 
            URISyntaxException {

        ConfigurationGroup keySetConfigs = getKeySetConfig(runner, appServiceClient, componentA);

        Assert.assertFalse(keySetConfigs.getConfigurationValue(KeySetConfigKeys.CLIENT_ID, 
            devEnvironment.getTemplateId()).orElse("").isBlank());
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
     * Set default Managed Authentication Configuration values.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param devEnvironment Environment instance
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void setDefaultManagedAuthConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment devEnvironment) throws TokenRetrievalException, IOException, 
            URISyntaxException {

        ConfigurationGroup managedAuthConfigs = getManagedAuthConfig(runner, appServiceClient, componentA);

        managedAuthConfigs.setConfigurationValue(ManagedAuthConfigKeys.POST_LOGIN_PATH, 
            DefaultManagedAuthConfigValues.POST_LOGIN_PATH, devEnvironment.getTemplateId());
        managedAuthConfigs.setConfigurationValue(ManagedAuthConfigKeys.POST_LOGOUT_PATH, 
            DefaultManagedAuthConfigValues.POST_LOGOUT_PATH, devEnvironment.getTemplateId());
        managedAuthConfigs.setConfigurationValue(ManagedAuthConfigKeys.SCOPES, 
            DefaultManagedAuthConfigValues.SCOPES, devEnvironment.getTemplateId());
        managedAuthConfigs.setConfigurationValue(ManagedAuthConfigKeys.SESSION_EXPIRY_TIME, 
            DefaultManagedAuthConfigValues.SESSION_EXPIRY_TIME, devEnvironment.getTemplateId());

        ConfigServiceUtils.updateConfigGroup(runner, appServiceClient, managedAuthConfigs);
    }

    /**
     * Validate Managed Authentication Configuration contaiins the default values.
     *
     * @param runner Citrus test runner
     * @param appServiceClient Citrus http client
     * @param componentA ChoreoComponent instance
     * @param devEnvironment Environment instance
     * @throws TokenRetrievalException if an error occurs while retrieving the token
     * @throws IOException if an error occurs while reading the response
     * @throws URISyntaxException if an error occurs while creating the URI
     */
    public static void validateDefaultManagedAuthConfig(TestNGCitrusSpringSupport runner, HttpClient appServiceClient, 
            ChoreoComponent componentA, Environment devEnvironment) throws TokenRetrievalException, IOException, 
            URISyntaxException {

        ConfigurationGroup managedAuthConfigs = getManagedAuthConfig(runner, appServiceClient, componentA);

        Assert.assertEquals(managedAuthConfigs.getConfigurationValue(ManagedAuthConfigKeys.POST_LOGIN_PATH, 
            devEnvironment.getTemplateId()).get(), DefaultManagedAuthConfigValues.POST_LOGIN_PATH);
        Assert.assertEquals(managedAuthConfigs.getConfigurationValue(ManagedAuthConfigKeys.POST_LOGOUT_PATH, 
            devEnvironment.getTemplateId()).get(), DefaultManagedAuthConfigValues.POST_LOGOUT_PATH);
        Assert.assertEquals(managedAuthConfigs.getConfigurationValue(ManagedAuthConfigKeys.SCOPES, 
            devEnvironment.getTemplateId()).get(), DefaultManagedAuthConfigValues.SCOPES);
        Assert.assertEquals(managedAuthConfigs.getConfigurationValue(ManagedAuthConfigKeys.SESSION_EXPIRY_TIME, 
            devEnvironment.getTemplateId()).get(), DefaultManagedAuthConfigValues.SESSION_EXPIRY_TIME);
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
