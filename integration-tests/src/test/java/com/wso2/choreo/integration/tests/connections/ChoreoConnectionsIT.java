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
import com.wso2.choreo.integration.common.*;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Tests related to Choreo connection creation and use.
 */
public class ChoreoConnectionsIT extends TestNGCitrusSpringSupport {

    private static final String SVC_COMPONENT_NAME = "";
    private static final String SVC_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/connection-test-loyalty-service";
    private static final String SVC_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";

    private static final String SVC_COMPONENT_ENDPOINT_NAME = "Loyalty Engine";
    private static String SVC_COMPONENT_SERVICE_NAME = "";
    private static final String CLIENT_COMPONENT_NAME = "";

    private static final String NETWORK_VISIBILITY_FILTER = "org,public";
    private static final String CLIENT_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/connection-test-reward-management-api";
    private static final String CLIENT_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";

    private HttpClient appServiceClient;
    private String accessToken;
    private String orgHandle;
    private int orgId;
    private String orgUUID;
    ChoreoProject project;
    private ChoreoComponent serviceChoreoComponent;
    private ChoreoComponent clientChoreoComponent;
    private List<Environment> clientComponentEnvironments;
    private ComponentDeploymentStatusDTO serviceDeploymentStatusDTO, servicePromotionStatusDTO;
    private ComponentDeploymentStatusDTO clientDeploymentStatusDTO, clientPromotionStatusDTO;
    private String githubOrg;

    private final String repoName = "connection-test-reward-management-api";


    private String API_INVOCATION_REQUEST_URI;
    private String API_INVOCATION_REQUEST_BODY;
    private String REST_API_EXPECTED_RESPONSE;
    private static final int REQUEST_COUNT = 5;

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
        project = ComponentUtils.createProject(this, citrusClients, accessToken,
                Constant.region.US.toString());
    }
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createServicePublisherComponent_TestChoreoConnections() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Repository repo = Repository.builder().
                repoUrl(SVC_COMPONENT_REPO_URL).
                oasFilePath("openapi.yaml").
                dockerfilePath(SVC_COMPONENT_DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, project, repo);

        serviceChoreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
    }
    @Test(dependsOnMethods = {"createServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void deployServicePublisherComponent_TestChoreoConnections() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                serviceChoreoComponent);
        serviceDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                serviceChoreoComponent, environments, ComponentFlavour.BYOC);
        SVC_COMPONENT_SERVICE_NAME = serviceChoreoComponent.getName().concat("-").concat(SVC_COMPONENT_ENDPOINT_NAME);
    }
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createServiceConsumerComponent_TestChoreoConnections() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Repository repo = Repository.builder().
                repoUrl(CLIENT_COMPONENT_REPO_URL).
                oasFilePath("openapi.yaml").
                dockerfilePath(CLIENT_COMPONENT_DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, project, repo);

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
                marketplaceServiceClient, accessToken, SVC_COMPONENT_SERVICE_NAME, NETWORK_VISIBILITY_FILTER);
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
                organizationUuid(orgUUID).projectUuid(project.getId()).componentUuid(clientChoreoComponent.getId()).build();
        visibilities.add(componentVisibility);

        ConnectionCreateRequest connectionReq = ConnectionCreateRequest.builder().name("Loyalty-svc-connection")
                .description("Connection for loyalty service")
                .serviceId(serviceId)
                .schemaReference(schemaReference)
                .environments(environmentsToQuery.toArray(new com.wso2.choreo.integration.models.marketplace.Environment[0]))
                .visibilities(visibilities.toArray(new Visibility[0]))
                .requestingServiceVisibility("PUBLIC")
                .orgIdInteger(orgId).build();
        String connectionId = ConnectionService.createChoreoConnection(this, connectionServiceClient,
                accessToken, connectionReq);

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

    //consider promoting


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
    public void undeployClientComponent_TestChoreoConnections() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(clientChoreoComponent.getId()).orgHandler(orgHandle)
                .componentType("byocRestApi").releaseId(clientDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
    @Test(dependsOnMethods = {"undeployClientComponent_TestChoreoConnections"})
    @CitrusTest
    public void undeployServiceComponent_TestChoreoConnections() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(serviceChoreoComponent.getId()).orgHandler(orgHandle)
                .componentType("byocRestApi").releaseId(serviceDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, appServiceClient, accessToken, graphqlDTO);
    }
}
