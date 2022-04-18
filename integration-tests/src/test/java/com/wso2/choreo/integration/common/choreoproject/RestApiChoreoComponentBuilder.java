package com.wso2.choreo.integration.common.choreoproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * The ChoreoComponentBuilder to create RestApiChoreoComponents
 */
public class RestApiChoreoComponentBuilder extends AbstractChoreoComponentBuilder {

    /**
     * Constructor
     *
     * @param project The Choreo project the creating component belongs to
     * @param org The Choreo organization the project belongs to
     */
    public RestApiChoreoComponentBuilder(ChoreoProject project, ChoreoOrganization org) {
        super(project, org);
    }

    /**
     * Create RestApiChoreoComponent
     *
     * @param accessToken OAuth token to invoke the Chorea backend
     * @return A RestApiChoreoComponent
     */
    public RestApiChoreoComponent createChoreoComponent(String accessToken, String projectsAPIAcessToken) throws
            IOException, InterruptedException, ComponentCreationException, ComponentCreationStatusCheckException,
            ComponentRetrieveException, ComponentCreationTimeoutException {
        String requestURI = CHOREO_ENDPOINT.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        String graphQlQuery = "mutation{ createComponent(" +
                "      component: {" +
                "        name: \"" + componentName + "\"," +
                "        orgId: " + org.getOrgId() + "," +
                "        orgHandler: \"" + org.getOrgHandle() + "\"," +
                "        displayName: \"" + componentName + "\"," +
                "        displayType: \"" + Constant.displayType.restAPI + "\"," +
                "        projectId: \"" + project.getId() + "\"," +
                "        labels: \"\"," +
                "        version: \"1.0.0\"," +
                "        description: \"\"," +
                "        apiId: \"\"," +
                "        ballerinaVersion: \"swan-lake-alpha5\"," +
                "        triggerChannels: \"\"," +
                "        triggerID: null," +
                "        httpBase: true," +
                "        sampleTemplate: \"\"" +
                "      }){" +
                "        id, orgId, projectId, handler" +
                "      }}";
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", graphQlQuery);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, projectsAPIAcessToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new ComponentCreationException(statusCode, response.body());
        }
        JsonObject choreoComponentJsonObject = new JsonParser().parse(response.body())
                .getAsJsonObject().getAsJsonObject("data").getAsJsonObject("createComponent");
        String componentId = choreoComponentJsonObject.get("id").isJsonNull() ? "" :
                choreoComponentJsonObject.get("id").getAsString();
        String componentHandle = choreoComponentJsonObject.get("handler").isJsonNull() ? "" :
                choreoComponentJsonObject.get("handler").getAsString();
        waitForComponentCreationSuccess(accessToken, org.getOrgHandle(), project.getId(), componentId);
        JsonObject retrievedChoreoComponent =
                retrieveComponentJsonObject(projectsAPIAcessToken, project.getId(), componentHandle);
        Gson gson = new Gson();
        return gson.fromJson(retrievedChoreoComponent.toString(), RestApiChoreoComponent.class);
    }
}
