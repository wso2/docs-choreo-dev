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

package com.wso2.choreo.integration.tests.observability;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import net.minidev.json.JSONObject;
import org.hamcrest.core.StringRegularExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class LoggingAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static JsonObject ast;
    private static ChoreoProject project;
    private static RestApiChoreoComponent restApiComponent;
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    private static String namespace;
    private static String releaseId;
    private static String obsId;


    @Autowired
    private HttpClient choreoCPTestClient;

    @BeforeClass
    public void beforeClass()
            throws IOException, InterruptedException, ProjectCreationException, GetCommitHistoryException,
            NoLatestCommitHashFoundException, AddConfigurationsException, NoLatestAppEnvIdFoundException,
            ComponentCreationStatusCheckException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException, ComponentCreationException, ComponentRetrieveException,
            ApiLifecycleChangeException, ComponentCreationTimeoutException, ComponentDeploymentTimeoutException,
            NoLatestApiVersionFoundException, ComponentDeploymentFailureException, TokenRetrievalException,
            ComponentInvokeInformationCheckException, InvokeInformationNotFoundException, APIKeyGenerationCheckException, ApiKeyNotFoundException, InvokeAPICheckException, ReleaseIdNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, ObservabilityDataNotFoundException, EnvironmentDetailsCheckException, NamespaceNotFoundException, ObservabilityDataCheckException, URISyntaxException, ObservabilityLogsCheckException, ObservabilityLogsNotFoundException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestTokenForCPAPIs());
        ChoreoOrganization org = new ChoreoOrganization(Configuration.TEST_CHOREO_ORG_HANDLE,
                String.valueOf(Configuration.TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
        ChoreoProject project = org.createProject(accessToken);
        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
        restApiComponent =
                (RestApiChoreoComponent) project.createChoreoComponent(restApiComponentBuilder, accessToken);
        restApiComponent.setProject(project);
        restApiComponent.setOrganization(org);
        restApiComponent.addConfigurations(accessToken, org.getOrgHandle());
        restApiComponent.deploy(accessToken, org.getOrgHandle(), org.getOrgUUID());
        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Development", 4);
        restApiComponent.waitTillObservabilityDataPopulate(accessToken);
        releaseId = restApiComponent.getReleaseIdForEnvironment("dev");
        namespace = restApiComponent.getNamespaceForEnvironment(accessToken, "dev");
        obsId = restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId).getObsId();
        restApiComponent.waitForObservabilityLogs(accessToken, obsId, releaseId, namespace);
    }

    @Test
    @CitrusTest
    public void testGroupedLogs() throws IOException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException, ReleaseIdNotFoundException, EnvironmentDetailsCheckException, NamespaceNotFoundException {
        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
                .concat(obsId)
                .concat("/groupedlogsV2");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(requestPath)
                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .queryParam("releaseId", releaseId)
                .queryParam("namespace", namespace)
                .queryParam("limit", "5")
                .queryParam("bin", "10")
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    System.out.println("payload" + message.getPayload().toString());
                })
                .validate(jsonPath()
                        .expression("$.keySet()", hasItems("columns", "rows"))
                        .expression("$.columns.size()", greaterThan(1))
                        .expression("$.columns[*].name", hasItems("TimeGenerated", "Logs"))
                        .expression("$.columns[*].type", hasItems("datetime", "dynamic"))
                        .expression("$.rows.size()", greaterThan(1))
                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")))
                )
        );
    }

    @Test
    @CitrusTest
    public void testLiveLogs() throws IOException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException, ReleaseIdNotFoundException, EnvironmentDetailsCheckException, NamespaceNotFoundException {
        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
                .concat(obsId)
                .concat("/logsV2");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(requestPath)
                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .queryParam("releaseId", releaseId)
                .queryParam("namespace", namespace)
                .queryParam("sort", "desc")
                .queryParam("limit", "95")
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    System.out.println("payload" + message.getPayload().toString());
                })
                .validate(jsonPath()
                        .expression("$.keySet()", hasItems("columns", "rows"))
                        .expression("$.columns.size()", greaterThan(1))
                        .expression("$.columns[*].name", hasItems("TimeGenerated", "LogLevel", "LogEntry", "LogContext"))
                        .expression("$.columns[*].type", hasItems("datetime", "string", "dynamic", "dynamic"))
                        .expression("$.rows.size()", greaterThan(1))
                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")))
                )
        );
    }
}
