/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.marketplace;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.marketplace.*;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class ConnectionService extends ControlPlaneAPI {

    private static final String CONTEXT = "connections/v1";
    private static final Logger log = LogManager.getLogger();

    public static String createChoreoConnection(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                ConnectionCreateRequest connectionReq, Boolean isPublisherSecured, List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs ) throws IOException {
        String createChoreoConnectionURI = CONTEXT.concat("/configurations/service-configs/choreo-connections");
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        AtomicReference<String> connectionId = new AtomicReference<>();
        runner.variable("isConnectionCreationSuccess",false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isConnectionCreationSuccess} = true )")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(createChoreoConnectionURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestPayload),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                .validate((message, context) -> {
                                            String payload = message.getPayload(String.class);
                                            JsonObject connectionJsonObject = new JsonParser().parse(payload).getAsJsonObject();
                                            JsonObject connectionStatus = connectionJsonObject.getAsJsonObject("status");
                                            for (int i = 0; i < publisherDeployedEnvs.size(); i++) {
                                                com.wso2.choreo.integration.models.environments.Environment environment = publisherDeployedEnvs.get(i);
                                                String envId = environment.getTemplateId();
                                                if (connectionStatus.has(envId)) {
                                                    JsonArray envStatus = connectionStatus.getAsJsonArray(envId);
                                                    if(isPublisherSecured){
                                                        if (isStageSuccess(envStatus, "Service Url resolved") && isStageSuccess(envStatus, "OAuth keys generated")) {
                                                            context.setVariable("isConnectionCreationSuccess", true);
                                                            connectionId.set(connectionJsonObject.get("groupUuid").getAsString());
                                                        }
                                                    }else{
                                                        if (isStageSuccess(envStatus, "Service Url resolved")) {
                                                            context.setVariable("isConnectionCreationSuccess", true);
                                                            connectionId.set(connectionJsonObject.get("groupUuid").getAsString());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                )));
        return connectionId.get();
    }
    private static boolean  isStageSuccess(JsonArray envStatus, String stage) {
        for (int i = 0; i < envStatus.size(); i++) {
            JsonObject stageObj = envStatus.get(i).getAsJsonObject();
            String stageName  = stageObj.get("stage").getAsString();
            boolean success = stageObj.get("success").getAsBoolean();
            if (stage.equals(stageName) && success) {
                return true;
            }
        }
        return false;
    }

    public static void refreshChoreoConnection(TestActionRunner runner, HttpClient client, String accessToken,
                                                 String connectionId, ConnectionCreateRequest connectionReq) throws Exception {
        String refreshChoreoConnectionURI = CONTEXT.
                concat("/configurations/service-configs/choreo-connections/refresh/")
                .concat(connectionId)
                .concat("?generateCreds=true");
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(refreshChoreoConnectionURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestPayload),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                ));
    }

    public static void deleteChoreoConnection(String accessToken,
                                                String  connectionId) throws IOException {
        log.info("Deleting connection with connection Id: " + connectionId );
        String deleteChoreoConnectionURI =CHOREO_APP_SERVICE_URL.concat("/")+ CONTEXT.concat("/configurations/service-configs/choreo-connections/").concat(connectionId);
        Response response = HttpClientUtil.httpDELETE(deleteChoreoConnectionURI, accessToken, "");
        if(response.getStatusCode() != HttpStatus.OK.value()){
                log.warn("Error while deleting the connection "+connectionId + "error is: "+response.getRes());
        }
    }


    public static ConnectionInfo[] getChoreoConnections(String accessToken, String projectId){
        String getChoreoConnectionsURI=CHOREO_APP_SERVICE_URL.concat("/")+CONTEXT.concat("/configurations/service-configs/connections").concat("?projectId=").concat(projectId);
        Response response = HttpClientUtil.httpGET(getChoreoConnectionsURI, accessToken, "");
        ConnectionInfo[] connectionListing = ObjectMapperUtil.mapStringToObject(ConnectionInfo[].class, response.getRes(), "");
        return connectionListing;

    }

    public static void createProjectLevelConnection(Map<Endpoints, HttpClient> citrusClients, TestNGCitrusSpringSupport runner,
                                                    String accessToken, String serviceName, String networkVisibilityFilter, String projectId , String connectionName,
                                                    String connectionDescription, String requestingServiceVisibility,
                                                    Boolean isPublisherSecured, List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs) throws IOException {
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        HttpClient marketplaceServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String networkVisibilityprojectId =networkVisibilityFilter.equals("project") ? projectId : "";
        List<ServiceInfo> services = MarketplaceService.searchForServices(runner,
                marketplaceServiceClient, accessToken, serviceName, networkVisibilityFilter,networkVisibilityprojectId);
        ServiceInfo serviceFound = services.get(0);  //we will only get one as we search by exact name
        String serviceId = serviceFound.getServiceId();
        String schemaReference = serviceFound.getConnectionSchemas()[0].getId();  //this will only have one schema
        //create connection under project two with project level visibility
        HttpClient connectionServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ArrayList<Environment> environmentsToQuery =
                new ArrayList<>();

        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .orgUuid(orgUuid)
                .projectId(projectId).build();

        List<com.wso2.choreo.integration.models.environments.Environment> environments = GraphQL.getEnvironments(runner, cpProjectsClient, accessToken, graphqlDTO);

        for (com.wso2.choreo.integration.models.environments.Environment env : environments) {
            environmentsToQuery.add(
                    com.wso2.choreo.integration.models.marketplace.Environment.builder()
                            .id(env.getTemplateId())
                            .isCritical(env.isCritical()).build()
            );
        }
        ArrayList<Visibility> visibilities = new ArrayList<>();
        Visibility projectVisibility = Visibility.builder().
                organizationUuid(orgUuid).projectUuid(projectId).build();
        visibilities.add(projectVisibility);

        ConnectionCreateRequest connectionReq = ConnectionCreateRequest.builder().name(connectionName)
                .description(connectionDescription)
                .serviceId(serviceId)
                .schemaReference(schemaReference)
                .environments(environmentsToQuery.toArray(new com.wso2.choreo.integration.models.marketplace.Environment[0]))
                .visibilities(visibilities.toArray(new Visibility[0]))
                .requestingServiceVisibility(requestingServiceVisibility)
                .orgIdInteger(orgId).build();
        String connectionId = ConnectionService.createChoreoConnection(runner, connectionServiceClient,
                accessToken, connectionReq,isPublisherSecured,publisherDeployedEnvs);
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Assert.assertTrue(UUID_REGEX.matcher(connectionId).matches());
    }


    public static  void disableEndpointSecurity(TestActionRunner runner,Map<Endpoints, HttpClient> citrusClients,String apiId, String accessToken){
        HttpClient httpClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        JsonObject apiInfo = ApiManager.getApi(runner,httpClient,accessToken,apiId);
        if (apiInfo.has("securityScheme")) {
            apiInfo.remove("securityScheme");
        }
        apiInfo.add("securityScheme",(new JsonArray()));
        ApiManager.updateApi(runner,httpClient,accessToken,apiId,apiInfo);
    }
}
