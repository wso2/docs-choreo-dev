package com.wso2.choreo.integration.tests.connectorbuilder;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Connector publishing related tests
 */
public class ConnectorBuilderIT extends TestNGCitrusSpringSupport {

    private static String accessToken;
    private static String componentHandler;
    private ChoreoComponent choreoComponent;
    private String projectId;
    private ChoreoProject project;
    private String orgHandle;
    private String orgUUID;
    private static String revisionId;
    private String orgId;
    private String githubOrg;
    private String githubPAT;
    private String devInvokeURL;

    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    private HttpClient choreoProjectsTestClient;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    ChoreoOrganization org;

    private List<Environment> environments;

    @BeforeClass
    public void setup_ConnectorBuilderIT() throws Exception {

        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        project = GraphQL.createProject(accessToken);
        projectId = project.getId();
        org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
        githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
        orgHandle=Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);

    }

    @Test
    @CitrusTest
    public void createComponent_ConnectorBuilderIT() throws Exception, ApiLifecycleChangeException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/rest-api").branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);

        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);

        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);


        //Deploying component
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, environments, ComponentFlavour.STANDARD);
        devInvokeURL = statusDTO.getInvokeUrl();

        //change the API lifecycle
        choreoComponent.getLatestApiVersion().changeApiLifeCycle(accessToken, org.getOrgUUID(), Constant.apiLIifCycleState.Publish);
        JsonArray revisions = choreoComponent.getRevisions(accessToken, choreoComponent.getLatestApiVersion().getProxyId(), orgUUID);
        JsonObject revision = (JsonObject) revisions.get(0);
        revisionId = revision.get("id").getAsString();
    }

    @Test(dependsOnMethods = "createComponent_ConnectorBuilderIT")
    @CitrusTest
    public void publishConnector_ConnectorBuilderIT() {
        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(orgHandle)
                        .concat("/").concat(choreoComponent.getId()))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .body("{" +
                        "    \"apiId\": \"" + revisionId + "\"," +
                        "    \"organizationId\": \"" + orgUUID + "\"," +
                        "    \"connectorVersion\": \"" + Constant.TEST_CONNECTOR_VERSION + "\"," +
                        "    \"visibility\": \"" + Constant.TEST_CONNECTOR_VISIBILITY + "\"" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/connectorbuilder/publish_success_ok.json")));
    }

    @Test(dependsOnMethods = {"publishConnector_ConnectorBuilderIT"})
    @CitrusTest
    public void getConnectorStatus_ConnectorBuilderIT() throws InterruptedException {
        $(repeatOnError()
                .until("i = 15")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(choreoTestClient)
                                .send()
                                .get(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/")
                                        .concat(orgHandle).concat("/")
                                        .concat(choreoComponent.getId()).concat("/status"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)
                                ),
                        http().client(choreoTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/connectorbuilder/publish_status_completed.json"))
                                .validate(json()
                                        .ignore("$.id")
                                        .ignore("$.created_at")
                                        .ignore("$.updated_at")
                                )
                )
        );
    }

    @Test(dependsOnMethods = {"getConnectorStatus_ConnectorBuilderIT"})
    @CitrusTest
    public void getConnector_ConnectorBuilderIT() {
        $(http()
                .client(choreoTestClient)
                .send()
                .get(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(orgHandle)
                        .concat("/").concat(choreoComponent.getId()))
                .queryParam("version=".concat(Constant.TEST_CONNECTOR_VERSION))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(new ClassPathResource("templates/connectorbuilder/get_connector_success.json"))
                .validate(json()
                        .ignore("$.name")
                        .ignore("$.org")
                        .ignore("$.modules")
                        .ignore("$.createdDate")
                )
        );
    }

    @Test(dependsOnMethods = {"getConnector_ConnectorBuilderIT"})
    @CitrusTest
    public void republishConnector_ConnectorBuilderIT() {
        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(orgHandle)
                        .concat("/").concat(choreoComponent.getId()).concat("/republish"))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .body("{" +
                        "    \"apiId\": \"" + revisionId + "\"," +
                        "    \"organizationId\": \"" + orgUUID + "\"," +
                        "    \"connectorVersion\": \"" + Constant.TEST_CONNECTOR_VERSION + "\"," +
                        "    \"visibility\": \"" + Constant.TEST_CONNECTOR_VISIBILITY + "\"" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/connectorbuilder/republish_success_ok.json")));
    }
}
