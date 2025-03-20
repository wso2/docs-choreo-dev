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
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.marketplace.*;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.http.message.HttpMessageHeaders.HTTP_STATUS_CODE;

public class ConnectionService extends ControlPlaneAPI {

    private static final String CONTEXT = "connections/v1";
    private static final Logger log = LogManager.getLogger();

    public static String createChoreoConnection(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                ConnectionCreateRequest connectionReq, Boolean isOauth2Secured,
                                                List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs, 
                                                boolean isWebApp ) throws IOException {
        String createChoreoConnectionURI = CONTEXT.concat("/configurations/service-configs/choreo-connections");
        if (isWebApp) {
            createChoreoConnectionURI = createChoreoConnectionURI.concat("?generateCreds=false");
        }else{
            createChoreoConnectionURI = createChoreoConnectionURI.concat("?generateCreds=true");
        }
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        AtomicReference<String> connectionId = new AtomicReference<>();
        runner.variable("isConnectionCreationSuccess",false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isConnectionCreationSuccess} = true )")
                .index("i")
                .autoSleep(10000)
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
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.CREATED.value()) {
                                        throw new ValidationException("Connection creation failed with status code: " + code);
                                    }
                                    String payload = message.getPayload(String.class);
                                    JsonObject connectionJsonObject = new JsonParser().parse(payload).getAsJsonObject();
                                    validateConnectionCreation (context, connectionJsonObject, connectionId,
                                            isOauth2Secured, "isConnectionCreationSuccess",
                                            publisherDeployedEnvs, isWebApp);
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

    public static void refreshChoreoConnection(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                 String connectionId, ConnectionCreateRequest connectionReq,
                                               List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs,
                                               boolean isPublisherSecured, boolean isWebApp) throws Exception {
        String refreshChoreoConnectionURI = CONTEXT.
                concat("/configurations/service-configs/choreo-connections/refresh/")
                .concat(connectionId);   
        if (isWebApp) {
            refreshChoreoConnectionURI = refreshChoreoConnectionURI.concat("?generateCreds=false");
        }else{
            refreshChoreoConnectionURI = refreshChoreoConnectionURI.concat("?generateCreds=true");
        }
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        runner.variable("isConnectionRefreshSuccess",false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isConnectionRefreshSuccess} = true )")
                .index("i")
                .autoSleep(5000)
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
                                .response()
                                .validate((message, context) -> {
                                            int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                            if (code != HttpStatus.CREATED.value()) {
                                                throw new ValidationException("Connection refreshing failed with " +
                                                        "status code: " + code);
                                            }
                                            String payload = message.getPayload(String.class);
                                            JsonObject connectionJsonObject = new JsonParser().parse(payload).getAsJsonObject();
                                            validateConnectionCreation (context, connectionJsonObject, null,
                                            isPublisherSecured, "isConnectionRefreshSuccess",
                                                    publisherDeployedEnvs, isWebApp);
                                        }
                                )));
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

    public static void regenerateAPIKey(String accessToken, String connectionId, List<com.wso2.choreo.integration.models.environments.Environment> environments) throws IOException {
        for (com.wso2.choreo.integration.models.environments.Environment environment : environments) {
            String envId = environment.getTemplateId();
            String regenerateAPIKeyURI = CHOREO_APP_SERVICE_URL.concat("/").concat(CONTEXT)
                .concat("/configurations/service-configs/choreo-connections/")
                .concat(connectionId).concat("/rotate-keys").concat("?environmentId=").concat(envId);
            String payloadString = "{}";
            Response response = HttpClientUtil.httpPOST(regenerateAPIKeyURI, payloadString, accessToken, "");
            if (response.getStatusCode() != HttpStatus.CREATED.value()) {
                log.warn("Error while regenerating the API key for connection " + connectionId + " in environment " + envId + " error is: " + response.getRes());
            }
        }
    }

    public static ConnectionInfo[] getChoreoConnections(String accessToken, String projectId){
        String getChoreoConnectionsURI=CHOREO_APP_SERVICE_URL.concat("/")+CONTEXT.concat("/configurations/service-configs/connections").concat("?projectId=").concat(projectId);
        Response response = HttpClientUtil.httpGET(getChoreoConnectionsURI, accessToken, "");
        ConnectionInfo[] connectionListing = ObjectMapperUtil.mapStringToObject(ConnectionInfo[].class, response.getRes(), "");
        return connectionListing;

    }
    public static ServiceInfo FindService(Map<Endpoints, HttpClient> citrusClients, TestNGCitrusSpringSupport runner, String accessToken, String serviceName,
                                                String networkVisibilityFilter, String projectId) throws IOException {
        HttpClient marketplaceServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<ServiceInfo> services = MarketplaceService.searchForServices(runner,
                marketplaceServiceClient, accessToken, serviceName, networkVisibilityFilter,projectId);
        return services.get(0);  //we will only get one as we search by exact name
    }
    public static ConnectionCreateRequest createComponentLevelConnectionCreationReq(
            List<com.wso2.choreo.integration.models.environments.Environment> clientComponentEnvironments,
            String projectId, String clientComponentId, String requestingServiceVisibility,
            ServiceInfo serviceFound ) throws IOException {

        String serviceId = serviceFound.getServiceId();
        String schemaReference = serviceFound.getConnectionSchemas()[0].getId();  //this will only have one schema

        //create connection under client component
        ArrayList<com.wso2.choreo.integration.models.marketplace.Environment> environmentsToQuery = new ArrayList<>();
        for (com.wso2.choreo.integration.models.environments.Environment env : clientComponentEnvironments) {
            environmentsToQuery.add(
                    com.wso2.choreo.integration.models.marketplace.Environment.builder()
                            .id(env.getTemplateId())
                            .isCritical(env.isCritical()).build()
            );
        }
        ArrayList<Visibility> visibilities = new ArrayList<>();
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        Visibility componentVisibility = Visibility.builder().
                organizationUuid(orgUuid).projectUuid(projectId).componentUuid(clientComponentId).build();
        visibilities.add(componentVisibility);
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);

        ConnectionCreateRequest connectionCreationReq = ConnectionCreateRequest.builder().name(connectionName)
                .description("Component Level connection")
                .serviceId(serviceId)
                .schemaReference(schemaReference)
                .environments(environmentsToQuery.toArray(new com.wso2.choreo.integration.models.marketplace.Environment[0]))
                .visibilities(visibilities.toArray(new Visibility[0]))
                .requestingServiceVisibility(requestingServiceVisibility)
                .orgIdInteger(orgId).build();

        return connectionCreationReq;

    }

    public static void createProjectLevelConnection(Map<Endpoints, HttpClient> citrusClients, TestNGCitrusSpringSupport runner,
                                                    String accessToken, String serviceName, String networkVisibilityFilter,
                                                    String projectId , String connectionName, String connectionDescription,
                                                    String requestingServiceVisibility, Boolean isOauth2Secured,
                                                    List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs) throws IOException {
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
        ArrayList<Environment> environmentsToQuery = new ArrayList<>();

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
                accessToken, connectionReq, isOauth2Secured, publisherDeployedEnvs,false);
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

    public static void enableOAuth2SecurityForAPI(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                                  String apiId, String accessToken) {

        HttpClient httpClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        JsonObject apiInfo = ApiManager.getApi(runner,httpClient,accessToken,apiId);
        if (apiInfo.has("securityScheme")) {
            apiInfo.remove("securityScheme");
        }
        JsonArray securityScheme = new JsonArray();
        securityScheme.add("oauth2");
        apiInfo.add("securityScheme",securityScheme);
        ApiManager.updateApi(runner,httpClient,accessToken,apiId,apiInfo);
    }

    public static String getEndpointForProxy(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, String accessToken,
                                             ChoreoComponent proxySourceComponent,
                                             List<com.wso2.choreo.integration.models.environments.Environment> environments) throws Exception {
        ComponentDeploymentStatusDTO deployedProxySourceComponentStatus = ComponentUtils.deployComponent(runner, citrusClients, accessToken, proxySourceComponent,
                environments, ComponentFlavour.BYOC);
        List<Endpoint> endpoints = ComponentUtils.getEndpoints(runner,citrusClients,accessToken,proxySourceComponent,
                deployedProxySourceComponentStatus);
        ConnectionService.disableEndpointSecurity(runner,citrusClients,endpoints.get(0).getApimId(),accessToken);
        ComponentUtils.deployComponent(runner, citrusClients, accessToken, proxySourceComponent, environments,
                ComponentFlavour.BYOC);
        List<ComponentDeploymentStatusDTO> promotionStatus = ComponentUtils.promoteComponent(runner, citrusClients,
                accessToken, proxySourceComponent,
                environments, ComponentFlavour.BYOC);
        int promotionStatusListSize =  promotionStatus.size();
        deployedProxySourceComponentStatus = promotionStatus.get(promotionStatusListSize-1);

        endpoints = ComponentUtils.getEndpoints(runner,citrusClients,accessToken,proxySourceComponent,
                deployedProxySourceComponentStatus);
        return endpoints.get(0).getPublicUrl();
    }

    public static String createAndUseConnection(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients, String accessToken,
                                     String requestedServiceName, String requestedServiceVisibility, String projectId,
                                     String clientChoreoComponentId, List<com.wso2.choreo.integration.models.environments.Environment> clientComponentEnvironments,
                                     List<com.wso2.choreo.integration.models.environments.Environment> servicePublisherComponentEnvironments,
                                              String repoName, String... branchName) throws IOException {

        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,runner,accessToken,requestedServiceName,requestedServiceVisibility.toLowerCase(),"");
        ConnectionCreateRequest connectionCreationReq= ConnectionService.createComponentLevelConnectionCreationReq(clientComponentEnvironments,projectId,clientChoreoComponentId,requestedServiceVisibility,serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String serviceId = serviceFound.getServiceId();
        String connectionId = ConnectionService.createChoreoConnection(runner, httpClient,
                accessToken, connectionCreationReq,true,servicePublisherComponentEnvironments.subList(0,1),false);

        //update component-config.yaml file
        //Let's consume service using public visibility
        String serviceIdentifier = MarketplaceService.getChoreoServiceIdentifier(runner,
                httpClient, accessToken, serviceId, ServiceVisibility.PUBLIC);
        Map<String, String> params = new HashMap<>();
        params.put("serviceIdentifier", serviceIdentifier);
        params.put("connectionId", connectionId);
        String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                "templates/marketplace/component-config.mustache", params);
        String encodedFileContent = Base64.getEncoder().
                encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
        Map<String, String> options = new HashMap<>();
        if (branchName.length > 0) {
            options.put("branchName",branchName[0]);
        }
        Response mergeCodeResp = GitHub.mergeNewCode( repoName, ".choreo/component-config.yaml", "Update component-config file", encodedFileContent,options);
        if (mergeCodeResp.getStatusCode() != HttpStatus.OK.value()) {
            throw new ValidationException("Error while update component-config.yaml file" + mergeCodeResp.getRes());
        }
        return connectionId;
    }

    public static void UpdateSourceConfigurationFile(String repoName, String branchName, String connectionIdentifier, String serviceIdentifier, SourceConfigurationFileTypes fileType, String choreoFolderPath) throws IOException {
        Map<String, String> options = new HashMap<>();
        options.put("orgName", GH_TEST_USER_ORG);
        options.put("branchName", branchName);
        if (fileType == SourceConfigurationFileTypes.COMPONENT_CONFIG){
            Map<String, String> params = new HashMap<>();
            params.put("serviceIdentifier", serviceIdentifier);
            params.put("connectionId", connectionIdentifier);
            String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                    "templates/connectionManagement/sourceConfigurationFiles/databaseConnections/component-config.mustache", params);
            String encodedFileContent = Base64.getEncoder().
                    encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
            Response mergeCodeResp = GitHub.mergeNewCode( repoName, choreoFolderPath.concat("/component-config.yaml"), "Update component-config.yaml file", encodedFileContent, options);
            if (mergeCodeResp.getStatusCode() != HttpStatus.OK.value()) {
                throw new ValidationException("Error while updating component-config.yaml file for db connections" + mergeCodeResp.getRes());
            }
        } else if (fileType == SourceConfigurationFileTypes.COMPONENT_V1D0){
            Map<String, String> params = new HashMap<>();
            params.put("serviceIdentifier", serviceIdentifier);
            params.put("connectionId", connectionIdentifier);
            String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                    "templates/connectionManagement/sourceConfigurationFiles/databaseConnections/componentv10.mustache", params);
            String encodedFileContent = Base64.getEncoder().
                    encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
            Response mergeCodeResp = GitHub.mergeNewCode(repoName, choreoFolderPath.concat("/component.yaml"), "Update component.yaml file v1.0", encodedFileContent, options);
            if (mergeCodeResp.getStatusCode() != HttpStatus.OK.value()) {
                throw new ValidationException("Error while updating component.yaml v1.0 file for db connections" + mergeCodeResp.getRes());
            }
        } else if (fileType == SourceConfigurationFileTypes.COMPONENT_V1D1){
            Map<String, String> params = new HashMap<>();
            params.put("resourceRef", serviceIdentifier);
            params.put("connectionName", connectionIdentifier);
            String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                    "templates/connectionManagement/sourceConfigurationFiles/databaseConnections/componentv11.mustache", params);
            String encodedFileContent = Base64.getEncoder().
                    encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
            Response mergeCodeResp = GitHub.mergeNewCode(repoName,choreoFolderPath.concat("/component.yaml") , "Update component.yaml file v1.1", encodedFileContent, options);
            if (mergeCodeResp.getStatusCode() != HttpStatus.OK.value()) {
                throw new ValidationException("Error while updating component.yaml v1.1 file for db connections" + mergeCodeResp.getRes());
            }
        }
    }

    private static boolean isPublisherDeployedEnvironment(List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs, String envId) {
        for (com.wso2.choreo.integration.models.environments.Environment environment : publisherDeployedEnvs) {
            if (environment.getTemplateId().equals(envId)) {
                return true;
            }
        }
        return false;
    }

    public static void validateConnectionCreation(com.consol.citrus.context.TestContext context,
                                                  JsonObject connectionJsonObject, AtomicReference<String> connectionId,
                                                  boolean isOauth2Secured, String contextVariableName,
                                                  List<com.wso2.choreo.integration.models.environments.Environment> publisherDeployedEnvs,
                                                  boolean isWebApp){

        JsonObject connectionStatus = connectionJsonObject.getAsJsonObject("status");
        for (String envId : connectionStatus.keySet()) {
            JsonArray envStatus = connectionStatus.getAsJsonArray(envId);
            if (isPublisherDeployedEnvironment(publisherDeployedEnvs, envId)) {
                if (!isStageSuccess(envStatus, "Service Url resolved")) {
                    throw new ValidationException("Connection configurations are not resolved properly for environment: " + envId);
                }
               if (isOauth2Secured && !isWebApp) {
                    if (!isStageSuccess(envStatus, "OAuth keys generated")) {
                        throw new ValidationException("Connection configurations are not resolved properly for environment: " + envId);
                    }
                }
            } else {
                boolean isPartiallyCreated = connectionJsonObject.get("isPartiallyCreated").getAsBoolean();
                if (!isPartiallyCreated) {
                    throw new ValidationException("Connection configurations are not properly partially created for " +
                            "environment: " + envId);
                }
                if (isStageSuccess(envStatus, "Service Url resolved")) {
                    throw new ValidationException("Connection configurations are not properly partially created for " +
                            "environment: " + envId);
                }
                if (isOauth2Secured && !isWebApp) {
                    if (!isStageSuccess(envStatus, "OAuth keys generated")) {
                        throw new ValidationException("Connection configurations are not properly partially created for " +
                                "environment: " + envId);                    }
                }
            }
        }
        context.setVariable(contextVariableName, true);
        if(connectionId != null) {
            connectionId.set(connectionJsonObject.get("groupUuid").getAsString());
        }
    }

    public static CommonResource FindDatabase(Map<Endpoints, HttpClient> citrusClients, TestNGCitrusSpringSupport runner, String accessToken, String databaseServerId,
                                          String datbaseName) throws IOException {
        HttpClient marketplaceServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<CommonResource> databases = MarketplaceService.searchForDatabase(runner,
                marketplaceServiceClient, accessToken, databaseServerId, datbaseName);
        return databases.get(0);
    }

    public static ConnectionInfo CreateChoreoDatabaseConnection(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                      DatabaseConnectionCreateRequest connectionReq) throws IOException {
        String createChoreoConnectionURI = CONTEXT.concat("/configurations/service-configs/choreo-database-connections");
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        runner.variable("isConnectionCreationSuccess", false);
        AtomicReference<ConnectionInfo> connection = new AtomicReference<>();
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isConnectionCreationSuccess} = true )")
                .index("i")
                .autoSleep(10000)
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
                                .response()
                                .validate((message, context) -> {
                                            int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                            if (code != HttpStatus.CREATED.value()) {
                                                throw new ValidationException("Connection creation failed with status code: " + code);
                                            }
                                    connection.set(ObjectMapperUtil.mapStringToObject(ConnectionInfo.class, message.getPayload(String.class), ""));
                                 }
                                )));
     return  connection.get();
    }


    public static ThirdPartyServiceConnectionResponse createThirdPartyServiceConnection(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                                ThirdPartyConnectionCreationRequest connectionReq) throws IOException {
        String createChoreoConnectionURI = CONTEXT.concat("/configurations/service-configs/third-party-connections");
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        runner.variable("isConnectionCreationSuccess", false);
        AtomicReference<ThirdPartyServiceConnectionResponse> connection = new AtomicReference<>();
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isConnectionCreationSuccess} = true )")
                .index("i")
                .autoSleep(10000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(createChoreoConnectionURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .queryParam("wellKnownService", "true")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestPayload),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.CREATED.value()) {
                                        throw new ValidationException("Connection creation failed with status code: " + code);
                                    }
                                    ThirdPartyServiceConnectionResponse connectionResponse = ObjectMapperUtil.mapStringToObject(ThirdPartyServiceConnectionResponse.class, message.getPayload(String.class));
                                    connection.set(connectionResponse);
                                    validateThirdPartyServiceConnection(connectionResponse);
                                })));
        return  connection.get();
    }

    public static void validateThirdPartyServiceConnection(ThirdPartyServiceConnectionResponse thirdPartyServiceConnectionResponse) {
        if (thirdPartyServiceConnectionResponse.getGroupUuid() == null) {
            throw new ValidationException("Group UUID is not found in the response");
        }
        if (thirdPartyServiceConnectionResponse.getName() == null) {
            throw new ValidationException("Name is not found in the response");
        }
        if (thirdPartyServiceConnectionResponse.getServiceName() == null) {
            throw new ValidationException("Service Name is not found in the response");
        }
        if (thirdPartyServiceConnectionResponse.getSchemaName() == null) {
            throw new ValidationException("Schema Name is not found in the response");
        }
        if (thirdPartyServiceConnectionResponse.getEnvMapping() == null) {
            throw new ValidationException("Environment Mapping is not found in the response");
        }
        if (thirdPartyServiceConnectionResponse.getConfigurations() == null) {
            throw new ValidationException("Configurations is not found in the response");
        }
    }

    // validate whether we are connected to the correct database in each environment
    public static void ValidateDatabaseConnection(JsonArray appointmentsList, JsonObject appointment, String keyName) throws Exception {
        boolean appointmentFound = false;

        for (JsonElement element : appointmentsList) {
            JsonObject currentAppointment = element.getAsJsonObject();

            // Check if the name of the appointment matches the keyName
            if (!currentAppointment.get("name").getAsString().equals(keyName)) {
                // If name doesn't match, throw validation exception
                throw new ValidationException("The 'name' field does not match for appointment with ID: " + appointment.get("id"));
            }

            // Check if the current appointment's id matches the provided appointment's id
            if (currentAppointment.get("id").getAsInt() == appointment.get("id").getAsInt()) {
                appointmentFound = true;
            }
        }

        // If no matching appointment was found, throw exception
        if (!appointmentFound) {
            throw new ValidationException("Appointment with ID: " + appointment.get("id") + " not found in the appointments list.");
        }
    }
}
