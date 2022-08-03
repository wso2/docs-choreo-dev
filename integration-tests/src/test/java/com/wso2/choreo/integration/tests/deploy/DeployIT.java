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

package com.wso2.choreo.integration.tests.deploy;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.AddConfigurationsException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * deployment related tests
 */
public class DeployIT extends TestNGCitrusSpringSupport {

  private String orgHandle;
  private String orgUuid;
  private String projectId;
  private String componentId;
  private String accessToken;
  private String latestCommitSha;
  private String latestVersionId;
  private String devEnvIdToDeploy;
  private String branch;
  private RestApiChoreoComponent restApiComponent;

  @Autowired
  private HttpClient choreoTestClient;

  @BeforeClass
  public void beforeClass()
      throws IOException, InterruptedException, ProjectCreationException, ComponentCreationStatusCheckException,
      ComponentCreationException, ComponentRetrieveException, ComponentCreationTimeoutException,
      GetCommitHistoryException, NoLatestCommitHashFoundException, NoLatestAppEnvIdFoundException,
      ComponentCreationStatusCheckException, TokenRetrievalException, NoLatestApiVersionFoundException,
      AddConfigurationsException {
    accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
    orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
    ChoreoOrganization org = new ChoreoOrganization(orgHandle, orgId, orgUuid);

    ChoreoProject project = org.createProject(accessToken);
    projectId = project.getId();
    RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
    restApiComponent = (RestApiChoreoComponent) project.createChoreoComponent(accessToken, restApiComponentBuilder);
    componentId = restApiComponent.getId();
    restApiComponent.setOrganization(org);
    restApiComponent.addConfigurations(accessToken, org.getOrgHandle());
    JsonArray commitHistory = restApiComponent.getCommitHistory(accessToken);
    latestCommitSha = restApiComponent.getLatestCommitHash(commitHistory);
    latestVersionId = restApiComponent.getLatestApiVersion().getId();
    devEnvIdToDeploy = restApiComponent.getLatestAppEnvId("dev");
    branch = restApiComponent.getRepository().getBranch();
  }

  @Test
  @CitrusTest
  public void testAddDeploymentConfiguration() throws Exception {
    String configurationsUpdateRequestURI = "/orgs/".concat(orgHandle).concat("/projects/")
            .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
            .concat(devEnvIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");
    HashMap<String, Object> requestBodyMap = new HashMap<>() {
      {
        put("moduleName", restApiComponent.getName());
        put("commitHash", latestCommitSha);
        put("applyNow", false);
        put("operation", 0);
        put("sourceUuid", "");
        put("configs", "");
      }
    };

    ObjectMapper configurationsObjectMapper = new ObjectMapper();
    String configurationsRequestBody = configurationsObjectMapper.writeValueAsString(requestBodyMap);

    // Update configurations
    $(http()
            .client(choreoTestClient)
            .send()
            .post(configurationsUpdateRequestURI)
            .message()
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(configurationsRequestBody)
            .accept(String.valueOf(MediaType.APPLICATION_JSON)));

    $(http()
            .client(choreoTestClient)
            .receive()
            .response(HttpStatus.OK));
  }

  @Test(dependsOnMethods = { "testAddDeploymentConfiguration" })
  @CitrusTest
  public void testDeploy() throws JsonProcessingException {
    String graphQlQuery = "mutation {deployComponent(" +
            "        deployment: {" +
            "          componentId: \"" + componentId + "\"," +
            "          versionId: \"" + latestVersionId + "\"," +
            "          envId: \"" + devEnvIdToDeploy + "\"," +
            "          branch: \"" + branch + "\"," +
            "          sha: \"" + latestCommitSha + "\"," +
            "        }" +
            "      ) { message, success }}";
    HashMap<String, String> gqlRequestPayload = new HashMap<>() {
      {
        put("query", graphQlQuery);
      }
    };

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

    // Deploy component
    $(http()
            .client(choreoTestClient)
            .send()
            .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
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
            .body(new ClassPathResource("templates/deploy/gql_deploy_component_success.json"))
            .validate(json()));
  }

  @Test(dependsOnMethods = { "testDeploy" })
  @CitrusTest
  public void testDeploymentStatusByVersion() throws Exception {
    String graphQlQuery = "query {" +
            "      deploymentStatusByVersion(" +
            "        componentId: \"" + componentId + "\"," +
            "        versionId: \"" + latestVersionId + "\"" +
            "  ) {" +
            "    id" +
            "    sha" +
            "    completed_at" +
            "    started_at" +
            "    name" +
            "    status" +
            "    conclusion" +
            "  }" +
            "}";
    HashMap<String, String> gqlRequestPayload = new HashMap<>() {
      {
        put("query", graphQlQuery);
      }
    };
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

    // Poll deployment status
    $(repeatOnError()
            .until("i = 36")
            .index("i")
            .autoSleep(5000)
            .actions(
                    http()
                            .client(choreoTestClient)
                            .send()
                            .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .body(requestBody)
                            .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                    http().client(choreoTestClient)
                            .receive()
                            .response(HttpStatus.OK)
                            .message()
                            .body(new ClassPathResource(
                                    "templates/deploy/deploy_status_by_version_success.json"))));

  }

  @Test(dependsOnMethods = { "testDeploymentStatusByVersion" })
  @CitrusTest
  public void testComponentDeploymentStatus() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("orgHandler", orgHandle);
    params.put("orgUuid", orgUuid);
    params.put("componentId", componentId);
    params.put("versionId", latestVersionId);
    params.put("environmentId", devEnvIdToDeploy);

    String graphQlQuery = ComponentUtils.generateStringFromTemplate(
            "templates/deploy/graphql/componentDeployment.mustache", params);
    HashMap<String, String> gqlRequestPayload = new HashMap<>() {
      {
        put("query", graphQlQuery);
      }
    };
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

    Map<String, String> responseParams = new HashMap<>();
    responseParams.put("environmentId", devEnvIdToDeploy);
    responseParams.put("sha", latestCommitSha);
    responseParams.put("versionId", latestVersionId);

    String expectedResponse = ComponentUtils.generateStringFromTemplate(
            "templates/deploy/deploy_status_success.mustache", responseParams);

    // Poll deployment status
    $(repeatOnError()
            .until("i = 25")
            .index("i")
            .autoSleep(5000)
            .actions(
                    http()
                            .client(choreoTestClient)
                            .send()
                            .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .body(requestBody)
                            .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                    http().client(choreoTestClient)
                            .receive()
                            .response(HttpStatus.OK)
                            .message()
                            .body(expectedResponse)));
  }

  @Test(dependsOnMethods = { "testComponentDeploymentStatus" })
  @CitrusTest
  public void testAPIInvocation() throws Exception {
    restApiComponent.invokeGetApplication(accessToken, "restAPI", "Development", 1);
  }


}
