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
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.SyntaxTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ObservabilityAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;

    ChoreoProject project;
    String devInvokeURL;
    String prodInvokeURL;
    String apiId;
    SyntaxTree syntaxTree;

    String traceId;

    private static final int REQUEST_COUNT = 5;

    private List<ObservabilityIdInformation> observabilityIdInfoList = new ArrayList<>();

    ChoreoComponent choreoComponent;

    private List<Environment> environments;
    private List<ComponentDeploymentStatusDTO> statusDTOs;

    private List<Environment> observabilityEnvs;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ObservabilityAPITestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = GraphQL.createProject(accessToken);
    }


    @Test
    @CitrusTest
    public void createUserManagedComponent_ObservabilityAPITestCase() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/rest-api").branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);

        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        Assert.assertNotNull(choreoComponent.getId());

        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
    }


    @Test(dependsOnMethods = {"createUserManagedComponent_ObservabilityAPITestCase"})
    @CitrusTest
    public void deploy_ObservabilityAPITestCase() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, environments, ComponentFlavour.STANDARD);
        apiId = statusDTO.getApiId();
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"deploy_ObservabilityAPITestCase"})
    @CitrusTest
    public void promote_ObservabilityAPITestCase() throws Exception {
        statusDTOs = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, choreoComponent, environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"promote_ObservabilityAPITestCase"})
    @CitrusTest
    public void getEnvironments_ObservabilityAPITestCase() throws Exception {
        observabilityEnvs = ComponentUtils.getEnvironments(this, citrusClients, accessToken, choreoComponent);
    }

    @Test(dependsOnMethods = {"getEnvironments_ObservabilityAPITestCase"})
    @CitrusTest
    public void getObservabilityIds_ObservabilityAPITestCase() throws Exception {
        observabilityIdInfoList = ComponentUtils.getObservabilityIds(this, citrusClients, accessToken, choreoComponent);
    }

    @Test(dependsOnMethods = {"getObservabilityIds_ObservabilityAPITestCase"})
    @CitrusTest
    public void invokeEP_ObservabilityAPITestCase() throws Exception {
        KeyData devKeyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                apiId, ComponentUtils.getKeyType(environments.get(0)));
        KeyData prodKeyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                apiId, ComponentUtils.getKeyType(environments.get(1)));
        String expectedResponse = TestHelper.getExpectedResponse();
        for (int i = 0; i < REQUEST_COUNT; ++i) {
            ComponentUtils.invokeApiGET(this, devKeyData.getApikey(), devInvokeURL, "/isOdd?number=12121", expectedResponse);

            for (ComponentDeploymentStatusDTO statusDTO : statusDTOs) {
                ComponentUtils.invokeApiGET(this, prodKeyData.getApikey(), statusDTO.getInvokeUrl(), "/isOdd?number=12121", expectedResponse);
            }
        }
    }


    @Test(dependsOnMethods = {"invokeEP_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityAST_ObservabilityAPITestCase() throws Exception {
        syntaxTree = ComponentUtils.verifyObservabilityAST(this, citrusClients, accessToken, observabilityIdInfoList, choreoComponent);
    }

    @Test(dependsOnMethods = {"testObservabilityAST_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityMetricDensity_ObservabilityAPITestCase() throws Exception {
        for (ObservabilityIdInformation obsIdInfo : observabilityIdInfoList) {
            ComponentUtils.verifyObservabilityMetricDensity(this, citrusClients, accessToken, obsIdInfo);
        }
    }

    @Test(dependsOnMethods = {"testObservabilityMetricDensity_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityMetricDensityHistogram_ObservabilityAPITestCase() throws Exception {
        ComponentUtils.verifyObservabilityMetricDensityHistrogram(this, citrusClients, accessToken,
                observabilityIdInfoList, choreoComponent);
    }

    @Test(dependsOnMethods = {"testObservabilityAST_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityStats_ObservabilityAPITestCase() throws Exception {
        ComponentUtils.verifyObservabilityAPI(this, citrusClients, accessToken, observabilityIdInfoList,
                choreoComponent, syntaxTree);
    }

    @Test(dependsOnMethods = {"testObservabilityAST_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityTraceList_ObservabilityAPITestCase() throws Exception {
        traceId = ComponentUtils.verifyObservabilityTraceList(this, citrusClients, accessToken,
                observabilityIdInfoList, choreoComponent, syntaxTree, REQUEST_COUNT);
    }

    @Test(dependsOnMethods = {"testObservabilityTraceList_ObservabilityAPITestCase"})
    @CitrusTest
    public void testObservabilityTraceInformation_ObservabilityAPITestCase() throws Exception {
        ComponentUtils.verifyObservabilityTraceInformation(this, citrusClients, accessToken,
                    observabilityIdInfoList, choreoComponent, syntaxTree, traceId);
    }
}
