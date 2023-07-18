package com.wso2.choreo.integration.common.choreoproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Abstract class handle the creation of Choreo component
 */
public abstract class AbstractChoreoComponentBuilder {
    protected final String choreoEndpoint;
    protected final String choreoCpProjectsEndpoint;
    protected static final HttpClient client = HttpClient.newHttpClient();
    protected ChoreoProject project;
    protected ChoreoOrganization org;

    /**
     * Constructor
     *
     * @param project The Choreo project the creating component belongs to
     * @param org     The Choreo organization the project belongs to
     */
    public AbstractChoreoComponentBuilder(ChoreoProject project, ChoreoOrganization org) {
        this.project = project;
        this.org = org;
        choreoEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);
        choreoCpProjectsEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_NEW_APP_SERVICE_ENDPOINT);
    }

    /**
     * An abstract method to create Choreo components
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @return A ChoreoComponent
     */
    public abstract ChoreoComponent createChoreoComponent(String accessToken) throws
            IOException, InterruptedException, ComponentCreationException, ComponentCreationStatusCheckException,
            ComponentRetrieveException, ComponentCreationTimeoutException;

    public void waitForComponentCreationSuccess(String accessToken, String choreoOrgHandle, String projectId,
                                                String componentId)
            throws IOException, InterruptedException, ComponentCreationStatusCheckException,
            ComponentCreationTimeoutException {
        String requestURI = choreoEndpoint.concat("/orgs/" + choreoOrgHandle + "/projects/" + projectId +
                "/components/" + componentId + "/init/status");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .GET()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .build();
        long timeTaken = 0;
        while (timeTaken <= Constant.COMPONENT_CREATE_TIMEOUT) {
            TimeUnit.SECONDS.sleep(2);
            timeTaken += 2000;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                continue;
            }
            if (statusCode != HttpStatus.OK.value()) {
                throw new ComponentCreationStatusCheckException(statusCode, response.body());
            }
            JsonObject dataJsonObject =
                    new JsonParser().parse(response.body()).getAsJsonObject().getAsJsonObject("data");
            String creationStatus =
                    dataJsonObject.get("status").isJsonNull() ? "" : dataJsonObject.get("status").getAsString();
            if (Objects.equals(creationStatus, "completed")) {
                return;
            }
        }
        throw new ComponentCreationTimeoutException();
    }

    /**
     * Get details of created Choreo component
     *
     * @param accessToken     OAuth token to invoke the Chorea backend
     * @param projectId       ID of the Choreo project the component belongs to
     * @param componentHandle The Choreo component handle
     */
    public JsonObject retrieveComponentJsonObject(String accessToken, String projectId, String componentHandle) throws
            IOException, InterruptedException, ComponentRetrieveException {
        String requestURI = choreoCpProjectsEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        String graphQlQuery = "query{" +
                "      component(" +
                "        projectId: \"" + projectId + "\"" +
                "        componentHandler: \"" + componentHandle + "\"" +
                "      ){" +
                "        id," +
                "        name," +
                "        handler," +
                "        description," +
                "        displayType," +
                "        displayName," +
                "        ownerName," +
                "        orgId," +
                "        orgHandler," +
                "        version," +
                "        labels," +
                "        createdAt," +
                "        updatedAt," +
                "        projectId," +
                "        apiId," +
                "        repository{" +
                "          nameApp," +
                "          nameConfig," +
                "          branch," +
                "          organizationApp," +
                "          organizationConfig," +
                "          isUserManage" +
                "        }," +
                "        apiVersions{" +
                "          apiVersion," +
                "          proxyName," +
                "          proxyUrl," +
                "          proxyId," +
                "          id," +
                "          state," +
                "          latest," +
                "          branch," +
                "          appEnvVersions{" +
                "            environmentId," +
                "            releaseId," +
                "            release{" +
                "              id," +
                "              metadata{" +
                "                choreoEnv" +
                "              }," +
                "              environmentId," +
                "              environment," +
                "              gitHash," +
                "              gitOpsHash," +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }";
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", graphQlQuery);
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
            throw new ComponentRetrieveException(statusCode, response.body());
        }
        return new JsonParser().parse(response.body()).getAsJsonObject().getAsJsonObject("data")
                .getAsJsonObject("component");
    }
}
