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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.consol.citrus.message.MessageType;
import org.springframework.core.io.ClassPathResource;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * $(http()
 * 
 * Connector publishing related tests
 */
public class CreateProjectIT extends TestNGCitrusSpringSupport {

  private static String accessToken;
  private String orgHandle;
  private String orgId;

  @Autowired
  private HttpClient choreoTestClient;

  @BeforeSuite
  public void beforeSuite()
      throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
    TokenHandler tokenHandler = new TokenHandler();
    accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
    ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
        String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
    orgHandle = org.getOrgHandle();
    orgId = org.getOrgId();
  }

  @Test
  @CitrusTest
  public void testCreateProject() throws JsonProcessingException {
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
        .body(new ClassPathResource("templates/createProject/mutation_create_project_success.json"))
        .validate(json()
            .ignore("$.data.createProject.id")
            .ignore("$.data.createProject.handler")
            .ignore("$.data.createProject.createdDate")
            .ignore("$.data.createProject.name")));
  }
}
