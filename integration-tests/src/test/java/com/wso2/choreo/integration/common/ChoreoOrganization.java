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
import java.net.http.HttpClient;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

        HttpPost request = new HttpPost(Configuration.CHOREO_ENDPOINT.concat("/graphql"));

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
                throw new ProjectCreationException(statusCode, responseBody);
            }

            JsonObject bodyJsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
            JsonObject projectJsonObject = bodyJsonObject.getAsJsonObject("data").getAsJsonObject("createProject");
            String projectId = projectJsonObject.get("id").isJsonNull() ? "" : projectJsonObject.get("id").getAsString();
            Gson gson = new Gson();
            ChoreoProject project = gson.fromJson(projectJsonObject.toString(), ChoreoProject.class);
            projectMap.put(projectId, project);
            return project;
        }
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
