package com.wso2.choreo.integration.tests.dp;

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
import com.wso2.choreo.integration.tests.graphqlservice.GqlServiceTestHelper;
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
    private String devInvokeURL;
    private String prodInvokeURL;
    private Environment[] en;
    @Autowired
    private HttpClient choreoCPTestClient;
    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;



     private final List<DataProviderWrapper> dps = new ArrayList<>();


    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return DataProviderWrapper.convertToDataProvider(dps);
    }
    @DataProvider(name = "reg")
    public Object[][] regions() {
        return DataProviderWrapper.convertToDataProvider(Arrays.asList(Constant.region.values()));
    }


    @DataProvider(name = "env-provider")
    public Object[][] envProvider(){
        return  DataProviderWrapper.convertToDataProvider(Arrays.asList(Constant.Environment.values()));
    }

    @BeforeClass
    public void setup_GraphQLServiceEUdpIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "reg")
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceEUdpIT(Constant.region region) throws Exception {
        ChoreoProject project = GraphQL.createProject(region, accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/graphql").
                projectId(project.getId()).
                displayType(Constant.displayType.graphql.name()).
                build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        DataProviderWrapper dp = DataProviderWrapper.builder().choreoProject(project).choreoComponent(choreoComponent).build();
        dps.add(dp);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), ComponentFlavour.STANDARD);
        devInvokeURL = statusDTO.getInvokeUrl();
        dp.setDevInvokeUrl(devInvokeURL);
    }

    @Test(dependsOnMethods = {"componentDeploy_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void promote_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, dp.getChoreoComponent(), ComponentFlavour.STANDARD);
        prodInvokeURL = statusDTO.getInvokeUrl();
        String apiId = statusDTO.getApiId();
        dp.setApiId(apiId);
        dp.setProdInvokeUrl(prodInvokeURL);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeQueryInDev_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken, dp.getApiId());
        ComponentUtils.invokeApiPOST(this, keyData.getApikey(), dp.getDevInvokeUrl(), "/",
                GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
        dp.setKeyData(keyData);
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeQueryInProd_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentUtils.invokeApiPOST(this, dp.getKeyData().getApikey(), dp.getProdInvokeUrl(), "/",
                GqlServiceTestHelper.getGqlQueryRequest(), GqlServiceTestHelper.getGqlQueryResponse());
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeMutationInDev_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentUtils.invokeApiPOST(this, dp.getKeyData().getApikey(), dp.getDevInvokeUrl(), "/",
                GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeMutationInProd_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        ComponentUtils.invokeApiPOST(this, dp.getKeyData().getApikey(), dp.getProdInvokeUrl(), "/",
                GqlServiceTestHelper.getGqlMutationRequest(), GqlServiceTestHelper.getGqlMutationResponse());
    }

    @Test(dependsOnMethods = {"invokeMutationInProd_GraphQLServiceEUdpIT"}, dataProvider = "dps")
    @CitrusTest
    public void waitForObservabilityLogs_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        en = GraphQL.getNamespaceForEnvironment(dp.getChoreoProject().getId(), accessToken);
        Environment devEnv = dp.getChoreoComponent().getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = dp.getChoreoComponent().getEnvironment(en, Constant.Environment.Production);
        dp.getChoreoComponent().waitForObservabilityLogs(devEnv, accessToken);
        dp.getChoreoComponent().waitForObservabilityLogs(prodEnv, accessToken);
    }

    @Test(dataProvider = "dps", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void testGroupedLogs_GraphQLServiceEUdpIT(DataProviderWrapper dp) throws Exception {
        Environment envDev = dp.getChoreoComponent().getEnvironment(en, dp.getDev());
        Environment envPrd = dp.getChoreoComponent().getEnvironment(en, dp.getPrd());
        String releaseIdDev = dp.getChoreoComponent().getReleaseIdForEnvironment(envDev.getChoreoEnv());
        String releaseIdPrd = dp.getChoreoComponent().getReleaseIdForEnvironment(envPrd.getChoreoEnv());
        String namespaceDev = envDev.getNamespace();
        String namespacePrd = envPrd.getNamespace();
        ObservabilityLogs observabilityLogsDev = ObservabilityService.getGroupLogs(releaseIdDev, namespaceDev, accessToken);
        ObservabilityLogs observabilityLogsPrd = ObservabilityService.getGroupLogs(releaseIdPrd, namespacePrd, accessToken);
        Assert.assertTrue(observabilityLogsDev.getRows().length > 0);
        Assert.assertTrue(observabilityLogsPrd.getRows().length > 0);
    }

    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void testLiveLogs_GraphQLServiceEUdpIT(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);

        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
                .concat(observabilityIdInformation.getObsId())
                .concat("/logsV2");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(requestPath)
                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .queryParam("releaseId", releaseId)
                .queryParam("namespace", namespace)
                .queryParam("sort", "desc")
                .queryParam("limit", "95")
                .message()
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
                        .expression("$.columns.size()", greaterThanOrEqualTo(1))
                        .expression("$.columns[*].name", hasItems("TimeGenerated", "LogLevel", "LogEntry", "LogContext"))
                        .expression("$.columns[*].type", hasItems("datetime", "string", "dynamic", "dynamic"))
                        .expression("$.rows.size()", greaterThanOrEqualTo(1))
                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")))
                )
        );
    }
}
