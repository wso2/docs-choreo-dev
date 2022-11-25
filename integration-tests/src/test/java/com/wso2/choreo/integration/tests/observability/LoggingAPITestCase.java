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
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.ObservabilityIdInformation;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.createcomponentresponse.ComponentCreationResponse;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
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
import java.net.URISyntaxException;
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
    private static RestApiChoreoComponent restApiComponent;
    ChoreoProject project;
    String repoName;
    String orgHandle;
    String projectId;
    String devInvokeURL;

    ChoreoComponent choreoComponent;

    @Autowired
    private HttpClient choreoCPTestClient;

    ComponentCreationResponse response;
    ChoreoOrganization org;

    @DataProvider(name = "env-provider")
    public Object[][] environment() {
        return new Object[][]{{Constant.DEV_ENVIRONMENT}, {Constant.PROD_ENVIRONMENT}};
    }

  //  @BeforeClass
//    public void setup_LoggingAPITestCase()
//            throws Exception {
      //  accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    //    String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
   //     String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
   //     String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);



//        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
//        restApiComponent =
//                (RestApiChoreoComponent) project.createChoreoComponent(accessToken, restApiComponentBuilder);
//        restApiComponent.setProject(project);
//        restApiComponent.setOrganization(org);
//
//        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.DEV_ENVIRONMENT);
//        restApiComponent.deploy(accessToken, org.getOrgHandle(), org.getOrgUUID());
//        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Development", 4);
//
//        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.PROD_ENVIRONMENT);
//        restApiComponent.promote(accessToken, Constant.DEV_ENVIRONMENT, Constant.PROD_ENVIRONMENT);
//        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Production", 4);
//
//        restApiComponent.waitForObservabilityLogs(accessToken, Constant.DEV_ENVIRONMENT);
//        restApiComponent.waitForObservabilityLogs(accessToken, Constant.PROD_ENVIRONMENT);
    }

    @BeforeClass
    public void setup_LoggingAPITestCase() throws Exception {
        accessToken="Bearer eyJ4NXQiOiJOMkprTmpZMllUZGtabVl4TldNNVltSTJabUkwWlRFNE56ZzRNREkxTVRneVpUaGpaVEppWWciLCJraWQiOiJNbUV5WlRSaFpHTTROamc1WW1SbU9XVXlOalkxT1dReVpURXlNREJoTXpVd01ESTFOak5pWlRkalptWXhZMlkzWWpCaU4ySTRaRFppTW1Jek5qYzJPUV9SUzI1NiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIxYzY5MjMwNi1iOGIwLTQ1ODUtYjNjYy1lOGVmZjQ4ODk5YTEiLCJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiaXNzIjoiaHR0cHM6XC9cL3N0cy5wcmV2aWV3LWR2LmNob3Jlby5kZXY6NDQzXC9vYXV0aDJcL3Rva2VuIiwiYXVkIjpbIld4cXkwbGlDZkxCc2RwWE9oa2N4Wno2dUxQa2EiLCJodHRwczpcL1wvc3RzLnByZXZpZXctZHYuY2hvcmVvLmRldjo0NDNcL29hdXRoMlwvdG9rZW4iXSwibmJmIjoxNjY5Mjc3ODc5LCJhenAiOiJXeHF5MGxpQ2ZMQnNkcFhPaGtjeFp6NnVMUGthIiwic2NvcGUiOiJhcGltOmFkbWluIGFwaW06YXBpX21hbmFnZSBhcGltOmFwaV9wdWJsaXNoIGFwaW06YXBpX3NldHRpbmdzIGFwaW06ZGNyOmFwcF9tYW5hZ2UgYXBpbTpkb2N1bWVudF9tYW5hZ2UgYXBpbTpwdWJsaXNoZXJfc2V0dGluZ3MgYXBpbTpzdWJzY3JpcHRpb25fbWFuYWdlIGFwaW06c3Vic2NyaXB0aW9uX3ZpZXcgYXBpbTp0aWVyX21hbmFnZSBjaG9yZW86Y29tcG9uZW50X21hbmFnZSBjaG9yZW86ZGVwbG95bWVudF9tYW5hZ2UgY2hvcmVvOmRldl9lbnZfbWFuYWdlIGNob3Jlbzpwcm9kX2Vudl9tYW5hZ2UgY2hvcmVvOnByb2plY3RfbWFuYWdlIGNob3Jlbzpyb2xlX21hbmFnZSBjaG9yZW86dXNlcl9tYW5hZ2UgZW52aXJvbm1lbnRzOnZpZXdfZGV2IGVudmlyb25tZW50czp2aWV3X3Byb2QiLCJvcmdhbml6YXRpb24iOnsiaGFuZGxlIjoiZGFzdW5hdHdzbzJjb20iLCJ1dWlkIjoiZmVjMDgzMmUtOTRkZC00NzQ5LWFhMGYtN2RhNWVkOWUwYTMxIn0sIm9yZ2FuaXphdGlvbnMiOlsiZmVjMDgzMmUtOTRkZC00NzQ5LWFhMGYtN2RhNWVkOWUwYTMxIiwiYzI5Y2Y2M2UtZTViYy00ODhlLTk5OGEtODE4NjZkMmIyNjZiIiwiYWJmNjRjM2ItMjU4ZC00NzQ2LTgyNzktZDZjNWY2M2VhNTU4Il0sImV4cCI6MTY2OTI4MTQ3OSwiaWRwX2NsYWltcyI6eyJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiYXV0aGVudGljYXRlZF9pZHAiOiJHb29nbGUiLCJuYW1lIjoiRGFzdW4gU2FtYXJhc2luZ2hlIiwiZ2l2ZW5fbmFtZSI6IkRhc3VuIiwiZmFtaWx5X25hbWUiOiJTYW1hcmFzaW5naGUiLCJlbWFpbCI6ImRhc3VuQHdzbzIuY29tIn0sImlhdCI6MTY2OTI3Nzg3OSwianRpIjoiNGE0MTY0ZjItYzdjYS00NTU2LWI3MzMtOWY3OGEzM2M5N2FkIn0.ddViF6-zqH_BOUvweiDkQQiSVLYTpRIiUbOdqFKq5F65JL_RTMrvy1IYO5k22nP8BbqALZ7Gvfc0SYa7ylZDAnuucm27vMJEOmP2XHc4D57BpRSVftTXgdgPPZ6Tf6RRXcj59PmnXdSZB1XdV7nNUvJ1Xai5XpMGv7__a_GaM5I_BeEswyHoLm5YVCudP5XNDl5A8aNs9AVgbFwqjePvnjMWZM074guXqSvQLpMIYc8zzW4hkFvqp1ZftG6O4461iuSHtlL_C6qv8abSiPKzPWRN8Er566ZyZgMy7MwVYc98HfxgwW6dETqL5exhXuuHJ9NvDyXWrAa-S4gEVqH7IsRcn8lOm0e-kb_kWD2FwTQU7Of7H1Qgj5coZ5YTwa7pqKE_8rz55oW2CYjrAlmpnZuUsuEg3WMcuZABrYfME-c14NER1OqyWa2l2d8AUpNwZNfP0UPqq4r0UkJJ93UR21x25Dysi-LWzdHHFt_ITkCF-uouq-X2T6rjhQA8ZQpON2bmlP72KH3wzPGvIdBGS1EgjUaG4oTAcNp7CIYMXSjHHEHZeQGDUWcLYDHDp5AFaquxlTGePAAKcfi3zrc62JdAHt-gaXSA7SsOYxR-YtrvilY9wiG-c94LatcUSfe4582r6qOZYxZ3lsDTi716ew8azl-XxRqroyf9pjLfngk";
        org = ControlPlaneAPI.getOrg();
        project = org.createProject(accessToken);
        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
    //    accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
     //   orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        org = TestContext.getTestOrg();
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();


    }


    @Test
    @CitrusTest
    public void createUserManagedComponent_LoggingAPITestCase() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").srcGitRepoUrl(GitHub.getGitHubRepoUrl(repoName)).projectId(projectId).displayType(Constant.displayType.restAPI.name()).build();
        response = GraphQL.createUserManagedComponent(dto, accessToken);
        Assert.assertNotNull(response.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponent_LoggingAPITestCase"})
    @CitrusTest
    public void createdComponentStatus_LoggingAPITestCase() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, response.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }


    @Test(dependsOnMethods = {"createdComponentStatus_LoggingAPITestCase"})
    @CitrusTest
    public void initialPRGeneration_LoggingAPITestCase() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 1);
        Assert.assertEquals(prs.length, 1);
    }

    @Test(dependsOnMethods = {"initialPRGeneration_LoggingAPITestCase"})
    @CitrusTest
    public void mergePR_LoggingAPITestCase() throws IOException, UnexpectedResponseException {
        GitHub.mergePR(repoName, "1");
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 0);
        Assert.assertEquals(prs.length, 0);
    }



    @Test(dependsOnMethods = {"mergePR_LoggingAPITestCase"})
    @CitrusTest
    public void componentRetrieval_LoggingAPITestCase() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, response.getHandler(), accessToken);
        choreoComponent.setOrganization(org);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_LoggingAPITestCase"})
    @CitrusTest
    public void addDeploymentConfiguration_LoggingAPITestCase() throws Exception {
        Orgs.addConfiguration( choreoComponent, "dev",accessToken);
    }


    @Test(dependsOnMethods = {"addDeploymentConfiguration_LoggingAPITestCase"})
    @CitrusTest
    public void deploy_LoggingAPITestCase() throws Exception {
       GraphQL.deployComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"deploy_LoggingAPITestCase"})
    @CitrusTest
    public void deploymentStatusByVersion_LoggingAPITestCase() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_LoggingAPITestCase"})
    @CitrusTest
    public void componentDevDeploymentStatus_LoggingAPITestCase() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, "dev", accessToken).getInvokeUrl();
    }


    @Test(dataProvider = "env-provider")
    @CitrusTest
    public void testGroupedLogs_LoggingAPITestCase(String env) throws ReleaseIdNotFoundException, EnvironmentDetailsCheckException,
            IOException, NamespaceNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException,
            InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        String namespace = restApiComponent.getNamespaceForEnvironment(accessToken, env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);

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

    //    @Test(dataProvider = "env-provider")
//    @CitrusTest
    public void testLiveLogs(String env) throws EnvironmentDetailsCheckException, IOException,
            NamespaceNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException,
            InterruptedException, ReleaseIdNotFoundException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        String namespace = restApiComponent.getNamespaceForEnvironment(accessToken, env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);

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

    //    @Test(dataProvider = "env-provider")
//    @CitrusTest
    public void downloadZippedLogs(String env) throws IOException, URISyntaxException,
            ObservabilityLogsDownloadStatusCheckException, ReleaseIdNotFoundException, EnvironmentDetailsCheckException,
            NamespaceNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException,
            InterruptedException {
        String releaseId = restApiComponent.getReleaseIdForEnvironment(env);
        String namespace = restApiComponent.getNamespaceForEnvironment(accessToken, env);
        ObservabilityIdInformation observabilityIdInformation =
                restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId);

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


//    @Test(dependsOnMethods = {"testGroupedLogs_LoggingAPITestCase"}, alwaysRun = true)
//    @CitrusTest
    public void deleteComponent_TestClientJwTValidation() throws IOException {
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
