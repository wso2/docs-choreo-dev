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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.APIRevisionLimitExceedException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.GetDeploymentsStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.wso2.choreo.integration.config.Constant.MAX_API_REVISIONS_LIMIT_DEPLOYMENTS;

public class CreateMaxAPIRevisionsUsingDeployments extends TestNGCitrusSpringSupport {

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

        String projectName = "max-revisions-test-project";
        String componentName = "maxApiRevisions";
        component = ComponentUtils.getReusableComponentForProject(
                TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(), componentName, projectName);

        ChoreoOrganization org = component.getOrganization();
        orgUuid = org.getOrgUUID();
        orgHandle = org.getOrgHandle();

        projectId = component.getProjectId();
        componentId = component.getId();
        environmentId = component.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        versionId = component.getLatestApiVersion().getId();
    }

    @Test
    @CitrusTest(name = "Create deployment to exceed API revision limit")
    public void createDeploymentToExceedApiRevisionLimit() throws APIRevisionLimitExceedException {
        try {
            component.deploy(accessToken, orgHandle, orgUuid);

            JsonArray deploymentArray = component.getDeployments(accessToken, orgHandle, orgUuid, versionId);
            JsonObject deployment = (JsonObject) deploymentArray.get(0);
            apiId = deployment.get("apiId").getAsString();
            releaseId = deployment.get("releaseId").getAsString();
        } catch (IOException | InterruptedException | NoLatestAppEnvIdFoundException | ComponentDeploymentException |
                 ComponentDeploymentStatusCheckException | NoLatestCommitHashFoundException |
                 GetCommitHistoryException | ComponentDeploymentTimeoutException | NoLatestApiVersionFoundException |
                 ComponentDeploymentFailureException | GetDeploymentsStatusCheckException e) {
            throw new APIRevisionLimitExceedException(e);
        }

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
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    int count = component.get("count").getAsInt();
                    Assert.assertEquals(count, MAX_API_REVISIONS_LIMIT_DEPLOYMENTS);
                }));
    }

    @Test(dependsOnMethods = {"createDeploymentToExceedApiRevisionLimit"})
    @CitrusTest(name = "Create backup revision for existing state")
    public void createBackupRevisionForExistingState() throws JsonProcessingException {
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
                .body(new ClassPathResource(
                        "templates/maxApiRevisions/api_revision_limit_exceed_error.json"))
                .validate(json()
                        .ignore("$.description")
                        .ignore("$.moreInfo")
                        .ignore("$.error"))
                .type(MessageType.JSON));
    }

    @Test(dependsOnMethods = {"createBackupRevisionForExistingState"})
    @CitrusTest(name = "Create API revision using Settings page to exceed API revision limit")
    public void createRevisionUsingSettingsPageToExceedApiRevisionLimit() {
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
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    int count = component.get("count").getAsInt();
                    Assert.assertEquals(count, MAX_API_REVISIONS_LIMIT_DEPLOYMENTS);
                }));
    }

    @AfterClass
    public void afterClass() throws Exception {
        component.undeploy(accessToken, componentId, releaseId, orgHandle);
    }
}
