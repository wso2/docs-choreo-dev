package com.wso2.choreo.integration.tests.createTrigger;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.consol.citrus.message.MessageType;

import org.springframework.core.io.ClassPathResource;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * $(http()
 *
 * trigger creation related tests
 */
public class CreateTriggerIT extends TestNGCitrusSpringSupport {

    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String projectId;
    private String componentId;
    private static String componentHandler;
    private static ChoreoComponent testComponent;
    String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void beforeClass()
            throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
        ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
        orgHandle = org.getOrgHandle();
        orgId = org.getOrgId();
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();
    }

    @Test
    @CitrusTest
    public void testTriggerComponent() throws JsonProcessingException {
        String graphQlQuery = "mutation{ createComponent(" +
                "      component: {" +
                "        name: \"" + componentName + "\"," +
                "        orgId: " + orgId + "," +
                "        orgHandler: \"" + orgHandle + "\"," +
                "        displayName: \"" + componentName + "\"," +
                "        displayType: \"webhook\"," +
                "        projectId: \"" + projectId + "\"," +
                "        labels: \"\"," +
                "        version: \"1.0.0\"," +
                "        description: \"\"," +
                "        apiId: \"\"," +
                "        ballerinaVersion: \"swan-lake-alpha5\"," +
                "        triggerChannels: \"IssuesService\"," +
                "        triggerID: 25," +
                "        httpBase: true," +
                "        sampleTemplate: \"\"" +
                "      }){" +
                "        id, orgId, projectId, handler" +
                "      }}";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        $(http()
                .client(choreoTestClient)
                .send()
                .post("/graphql")
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/createComponent/mutation_create_component_success.json"))
                .validate(json()
                        .ignore("$.data.createComponent.id")
                        .ignore("$.data.createComponent.handler")
                        .ignore("$.data.createComponent.projectId"))
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("createComponent");
                    componentHandler = component.get("handler").getAsString();
                    componentId = component.get("id").getAsString();
                }));
    }

    @Test(dependsOnMethods = { "testTriggerComponent" })
    @CitrusTest
    public void testTriggerCreatedComponentStatus() throws InterruptedException {
        $(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(choreoTestClient)
                                .send()
                                .get("/orgs/"
                                        .concat(orgHandle)
                                        .concat("/projects/")
                                        .concat(projectId)
                                        .concat("/components/")
                                        .concat(componentId)
                                        .concat("/init/status"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(choreoTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/createComponent/get_create_status_success.json"))
                                .validate(json()
                                        .ignore("$.message"))));
    }

    @Test(dependsOnMethods = {"testTriggerCreatedComponentStatus"})
    @CitrusTest
    public void testTriggerRetrieve() throws  IOException, InterruptedException, ComponentRetrieveException {
        String graphQlQuery = "query{" +
                "      component(" +
                "        projectId: \"" + projectId + "\"" +
                "        componentHandler: \"" + componentHandler + "\"" +
                "      ){" +
                "        id," +
                "        name," +
                "        handler," +
                "        description," +
                "        displayType," +
                "        displayName," +
                "        ownerName," +
                "        orgId," +
                "        orgHandler," +
                "        version," +
                "        labels," +
                "        createdAt," +
                "        updatedAt," +
                "        projectId," +
                "        apiId," +
                "        repository{" +
                "          nameApp," +
                "          nameConfig," +
                "          branch," +
                "          organizationApp," +
                "          organizationConfig," +
                "          isUserManage" +
                "        }," +
                "        apiVersions{" +
                "          apiVersion," +
                "          proxyName," +
                "          proxyUrl," +
                "          proxyId," +
                "          id," +
                "          state," +
                "          latest," +
                "          branch," +
                "          appEnvVersions{" +
                "            environmentId," +
                "            releaseId," +
                "            release{" +
                "              id," +
                "              metadata{" +
                "                choreoEnv" +
                "              }," +
                "              environmentId," +
                "              environment," +
                "              gitHash," +
                "              gitOpsHash," +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        $(http()
                .client(choreoTestClient)
                .send()
                .post("/graphql")
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("component");
                    Gson gson = new Gson();
                    testComponent =  gson.fromJson(component.toString(), RestApiChoreoComponent.class);
                }));
    }

    @Test(dependsOnMethods = { "testTriggerRetrieve" })
    @CitrusTest
    public void testTriggerDeploy() throws IOException, NoLatestApiVersionFoundException, NoLatestAppEnvIdFoundException,
            GetCommitHistoryException, InterruptedException, NoLatestCommitHashFoundException {
        JsonArray commitHistory = testComponent.getCommitHistory(accessToken);
        String latestCommitSha = testComponent.getLatestCommitHash(commitHistory);
        String latestVersionId = testComponent.getLatestApiVersion().getId();
        String devEnvIdToDeploy = testComponent.getLatestAppEnvId("dev");
        String branch = testComponent.getRepository().getBranch();
        HashMap<String, String> requestBodyMap = new HashMap<>() {
            {
                put("componentId", componentId);
                put("versionId", latestVersionId);
                put("envId", devEnvIdToDeploy);
                put("sha", latestCommitSha);
                put("branch", branch);
            }
        };
        String requestURI = "".concat("/orgs/").concat(orgHandle).concat("/projects/").concat(projectId)
                .concat("/triggers/deployment");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        $(http()
                .client(choreoTestClient)
                .send()
                .post(requestURI)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/deploy/post_deploy_success.json")));
    }

}
