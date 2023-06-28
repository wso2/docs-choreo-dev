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
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.ControlPlaneAPIs;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.GraphQLException;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.componentstatusbyversion.ComponentStatusByVersion;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.graphql.CreateByocComponentResponseDTO;
import com.wso2.choreo.integration.models.graphql.CreateComponentResponseDTO;
import com.wso2.choreo.integration.models.graphql.CreateNewVersionResponseDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.proxyapi.ProxyDeployment;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Implements GraphQL API calls and their response validations.
 */
@Log4j2
public class GraphQL extends ControlPlaneAPI {

    public static ProxyResponse<ChoreoComponent> createGraphqlQueryForComponentCreation(String apiName, String projectId, String apiId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().name(apiName.toLowerCase()).orgId(ORG_ID).orgHandler(ORG_HANDLE).displayName(apiName).
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

    public static Optional<CreateComponentResponseDTO> createUserManagedComponent(TestActionRunner runner, HttpClient client,
                                                                        String queryString, String projectId,
                                                                        String accessToken) throws Exception {
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("orgId", String.valueOf(ORG_ID));
        responseParams.put("projectId", projectId);
        responseParams.put("handler", ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/responses/createComponentSuccess.mustache", responseParams);

        AtomicReference<CreateComponentResponseDTO> responseDTO = new AtomicReference<>();
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
                .body(expectedResponse)
                .validate((message, context) -> {
                    responseDTO.set(ObjectMapperUtil.mapStringToObject(CreateComponentResponseDTO.class,
                            (String) message.getPayload(), "createComponent"));
                }));

        return responseDTO.get() == null ? Optional.empty() :  Optional.of(responseDTO.get());
    }

    public static Optional<CreateByocComponentResponseDTO> createBYOCComponent(TestActionRunner runner, HttpClient client,
                                                                               GraphqlDTO graphqlDTO,
                                                                               String accessToken) throws Exception {
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/createBYOCcomponent.mustache", graphqlDTO);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("orgId", String.valueOf(ORG_ID));
        responseParams.put("projectId", graphqlDTO.getProjectId());
        responseParams.put("handler", ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/responses/createByocComponentSuccess.mustache", responseParams);

        AtomicReference<CreateByocComponentResponseDTO> responseDTO = new AtomicReference<>();
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
                .body(expectedResponse)
                .validate((message, context) -> {
                    responseDTO.set(ObjectMapperUtil.mapStringToObject(CreateByocComponentResponseDTO.class,
                            (String) message.getPayload(), "createByocComponent"));
                }));

        return responseDTO.get() == null ? Optional.empty() :  Optional.of(responseDTO.get());
    }

    public static List<Commit> getCommitHistory(TestActionRunner runner, HttpClient client, String componentId,
                                            String accessToken) throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).build();
        String queryString = ObjectMapperUtil.
                mapObjectToString("templates/graphql/requests/commitHistory.mustache", dto);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        List<Commit> commitList = new ArrayList<>();

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
                    Commit[] commits = ObjectMapperUtil.mapToCollection(Commit[].class, message.getPayload(String.class), "commitHistory");
                    commitList.addAll(List.of(commits));
                }));

        return commitList;
    }

    public static void handleConfigInit(TestActionRunner runner, HttpClient client,
                                        String accessToken , String componentId) throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).build();
        String queryString = ObjectMapperUtil.
                mapObjectToString("templates/graphql/requests/handleConfigInit.mustache", dto);
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
                .validate(jsonPath()
                        .expression("$.data.handleConfigInit.success", "true")
                ));
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

    public static ChoreoProject createProject(String region, String accessToken) throws IOException {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().name(Constant.TEST_PROJECT_NAME_PREFIX.concat(String.valueOf(new Date().getTime())))
                .description(Constant.TEST_PROJECT_DESCRIPTION).region(region).orgId(ORG_ID).orgHandler(ORG_HANDLE).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createProject.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoProject.class, response.getRes(), "createProject");
    }

    public static ChoreoComponent createBYOCComponent(GraphqlDTO graphqlDTO, String accessToken) throws IOException {
        String srcGitHubURL = "https://github.com/choreo-test-apps/byor-greetings-app2";
        graphqlDTO.setSrcGitRepoUrl(srcGitHubURL);
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        graphqlDTO.setComponentType("byocRestApi");
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createBYOCcomponent.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ChoreoComponent.class, response.getRes(), "createByocComponent");
    }
    
    public static Environment[] getNamespaceForEnvironment(String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgUuid(ORG_UUID).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getEnvironments.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Environment[].class, response.getRes(), "environments");
    }

    public static Environment[] getComponentDeploymentEnvironment(String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgUuid(ORG_UUID).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getComponentDeploymentEnvironments.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Environment[].class, response.getRes(), "environments");
    }

    public static ObservabilityIdInformation getComponentObservabilityIdForReleaseId(String releaseId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().releaseIds(releaseId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getObservabilityIds.mustache", dto);
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

    public static List<ChoreoComponent> getProjectComponents(TestActionRunner runner, HttpClient client,
             String projectId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).projectId(projectId).build();
        String queryString = ObjectMapperUtil.
                mapObjectToString("templates/graphql/requests/getProjectComponents.mustache", dto);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        List<ChoreoComponent> componentsList = new ArrayList<>();

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
                    ChoreoComponent[] projectComponents = ObjectMapperUtil.mapToCollection(ChoreoComponent[].class,
                            message.getPayload(String.class), "components");
                    componentsList.addAll(List.of(projectComponents));
                }));

        return componentsList;
    }

    public static ChoreoComponent getComponentDetails(TestActionRunner runner, HttpClient client, String projectId,
              String componentHandler, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        String queryString = ObjectMapperUtil.
                mapObjectToString("templates/observability/graphql/queryForComponentInformation.mustache", dto);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

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
                    ChoreoComponent component = ObjectMapperUtil.mapStringToObject(ChoreoComponent.class,
                            message.getPayload(String.class), "component");
                    componentArray[0] = component;
                }));

        return componentArray[0];
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
        Commit latestCommit = Commit.getLatestCommit(List.of(commits));
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

    public static ComponentDeploymentStatusDTO componentDeployment(ChoreoComponent component, String envName, String accessToken) throws Exception {
        String envId = component.getLatestAppEnvId(envName);
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).orgUuid(ORG_UUID).componentId(component.getId()).versionId(component.getLatestApiVersion().getId()).environmentId(envId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/componentDeployment.mustache", dto);
        Response response;
        ComponentDeploymentStatusDTO deployments = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
            deployments = ObjectMapperUtil.mapStringToObject(ComponentDeploymentStatusDTO.class, response.getRes(), "componentDeployment");
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
                "templates/createIntegrationComponent/IntegrationComponentCreation.mustache", graphqlDTO);
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
                .accept(MediaType.APPLICATION_JSON_VALUE));
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
    public static ChoreoComponent retrieveComponent(TestActionRunner runner, HttpClient client,
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
                .accept(MediaType.APPLICATION_JSON_VALUE));
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

    public static List<Environment> getDeploymentEndvironments(TestActionRunner runner, HttpClient client,
                                                               String accessToken, GraphqlDTO dto) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getComponentDeploymentEnvironments.mustache", dto);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        List<Environment> envs = new ArrayList<>();

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    envs.addAll(List.of(ObjectMapperUtil.mapToCollection(Environment[].class,
                            message.getPayload(String.class), "environments")));
                }));

        return envs;
    }

    /**
     * Deploy component with validations
     *
     * @param runner      Test action runner
     * @param accessToken Access token
     * @param graphqlDTO  DTO
     * @throws IOException If error occurred in object mapping
     */
    public static void deployComponent(TestActionRunner runner, HttpClient client, String accessToken,
                                       GraphqlDTO graphqlDTO) throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/deployComponent.mustache", graphqlDTO);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(10000)
                .actions(
                        http()
                                .client(client)
                                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE)));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/graphql/responses/deployComponentSuccess.json"))
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
            GraphqlDTO graphqlDTO) throws Exception {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/deploymentStatusByVersion.mustache", graphqlDTO);
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
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .validate(jsonPath()
                                        .expression("$.data.deploymentStatusByVersion[0].conclusion", "success")
                                                )
                                        )
                                );
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
            String accessToken, GraphqlDTO graphqlDTO) throws Exception {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/deploymentStatusByVersion.mustache", graphqlDTO);
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
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(new ClassPathResource("templates/graphql/responses/deploymentStatusByVersionFailure.json"))));
    }

    public static String getRunId(TestActionRunner runner, HttpClient client, String accessToken, GraphqlDTO graphqlDTO) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/createIntegrationComponent/deploymentStatusByVersion.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        final AtomicReference<String> runIdRef = new AtomicReference<>();
        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    String runId = new JsonParser().parse((String) message.getPayload())
                            .getAsJsonObject()
                            .getAsJsonObject("data")
                            .getAsJsonArray("deploymentStatusByVersion")
                            .get(0)
                            .getAsJsonObject()
                            .get("id")
                            .getAsString();
                    runIdRef.set(runId);
                }));
        return runIdRef.get();
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
    public static ComponentDeploymentStatusDTO getComponentDeploymentStatus(TestActionRunner runner, HttpClient client, String accessToken,
                                                                            GraphqlDTO graphqlDTO, Map<String, String> responseParams)
            throws IOException {

        String queryString = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/componentDeployment.mustache",
                graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);
        String expectedResponse = ComponentUtils.generateStringFromTemplate(
                "templates/createIntegrationComponent/deployment_details_success.mustache",
                responseParams);

        AtomicReference<ComponentDeploymentStatusDTO> deploymentStatus = new AtomicReference<>();

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
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .body(expectedResponse)
                                .validate((message, context) -> {
                                    String payload = message.getPayload(String.class);

                                    JsonObject responseJson = new JsonParser().parse(message.getPayload(String.class))
                                            .getAsJsonObject();

                                    JsonObject data = responseJson.getAsJsonObject("data");

                                    if (data != null && !data.isJsonNull()) {
                                        deploymentStatus.set(ObjectMapperUtil.
                                                mapStringToObject(ComponentDeploymentStatusDTO.class,
                                                        payload, "componentDeployment"));

                                        if (deploymentStatus.get().getDeploymentStatusV2().equals("ERROR") ||
                                                deploymentStatus.get().getDeploymentStatus().equals("ERROR")) {
                                            throw new RuntimeException("deploymentStatusV2 is " +
                                                    deploymentStatus.get().getDeploymentStatusV2() +
                                                    " and deploymentStatus is " +
                                                    deploymentStatus.get().getDeploymentStatus());
                                        }

                                    }
                                })));

        return deploymentStatus.get();
    }

    public static ProxyDeployment getProxyComponentDeployment(TestActionRunner runner, HttpClient client, String accessToken,
                                                                            GraphqlDTO graphqlDTO) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getProxyDeploymentDetails.mustache", graphqlDTO);
        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        AtomicReference<ProxyDeployment> proxyDeployment = new AtomicReference<>();

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
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    ProxyDeployment pd = new ProxyDeployment();
                                    JsonObject responseJson = new JsonParser().parse(message.getPayload(String.class))
                                            .getAsJsonObject();
                                    String invokeUrl = responseJson.getAsJsonObject("data")
                                            .getAsJsonObject("proxyDeployment").get("invokeUrl").getAsString();
                                    String environment = responseJson.getAsJsonObject("data")
                                            .getAsJsonObject("proxyDeployment").getAsJsonObject("environment")
                                            .get("name").getAsString();
                                    pd.setInvokeUrl(invokeUrl);
                                    pd.setEnvironment(environment);
                                    proxyDeployment.set(pd);
                                })));

        return proxyDeployment.get();
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

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(10000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(new ClassPathResource("templates/graphql/responses/promoteSuccess.json"))
                                .validate(json())));
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
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/deploy/stop_deployment_success.json"))
                .validate(json()));
    }

    public static List<Environment> getEnvironments(TestActionRunner runner, HttpClient client, String accessToken,
                                       GraphqlDTO graphqlDTO) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/getEnvironments.mustache", graphqlDTO);

        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        List<Environment> environments = new ArrayList<>();

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    Environment[] envArray = ObjectMapperUtil.mapToCollection(Environment[].class,
                            message.getPayload(String.class), "environments");
                    environments.addAll(List.of(envArray));
                }));

        return environments;
    }

    public static List<ObservabilityIdInformation> getObservabilityIds(TestActionRunner runner, HttpClient client, String accessToken,
                                                    GraphqlDTO graphqlDTO) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/getObservabilityIds.mustache", graphqlDTO);

        String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        List<ObservabilityIdInformation> observabilityIds = new ArrayList<>();

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate((message, context) -> {
                    ObservabilityIdInformation[] observerbilityIdArray = ObjectMapperUtil.mapToCollection(ObservabilityIdInformation[].class,
                            message.getPayload(String.class), "observerbilityIds");
                    observabilityIds.addAll(List.of(observerbilityIdArray));
                }));

        return observabilityIds;
    }
    
    public static void createNewVersion(TestActionRunner runner,
                    HttpClient choreoProjectsTestClient, String accessToken,
                    GraphqlDTO graphqlDTO) throws IOException {

            String queryString = ObjectMapperUtil.mapObjectToString(
                            "templates/graphql/requests/createNewVersion.mustache", graphqlDTO);
            String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);
            AtomicReference<CreateNewVersionResponseDTO> mapStringToObject = new AtomicReference<>();
            runner.$(http()
                .client(choreoProjectsTestClient)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
            runner.$(http()
                .client(choreoProjectsTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/graphql/responses/createNewVersionSuccess.json"))
                .validate(json()));
    }
    
    public static List<Commit> getCommitHistory(TestActionRunner runner, HttpClient client, String componentId,
                                            String accessToken, String branchName) throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).branch(branchName).build();
        String queryString = ObjectMapperUtil.
                mapObjectToString("templates/graphql/requests/commitHistoryBranch.mustache", dto);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        List<Commit> commitList = new ArrayList<>();

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
                    Commit[] commits = ObjectMapperUtil.mapToCollection(Commit[].class, message.getPayload(String.class), "commitHistory");
                    commitList.addAll(List.of(commits));
                }));

        return commitList;
    }

    /**
     * Generate component endpoints
     * @param runner TestActionRunner
     * @param client HttpClient
     * @param accessToken Access token
     * @param requestParams Request parameters
     * @throws IOException If an error occurs while reading the request template file
     */
    public static void generateEndpoints(TestActionRunner runner, HttpClient client, String accessToken,
                                         Map<String, String> requestParams) throws IOException {

        final String queryString = ComponentUtils.generateStringFromTemplate(
                "templates/endpoints/GenerateEndpoints.mustache", requestParams);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        runner.$(repeatOnError()
                .until("i = 5")
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
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate(jsonPath()
                                        .expression("$.data.generateComponentEndpoints.size()",
                                                greaterThan(0)))
                )
        );
    }

    /**
     * Get endpoints of a component
     * @param runner TestActionRunner
     * @param client HttpClient
     * @param accessToken Access token
     * @param requestParams Request parameters
     * @return List of component endpoints
     * @throws IOException If an error occurs while reading the request template file
     */
    public static List<Endpoint> getEndpoints(TestActionRunner runner, HttpClient client, String accessToken,
                                              Map<String, String> requestParams) throws IOException {

        final String queryString = ComponentUtils.generateStringFromTemplate(
                "templates/endpoints/GetEndpoints.mustache", requestParams);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);
        List<Endpoint> endpoints = new ArrayList<>();

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .validate((message, context) -> {
                            Endpoint[] endpointArray = ObjectMapperUtil.mapToCollection(Endpoint[].class,
                                    message.getPayload(String.class), "componentEndpoints");
                            endpoints.addAll(List.of(endpointArray));
                        }
                )
        );
        return endpoints;
    }

    /**
     * Update Component Endpoint
     * @param runner TestActionRunner
     * @param client HttpClient
     * @param accessToken Access Token
     * @param requestParams Request Parameters
     * @return Updated Endpoint
     * @throws IOException If an error occurs while reading the request template file
     */
    public static Endpoint updateEndpoint(TestActionRunner runner, HttpClient client, String accessToken,
                                                Map<String, String> requestParams) throws IOException {

        final String queryString = ComponentUtils.generateStringFromTemplate(
                "templates/endpoints/UpdateEndpoint.mustache", requestParams);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);
        Endpoint[] endpoints = new Endpoint[1];

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .validate((message, context) -> {
                            Endpoint endpoint = ObjectMapperUtil.mapStringToObject(Endpoint.class,
                                    message.getPayload(String.class), "updateComponentEndpoint");
                            endpoints[0] = endpoint;
                        }
                )
        );
        return endpoints[0];
    }

    /**
     * Promote component endpoints
     *
     * @param runner TestActionRunner
     * @param client HttpClient
     * @param accessToken Access token
     * @param requestParams Request parameters
     * @return Promoted Endpoints List
     * @throws IOException If an error occurs while reading the request template file
     */
    public static List<Endpoint> promoteEndpoints(TestActionRunner runner, HttpClient client, String accessToken,
                                              Map<String, String> requestParams) throws IOException {

        final String queryString = ComponentUtils.generateStringFromTemplate(
                "templates/endpoints/PromoteComponentEndpoints.mustache", requestParams);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);
        List<Endpoint> endpoints = new ArrayList<>();

        runner.$(http()
                .client(client)
                .send()
                .post(Constant.GRAPHQL_ENDPOINT_SUFFIX)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .validate((message, context) -> {
                            Endpoint[] endpointArray = ObjectMapperUtil.mapToCollection(Endpoint[].class,
                                    message.getPayload(String.class), "promoteComponentEndpoints");
                            endpoints.addAll(List.of(endpointArray));
                        }
                )
        );
        return endpoints;
    }

    /**
     * Validate endpoint deployment
     * @param runner TestActionRunner
     * @param client HttpClient
     * @param accessToken Access token
     * @param requestParams Request parameters
     * @throws IOException If an error occurs while reading the request template file
     */
    public static void validateEndpointDeployment(TestActionRunner runner, HttpClient client, String accessToken,
                                         Map<String, String> requestParams) throws IOException {

        final String queryString = ComponentUtils.generateStringFromTemplate(
                "templates/endpoints/GetEndpoints.mustache", requestParams);
        final String requestBody = ObjectMapperUtil.mapToGraphQLQuery(queryString);

        runner.$(repeatOnError()
                .until("i = 5")
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
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate(jsonPath()
                                        .expression("$.data.componentEndpoints.size()",
                                                greaterThan(0))
                                        .expression("$.data.componentEndpoints[0].state",
                                                comparesEqualTo("Active")))
                )
        );
    }
}
