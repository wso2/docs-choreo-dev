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

package com.wso2.choreo.integration.apis.graphql;


import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.ControlPlaneAPIs;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.proxyapi.ProxyDeployment;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.componentstatusbyversion.ComponentStatusByVersion;
import com.wso2.choreo.integration.models.deploymentstatus.ComponentDeploymentStatus;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Implements GraphQL API calls and their response validations.
 */
@Slf4j
public class GraphQL extends ControlPlaneAPI {


    public static ProxyResponse<ChoreoComponent> createGraphqlQueryForComponentCreation(String apiName, String projectId, String apiId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().apiName(apiName.toLowerCase()).orgId(ORG_ID).orgHandler(ORG_HANDLE).displayName(apiName).
                displayType(Constant.displayType.proxy.name()).projectId(projectId).apiId(apiId.replaceAll("\"", "")).build();

        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/api-proxy/graphqlQueryForComponentCreation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
        ChoreoComponent res = ObjectMapperUtil.mapStringToObject(ChoreoComponent.class, response.getRes(), "createComponent");

        return ProxyResponse.<ChoreoComponent>builder().entity(res).response(response).build();

    }

    private static String getComponentQuery(String componentHandler, String id) {
        return  "query{" +
                "      component(" +
                "        projectId: \"" + id + "\"" +
                "        componentHandler: \"" + componentHandler + "\"" +
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
    }




    private static Optional<ChoreoComponent> getComponentByHandler(String accessToken, String componentHandler, String id) throws ComponentRetrieveException {
        String gqlQuery = getComponentQuery(componentHandler,id);

        try {
            JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

            JsonObject componentJson = body.getAsJsonObject().getAsJsonObject("data")
                    .getAsJsonObject("component");

            return Optional.of(new Gson().fromJson(componentJson.toString(), (Type) RestApiChoreoComponent.class));
        } catch (GraphQLException e) {
            throw new ComponentRetrieveException(e);
        }
    }

    public static ChoreoComponent createUserManagedComponent(ChoreoProject project, GraphqlDTO graphqlDTO, String accessToken) throws Exception {
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/createUserManagedComponent.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        JsonObject responseJson = new JsonParser().parse(response.getRes()).getAsJsonObject();

        JsonObject choreoComponentJsonObject = responseJson.getAsJsonObject("data").getAsJsonObject("createComponent");
        String componentId = choreoComponentJsonObject.get("id").isJsonNull() ? "" :
                choreoComponentJsonObject.get("id").getAsString();
        String componentHandler = choreoComponentJsonObject.get("handler").isJsonNull() ? "" :
                choreoComponentJsonObject.get("handler").getAsString();
        ControlPlaneAPIs.waitForComponentCreationSuccess(accessToken, ORG_HANDLE, project.getId(), componentId);
        Optional<ChoreoComponent> component = getComponentByHandler(accessToken, componentHandler,project.getId());
        if (component.isPresent()) {
            return component.get();
        }
        throw new ComponentRetrieveException("Could not find component with handler: " + componentHandler);
    }


