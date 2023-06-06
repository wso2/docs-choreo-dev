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

package com.wso2.choreo.integration.common.choreoproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.observability.ObservabilityService;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.exceptions.APIKeyGenerationCheckException;
import com.wso2.choreo.integration.common.exceptions.AddConfigurationsException;
import com.wso2.choreo.integration.common.exceptions.ApiKeyNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentInvokeInformationCheckException;
import com.wso2.choreo.integration.common.exceptions.EnvironmentDetailsCheckException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.GetDeploymentsStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.GraphQLException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NamespaceNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityASTCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityDataCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityDataNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityIdCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityIdNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilityLogsNotFoundException;
import com.wso2.choreo.integration.common.exceptions.ObservabilitySystemMetricsCheckException;
import com.wso2.choreo.integration.common.exceptions.ObservabilitySystemMetricsNotFoundException;
import com.wso2.choreo.integration.common.exceptions.RedeployException;
import com.wso2.choreo.integration.common.exceptions.ReleaseIdNotFoundException;
import com.wso2.choreo.integration.common.exceptions.UndeployException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import java.util.concurrent.TimeUnit;

import com.wso2.choreo.integration.config.ObsRequestParam;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.commithistory.Commit;

import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.imageregistry.ImageRegistry;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.observability.ObservabilityLogs;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Abstract class to represent Choreo component
 */
public class ChoreoComponent {

    private final String choreoEndpoint;
    private final String choreoCpProjectsEndpoint;
    private final String configCPGatewayEndpoint;
    private String id;
    private String apiId;
    private List<ApiVersion> apiVersions = new ArrayList<>();
    private String createdAt;
    private String description;
    private String displayName;
    private String displayType;
    private String handler;
    private List<String> labels = new ArrayList<>();
    private String name;
    private String orgHandler;
    private String orgId;
    private String projectId;
    private ComponentRepository repository;
    private String updatedAt;
    private String version;
    private ChoreoProject project;
    private ChoreoOrganization organization = TestContext.getTestOrg();


    private String handle;
    private String organizationId;
    private String orgHandle;
    private String type;
    private String imageRegistryId;
    private String componentType;
    private boolean httpBased;
    private ImageRegistry imageRegistry;
    private String branch;
    private static final Logger log = LogManager.getLogger(ChoreoComponent.class);
    private static final Gson gson = new Gson();

