/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.tests.observability;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LoggingAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    ChoreoProject project;
    String projectId;
    String devInvokeURL;
    String prodInvokeURL;
    private String apiId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    ChoreoComponent choreoComponent;

    private List<Environment> environments;
    private List<ComponentDeploymentStatusDTO> statusDTOs;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}, {Constant.Environment.Production}};
    }

    @BeforeClass
    public void setup_LoggingAPITestCase() throws Exception {
       accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }


    @Test
    @CitrusTest
    public void createUserManagedComponent_LoggingAPITestCase() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/rest-api").branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);

        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        Assert.assertNotNull(choreoComponent.getId());

        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
    }


    @Test(dependsOnMethods = {"createUserManagedComponent_LoggingAPITestCase"})
    @CitrusTest
    public void deploy_LoggingAPITestCase() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, environments, ComponentFlavour.STANDARD);
        apiId = statusDTO.getApiId();
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"deploy_LoggingAPITestCase"})
    @CitrusTest
    public void promote_LoggingAPITestCase() throws Exception {
        statusDTOs = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, choreoComponent, environments, ComponentFlavour.STANDARD);
    }


    @Test(dependsOnMethods = {"promote_LoggingAPITestCase"})
    @CitrusTest
    public void invokeEP_LoggingAPITestCase() throws Exception {
        KeyData devKeyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                apiId, environments.get(0).getName());
        KeyData prodKeyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                apiId, environments.get(1).getName());
        String expectedResponse = TestHelper.getExpectedResponse();
        for (int i = 0; i < 5; ++i) {
            ComponentUtils.invokeApiGET(this, devKeyData.getApikey(), devInvokeURL, "/isOdd?number=12121", expectedResponse);

            for (ComponentDeploymentStatusDTO statusDTO : statusDTOs) {
                ComponentUtils.invokeApiGET(this, prodKeyData.getApikey(), statusDTO.getInvokeUrl(), "/isOdd?number=12121", expectedResponse);
            }
        }
    }


    @Test(dependsOnMethods = {"invokeEP_LoggingAPITestCase"})
    @CitrusTest
    public void testGroupedLogs_LoggingAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients, accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyGroupLogs(this, citrusClients, accessToken, choreoComponent, env, Constant.region.US.name());
        }
    }

    @Test(dependsOnMethods = {"testGroupedLogs_LoggingAPITestCase"})
    @CitrusTest
    public void testLiveLogs_LoggingAPITestCase() throws Exception {
        for (Environment env : environments) {
            ComponentUtils.verifyLogs(this, citrusClients, accessToken, choreoComponent, env, Constant.region.US.name());
        }
    }

    @Test(dependsOnMethods = {"testLiveLogs_LoggingAPITestCase"})
    @CitrusTest
    public void downloadZippedLogs_LoggingAPITestCase() throws Exception {
        for (Environment env : environments) {
            ComponentUtils.verifyZipLogs(this, citrusClients, accessToken, choreoComponent, env, Constant.region.US.name());
        }
    }

}
