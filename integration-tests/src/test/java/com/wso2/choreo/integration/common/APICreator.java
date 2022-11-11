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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.ApiCreationException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.GraphqlDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class APICreator {
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    public String createAPI(String accessToken, String apiName, String apiContext) throws IOException,
            InterruptedException, ApiCreationException {
        String requestURI = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT).
                concat(Constant.APIS_ENDPOINT).concat("?").concat(Constant.ORGANIZATION_ID).concat("=")
                .concat(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
        String requestBody = getRequestBodyForAPICreation(apiName, apiContext);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURI))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.CREATED.value()) {
            throw new ApiCreationException(statusCode, response.body());
        }
        JsonObject responseBody = new JsonParser().parse(response.body()).getAsJsonObject();
        String apiId = responseBody.get(Constant.ID).toString();
        return apiId;
    }

    public String getRequestBodyForAPICreation(String apiName, String apiContext) throws IOException {
        ApiDTO api = ApiDTO.builder().
                apiName(apiName).
                version(Constant.DEFAULT_VERSION).
                context(apiContext).
                productionEndpoint(Constant.DEFAULT_ENDPOINT).
                sandboxEndpoint(Constant.DEFAULT_ENDPOINT).build();
        return ObjectMapperUtil.mapObjectToString("templates/api-proxy/requestBodyForAPICreation.mustache", api);
    }

    public String createGraphqlQueryForComponentCreation(String apiName, String projectId, String apiId) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/api-proxy/graphqlQueryForComponentCreation.mustache");
        Writer writer = new StringWriter();

        GraphqlDTO gql = new GraphqlDTO();
        gql.setApiName(apiName.toLowerCase());
        gql.setOrgId(Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID)));
        gql.setOrgHandler(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE));
        gql.setDisplayName(apiName);
        gql.setDisplayType(String.valueOf(Constant.displayType.proxy));
        gql.setProjectId(projectId);
        gql.setApiId(apiId.replaceAll("\"", ""));
        mustache.execute(writer, gql).flush();
        String graphQlQuery = writer.toString();

        return graphQlQuery;
    }

    public String createUserManagedNonEmptyComponentCreationQuery(String componentName, String orgId, String orgHandle, String projectId, String srcGitRepoUrl, String repoSubpath, String repoType, String repoBranch) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/createUserManagedComponent/graphqlQueryForComponentCreation.mustache");
        Writer writer = new StringWriter();


        GraphqlDTO graphqlDTO = GraphqlDTO.builder().
                apiName(componentName.toLowerCase()).
                orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).
                displayName(componentName).
                displayType(Constant.displayType.restAPI.name()).
                projectId(projectId).
                srcGitRepoUrl(srcGitRepoUrl).
                repositorySubPath(repoSubpath).
                repositoryType(repoType).
                repositoryBranch(repoBranch).
                build();
        mustache.execute(writer, graphqlDTO).flush();
        return writer.toString();


    }

    public String getComponentDetailsQuery(String projectId, String componentHandler) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("templates/createUserManagedComponent/graphqlQueryForComponentDetails.mustache");
        Writer writer = new StringWriter();

        Map<String, String> queryParams = new HashMap<String, String>();
        queryParams.put("projectId", projectId);
        queryParams.put("componentHandler", componentHandler);
        mustache.execute(writer, queryParams).flush();
        String graphQlQuery = writer.toString();

        return graphQlQuery;
    }
}
