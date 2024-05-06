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

package com.wso2.choreo.integration.tests.connections;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.marketplace.ConnectionService;
import com.wso2.choreo.integration.apis.marketplace.MarketplaceService;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.marketplace.ConnectionCreateRequest;
import com.wso2.choreo.integration.models.marketplace.ServiceInfo;
import com.wso2.choreo.integration.models.marketplace.ServiceVisibility;
import com.wso2.choreo.integration.models.marketplace.Visibility;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests related to Choreo connection creation and use.
 */
public class ChoreoConnections extends TestNGCitrusSpringSupport {

    private static final String SVC_COMPONENTS_REPO_URL = "https://github.com/choreo-test-apps/connection-test-loyalty-service";
    private static final String PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT="loyalty-service-public-visibility/";
    private static final String ORG_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT="loyalty-service-org-visibility/";
    private static final String PROJECT_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT="loyalty-service-project-visibility/";
    private static final String SVC_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";
    private static final String OAS_FILE_PATH = "openapi.yaml";

    private static String SVC_COMPONENT_SERVICE_NAME = "";
    private static final String NETWORK_VISIBILITY_FILTER = "org,public";
    private static final String ORG_LVL_NETWORK_VISIBILITY_FILTER = "org";
    private static final String PROJECT_LVL_NETWORK_VISIBILITY_FILTER = "project";
    private static final String PUBLIC_SERVICE = "PUBLIC";
    private static final String ORGANIZATION_SERVICE = "ORGANIZATION";
    private static final String PROJECT_SERVICE = "PROJECT";


    private static final String CLIENT_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/connections-test";
    private static final String CLIENT_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";

    private HttpClient appServiceClient;
    private String accessToken;
    private String orgHandle;
    private int orgId;
    private String orgUUID;
    ChoreoProject projectOne;
    private ChoreoComponent publicEndpointServiceComponent;
    private ChoreoComponent orgEndpointServiceComponent;
    private ChoreoComponent projectEndpointServiceComponent;

    private ChoreoComponent clientChoreoComponent;
    private ChoreoComponent proxyComponent;
    private String proxyApiId;

    private String componentLevelConnectionId;
    private List<Environment> servicePublisherComponentEnvironments;
    private List<Environment> proxyPublisherComponentEnvironments;
    private List<Environment> orgEndpointComponentDeployedEnvs;

    private List<Environment> projectEndpointComponentDeployedEnvs;
    private List<Environment> clientComponentEnvironments;
    private ConnectionCreateRequest connectionCreationReq;
    private ComponentDeploymentStatusDTO publicEndpointServiceDeploymentStatusDTO;

