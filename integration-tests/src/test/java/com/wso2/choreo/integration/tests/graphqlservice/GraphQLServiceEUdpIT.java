package com.wso2.choreo.integration.tests.graphqlservice;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.observability.ObservabilityService;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import com.wso2.choreo.integration.models.response.Response;
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
import java.util.Date;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class GraphQLServiceEUdpIT extends TestNGCitrusSpringSupport {

    private String accessToken;
    private String projectId;
    private String repoName;
    private ChoreoComponent choreoComponent;

    private String apiKey;
    private String devInvokeURL;
    private String prodInvokeURL;
    private Environment[] en;
    private static final String QUERY = "query{greeting(name:\"" + "John" + "\")}";
    private static final String MUTATION = "mutation{createUser(name:\"" + "John" + "\")}";
    @Autowired
    private HttpClient choreoCPTestClient;


    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}, {Constant.Environment.Development}};
    }


    @BeforeClass
    public void setup_GraphQLServiceEUdpIT() throws Exception {


        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));


        ChoreoProject project = GraphQL.createProject(Constant.region.EU, accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceEUdpIT() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/graphql").
                projectId(projectId).
                displayType(Constant.displayType.graphql.name()).
                build();
        choreoComponent = GraphQL.createUserManagedComponent(dto, accessToken);
        Assert.assertNotNull(choreoComponent.getId());
    }


    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void createdComponentStatus_GraphQLServiceEUdpIT() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, choreoComponent.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }


    @Test(dependsOnMethods = {"createdComponentStatus_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void componentRetrieval_GraphQLServiceEUdpIT() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandler(), accessToken);
        Assert.assertNotNull(choreoComponent);
    }


    @Test(dependsOnMethods = {"componentRetrieval_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void addDeploymentConfiguration_GraphQLServiceEUdpIT() throws Exception {
        Orgs.getConfigurationMapping(choreoComponent, accessToken);
        Orgs.addConfiguration(choreoComponent, Constant.DEV_ENVIRONMENT, accessToken);
    }

    @Test(dependsOnMethods = {"addDeploymentConfiguration_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void componentDeploy_GraphQLServiceEUdpIT() throws Exception {
        GraphQL.deployComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"componentRetrieval_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void deploymentStatusByVersion_GraphQLServiceEUdpIT() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void componentDevDeploymentStatus_GraphQLServiceEUdpIT() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT, accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void addPromoteConfiguration_GraphQLServiceEUdpIT() throws Exception {
        Response res = Orgs.addConfiguration(choreoComponent, Constant.PROD_ENVIRONMENT, accessToken);
        Orgs.getConfigurationMapping(choreoComponent, accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void promote_GraphQLServiceEUdpIT() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void componentProdDeploymentStatus_GraphQLServiceEUdpIT() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, Constant.PROD_ENVIRONMENT, accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentProdDeploymentStatus_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void invokeQueryInDev_GraphQLServiceEUdpIT() throws Exception {
        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        Response res = GqlServiceTestHelper.sendRequest(devInvokeURL, QUERY, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void invokeQueryInProd_GraphQLServiceEUdpIT() throws Exception {
        Response res = GqlServiceTestHelper.sendRequest(prodInvokeURL, QUERY, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void invokeMutationInDev_GraphQLServiceEUdpIT() throws Exception {
        Response res = GqlServiceTestHelper.sendRequest(devInvokeURL, MUTATION, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void invokeMutationInProd_GraphQLServiceEUdpIT() throws Exception {
        Response res = GqlServiceTestHelper.sendRequest(prodInvokeURL, MUTATION, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeMutationInProd_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void waitForObservabilityLogs_GraphQLServiceEUdpIT() throws Exception {
        en = GraphQL.getNamespaceForEnvironment(projectId, accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = choreoComponent.getEnvironment(en, Constant.Environment.Production);
        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        choreoComponent.waitForObservabilityLogs(prodEnv, accessToken);
    }

    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceEUdpIT"})
    @CitrusTest
    public void testGroupedLogs_GraphQLServiceEUdpIT(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityLogs observabilityLogs = ObservabilityService.getGroupLogs(releaseId, namespace, accessToken);
 Assert.assertTrue(observabilityLogs.getRows().length > 0);
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
