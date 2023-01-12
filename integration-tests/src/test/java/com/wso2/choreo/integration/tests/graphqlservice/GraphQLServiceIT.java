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
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
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
import static org.hamcrest.Matchers.everyItem;

public class GraphQLServiceIT extends TestNGCitrusSpringSupport {

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
    public void setup_GraphQLServiceIT() throws Exception {
        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceIT() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").srcGitRepoUrl(GitHub.getGitHubRepoUrl(repoName)).projectId(projectId).displayType(Constant.displayType.graphql.name()).build();
        choreoComponent = GraphQL.createUserManagedComponent(dto, accessToken);
        Assert.assertNotNull(choreoComponent.getId());
    }


    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceIT"})
    @CitrusTest
    public void createdComponentStatus_GraphQLServiceIT() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, choreoComponent.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }


    @Test(dependsOnMethods = {"createdComponentStatus_GraphQLServiceIT"})
    @CitrusTest
    public void initialPRGeneration_GraphQLServiceIT() throws IOException, UnexpectedResponseException {

    }

    @Test(dependsOnMethods = {"initialPRGeneration_GraphQLServiceIT"})
    @CitrusTest
    public void mergePR_GraphQLServiceIT() throws IOException, UnexpectedResponseException {

    }


    @Test(dependsOnMethods = {"mergePR_GraphQLServiceIT"})
    @CitrusTest
    public void mergeNewCode_GraphQLServiceIT() throws IOException {
        String encodedContent = FileUtil.readFileEncodedContent("src/test/resources/templates/encodedbal/gql.bal");
        String balToml = FileUtil.readFileEncodedContent("src/test/resources/templates/encodedbal/jwt/Ballerina.toml");
        GitHub.createNewFile(repoName, "Ballerina.toml", balToml);
        GitHub.createNewFile(repoName, "sample.bal", encodedContent);
    }

    @Test(dependsOnMethods = {"mergeNewCode_GraphQLServiceIT"})
    @CitrusTest
    public void componentRetrieval_GraphQLServiceIT() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandler(), accessToken);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_GraphQLServiceIT"})
    @CitrusTest
    public void addDeploymentConfiguration_GraphQLServiceIT() throws Exception {
        Orgs.getConfigurationMapping(choreoComponent, accessToken);
        Orgs.addConfiguration(choreoComponent, Constant.DEV_ENVIRONMENT, accessToken);
    }

    @Test(dependsOnMethods = {"addDeploymentConfiguration_GraphQLServiceIT"})
    @CitrusTest
    public void deploy_GraphQLServiceIT() throws Exception {

    }

    @Test(dependsOnMethods = {"deploy_GraphQLServiceIT"})
    @CitrusTest
    public void deploymentStatusByVersion_GraphQLServiceIT() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_GraphQLServiceIT"})
    @CitrusTest
    public void componentDevDeploymentStatus_GraphQLServiceIT() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT, accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_GraphQLServiceIT"})
    @CitrusTest
    public void addPromoteConfiguration_GraphQLServiceIT() throws Exception {
        Response res = Orgs.addConfiguration(choreoComponent, Constant.PROD_ENVIRONMENT, accessToken);
        Orgs.getConfigurationMapping(choreoComponent, accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_GraphQLServiceIT"})
    @CitrusTest
    public void promote_GraphQLServiceIT() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceIT"})
    @CitrusTest
    public void componentProdDeploymentStatus_GraphQLServiceIT() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, Constant.PROD_ENVIRONMENT, accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentProdDeploymentStatus_GraphQLServiceIT"})
    @CitrusTest
    public void invokeQueryInDev_GraphQLServiceIT() throws Exception {
        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        Response res = GqlServiceTestHelper.sendRequest(devInvokeURL, QUERY, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_GraphQLServiceIT"})
    @CitrusTest
    public void invokeQueryInProd_GraphQLServiceIT() throws Exception {
        Response res = GqlServiceTestHelper.sendRequest(prodInvokeURL, QUERY, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_GraphQLServiceIT"})
    @CitrusTest
    public void invokeMutationInDev_GraphQLServiceIT() throws Exception {
        Response res = GqlServiceTestHelper.sendRequest(devInvokeURL, MUTATION, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_GraphQLServiceIT"})
    @CitrusTest
    public void invokeMutationInProd_GraphQLServiceIT() throws Exception {
        Response res = GqlServiceTestHelper.sendRequest(prodInvokeURL, MUTATION, apiKey);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeMutationInProd_GraphQLServiceIT"})
    @CitrusTest
    public void waitForObservabilityLogs_GraphQLServiceIT() throws Exception {
        en = GraphQL.getNamespaceForEnvironment(projectId, accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = choreoComponent.getEnvironment(en, Constant.Environment.Production);
        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        choreoComponent.waitForObservabilityLogs(prodEnv, accessToken);
    }

    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceIT"})
    @CitrusTest
    public void testGroupedLogs_GraphQLServiceIT(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityService.getGroupLogs(releaseId, namespace, accessToken);
    }

    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_GraphQLServiceIT"})
    @CitrusTest
    public void testLiveLogs_GraphQLServiceIT(Constant.Environment env) throws Exception {
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