    public static void handleConfigInit(String accessToken , String componentId) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String srcCode = MessageUtils.generateStringFromTemplate(
                "templates/graphql/requests/handleConfigInit.mustache", params);
        Response response =  HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(srcCode), accessToken, "");
        if(response.getStatusCode() != HttpStatus.OK.value()){
            throw new ComponentCreationException(response.getStatusCode(),"Failed to init config");
        }
    }
    public static ChoreoComponent createChoreoManagedComponent(GraphqlDTO graphqlDTO, String accessToken) throws IOException {
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createChoreoManagedComponent.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoComponent.class, response.getRes(), "createComponent");
    }


    public static Response promoteComponent(ChoreoComponent component, String accessToken)
            throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(component.getId()).apiVersionId(component.getLatestApiVersion().getId()).
                sourceReleaseId(component.getReleaseIdForEnvironment("dev")).targetEnvironmentId(component.getLatestAppEnvId("prod")).build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/promote.mustache", dto);
        return HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
    }


    public static ChoreoProject createProject(String accessToken) throws IOException {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().name(Constant.TEST_PROJECT_NAME_PREFIX.concat(String.valueOf(new Date().getTime())))
                .description(Constant.TEST_PROJECT_DESCRIPTION).orgId(ORG_ID).orgHandler(ORG_HANDLE).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createProject.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoProject.class, response.getRes(), "createProject");
    }


    public static ChoreoProject createProject(Constant.region region, String accessToken) throws IOException {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().name(Constant.TEST_PROJECT_NAME_PREFIX.concat(String.valueOf(new Date().getTime())))
                .description(Constant.TEST_PROJECT_DESCRIPTION).region(region.name()).orgId(ORG_ID).orgHandler(ORG_HANDLE).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createProject.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoProject.class, response.getRes(), "createProject");
    }

    public static ChoreoComponent createBYOCComponent(GraphqlDTO graphqlDTO, String accessToken) throws IOException {
        String srcGitHubURL = "https://github.com/choreo-test-apps/byor-greetings-app2";
        graphqlDTO.setSrcGitRepoUrl(srcGitHubURL);
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createBYOCcomponent.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoComponent.class, response.getRes(), "createByocComponent");
    }


    public static PullRequest[] getComponentPullRequests(String componentId, String accessToken, int expectedPRs) throws IOException, UnexpectedResponseException {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getComponentPullRequests.mustache", dto);
        Response response = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
            PullRequest[] pullRequests = ObjectMapperUtil.mapToCollection(PullRequest[].class, response.getRes(), "componentPullRequests");
            if (pullRequests.length == expectedPRs) {
                return pullRequests;
            }
            SleepUtil.sleep(35);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "Expected PullRequest length " + expectedPRs + " but found " + 0);
    }


    public static Environment[] getNamespaceForEnvironment(String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgUuid(ORG_UUID).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentObservabilityEnvironmentInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Environment[].class, response.getRes(), "environments");
    }

    public static Environment[] getComponentDeploymentEnvironment(String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgUuid(ORG_UUID).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getComponentDeploymentEnvironments.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Environment[].class, response.getRes(), "environments");
    }

    public static ChoreoComponent getComponentDetails(String projectId, String componentHandler, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentHandler(componentHandler).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoComponent.class, response.getRes(), "component");
    }

    public static ObservabilityIdInformation getComponentObservabilityIdForReleaseId(String releaseId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().releaseId(releaseId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentObservabilityIds.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return Arrays.
                stream(ObjectMapperUtil.mapToCollection(ObservabilityIdInformation[].class,
                        response.getRes(), "observerbilityIds")).filter(ob -> ob.getReleaseId().equals(releaseId)).findFirst().get();
    }


    public static Response deleteComponent(String componentId, String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).componentId(componentId).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/deleteComponent.mustache", dto);
        return HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
    }

    public static ChoreoComponent[] getProjectComponents(String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getProjectComponents.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(ChoreoComponent[].class, response.getRes(), "components");
    }

    public static Commit[] getCommitHistory(String componentId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/commitHistory.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Commit[].class, response.getRes(), "commitHistory");
    }

    public static Commit[] getCommitHistoryBranch(String componentId, String branch, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).branch(branch).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/commitHistoryBranch.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Commit[].class, response.getRes(), "commitHistory");
    }


    public static Status deployComponent(ChoreoComponent component, String accessToken) throws IOException, NoLatestCommitHashFoundException, NoLatestApiVersionFoundException, NoLatestAppEnvIdFoundException {
        Commit[] commits = getCommitHistory(component.getId(), accessToken);
        Commit latestCommit = Commit.getLatestCommit(commits);
        GraphqlDTO dto = GraphqlDTO.builder().componentId(component.getId()).latestVersionId(component.getLatestApiVersion().getId()).
                devEnvIdToDeploy(component.getLatestAppEnvId("dev")).sha(latestCommit.getSha()).branch("main").shaDate(latestCommit.getAuthor().getDate()).build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/deployComponent.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "deployComponent");

    }


    public static ComponentStatusByVersion deploymentStatusByVersion(ChoreoComponent component, String accessToken)
            throws Exception {
        ComponentStatusByVersion componentStatusByVersion = null;
        GraphqlDTO dto = GraphqlDTO.builder().componentId(component.getId()).latestVersionId(component.getLatestApiVersion().getId()).build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/deploymentStatusByVersion.mustache", dto);
        Response response = null;
        for (int i = 0; i < 20; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
            ComponentStatusByVersion[] status = ObjectMapperUtil.mapToCollection(ComponentStatusByVersion[].class, response.getRes(), "deploymentStatusByVersion");

            log.info(response.getRes());
            if (status.length > 0) {
                componentStatusByVersion = status[0];
                if (componentStatusByVersion.getConclusion() != null && componentStatusByVersion.getConclusion().equals("failure")) {
                    throw new UnexpectedResponseException(response.getStatusCode(), "Component was not deployed successfully");
                }
                if (componentStatusByVersion.getStatus().equals("completed") && componentStatusByVersion.getConclusion().equals("success")) {
                    return componentStatusByVersion;
                }
            }
            SleepUtil.sleep(25);
        }

        return componentStatusByVersion;
    }

    public static ComponentDeploymentStatus componentDeployment(ChoreoComponent component, String envName, String accessToken) throws Exception {
        String envId = component.getLatestAppEnvId(envName);
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).orgUuid(ORG_UUID).componentId(component.getId()).versionId(component.getLatestApiVersion().getId()).environmentId(envId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/componentDeployment.mustache", dto);
        Response response;
        ComponentDeploymentStatus deployments = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
            deployments = ObjectMapperUtil.mapStringToObject(ComponentDeploymentStatus.class, response.getRes(), "componentDeployment");
            if (deployments.getDeploymentStatusV2().equals("ERROR") || deployments.getDeploymentStatus().equals("ERROR")) {
                throw new UnexpectedResponseException(response.getStatusCode(), "deploymentStatusV2 is " +
                        deployments.getDeploymentStatusV2() + " and deploymentStatus is " + deployments.getDeploymentStatus());
            }

            if (deployments.getDeploymentStatusV2().equals("ACTIVE") && deployments.getDeploymentStatus().equals("ACTIVE")) {
                component.setApiId(deployments.getApiId());
                break;
            }
            SleepUtil.sleep(60);
        }
        return deployments;
    }

    public static ProxyDeployment getProxyAPIDeploymentDetails(String componentId, String versionId, String envId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).orgUuid(ORG_UUID).componentId(componentId).versionId(versionId).environmentId(envId).build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getProxyDeploymentDetails.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ProxyDeployment.class, response.getRes(), "proxyDeployment");
    }

    /**
     * Create integration component with validations
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @return Component handle for created component
     * @throws IOException If error occurred in object mapping
     */
    public static String createIntegrationComponent(TestActionRunner runner, HttpClient client, String accessToken,
                                                    GraphqlDTO graphqlDTO) throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/createIntegrationComponent/IntegrationComponentCreation.mustache",
                graphqlDTO);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        final String[] componentHandlerArray = new String[1];
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
                .body(new ClassPathResource(
                        "templates/createIntegrationComponent/mutation_create_integration_component_success.json"))
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("createIntegrationComponent");
                    componentHandlerArray[0] = component.get("handle").getAsString();
                }));
        return componentHandlerArray[0];
    }

    /**
     * Retrieve integration component with validations
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @return Retrieved component
     * @throws IOException If error occurred in object mapping
     */
    public static ChoreoComponent retrieveIntegrationComponent(TestActionRunner runner, HttpClient client,
                                                               String accessToken, GraphqlDTO graphqlDTO)
            throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/createUserManagedComponent/graphqlQueryForComponentDetails.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        final ChoreoComponent[] componentArray = new ChoreoComponent[1];
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
                .validate((message, context) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonObject("component");
                    Gson gson = new Gson();
                    componentArray[0] = gson.fromJson(component.toString(), ChoreoComponent.class);
                }));
        return componentArray[0];
    }

    /**
     * Deploy Integration component with validations
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @throws IOException If error occurred in object mapping
     */
    public static void deployComponent(TestActionRunner runner, HttpClient client, String accessToken,
                                       GraphqlDTO graphqlDTO) throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/deployComponent.mustache", graphqlDTO);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

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
                .body(new ClassPathResource("templates/deploy/gql_deploy_component_success.json"))
                .validate(json()));
    }

    /**
     * Get deployment status of the component by version with validation
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @throws IOException If error occurred in object mapping
     */
    public static void getDeploymentStatusByVersion(TestActionRunner runner, HttpClient client, String accessToken,
            GraphqlDTO graphqlDTO) throws IOException {
        getDeploymentStatusByVersion(runner, client, accessToken, graphqlDTO,
                "templates/deploy/deploy_status_by_version_success.json");
    }

    /**
     * Get deployment status of a failed component by version with validation
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @throws IOException If error occurred in object mapping
     */
    public static void getDeploymentStatusOfFailureByVersion(TestActionRunner runner, HttpClient client,
            String accessToken, GraphqlDTO graphqlDTO) throws IOException {
        getDeploymentStatusByVersion(runner, client, accessToken, graphqlDTO,
                "templates/deploy/deploy_status_by_version_failure.json");
    }

    /**
     * Get deployment status of the component by version with validation
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @param responseTemplatePath  path to response template file
     * @throws IOException If error occurred in object mapping
     */
    public static void getDeploymentStatusByVersion(TestActionRunner runner, HttpClient client, String accessToken,
            GraphqlDTO graphqlDTO, String responseTemplatePath) throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/createIntegrationComponent/deploymentStatusByVersion.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        // Poll deployment status
        runner.$(repeatOnError()
                .until("i = 50")
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
                                .body(new ClassPathResource(responseTemplatePath))));
    }

    public static String getRunId(TestActionRunner runner, HttpClient client, String accessToken, GraphqlDTO graphqlDTO) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/createIntegrationComponent/deploymentStatusByVersion.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        final String[] runId = new String[1];

        final ChoreoComponent[] componentArray = new ChoreoComponent[1];
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
                .validate((message, context) -> {
                    runId[0] = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonArray("deploymentStatusByVersion")
                            .get(0)
                            .getAsJsonObject()
                            .get("id")
                            .getAsString();
                }));
        return runId[0];
    }

    /**
     * Get component deployment status with validation
     *
     * @param runner         Test Action Runner
     * @param client         HTTP client
     * @param accessToken    Access token
     * @param graphqlDTO     dto
     * @param responseParams Expected response parameters
     * @return Release ID of the deployment
     * @throws IOException If error occurred in object mapping
     */
    public static void getComponentDeploymentStatus(TestActionRunner runner, HttpClient client, String accessToken,
                                                    GraphqlDTO graphqlDTO, Map<String, String> responseParams)
            throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString("templates/deploy/graphql/componentDeployment.mustache",
                graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);
        String expectedResponse = ComponentUtils.generateStringFromTemplate(
                "templates/createIntegrationComponent/deployment_details_success.mustache",
                responseParams);

        // Poll deployment status
        runner.$(repeatOnError()
                .until("i = 30")
                .index("i")
                .autoSleep(5000)
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

    /**
     * Promote component with validation
     *
     * @param runner      Test action runner
     * @param client      HTTP client
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @throws IOException If error occurred in object mapping
     */
    public static void promoteComponent(TestActionRunner runner, HttpClient client, String accessToken,
                                        GraphqlDTO graphqlDTO) throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/promote.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

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

    /**
     * Stop component deployment
     *
     * @param runner      Test Action Runner
     * @param client      HTTP Client
     * @param accessToken Access Token
     * @param graphqlDTO  DTO
     * @throws IOException If error occurred in object mapping
     */
    public static void stopDeployment(TestActionRunner runner, HttpClient client, String accessToken,
                                      GraphqlDTO graphqlDTO) throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/deploy/stopDeployment.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

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
                .body(new ClassPathResource("templates/deploy/stop_deployment_success.json"))
                .validate(json()));
    }
}
