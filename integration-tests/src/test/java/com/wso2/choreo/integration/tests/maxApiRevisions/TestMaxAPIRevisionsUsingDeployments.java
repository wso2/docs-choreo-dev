/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.tests.maxApiRevisions;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.wso2.choreo.integration.config.Constant.MAX_API_REVISIONS_LIMIT_DEPLOYMENTS;

/**
 * Create revision to exceed API revision limit reached with deployments
 */
public class TestMaxAPIRevisionsUsingDeployments extends TestNGCitrusSpringSupport {

    private String accessToken;
    private ChoreoComponent component;
    private String orgUuid;
    private String orgHandle;
    private String projectId;
    private String componentId;
    private String environmentId;
    private String versionId;
    private String apiId;
    private String releaseId;

    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    private HttpClient choreoTestClientForSTS;
    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void beforeClass() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        String componentName = "maxApiRevisionsUsingDeployments";
        // Access a reusable component which has deployed 20 times to reach API revision limit (i.e. 20 revisions)
        component = ComponentUtils.getReusableComponent(
                TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(), componentName.toLowerCase());

        ChoreoOrganization org = component.getOrganization();
        orgUuid = org.getOrgUUID();
        orgHandle = org.getOrgHandle();

        projectId = component.getProjectId();
        componentId = component.getId();
        environmentId = component.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        versionId = component.getLatestApiVersion().getId();
    }

    @Test
    @CitrusTest(name = "Create revision using a deployment to exceed API revision limit")
    public void createRevisionUsingDeploymentToExceedApiRevisionLimit() throws Exception {
        // Each deployment creates a new revision.
        component.deploy(accessToken, orgHandle, orgUuid);

        JsonArray deploymentArray = component.getDeployments(accessToken, orgHandle, orgUuid, versionId);
        JsonObject deployment = (JsonObject) deploymentArray.get(0);
        apiId = deployment.get("apiId").getAsString();
        releaseId = deployment.get("releaseId").getAsString();

        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", MAX_API_REVISIONS_LIMIT_DEPLOYMENTS);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/get_revisions_success.mustache",
                responseParams);

        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

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
                        .ignore("$.list")));
    }

    @Test(dependsOnMethods = {"createRevisionUsingDeploymentToExceedApiRevisionLimit"})
    @CitrusTest(name = "Create revision using Settings page to exceed API revision limit")
    public void createRevisionUsingSettingsPageToExceedApiRevisionLimit() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("API_ID", this.apiId);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/api_revision_limit_exceed_error.mustache",
                responseParams);

        // A call is made to create a backup revision initially when using Settings page to create a revision.
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("description", "backup revision");
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
                .response(HttpStatus.BAD_REQUEST)
                .message()
                .type(MessageType.JSON)
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.moreInfo")
                        .ignore("$.error")));
    }

    @Test(dependsOnMethods = {"createRevisionUsingSettingsPageToExceedApiRevisionLimit"})
    @CitrusTest(name = "Verify revision count after exceeding API revision limit")
    public void verifyRevisionCountAfterExceedingApiRevisionLimit() throws Exception {
        // Total revision count is maintained at API revision limit of deployments (i.e. 20).
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", MAX_API_REVISIONS_LIMIT_DEPLOYMENTS);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/get_revisions_success.mustache",
                responseParams);

        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

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
                        .ignore("$.list")));
    }

    @AfterClass
    public void afterClass() throws Exception {
        component.undeploy(accessToken, componentId, releaseId, orgHandle);
    }
}