    private ComponentDeploymentStatusDTO orgEndpointServiceDeploymentStatusDTO;
    private ComponentDeploymentStatusDTO clientDeploymentStatusDTO, clientPromotionStatusDTO;
    private final String repoName = "connections-test";
    private String API_INVOCATION_REQUEST_URI;
    private String API_INVOCATION_REQUEST_BODY;
    private String REST_API_EXPECTED_RESPONSE;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestChoreoConnectionTestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        API_INVOCATION_REQUEST_URI = "/select-reward";
        API_INVOCATION_REQUEST_BODY = "{\n" +
                "  \"acceptedTnC\": true,\n" +
                "  \"selectedRewardDealId\": \"RWD34589\",\n" +
                "  \"userId\": \"U451298\"\n" +
                "}";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/connectionManagement/loyaltyServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test
    @CitrusTest
    public void createProject_TestChoreoConnections() throws Exception {
        projectOne = ComponentUtils.createProject(this, citrusClients, accessToken,
                Constant.region.US.toString());
    }
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createServicePublisherComponent_TestChoreoConnections() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, projectOne, repo);
        publicEndpointServiceComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
    }
    @Test(dependsOnMethods = {"createServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void deployServicePublisherComponent_TestChoreoConnections() throws Exception {
        servicePublisherComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                publicEndpointServiceComponent);
        publicEndpointServiceDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                publicEndpointServiceComponent, servicePublisherComponentEnvironments, ComponentFlavour.BYOC);
        SVC_COMPONENT_SERVICE_NAME = publicEndpointServiceComponent.getName();
    }
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createServiceConsumerComponent_TestChoreoConnections() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(CLIENT_COMPONENT_REPO_URL).
                oasFilePath(OAS_FILE_PATH).
                dockerfilePath(CLIENT_COMPONENT_DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, projectOne, repo);

        clientChoreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        clientComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
    }

    @Test(dependsOnMethods = {"createServiceConsumerComponent_TestChoreoConnections", "deployServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createComponentLevelConnection_TestChoreoConnections() throws Exception {
        //Get created service from resource registry
        HttpClient marketplaceServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<ServiceInfo> services = MarketplaceService.searchForServices(this,
                marketplaceServiceClient, accessToken, SVC_COMPONENT_SERVICE_NAME, NETWORK_VISIBILITY_FILTER,"");
        ServiceInfo serviceFound = services.get(0);  //we will only get one as we search by exact name
        String serviceId = serviceFound.getServiceId();
        String schemaReference = serviceFound.getConnectionSchemas()[0].getId();  //this will only have one schema


        //create connection under client component
        HttpClient connectionServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ArrayList<com.wso2.choreo.integration.models.marketplace.Environment> environmentsToQuery =
                new ArrayList<>();
        for (Environment env : clientComponentEnvironments) {
            environmentsToQuery.add(
                    com.wso2.choreo.integration.models.marketplace.Environment.builder()
                            .id(env.getTemplateId())
                            .isCritical(env.isCritical()).build()
            );
        }
        ArrayList<Visibility> visibilities = new ArrayList<>();
        Visibility componentVisibility = Visibility.builder().
                organizationUuid(orgUUID).projectUuid(projectOne.getId()).componentUuid(clientChoreoComponent.getId()).build();
        visibilities.add(componentVisibility);
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);

        connectionCreationReq = ConnectionCreateRequest.builder().name(connectionName)
                .description("Component Level connection for a secured service with public visibility")
                .serviceId(serviceId)
                .schemaReference(schemaReference)
                .environments(environmentsToQuery.toArray(new com.wso2.choreo.integration.models.marketplace.Environment[0]))
                .visibilities(visibilities.toArray(new Visibility[0]))
                .requestingServiceVisibility(PUBLIC_SERVICE)
                .orgIdInteger(orgId).build();
        String connectionId = ConnectionService.createChoreoConnection(this, connectionServiceClient,
                accessToken, connectionCreationReq,true,servicePublisherComponentEnvironments);
        componentLevelConnectionId = connectionId;
        //update component-config.yaml file
        //Let's consume service using organization visibility
        String serviceIdentifier = MarketplaceService.getChoreoServiceIdentifier(this,
                marketplaceServiceClient, accessToken, serviceId, ServiceVisibility.PUBLIC);
        Map<String, String> params = new HashMap<>();
        params.put("serviceIdentifier", serviceIdentifier);
        params.put("connectionId", connectionId);
        String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                "templates/marketplace/component-config.mustache", params);
        String encodedFileContent = Base64.getEncoder().
                encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
        GitHub.mergeNewCode(repoName, ".choreo/component-config.yaml", "Update component-config file", encodedFileContent);
    }
    @Test(dependsOnMethods = {"createComponentLevelConnection_TestChoreoConnections"})
    @CitrusTest
    public void deployServiceConsumerComponent_TestChoreoConnections() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
        clientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, clientChoreoComponent,
                environments, ComponentFlavour.BYOC);
    }

    //TODO: verify connection is partial
    public void verifyPartialConnection_TestChoreoConnections() throws Exception {

    }


    @Test(dependsOnMethods = {"deployServiceConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIDev_TestChoreoConnections() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponent, clientDeploymentStatusDTO, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"invokeAPIDev_TestChoreoConnections"})
    @CitrusTest
    public void promoteServicePublisherComponent_TestChoreoConnections() throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, publicEndpointServiceComponent,
                servicePublisherComponentEnvironments, ComponentFlavour.BYOC);
        ComponentDeploymentStatusDTO servicePromotionStatusDTO = statusDTO.get(0);  // we'll consider only the first promotion

    }
    @Test(dependsOnMethods = {"promoteServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void refreshComponentLevelConnection_TestChoreoConnections() throws Exception {
        HttpClient connectionServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ConnectionService.refreshChoreoConnection(this, connectionServiceClient,
                accessToken, componentLevelConnectionId, connectionCreationReq);
    }

    @Test(dependsOnMethods = {"refreshComponentLevelConnection_TestChoreoConnections"})
    @CitrusTest
    public void promoteClientComponent_TestChoreoConnections() throws Exception {
        List<ComponentDeploymentStatusDTO> statusDTO =  ComponentUtils.promoteComponent(this, citrusClients, accessToken, clientChoreoComponent,
                clientComponentEnvironments, ComponentFlavour.BYOC);
        clientPromotionStatusDTO = statusDTO.get(0);  //we'll consider only the first promotion
    }

    @Test(dependsOnMethods = {"promoteClientComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIStage_TestChoreoConnections() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponent, clientPromotionStatusDTO, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }


    @Test(dependsOnMethods = {"invokeAPIDev_TestChoreoConnections"})
    @CitrusTest
    public void undeployClientComponent_TestChoreoConnections() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(clientChoreoComponent.getId()).orgHandler(orgHandle)
                .componentType("byocRestApi").releaseId(clientDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"undeployClientComponent_TestChoreoConnections"})
    @CitrusTest
    public void undeployServiceComponent_TestChoreoConnections() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(publicEndpointServiceComponent.getId()).orgHandler(orgHandle)
                .componentType("byocRestApi").releaseId(publicEndpointServiceDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }

    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createPublicEndpointPublisherComponent_TestChoreoConnections() throws Exception {

        //create a proxy component
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        String apiName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        Pair<ChoreoComponent, ProxyAPI> componentDetail = ComponentUtils.createProxyComponent(this, citrusClients,
                accessToken, componentName, apiName, projectOne);
        //update proxy with details
        ProxyAPI proxyApi = componentDetail.getRight();
        ApiDTO apiDTO = ApiDTO.builder().apiName(proxyApi.getName()).
                description(proxyApi.getDescription()).
                productionEndpoint(Constant.DEFAULT_ENDPOINT).
                sandboxEndpoint(Constant.DEFAULT_ENDPOINT).
                basePath(proxyApi.getContext() + "/1.0.0").build();
        String apiPayload = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/proxyAPIUpdateRequestWithAPIRateLimit.mustache", apiDTO);  //This api is exposed in public
        Response response = APICreator.updateAPIWithRestAPIContent(proxyApi, apiPayload, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        //deploy proxy component
        proxyApiId=proxyApi.getId();
        proxyComponent = componentDetail.getLeft();
        proxyPublisherComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                proxyComponent);
        ComponentUtils.deployProxyComponent(this, citrusClients, accessToken,
                proxyComponent , proxyPublisherComponentEnvironments);
    }

    @Test(dependsOnMethods = {"createPublicEndpointPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToSecuredPublicService_TestChoreoConnections() throws Exception {
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                proxyComponent.getName(), NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for a secured service with public visibility",PUBLIC_SERVICE,
                true,proxyPublisherComponentEnvironments);

    }

    @Test(dependsOnMethods = {"createProjectLevelConnectionToSecuredPublicService_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToUnSecuredPublicService_TestChoreoConnections() throws Exception {
        ConnectionService.disableEndpointSecurity(this,citrusClients,proxyApiId,accessToken);
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                proxyComponent);
        ComponentUtils.deployProxyComponent(this, citrusClients, accessToken,
                proxyComponent , environments);
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                proxyComponent.getName(), NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for an unsecured service with public visibility",PUBLIC_SERVICE,
                false,proxyPublisherComponentEnvironments);

    }


    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createOrgEndpointPublisherComponent_TestChoreoConnections() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(ORG_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(ORG_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(ORG_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, projectOne, repo);
        orgEndpointServiceComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        orgEndpointComponentDeployedEnvs = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                orgEndpointServiceComponent);
        orgEndpointServiceDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                orgEndpointServiceComponent, orgEndpointComponentDeployedEnvs, ComponentFlavour.BYOC);
    }


    @Test(dependsOnMethods = {"createOrgEndpointPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToSecuredOrgService_TestChoreoConnections() throws Exception {
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                orgEndpointServiceComponent.getName(), ORG_LVL_NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for a secured service with org visibility",ORGANIZATION_SERVICE,
                true,orgEndpointComponentDeployedEnvs);
    }

    @Test(dependsOnMethods = {"createProjectLevelConnectionToSecuredOrgService_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToUnSecuredOrgService_TestChoreoConnections() throws Exception {
        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this,citrusClients,accessToken,orgEndpointServiceComponent,orgEndpointServiceDeploymentStatusDTO);
        ConnectionService.disableEndpointSecurity(this,citrusClients,endpoints.get(0).getApimId(),accessToken);
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                orgEndpointServiceComponent);
        ComponentUtils.deployComponent(this, citrusClients, accessToken,
                orgEndpointServiceComponent, environments, ComponentFlavour.BYOC);
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                orgEndpointServiceComponent.getName(), ORG_LVL_NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for an unsecured service with org visibility",ORGANIZATION_SERVICE,
                false,orgEndpointComponentDeployedEnvs);

    }

    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createProjectEndpointPublisherComponent_TestChoreoConnections() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(PROJECT_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(PROJECT_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(PROJECT_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();


        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, projectOne, repo);
        projectEndpointServiceComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        projectEndpointComponentDeployedEnvs = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                projectEndpointServiceComponent);
        ComponentUtils.deployComponent(this, citrusClients, accessToken,
                projectEndpointServiceComponent, projectEndpointComponentDeployedEnvs, ComponentFlavour.BYOC);
    }

    @Test(dependsOnMethods = {"createProjectEndpointPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToProjectService_TestChoreoConnections() throws Exception {
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                projectEndpointServiceComponent.getName(), PROJECT_LVL_NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for a service with project visibility",PROJECT_SERVICE,
                false,projectEndpointComponentDeployedEnvs);
    }

}
