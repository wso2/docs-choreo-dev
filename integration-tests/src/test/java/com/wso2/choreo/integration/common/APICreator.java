package com.wso2.choreo.integration.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.ApiCreationException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APICreator {
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    public String createAPI(String accessToken, String apiName, String apiContext) throws IOException,
            InterruptedException, ApiCreationException {
        String requestURI = Configuration.STS_ENDPOINT.
                concat(Constant.APIS_ENDPOINT).concat("?").concat(Constant.ORGANIZATION_ID).concat("=")
                .concat(Configuration.TEST_CHOREO_ORG_UUID);
        String requestBody = "{\"name\":" +
                "\"" + apiName + "\"," +
                "\"version\":\"" + Constant.DEFAULT_VERSION + "\"," +
                "\"description\":\"This api is used to connect to the" + apiName + " service\"," +
                "\"context\":\"" + apiContext + "\"," +
                "\"policies\":[\"Bronze\"]," +
                "\"endpointConfig\":{\"endpoint_type\":\"http\"," +
                "\"production_endpoints\":{\"url\":\"" + Constant.DEFAULT_ENDPOINT + "\"}," +
                "\"sandbox_endpoints\":{\"url\":\"" + Constant.DEFAULT_ENDPOINT + "\"}}}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.CREATED.value()) {
            throw new ApiCreationException(statusCode, response.body());
        }
        JsonObject responseBody = new JsonParser().parse(response.body()).getAsJsonObject();
        String apiId = responseBody.get(Constant.ID).toString();
        return apiId;
    }

    public String createGraphqlQueryForComponentCreation(String apiName, String projectId, String apiId) {
        String graphQlQuery = "mutation{ createComponent(" +
                "      component: {" +
                "        name: \"" + apiName.toLowerCase() + "\"," +
                "        orgId: " + Configuration.TEST_CHOREO_ORG_ID + "," +
                "        orgHandler: \"" + Configuration.TEST_CHOREO_ORG_HANDLE + "\"," +
                "        displayName: \"" + apiName + "\"," +
                "        displayType: \"" + Constant.displayType.proxy + "\"," +
                "        projectId: \"" + projectId + "\"," +
                "        labels: \"\"," +
                "        version: \"1.0.0\"," +
                "        description: \"\"," +
                "        apiId: " + apiId + "," +
                "        ballerinaVersion: \"swan-lake-alpha5\"," +
                "        triggerChannels: \"\"," +
                "        triggerID: null," +
                "        httpBase: true," +
                "        sampleTemplate: \"\"" +
                "      }){" +
                "        id, orgId, projectId, handler" +
                "      }}";
        return graphQlQuery;
    }
}
