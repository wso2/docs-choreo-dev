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

package com.wso2.choreo.integration.tests.apiproxy;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.apis.apim.ApiManager;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ApiCreationException;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;


public class CreateAPIProxyFromScratch extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String projectHandler;
    private static String projectId;
    private static String firstAPIName;
    private static String firstContext;
    private static String orgUuid;

    @Autowired
    private HttpClient choreoTestClient;

    @Autowired
    private HttpClient choreoTestClientForSTS;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void beforeClass() throws TokenRetrievalException, IOException, InterruptedException, ProjectCreationException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        ChoreoOrganization testOrg = new ChoreoOrganization(orgHandle, orgId, orgUuid);
        ChoreoProject testProject = testOrg.createProject(accessToken);
        projectHandler = testProject.getHandler();
        projectId = testProject.getId();
        // Create a unique API Name and a Context.
        firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        firstContext = orgUuid.concat("/").concat(projectHandler).concat("/")
                .concat(firstAPIName.toLowerCase());
    }

    @Test
    @CitrusTest
    public void testAPINameValidationForAPIProxyCreation() throws IOException, InterruptedException, ApiCreationException {

        Response response = ApiManager.validateAPIName(firstAPIName, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());

        //  Create an API by providing a unique API Name.
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
                .client(choreoProjectsTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody));

        $(http()
                .client(choreoProjectsTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON));


    }

    @Test(dependsOnMethods = {"testAPINameValidationForAPIProxyCreation"})
    @CitrusTest
    public void testExistingAPI() throws IOException {
        Response response = ApiManager.validateAPIName(firstAPIName, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testExistingAPI"})
    @CitrusTest
    public void testAPIBasePathValidationForAPIProxyCreation() throws IOException {
        // Create a unique API Name.
        String secondAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        String requestBody = new APICreator().getRequestBodyForAPICreation(secondAPIName, firstContext);
        // Test API Proxy creation with an already existing context.
        $(http()
                .client(choreoTestClientForSTS)
                .send()
                .post(Constant.APIS_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID).concat("=")
                        .concat(orgUuid))
                .message()
                .body(requestBody)
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
