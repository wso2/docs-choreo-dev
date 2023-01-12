package com.wso2.choreo.integration.tests.maxApiRevisions;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

public class APIRevisions extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String projectId;
    private static String firstAPIName;
    private static String firstContext;
    private ProxyAPI proxyAPI;
    private String apiId;
    private String releaseId;
    private String componentId;
    private String versionId;
    private String newRevisionId;
    private String oldRevisionId;
    private String orgUuid;
    private String orgHandle;
    Environment[] environments;
    Environment devEnv;
    ChoreoComponent choreoComponent;
    ProxyAPIBuild proxyAPIBuild;
    private String deploymentName;
    private String deploymentVHost;
    private Boolean deploymentDisplayOnDevportal;

    @Autowired
    private HttpClient choreoTestClientForSTS;

    @BeforeClass
    public void setup_APIRevisions() throws IOException, TokenRetrievalException, NoLatestApiVersionFoundException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);

        ChoreoProject testProject = GraphQL.createProject(accessToken);
        projectId = testProject.getId();

        firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        firstContext = APICreator.generateContext(firstAPIName);
        proxyAPI = APICreator.createAPI(firstAPIName, firstContext, accessToken).getEntity();
        Assert.assertNotNull(proxyAPI.getId());
        this.apiId = proxyAPI.getId();

        ProxyResponse<ChoreoComponent> response = GraphQL.createGraphqlQueryForComponentCreation(firstAPIName,
                projectId, this.apiId, accessToken);
        choreoComponent = response.getEntity();
        Assert.assertEquals(response.getResponse().getStatusCode(), HttpStatus.OK.value());

        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandler(), accessToken);
        Assert.assertNotNull(choreoComponent);

        environments = GraphQL.getComponentDeploymentEnvironment(projectId, accessToken);

        devEnv = choreoComponent.getEnvironment(environments, Constant.Environment.Development);
        ProxyResponse<Status> statusProxyResponse = APICreator.initiateDeployment(choreoComponent.getId(),
                choreoComponent.getLatestApiVersion().getId(), devEnv.getId(), accessToken);
        Assert.assertEquals(statusProxyResponse.getResponse().getStatusCode(), HttpStatus.OK.value());
        Assert.assertTrue(statusProxyResponse.getEntity().isSuccess());

        proxyAPIBuild = APICreator.getAPIBuilds(choreoComponent.getId(), choreoComponent.getLatestApiVersion().getId(),
                accessToken);
        String buildId = proxyAPIBuild.getBuilds()[0].getBuildId();
        ProxyResponse<Status> res = APICreator.deployProxyAPI(choreoComponent.getId(), this.apiId, buildId,
                devEnv.getId(), accessToken);
        Assert.assertEquals(res.getResponse().getStatusCode(), HttpStatus.OK.value());
    }

    @Test
    @CitrusTest(name = "Create new revision")
    public void createNewRevision() throws Exception {

        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("API_ID", proxyAPI.getId());

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/create_new_revision_success.mustache",
                responseParams);

        String path = Constant.APIS_ENDPOINT.concat("/").concat(proxyAPI.getId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=")
                .concat(this.orgUuid);
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("description", "new revision");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                .body(requestBody));
        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.displayName")
                        .ignore("$.id")
                        .ignore("$.createdTime"))
                .extract((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    this.newRevisionId = component.get("id").getAsString();
                }));
    }

    @Test(dependsOnMethods = {"createNewRevision"})
    @CitrusTest(name = "Verify if the new revision is listed")
    public void verifyCreateNewRevision() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", 2);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/get_revisions_success.mustache",
                responseParams);

        String path = Constant.APIS_ENDPOINT.concat("/").concat(proxyAPI.getId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=")
                .concat(this.orgUuid);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .get(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.list"))
                .extract((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    JsonArray list = component.get("list").getAsJsonArray();

                    JsonObject deployedRevisionObject  = list.get(0).getAsJsonObject();
                    JsonArray deploymentInfoArray = deployedRevisionObject.get("deploymentInfo").getAsJsonArray();
                    if (deploymentInfoArray.size() != 0) {
                        JsonObject deploymentInfo = deploymentInfoArray.get(0).getAsJsonObject();
                        this.oldRevisionId = deploymentInfo.get("revisionUuid").getAsString();
                        this.deploymentName = deploymentInfo.get("name").getAsString();
                        this.deploymentVHost = deploymentInfo.get("vhost").getAsString();
                        this.deploymentDisplayOnDevportal = deploymentInfo.get("displayOnDevportal").getAsBoolean();
//                        this.releaseId = deploymentInfo.get("releaseId").getAsString();

                    }}));
    }

    @Test(dependsOnMethods = {"verifyCreateNewRevision"})
    @CitrusTest(name = "Deploy new revision and verify")
    public void deployNewRevision() throws Exception {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("deploy-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid)
                .concat("&").concat("revisionId").concat("=").concat(this.newRevisionId);

        String deploymentName = this.deploymentName;
        String deploymentVHost = this.deploymentVHost;
        Boolean deploymentDisplayOnDevportal = true;
        List<HashMap<String, Object>> requestBodyMapList = new ArrayList<>();
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("name", deploymentName);
                put("vhost", deploymentVHost);
                put("displayOnDevportal", deploymentDisplayOnDevportal);
            }
        };
        requestBodyMapList.add(requestBodyMap);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMapList);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                .body(requestBody));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource(
                        "templates/maxApiRevisions/deploy_revision_success.json"))
                .validate(json()));
    }

    @Test(dependsOnMethods = {"deployNewRevision"})
    @CitrusTest(name = "Re deploy old revision and verify")
    public void deployOldRevision() throws Exception {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("deploy-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid)
                .concat("&").concat("revisionId").concat("=").concat(this.oldRevisionId);

        String deploymentName = this.deploymentName;
        String deploymentVHost = this.deploymentVHost;
        Boolean deploymentDisplayOnDevportal = this.deploymentDisplayOnDevportal;
        List<HashMap<String, Object>> requestBodyMapList = new ArrayList<>();
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("name", deploymentName);
                put("vhost", deploymentVHost);
                put("displayOnDevportal", deploymentDisplayOnDevportal);
            }
        };
        requestBodyMapList.add(requestBodyMap);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMapList);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                .body(requestBody));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource(
                        "templates/maxApiRevisions/redeploy_revision_success.json"))
                .validate(json()));
    }
}
