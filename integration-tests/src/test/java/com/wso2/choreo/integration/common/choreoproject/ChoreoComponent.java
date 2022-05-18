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
import com.google.gson.*;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.exceptions.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.AddConfigurationsException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentFailureException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentDeploymentTimeoutException;
import com.wso2.choreo.integration.common.exceptions.GetCommitHistoryException;
import com.wso2.choreo.integration.common.exceptions.GetDeploymentsStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.RedeployException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Abstract class to represent Choreo component
 */
public abstract class ChoreoComponent {

    private static final String CHOREO_ENDPOINT = Configuration.CHOREO_ENDPOINT;
    private static final String CHOREO_CP_PROJECTS_ENDPOINT = Configuration.CHOREO_CP_PROJECTS_ENDPOINT;
    private static final HttpClient client = HttpClient.newHttpClient();
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
    private ChoreoOrganization organization;
    private final static Logger log = LoggerFactory.getLogger(ChoreoComponent.class);


    /**
     * Retrieve commit history of a component
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @return A JsonArray of commit history
     * @throws IOException               if an IO error occurs when sending or receiving request
     * @throws InterruptedException      if sending request is interrupted
     * @throws GetCommitHistoryException if retrieving the component commit history fails
     */
    public JsonArray getCommitHistory(String accessToken)
            throws IOException, InterruptedException, GetCommitHistoryException {
        String requestURI = CHOREO_CP_PROJECTS_ENDPOINT.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new GetCommitHistoryException(statusCode, response.body());
        }
        return new JsonParser().parse(response.body()).getAsJsonObject().getAsJsonObject("data")
                .getAsJsonArray("commitHistory");
    }

    /**
     * Add deployment configurations to a Choreo component
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @param orgHandler  Choreo organization handle
     */
    public void addConfigurations(String accessToken, String orgHandler)
            throws GetCommitHistoryException, NoLatestCommitHashFoundException, IOException, InterruptedException,
            AddConfigurationsException, NoLatestAppEnvIdFoundException, NoLatestApiVersionFoundException {
        JsonArray commitHistory = getCommitHistory(accessToken);
        String latestCommitSha = getLatestCommitHash(commitHistory);
        String latestVersionId = getLatestApiVersion().getId();
        String devEnvIdToDeploy = getLatestAppEnvId("dev");
        HashMap<String, Object> requestBodyMap = new HashMap<>() {{
            put("applyNow", false);
            put("commitHash", latestCommitSha);
            put("configs", new ArrayList<>());
            put("moduleName", name);
            put("operation", 0);
            put("sourceUuid", "");
        }};
        String requestURI = CHOREO_ENDPOINT.concat("/orgs/").concat(orgHandler).concat("/projects/").concat(projectId)
                .concat("/components/".concat(id).concat("/envs/").concat(devEnvIdToDeploy).concat("/")
                        .concat(latestVersionId).concat("/configurations"));
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new AddConfigurationsException(statusCode, response.body());
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
            ComponentDeploymentTimeoutException, NoLatestApiVersionFoundException, ComponentDeploymentFailureException {
        JsonArray commitHistory = getCommitHistory(accessToken);
        String latestCommitSha = getLatestCommitHash(commitHistory);
        String latestVersionId = getLatestApiVersion().getId();
        String devEnvIdToDeploy = getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String branch = getRepository().getBranch();
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("componentId", id);
            put("versionId", latestVersionId);
            put("envId", devEnvIdToDeploy);
            put("sha", latestCommitSha);
            put("branch", branch);
        }};
        String requestURI = CHOREO_ENDPOINT.concat("/orgs/").concat(orgHandle).concat("/projects/").concat(projectId)
                .concat("/triggers/deployment");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new ComponentDeploymentException(statusCode, response.body());
        }
        JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
        if (!jsonObject.get("success").getAsBoolean()) {
            throw new ComponentDeploymentFailureException();
        }
        waitForComponentDeploymentSuccess(accessToken, orgHandle, orgUUID, latestVersionId);
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
                                                  String versionId)
            throws IOException, InterruptedException, ComponentDeploymentStatusCheckException,
            ComponentDeploymentTimeoutException {
        String requestURI = CHOREO_ENDPOINT.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", "query {" +
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
                    "}");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        long timeTaken = 0;
        while (timeTaken <= Constant.COMPONENT_DEPLOY_TIMEOUT) {
            TimeUnit.SECONDS.sleep(2);
            timeTaken += 2000;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode != HttpStatus.OK.value()) {
                throw new ComponentDeploymentStatusCheckException(statusCode, response.body());
            }
            JsonArray deploymentJsonArray = new JsonParser().parse(response.body()).getAsJsonObject()
                    .getAsJsonObject("data").getAsJsonArray("deployments");
            if (deploymentJsonArray != null && deploymentJsonArray.size() > 0) {
                return;
            }
        }
        throw new ComponentDeploymentTimeoutException();
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
            throws IOException, InterruptedException, ComponentDeploymentStatusCheckException,
            ComponentDeploymentTimeoutException, GetDeploymentsStatusCheckException {
        String requestURI = CHOREO_ENDPOINT.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", "query {" +
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
                    "}");
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new GetDeploymentsStatusCheckException(statusCode, response.body());
        }
        JsonArray deploymentJsonArray = new JsonParser().parse(response.body()).getAsJsonObject()
                .getAsJsonObject("data").getAsJsonArray("deployments");
        return deploymentJsonArray;
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
     * Get Component environment information graphql query
     *
     * @return request body containing graphql query
     */
    public String getComponentEnvironments() throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForComponentEnvironmentInformation.mustache");
        Writer writer = new StringWriter();
        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("orgUUID", organization.getOrgUUID());
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
     * Get Component environment information graphql query
     *
     * @param releaseId release id for your component
     * @return request body containing graphql query
     */
    public String getComponentObservabilityIdsQuery(String releaseId) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/observability/graphql/queryForComponentObservabilityIds.mustache");
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
        String requestURI = CHOREO_ENDPOINT.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
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
            if (statusCode != HttpStatus.OK.value()) {
                throw new ObservabilityIdCheckException(statusCode, responseBody);
            }
            JsonObject bodyJsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonArray obsIdJsonArray = bodyJsonObject.getAsJsonObject("data").getAsJsonArray("observerbilityIds");
            Gson gson = new Gson();
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
    public InvokeInformation getInvokeInformation(String accessToken, String componentType, String environment) throws
            IOException, NoLatestApiVersionFoundException, InterruptedException, ComponentInvokeInformationCheckException, InvokeInformationNotFoundException {
        String requestURI = CHOREO_ENDPOINT.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
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
            if (statusCode != HttpStatus.OK.value()) {
                throw new ComponentInvokeInformationCheckException(statusCode, responseBody);
            }
            JsonObject bodyJsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonArray invokeInformationJsonArray = bodyJsonObject.getAsJsonObject("data").getAsJsonArray("invokeInformation");
            Gson gson = new Gson();
            InvokeInformation[] invokeInformation = gson.fromJson(invokeInformationJsonArray, InvokeInformation[].class);
            for (InvokeInformation envInvokeInformation : invokeInformation) {
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
    public String getAPIKeyForInvoke(String accessToken, String apiId) throws InterruptedException, IOException,
            APIKeyGenerationCheckException, ApiKeyNotFoundException, NoLatestApiVersionFoundException {
        String requestURI = Configuration.STS_ENDPOINT.
                concat(Constant.APIS_ENDPOINT)
                .concat("/")
                .concat(apiId)
                .concat("/generate-key")
                .concat("?")
                .concat(Constant.ORGANIZATION_ID)
                .concat("=")
                .concat(organization.getOrgUUID());
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
            if (statusCode != HttpStatus.OK.value()) {
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

    public void waitTillObservabilityDataPopulate(String accessToken) throws ReleaseIdNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, IOException, InterruptedException, ObservabilityDataNotFoundException {
        String requestURI = Configuration.CHOREO_CP_GW_ENDPOINT.concat(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX);
        String releaseId = getReleaseIdForEnvironment("dev");
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
     * @throws InterruptedException
     * @throws RedeployException
     */
    public void redeploy(String accessToken, String componentId, String releaseId, String orgHandle) throws IOException, InterruptedException, RedeployException {
        String graphQlQuery = "mutation { redeployDeployment(orgHandler: \"" + orgHandle + "\", componentId: \"" + componentId + "\", releaseId: \"" + releaseId + "\", type: \"restAPI\" )}";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);

        HttpPost request = new HttpPost(Configuration.CHOREO_CP_PROJECTS_ENDPOINT.concat("/graphql"));

        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);

        StringEntity requestEntity = new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        request.setEntity(requestEntity);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());
        if (statusCode != HttpStatus.OK.value()) {
            throw new RedeployException(statusCode, responseBody);
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
}
