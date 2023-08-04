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
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class SysObservabilityAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private ChoreoComponent choreoComponent;
    private String orgHandle;
    private HttpClient appServiceClient;
    private String API_INVOCATION_REQUEST_URI;
    private String REST_API_EXPECTED_RESPONSE;
    private List<Environment> environments;

    @Autowired
    private HttpClient choreoCPTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}, {Constant.Environment.Production}};
    }

    @BeforeClass
    public void setup_SysObservabilityAPITestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        API_INVOCATION_REQUEST_URI = "/books";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/ballerinaService/ballerinaServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test
    @CitrusTest
    public void createComponent_SysObservabilityAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = {"createComponent_SysObservabilityAPITestCase"})
    @CitrusTest
    public void deployComponent_SysObservabilityAPITestCase() throws Exception {
        ComponentUtils.deployComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"deployComponent_SysObservabilityAPITestCase"})
    @CitrusTest
    public void promoteComponent_SysObservabilityAPITestCase() throws Exception {
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, choreoComponent,
                environments, ComponentFlavour.STANDARD);
    }

    @Test(dependsOnMethods = {"promoteComponent_SysObservabilityAPITestCase"})
    @CitrusTest
    public void getEnvironments_SysObservabilityAPITestCase() throws Exception {
        List<Environment> observabilityEnvs = ComponentUtils.getEnvironments(this, citrusClients, accessToken,
                choreoComponent);
    }

    @Test(dependsOnMethods = {"getEnvironments_SysObservabilityAPITestCase"})
    @CitrusTest
    public void getObservabilityIds_SysObservabilityAPITestCase() throws Exception {
        List<ObservabilityIdInformation> observabilityIdInfoList = ComponentUtils.getObservabilityIds(this,
                citrusClients, accessToken, choreoComponent);
    }

    @Test(dependsOnMethods = {"getObservabilityIds_SysObservabilityAPITestCase"})
    @CitrusTest
    public void invokeAPIDev_SysObservabilityAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = {"invokeAPIDev_SysObservabilityAPITestCase"})
    @CitrusTest
    public void invokeAPIProd_SysObservabilityAPITestCase() throws Exception {
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

    @Test(dependsOnMethods = {"invokeAPIProd_SysObservabilityAPITestCase"})
    @CitrusTest
    public void testSystemMetrics_SysObservabilityAPITestCase() throws Exception {
        ComponentUtils.updateEnvironments(environments, ComponentUtils.getEnvironments(this, citrusClients,
                accessToken, choreoComponent));
        for (Environment env : environments) {
            String releaseId = choreoComponent.getReleaseIdForEnvironment(env);
            String namespace = env.getNamespace();
            ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(
                    releaseId, accessToken);

            String requestPath = Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX
                    .concat(observabilityIdInformation.getObsId())
                    .concat("/metricsV2");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            $(repeatOnError()
                    .until("i = 40")
                    .index("i")
                    .autoSleep(30000)
                    .actions(
                            http()
                                    .client(choreoCPTestClient)
                                    .send()
                                    .get(requestPath)
                                    .message()
                                    .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC)
                                            .truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                                    .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC)
                                            .truncatedTo(ChronoUnit.SECONDS).plusSeconds(10 * 60)))
                                    .queryParam("interval", "14")
                                    .queryParam("region", "US")
                                    .queryParam("releaseId", releaseId)
                                    .queryParam("namespace", namespace)
                                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                            http()
                                    .client(choreoCPTestClient)
                                    .receive()
                                    .response(HttpStatus.OK)
                                    .message()
                                    .type(MessageType.JSON)
                                    .validate(jsonPath()
                                            .expression("$.keySet()", hasItems("columns", "rows"))
                                            .expression("$.columns[*].name", hasItems("cpu", "memory",
                                                    "cpuPercentage", "memoryPercentage", "TimeGenerated"))
                                            .expression("$.columns[*].type", hasItems("dynamic", "dynamic",
                                                    "dynamic", "dynamic", "dynamic"))
                                            .expression("$.rows.size()", greaterThan(0))
                                            .expression("$.rows[*]", allOf(is(not(emptyString()))))
                                    )
                    )
            );
        }
    }

    @Test(dependsOnMethods = {"testSystemMetrics_SysObservabilityAPITestCase"})
    @CitrusTest
    public void undeployComponentDev_SysObservabilityAPITestCase() throws Exception {
        String devReleaseId = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT,
                accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(devReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployComponentDev_SysObservabilityAPITestCase"})
    @CitrusTest
    public void undeployComponentProd_SysObservabilityAPITestCase() throws Exception {
        String prodReleaseId = GraphQL.componentDeployment(choreoComponent, Constant.PROD_ENVIRONMENT,
                accessToken).getReleaseId();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(choreoComponent.getId()).orgHandler(orgHandle)
                .componentType("ballerinaService").releaseId(prodReleaseId).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
}
