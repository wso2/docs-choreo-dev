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
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LoggingAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private ChoreoComponent choreoComponent;
    private String orgHandle;
    private HttpClient appServiceClient;
    private String API_INVOCATION_REQUEST_URI;
    private String REST_API_EXPECTED_RESPONSE;
    private List<Environment> environments;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}, {Constant.Environment.Production}};
    }

    @BeforeClass
    public void setup_LoggingAPITestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        API_INVOCATION_REQUEST_URI = "/books";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/ballerinaService/ballerinaServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test
    @CitrusTest
    public void createComponent_LoggingAPITestCase() throws Exception {
        ChoreoProject project = GraphQL.createProject(accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/byor-service-app1").
                branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);

        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                choreoComponent);

        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createComponent_LoggingAPITestCase"})
    @CitrusTest
    public void deployComponent_LoggingAPITestCase() throws Exception {
        ComponentUtils.deployComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"deployComponent_LoggingAPITestCase"})
    @CitrusTest
    public void promoteComponent_LoggingAPITestCase() throws Exception {
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"promoteComponent_LoggingAPITestCase"})
    @CitrusTest
    public void invokeAPIDev_LoggingAPITestCase() throws Exception {
        Endpoint endpoint = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
                choreoComponent, Constant.DEV_ENVIRONMENT).get(0);
        String devApiKey = choreoComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(0).getName()).replace("\"", "");
        String invokeUrlDev = endpoint.getPublicUrl();
        for (int i = 0; i < 5; ++i) {
            ComponentUtils.invokeApiGET(this, devApiKey, invokeUrlDev, API_INVOCATION_REQUEST_URI,
                    REST_API_EXPECTED_RESPONSE);
        }
    }

    @Test(dependsOnMethods = {"invokeAPIDev_LoggingAPITestCase"})
    @CitrusTest
    public void invokeAPIProd_LoggingAPITestCase() throws Exception {
        Endpoint endpoint = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
                choreoComponent, Constant.PROD_ENVIRONMENT).get(0);
        String prodApiKey = choreoComponent.getAPIKeyForInvoke(accessToken, endpoint.getApimId(),
                environments.get(1).getName()).replace("\"", "");
        String invokeUrlProd = endpoint.getPublicUrl();
        for (int i = 0; i < 5; ++i) {
            ComponentUtils.invokeApiGET(this, prodApiKey, invokeUrlProd, API_INVOCATION_REQUEST_URI,
                    REST_API_EXPECTED_RESPONSE);
        }
    }

    @Test(dependsOnMethods = {"invokeAPIProd_LoggingAPITestCase"})
    @CitrusTest
    public void verifyGroupedLogs_LoggingAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyGroupLogs(this, citrusClients, accessToken, choreoComponent, env,
                    Constant.region.US.name());
        }
    }

    @Test(dependsOnMethods = {"verifyGroupedLogs_LoggingAPITestCase"})
    @CitrusTest
    public void verifyLiveLogs_LoggingAPITestCase() throws Exception {
        for (Environment env : environments) {
            ComponentUtils.verifyLogs(this, citrusClients, accessToken, choreoComponent, env,
                    Constant.region.US.name());
        }
    }

    @Test(dependsOnMethods = {"verifyLiveLogs_LoggingAPITestCase"})
    @CitrusTest
    public void verifyZipLogs_LoggingAPITestCase() throws Exception {
        for (Environment env : environments) {
            ComponentUtils.verifyZipLogs(this, citrusClients, accessToken, choreoComponent, env,
                    Constant.region.US.name());
        }
    }

    @Test(dependsOnMethods = {"verifyZipLogs_LoggingAPITestCase"})
    @CitrusTest
    public void undeployComponentDev_LoggingAPITestCase() throws Exception {
        String devReleaseId = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT,
                accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(devReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_LoggingAPITestCase"})
    @CitrusTest
    public void undeployComponentProd_LoggingAPITestCase() throws Exception {
        String prodReleaseId = GraphQL.componentDeployment(choreoComponent, Constant.PROD_ENVIRONMENT,
                accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(prodReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
}
