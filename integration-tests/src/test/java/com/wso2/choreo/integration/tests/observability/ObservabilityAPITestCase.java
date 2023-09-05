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
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.SyntaxTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ObservabilityAPITestCase extends TestNGCitrusSpringSupport {

    private static String accessToken;
    private SyntaxTree syntaxTree;
    private ChoreoComponent choreoComponent;
    private ChoreoProject project;
    private String orgHandle;
    private String traceId;
    private HttpClient appServiceClient;
    private String API_INVOCATION_REQUEST_URI;
    private String REST_API_EXPECTED_RESPONSE;
    private static final int REQUEST_COUNT = 5;
    private List<Environment> environments;
    private List<ObservabilityIdInformation> observabilityIdInfoList = new ArrayList<>();

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ComponentDeploymentStatusDTO deploymentStatusDTO, promotionStatusDTO;

    @BeforeClass
    public void setup_ObservabilityAPITestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        API_INVOCATION_REQUEST_URI = "/books";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/ballerinaService/ballerinaServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test
    @CitrusTest
    public void createProject_ObservabilityAPITestCase() throws Exception {
        project = ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.US.toString());
    }

    @Test(dependsOnMethods = {"createProject_ObservabilityAPITestCase"})
    @CitrusTest
    public void createComponent_ObservabilityAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = {"createComponent_ObservabilityAPITestCase"})
    @CitrusTest
    public void deployComponent_ObservabilityAPITestCase() throws Exception {
        deploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"deployComponent_ObservabilityAPITestCase"})
    @CitrusTest
    public void promoteComponent_ObservabilityAPITestCase() throws Exception {
        List<ComponentDeploymentStatusDTO> promotionStatuses = ComponentUtils.promoteComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
        promotionStatusDTO = promotionStatuses.get(0);
    }

    @Test(dependsOnMethods = {"promoteComponent_ObservabilityAPITestCase"})
    @CitrusTest
    public void getEnvironments_ObservabilityAPITestCase() throws Exception {
        List<Environment> observabilityEnvs = ComponentUtils.getEnvironments(this, citrusClients, accessToken, choreoComponent);
    }

    @Test(dependsOnMethods = {"getEnvironments_ObservabilityAPITestCase"})
    @CitrusTest
    public void getObservabilityIds_ObservabilityAPITestCase() throws Exception {
        observabilityIdInfoList = ComponentUtils.getObservabilityIds(this, citrusClients, accessToken,
                choreoComponent);
    }

    @Test(dependsOnMethods = {"getObservabilityIds_ObservabilityAPITestCase"})
    @CitrusTest
    public void invokeAPIDev_ObservabilityAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = {"invokeAPIDev_ObservabilityAPITestCase"})
    @CitrusTest
    public void invokeAPIProd_ObservabilityAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = {"invokeAPIProd_ObservabilityAPITestCase"})
    @CitrusTest
    public void verifyObservabilityAST_ObservabilityAPITestCase() throws Exception {
        syntaxTree = ComponentUtils.verifyObservabilityAST(this, citrusClients, accessToken,
                observabilityIdInfoList, choreoComponent);
    }

    @Test(dependsOnMethods = {"verifyObservabilityAST_ObservabilityAPITestCase"})
    @CitrusTest
    public void verifyObservabilityMetricDensity_ObservabilityAPITestCase() throws Exception {
        for (ObservabilityIdInformation obsIdInfo : observabilityIdInfoList) {
            ComponentUtils.verifyObservabilityMetricDensity(this, citrusClients, accessToken, obsIdInfo);
        }
    }

    @Test(dependsOnMethods = {"verifyObservabilityMetricDensity_ObservabilityAPITestCase"})
    @CitrusTest
    public void verifyObservabilityMetricDensityHistogram_ObservabilityAPITestCase() throws Exception {
        ComponentUtils.verifyObservabilityMetricDensityHistrogram(this, citrusClients, accessToken,
                observabilityIdInfoList, choreoComponent);
    }

    @Test(dependsOnMethods = {"verifyObservabilityAST_ObservabilityAPITestCase"})
    @CitrusTest
    public void verifyObservabilityStats_ObservabilityAPITestCase() throws Exception {
        ComponentUtils.verifyObservabilityAPI(this, citrusClients, accessToken, observabilityIdInfoList,
                choreoComponent, syntaxTree);
    }

    @Test(dependsOnMethods = {"verifyObservabilityAST_ObservabilityAPITestCase"})
    @CitrusTest
    public void verifyObservabilityTraceList_ObservabilityAPITestCase() throws Exception {
        String entryPointSvcName = "/readinglist";
        String entryPointFuncName = "/books";
        traceId = ComponentUtils.verifyObservabilityTraceList(this, citrusClients, accessToken,
                observabilityIdInfoList, choreoComponent, syntaxTree, REQUEST_COUNT, entryPointSvcName,
                entryPointFuncName);
    }

    @Test(dependsOnMethods = {"verifyObservabilityTraceList_ObservabilityAPITestCase"})
    @CitrusTest
    public void verifyObservabilityTraceInformation_ObservabilityAPITestCase() throws Exception {
        ComponentUtils.verifyObservabilityTraceInformation(this, citrusClients, accessToken,
                observabilityIdInfoList, choreoComponent, syntaxTree, traceId);
    }

    @Test(dependsOnMethods = {"verifyObservabilityTraceInformation_ObservabilityAPITestCase"})
    @CitrusTest
    public void undeployComponentDev_ObservabilityAPITestCase() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(deploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_ObservabilityAPITestCase"})
    @CitrusTest
    public void undeployComponentProd_ObservabilityAPITestCase() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(promotionStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
}
