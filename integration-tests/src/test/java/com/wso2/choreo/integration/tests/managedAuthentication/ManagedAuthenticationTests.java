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

package com.wso2.choreo.integration.tests.managedAuthentication;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationConstants.Project;
import com.wso2.choreo.integration.common.managedAuthentication.ManagedAuthenticationUtils;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.environments.Environment;


public class ManagedAuthenticationTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private HttpClient appServiceClient;
    private ChoreoProject project;
    private ChoreoComponent defaultComponent;
    private List<Environment> defaultComponentEnvironments;
    private Environment defaultComponentDevEnv;
    private Environment defaultComponentProdEnv;
    private String accessToken;

    @BeforeClass
    public void setup_ManagedAuthenticationTests() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
    }

    @Test()
    @CitrusTest
    public void createProject_ManagedAuthenticationTests() throws Exception {
        project = ComponentUtils.createProject(this, citrusClients, accessToken, Project.REGION);
    }

    @Test(dependsOnMethods = {"createProject_ManagedAuthenticationTests"})
    @CitrusTest
    public void setupDefaultComponent_ManagedAuthenticationTests() throws Exception {
        defaultComponent = ManagedAuthenticationUtils.createWebAppComponent(this, citrusClients, accessToken, project);

        defaultComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, 
            defaultComponent);
        defaultComponentDevEnv = defaultComponentEnvironments.stream()
            .filter(env -> env.getChoreoEnv().equals(Constant.DEV_ENVIRONMENT))
            .findFirst()
            .get();
        defaultComponentProdEnv = defaultComponentEnvironments.stream()
            .filter(env -> env.getChoreoEnv().equals(Constant.PROD_ENVIRONMENT))
            .findFirst()
            .get();
    }

    @Test(dependsOnMethods = {"setupDefaultComponent_ManagedAuthenticationTests"})
    @CitrusTest
    public void deployComponentWithDefaultConfigurations_ManagedAuthenticationTests() throws Exception {
        ManagedAuthenticationUtils.GenerateKeyset(this, appServiceClient, defaultComponent, defaultComponentDevEnv);
        ManagedAuthenticationUtils.setDefaultManagedAuthConfig(this, appServiceClient, defaultComponent, 
            defaultComponentDevEnv);
        ManagedAuthenticationUtils.buildAndDeployWebAppComponent(this, citrusClients, accessToken, defaultComponent, 
            defaultComponentEnvironments);

        ManagedAuthenticationUtils.validateKeySetConfig(this, appServiceClient, defaultComponent, 
            defaultComponentDevEnv);
        ManagedAuthenticationUtils.validateDefaultManagedAuthConfig(this, appServiceClient, defaultComponent, 
            defaultComponentDevEnv);
    }

    @Test(dependsOnMethods = {"deployComponentWithDefaultConfigurations_ManagedAuthenticationTests"})
    @CitrusTest
    public void promoteComponentWithDefaultConfigurations_ManagedAuthenticationTests() throws Exception {
        ManagedAuthenticationUtils.GenerateKeyset(this, appServiceClient, defaultComponent, defaultComponentProdEnv);
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, defaultComponent, 
            defaultComponentEnvironments, ComponentFlavour.WEBAPP);

        ManagedAuthenticationUtils.validateKeySetConfig(this, appServiceClient, defaultComponent, 
            defaultComponentProdEnv);
        ManagedAuthenticationUtils.validateDefaultManagedAuthConfig(this, appServiceClient, defaultComponent, 
            defaultComponentProdEnv);
    }

    @Test(dependsOnMethods = {"promoteComponentWithDefaultConfigurations_ManagedAuthenticationTests"})
    @CitrusTest
    public void deployComponentWithManagedAuthDisabled_ManagedAuthenticationTests() throws Exception {
        ManagedAuthenticationUtils.setManagedAuthDisabledConfig(this, appServiceClient, defaultComponent, 
            defaultComponentDevEnv);
        ManagedAuthenticationUtils.deployBuiltWebAppComponent(this, citrusClients, accessToken, defaultComponent, 
            defaultComponentEnvironments);

        ManagedAuthenticationUtils.validateManagedAuthDisabledConfig(this, appServiceClient, defaultComponent, 
            defaultComponentDevEnv);
    }

    @Test(dependsOnMethods = {"deployComponentWithManagedAuthDisabled_ManagedAuthenticationTests"})
    @CitrusTest
    public void promoteComponentWithManagedAuthDisabled_ManagedAuthenticationTests() throws Exception {
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, defaultComponent, 
            defaultComponentEnvironments, ComponentFlavour.WEBAPP);

        ManagedAuthenticationUtils.validateManagedAuthDisabledConfig(this, appServiceClient, defaultComponent, 
            defaultComponentProdEnv);
    }
}
