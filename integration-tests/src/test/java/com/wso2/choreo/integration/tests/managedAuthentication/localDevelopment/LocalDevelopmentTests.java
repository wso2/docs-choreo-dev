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

package com.wso2.choreo.integration.tests.managedAuthentication.localDevelopment;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.external.ManagedAuth;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.Project;
import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentConstants.EnableLocalDevelopmentCustomConfigs;
import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentConstants.LocalDevelopmentProxyHeaders;
import com.wso2.choreo.integration.common.managedAuthentication.localDevelopment.LocalDevelopmentUtils;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationUtils;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocalDevelopmentTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ChoreoProject testProject;
    private ChoreoComponent testComponent;
    private List<Environment> testComponentEnvironments;
    private Environment testComponentDevEnv, testComponentProdEnv;
    private String devEnvReleaseId, prodEnvReleaseId;
    private String devEnvInvokeURL;

    @Test()
    @CitrusTest
    public void createProject_LocalDevelopmentTests() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        testProject = ComponentUtils.createProject(this, citrusClients, accessToken, Project.REGION);
    }

    @Test(dependsOnMethods = "createProject_LocalDevelopmentTests")
    @CitrusTest
    public void createTestComponent_LocalDevelopmentTests() throws Exception {

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        testComponent = ManagedAuthenticationUtils.createWebAppComponent(this, citrusClients, accessToken,
                testProject);
        ComponentUtils.waitForComponentInitialBuildComplete(this, citrusClients, accessToken, testComponent);
        testComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                testComponent);
        testComponentDevEnv = testComponentEnvironments.stream()
                .filter(env -> env.getChoreoEnv().equals(Constant.DEV_ENVIRONMENT))
                .findFirst()
                .get();
        testComponentProdEnv = testComponentEnvironments.stream()
                .filter(env -> env.getChoreoEnv().equals(Constant.PROD_ENVIRONMENT))
                .findFirst()
                .get();
    }

    @Test(dependsOnMethods = "createTestComponent_LocalDevelopmentTests")
    @CitrusTest
    public void deployComponentToDevEnv_LocalDevelopmentTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        ManagedAuthenticationUtils.GenerateKeyset(this, appServiceClient, testComponent, testComponentDevEnv);
        ManagedAuthenticationUtils.setManagedAuthConfig(this, appServiceClient, testComponent,
                testComponentDevEnv, ManagedAuthenticationConstants.getDefaultManagedAuthConfig());
        ComponentDeploymentStatusDTO status = ManagedAuthenticationUtils.buildAndDeployWebAppComponent(this,
                citrusClients, accessToken, testComponent,
                testComponentEnvironments);

        devEnvReleaseId = status.getReleaseId();
        devEnvInvokeURL = status.getInvokeUrl();
    }

    @Test(dependsOnMethods = "deployComponentToDevEnv_LocalDevelopmentTests")
    @CitrusTest
    public void enableLocalDevelopmentWithDefaultConfigsInDevEnvironment_LocalDevelopmentTests()
            throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ComponentUtils.configureLocalDevelopmentForManagedAuthentication(this, appServiceClient,
                testProject.getId(), testComponent.getId(),
                devEnvReleaseId,
                LocalDevelopmentUtils.getEnableLocalDevelopmentWithDefaultConfigRequest(),
                HttpStatus.OK);
    }

    @Test(dependsOnMethods = "enableLocalDevelopmentWithDefaultConfigsInDevEnvironment_LocalDevelopmentTests")
    @CitrusTest
    public void disableLocalDevelopmentWithDefaultConfigsInDevEnvironment_LocalDevelopmentTests()
            throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ComponentUtils.configureLocalDevelopmentForManagedAuthentication(this, appServiceClient,
                testProject.getId(), testComponent.getId(),
                devEnvReleaseId,
                LocalDevelopmentUtils.getDisableLocalDevelopmentWithDefaultConfigRequest(),
                HttpStatus.OK);
    }

    @Test(dependsOnMethods = "disableLocalDevelopmentWithDefaultConfigsInDevEnvironment_LocalDevelopmentTests")
    @CitrusTest
    public void enableLocalDevelopmentWithCustomConfigsInDevEnvironment_LocalDevelopmentTests()
            throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ComponentUtils.configureLocalDevelopmentForManagedAuthentication(this, appServiceClient,
                testProject.getId(), testComponent.getId(),
                devEnvReleaseId,
                LocalDevelopmentUtils.getEnableLocalDevelopmentWithCustomConfigRequest(),
                HttpStatus.OK);
    }

    @Test(dependsOnMethods = "enableLocalDevelopmentWithCustomConfigsInDevEnvironment_LocalDevelopmentTests")
    @CitrusTest
    public void invokeAuthURLWithLocalDevelopmentEnabled_LocalDevelopmentTests() throws Exception {

        String redirectHeaderValue = ManagedAuth.initiateManagedAuthLoginFlow(devEnvInvokeURL,
                getHeadersForManagedAuthLoginFlow(
                        EnableLocalDevelopmentCustomConfigs.ALLOWED_URIS.get(0)));

        Assert.assertTrue(extractParametersFromRedirectURL(redirectHeaderValue, "redirect_uri")
                .contains(EnableLocalDevelopmentCustomConfigs.ALLOWED_URIS.get(0)));

    }

    @Test(dependsOnMethods = "invokeAuthURLWithLocalDevelopmentEnabled_LocalDevelopmentTests")
    @CitrusTest
    public void promoteComponentToProdEnv_LocalDevelopmentTests() throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        ManagedAuthenticationUtils.GenerateKeyset(this, appServiceClient, testComponent, testComponentProdEnv);
        List<ComponentDeploymentStatusDTO> promotionStatus = ComponentUtils.promoteComponent(this,
                citrusClients,
                accessToken, testComponent,
                testComponentEnvironments, ComponentFlavour.WEBAPP);

        prodEnvReleaseId = promotionStatus.get(0).getReleaseId();
    }

    @Test(dependsOnMethods = "promoteComponentToProdEnv_LocalDevelopmentTests")
    @CitrusTest
    public void enableLocalDevelopmentWithDefaultConfigsInProdEnvironment_LocalDevelopmentTests()
            throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ComponentUtils.configureLocalDevelopmentForManagedAuthentication(this, appServiceClient,
                testProject.getId(), testComponent.getId(),
                prodEnvReleaseId,
                LocalDevelopmentUtils.getEnableLocalDevelopmentWithDefaultConfigRequest(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static Map<String, String> getHeadersForManagedAuthLoginFlow(String proxyURL) {

        Map<String, String> headers = new HashMap<>();
        headers.put(LocalDevelopmentProxyHeaders.LOCAL_DEV_MODE, proxyURL);

        return headers;
    }

    private static String extractParametersFromRedirectURL(String redirectURL, String parameterName)
            throws URISyntaxException, UnsupportedEncodingException {

        URI uri = new URI(redirectURL);
        String query = uri.getQuery();

        Map<String, String> queryPairs = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
            String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            queryPairs.put(key, value);
        }

        return queryPairs.get(parameterName);

    }

}
