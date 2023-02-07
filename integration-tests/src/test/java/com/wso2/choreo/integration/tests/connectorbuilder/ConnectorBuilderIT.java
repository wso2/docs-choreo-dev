package com.wso2.choreo.integration.tests.connectorbuilder;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
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


import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Connector publishing related tests
 */
public class ConnectorBuilderIT extends TestNGCitrusSpringSupport {

    private static String componentId;
    private static String accessToken;
    private String orgHandle;
    private String orgUuid;
    private static String revisionId;

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup_ConnectorBuilderIT()
            throws Exception, ApiLifecycleChangeException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        ChoreoOrganization org = new ChoreoOrganization(orgHandle,orgId,orgUuid);
        ChoreoProject project = GraphQL.createProject(accessToken);
        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
        RestApiChoreoComponent restApiComponent =
                (RestApiChoreoComponent) project.createChoreoComponent(accessToken, restApiComponentBuilder);
        componentId = restApiComponent.getId();
        Orgs.getConfigurationMapping(restApiComponent,accessToken);
        Orgs.addConfiguration(restApiComponent, "dev", accessToken);
        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.DEV_ENVIRONMENT);
        restApiComponent.deploy(accessToken, org.getOrgHandle(), org.getOrgUUID());
        restApiComponent.getLatestApiVersion()
                .changeApiLifeCycle(accessToken, org.getOrgUUID(), Constant.apiLIifCycleState.Publish);
        JsonArray revisions = restApiComponent.getRevisions(accessToken, restApiComponent.getLatestApiVersion().getProxyId(),orgUuid);
        JsonObject revision =(JsonObject) revisions.get(0);
        revisionId = revision.get("id").getAsString();
    }

    @Test
    @CitrusTest
    public void publishConnector_ConnectorBuilderIT() {
        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(orgHandle)
                        .concat("/").concat(componentId))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .body("{" +
                        "    \"apiId\": \"" + revisionId + "\"," +
                        "    \"organizationId\": \"" + orgUuid + "\"," +
                        "    \"connectorVersion\": \"" + Constant.TEST_CONNECTOR_VERSION + "\"," +
                        "    \"visibility\": \"" + Constant.TEST_CONNECTOR_VISIBILITY + "\"" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
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
                                        .concat(componentId).concat("/status"))
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
                        .concat("/").concat(componentId))
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
                        .concat("/").concat(componentId).concat("/republish"))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .body("{" +
                        "    \"apiId\": \"" + revisionId + "\"," +
                        "    \"organizationId\": \"" + orgUuid + "\"," +
                        "    \"connectorVersion\": \"" + Constant.TEST_CONNECTOR_VERSION + "\"," +
                        "    \"visibility\": \"" + Constant.TEST_CONNECTOR_VISIBILITY + "\"" +
                        "}")
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/connectorbuilder/republish_success_ok.json")));
    }
}
