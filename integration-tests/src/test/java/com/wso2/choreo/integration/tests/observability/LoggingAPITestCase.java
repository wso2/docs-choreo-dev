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
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.APIKeyGenerationCheckException;
import com.wso2.choreo.integration.common.exceptions.AddConfigurationsException;
import com.wso2.choreo.integration.common.exceptions.ApiKeyNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentInvokeInformationCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.EnvironmentDetailsCheckException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.GetDeploymentsStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NamespaceNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityDataCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityDataNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityIdCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityIdNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsDownloadStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.ReleaseIdNotFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringRegularExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;

public class LoggingAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static RestApiChoreoComponent restApiComponent;
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
            ComponentInvokeInformationCheckException, InvokeInformationNotFoundException, APIKeyGenerationCheckException, ApiKeyNotFoundException, InvokeAPICheckException, ReleaseIdNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, ObservabilityDataNotFoundException, EnvironmentDetailsCheckException, NamespaceNotFoundException, ObservabilityDataCheckException, URISyntaxException, ObservabilityLogsCheckException, ObservabilityLogsNotFoundException, GetDeploymentsStatusCheckException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        ChoreoOrganization org = new ChoreoOrganization(orgHandle, orgId, orgUuid);
        ChoreoProject project = org.createProject(accessToken);
        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
        restApiComponent =
                (RestApiChoreoComponent) project.createChoreoComponent(accessToken, restApiComponentBuilder);
        restApiComponent.setProject(project);
        restApiComponent.setOrganization(org);
        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.DEV_ENVIRONMENT);
        restApiComponent.deploy(accessToken, org.getOrgHandle(), org.getOrgUUID());
        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Development", 4);
        restApiComponent.waitForMetricsData(accessToken, "dev");
        releaseId = restApiComponent.getReleaseIdForEnvironment("dev");
        namespace = restApiComponent.getNamespaceForEnvironment(accessToken, "dev");
        obsId = restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId).getObsId();
        restApiComponent.waitForObservabilityLogs(accessToken, obsId, releaseId, namespace);
    }

    @Test
    @CitrusTest
    public void testGroupedLogs() {
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
    public void testLiveLogs() {
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
                .validate(jsonPath()
                        .expression("$.keySet()", hasItems("columns", "rows"))
                        .expression("$.columns.size()", greaterThanOrEqualTo(1))
                        .expression("$.columns[*].name", hasItems("TimeGenerated", "LogLevel", "LogEntry", "LogContext"))
                        .expression("$.columns[*].type", hasItems("datetime", "string", "dynamic", "dynamic"))
                        .expression("$.rows.size()", greaterThanOrEqualTo(1))
                        .expression("$.rows[*][0]", everyItem(StringRegularExpression.matchesRegex("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")))
                )
        );
    }

    @Test
    @CitrusTest
    public void downloadZippedLogs() throws IOException, URISyntaxException, ObservabilityLogsDownloadStatusCheckException {
        String requestPath = Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT).concat(Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX)
                .concat(obsId)
                .concat("/logsV2/zip/");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        HttpGet request = new HttpGet(requestPath);
        URI uri = new URIBuilder(request.getURI())
                .addParameter("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusDays(7)))
                .addParameter("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .addParameter("releaseId", releaseId)
                .addParameter("namespace", namespace)
                .build();
        request.setURI(uri);
        request.setHeader(org.apache.http.HttpHeaders.AUTHORIZATION, accessToken);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new ObservabilityLogsDownloadStatusCheckException(statusCode, EntityUtils.toString(response.getEntity()));
            }
            byte[] zipFile = EntityUtils.toByteArray(response.getEntity());
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile));
            String initialFileName = "logs-" + releaseId + "-1.txt";
            Map<String, String> entries = readZipEntries(zis);
            MatcherAssert.assertThat(entries.size(), greaterThan(0));
            MatcherAssert.assertThat(entries.keySet(), hasItems(initialFileName));
            String content = entries.get(initialFileName).toString();
            MatcherAssert.assertThat(content, containsStringIgnoringCase(obsId));
        }
    }

    private static Map<String, String> readZipEntries(ZipInputStream zis) throws IOException {
        Map<String, String> entries = new HashMap<>();
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            String filename = zipEntry.getName();
            StringBuilder sb = new StringBuilder();
            byte[] data = new byte[1024];
            int count;
            while ((count = (zis.read(data, 0, 1024))) != -1) {
                sb.append(new String(data, 0, count, StandardCharsets.UTF_8));
            }
            entries.put(filename, sb.toString());
            zis.closeEntry();
        }
        zis.close();
        return entries;
    }
}
