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


import com.consol.citrus.TestActionRunner;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.wso2.choreo.integration.config.Constant.INDEX_OF_DEPLOYED_REVISION;
import static com.wso2.choreo.integration.config.Constant.INDEX_OF_REVISION_TO_DELETE;
import static com.wso2.choreo.integration.config.Constant.MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE;
import static com.wso2.choreo.integration.config.Constant.REVISION_COUNT_AFTER_BACKUP_DELETION;
import static com.wso2.choreo.integration.config.Constant.REVISION_COUNT_AFTER_DELETION;
import static com.wso2.choreo.integration.config.Constant.REVISION_COUNT_BEFORE_DELETION;

/**
 * Create revision to exceed API revision limit reached with revision creation in Settings page
 */
public class CreateMaxAPIRevisionsUsingSettingsPage extends TestNGCitrusSpringSupport {

    private String accessToken;
    private String componentName;
    private String projectName;
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
    private RevisionWrapper revisionWrapper;
    private List<Environment> environments;

    private int revisionCount;
    private int count;


    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    private HttpClient choreoTestClientForSTS;
    @Autowired
    private HttpClient choreoProjectsTestClient;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        componentName = "maxApiRevisionsUsingSettingsPageV2";
    }

    @Test
    @CitrusTest
    public void getRevisionCount_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/rest-api").branch("main").subPath("").build();

        // Access a reusable component which has a total of 18 revisions
        component = ComponentUtils.getReusableComponent(this, accessToken, repo, componentName.toLowerCase(),
                citrusClients, ComponentFlavour.STANDARD);

        ChoreoOrganization org = component.getOrganization();
        orgUuid = org.getOrgUUID();
        orgHandle = org.getOrgHandle();

        projectId = component.getProjectId();
        componentId = component.getId();
        environmentId = component.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        versionId = component.getLatestApiVersion().getId();

        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, component);
        JsonArray deploymentArray = component.getDeployments(accessToken, orgHandle, orgUuid, versionId);

        if(deploymentArray.size()==0){
            revisionCount = 0;
        }else{
            JsonObject deployment = (JsonObject) deploymentArray.get(0);
            apiId = deployment.get("apiId").getAsString();
            releaseId = deployment.get("releaseId").getAsString();
            revisionWrapper = ComponentUtils.getRevisions(this, citrusClients, accessToken, apiId, orgUuid);
            revisionCount = revisionWrapper.getCount();
        }

        while(revisionCount<18){
            ComponentUtils.deployComponent(this, citrusClients,
                    accessToken, component, environments, ComponentFlavour.STANDARD);
            revisionCount = revisionCount+1;
            SleepUtil.sleep(30);
        }
    }

    @Test(dependsOnMethods = {"getRevisionCount_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void createDeploymentAtApiRevisionLimit_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
//         Creating a revision using Settings page requires a deployment.
//         Each deployment creates a new revision.
//         This deployment is done to reach API revision limit of the Settings page (i.e. 19).

        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, component, environments, ComponentFlavour.STANDARD);

        JsonArray deploymentArray = component.getDeployments(accessToken, orgHandle, orgUuid, versionId);
        JsonObject deployment = (JsonObject) deploymentArray.get(0);
        releaseId = deployment.get("releaseId").getAsString();
        apiId = deployment.get("apiId").getAsString();

        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE);

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

    @Test(dependsOnMethods = {"createDeploymentAtApiRevisionLimit_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void getRevisionToDelete_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        // Creating a revision using Settings page to exceed API revision limit includes several network calls.
        // This logic is handled in the frontend.
        // The following test cases make the above network calls sequentially.
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", REVISION_COUNT_BEFORE_DELETION);

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
                        .ignore("$.list"))
                .extract((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    JsonArray list = component.get("list").getAsJsonArray();

                    JsonObject revisionObjectToDelete = list.get(INDEX_OF_REVISION_TO_DELETE).getAsJsonObject();
                    this.revisionIdToDelete = revisionObjectToDelete.get("id").getAsString();

                    JsonObject deployedRevisionObject  = list.get(INDEX_OF_DEPLOYED_REVISION).getAsJsonObject();
                    JsonArray deploymentInfoArray = deployedRevisionObject.get("deploymentInfo").getAsJsonArray();
                    if (deploymentInfoArray.size() != 0) {
                        JsonObject deploymentInfo = deploymentInfoArray.get(0).getAsJsonObject();
                        this.revisionIdToRestore = deploymentInfo.get("revisionUuid").getAsString();
                        this.deploymentName = deploymentInfo.get("name").getAsString();
                        this.deploymentVHost = deploymentInfo.get("vhost").getAsString();
                        this.deploymentDisplayOnDevportal = deploymentInfo.get("displayOnDevportal").getAsBoolean();
                    }
                }));
    }

    @Test(dependsOnMethods = {"getRevisionToDelete_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void deleteOldestUndeployedRevision_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", REVISION_COUNT_AFTER_DELETION);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/delete_revision_success.mustache",
                responseParams);

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
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.list")));
    }

    @Test(dependsOnMethods = {"deleteOldestUndeployedRevision_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void createBackupRevisionForExistingState_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("API_ID", this.apiId);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/create_backup_revision_success.mustache",
                responseParams);

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
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.displayName")
                        .ignore("$.id")
                        .ignore("$.createdTime"))
                .extract((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject();
                    this.backupRevisionId = component.get("id").getAsString();
                }));
    }

    @Test(dependsOnMethods = {"createBackupRevisionForExistingState_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void restoreRevisionForExistingState_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("API_ID", this.apiId);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/restore_revision_success.mustache",
                responseParams);

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
                .body(expectedResponse)
                .validate(json()));
    }

    @Test(dependsOnMethods = {"restoreRevisionForExistingState_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void createRevisionForNewState_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("API_ID", this.apiId);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/create_new_revision_success.mustache",
                responseParams);

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
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.displayName")
                        .ignore("$.id")
                        .ignore("$.createdTime"))
                .extract((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    this.newRevisionId = component.get("id").getAsString();
                }));
    }

    @Test(dependsOnMethods = {"createRevisionForNewState_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void deployRevisionWithNewState_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(this.apiId)
                .concat("/").concat("deploy-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(this.orgUuid)
                .concat("&").concat("revisionId").concat("=").concat(this.newRevisionId);

        String deploymentName = this.deploymentName;
        String deploymentVHost = this.deploymentVHost;
        Boolean deploymentDisplayOnDevportal = this.deploymentDisplayOnDevportal;
        List<HashMap<String, Object>> requestBodyMapList = new ArrayList<>();
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("name", deploymentName);
                put("vhost", deploymentVHost);
                put("displayOnDevportal", deploymentDisplayOnDevportal);
            }
        };
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
                .body(new ClassPathResource(
                        "templates/maxApiRevisions/deploy_revision_success.json"))
                .validate(json()));
    }

    @Test(dependsOnMethods = {"deployRevisionWithNewState_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void queryBuildByVersion_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
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
                .body(new ClassPathResource(
                        "templates/maxApiRevisions/query_build_by_version_success.json"))
                .validate(json()
                        .ignore("$.data.buildsByVersion[0].revisions[*]"))
                .extract((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject();
                    JsonObject data = component.get("data").getAsJsonObject();
                    JsonArray buildsByVersionArray = data.get("buildsByVersion").getAsJsonArray();
                    JsonObject buildsByVersion = buildsByVersionArray.get(0).getAsJsonObject();

                    this.buildId = buildsByVersion.get("buildId").getAsString();
                    JsonArray revisions = buildsByVersion.get("revisions").getAsJsonArray();

                    JsonObject revision = revisions.get(revisions.size() - 1).getAsJsonObject();
                    JsonArray environments = revision.get("environments").getAsJsonArray();
                    if (environments.size() != 0) {
                        this.environmentId = environments.get(0).getAsString();
                    }
                }));
    }

    @Test(dependsOnMethods = {"queryBuildByVersion_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void createRevisionInProjectManager_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
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
                .type(MessageType.JSON)
                .body(new ClassPathResource(
                        "templates/maxApiRevisions/mutation_create_revision_success.json"))
                .validate(json()
                        .ignore("$.data.createRevision.revisions[*]")));
    }

    @Test(dependsOnMethods = {"createRevisionInProjectManager_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void restoreBackupRevision_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("API_ID", this.apiId);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/restore_revision_success.mustache",
                responseParams);

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
                .body(expectedResponse)
                .validate(json()));
    }

    @Test(dependsOnMethods = {"restoreBackupRevision_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void deleteBackupRevision_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", REVISION_COUNT_AFTER_BACKUP_DELETION);

        String expectedResponse = ComponentUtils.generateStringPayloadFromTemplate(
                "templates/maxApiRevisions/delete_revision_success.mustache",
                responseParams);

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
                .body(expectedResponse)
                .validate(json()
                        .ignore("$.list")));
    }

    @Test(dependsOnMethods = {"deleteBackupRevision_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void verifyRevisionCountAfterExceedingApiRevisionLimit_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        // Total revision count is maintained at API revision limit of Settings page (i.e. 19).
        Map<String, Object> responseParams = new HashMap<>();
        responseParams.put("REVISION_COUNT", MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE);

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
        if(this.revisionIdToRestore!=null){
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
}
