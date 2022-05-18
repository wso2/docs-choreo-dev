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
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
  private String latestCommitSha;
  private String latestVersionId;
  private String devEnvIdToDeploy;
  private String name;

  @Autowired
  private HttpClient choreoTestClient;

  @BeforeClass
  public void beforeClass() throws Exception {
    ChoreoComponent component = ComponentUtils.getReusableComponent(
            TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(), AddConfigurationsIT.class.getSimpleName());

    orgHandler = component.getOrgHandler();
    projectId = component.getProjectId();
    componentId = component.getId();
    JsonArray commitHistory = component.getCommitHistory(TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs());
    latestCommitSha = component.getLatestCommitHash(commitHistory);
    latestVersionId = component.getLatestApiVersion().getId();
    devEnvIdToDeploy = component.getLatestAppEnvId("dev");
    name = component.getName();
  }


  @Test
  @CitrusTest
  public void testAddConfigurations() throws Exception {
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
        .header(HttpHeaders.AUTHORIZATION, TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .contentType(String.valueOf(MediaType.APPLICATION_JSON))
        .body(requestBody)
        .accept(String.valueOf(MediaType.APPLICATION_JSON)));
    $(http()
        .client(choreoTestClient)
        .receive()
        .response(HttpStatus.OK));
  }
}
