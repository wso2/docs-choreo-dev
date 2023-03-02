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

package com.wso2.choreo.integration.common;


import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.graphql.CreateByocComponentResponseDTO;
import com.wso2.choreo.integration.models.graphql.CreateComponentResponseDTO;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class ComponentUtils {

    public static ChoreoComponent getReusableComponent(String accessToken, String testName) throws Exception {
        ChoreoOrganization org = TestContext.getTestOrg();
        String projectName = "integration-test-project";

        Optional<ChoreoProject> existingProject = org.getProjectByName(accessToken, projectName);
        ChoreoProject project;
        if (existingProject.isEmpty()) {
            project = org.createProject(accessToken, projectName, projectName);
        } else {
            project = existingProject.get();
        }

        String componentName = testName + "component";
        Optional<ChoreoComponent> component = project.getComponentByName(accessToken, componentName);
        ChoreoComponent restAPI;

        if (component.isEmpty()) {
            restAPI = project.createRestAPI(accessToken, componentName, org);
            restAPI.setProjectId(project.getId());
        } else {
            restAPI = component.get();
        }

        restAPI.setOrganization(org);

        return restAPI;
    }

    public static ChoreoComponent createComponent(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                                  String accessToken, GraphqlDTO dto,
                                                  ComponentFlavour componentFlavour) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_CP_PROJECTS_ENDPOINT);
        HttpClient choreoClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);


        GraphqlDTO graphqlDTO;

        if (componentFlavour.equals(ComponentFlavour.BYOC)) {
            Optional<CreateByocComponentResponseDTO> responseDTO = GraphQL.createBYOCComponent(runner, cpProjectsClient, dto, accessToken);

            graphqlDTO = GraphqlDTO.builder().
                    projectId(responseDTO.get().getProjectId()).
                    componentHandler(responseDTO.get().getHandle()).build();
        } else {
            Optional<CreateComponentResponseDTO> responseDTO = GraphQL.createUserManagedComponent(runner, cpProjectsClient, dto, accessToken);

            Orgs.waitForComponentCreationSuccess(runner, choreoClient, accessToken, responseDTO.get().getProjectId(),
                    responseDTO.get().getId());

            graphqlDTO = GraphqlDTO.builder().
                    projectId(responseDTO.get().getProjectId()).
                    componentHandler(responseDTO.get().getHandler()).build();
        }

        return GraphQL.retrieveComponent(runner, cpProjectsClient, accessToken,
                    graphqlDTO);
    }

    public static ComponentDeploymentStatusDTO deployComponent(TestActionRunner runner, Map<Endpoints,
            HttpClient> citrusClients, String accessToken, ChoreoComponent component, ComponentFlavour componentFlavour,
                                                               BalConfig... balconfigs) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_CP_PROJECTS_ENDPOINT);
        HttpClient choreoClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, cpProjectsClient, component.getId(), accessToken);

        if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
            Orgs.addConfiguration(runner, choreoClient, component, commitHistory, Constant.DEV_ENVIRONMENT, balconfigs);
        }

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String shaDate = latestCommit.getAuthor().getDate();
        String sha = latestCommit.getSha();

        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        String devEnvIdToDeploy = component.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String branch = component.getRepository().getBranch();

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).latestVersionId(latestVersionId)
                .devEnvIdToDeploy(devEnvIdToDeploy).branch(branch).sha(sha).shaDate(shaDate).build();

        // Deploy component
        GraphQL.deployComponent(runner, cpProjectsClient, accessToken, graphqlDTO);

        GraphQL.getDeploymentStatusByVersion(runner, cpProjectsClient, accessToken, graphqlDTO);

        ChoreoOrganization org = component.getOrganization();
        graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle()).
                orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(devEnvIdToDeploy).build();

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", devEnvIdToDeploy);
        responseParams.put("sha", sha);
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, cpProjectsClient, accessToken, graphqlDTO, responseParams);
    }

    public static ComponentDeploymentStatusDTO promoteComponent(TestActionRunner runner, Map<Endpoints,
            HttpClient> citrusClients, String accessToken, ChoreoComponent component, ComponentFlavour componentFlavour,
                                                                BalConfig... balconfigs) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_CP_PROJECTS_ENDPOINT);
        HttpClient choreoClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().
                projectId(component.getProjectId()).
                componentHandler(component.getHandler()).build();

        component = GraphQL.retrieveComponent(runner, cpProjectsClient, accessToken,
                graphqlDTO);

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, cpProjectsClient, component.getId(), accessToken);

        if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
            Orgs.addConfiguration(runner, choreoClient, component, commitHistory, Constant.PROD_ENVIRONMENT, balconfigs);
        }

        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();
        String sourceReleaseId = component.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);
        String latestAppEnvId = component.getLatestAppEnvId(Constant.PROD_ENVIRONMENT);

        graphqlDTO = GraphqlDTO.builder().componentId(componentId).apiVersionId(latestVersionId).
                sourceReleaseId(sourceReleaseId).targetEnvironmentId(latestAppEnvId).build();
        GraphQL.promoteComponent(runner, cpProjectsClient, accessToken, graphqlDTO);

        ChoreoOrganization org = component.getOrganization();
        graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle()).
                orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(latestAppEnvId).build();

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String sha = latestCommit.getSha();

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", latestAppEnvId);
        responseParams.put("sha", sha);
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, cpProjectsClient, accessToken, graphqlDTO, responseParams);
    }

    public static void invokeApiEndpoint(String accessToken, ChoreoComponent component, Constant.Environment env) throws Exception {
        InvokeInformation invokeInformation = component.getInvokeInformation(accessToken,
                Constant.displayType.restAPI.name(), env.name());
        String requestURI = invokeInformation.getInvokeUrl();
        if (requestURI == null) {
            throw new InvokeInformationNotFoundException();
        }
        requestURI = requestURI.concat("/")
                .concat("greeting")
                .concat("?name=testUser");
        // Escaping the quotations
        String apiKey = component.getAPIKeyForInvoke(accessToken, invokeInformation.getApiId()).replace("\"", "");
        HttpGet request = new HttpGet(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != HttpStatus.OK.value()) {
                throw new InvokeAPICheckException(statusCode, responseBody);
            }
        }

    }


    public static String generateStringFromTemplate(String templateRelativePath, Map<String, String> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

    public static String generateStringPayloadFromTemplate(String templateRelativePath, Map<String, Object> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

    /**
     * Invoke API GET with validation
     *
     * @param runner           Test action runner
     * @param apiKey           API Key
     * @param invokeUrl        Invoke URL
     * @param resource    API Resource
     * @param expectedResponse Expected response
     */
    public static void invokeApiGET(TestActionRunner runner, String apiKey, String invokeUrl, String resource,
                                    String expectedResponse) {
        // Test API Invocation
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions((http()
                            .client(invokeUrl)
                            .send()
                            .get(resource)
                            .message()
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .header("API-Key", apiKey)),
                        http()
                            .client(invokeUrl)
                            .receive()
                            .response(HttpStatus.OK)
                            .message()
                            .type(MessageType.JSON)
                            .body(expectedResponse)));
    }

    /**
     * Invoke API POST with validation
     *
     * @param runner           Test action runner
     * @param apiKey           API Key
     * @param invokeUrl        Invoke URL
     * @param resource    API Resource
     * @param requestBody Request payload
     * @param expectedResponse Expected response
     *
     */
    public static void invokeApiPOST(TestActionRunner runner, String apiKey, String invokeUrl, String resource,
                                    String requestBody, String expectedResponse) {
        // Test API Invocation
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions((http()
                                .client(invokeUrl)
                                .send()
                                .post(resource)
                                .message()
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestBody)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .header("API-Key", apiKey)),
                        http()
                                .client(invokeUrl)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(expectedResponse)));
    }
}
