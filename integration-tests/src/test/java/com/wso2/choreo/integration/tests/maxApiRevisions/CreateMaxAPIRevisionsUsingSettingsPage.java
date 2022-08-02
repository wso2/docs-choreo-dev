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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.wso2.choreo.integration.config.Constant.MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE;

public class CreateMaxAPIRevisionsUsingSettingsPage extends TestNGCitrusSpringSupport {

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
    private String buildId;

    private String revisionIdToDelete;
    private String revisionIdToRestore;
    private String deploymentName;
    private String deploymentVHost;
    private Boolean deploymentDisplayOnDevportal;
    private String backupRevisionId;
    private String newRevisionId;

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
        String componentName = "maxApiRevisionsUsingSettingsPage";
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
    @CitrusTest(name = "Create deployment at API revision limit")
    public void createDeploymentAtApiRevisionLimit() throws APIRevisionLimitExceedException {
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
                    Assert.assertEquals(count, MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE);
                }));
    }

    @Test(dependsOnMethods = {"createDeploymentAtApiRevisionLimit"})
    @CitrusTest(name = "Get revision to delete")
    public void getRevisionToDelete() {
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
                    JsonArray list = component.get("list").getAsJsonArray();
                    JsonObject revisionObjectToDelete = list.get(0).getAsJsonObject();
                    String id = revisionObjectToDelete.get("id").getAsString();
                    this.revisionIdToDelete = id;

                    int count = component.get("count").getAsInt();
                    for (int i = 0; i < count; i++) {
                        JsonObject revisionObject  = list.get(i).getAsJsonObject();
                        JsonObject apiInfo = revisionObject.get("apiInfo").getAsJsonObject();
                        JsonArray deploymentInfoArray = revisionObject.get("deploymentInfo").getAsJsonArray();
                        if (deploymentInfoArray.size() != 0) {
                            JsonObject deploymentInfo = deploymentInfoArray.get(0).getAsJsonObject();
                            this.revisionIdToRestore = deploymentInfo.get("revisionUuid").getAsString();
                            this.deploymentName = deploymentInfo.get("name").getAsString();
                            this.deploymentVHost = deploymentInfo.get("vhost").getAsString();
                            this.deploymentDisplayOnDevportal = deploymentInfo.get("displayOnDevportal").getAsBoolean();
                            break;
                        }
                    }
                }));
    }

    @Test(dependsOnMethods = {"getRevisionToDelete"})
    @CitrusTest(name = "Delete oldest undeployed revision")
    public void deleteOldestUndeployedRevision() {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("/").concat(this.revisionIdToDelete)
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .delete(path)
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
                    JsonArray list = component.get("list").getAsJsonArray();
                    JsonObject revisionObjectToDelete = list.get(0).getAsJsonObject();
                    String id = revisionObjectToDelete.get("id").getAsString();
                    Assert.assertNotEquals(id, this.revisionIdToDelete);
                }));
    }

    @Test(dependsOnMethods = {"deleteOldestUndeployedRevision"})
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
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    String id = component.get("id").getAsString();
                    this.backupRevisionId = id;
                }));
    }

    @Test(dependsOnMethods = {"createBackupRevisionForExistingState"})
    @CitrusTest(name = "Restore revision for existing state")
    public void restoreRevisionForExistingState() {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("restore-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid)
                .concat("&").concat("revisionId").concat("=").concat(this.revisionIdToRestore);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                }));
    }

    @Test(dependsOnMethods = {"restoreRevisionForExistingState"})
    @CitrusTest(name = "Create revision for new state")
    public void createRevisionForNewState() throws JsonProcessingException {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("description", "new revision");
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
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    String id = component.get("id").getAsString();
                    this.newRevisionId = id;
                }));
    }

    @Test(dependsOnMethods = {"createRevisionForNewState"})
    @CitrusTest(name = "Deploy revision with new state")
    public void deployRevisionWithNewState() throws JsonProcessingException {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("deploy-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid)
                .concat("&").concat("revisionId").concat("=").concat(this.newRevisionId);

        String deploymentName = this.deploymentName;
        String deploymentVHost = this.deploymentVHost;
        Boolean deploymentDisplayOnDevportal = this.deploymentDisplayOnDevportal;
        List<HashMap<String, Object>> requestBodyMapList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> requestBodyMap = new HashMap<>() {{
            put("name", deploymentName);
            put("vhost", deploymentVHost);
            put("displayOnDevportal", deploymentDisplayOnDevportal);
        }};
        requestBodyMapList.add(requestBodyMap);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMapList);

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
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonArray component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonArray();
                }));
    }

    @Test(dependsOnMethods = {"deployRevisionWithNewState"})
    @CitrusTest(name = "Query build by version")
    public void queryBuildByVersion() throws JsonProcessingException {
        String graphQlQuery = "query {" +
                "   buildsByVersion(" +
                "       orgHandler: \"" + this.orgHandle + "\"," +
                "       build: {" +
                "           componentId: \"" + this.componentId + "\"," +
                "           versionId: \"" + this.versionId + "\"" +
                "       }" +
                "   )" +
                "   {" +
                "       id," +
                "       createdDate," +
                "       versionId," +
                "       buildId," +
                "       commitHash," +
                "       commitMessage," +
                "       revisions {" +
                "           revisionId," +
                "           createdDate," +
                "           description," +
                "           environments" +
                "       }" +
                "   }" +
                "}";

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put(Constant.QUERY, graphQlQuery);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        $(http()
                .client(choreoProjectsTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody));

        $(http()
                .client(choreoProjectsTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    JsonObject data = component.get("data").getAsJsonObject();
                    JsonArray buildsByVersionArray = data.get("buildsByVersion").getAsJsonArray();
                    JsonObject buildsByVersion = buildsByVersionArray.get(0).getAsJsonObject();
                    this.buildId = buildsByVersion.get("buildId").getAsString();
                    JsonArray revisions = buildsByVersion.get("revisions").getAsJsonArray();
                    for (int i = 0; i < revisions.size(); i++) {
                        JsonObject revision = revisions.get(i).getAsJsonObject();
                        JsonArray environments = revision.get("environments").getAsJsonArray();
                        if (environments.size() != 0) {
                            this.environmentId = environments.get(0).getAsString();
                            break;
                        }
                    }
                }));
    }

    @Test(dependsOnMethods = {"queryBuildByVersion"})
    @CitrusTest(name = "Create revision in project manager")
    public void createRevisionInProjectManager() throws JsonProcessingException {
        String graphQlQuery = "mutation {" +
                "   createRevision(" +
                "       orgHandler: \"" + this.orgHandle + "\"," +
                "       build: {" +
                "           componentId: \"" + this.componentId + "\"," +
                "           versionId: \"" + this.versionId + "\"," +
                "           buildId: \"" + this.buildId + "\"," +
                "           revisionId: \"" + this.newRevisionId + "\"," +
                "           environmentId: \"" + this.environmentId + "\"," +
                "       }" +
                "   )" +
                "   {" +
                "       revisions {" +
                "           id," +
                "           revisionId," +
                "           createdDate," +
                "           environments," +
                "           updatedDate" +
                "       }" +
                "   }" +
                "}";

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put(Constant.QUERY, graphQlQuery);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        $(http()
                .client(choreoProjectsTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody));

        $(http()
                .client(choreoProjectsTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON));
    }

    @Test(dependsOnMethods = {"createRevisionInProjectManager"})
    @CitrusTest(name = "Restore backup revision")
    public void restoreBackupRevision() {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("restore-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid)
                .concat("&").concat("revisionId").concat("=").concat(this.backupRevisionId);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                }));
    }

    @Test(dependsOnMethods = {"restoreBackupRevision"})
    @CitrusTest(name = "Delete backup revision")
    public void deleteBackupRevision() {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("/").concat(this.backupRevisionId)
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .delete(path)
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
                    JsonArray list = component.get("list").getAsJsonArray();
                    JsonObject revisionObjectToDelete = list.get(0).getAsJsonObject();
                }));
    }

    @Test(dependsOnMethods = {"deleteBackupRevision"})
    @CitrusTest(name = "Get revisions")
    public void getRevisions() {
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
                    Assert.assertEquals(count, MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE);
                }));
    }

    @AfterClass
    public void afterClass() throws Exception {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("revisions")
                .concat("/").concat(this.revisionIdToRestore)
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid);

        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .delete(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON));

        component.undeploy(accessToken, componentId, releaseId, orgHandle);
    }
}
