/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.logs;

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
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DPLogsAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private ChoreoComponent choreoComponent;
    private String orgHandle;
    private String API_INVOCATION_REQUEST_URI;
    private String REST_API_EXPECTED_RESPONSE;
    private List<Environment> environments;
    private ChoreoProject project;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][] { { Constant.Environment.Development }, { Constant.Environment.Production } };
    }

    private ComponentDeploymentStatusDTO deploymentStatusDTO, promotionStatusDTO;

    @BeforeClass
    public void setup_DPLogsAPITestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        API_INVOCATION_REQUEST_URI = "/books";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/ballerinaService/ballerinaServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test
    @CitrusTest
    public void verifyAuditLogs_DPLogsAPITestCase() throws Exception {
        ComponentUtils.verifyAuditLogs(this, citrusClients, accessToken);
    }

    @Test
    @CitrusTest
    public void verifyDataPlanes_DPLogsAPITestCase() throws Exception {
        ComponentUtils.verifyDataPlanes(this, citrusClients, accessToken);
    }

    @Test
    @CitrusTest
    public void verifyCloudDataPlanes_DPLogsAPITestCase() throws Exception {
        ComponentUtils.verifyCloudPlanes(this, citrusClients, accessToken);
    }

    @Test(dependsOnMethods = { "verifyCloudDataPlanes_DPLogsAPITestCase" })
    @CitrusTest
    public void createProject_DPLogsAPITestCase() throws Exception {
        project = ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.US.toString());
    }

    @Test(dependsOnMethods = { "createProject_DPLogsAPITestCase" })
    @CitrusTest
    public void createComponent_DPLogsAPITestCase() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/byor-service-app1")
                .branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);

        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                choreoComponent);

        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = { "createComponent_DPLogsAPITestCase" })
    @CitrusTest
    public void deployComponent_DPLogsAPITestCase() throws Exception {
        deploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = { "deployComponent_DPLogsAPITestCase" })
    @CitrusTest
    public void promoteComponent_DPLogsAPITestCase() throws Exception {
        List<ComponentDeploymentStatusDTO> promoteComponentStatues = ComponentUtils.promoteComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
        promotionStatusDTO = promoteComponentStatues.get(0);
    }

    @Test(dependsOnMethods = { "promoteComponent_DPLogsAPITestCase" })
    @CitrusTest
    public void invokeAPIDev_DPLogsAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = { "promoteComponent_DPLogsAPITestCase" })
    @CitrusTest
    public void invokeAPIProd_DPLogsAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = { "invokeAPIProd_DPLogsAPITestCase" })
    @CitrusTest
    public void verifyProjectLevelDPLogs_DPLogsLiveAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyProjectLevelDPLogsLive(this, citrusClients, accessToken, project, choreoComponent,
                    env);
        }
    }

    @Test(dependsOnMethods = { "invokeAPIProd_DPLogsAPITestCase" })
    @CitrusTest
    public void verifyProjectLevelDPMetrics_DPMetricsLiveAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyProjectLevelDPMetrics(this, citrusClients, accessToken, project, choreoComponent,
                    env);
        }
    }

    
    @Test(dependsOnMethods = { "verifyProjectLevelDPLogs_DPLogsLiveAPITestCase" })
    @CitrusTest
    public void verifyComponentLevelDPLogs_DPLogsLiveAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyComponentLevelDPLogsLive(this, citrusClients, accessToken, project, choreoComponent,
                    env);
        }
    }

    @Test(dependsOnMethods = { "verifyComponentLevelDPLogs_DPLogsLiveAPITestCase" })
    @CitrusTest
    public void verifyComponentLevelGatewayDPLogs_DPLogsLiveAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyGatewayDPLogsLive(this, citrusClients, accessToken, project, choreoComponent, env);
        }
    }

    @Test(dependsOnMethods = { "verifyComponentLevelGatewayDPLogs_DPLogsLiveAPITestCase" })
    @CitrusTest
    public void verifyProjectLevelDPLogs_DPLogsAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyProjectLevelDPLogs(this, citrusClients, accessToken, project, choreoComponent, env);
        }
    }

    @Test(dependsOnMethods = { "verifyProjectLevelDPLogs_DPLogsAPITestCase" })
    @CitrusTest
    public void verifyComponentLevelDPLogs_DPLogsAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyComponentLevelDPLogs(this, citrusClients, accessToken, project, choreoComponent, env);
        }
    }

    @Test(dependsOnMethods = { "verifyComponentLevelDPLogs_DPLogsAPITestCase" })
    @CitrusTest
    public void verifyComponentLevelGatewayDPLogs_DPLogsAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            ComponentUtils.verifyGatewayDPLogs(this, citrusClients, accessToken, project, choreoComponent, env);
        }
    }

    // Cleanup
    @Test(dependsOnMethods = { "verifyComponentLevelGatewayDPLogs_DPLogsAPITestCase" })
    @CitrusTest
    public void undeployComponentDev_DPLogsAPITestCase() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(deploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = { "undeployComponentDev_DPLogsAPITestCase" })
    @CitrusTest
    public void undeployComponentProd_DPLogsAPITestCase() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(promotionStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }
}
