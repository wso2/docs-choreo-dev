package com.wso2.choreo.integration.tests.managedAuthentication;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
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
    private List<Environment> componentAEnvironments;
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
    public void deployComponentWithDefaultConfigurations_ManagedAuthenticationTests() throws Exception {
        defaultComponent = ManagedAuthenticationUtils.createWebAppComponent(this, citrusClients, accessToken, project);

        componentAEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, 
            defaultComponent);
        Environment devEnvironment = componentAEnvironments.stream()
            .filter(env -> env.getChoreoEnv().equals(Constant.DEV_ENVIRONMENT))
            .findFirst()
            .get();

        ManagedAuthenticationUtils.GenerateKeyset(this, appServiceClient, defaultComponent, devEnvironment);
        ManagedAuthenticationUtils.setDefaultManagedAuthConfig(this, appServiceClient, defaultComponent, 
            devEnvironment);
        ManagedAuthenticationUtils.buildAndDeployWebAppComponent(this, citrusClients, accessToken, defaultComponent, 
            componentAEnvironments);

        ManagedAuthenticationUtils.validateKeySetConfig(this, appServiceClient, defaultComponent, devEnvironment);
        ManagedAuthenticationUtils.validateDefaultManagedAuthConfig(this, appServiceClient, defaultComponent, 
            devEnvironment);
    }
}
