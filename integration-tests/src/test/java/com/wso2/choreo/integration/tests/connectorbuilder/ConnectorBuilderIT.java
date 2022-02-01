package com.wso2.choreo.integration.tests.connectorbuilder;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.ChoreoApi;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.AddConfigurationsException;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

/**
 * Connector publishing related tests
 */
public class ConnectorBuilderIT extends TestNGCitrusSpringSupport {

    private static String testComponentId;
    private static String accessToken;
    private static String testApiId;

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeSuite
    public void beforeSuite()
            throws IOException, InterruptedException, ProjectCreationException, GetCommitHistoryException,
            NoLatestCommitHashFoundException, AddConfigurationsException, NoLatestAppEnvIdFoundException,
            ComponentCreationStatusCheckException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException,
            ComponentCreationException, ComponentRetrieveException, ApiLifecycleChangeException,
            ComponentCreationTimeoutException, ComponentDeploymentTimeoutException, NoLatestApiVersionFoundException,
            ComponentDeploymentFailureException, TokenRetrievalException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
        ChoreoOrganization testOrg = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
        ChoreoProject testProject = testOrg.createProject(accessToken);
        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(testProject, testOrg);
        RestApiChoreoComponent testRestApiComponent =
                (RestApiChoreoComponent) testProject.createChoreoComponent(restApiComponentBuilder, accessToken);
        testComponentId = testRestApiComponent.getId();
        testApiId = testRestApiComponent.getLatestApiVersion().getProxyId();
        testRestApiComponent.addConfigurations(accessToken, testOrg.getOrgHandle());
        testRestApiComponent.deploy(accessToken, testOrg.getOrgHandle(), testOrg.getOrgUUID());
        ChoreoApi testRestApi = new ChoreoApi(testApiId);
        testRestApi.changeApiLifeCycle(accessToken, testOrg.getOrgUUID(), Constant.apiLIifCycleState.Publish);
    }

    @Test
    @CitrusTest
    public void testPublishConnector() {
        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(Configuration.TEST_CHOREO_ORG_HANDLE)
                        .concat("/").concat(testComponentId))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{" +
                        "    \"apiId\": \"" + testApiId + "\"," +
                        "    \"organizationId\": \"" + Configuration.TEST_CHOREO_ORG_UUID + "\"," +
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
                .body(new ClassPathResource("templates/connector-builder/publish_success_ok.json")));
    }


    @Test(dependsOnMethods = {"testPublishConnector"})
    @CitrusTest
    public void testGetConnectorStatus() throws InterruptedException {
        $(repeatOnError()
                .until("i = 15")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(choreoTestClient)
                                .send()
                                .get(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/")
                                        .concat(Configuration.TEST_CHOREO_ORG_HANDLE).concat("/")
                                        .concat(testComponentId).concat("/status"))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)
                                ),
                        http().client(choreoTestClient)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/connector-builder/publish_status_completed.json"))
                                .validate(json()
                                        .ignore("$.id")
                                        .ignore("$.created_at")
                                        .ignore("$.updated_at")
                                )
                )
        );
    }

    @Test(dependsOnMethods = {"testGetConnectorStatus"})
    @CitrusTest
    public void testGetConnector() {
        $(http()
                .client(choreoTestClient)
                .send()
                .get(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(Configuration.TEST_CHOREO_ORG_HANDLE)
                        .concat("/").concat(testComponentId))
                .queryParam("version=".concat(Constant.TEST_CONNECTOR_VERSION))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(new ClassPathResource("templates/connector-builder/get_connector_success.json"))
                .validate(json()
                        .ignore("$.name")
                        .ignore("$.org")
                        .ignore("$.modules")
                        .ignore("$.createdDate")
                )
                .validate(jsonPath()
                ));
    }
}
