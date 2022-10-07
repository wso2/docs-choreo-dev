package com.wso2.choreo.integration.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class GQLutil {


    public static String createComponent(String componentName, String orgId, String orgHandle, String displayType, String projectId, String srcGitHubURL, String triggerChannels, String triggerId) throws JsonProcessingException {


        String graphQlQuery = "mutation{ createComponent(" + "component: {" + "name: \"" + componentName + "\"," + "orgId: " + orgId + "," + "orgHandler: \"" + orgHandle + "\"," + "displayName: \"" + componentName + "\"," + "displayType: \"" + displayType + "\"," + "projectId: \"" + projectId + "\"," + "labels: \"\"," + "version: \"1.0.0\"," + "description: \"\"," + "apiId: \"\"," + "ballerinaVersion: \"swan-lake-alpha5\"," + "triggerChannels: \"\"," + "triggerID: null," + "httpBase: true," + "sampleTemplate: \"\"," + "accessibility: \"external\"," + "srcGitRepoUrl: \"" + srcGitHubURL + "\"" + "repositorySubPath: \"\"," + "repositoryType: \"\"," + "repositoryBranch: \"\"," + "}){" + "id, orgId, projectId, handler" + "}}";


        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        return ObjectMapperUtil.mapToString(gqlRequestPayload);
    }

    public static String getComponentPullRequests(String componentId) throws JsonProcessingException {
        String graphQlQuery = "query{ componentPullRequests(" + "componentId: \"" + componentId + "\"," + "){" + "url, number" + "}}";
        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };

        return ObjectMapperUtil.mapToString(gqlRequestPayload);
    }
    public static String getComponentDetailsQuery(String projectId, String componentHandler) throws JsonProcessingException {
        String query =  "query{ component(" +
                "        projectId: \"" + projectId + "\"," +
                "        componentHandler: \"" + componentHandler + "\"," +
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
                "          branchApp," +
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

        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", query);
            }
        };

        return ObjectMapperUtil.mapToString(gqlRequestPayload);
    }

    public  static String deploy(String componentId,String latestVersionId,String devEnvIdToDeploy,String branch,String latestCommitSha) throws JsonProcessingException {
        String graphQlQuery = "mutation {deployComponent(" + "        deployment: {" + "     " +
                "     componentId: \"" + componentId + "\"," + "        " +
                "  versionId: \"" + latestVersionId + "\"," + "    " +
                "      envId: \"" + devEnvIdToDeploy + "\"," + "     " +
                "     branch: \"" + branch + "\"," + "    " +
                "      sha: \"" + latestCommitSha + "\"," + "      " +
                "  }" + "      ) { message, success }}";
        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
        return  ObjectMapperUtil.mapToString(gqlRequestPayload);
    }

    public static String getDeploymentStatusByVersion(String componentId, String versionId) throws JsonProcessingException {
        String graphQlQuery = "query {" + "deploymentStatusByVersion(" + "componentId: \"" + componentId + "\"," + "        versionId: \"" + versionId + "\"" + "  ) {" + "    id" + "    sha" + "    completed_at" + "    started_at" + "    name" + "    status" + "    conclusion" + "  }" + "}";
        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
      return  ObjectMapperUtil.mapToString(gqlRequestPayload);
    }

    public static String getInvokeUrlInformation(String orgHandle,String orgUUID,String componentId,String latestVersionId) throws JsonProcessingException {
        String graphQlQuery = "query {" +
                "      invokeInformation(" +
                "        orgHandler: \"" + orgHandle + "\"," +
                "        orgUuid: \"" + orgUUID + "\"," +
                "        componentId: \"" + componentId + "\"," +
                "        versionId: \"" + latestVersionId + "\"," +
                "        componentType: \"restAPI\"" +
                "      ) { apiId, invokeUrl } }";
        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQlQuery);
            }
        };
      return  ObjectMapperUtil.mapToString(gqlRequestPayload);
    }

    public static String getComponentDeployment(String orgHandle,String orgUuid,String componentId,String versionId, String envId) throws JsonProcessingException {
        String query = "query {\n" +
                "      componentDeployment(\n" +
                "        orgHandler: \""+orgHandle+"\"\n" +
                "        orgUuid:\""+orgUuid+"\"\n" +
                "        componentId: \""+componentId+"\"\n" +
                "        versionId: \""+versionId+"\"\n" +
                "        environmentId: \""+envId+"\"\n" +
                "      ) {\n" +
                "        environmentId\n" +
                "        configCount\n" +
                "        apiId\n" +
                "        releaseId\n" +
                "        build{\n" +
                "          buildId\n" +
                "          deployedAt\n" +
                "          commit {\n" +
                "            author {\n" +
                "              name\n" +
                "              date\n" +
                "              email\n" +
                "              avatarUrl\n" +
                "            }\n" +
                "            sha\n" +
                "            message\n" +
                "            isLatest\n" +
                "          }\n" +
                "        }\n" +
                "        invokeUrl\n" +
                "        versionId\n" +
                "        deploymentStatus\n" +
                "        deploymentStatusV2\n" +
                "        version\n" +
                "        cron\n" +
                "      }\n" +
                "    }";

        HashMap<String, Object> gqlRequestPayload = new HashMap<>() {
            {
                put("query", query);
            }
        };
        return ObjectMapperUtil.mapToString(gqlRequestPayload);
    }

}