    public ChoreoComponent() {
        choreoEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);
        choreoCpProjectsEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_CP_PROJECTS_ENDPOINT);
        configCPGatewayEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_CP_GW_ENDPOINT);
    }





    /**
     * Retrieve commit history of a component
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @return A JsonArray of commit history
     * @throws IOException               if an IO error occurs when sending or receiving request
     * @throws GetCommitHistoryException if retrieving the component commit history fails
     */
    public JsonArray getCommitHistory(String accessToken)
            throws IOException, GetCommitHistoryException {
        String requestURI = choreoCpProjectsEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", "query {" +
                    "      commitHistory(componentId: \"" + id + "\") {" +
                    "          author {" +
                    "            name," +
                    "            date," +
                    "            email," +
                    "            avatarUrl" +
                    "          }," +
                    "        message" +
                    "        sha" +
                    "        isLatest" +
                    "    }" +
                    "  }");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            log.debug(responseBody);
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new GetCommitHistoryException(statusCode, responseBody);
            }

            return new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonObject("data")
                    .getAsJsonArray("commitHistory");
        }
    }


    /**
     * Retrieve commit history of a component with custom branch
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @return A JsonArray of commit history
     * @throws IOException               if an IO error occurs when sending or receiving request
     * @throws GetCommitHistoryException if retrieving the component commit history fails
     */
    public JsonArray getCommitHistorySub(String accessToken)
            throws IOException, GetCommitHistoryException {
        String requestURI = choreoCpProjectsEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        String branchName = "feature-v2";
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", "query {" +
                    "      commitHistory(componentId: \"" + id + "\", branch: \"" + branchName + "\") {" +
                    "          author {" +
                    "            name," +
                    "            date," +
                    "            email," +
                    "            avatarUrl" +
                    "          }," +
                    "        message" +
                    "        sha" +
                    "        isLatest" +
                    "    }" +
                    "  }");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            log.debug(responseBody);
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new GetCommitHistoryException(statusCode, responseBody);
            }

            return new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonObject("data")
                    .getAsJsonArray("commitHistory");
        }
    }

    /**
     * Add deployment configurations to a Choreo component
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param orgHandler  Choreo organization handle
     */
    public void addConfigurations(String accessToken, String orgHandler, String env)
            throws GetCommitHistoryException, NoLatestCommitHashFoundException, IOException, InterruptedException,
            AddConfigurationsException, NoLatestAppEnvIdFoundException, NoLatestApiVersionFoundException {
        JsonArray commitHistory = getCommitHistory(accessToken);
        String latestCommitSha = getLatestCommitHash(commitHistory);
        String latestVersionId = getLatestApiVersion().getId();
        String targetEnvIdToDeploy = getLatestAppEnvId(env);
        HashMap<String, Object> requestBodyMap = new HashMap<>() {{
            put("applyNow", false);
            put("commitHash", latestCommitSha);
            put("configs", new ArrayList<>());
            put("moduleName", name);
            put("operation", 0);
            put("sourceUuid", "");
        }};
        String requestURI = choreoEndpoint.concat("/orgs/").concat(orgHandler).concat("/projects/").concat(projectId)
                .concat("/components/".concat(id).concat("/envs/").concat(targetEnvIdToDeploy).concat("/")
                        .concat(latestVersionId).concat("/configurations"));
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            log.debug(responseBody);
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new AddConfigurationsException(statusCode, responseBody);
            }
        }
    }

    /**
     * Deploy a Choreo component
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param orgHandle   Choreo organization handle
     * @param orgUUID     Choreo organization UUID
     */
    public void deploy(String accessToken, String orgHandle, String orgUUID)
            throws IOException, InterruptedException, NoLatestAppEnvIdFoundException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException, NoLatestCommitHashFoundException, GetCommitHistoryException,
            ComponentDeploymentTimeoutException, NoLatestApiVersionFoundException, ComponentDeploymentFailureException, GetDeploymentsStatusCheckException {

        JsonArray commitHistory = getCommitHistory(accessToken);
        String latestCommitSha = getLatestCommitHash(commitHistory);
        String latestVersionId = getLatestApiVersion().getId();
        String devEnvIdToDeploy = getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String branch = getRepository().getBranch();

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", "mutation {" +
                    "     deployComponent(" +
                    "     deployment: {" +
                    "     componentId: \"" + id + "\"," +
                    "     versionId: \"" + latestVersionId + "\"," +
                    "     envId: \"" + devEnvIdToDeploy + "\"," +
                    "     branch: \"" + branch + "\"," +
                    "     sha: \"" + latestCommitSha + "\"," +
                    "     cron: \"\" " +
                    "     }) { " +
                    "     message" +
                    "     success" +
                    "     } }");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpPost request = new HttpPost(choreoCpProjectsEndpoint.concat("/graphql"));

        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new ComponentDeploymentException(statusCode, responseBody);
            }
            JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("deployComponent");
            if (!jsonObject.get("success").getAsBoolean()) {
                throw new ComponentDeploymentFailureException();
            }
        }
        waitForComponentDeploymentSuccess(accessToken, orgHandle, orgUUID, latestVersionId, devEnvIdToDeploy);

    }

    /**
     * Promote a component version from one environment to another.
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param sourceEnv   Source environment from which the version should be picked up
     * @param targetEnv   Target environment to which the version should be picked up
     */
    public void promote(String accessToken, String sourceEnv, String targetEnv)
            throws NoLatestApiVersionFoundException, ReleaseIdNotFoundException, NoLatestAppEnvIdFoundException,
            IOException, ComponentDeploymentStatusCheckException, InterruptedException,
            ComponentDeploymentTimeoutException, ComponentDeploymentException, ComponentDeploymentFailureException {
        String latestVersionId = getLatestApiVersion().getId();
        String targetEnvId = getLatestAppEnvId(targetEnv);

        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", getId());
                put("apiVersionId", latestVersionId);
                put("sourceReleaseId", getReleaseIdForEnvironment(sourceEnv));
                put("targetEnvironmentId", targetEnvId);
            }
        };
        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/promote.mustache",
                requestParams);
        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        HttpPost request = new HttpPost(choreoCpProjectsEndpoint.concat("/graphql"));
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new ComponentDeploymentException(statusCode, responseBody);
            }
            String promoteResult = new JsonParser().parse(responseBody)
                    .getAsJsonObject()
                    .getAsJsonObject("data")
                    .getAsJsonPrimitive("promote")
                    .getAsString();
            if (!"success".equals(promoteResult)) {
                throw new ComponentDeploymentFailureException();
            }
        }
        waitForComponentDeploymentSuccess(accessToken, orgHandler, orgId, latestVersionId, targetEnvId);
    }

    public void promoteNewVersion(String accessToken, String sourceReleaseId, String targetEnvId)
            throws NoLatestApiVersionFoundException, IOException, ComponentDeploymentException,
            ComponentDeploymentFailureException, ComponentDeploymentStatusCheckException, InterruptedException,
            ComponentDeploymentTimeoutException {
        String latestVersionId = getLatestApiVersion().getId();

        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", getId());
                put("apiVersionId", latestVersionId);
                put("sourceReleaseId", sourceReleaseId);
                put("targetEnvironmentId", targetEnvId);
            }
        };
        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/promote.mustache",
                requestParams);
        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        HttpPost request = new HttpPost(choreoCpProjectsEndpoint.concat("/graphql"));
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new ComponentDeploymentException(statusCode, responseBody);
            }
            String promoteResult = new JsonParser().parse(responseBody)
                    .getAsJsonObject()
                    .getAsJsonObject("data")
                    .getAsJsonPrimitive("promote")
                    .getAsString();
            if (!"success".equals(promoteResult)) {
                throw new ComponentDeploymentFailureException();
            }
        }
        waitForComponentDeploymentSuccess(accessToken, orgHandler, orgId, latestVersionId, targetEnvId);
    }

    /**
     * Wait until the Choreo component deployment is successful
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param orgHandle   Choreo organization handle
     * @param orgUUID     Choreo organization UUID
     * @param versionId   the ID of the latest API version
     */
    public void waitForComponentDeploymentSuccess(String accessToken, String orgHandle, String orgUUID,
                                                  String versionId, String envId)
            throws InterruptedException, ComponentDeploymentStatusCheckException, ComponentDeploymentTimeoutException {
        waitForDeploymentStatusByVersion(accessToken, versionId);

        String gqlQuery =
                "query {" +
                        "      componentDeployment(" +
                        "    orgHandler: \"" + orgHandle + "\"" +
                        "    orgUuid:\"" + orgUUID + "\"" +
                        "    componentId:\"" + id + "\" " +
                        "    versionId:\"" + versionId + "\"" +
                        "    environmentId:\"" + envId + "\") {        " +
                        "    environmentId" +
                        "    configCount" +
                        "    apiId" +
                        "    releaseId" +
                        "    build{" +
                        "    buildId" +
                        "    deployedAt" +
                        "    commit {" +
                        "    author {" +
                        "    name" +
                        "    date" +
                        "    email" +
                        "    avatarUrl" +
                        "    }" +
                        "    sha" +
                        "    message" +
                        "    isLatest}}" +
                        "    invokeUrl" +
                        "    versionId" +
                        "    deploymentStatus" +
                        "    deploymentStatusV2" +
                        "    version" +
                        "    cron" +
                        "    }}";


        int numberOfTries = 0;
        long waitForSeconds = 6;
        long timeTakenInSeconds = 0;

        while (timeTakenInSeconds < Constant.COMPONENT_DEPLOY_TIMEOUT_SECONDS) {
            TimeUnit.SECONDS.sleep(waitForSeconds);
            timeTakenInSeconds += waitForSeconds;
            ++numberOfTries;

            try {
                JsonObject response = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

                String status = response.getAsJsonObject()
                        .getAsJsonObject("data").getAsJsonObject("componentDeployment")
                        .get("deploymentStatusV2").getAsString();
                if (status.equals("ACTIVE")) {
                    log.debug("waitForComponentDeploymentSuccess()... " + numberOfTries + " tries taken to succeed");
                    return;
                } else if (status.equals("ERROR")) {
                    throw new ComponentDeploymentStatusCheckException("deploymentStatusV2 is ERROR");
                }

                // If still waiting after 10 attempts, increase the wait time between calls by 2 seconds
                // to reduce sending too many requests
                if (numberOfTries == 10) {
                    waitForSeconds += 5;
                }

                log.debug("waitForComponentDeploymentSuccess()... " + numberOfTries + " attempts, retrying");
            } catch (GraphQLException e) {
                throw new ComponentDeploymentStatusCheckException(e);
            }
        }
        throw new ComponentDeploymentTimeoutException();

    }

    private void waitForDeploymentStatusByVersion(String accessToken, String versionId)
            throws InterruptedException, ComponentDeploymentStatusCheckException {
        String gqlQuery = "query {" +
                "  deploymentStatusByVersion(" +
                "    componentId: \"" + id + "\"" +
                "    versionId: \"" + versionId + "\"" +
                "  ) {" +
                "    id" +
                "    sha" +
                "    completed_at" +
                "    started_at" +
                "    name" +
                "    status" +
                "    conclusion" +
                "  }" +
                "}";

        int numberOfTries = 0;
        long waitForSeconds = 6;
        long timeTakenInSeconds = 0;

        while (timeTakenInSeconds < Constant.COMPONENT_DEPLOY_TIMEOUT_SECONDS) {
            TimeUnit.SECONDS.sleep(waitForSeconds);
            timeTakenInSeconds += waitForSeconds;
            ++numberOfTries;

            try {
                JsonObject response = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

                JsonArray deploymentJsonArray = response.getAsJsonObject()
                        .getAsJsonObject("data").getAsJsonArray("deploymentStatusByVersion");

                if (deploymentJsonArray.size() == 0) {
                    continue;
                }

                String status = ((JsonObject) deploymentJsonArray.get(0)).get("status").getAsString();

                if (status.equals("completed")) {
                    String conclusion = ((JsonObject) deploymentJsonArray.get(0)).get("conclusion").getAsString();

                    if (conclusion.equals("success")) {
                        log.debug("waitForDeploymentStatusByVersion()... " + numberOfTries + " tries taken to succeed");
                        return;
                    } else {
                        throw new ComponentDeploymentStatusCheckException("Component deployment was not successful, " +
                                "conclusion: " + conclusion);
                    }
                }

                // If still waiting after 10 attempts, increase the wait time between calls by 2 seconds
                // to reduce sending too many requests
                if (numberOfTries == 10) {
                    waitForSeconds += 2;
                }

                log.debug("waitForDeploymentStatusByVersion()... " + numberOfTries + " attempts, retrying");
            } catch (GraphQLException e) {
                throw new ComponentDeploymentStatusCheckException(e);
            }
        }
    }

    /**
     * Get details about the deployments of a component
     *
     * @param accessToken
     * @param orgHandle
     * @param orgUUID
     * @param versionId
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ComponentDeploymentStatusCheckException
     * @throws ComponentDeploymentTimeoutException
     * @throws GetDeploymentsStatusCheckException
     */
    public JsonArray getDeployments(String accessToken, String orgHandle, String orgUUID,
                                    String versionId)
            throws GetDeploymentsStatusCheckException {
        String gqlQuery = "query {" +
                "  deployments(" +
                "    orgHandler: \"" + orgHandle + "\"" +
                "    orgUuid:\"" + orgUUID + "\"" +
                "    componentId: \"" + id + "\"" +
                "    versionId: \"" + versionId + "\"" +
                "  ) {" +
                "    environmentId" +
                "    environmentName" +
                "    configCount" +
                "    apiId" +
                "    releaseId" +
                "    build{" +
                "      buildId" +
                "      deployedAt" +
                "      commit {" +
                "        author {" +
                "          name" +
                "          date" +
                "          email" +
                "          avatarUrl" +
                "        }" +
                "        sha" +
                "        message" +
                "        isLatest" +
                "      }" +
                "    }" +
                "    invokeUrl" +
                "    versionId" +
                "    deploymentStatus" +
                "    version" +
                "    cron" +
                "  }" +
                "}";

        try {
            JsonObject response = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

            JsonArray deploymentJsonArray = response.getAsJsonObject()
                    .getAsJsonObject("data").getAsJsonArray("deployments");
            return deploymentJsonArray;
        } catch (GraphQLException e) {
            throw new GetDeploymentsStatusCheckException(e);
        }

    }

    /**
     * Get details about the revisions of an api
     *
     * @param accessToken
     * @param apiId
     * @param orgUUID
     * @return
     * @throws Exception
     */
    public JsonArray getRevisions(String accessToken, String apiId, String orgUUID)
            throws Exception {
        String requestURI = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT)
                .concat("/api/am/publisher/v2/apis/")
                .concat(apiId).concat("/revisions?organizationId=")
                .concat(orgUUID);
        HttpGet request = new HttpGet(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonObject responseJSON = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonArray revisions = (JsonArray) responseJSON.get("list");
            if (revisions != null) {
                return revisions;
            }
            throw new ApiKeyNotFoundException();
        }
    }

    /**
     * Get the latest commit hash from list of commit hashes
     *
     * @param commitHistory The list of commit hashes
     * @return The latest commit hash
     */
    public String getLatestCommitHash(JsonArray commitHistory) throws NoLatestCommitHashFoundException {
        for (JsonElement commit : commitHistory) {
            JsonObject commitJsonObject = commit.getAsJsonObject();
            String isLatest =
                    commitJsonObject.get("isLatest").isJsonNull() ? "" : commitJsonObject.get("isLatest").getAsString();
            if (Objects.equals(isLatest, "true")) {
                return commitJsonObject.get("sha").getAsString();
            }
        }
        throw new NoLatestCommitHashFoundException();
    }

    public String getLatestCommitHash(Commit[] commitHistory) throws NoLatestCommitHashFoundException {
        for (Commit commit : commitHistory) {
            if (commit.isLatest()) {
                return commit.getSha();
            }
        }
        throw new NoLatestCommitHashFoundException();
    }

    /**
     * Get the app environment ID of the latest API version
     *
     * @param environment The environment name
     * @return The app environment ID of the latest API version
     */
    public String getLatestAppEnvId(String environment)
            throws NoLatestAppEnvIdFoundException, NoLatestApiVersionFoundException {
        ApiVersion latestApiVersion = getLatestApiVersion();
        for (AppEnvVersion latestAppEnvVersion : latestApiVersion.getAppEnvVersions()) {
            if (Objects.equals(latestAppEnvVersion.getRelease().getMetadata().getChoreoEnv(), environment)) {
                return latestAppEnvVersion.getEnvironmentId();
            }
        }
        throw new NoLatestAppEnvIdFoundException();
    }

    public String getReleaseIdForEnvironment(Environment env) throws NoLatestApiVersionFoundException {
        ApiVersion latestApiVersion = getLatestApiVersion();
        for (AppEnvVersion latestAppEnvVersion : latestApiVersion.getAppEnvVersions()) {
            if (latestAppEnvVersion.getEnvironmentId().equals(env.getId())) {
                return latestAppEnvVersion.getReleaseId();
            }
        }

        throw new IllegalStateException("Corresponding environment " + env.getId() + "does not exist in component");
    }

    /**
     * Get the app environment ID for a given API version
     *
     * @param apiVersion ApiVersion which require the app env id
     * @param environment The environment name
     * @return The app environment ID of the latest API version
     */
    public String getAppEnvIdForVersion(ApiVersion apiVersion, String environment)
            throws NoLatestAppEnvIdFoundException {
        for (AppEnvVersion appEnvVersion : apiVersion.getAppEnvVersions()) {
            if (Objects.equals(appEnvVersion.getRelease().getMetadata().getChoreoEnv(), environment)) {
                return appEnvVersion.getEnvironmentId();
            }
        }
        throw new NoLatestAppEnvIdFoundException();
    }

    /**
     * Get the latest API version of a Choreo component
     *
     * @return The latest API version
     */
    public ApiVersion getLatestApiVersion() throws NoLatestApiVersionFoundException {
        for (ApiVersion apiVersion : apiVersions) {
            if (apiVersion.isLatest()) {
                return apiVersion;
            }
        }
        throw new NoLatestApiVersionFoundException();
    }

    /**
     * Get Component information query for the graphql call
     *
     * @return request body containing graphql query
     */
    public String getComponentInformation() throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForComponentInformation.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("projectId", project.getId());
        queryParams.put("componentHandler", handler);
        mustache.execute(writer, queryParams).flush();
        String graphQlQuery = writer.toString();
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(gqlRequestPayload);
    }

    /**
     * Get Component namespace for given environment
     *
     * @return request namespace
     */
    public String getNamespaceForEnvironment(String accessToken, String environment) throws IOException, EnvironmentDetailsCheckException, NamespaceNotFoundException {
        String requestURI = choreoEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/graphql/requests/getEnvironments.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("orgUUID", organization.getOrgUUID());
        queryParams.put("projectId", projectId);
        mustache.execute(writer, queryParams).flush();
        String graphQlQuery = writer.toString();
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new EnvironmentDetailsCheckException(statusCode, responseBody);
            }
            JsonObject bodyJsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonArray environmentInfoArray = bodyJsonObject.getAsJsonObject("data").getAsJsonArray("environments");
            for (JsonElement environmentInfo : environmentInfoArray) {
                if (environmentInfo.getAsJsonObject().has("choreoEnv") && environmentInfo.getAsJsonObject().get("choreoEnv").getAsString().equals(environment)) {
                    return environmentInfo.getAsJsonObject().get("namespace").getAsString();
                }
            }
            throw new NamespaceNotFoundException();
        }
    }

    /**
     * Get Component environment information graphql query
     *
     * @param releaseId release id for your component
     * @return request body containing graphql query
     */
    public String getComponentObservabilityIdsQuery(String releaseId) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/graphql/requests/getObservabilityIds.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("releaseId", releaseId);
        mustache.execute(writer, queryParams).flush();
        String graphQlQuery = writer.toString();
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(gqlRequestPayload);
    }

    /**
     * Get Component observability ids
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param releaseId   release id for your component
     * @return request body containing graphql query
     */
    public ObservabilityIdInformation getComponentObservabilityIdForReleaseId(String accessToken, String releaseId) throws IOException, ObservabilityIdCheckException, InterruptedException, ObservabilityIdNotFoundException {
        String requestURI = choreoEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        String requestBody = getComponentObservabilityIdsQuery(releaseId);
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new ObservabilityIdCheckException(statusCode, responseBody);
            }
            JsonObject bodyJsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonArray obsIdJsonArray = bodyJsonObject.getAsJsonObject("data").getAsJsonArray("observerbilityIds");
            ObservabilityIdInformation[] obsIds = gson.fromJson(obsIdJsonArray, ObservabilityIdInformation[].class);
            for (ObservabilityIdInformation obsId : obsIds) {
                if (obsId.getReleaseId().equals(releaseId)) {
                    return obsId;
                }
            }
            throw new ObservabilityIdNotFoundException();
        }
    }

    /**
     * Get Component invoke information
     *
     * @param accessToken   OAuth token to invoke the Chorea backend
     * @param componentType type of the component
     * @param environment   environment of the deployment
     * @return Invoke information related to requested environment
     */
    public InvokeInformation  getInvokeInformation(String accessToken, String componentType, String environment) throws
            IOException, NoLatestApiVersionFoundException, InterruptedException, ComponentInvokeInformationCheckException, InvokeInformationNotFoundException {
        String requestURI = choreoEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/deploy/graphql/queryForInvokeInformation.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("orgHandler", organization.getOrgHandle());
        queryParams.put("orgUuid", organization.getOrgUUID());
        queryParams.put("componentId", id);
        String latestVersionId = getLatestApiVersion().getId();
        queryParams.put("versionId", latestVersionId);
        queryParams.put("componentType", componentType);
        mustache.execute(writer, queryParams).flush();
        String graphQlQuery = writer.toString();
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new ComponentInvokeInformationCheckException(statusCode, responseBody);
            }
            JsonObject bodyJsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonArray invokeInformationJsonArray = bodyJsonObject.getAsJsonObject("data").getAsJsonArray("invokeInformation");
           InvokeInformation[] invokeInformation = gson.fromJson(invokeInformationJsonArray, InvokeInformation [].class);
            for (InvokeInformation  envInvokeInformation : invokeInformation ) {
                if (Objects.equals(envInvokeInformation.getEnvironmentName(), environment)) {
                    return envInvokeInformation;
                }
            }
            throw new InvokeInformationNotFoundException();
        }
    }

    /**
     * Get api-key to invoke the application from APIM
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param apiId       apiId for the deployed component
     * @return request body containing graphql query
     */
    public String getAPIKeyForInvoke(String accessToken, String apiId, String keyType) throws  IOException,
            APIKeyGenerationCheckException, ApiKeyNotFoundException {
        String requestURI = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT)
                .concat(Constant.APIS_ENDPOINT)
                .concat("/")
                .concat(apiId)
                .concat("/generate-key")
                .concat("?")
                .concat(Constant.ORGANIZATION_ID)
                .concat("=")
                .concat(organization.getOrgUUID())
                .concat("&keyType=")
                .concat(keyType);
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                "",
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new APIKeyGenerationCheckException(statusCode, responseBody);
            }
            JsonObject responseJSON = new JsonParser().parse(responseBody).getAsJsonObject();
            String apiKey = responseJSON.get("apikey").toString();
            if (apiKey != null) {
                return apiKey;
            }
            throw new ApiKeyNotFoundException();
        }
    }



    public String getReleaseIdForEnvironment(String env) throws ReleaseIdNotFoundException {
        AppEnvVersion[] appEnvVersions = getApiVersions().get(0).getAppEnvVersions().toArray(new AppEnvVersion[0]);
        for (AppEnvVersion appEnvVersion : appEnvVersions) {
            if (appEnvVersion.getEnvironment().equals(env)) {
                return appEnvVersion.getReleaseId();
            }
        }
        throw new ReleaseIdNotFoundException();
    }

    public String getReleaseIdForEnvironmentV2(String env) throws ReleaseIdNotFoundException {
        AppEnvVersion[] appEnvVersions = getApiVersions().get(1).getAppEnvVersions().toArray(new AppEnvVersion[0]);
        for (AppEnvVersion appEnvVersion : appEnvVersions) {
            if (appEnvVersion.getEnvironment().equals(env)) {
                return appEnvVersion.getReleaseId();
            }
        }

        throw new ReleaseIdNotFoundException();
    }

    public JsonObject fetchAST(String accessToken, String env) throws IOException, ReleaseIdNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, InterruptedException, ObservabilityASTCheckException {
        String requestURI = configCPGatewayEndpoint.concat(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX);
        String releaseId = getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation = getComponentObservabilityIdForReleaseId(accessToken, releaseId);
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForAst.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("obsId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        mustache.execute(writer, queryParams).flush();
        String body = writer.toString();
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build(); CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new ObservabilityASTCheckException(statusCode, responseBody);
            }
            JsonParser parser = new JsonParser();
            String astString = new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("ast").get("ast").getAsString();
            return (JsonObject) parser.parse(astString);
        }
    }

    public void waitForMetricsData(String accessToken, String env) throws ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, IOException, InterruptedException,
            ObservabilityDataNotFoundException, ObservabilityDataCheckException {
        String requestURI = configCPGatewayEndpoint.concat(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX);
        String releaseId = getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation = getComponentObservabilityIdForReleaseId(accessToken, releaseId);
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForMetricDensity.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        mustache.execute(writer, queryParams).flush();
        String requestBody = writer.toString();
        int attempts = 0;
        log.info("Waiting till observability data appear");
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);
        while (attempts < 10) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                    throw new ObservabilityDataCheckException(statusCode, responseBody);
                }
                int count = new JsonParser()
                        .parse(responseBody)
                        .getAsJsonObject()
                        .getAsJsonObject("data")
                        .getAsJsonObject("metricDensity")
                        .getAsJsonArray("metricCounts")
                        .get(0)
                        .getAsJsonObject()
                        .get("count")
                        .getAsInt();
                if (count > 0) {
                    break;
                }
                log.debug("Observability data has not appeared, trying again. Attempt : " + attempts);
                Thread.sleep(3000);
                attempts++;
                if (attempts == 10) {
                    log.warn("Exceeding maximum number of attempts for checking observability data.");
                    throw new ObservabilityDataNotFoundException();
                }
            }
        }
    }

    public void waitForTraceData(String accessToken, String env) throws ReleaseIdNotFoundException,
            ObservabilityIdNotFoundException, ObservabilityIdCheckException, IOException, InterruptedException,
            ObservabilityDataNotFoundException, ObservabilityDataCheckException, ObservabilityASTCheckException {
        JsonObject ast = fetchAST(accessToken, env);
        String moduleId = ast.get("packageOrg").getAsString() + "/" + ast.get("packageName").getAsString() + ":" + ast.get("packageVersion").getAsString();
        String releaseId = getReleaseIdForEnvironment(env);
        ObservabilityIdInformation observabilityIdInformation = getComponentObservabilityIdForReleaseId(accessToken, releaseId);
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForTraceList.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("observeId", observabilityIdInformation.getObsId());
        queryParams.put("version", observabilityIdInformation.getVerzion());
        queryParams.put("moduleId", moduleId);
        queryParams.put("entryPointFuncModule", moduleId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        queryParams.put("from", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)));
        queryParams.put("to", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)));
        mustache.execute(writer, queryParams).flush();
        String requestBody = writer.toString();

        log.info("Waiting till trace data appear");
        String requestURI = configCPGatewayEndpoint.concat(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX);
        HttpPost request = new HttpPost(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        StringEntity requestEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        int attempts = 0;
        while (attempts < 10) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build(); CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                    throw new ObservabilityDataCheckException(statusCode, responseBody);
                }
                int count = new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("requestTraceGroup").get("totalCount").getAsInt();
                if (count > 0) {
                    break;
                }
                log.debug("Observability trace data has not appeared, trying again. Attempt : " + attempts);
                Thread.sleep(3000);
                attempts++;
                if (attempts == 10) {
                    log.warn("Exceeding maximum number of attempts for checking observability trace data.");
                    throw new ObservabilityDataNotFoundException();
                }
            }
        }
    }

    public Environment getEnvironment(Environment[] environments, Constant.Environment env) {
        return Arrays.stream(environments).filter(e -> e.getName().equals(env.name())).findFirst().get();
    }

    public void waitForObservabilityLogs(Environment environment, String accessToken) throws IOException,
            ObservabilityLogsNotFoundException, URISyntaxException,
            ReleaseIdNotFoundException {

        String releaseId = getReleaseIdForEnvironment(environment.getChoreoEnv());
        String namespace = environment.getNamespace();

        log.info("Waiting till observability data appear");
        ObsRequestParam orp = ObsRequestParam.builder().namespace(namespace).releaseId(releaseId).sort("asc").limit("63").build();
        String url = ObservabilityService.getObsUrl(orp,Constant.logType.logsV2);
        for (int i = 0; i < 20; i++) {
            Response res = HttpClientUtil.httpGET(url, accessToken, "");
            ObservabilityLogs obslogs = ObjectMapperUtil.mapStringToObject(ObservabilityLogs.class, res.getRes(), "");

            if (res.getStatusCode()<205  && obslogs.getRows().length > 0) {
                return;
            }
            log.debug("Observability logs has not appeared, trying again. Attempt : " + i);
            SleepUtil.sleep(30);
        }
        log.warn("Exceeding maximum number of attempts for checking observability logs.");
        throw new ObservabilityLogsNotFoundException();
    }

    public void waitForObservabilitySystemMetrics(String accessToken, String env) throws IOException,
            InterruptedException, URISyntaxException, ObservabilitySystemMetricsCheckException,
            ObservabilitySystemMetricsNotFoundException, ReleaseIdNotFoundException, EnvironmentDetailsCheckException,
            NamespaceNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException {
        String releaseId = getReleaseIdForEnvironment(env);
        String namespace = getNamespaceForEnvironment(accessToken, env);
        ObservabilityIdInformation observabilityIdInformation =
                getComponentObservabilityIdForReleaseId(accessToken, releaseId);

        log.info("Waiting till observability data appear");
        String requestURI = configCPGatewayEndpoint.concat(Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX)
                .concat(observabilityIdInformation.getObsId())
                .concat("/metricsV2");
        URIBuilder builder = new URIBuilder(requestURI);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        builder.setParameter("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusDays(1)))
                .setParameter("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).plusMinutes(10)))
                .setParameter("releaseId", releaseId)
                .setParameter("namespace", namespace)
                .setParameter("interval", "15");
        HttpGet request = new HttpGet(builder.build());
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        int attempts = 0;
        while (attempts < 50) {
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                 CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                    throw new ObservabilitySystemMetricsCheckException(statusCode, responseBody);
                }
                int count = new JsonParser()
                        .parse(responseBody)
                        .getAsJsonObject()
                        .getAsJsonArray("rows")
                        .size();
                if (count > 0) {
                    break;
                }
                log.debug("Observability system metrics has not appeared, trying again. Attempt : " + attempts);
                Thread.sleep(6000);
                attempts++;
                if (attempts == 50) {
                    log.warn("Exceeding maximum number of attempts for checking observability system metrics.");
                    throw new ObservabilitySystemMetricsNotFoundException();
                }
            }
        }
    }


    public String getId() {
        return id;
    }


    /**
     * Redeploy a stopped component
     *
     * @param accessToken
     * @param componentId
     * @param releaseId
     * @param orgHandle
     * @throws IOException
     * @throws RedeployException
     */
    public void redeploy(String accessToken, String componentId, String releaseId, String orgHandle) throws IOException, RedeployException {
        String graphQlQuery = "mutation { redeployDeployment(orgHandler: \"" + orgHandle + "\", componentId: \"" + componentId + "\", releaseId: \"" + releaseId + "\", type: \"restAPI\" )}";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        HttpPost request = new HttpPost(choreoCpProjectsEndpoint.concat("/graphql"));

        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new RedeployException(statusCode, responseBody);
            }
        }
    }

    public void undeploy(String accessToken, String componentId, String releaseId, String orgHandle) throws IOException, UndeployException {
        String graphQlQuery = "mutation { stopDeployment(orgHandler: \"" + orgHandle + "\", componentId: \"" + componentId + "\", releaseId: \"" + releaseId + "\", type: \"restAPI\" )}";

        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

        HttpPost request = new HttpPost(choreoCpProjectsEndpoint.concat("/graphql"));

        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new UndeployException(statusCode, responseBody);
            }
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public List<ApiVersion> getApiVersions() {
        return apiVersions;
    }

    public void setApiVersions(List<ApiVersion> apiVersions) {
        this.apiVersions = apiVersions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgHandler() {
        return orgHandler;
    }

    public void setOrgHandler(String orgHandler) {
        this.orgHandler = orgHandler;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public ComponentRepository getRepository() {
        return repository;
    }

    public void setRepository(ComponentRepository repository) {
        this.repository = repository;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ChoreoProject getProject() {
        return project;
    }

    public void setProject(ChoreoProject project) {
        this.project = project;
    }

    public ChoreoOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(ChoreoOrganization organization) {
        this.organization = organization;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrgHandle() {
        return orgHandle;
    }

    public void setOrgHandle(String orgHandle) {
        this.orgHandle = orgHandle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageRegistryId() {
        return imageRegistryId;
    }

    public void setImageRegistryId(String imageRegistryId) {
        this.imageRegistryId = imageRegistryId;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public boolean isHttpBased() {
        return httpBased;
    }

    public void setHttpBased(boolean httpBased) {
        this.httpBased = httpBased;
    }

    public ImageRegistry getImageRegistry() {
        return imageRegistry;
    }

    public void setImageRegistry(ImageRegistry imageRegistry) {
        this.imageRegistry = imageRegistry;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
