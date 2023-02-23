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
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsDownloadStatusCheckException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.response.Response;
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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    ChoreoProject project;
    String projectId;
    String devInvokeURL;
    String prodInvokeURL;
    Environment[] en;
    String apiKey;

    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    private HttpClient choreoCPTestClient;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    ChoreoComponent choreoComponent;
    ChoreoOrganization org;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.Environment.Development}, {Constant.Environment.Production}};
    }

    @BeforeClass
    public void setup_LoggingAPITestCase() throws Exception {
       accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = GraphQL.createProject(accessToken);
        projectId = project.getId();
    }


    @Test
    @CitrusTest
    public void createUserManagedComponent_LoggingAPITestCase() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
             GraphqlDTO dto = GraphqlDTO.builder().name(componentName).
                triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/rest-api").
                projectId(projectId).
                displayType(Constant.displayType.restAPI.name()).build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        Assert.assertNotNull(choreoComponent.getId());
    }


    @Test(dependsOnMethods = {"createUserManagedComponent_LoggingAPITestCase"})
    @CitrusTest
    public void deploy_LoggingAPITestCase() throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent);
        devInvokeURL = statusDTO.getInvokeUrl();
    }

    @Test(dependsOnMethods = {"deploy_LoggingAPITestCase"})
    @CitrusTest
    public void addPromoteConfiguration_LoggingAPITestCase() throws Exception {
        Response res = Orgs.addConfiguration(choreoComponent, "prod", accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_LoggingAPITestCase"})
    @CitrusTest
    public void promote_LoggingAPITestCase() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_LoggingAPITestCase"})
    @CitrusTest
    public void componentProdDeploymentStatus_LoggingAPITestCase() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentProdDeploymentStatus_LoggingAPITestCase"})
    @CitrusTest
    public void invokeEP_LoggingAPITestCase() throws IOException {
        apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        TestHelper.invokeEP(devInvokeURL, apiKey);
        TestHelper.invokeEP(prodInvokeURL, apiKey);
    }


    @Test(dependsOnMethods = {"componentProdDeploymentStatus_LoggingAPITestCase"})
    @CitrusTest
    public void waitForObservabilityLogs_LoggingAPITestCase() throws Exception {

        en = GraphQL.getNamespaceForEnvironment(projectId, accessToken);
        Environment devEnv = choreoComponent.getEnvironment(en, Constant.Environment.Development);
        Environment prodEnv = choreoComponent.getEnvironment(en, Constant.Environment.Production);
        choreoComponent.waitForObservabilityLogs(devEnv, accessToken);
        choreoComponent.waitForObservabilityLogs(prodEnv, accessToken);

        String devReleaseId =  choreoComponent.getReleaseIdForEnvironment(devEnv.getChoreoEnv());
        String prodReleaseId =  choreoComponent.getReleaseIdForEnvironment(prodEnv.getChoreoEnv());
    }

    @Test(dataProvider = "env-provider", dependsOnMethods = {"waitForObservabilityLogs_LoggingAPITestCase"})
    @CitrusTest
    public void testGroupedLogs_LoggingAPITestCase(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);

        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
                .concat(observabilityIdInformation.getObsId())
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

    @Test(dataProvider = "env-provider", dependsOnMethods = {"testGroupedLogs_LoggingAPITestCase"})
    @CitrusTest
    public void testLiveLogs_LoggingAPITestCase(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);

        String requestPath = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX
                .concat(observabilityIdInformation.getObsId())
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

    @Test(dataProvider = "env-provider", dependsOnMethods = {"testLiveLogs_LoggingAPITestCase"})
    @CitrusTest
    public void downloadZippedLogs_LoggingAPITestCase(Constant.Environment env) throws Exception {
        Environment environment = choreoComponent.getEnvironment(en, env);
        String releaseId = choreoComponent.getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();
        ObservabilityIdInformation observabilityIdInformation = GraphQL.getComponentObservabilityIdForReleaseId(releaseId, accessToken);

        String requestPath = Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT).concat(Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX)
                .concat(observabilityIdInformation.getObsId())
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
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.OK.value()) {
                throw new ObservabilityLogsDownloadStatusCheckException(statusCode, EntityUtils.toString(response.getEntity()));
            }
            byte[] zipFile = EntityUtils.toByteArray(response.getEntity());
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile));
            String initialFileName = "logs-" + releaseId + "-1.txt";
            Map<String, String> entries = readZipEntries(zis);
            MatcherAssert.assertThat(entries.size(), greaterThan(0));
            MatcherAssert.assertThat(entries.keySet(), hasItems(initialFileName));
            String content = entries.get(initialFileName);
            MatcherAssert.assertThat(content, containsStringIgnoringCase(observabilityIdInformation.getObsId()));
        }
    }


    @Test(dependsOnMethods = {"downloadZippedLogs_LoggingAPITestCase"}, alwaysRun = true)
    @CitrusTest
    public void deleteComponent_LoggingAPITestCase() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), project.getId(), accessToken);
        ChoreoComponent[] components = GraphQL.getProjectComponents(project.getId(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        Assert.assertEquals(components.length, 0);
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
