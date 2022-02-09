package com.wso2.choreo.integration.tests.createComponent;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeTest;
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
 * component creation related tests
 */
public class CreateComponentIT extends TestNGCitrusSpringSupport {

  private static String accessToken;
  private String orgHandle;
  private String orgId;
  private String projectId;
  private String componentId;

  @Autowired
  private HttpClient choreoTestClient;

  @BeforeTest
  public void beforeSuite()
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
  public void testCreateRESTComponent() throws JsonProcessingException {
    String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
    String graphQlQuery = "mutation{ createComponent(" +
        "      component: {" +
        "        name: \"" + componentName + "\"," +
        "        orgId: " + orgId + "," +
        "        orgHandler: \"" + orgHandle + "\"," +
        "        displayName: \"" + componentName + "\"," +
        "        displayType: \"" + Constant.displayType.restAPI + "\"," +
        "        projectId: \"" + projectId + "\"," +
        "        labels: \"\"," +
        "        version: \"1.0.0\"," +
        "        description: \"\"," +
        "        apiId: \"\"," +
        "        ballerinaVersion: \"swan-lake-alpha5\"," +
        "        triggerChannels: \"\"," +
        "        triggerID: null," +
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
          componentId = component.get("id").getAsString();
        }));
  }

  @Test(dependsOnMethods = { "testCreateRESTComponent" })
  @CitrusTest
  public void testCreatedComponentStatus() throws InterruptedException {
    $(repeatOnError()
        .until("i = 15")
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
}
