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

package com.wso2.choreo.integration.tests.addConfigurations;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import com.google.gson.JsonArray;
import java.util.ArrayList;

/**
 * add configurations related tests
 */
public class AddConfigurationsIT extends TestNGCitrusSpringSupport {

  private String orgHandler;
  private String projectId;
  private String componentId;
  private String accessToken;
  private String latestCommitSha;
  private String latestVersionId;
  private String devEnvIdToDeploy;
  private String name;

  @Autowired
  private HttpClient choreoTestClient;

  @BeforeClass
  public void beforeClass()
      throws IOException, InterruptedException, ProjectCreationException, ComponentCreationStatusCheckException,
      ComponentCreationException, ComponentRetrieveException, ComponentCreationTimeoutException,
      GetCommitHistoryException, NoLatestCommitHashFoundException, NoLatestAppEnvIdFoundException,
      ComponentCreationStatusCheckException,
      TokenRetrievalException, NoLatestApiVersionFoundException {
    TokenHandler tokenHandler = new TokenHandler();
    accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
    ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
        String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
    orgHandler = org.getOrgHandle();
    ChoreoProject project = org.createProject(accessToken);
    projectId = project.getId();

    RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
    RestApiChoreoComponent restApiComponent = (RestApiChoreoComponent) project
        .createChoreoComponent(restApiComponentBuilder, accessToken);
    componentId = restApiComponent.getId();
    JsonArray commitHistory = restApiComponent.getCommitHistory(accessToken);
    latestCommitSha = restApiComponent.getLatestCommitHash(commitHistory);
    latestVersionId = restApiComponent.getLatestApiVersion().getId();
    devEnvIdToDeploy = restApiComponent.getLatestAppEnvId("dev");
    name = restApiComponent.getName();
  }

  @Test
  @CitrusTest
  public void testAddConfigurations() throws JsonProcessingException {
    HashMap<String, Object> requestBodyMap = new HashMap<>() {
      {
        put("applyNow", false);
        put("commitHash", latestCommitSha);
        put("configs", new ArrayList<>());
        put("moduleName", name);
        put("operation", 0);
        put("sourceUuid", "");
      }
    };
    String requestURI = "".concat("/orgs/").concat(orgHandler).concat("/projects/").concat(projectId)
        .concat("/components/".concat(componentId).concat("/envs/").concat(devEnvIdToDeploy).concat("/")
            .concat(latestVersionId).concat("/configurations"));
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
        .response(HttpStatus.OK));
  }

}
