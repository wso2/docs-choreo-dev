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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.ControlPlaneAPIs;
import com.wso2.choreo.integration.common.exceptions.GraphQLException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.ProjectRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.util.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Maintain information of the choreo organization used for tests
 */
public class ChoreoOrganization {
    private final static Logger log = LoggerFactory.getLogger(ChoreoOrganization.class);

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
        String gqlQuery = getCreateProjectMutation(
                Constant.TEST_PROJECT_NAME_PREFIX.concat(String.valueOf(new Date().getTime())),
                Constant.TEST_PROJECT_DESCRIPTION);


        try {
            JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);
            JsonObject projectJson = body.getAsJsonObject("data").getAsJsonObject("createProject");
            Gson gson = new Gson();
            ChoreoProject project = gson.fromJson(projectJson.toString(), ChoreoProject.class);
            return project;
        } catch (GraphQLException e) {
            throw new ProjectCreationException(e);
        }
    }

    Optional<ChoreoProject> getProjectByName(String accessToken, String name) throws ProjectRetrievalException {
        loadProjects(accessToken);

        for (ChoreoProject project : projectMap.values()) {
            if (project.getName().equals(name)) {
                return Optional.of(project);
            }
        }

        return Optional.empty();
    }

    List<ChoreoProject> getProjects(String accessToken) throws ProjectRetrievalException {
        loadProjects(accessToken);

        return new ArrayList<>(projectMap.values());
    }

    private void loadProjects(String accessToken) throws ProjectRetrievalException {
        if (projectMap.isEmpty()) {
            try {
                String gqlQuery = getProjectsQuery();

                JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

                JsonArray projectsJson = body.getAsJsonObject("data").getAsJsonArray("projects");
                Gson gson = new Gson();

                for (int i = 0; i < projectsJson.size(); ++i) {
                    JsonObject projectJson = projectsJson.get(i).getAsJsonObject();
                    ChoreoProject project = gson.fromJson(projectJson.toString(), (Type) ChoreoProject.class);
                    projectMap.put(project.getId(), project);
                }
            } catch (GraphQLException e) {
                throw new ProjectRetrievalException(e);
            }
        }
    }

    ChoreoProject createProject(String accessToken, String name, String description)
            throws ProjectCreationException {
        String gqlQuery = getCreateProjectMutation(name, description);

        try {
            JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);
            JsonObject projectJson = body.getAsJsonObject("data").getAsJsonObject("createProject");
            Gson gson = new Gson();
            ChoreoProject project = gson.fromJson(projectJson.toString(), ChoreoProject.class);
            return project;
        } catch (GraphQLException e) {
            throw new ProjectCreationException(e);
        }

    }

    boolean deleteProject(String accessToken, String projectId) {
        String gqlQuery = getDeleteProjectMutation(projectId);

        try {
            ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);
            projectMap.remove(projectId);
            return true;
        } catch (GraphQLException e) {
            log.error("Error while deleting project", e);
        }

        return false;
    }

    private String getDeleteProjectMutation(String projectId) {
        return "mutation{ deleteProject(" +
                "        orgId: " + orgId + "," +
                "        projectId: \"" + projectId + "\"){ status, details }}";
    }

    private String getCreateProjectMutation(String name, String description) {
        return  "mutation{ createProject(project: {" +
                "      name: \"" + name +
                "\", " +
                "      description: \"" + description + "\"," +
                "      orgId: " + orgId + "," +
                "      orgHandler: \"" + orgHandle + "\"," +
                "      version: \"1.0.0\"," +
                "    }){ " +
                "      id, orgId, name, version, createdDate, handler," +
                "    } }";
    }

    private String getProjectsQuery() {
        return "query{projects(orgId: " + orgId + "," +
                "    ){ \n" +
                "     id, orgId, name, version, createdDate, handler,\n" +
                "    } }";
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
