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

package com.wso2.choreo.integration.apis;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.RequestExecutionException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.createcomponentresponse.CreateComponent;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Implements GraphQL API calls and their response validations.
 */
public class GraphQL {

    private static final String choreoProjectURL = Configuration.getConfig(ConfigDefinition.CHOREO_CP_PROJECTS_ENDPOINT) + Constant.GRAPHQL_ENDPOINT_SUFFIX;
    private static final String ORG_HANDLE = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    private static final String ORG_ID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
    private static final String ORG_UUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

    public static void deployComponent(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);

        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", component.getId());
                put("latestVersionId", component.getLatestApiVersion().getId());
                put("devEnvIdToDeploy", component.getLatestAppEnvId("dev"));
                put("branch", component.getRepository().getBranch());
                put("latestCommitSha", latestCommitSha);
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/deployComponent.mustache",
                requestParams);

        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        // Deploy component
        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/graphql/responses/deployComponentSuccess.json"))
                .validate(json()));
    }

    public static void promoteComponent(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", component.getId());
                put("apiVersionId", component.getLatestApiVersion().getId());
                put("sourceReleaseId", component.getReleaseIdForEnvironment("dev"));
                put("targetEnvironmentId", component.getLatestAppEnvId("prod"));
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/promote.mustache",
                requestParams);

        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        // Promote component
        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/graphql/responses/promoteSuccess.json"))
                .validate(json()));
    }

    public static void deploymentStatusByVersion(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        Map<String, String> requestParams = new HashMap<>() {
            {
                put("componentId", component.getId());
                put("latestVersionId", component.getLatestApiVersion().getId());
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/deploymentStatusByVersion.mustache",
                requestParams);

        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        // Poll deployment status
        runner.$(repeatOnError()
                .until("i = 36")
                .index("i")
                .autoSleep(10000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION,
                                        TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs())
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource(
                                        "templates/graphql/responses/deploymentStatusByVersionSuccess.json"))
                                .validate(json())));
    }

    public static void componentDeployment(HttpClient client, TestActionRunner runner,
                                           ChoreoComponent component, String envName,String message) throws Exception {
        String envId = component.getLatestAppEnvId(envName);

        Map<String, String> requestParams = new HashMap<>() {
            {
                put("orgHandler", component.getOrgHandler());
                put("orgUuid", component.getOrganization().getOrgUUID());
                put("componentId", component.getId());
                put("versionId", component.getLatestApiVersion().getId());
                put("environmentId", envId);
            }
        };

        String graphQuery = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/componentDeployment.mustache", requestParams);
        String requestBody = MessageUtils.generateGQLPayload(graphQuery);

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);

        Map<String, String> responseParams = new HashMap<>() {
            {
                put("environmentId", envId);
                put("sha", latestCommitSha);
                put("versionId", component.getLatestApiVersion().getId());
                put("message",message);
            }
        };

        String expectedResponse = ComponentUtils.generateStringFromTemplate(
                "templates/graphql/responses/componentDeploymentSuccess.mustache", responseParams);

        // Poll deployment status
        runner.$(repeatOnError()
                .until("i = 30")
                .index("i")
                .autoSleep(10000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .body(requestBody)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(expectedResponse)));
    }

    public static CreateComponent createBYORComponent(String repoName, String componentName, String projectId, String accessToken) throws IOException {
        String srcGitHubURL = "https://github.com/" + Configuration.getConfig(ConfigDefinition.GITHUB_ORG) + "/" + repoName;
        Map<String, String> responseParams = new HashMap<>() {
            {
                put("name", componentName);
                put("orgId", ORG_ID);
                put("orgHandler", ORG_HANDLE);
                put("displayName", componentName);
                put("projectId", projectId);
                put("srcGitRepoUrl", srcGitHubURL);
            }
        };

        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/createComponent/create_user_managed_component.mustache", responseParams);

        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "", 0);
        return ObjectMapperUtil.mapStringToObject(CreateComponent.class, response.getRes(), "createComponent");
    }

    public static PullRequest[] getComponentPullRequests(String componentId, String accessToken) throws IOException {

//        try {
//            TimeUnit.MINUTES.sleep(2);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String request = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/getComponentPullRequests.mustache", params);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(request), accessToken, "", 10);
        return ObjectMapperUtil.mapToCollection(PullRequest[].class, response.getRes(), "componentPullRequests");

    }

    public static ChoreoComponent getComponentDetails(String projectId, String componentHandler, String accessToken) throws IOException, RequestExecutionException {
        Map<String, String> responseParams = new HashMap<>();

        responseParams.put("componentHandler", componentHandler);
        responseParams.put("projectId", projectId);

        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/observability/graphql/queryForComponentInformation.mustache", responseParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "", 0);
        return ObjectMapperUtil.mapStringToObject(RestApiChoreoComponent.class, response.getRes(), "component");
    }


    public static InvokeInformation[] getInvokeInformation(ChoreoComponent component, String componentType, String accessToken) throws IOException, NoLatestApiVersionFoundException {
        Map<String, String> responseParams = new HashMap<>() {
            {
                put("orgHandler", ORG_HANDLE);
                put("orgUuid", component.getOrganization().getOrgUUID());
                put("componentId", component.getId());
                put("versionId", component.getLatestApiVersion().getId());
                put("componentType", componentType);
            }
        };
        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/deploy/graphql/queryForInvokeInformation.mustache", responseParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "", 0);
        return ObjectMapperUtil.mapToCollection(InvokeInformation[].class, response.getRes(), "invokeInformation");
    }

    public static Response deleteComponent(String orgHandler,String componentId,String projectId,String accessToken) throws IOException {
        Map<String,String> requestParam = new HashMap<>();
        requestParam.put("orgHandler",orgHandler);
        requestParam.put("componentId",componentId);
        requestParam.put("projectId",projectId);
        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/deleteComponent.mustache", requestParam);
       return HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "", 0);
    }

}
