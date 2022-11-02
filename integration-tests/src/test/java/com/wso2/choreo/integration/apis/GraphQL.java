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
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.byoc.ByocComponenet;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.componentstatusbyversion.ComponentStatusByVersion;
import com.wso2.choreo.integration.models.createcomponentresponse.CreateComponent;
import com.wso2.choreo.integration.models.deploymentstatus.ComponentDeploymentStatus;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Implements GraphQL API calls and their response validations.
 */
@Slf4j
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


    public static Status deployComponent(ChoreoComponent component, String accessToken)
            throws Exception {
        JsonArray commitHistory = component.getCommitHistory(accessToken);
        String latestCommitSha = component.getLatestCommitHash(commitHistory);

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("componentId", component.getId());
        requestParams.put("latestVersionId", component.getLatestApiVersion().getId());
        requestParams.put("devEnvIdToDeploy", component.getLatestAppEnvId("dev"));
        requestParams.put("branch", component.getRepository().getBranch());
        requestParams.put("latestCommitSha", latestCommitSha);

        String request = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/deployComponent.mustache", requestParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(request), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "deployComponent");

    }

    public static void promoteComponent(HttpClient client, TestActionRunner runner, ChoreoComponent component)
            throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("componentId", component.getId());
        requestParams.put("apiVersionId", component.getLatestApiVersion().getId());
        requestParams.put("sourceReleaseId", component.getReleaseIdForEnvironment("dev"));
        requestParams.put("targetEnvironmentId", component.getLatestAppEnvId("prod"));


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


    public static ComponentStatusByVersion deploymentStatusByVersion(ChoreoComponent component, String accessToken)
            throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("componentId", component.getId());
        requestParams.put("latestVersionId", component.getLatestApiVersion().getId());


        String request = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/deploymentStatusByVersion.mustache", requestParams);

        Response response = null;
        for (int i = 0; i < 36; i++) {
            response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(request), accessToken, "");
            ComponentStatusByVersion[] status = ObjectMapperUtil.mapToCollection(ComponentStatusByVersion[].class, response.getRes(), "deploymentStatusByVersion");
            log.info(response.getRes());
            if (status.length > 0) {
                if (status[0].getStatus().equals("completed") && status[0].getConclusion().equals("success")) {
                    return status[0];
                }
            }
            SleepUtil.sleep(30);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "Component was not deployed successfully");
    }

    public static void componentDeployment(HttpClient client, TestActionRunner runner,
                                           ChoreoComponent component, String envName, String message) throws Exception {
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
                put("message", message);
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

    public static void componentDeployment(ChoreoComponent component, String envName, String accessToken) throws Exception {
        String envId = component.getLatestAppEnvId(envName);

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("orgHandler", component.getOrgHandler());
        requestParams.put("orgUuid", component.getOrganization().getOrgUUID());
        requestParams.put("componentId", component.getId());
        requestParams.put("versionId", component.getLatestApiVersion().getId());
        requestParams.put("environmentId", envId);


        String request = MessageUtils.generateStringFromTemplate("templates/graphql/requests/componentDeployment.mustache", requestParams);

        Response response = null;
        ComponentDeploymentStatus deployments = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(request), accessToken, "");
            deployments = ObjectMapperUtil.mapStringToObject(ComponentDeploymentStatus.class, response.getRes(), "componentDeployment");
            log.info(response.getRes());
            if (deployments.getDeploymentStatusV2().equals("ACTIVE") && deployments.getDeploymentStatus().equals("ACTIVE")) {
                component.setApiId(deployments.getApiId());
                return;
            }
            SleepUtil.sleep(60);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "deploymentStatusV2 is " +
                deployments.getDeploymentStatusV2() + " and deploymentStatus is " + deployments.getDeploymentStatus());
    }

    public static CreateComponent createUserManagedComponent(String repoName, String componentName, String projectId, String accessToken) throws IOException {
        String srcGitHubURL = "https://github.com/" + Configuration.getConfig(ConfigDefinition.GITHUB_ORG) + "/" + repoName;
        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("name", componentName);
        responseParams.put("orgId", ORG_ID);
        responseParams.put("orgHandler", ORG_HANDLE);
        responseParams.put("displayName", componentName);
        responseParams.put("projectId", projectId);
        responseParams.put("srcGitRepoUrl", srcGitHubURL);


        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/createUserManagedComponent.mustache", responseParams);

        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(CreateComponent.class, response.getRes(), "createComponent");
    }


    public static ByocComponenet createBYOCComponent(String componentName, String projectId, String repoName, String dockerfilePath, String accessToken) throws IOException {
      //      String srcGitHubURL = "https://github.com/" + Configuration.getConfig(ConfigDefinition.GITHUB_ORG) + "/" + repoName;

        String srcGitHubURL = "https://github.com/choreo-test-apps/byor-greetings-app2";
        Map<String, String> responseParams = new HashMap<>();

        responseParams.put("name", componentName);
        responseParams.put("orgId", ORG_ID);
        responseParams.put("orgHandler", ORG_HANDLE);
        responseParams.put("displayName", componentName);
        responseParams.put("projectId", projectId);
        responseParams.put("srcGitRepoUrl", srcGitHubURL);
        responseParams.put("dockerfilePath", dockerfilePath);

        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/createBYOCcomponent.mustache", responseParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ByocComponenet.class, response.getRes(), "createByocComponent");
    }

    public static PullRequest[] getComponentPullRequests(String componentId, String accessToken, int expectedPRs) throws IOException, UnexpectedResponseException {
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String request = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/getComponentPullRequests.mustache", params);

        Response response = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(request), accessToken, "");
            PullRequest[] pullRequests = ObjectMapperUtil.mapToCollection(PullRequest[].class, response.getRes(), "componentPullRequests");
            if (pullRequests.length == expectedPRs) {
                return pullRequests;
            }
            SleepUtil.sleep(5);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "Expected PullRequest length" + expectedPRs + "but found " + 0);
    }


    public static ChoreoComponent getComponentDetails(String projectId, String componentHandler, String accessToken) throws IOException {
        Map<String, String> responseParams = new HashMap<>();

        responseParams.put("componentHandler", componentHandler);
        responseParams.put("projectId", projectId);

        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/observability/graphql/queryForComponentInformation.mustache", responseParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(RestApiChoreoComponent.class, response.getRes(), "component");
    }

    public static Environment[] getEnvironments(ChoreoComponent component,String accessToken) throws IOException {
        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("projectId", component.getProjectId());
        responseParams.put("orgUuid", component.getOrganization().getOrgUUID());

        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/getEnvironments.mustache", responseParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Environment[].class, response.getRes(), "environments");
    }

    public static InvokeInformation[] getInvokeInformation(ChoreoComponent component, String componentType, String accessToken) throws IOException, NoLatestApiVersionFoundException {
        Map<String, String> responseParams = new HashMap<>();

        responseParams.put("orgHandler", ORG_HANDLE);
        responseParams.put("orgUuid", component.getOrganization().getOrgUUID());
        responseParams.put("componentId", component.getId());
        responseParams.put("versionId", component.getLatestApiVersion().getId());
        responseParams.put("componentType", componentType);

        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/deploy/graphql/queryForInvokeInformation.mustache", responseParams);
        Response response = HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(InvokeInformation[].class, response.getRes(), "invokeInformation");
    }

    public static Response deleteComponent(String componentId, String projectId, String accessToken) throws IOException {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("orgHandler", ORG_HANDLE);
        requestParam.put("componentId", componentId);
        requestParam.put("projectId", projectId);
        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/deleteComponent.mustache", requestParam);
        return HttpClientUtil.httpPOST(choreoProjectURL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
    }

    public static ByocComponenet[] getProjectComponents(String projectId,String accessToken) throws IOException {
        Map<String, String> requestParam = new HashMap<>();
        requestParam.put("orgHandler", ORG_HANDLE);
        requestParam.put("projectId", projectId);
        String expectedResponse = ComponentUtils.generateStringFromTemplate("templates/graphql/requests/getProjectComponents.mustache", requestParam);
        Response response =  HttpClientUtil.httpPOST(choreoProjectURL,  ObjectMapperUtil.mapToGraphQLQuery(expectedResponse),accessToken, "");
        return ObjectMapperUtil.mapToCollection(ByocComponenet[].class, response.getRes(), "components");
    }

}
