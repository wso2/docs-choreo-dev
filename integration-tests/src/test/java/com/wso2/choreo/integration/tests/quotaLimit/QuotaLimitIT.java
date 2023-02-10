package com.wso2.choreo.integration.tests.quotaLimit;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.ConfigDefinition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.testng.annotations.AfterClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * $(http()
 * <p>
 * quota limit related tests
 */
public class QuotaLimitIT extends TestNGCitrusSpringSupport {

    private String choreoEndpoint;
    private String componentId;

    private String accessToken;
    private String orgHandle;
    private String releaseID;
    private String orgUUID;

    private List<String> orgHandleList = new ArrayList<String>();
    private List<String> releaseIDList = new ArrayList<String>();
    private List<String> componentIDList = new ArrayList<String>();
    private List<ChoreoComponent> componentList = new ArrayList<ChoreoComponent>();

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void beforeClass() throws Exception {
        choreoEndpoint = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);
        String versionID;
        ChoreoComponent component;
        ChoreoOrganization org;


        for (int i = 0; i < 6; i++) {
            accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
            String name = "quotaLimitIT" + i;
            component = ComponentUtils.getReusableComponent(
                    TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(), name);
            org = component.getOrganization();
            orgUUID = org.getOrgUUID();
            orgHandle = org.getOrgHandle();
            orgHandleList.add(orgHandle);
            componentId = component.getId();
            versionID = component.getLatestApiVersion().getId();
            componentList.add(component);
            componentIDList.add(componentId);


            JsonArray deploymentArray = component.getDeployments(accessToken, orgHandle, orgUUID, versionID);
            JsonObject deployment = (JsonObject) deploymentArray.get(0);
            releaseID = deployment.get("releaseId").toString();
            releaseID = releaseID.substring(1, releaseID.length() - 1);
            releaseIDList.add(releaseID);


        }
    }

    @Test
    @CitrusTest
    public void testQuotaNotLimited() throws QuotaLimitException, IOException {
        String graphQlQuery = "query{" +
                "  quotaLimitStatus( " +
                "orgUuid: \"" + orgUUID + "\", resourceType:\"runningDeployment\"){\n" +
                "    isRunningComponentsLimited\n" +
                "  }}\n";

        String requestURI = choreoEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", graphQlQuery);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
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
            log.debug(responseBody);

            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new QuotaLimitException(statusCode, responseBody);
            }
            JsonPrimitive isRateLimited = new JsonParser().parse(responseBody).getAsJsonObject()
                    .getAsJsonObject("data").getAsJsonObject("quotaLimitStatus").getAsJsonPrimitive("isRunningComponentsLimited");

            if (!isRateLimited.getAsBoolean()) {
                return;
            }
        }


        throw new QuotaLimitException(200, "Expected false returned true");
    }

    @Test
    @CitrusTest

    public void testQuotaLimited() throws QuotaLimitException, IOException, InterruptedException, NoLatestAppEnvIdFoundException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException, NoLatestCommitHashFoundException, GetCommitHistoryException,
            ComponentDeploymentTimeoutException, NoLatestApiVersionFoundException, ComponentDeploymentFailureException, GetDeploymentsStatusCheckException {
        for (int i = 0; i < 5; i++) {
            componentList.get(i).deploy(accessToken, orgHandle, orgUUID);
        }
        String graphQlQuery = "query{" +
                "  quotaLimitStatus( " +
                "orgUuid: \"" + orgUUID + "\", resourceType:\"runningDeployment\"){\n" +
                "    isRunningComponentsLimited\n" +
                "  }}\n";

        String requestURI = choreoEndpoint.concat(Constant.GRAPHQL_ENDPOINT_SUFFIX);

        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("query", graphQlQuery);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
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
            log.debug(responseBody);

            if (statusCode != org.apache.http.HttpStatus.SC_OK) {
                throw new QuotaLimitException(statusCode, responseBody);
            }
            JsonPrimitive isRateLimited = new JsonParser().parse(responseBody).getAsJsonObject()
                    .getAsJsonObject("data").getAsJsonObject("quotaLimitStatus").getAsJsonPrimitive("isRunningComponentsLimited");

            if (!isRateLimited.getAsBoolean()) {
                return;
            }
        }


        throw new QuotaLimitException(200, "Expected true returned false");
    }


    @Test
    @CitrusTest
    public void QuotaExceeded() throws Exception, QuotaLimitException {
        try {
            componentList.get(5).deploy(accessToken, orgHandle, orgUUID);
        } catch (ComponentDeploymentException e) {
            int statusCode = e.getStatusCode();
            if (statusCode == 429) {
                return;
            }
            throw new QuotaLimitException(statusCode, e.getMessage());

        }
    }


    @AfterClass
    public void afterClass() throws Exception {

        for (int j = 0; j < 5; j++) {
            componentList.get(j).undeploy(accessToken, componentIDList.get(j), releaseIDList.get(j), orgHandle);
        }
    }
}
