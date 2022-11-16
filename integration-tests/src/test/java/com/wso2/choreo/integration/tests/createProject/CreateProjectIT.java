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

package com.wso2.choreo.integration.tests.createProject;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.TestContext;
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
import java.util.Date;
import java.util.HashMap;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * $(http()
 * 
 * project creation related tests.
 */
public class CreateProjectIT extends TestNGCitrusSpringSupport {

  private static String accessToken;
  private String orgHandle;
  private String orgId;

  @Autowired
  private HttpClient choreoProjectsTestClient;

  @BeforeClass
  public void setup_CreateProjectIT()
      throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
    accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
  }

  @Test
  @CitrusTest
  public void createProject_CreateProjectIT() throws JsonProcessingException {
    String graphQlQuery = "mutation{ createProject(project: {" +
        "      name: \"" + Constant.TEST_PROJECT_NAME_PREFIX.concat(String.valueOf(new Date().getTime())) +
        "\", " +
        "      description: \"" + Constant.TEST_PROJECT_DESCRIPTION + "\"," +
        "      orgId: " + orgId + "," +
        "      orgHandler: \"" + orgHandle + "\"," +
        "      version: \"1.0.0\"," +
        "    }){ " +
        "      id, orgId, name, version, createdDate, handler," +
        "    } }";
    HashMap<String, String> gqlRequestPayload = new HashMap<>() {
      {
        put("query", graphQlQuery);
      }
    };
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
    $(http()
        .client(choreoProjectsTestClient)
        .send()
        .post("/graphql")
        .message()
        .header(HttpHeaders.AUTHORIZATION, accessToken)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(requestBody)
        .accept(String.valueOf(MediaType.APPLICATION_JSON)));

    $(http()
        .client(choreoProjectsTestClient)
        .receive()
        .response(HttpStatus.OK)
        .message()
        .type(MessageType.JSON)
        .body(new ClassPathResource("templates/createProject/mutation_create_project_success.json"))
        .validate(json()
            .ignore("$.data.createProject.id")
            .ignore("$.data.createProject.handler")
            .ignore("$.data.createProject.createdDate")
            .ignore("$.data.createProject.orgId")
            .ignore("$.data.createProject.name")));
  }
}
