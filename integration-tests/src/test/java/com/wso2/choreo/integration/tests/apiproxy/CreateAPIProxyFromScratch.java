package com.wso2.choreo.integration.tests.apiproxy;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.wso2.choreo.integration.config.Configuration.TEST_CHOREO_ORG_HANDLE;
import static com.wso2.choreo.integration.config.Configuration.TEST_CHOREO_ORG_ID;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;

import com.consol.citrus.message.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TokenHandler;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ApiCreationException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.io.IOException;

public class CreateAPIProxyFromScratch extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String projectHandler;
    private static String projectId;
    private static String firstAPIName;
    private static String firstContext;

    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    private HttpClient choreoTestClientForSTS;

    @BeforeSuite
    public void beforeSuite() throws TokenRetrievalException, IOException, InterruptedException, ProjectCreationException {
        TokenHandler tokenHandler = new TokenHandler();
        accessToken = Constant.BEARER_PREFIX.concat(tokenHandler.getTestToken());
        ChoreoOrganization testOrg = new ChoreoOrganization(TEST_CHOREO_ORG_HANDLE,
                String.valueOf(TEST_CHOREO_ORG_ID), Configuration.TEST_CHOREO_ORG_UUID);
        ChoreoProject testProject = testOrg.createProject(accessToken);
        projectHandler = testProject.getHandler();
        projectId = testProject.getId();
        // Create a unique API Name and a Context.
        firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        firstContext = Configuration.TEST_CHOREO_ORG_UUID.concat("/").concat(projectHandler).concat("/")
                .concat(firstAPIName.toLowerCase());
    }

    @Test
    @CitrusTest
    public void testAPINameValidationForAPIProxyCreation() throws IOException, InterruptedException, ApiCreationException {
        String requestURL = Constant.API_VALIDATE_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID)
                .concat("=").concat(Configuration.TEST_CHOREO_ORG_UUID)
                .concat("&query=name:").concat(firstAPIName);
        // Test API Name validation.
        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(requestURL)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        // Publisher API should return a 404 if the API name is unique.
        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.NOT_FOUND)
                .message()
                .type(MessageType.JSON));

        // Create an API by providing a unique API Name.
        APICreator testAPI = new APICreator();
        String apiId = testAPI.createAPI(accessToken, firstAPIName, firstContext);

        // Test component creation.
        // Create graphql query.
        String graphQlQuery = testAPI.createGraphqlQueryForComponentCreation(firstAPIName, projectId, apiId);
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put(Constant.QUERY, graphQlQuery);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        $(http()
                .client(choreoTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody));

        $(http()
                .client(choreoTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON));

        // Test API Name validation for an existing API Name.
        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(requestURL)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        // Publisher API should return a 200 if the API name already exists.
        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON));
    }

    @Test(dependsOnMethods = {"testAPINameValidationForAPIProxyCreation"})
    @CitrusTest
    public void testAPIBasePathValidationForAPIProxyCreation() {
        // Create a unique API Name.
        String secondAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        // Test API Proxy creation with an already existing context.
        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(Constant.APIS_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID).concat("=")
                        .concat(Configuration.TEST_CHOREO_ORG_UUID))
                .message()
                .body("{\"name\":" +
                        "\"" + secondAPIName + "\"," +
                        "\"version\":\"" + Constant.DEFAULT_VERSION + "\"," +
                        "\"description\":\"This api is used to connect to the" + secondAPIName + " service\"," +
                        "\"context\":\"" + firstContext + "\"," +
                        "\"policies\":[\"Bronze\"]," +
                        "\"endpointConfig\":{\"endpoint_type\":\"http\"," +
                        "\"production_endpoints\":{\"url\":\"" + Constant.DEFAULT_ENDPOINT + "\"}," +
                        "\"sandbox_endpoints\":{\"url\":\"" + Constant.DEFAULT_ENDPOINT + "\"}}}")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        $(http()
                .client(choreoTestClientForSTS)
                .receive()
                .response(HttpStatus.CONFLICT)
                .message()
                .body(new ClassPathResource(
                        "templates/api-proxy/existing_context_error.json"))
                .validate(json()
                        .ignore("$.description")
                        .ignore("$.moreInfo")
                        .ignore("$.error")
                )
                .type(MessageType.JSON));
    }
}
