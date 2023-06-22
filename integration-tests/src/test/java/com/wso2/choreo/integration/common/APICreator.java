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


import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;
import com.wso2.choreo.integration.models.proxyapi.DeploymentStatus;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.requestheader.HeaderValues;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class APICreator extends ControlPlaneAPI {

    private static final String APIM_ENDPOINT = STS_ENDPOINT + Constant.API_VALIDATE_ENDPOINT;
    private static final String PROXY_URI = CHOREO_EP + "/proxy/deployer/v1/components/";
    private static final String APIS_ENDPOINT = STS_ENDPOINT + Constant.APIS_ENDPOINT;

    public static Response validateAPIName(String apiName, String accessToken) throws IOException {
        String requestURL = APIM_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(ORG_UUID)
                .concat("&query=name:").concat(apiName);
        return HttpClientUtil.httpPOST(requestURL, "", accessToken, "");
    }

    public static ProxyResponse<ProxyAPI> createAPI(String apiName, String apiContext, String accessToken) throws IOException {
        String requestURI = APIS_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID).concat("=") + ORG_UUID;
        String requestBody = getRequestBodyForAPICreation(apiName, apiContext);
        Response res = HttpClientUtil.httpPOST(requestURI, requestBody, accessToken, "");
        if (res.getStatusCode() != HttpStatus.CREATED.value()) {
            return ProxyResponse.<ProxyAPI>builder().entity(new ProxyAPI()).response(res).build();
        }
        return ProxyResponse.<ProxyAPI>builder().entity(ObjectMapperUtil.mapStringToObject(ProxyAPI.class, res.getRes(), "")).response(res).build();
    }

    public static String getRequestBodyForAPICreation(String apiName, String apiContext) throws IOException {
        String scopePrefix = "urn:" + ORG_HANDLE + ":" + apiName.toLowerCase() + ":";
        ApiDTO api = ApiDTO.builder().apiName(apiName).version(Constant.DEFAULT_VERSION).context(apiContext).
                scopePrefix(scopePrefix).productionEndpoint(Constant.DEFAULT_ENDPOINT).sandboxEndpoint(Constant.DEFAULT_ENDPOINT).build();
        return ObjectMapperUtil.mapObjectToString("templates/api-proxy/requestBodyForAPICreation.mustache", api);
    }

    public static Response updateAPI(ProxyAPI proxyAPI, String accessToken) throws IOException {
        return updateAPIWithSwaggerFile(proxyAPI,
                "templates/graphql/requests/proxyapiupdaterequest.mustache",
                accessToken);
    }

    public static Response updateAPIWithSwaggerFile(ProxyAPI proxyAPI, String swaggerFileName, String accessToken)
            throws IOException {
        String requestURI = APIS_ENDPOINT + "/" + proxyAPI.getId() + "/swagger?organizationId=" + ORG_UUID;
        HeaderValues headerValues = new HeaderValues().setValues(org.springframework.http.HttpHeaders.AUTHORIZATION, accessToken);
        ApiDTO apiDTO = ApiDTO.builder().apiName(proxyAPI.getName()).description(proxyAPI.getDescription()).productionEndpoint(Constant.DEFAULT_ENDPOINT).
                sandboxEndpoint(Constant.DEFAULT_ENDPOINT).basePath(proxyAPI.getContext() + "/1.0.0").build();

        String swaggerContent = ObjectMapperUtil.mapObjectToString(swaggerFileName, apiDTO);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addTextBody("apiDefinition", swaggerContent);
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        HttpEntity entity = multipartEntityBuilder.build();

        return HttpClientUtil.httpPUT(requestURI, entity, headerValues);
    }

    public static Response updateAPIWithRestAPIContent(ProxyAPI proxyAPI, String apiPayload, String accessToken)
            throws IOException {
        String requestURI = APIS_ENDPOINT + "/" + proxyAPI.getId() + "?organizationId=" + ORG_UUID;
        return HttpClientUtil.httpPUT(requestURI, apiPayload, accessToken, "");
    }

    public static String generateContext(String firstAPIName) {
        return ORG_UUID.concat("/").concat(ORG_HANDLE).concat("/").concat(firstAPIName.toLowerCase());
    }

    public String createUserManagedNonEmptyComponentCreationQuery(String componentName, String orgId, String orgHandle, String projectId, String srcGitRepoUrl, String repoSubpath, String repoType, String repoBranch) throws IOException {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().apiName(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).displayType(Constant.displayType.restAPI.name()).
                projectId(projectId).srcGitRepoUrl(srcGitRepoUrl).repositorySubPath(repoSubpath).
                repositoryType(repoType).repositoryBranch(repoBranch).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/createUserManagedComponent/graphqlQueryForComponentCreation.mustache", graphqlDTO);
        return ObjectMapperUtil.mapToGraphQLQuery(expectedResponse);
    }

    public String createNewComponentVersionQuery(String orgHandler, String orgUuid, String componentId, String componentType, String apiId, String branch)
            throws IOException {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().orgHandler(orgHandler).orgUuid(orgUuid).
                componentId(componentId).componentType(componentType).apiId(apiId).branch(branch).build();

        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/byor/graphqlQueryForNewVersionCreation.mustache", graphqlDTO);
        return ObjectMapperUtil.mapToGraphQLQuery(expectedResponse);
    }

    public String getComponentDetailsQuery(String projectId, String componentHandler) throws IOException {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/createUserManagedComponent/graphqlQueryForComponentDetails.mustache", graphqlDTO);
        return ObjectMapperUtil.mapToGraphQLQuery(expectedResponse);
    }

    public static ProxyResponse<Status> initiateDeployment(String componentId, String versionId, String envId, String accessToken) throws IOException {
        String url = PROXY_URI + componentId + "/versions/" + versionId + "/initiate-deployment?environmentId=" + envId + "&accessMode=external";
        Response response = HttpClientUtil.httpPOST(url, "", accessToken, "");
        Status status = ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "");
        return ProxyResponse.<Status>builder().response(response).entity(status).build();
    }

    public static ProxyAPIBuild getAPIBuilds(String componentId, String versionId, String accessToken) {
        String url = PROXY_URI + componentId + "/versions/" + versionId + "/builds";
        Response res = HttpClientUtil.httpGET(url, accessToken, "");
        return ObjectMapperUtil.mapStringToObject(ProxyAPIBuild.class, res.getRes(), "");
    }

    public static ProxyResponse<Status> deployProxyAPI(String componentId, String versionId, String buildId, String envId, String accessToken) throws IOException {
        String url = PROXY_URI + componentId + "/versions/" + versionId + "/deploy-service?buildId=" + buildId + "&environmentId=" + envId;
        Response response = HttpClientUtil.httpPOST(url, "", accessToken, "");
        Status status = ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "");
        return ProxyResponse.<Status>builder().response(response).entity(status).build();

    }

    public static Status promoteProxyAPI(String componentId, String versionId, String fromEnv,String targetEnv,  String buildId,  String accessToken) throws IOException {
        String url = PROXY_URI + componentId + "/versions/" + versionId + "/promote?fromEnv=" + fromEnv+"&targetEnv="+targetEnv +  "&buildId=" + buildId ;
        Response response = HttpClientUtil.httpPOST(url, "", accessToken, "");
        return ObjectMapperUtil.mapStringToObject(Status.class, response.getRes(), "");
    }

    public static KeyData getAPIKey(String apiId, String accessToken) throws IOException {
        String url = APIS_ENDPOINT + "/" + apiId + "/generate-key?organizationId=" + ORG_UUID;
        Response res = HttpClientUtil.httpPOST(url, "", accessToken, "");
        return ObjectMapperUtil.mapStringToObject(KeyData.class, res.getRes(), "");
    }

    public static DeploySettings deployRevision(String componentId, String versionId, String envId, String orgId,
                                                String revisionId, String buildId, String apiId, String accessToken)
            throws IOException {
        return deployRevision(componentId, versionId, envId, orgId, revisionId, buildId, apiId, accessToken, null, null);
    }

    public static DeploySettings deployRevision(String componentId, String versionId, String envId, String orgId,
                                                String revisionId, String buildId, String apiId, String accessToken,
                                                String restAPIContent, String swaggerContent)
            throws IOException {
        String url = PROXY_URI + componentId + "/versions/" + versionId + "/deploy-settings?environmentId=" + envId +
                "&revisionId=" + revisionId + "&buildId=" + buildId + "&description=" + "" + "&apiId=" + apiId +
                "&accessMode=external" + "&isDevEnv=" + true;
        Map<String, String> payload = new HashMap<>();
        payload.put("openApi",getSwagger(apiId, orgId, accessToken));
        if (restAPIContent != null) {
            payload.put("api", restAPIContent);
        } else {
            payload.put("api", getApi(apiId, orgId, accessToken));
        }

        if (swaggerContent != null) {
            payload.put("openApi", swaggerContent);
        } else {
            payload.put("openApi", getSwagger(apiId, orgId, accessToken));
        }
        Response response = HttpClientUtil.httpPOSTFormData(url, payload, accessToken, "");
        return ObjectMapperUtil.mapStringToObject(DeploySettings.class, response.getRes(), "");
    }

    public static String getSwagger(String apiId, String organizationId, String accessToken) {
        String url = APIS_ENDPOINT + "/" + apiId + "/swagger" + "?organizationId=" + organizationId;
        Response res = HttpClientUtil.httpGET(url,  accessToken, "");
        return res.getRes();
    }

    public static String getApi(String apiId, String organizationId, String accessToken) {
        String url = APIS_ENDPOINT + "/" + apiId + "?organizationId=" + organizationId;
        Response res = HttpClientUtil.httpGET(url, accessToken, "");
        return res.getRes();
    }

    public static ProxyResponse<ProxyAPI> getProxyAPI(String apiId, String organizationId, String accessToken) {
        String url = APIS_ENDPOINT + "/" + apiId + "?organizationId=" + organizationId;
        Response res = HttpClientUtil.httpGET(url, accessToken, "");
        if (res.getStatusCode() != HttpStatus.OK.value()) {
            return ProxyResponse.<ProxyAPI>builder().entity(new ProxyAPI()).response(res).build();
        }
        return ProxyResponse.<ProxyAPI>builder().entity(ObjectMapperUtil.mapStringToObject(ProxyAPI.class, res.getRes(), "")).response(res).build();
    }
}
