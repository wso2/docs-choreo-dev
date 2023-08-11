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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ApiRevisionDTO;
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
    private ChoreoComponent component;
    private String revisionIdToDelete;
    private String revisionIdToRestore;
    private String deploymentName;
    private String deploymentVHost;
    private Boolean deploymentDisplayOnDevportal;
    private String backupRevisionId;
    private RevisionWrapper revisionWrapper;
    private List<Environment> environments;
    private int revisionCount;
    private ApiRevisionDTO apiRevisionDTO;

    @Autowired
    private HttpClient choreoTestClientForSTS;
    @Autowired
    private HttpClient choreoProjectsTestClient;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        componentName = "maxApiRevisionsUsingSettingsPageV3";
    }

    @Test
    @CitrusTest
    public void getRevisionCount_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/rest-api").branch("main").subPath("").build();

        // Access a reusable component which has a total of 18 revisions
        component = ComponentUtils.getReusableComponent(this, accessToken, repo, componentName.toLowerCase(),
                citrusClients, ComponentFlavour.STANDARD);     
        ChoreoOrganization org = component.getOrganization();
        String orgUuid = org.getOrgUUID();
        String orgHandle = org.getOrgHandle();
        String componentId = component.getId();
        String environmentId = component.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String versionId = component.getLatestApiVersion().getId();
        apiRevisionDTO = ApiRevisionDTO.builder()
                .orgUuid(orgUuid)
                .orgHandler(orgHandle)
                .componentId(componentId)
                .versionId(versionId)
                .environmentId(environmentId)
                .build();
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, component);
        ComponentUtils.deployComponent(this, citrusClients, accessToken, component, environments, 
                ComponentFlavour.STANDARD);
        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
        component, Constant.DEV_ENVIRONMENT);
        Endpoint endpoint = endpoints.get(0);
        String apiId = endpoint.getApimId();
        String releaseId = endpoint.getReleaseId();
        revisionWrapper = ComponentUtils.getRevisions(this, citrusClients, accessToken, apiId, orgUuid);
        revisionCount = revisionWrapper.getCount();
        apiRevisionDTO.setApiId(apiId);
        apiRevisionDTO.setReleaseId(releaseId);
        apiRevisionDTO.setRevisionCount(revisionCount);
  
        while(revisionCount<18){
            ComponentUtils.deployComponent(this, citrusClients,
                    accessToken, component, environments, ComponentFlavour.STANDARD);
            revisionCount = revisionCount+1;
            apiRevisionDTO.setRevisionCount(apiRevisionDTO.getRevisionCount() + 1);
            SleepUtil.sleep(30);
        }
    }

    @Test(dependsOnMethods = {"getRevisionCount_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void createDeploymentAtApiRevisionLimit_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        // Creating a revision using Settings page requires a deployment.
        // Each deployment creates a new revision.
        // This deployment is done to reach API revision limit of the Settings page (i.e. 19).
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, component, environments, ComponentFlavour.STANDARD);
        apiRevisionDTO.setBuildId(statusDTO.getBuild().getBuildId());

        Endpoint endpoint = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
                component, Constant.DEV_ENVIRONMENT).get(0);
        String releaseId = endpoint.getReleaseId();
        apiRevisionDTO.setReleaseId(releaseId);
        String apiId = endpoint.getApimId();
        apiRevisionDTO.setApiId(apiId);
        apiRevisionDTO.setRevisionCount(MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/get_revisions_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());

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
        // The following test cases make the above network calls sequentially

        apiRevisionDTO.setRevisionCount(REVISION_COUNT_BEFORE_DELETION);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/get_revisions_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());

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
        apiRevisionDTO.setRevisionCount(REVISION_COUNT_AFTER_DELETION);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/delete_revision_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("/").concat(this.revisionIdToDelete)
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());

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
        apiRevisionDTO.setRevisionCount(REVISION_COUNT_AFTER_DELETION);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/create_backup_revision_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());
        apiRevisionDTO.setDescription("backup revision");
        String requestBody = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/create_new_revision_payload.mustache",
                apiRevisionDTO);

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
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/restore_revision_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("restore-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid())
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
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/create_new_revision_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());
        apiRevisionDTO.setDescription("new revision");
        String requestBody = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/create_new_revision_payload.mustache",
                apiRevisionDTO);

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
                    apiRevisionDTO.setNewRevisionId(component.get("id").getAsString());
                }));
    }

    @Test(dependsOnMethods = {"createRevisionForNewState_CreateMaxAPIRevisionsUsingSettingsPage"})
    @CitrusTest
    public void deployRevisionWithNewState_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("deploy-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid())
                .concat("&").concat("revisionId").concat("=").concat(apiRevisionDTO.getNewRevisionId());

        String deploymentName = this.deploymentName;
        String deploymentVHost = this.deploymentVHost;
        Boolean deploymentDisplayOnDevportal = this.deploymentDisplayOnDevportal;

        apiRevisionDTO.setName(deploymentName);
        apiRevisionDTO.setVhost(deploymentVHost);
        apiRevisionDTO.setDisplayOnDevportal(deploymentDisplayOnDevportal);
        String requestBody = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/api_revision_with_new_state_payload.mustache",
                apiRevisionDTO);

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
    public void createRevisionInProjectManager_CreateMaxAPIRevisionsUsingSettingsPage() throws Exception {
        String requestBody = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/create_revision_in_project_manager_payload.mustache",
                apiRevisionDTO);

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
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/restore_revision_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("restore-revision")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid())
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
        apiRevisionDTO.setRevisionCount(REVISION_COUNT_AFTER_BACKUP_DELETION);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/delete_revision_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("/").concat(this.backupRevisionId)
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());

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
        apiRevisionDTO.setRevisionCount(MAX_API_REVISIONS_LIMIT_SETTINGS_PAGE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/maxApiRevisions/get_revisions_success.mustache",
                apiRevisionDTO);
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());

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
            String path = Constant.APIS_ENDPOINT.concat("/").concat(apiRevisionDTO.getApiId())
                    .concat("/").concat("revisions")
                    .concat("/").concat(this.revisionIdToRestore)
                    .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(apiRevisionDTO.getOrgUuid());

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

            component.undeploy(accessToken, 
                apiRevisionDTO.getComponentId(),
                apiRevisionDTO.getReleaseId(),
                apiRevisionDTO.getOrgHandler());
        }
    }
}
