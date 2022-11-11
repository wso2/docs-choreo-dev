package com.wso2.choreo.integration.apis.graphql;

import com.wso2.choreo.integration.apis.AbstractConfigs;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestAppEnvIdFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.componentstatusbyversion.ComponentStatusByVersion;
import com.wso2.choreo.integration.models.deploymentstatus.ComponentDeploymentStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ComponentDeploymentGQL extends AbstractConfigs {


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
                ComponentStatusByVersion csbv =  status[0];
                if (csbv.getConclusion()!= null && csbv.getConclusion().equals("failure")) {
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

    public static void componentDeployment(ChoreoComponent component, String envName, String accessToken) throws Exception {

        String envId = component.getLatestAppEnvId(envName);

        GraphqlDTO dto = GraphqlDTO.builder().
                orgHandler(ORG_HANDLE).
                orgUuid(ORG_UUID).
                componentId(component.getId()).
                versionId(component.getLatestApiVersion().getId()).
                environmentId(envId).build();

        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/graphql/requests/componentDeployment.mustache", dto);

        Response response = null;
        ComponentDeploymentStatus deployments = null;
        for (int i = 0; i < 10; i++) {
            response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
            deployments = ObjectMapperUtil.mapStringToObject(ComponentDeploymentStatus.class, response.getRes(), "componentDeployment");

            if (deployments.getDeploymentStatus().equals("ERROR")) {
                throw new UnexpectedResponseException(response.getStatusCode(), "deploymentStatusV2 is " +
                        deployments.getDeploymentStatusV2() + " and deploymentStatus is " + deployments.getDeploymentStatus());
            }
            if (deployments.getDeploymentStatusV2().equals("ACTIVE") && deployments.getDeploymentStatus().equals("ACTIVE")) {
                component.setApiId(deployments.getApiId());
                return;
            }
            SleepUtil.sleep(60);
        }
        throw new UnexpectedResponseException(response.getStatusCode(), "deploymentStatusV2 is " +
                deployments.getDeploymentStatusV2() + " and deploymentStatus is " + deployments.getDeploymentStatus());
    }
}
