package com.wso2.choreo.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Maintain information of the choreo organization used for tests
 */
public class ChoreoOrganization {
    protected static final HttpClient client = HttpClient.newHttpClient();
    private final HashMap<String, ChoreoProject> projectMap;
    private String orgHandle;
    private String orgId;
    private String orgUUID;

    /**
     * Constructor
     *
     * @param orgHandle Choreo organization handle
     * @param orgId     Choreo organization id
     * @param orgUUID   Choreo organization UUID
     */
    public ChoreoOrganization(String orgHandle, String orgId, String orgUUID) {
        this.orgHandle = orgHandle;
        this.orgId = orgId;
        this.orgUUID = orgUUID;
        this.projectMap = new HashMap<>();
    }

    /**
     * Create a Choreo project in the Choreo organization
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @return a ChoreoProject instance
     * @throws IOException              if an IO error occurs when sending or receiving request
     * @throws InterruptedException     if sending request is interrupted
     * @throws ProjectCreationException if project creation fails
     */
    public ChoreoProject createProject(String accessToken) throws
            IOException, InterruptedException, ProjectCreationException {
        String graphQlQuery = "mutation{ createProject(project: {" +
                "      name: \"" + Constant.TEST_PROJECT_NAME_PREFIX.concat(String.valueOf(new Date().getTime())) +
                "\", " +
                "      description: \"" + Constant.TEST_PROJECT_DESCRIPTION + "\"," +
                "      orgId: " + orgId + "," +
                "      orgHandler: \"" + orgHandle + "\"," +
                "      version: \"1.0.0\"," +
                "    }){ " +
                "      id, orgId, name, version, createdDate, handler," +
                "    } }";
        HashMap<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(gqlRequestPayload);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Configuration.CHOREO_ENDPOINT.concat("/graphql")))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new ProjectCreationException(statusCode, response.body());
        }
        JsonObject bodyJsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
        JsonObject projectJsonObject = bodyJsonObject.getAsJsonObject("data").getAsJsonObject("createProject");
        String projectId = projectJsonObject.get("id").isJsonNull() ? "" : projectJsonObject.get("id").getAsString();
        Gson gson = new Gson();
        ChoreoProject project = gson.fromJson(projectJsonObject.toString(), ChoreoProject.class);
        projectMap.put(projectId, project);
        return project;
    }

    public String getOrgHandle() {
        return orgHandle;
    }

    public void setOrgHandle(String orgHandle) {
        this.orgHandle = orgHandle;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public ChoreoProject getProjectById(String id) {
        return projectMap.get(id);
    }

    public String getOrgUUID() {
        return orgUUID;
    }

    public void setOrgUUID(String orgUUID) {
        this.orgUUID = orgUUID;
    }
}
