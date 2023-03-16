package com.wso2.choreo.integration.tests.graphqlservice;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.observability.ObservabilityService;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;
import org.apache.tools.ant.Project;
import org.hamcrest.core.StringRegularExpression;
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
import java.util.*;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;

public class GraphQLServiceEUdpIT extends TestNGCitrusSpringSupport {

    private String accessToken;
    private ChoreoComponent choreoComponent;
    private ChoreoProject project;
    private String devInvokeURL;
    private String prodInvokeURL;
    private String apiId;
    private KeyData keyData;
    private Environment[] en;
    @Autowired
    private HttpClient choreoCPTestClient;
    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;


    private final List<ChoreoComponent> componentList = new ArrayList<>();
    private final List<String> apiIds = new ArrayList<>();
    private final List<KeyData> keyDataList = new ArrayList<>();
    private final List<ChoreoProject> projectList = new ArrayList<>();

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}, {Constant.Environment.Development}};
    }

    @DataProvider(name = "reg")
    public Object[][] regions() {
        return convertToDataProvider(Arrays.asList(Constant.region.values()));
    }

    @DataProvider(name = "reg-components")
    public Object[][] regionalComponentsDataProvider() {
        return convertToDataProvider(componentList);
    }

    @DataProvider(name = "api-ids")
    public Object[][] apiIdProvider() {
        return convertToDataProvider(apiIds);
    }

    @DataProvider(name = "projects")
    public Object[][] projectProvider() {
        return convertToDataProvider(projectList);
    }

    @DataProvider(name = "key-data")
    public Object[][] keyProvider() {
        return convertToDataProvider(keyDataList);
    }

    public <T> Object[][] convertToDataProvider(List<T> list) {
        Object[][] dataProvider = new Object[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            dataProvider[i][0] = list.get(i);
        }
        return dataProvider;
    }

    @DataProvider(name = "com-data")
    public Object[][] envComponentProvider() {
        Constant.region[] reg = Constant.region.values();
        Object[][] com = new Object[reg.length][componentList.size()];

        for (int i = 0; i < reg.length; i++) {
            for (int j = 0; j < componentList.size(); j++) {
                com[j][0] = reg[i];
            }
        }
        for (int j = 0; j < componentList.size(); j++) {
            for (int i = 0; i < reg.length; i++) {
                com[i][1] = componentList.get(j);
            }
        }

        return com;
    }


    @BeforeClass
    public void setup_GraphQLServiceEUdpIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "reg")
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceEUdpIT(Constant.region region) throws Exception {
        project = GraphQL.createProject(region, accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/graphql").
                projectId(project.getId()).
                displayType(Constant.displayType.graphql.name()).
                build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        componentList.add(choreoComponent);
        projectList.add(project);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceEUdpIT"}, dataProvider = "reg-components")
    @CitrusTest
    public void componentDeploy_GraphQLServiceEUdpIT(ChoreoComponent choreoComponent) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, ComponentFlavour.STANDARD);
        devInvokeURL = statusDTO.getInvokeUrl();

    }

    @Test(dependsOnMethods = {"componentDeploy_GraphQLServiceEUdpIT"}, dataProvider = "reg-components")
    @CitrusTest
    public void promote_GraphQLServiceEUdpIT(ChoreoComponent choreoComponent) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, choreoComponent, ComponentFlavour.STANDARD);
        prodInvokeURL = statusDTO.getInvokeUrl();
        apiId = statusDTO.getApiId();
        apiIds.add(apiId);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceEUdpIT"}, dataProvider = "api-ids")
    @CitrusTest
    public void invokeQueryInDev_GraphQLServiceEUdpIT(String apiId) throws Exception {
        keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken, apiId);
        ComponentUtils.invokeApiPOST(this, keyData.getApikey(), devInvokeURL, "/",
                GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
        keyDataList.add(keyData);
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_GraphQLServiceEUdpIT"}, dataProvider = "key-data")
    @CitrusTest
    public void invokeQueryInProd_GraphQLServiceEUdpIT(KeyData keyData) throws Exception {
        ComponentUtils.invokeApiPOST(this, keyData.getApikey(), prodInvokeURL, "/",
                GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_GraphQLServiceEUdpIT"}, dataProvider = "key-data")
    @CitrusTest
    public void invokeMutationInDev_GraphQLServiceEUdpIT(KeyData keyData) throws Exception {
        ComponentUtils.invokeApiPOST(this, keyData.getApikey(), devInvokeURL, "/",
                GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_GraphQLServiceEUdpIT"}, dataProvider = "key-data")
    @CitrusTest
    public void invokeMutationInProd_GraphQLServiceEUdpIT(KeyData keyData) throws Exception {
        ComponentUtils.invokeApiPOST(this, keyData.getApikey(), prodInvokeURL, "/",
                GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
    }

    @Test(dependsOnMethods = {"invokeMutationInProd_GraphQLServiceEUdpIT"}, dataProvider = "projects")
    @CitrusTest
    public void waitForObservabilityLogs_GraphQLServiceEUdpIT(ChoreoProject project) throws Exception {
        en = GraphQL.getNamespaceForEnvironment(project.getId(), accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = choreoComponent.getEnvironment(en, Constant.Environment.Production);
        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        choreoComponent.waitForObservabilityLogs(prodEnv, accessToken);
    }

    @Test(dataProvider = "com-data", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void testGroupedLogs_GraphQLServiceEUdpIT(Constant.Environment env, ChoreoComponent choreoComponent) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityLogs observabilityLogs = ObservabilityService.getGroupLogs(releaseId, namespace, accessToken);
        Assert.assertTrue(observabilityLogs.getRows().length > 0);
    }
//
//    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceEUdpIT"})
//    @CitrusTest
//    public void testLiveLogs_GraphQLServiceEUdpIT(Constant.Environment env) throws Exception {
//        Environment environment = choreoComponent.getEnvironment(en, env);
//        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
//        String namespace = environment.getNamespace();
//        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);
//
//        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
//                .concat(observabilityIdInformation.getObsId())
//                .concat("/logsV2");
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        $(http()
//                .client(choreoCPTestClient)
//                .send()
//                .get(requestPath)
//                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
//                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
//                .queryParam("releaseId", releaseId)
//                .queryParam("namespace", namespace)
//                .queryParam("sort", "desc")
//                .queryParam("limit", "95")
//                .message()
//                .header(HttpHeaders.AUTHORIZATION, accessToken)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
//        $(http()
//                .client(choreoCPTestClient)
//                .receive()
//                .response(HttpStatus.OK)
//                .message()
//                .type(MessageType.JSON)
//                .validate(jsonPath()
//                        .expression("$.keySet()", hasItems("columns", "rows"))
//                        .expression("$.columns.size()", greaterThanOrEqualTo(1))
//                        .expression("$.columns[*].name", hasItems("TimeGenerated", "LogLevel", "LogEntry", "LogContext"))
//                        .expression("$.columns[*].type", hasItems("datetime", "string", "dynamic", "dynamic"))
//                        .expression("$.rows.size()", greaterThanOrEqualTo(1))
//                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")))
//                )
//        );
//    }
}
