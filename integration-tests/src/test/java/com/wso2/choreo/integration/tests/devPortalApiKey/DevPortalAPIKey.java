/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.devPortalApiKey;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.common.*;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.connections.ConnectionUtils;
import com.wso2.choreo.integration.common.devPortalApiKey.DevPortalApiKeyUtils;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.apiKey.ApiKeyGenerateRequest;
import com.wso2.choreo.integration.models.apiKey.ApiKeyResponse;
import com.wso2.choreo.integration.models.apimanager.Application;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class DevPortalAPIKey extends TestNGCitrusSpringSupport {

    private static final String SVC_COMPONENTS_REPO_URL = "https://github.com/choreo-test-apps/connection-test-loyalty-service";
    private static final String PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT = "loyalty-service-public-visibility/";
    private static final String SVC_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";
    private static final String OAS_FILE_PATH = "openapi.yaml";
    private ChoreoProject project;
    private ChoreoComponent component;
    private List<Environment> environments;
    private String endpointUrl;
    private String apiId;
    private String applicationId;
    private String environmentId;
    private String apiKey;
    private String regeneratedApiKey;
    private String apiKeyId;
    private DevPortalApiKeyUtils.KeySetType keySetType;
    private static final String RESOURCE_PATH = "/rewards";
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @AfterClass
    public void removeResources() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ApiManager.deleteDevPortalApplication(accessToken, applicationId);
    }

    @Test
    @CitrusTest
    public void createProject_TestDevPortalAPIKey() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = ComponentUtils.createProject(this, citrusClients, accessToken,
                Constant.region.US.toString());
    }


    @Test(dependsOnMethods = {"createProject_TestDevPortalAPIKey"})
    @CitrusTest
    public void createServiceComponent_TestDevPortalAPIKey() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();

        component =
                ConnectionUtils.createByocComponent(this, citrusClients, accessToken, componentName, project, repo);
    }

    @Test(dependsOnMethods = {"createServiceComponent_TestDevPortalAPIKey"})
    @CitrusTest
    public void deployServiceComponent_TestDevPortalAPIKey() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                component);
        ComponentUtils.deployAndValidateBuiltComponent(this, citrusClients, accessToken,
                component, environments);
        List<ComponentDeploymentStatusDTO> promotionStatus = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, component,
                environments, ComponentFlavour.BYOC);
        int promotionStatusListSize =  promotionStatus.size();
        ComponentDeploymentStatusDTO lastpromotionStatus = promotionStatus.get(promotionStatusListSize-1);

        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this,citrusClients,accessToken, component,
                lastpromotionStatus);

        Endpoint endpoint = endpoints.get(0);
        endpointUrl = endpoint.getPublicUrl();
        apiId = endpoint.getApimId();
        environments.forEach(env -> {
            if (env.getId().equals(endpoint.getEnvironmentId())) {
                environmentId = env.getTemplateId();
                if (env.isCritical()) {
                    keySetType = DevPortalApiKeyUtils.KeySetType.PRODUCTION;
                } else {
                    keySetType = DevPortalApiKeyUtils.KeySetType.SANDBOX;
                }
            }
        });
        DevPortalApiKeyUtils.enableAPIKeySecurityForAPI(this,citrusClients, apiId,accessToken);

    }

    @Test(dependsOnMethods = {"deployServiceComponent_TestDevPortalAPIKey"})
    @CitrusTest
    public void deployAPIKeyEnabledService_TestDevPortalAPIKey() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                component);
        ComponentUtils.deployAndValidateBuiltComponent(this, citrusClients, accessToken,
                component, environments);
        List<ComponentDeploymentStatusDTO> promotionStatus = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, component,
                environments, ComponentFlavour.BYOC);
        int promotionStatusListSize =  promotionStatus.size();
        ComponentDeploymentStatusDTO lastpromotionStatus = promotionStatus.get(promotionStatusListSize-1);
    }

    @Test(dependsOnMethods = {"deployAPIKeyEnabledService_TestDevPortalAPIKey"})
    @CitrusTest
    public void publishService_TestDevPortalAPIKey() throws TokenRetrievalException, IOException, URISyntaxException {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Response resp = ApiManager.changeLifeCycle(apiId, "Publish", accessToken);
        if (resp.getStatusCode() != HttpStatus.OK.value()) {
            throw new ValidationException("Failed to publish the API");
        }
    }

    @Test(dependsOnMethods = {"publishService_TestDevPortalAPIKey"})
    @CitrusTest
    public void createDevPortalApplication_TestDevPortalAPIKey() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Application application = ApiManager.CreateDevPortalApplication(accessToken);
        applicationId = application.getApplicationId();
    }

    @Test(dependsOnMethods = {"createDevPortalApplication_TestDevPortalAPIKey"})
    @CitrusTest
    public void createAnApiKey_TestDevPortalAPIKey() throws Exception {
        ApiKeyGenerateRequest apiKeyGenerateRequest = ApiKeyGenerateRequest.builder()
                .name(NameGenerator.generateThreadUniqueName())
                .applicationId(applicationId)
                .apiId(apiId)
                .subscriptionPlan("Unlimited")
                .environmentTemplateId(environmentId)
                .keyType(keySetType.toString())
                .build();

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String payLoad = ObjectMapperUtil.mapObjectToString(apiKeyGenerateRequest);
        ApiKeyResponse apiKeyResponse = ApiManager.createApiKeyV2(accessToken, payLoad);
        apiKey = apiKeyResponse.getValue();
        apiKeyId = apiKeyResponse.getId();
    }

    @Test(dependsOnMethods = {"createAnApiKey_TestDevPortalAPIKey"})
    @CitrusTest
    public void invokeAPIWithAPIKey_TestDevPortalAPIKey() throws Exception {
        String testSessionId = NameGenerator.generateThreadUniqueName();
        DevPortalApiKeyUtils.invokeApiGET(this, apiKey, endpointUrl, testSessionId, RESOURCE_PATH, HttpStatus.OK);
    }

    @Test(dependsOnMethods = {"invokeAPIWithAPIKey_TestDevPortalAPIKey"})
    @CitrusTest
    public void regenerateAPIKeyAndInvoke_TestDevPortalAPIKey() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ApiKeyResponse apiKeyResponse = ApiManager.regenerateApiKeyV2(accessToken, apiKeyId);
        regeneratedApiKey = apiKeyResponse.getValue();
        String testSessionId = NameGenerator.generateThreadUniqueName();
        DevPortalApiKeyUtils.invokeApiGET(this, regeneratedApiKey, endpointUrl, testSessionId, RESOURCE_PATH, HttpStatus.OK);
    }

    @Test(dependsOnMethods = {"regenerateAPIKeyAndInvoke_TestDevPortalAPIKey"})
    @CitrusTest
    public void deleteAPIKeyAndInvoke() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ApiManager.deleteApiKeyV2(accessToken, apiKeyId);
        String testSessionId = NameGenerator.generateThreadUniqueName();
        DevPortalApiKeyUtils.invokeApiGET(this, regeneratedApiKey, endpointUrl, testSessionId, RESOURCE_PATH, HttpStatus.UNAUTHORIZED);
    }
}
