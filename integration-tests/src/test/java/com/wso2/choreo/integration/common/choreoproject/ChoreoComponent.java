package com.wso2.choreo.integration.common.choreoproject;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
}
