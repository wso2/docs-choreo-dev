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
import com.wso2.choreo.integration.apis.proxydeployer.ProxyDeployer;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;
import com.wso2.choreo.integration.models.proxyapi.DeploymentStatus;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

public class TestBasicAPIRevisionCreation extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private ProxyAPI proxyAPI;
    private String apiId;
    private String versionId;
    private String newRevisionId;
    private String oldRevisionId;
    private String orgUuid;
    Environment[] environments;
    Environment devEnv;
    ChoreoComponent choreoComponent;
    ProxyAPIBuild proxyAPIBuild;

    @Autowired
    private HttpClient choreoTestClientForSTS;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @Test
    @CitrusTest
    public void setup_TestBasicAPIRevisionCreation() throws IOException, TokenRetrievalException, NoLatestApiVersionFoundException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        ChoreoProject testProject = GraphQL.createProject(accessToken);
        String projectId = testProject.getId();

        String firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        String firstContext = APICreator.generateContext(firstAPIName);
        proxyAPI = APICreator.createAPI(firstAPIName, firstContext, accessToken).getEntity();
        Assert.assertNotNull(proxyAPI.getId());
        this.apiId = proxyAPI.getId();

        ProxyResponse<ChoreoComponent> response = GraphQL.createGraphqlQueryForComponentCreation(firstAPIName,
                projectId, this.apiId, accessToken);
        choreoComponent = response.getEntity();
        Assert.assertEquals(response.getResponse().getStatusCode(), HttpStatus.OK.value());

        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_CP_PROJECTS_ENDPOINT);
        choreoComponent = GraphQL.getComponentDetails(this, cpProjectsClient, projectId,  choreoComponent.getHandler(), accessToken);
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

    @Test(dependsOnMethods = {"setup_TestBasicAPIRevisionCreation"})
    @CitrusTest
    public void createNewRevision_TestBasicAPIRevisionCreation() throws Exception {

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

    @Test(dependsOnMethods = {"createNewRevision_TestBasicAPIRevisionCreation"})
    @CitrusTest
    public void verifyCreateNewRevision_TestBasicAPIRevisionCreation() throws Exception {
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
                    JsonObject apiInfo = deployedRevisionObject.get("apiInfo").getAsJsonObject();
                    versionId = apiInfo.get("id").getAsString();
                    if (deploymentInfoArray.size() != 0) {
                        JsonObject deploymentInfo = deploymentInfoArray.get(0).getAsJsonObject();
                        oldRevisionId = deploymentInfo.get("revisionUuid").getAsString();
                    }}));
    }

    @Test(dependsOnMethods = {"verifyCreateNewRevision_TestBasicAPIRevisionCreation"})
    @CitrusTest
    public void deployNewRevision_TestBasicAPIRevisionCreation() throws Exception {
        String buildId = proxyAPIBuild.getBuilds()[0].getBuildId();
        DeploySettings res = APICreator.deployRevision(choreoComponent.getId(), versionId, devEnv.getId(), orgUuid,
                newRevisionId, buildId, apiId, accessToken);
        Assert.assertEquals(res.getMessage(), "Settings deployment started");
        HttpClient choreoEPClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        ProxyDeployer.getProxyAPIDeploymentStatus(this, choreoEPClient, accessToken, choreoComponent.getId(),
                choreoComponent.getLatestApiVersion().getId(), res.getRequestId());
    }

    @Test(dependsOnMethods = {"deployNewRevision_TestBasicAPIRevisionCreation"})
    @CitrusTest
    public void verifyDeployNewRevision_TestBasicAPIRevisionCreation() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", 3);

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

                    JsonObject newDeployedRevisionObject  = list.get(2).getAsJsonObject();
                    JsonArray newDeploymentInfoArray = newDeployedRevisionObject.get("deploymentInfo").getAsJsonArray();
                    Assert.assertTrue(newDeploymentInfoArray.size() > 0);
                }));
    }

    @Test(dependsOnMethods = {"verifyDeployNewRevision_TestBasicAPIRevisionCreation"})
    @CitrusTest
    public void deployOldRevision_TestBasicAPIRevisionCreation() throws Exception {
        String buildId = proxyAPIBuild.getBuilds()[0].getBuildId();
        DeploySettings res = APICreator.deployRevision(choreoComponent.getId(), versionId, devEnv.getId(), orgUuid,
                oldRevisionId, buildId, apiId, accessToken);
        Assert.assertEquals(res.getMessage(), "Settings deployment started");
        HttpClient choreoEPClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        ProxyDeployer.getProxyAPIDeploymentStatus(this, choreoEPClient, accessToken, choreoComponent.getId(),
                choreoComponent.getLatestApiVersion().getId(), res.getRequestId());
    }

    @Test(dependsOnMethods = {"deployOldRevision_TestBasicAPIRevisionCreation"})
    @CitrusTest
    public void verifyRedeployOldRevision_TestBasicAPIRevisionCreation() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", 4);

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
                    JsonObject oldDeployedRevisionObject  = list.get(3).getAsJsonObject();
                    JsonArray oldDeploymentInfoArray = oldDeployedRevisionObject.get("deploymentInfo").getAsJsonArray();
                    Assert.assertTrue(oldDeploymentInfoArray.size() > 0);
                }));
    }
}
