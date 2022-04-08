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

package com.wso2.choreo.integration.tests.getCommitList;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
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
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

/**
 * git commit list related tests
 */
public class GetCommitListIT extends TestNGCitrusSpringSupport {

  private static String componentId;
  private static String accessToken;
  private static String projectsAPIAccessToken;

  @Autowired
  private HttpClient choreoProjectsTestClient;

  @BeforeClass
  public void beforeClass()
      throws IOException, InterruptedException, ProjectCreationException, ComponentCreationStatusCheckException,
      ComponentCreationException, ComponentRetrieveException, ComponentCreationTimeoutException,
      TokenRetrievalException {
    TokenHandler tokenHandler = new TokenHandler();
    accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
    projectsAPIAccessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestTokenForCPAPIs());
    ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
        String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
    ChoreoProject project = org.createProject(projectsAPIAccessToken);
    RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
    RestApiChoreoComponent restApiComponent = (RestApiChoreoComponent) project
        .createChoreoComponent(restApiComponentBuilder, accessToken, projectsAPIAccessToken);
    componentId = restApiComponent.getId();
  }

  @Test
  @CitrusTest
  public void testGetCommitList() throws JsonProcessingException {
    HashMap<String, String> gqlRequestPayload = new HashMap<>() {
      {
        put("query", "query {" +
            "      commitHistory(componentId: \"" + componentId + "\") {" +
            "          author {" +
            "            name," +
            "            date," +
            "            email," +
            "            avatarUrl" +
            "          }," +
            "        message" +
            "        sha" +
            "        isLatest" +
            "    }" +
            "  }");
      }
    };
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
    $(http()
        .client(choreoProjectsTestClient)
        .send()
        .post("/graphql")
        .message()
        .header(HttpHeaders.AUTHORIZATION, projectsAPIAccessToken)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(requestBody)
        .accept(String.valueOf(MediaType.APPLICATION_JSON)));
    $(http()
        .client(choreoProjectsTestClient)
        .receive()
        .response(HttpStatus.OK)
        .message()
        .name("createComponent")
        .type(MessageType.JSON)
        .body(new ClassPathResource("templates/getCommitList/query_get_commit_list_success.json"))
        .validate(json()
            .ignore("$.data.commitHistory[0].author.date")
            .ignore("$.data.commitHistory[0].author.avatarUrl")
            .ignore("$.data.commitHistory[0].sha")));
  }

}
