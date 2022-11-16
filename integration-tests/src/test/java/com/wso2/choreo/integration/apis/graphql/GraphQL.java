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
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.componentstatusbyversion.ComponentStatusByVersion;
import com.wso2.choreo.integration.models.createcomponentresponse.ComponentCreationResponse;
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

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;

/**
 * Implements GraphQL API calls and their response validations.
 */
@Slf4j
public class GraphQL extends ControlPlaneAPI {


    public static void promoteComponent(ChoreoComponent component, String accessToken)
            throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(component.getId()).apiVersionId(component.getLatestApiVersion().getId()).
                sourceReleaseId(component.getReleaseIdForEnvironment("dev")).targetEnvironmentId(component.getLatestAppEnvId("prod")).build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/promote.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");


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

    public static ComponentCreationResponse createUserManagedComponent(GraphqlDTO graphqlDTO, String accessToken) throws IOException {
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createUserManagedComponent.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ComponentCreationResponse.class, response.getRes(), "createComponent");
    }

    public static ComponentCreationResponse createChoreoManagedComponent(GraphqlDTO graphqlDTO, String accessToken) throws IOException {
        graphqlDTO.setOrgId(ORG_ID);
        graphqlDTO.setOrgHandler(ORG_HANDLE);
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/createChoreoManagedComponent.mustache", graphqlDTO);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ComponentCreationResponse.class, response.getRes(), "createComponent");
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
            SleepUtil.sleep(15);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "Expected PullRequest length " + expectedPRs + " but found " + 0);
    }


    public static ChoreoComponent getComponentDetails(String projectId, String componentHandler, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentHandler(componentHandler).projectId(projectId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(RestApiChoreoComponent.class, response.getRes(), "component");
    }

    public static Environment[] getEnvironments(ChoreoComponent component, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgUuid(component.getOrganization().getOrgUUID()).projectId(component.getProjectId()).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/getEnvironments.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(Environment[].class, response.getRes(), "environments");
    }

    public static InvokeInformation[] getInvokeInformation(ChoreoComponent component, String componentType, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).orgUuid(component.getOrganization().
                getOrgUUID()).componentId(component.getId()).versionId(component.getId()).componentType(componentType).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/deploy/graphql/queryForInvokeInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        return ObjectMapperUtil.mapToCollection(InvokeInformation[].class, response.getRes(), "invokeInformation");
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

    public static void fetchObservabilityIds(String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentEnvironmentInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");

    }

    public static Commit[] getCommitHistory(String componentId, String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(componentId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/deploy/graphql/commitHistory.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        log.info(response.getRes());
        return ObjectMapperUtil.mapToCollection(Commit[].class, response.getRes(), "commitHistory");
    }


    public static Status deployComponent(ChoreoComponent component, String accessToken) throws IOException, NoLatestCommitHashFoundException, NoLatestApiVersionFoundException, NoLatestAppEnvIdFoundException {
        Commit[] commits = getCommitHistory(component.getId(), accessToken);
        Commit latestCommit = Commit.getLatestCommit(commits);


        GraphqlDTO dto = GraphqlDTO.builder().componentId(component.getId()).latestVersionId(component.getLatestApiVersion().getId()).
                devEnvIdToDeploy(component.getLatestAppEnvId("dev")).sha(latestCommit.getSha()).branch("main").build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/deployComponent.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
        return ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "deployComponent");

    }


    public static ComponentStatusByVersion deploymentStatusByVersion(ChoreoComponent component, String accessToken)
            throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(component.getId()).latestVersionId(component.getLatestApiVersion().getId()).build();
        String generatedQuery = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/deploymentStatusByVersion.mustache", dto);
        Response response = null;
        for (int i = 0; i < 36; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(generatedQuery), accessToken, "");
            ComponentStatusByVersion[] status = ObjectMapperUtil.mapToCollection(ComponentStatusByVersion[].class, response.getRes(), "deploymentStatusByVersion");

            log.info(response.getRes());
            if (status.length > 0) {
                ComponentStatusByVersion csbv = status[0];
                if (csbv.getConclusion() != null && csbv.getConclusion().equals("failure")) {
                    throw new UnexpectedResponseException(response.getStatusCode(), "Component was not deployed successfully");
                }
                if (csbv.getStatus().equals("completed") && csbv.getConclusion().equals("success")) {
                    return csbv;
                }
            }
            SleepUtil.sleep(30);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "Component was not deployed successfully");
    }

    public static ComponentDeploymentStatus componentDeployment(ChoreoComponent component, String envName, String accessToken) throws Exception {
        String envId = component.getLatestAppEnvId(envName);
        GraphqlDTO dto = GraphqlDTO.builder().orgHandler(ORG_HANDLE).orgUuid(ORG_UUID).componentId(component.getId()).versionId(component.getLatestApiVersion().getId()).environmentId(envId).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/componentDeployment.mustache", dto);
        Response response = null;
        ComponentDeploymentStatus deployments = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
            deployments = ObjectMapperUtil.mapStringToObject(ComponentDeploymentStatus.class, response.getRes(), "componentDeployment");
            log.info(response.getRes());
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


    public static void getObservabilityEnvironments(String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentHandler(ORG_UUID).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentEnvironmentInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        System.out.println(response.getRes());
        //    return ObjectMapperUtil.mapStringToObject(RestApiChoreoComponent.class, response.getRes(), "component");

    }

}
