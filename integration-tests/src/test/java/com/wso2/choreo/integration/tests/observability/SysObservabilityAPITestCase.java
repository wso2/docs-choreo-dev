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
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class SysObservabilityAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    ChoreoProject project;
    String projectId;
    String devInvokeURL;
    String prodInvokeURL;
    Environment[] en;
    String apiKey;
    ChoreoComponent choreoComponent;
    ChoreoOrganization org;

    @Autowired
    private HttpClient choreoTestClient;
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
        project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }


    @Test
    @CitrusTest
    public void createUserManagedComponent_SysObservabilityAPITestCase() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).
                triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/rest-api").
                projectId(projectId).
                displayType(Constant.displayType.restAPI.name()).build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponent_SysObservabilityAPITestCase"})
    @CitrusTest
    public void deploy_SysObservabilityAPITestCase() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent);
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"deploy_SysObservabilityAPITestCase"})
    @CitrusTest
    public void addPromoteConfiguration_SysObservabilityAPITestCase() throws Exception {
        Response res = Orgs.addConfiguration(choreoComponent, "prod", accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_SysObservabilityAPITestCase"})
    @CitrusTest
    public void promote_SysObservabilityAPITestCase() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_SysObservabilityAPITestCase"})
    @CitrusTest
    public void componentProdDeploymentStatus_SysObservabilityAPITestCase() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentProdDeploymentStatus_SysObservabilityAPITestCase"})
    @CitrusTest
    public void invokeEP_SysObservabilityAPITestCase() throws IOException {
        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        TestHelper.invokeEP(devInvokeURL, apiKey);
        TestHelper.invokeEP(prodInvokeURL, apiKey);
    }

    @Test(dependsOnMethods = {"invokeEP_SysObservabilityAPITestCase"})
    @CitrusTest
    public void waitForObservabilityLogs_SysObservabilityAPITestCase() throws Exception {

        en = GraphQL.getNamespaceForEnvironment(projectId, accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = choreoComponent.getEnvironment(en, Constant.Environment.Production);
        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        choreoComponent.waitForObservabilityLogs(prodEnv, accessToken);

        String devReleaseId = choreoComponent.getReleaseIdForEnvironment(devEnv.getChoreoEnv());
        String prodReleaseId = choreoComponent.getReleaseIdForEnvironment(prodEnv.getChoreoEnv());
    }

    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_SysObservabilityAPITestCase"})
    @CitrusTest
    public void testSystemMetrics_SysObservabilityAPITestCase(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);

        String requestPath = Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX
                .concat(observabilityIdInformation.getObsId())
                .concat("/metricsV2");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(requestPath)
                .message()
                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .queryParam("interval", "15")
                .queryParam("releaseId", releaseId)
                .queryParam("namespace", namespace)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.keySet()", hasItems("columns", "rows"))
                        .expression("$.columns[*].name", hasItems("cpu", "memory", "cpuPercentage", "memoryPercentage", "TimeGenerated"))
                        .expression("$.columns[*].type", hasItems("dynamic", "dynamic", "dynamic", "dynamic", "dynamic"))
                        .expression("$.rows.size()", greaterThan(0))
                        .expression("$.rows[*]", allOf(is(not(emptyString()))))
                )
        );
    }
}
