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
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.github.GitHub;
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
import com.wso2.choreo.integration.common.choreoproject.ComponentRepository;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.marketplace.ConnectionCreateRequest;
import com.wso2.choreo.integration.models.marketplace.ServiceInfo;
import com.wso2.choreo.integration.models.marketplace.ServiceStatus;
import com.wso2.choreo.integration.models.marketplace.ServiceVisibility;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Tests related to Choreo connection creation and use.
 */
public class ChoreoConnections extends TestNGCitrusSpringSupport {

    private static final String SVC_COMPONENTS_REPO_URL = "https://github.com/choreo-test-apps/connection-test-loyalty-service";
    private static final String WEBAPP_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/choreo-samples";
    private static final String WEBAPP_COMPONENT_DOCKER_CONTEXT = "react-single-page-app/";

    private static final String PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT = "loyalty-service-public-visibility/";
    private static final String ORG_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT = "loyalty-service-org-visibility/";
    private static final String PROJECT_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT = "loyalty-service-project-visibility/";
    private static final String SVC_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";
    private static final String OAS_FILE_PATH = "openapi.yaml";

    private static String SVC_COMPONENT_SERVICE_NAME = "";
    private static String PROJECT_VISIBILITY_SVC_COMPONENT_SERVICE_NAME = "";
    private static String ORG_VISIBILITY_SVC_COMPONENT_SERVICE_NAME = "";
    private static String DEPLOYED_SVC_COMPONENT_SERVICE_NAME = "connections-publisher-";
    private static String PROXY_COMPONENT_SRC_SERVICE_NAME = "loyalty-service-";
    private static String PROXY_COMPONENT_ENDPOINT;
    private static String PREV_CREATED_CLIENT_COMPONENT_SERVICE_NAME = "connections-consumer-one-";
    private static String DEPLOYED_COMPONENTS_PROJECT_NAME = "integration-test-project-V2";
    private static String DEPLOYED_CLIENT_COMPONENT_SERVICE_NAME = "connections-consumer-two-";
    private static final String NETWORK_VISIBILITY_FILTER = "org,public";
    private static final String ORG_LVL_NETWORK_VISIBILITY_FILTER = "org";
    private static final String PROJECT_LVL_NETWORK_VISIBILITY_FILTER = "project";
    private static final String PUBLIC_SERVICE = "PUBLIC";
    private static final String ORGANIZATION_SERVICE = "ORGANIZATION";
    private static final String PROJECT_SERVICE = "PROJECT";
    private static final String CLIENT_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/connections-test";
    private static final String API_KEY_CLIENT_COMPONENT_REPO_URL = "https://github.com/choreo-test-apps/connection-test-api-key";
    private static final String CLIENT_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";
    private static final String SERVICE_PUBLISHER_COMPONENT_REPO_NEW_BRANCH_NAME = "next";
    private HttpClient appServiceClient;
    private String orgHandle;
    private int orgId;
    private String orgUUID;
    ChoreoProject projectOne;
    private ChoreoComponent publicEndpointServiceComponent;
    private ChoreoComponent publicEndpointServiceComponentNewVersion;
    private ChoreoComponent orgEndpointServiceComponent;
    private ChoreoComponent projectEndpointServiceComponent;

    private ChoreoComponent clientChoreoComponent;
    private ChoreoComponent apiKeyEnabledClientChoreoComponent;
    private ChoreoComponent proxyComponent;
    private ChoreoComponent newClientChoreoComponent;
    private String proxyApiId;
    private ProxyAPIBuild proxyAPIBuild;

    private String componentLevelConnectionId;
    private String componentLevelNewConnectionId;
    private List<Environment> servicePublisherComponentEnvironments;
    private List<Environment> proxyPublisherComponentEnvironments;
    private List<Environment> orgEndpointComponentDeployedEnvs;
    private List<Environment> projectEndpointComponentDeployedEnvs;
    private List<Environment> clientComponentEnvironments;
    private List<Environment> newClientComponentEnvironments;
    private List<Environment> apiKeyEnabledClientComponentEnvironments;
    private ConnectionCreateRequest componentLevelConnectionCreationReq;
    private ConnectionCreateRequest componentLevelUnsecuredPublicConnectionCreationReq;
    private ConnectionCreateRequest componentLevelProjectVisibilityConnectionCreationReq;
    private ConnectionCreateRequest deployedServiceConnectionCreationReq;
    private ComponentDeploymentStatusDTO publicEndpointServiceDeploymentStatusDTO;
    private ComponentDeploymentStatusDTO projectEndpointServiceDeploymentStatusDTO;
    private ComponentDeploymentStatusDTO orgEndpointServiceDeploymentStatusDTO;
    private ComponentDeploymentStatusDTO clientDeploymentStatusDTO, clientPromotionStatusDTO,
            newClientDeploymentStatusDTO, newClientPromotionStatusDTO, apiKeyEnabledClientDeploymentStatusDTO,
            apiKeyEnabledClientPromotionStatusDTO;
    private final String repoName = "connections-test";
    private String API_INVOCATION_REQUEST_URI;
    private String API_INVOCATION_REQUEST_BODY;
    private String API_NEW_VERSION_INVOCATION_REQUEST_BODY;
    private String REST_API_EXPECTED_RESPONSE;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestChoreoConnectionTestCase() throws Exception {
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
        API_NEW_VERSION_INVOCATION_REQUEST_BODY = "{\n" +
                "  \"acceptedTnC\": true,\n" +
                "  \"selectedRewardDealId\": \"RWD34589\",\n" +
                "  \"userId\": \"U451301\"\n" + // This user is added in the new version
                "}";
        REST_API_EXPECTED_RESPONSE = new String(new ClassPathResource(
                "templates/connectionManagement/loyaltyServiceResponse.json").getInputStream().readAllBytes());
    }

    @Test
    @CitrusTest
    public void createProject_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        projectOne = ComponentUtils.createProject(this, citrusClients, accessToken,
                Constant.region.US.toString());
    }
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createServicePublisherComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
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
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        servicePublisherComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                publicEndpointServiceComponent);
        publicEndpointServiceDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                publicEndpointServiceComponent, servicePublisherComponentEnvironments, ComponentFlavour.BYOC);
        SVC_COMPONENT_SERVICE_NAME = publicEndpointServiceComponent.getName();
    }
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"})
    @CitrusTest
    public void createServiceConsumerComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
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
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, publicEndpointServiceComponent,
                publicEndpointServiceDeploymentStatusDTO);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,this,accessToken,SVC_COMPONENT_SERVICE_NAME,NETWORK_VISIBILITY_FILTER,"");
        componentLevelConnectionCreationReq = ConnectionService.createComponentLevelConnectionCreationReq(clientComponentEnvironments,projectOne.getId(),
                clientChoreoComponent.getId(),PUBLIC_SERVICE,serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String serviceId = serviceFound.getServiceId();
        String connectionId = ConnectionService.createChoreoConnection(this, httpClient,
                accessToken, componentLevelConnectionCreationReq, true, servicePublisherComponentEnvironments.subList(0,1), false);
        componentLevelConnectionId = connectionId;
        //update component-config.yaml file
        //Let's consume service using public visibility
        String serviceIdentifier = MarketplaceService.getChoreoServiceIdentifier(this,
                httpClient, accessToken, serviceId, ServiceVisibility.PUBLIC);
        Map<String, String> params = new HashMap<>();
        params.put("serviceIdentifier", serviceIdentifier);
        params.put("connectionId", connectionId);
        String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                "templates/marketplace/component-config.mustache", params);
        String encodedFileContent = Base64.getEncoder().
                encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
        GitHub.mergeNewCode(repoName, ".choreo/component-config.yaml", "Update component-config file", encodedFileContent, null);
    }
    @Test(dependsOnMethods = {"createComponentLevelConnection_TestChoreoConnections"})
    @CitrusTest
    public void deployServiceConsumerComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
        clientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, clientChoreoComponent,
                environments, ComponentFlavour.BYOC);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponent,
                clientDeploymentStatusDTO);
    }

    @Test(dependsOnMethods = {"deployServiceConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIDev_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
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
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, publicEndpointServiceComponent,
                servicePublisherComponentEnvironments, ComponentFlavour.BYOC, projectOne);
        ComponentDeploymentStatusDTO servicePromotionStatusDTO = statusDTO.get(0);  // we'll consider only the first promotion

    }

    @Test(dependsOnMethods = {"promoteServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void verifyServiceStatus_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,
                this, accessToken, SVC_COMPONENT_SERVICE_NAME, NETWORK_VISIBILITY_FILTER,"");
        if (serviceFound == null) {
            throw new ValidationException("Service not found");
        }
        if (serviceFound.getStatus() != ServiceStatus.PUBLISHED) {
            throw new ValidationException("Service is not in active state");
        }
    }

    @Test(dependsOnMethods = {"promoteServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void refreshComponentLevelConnection_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        HttpClient connectionServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ConnectionService.refreshChoreoConnection(this, connectionServiceClient,
                accessToken, componentLevelConnectionId, componentLevelConnectionCreationReq,
                servicePublisherComponentEnvironments, true, false);
    }

    @Test(dependsOnMethods = {"refreshComponentLevelConnection_TestChoreoConnections"})
    @CitrusTest
    public void promoteClientComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<ComponentDeploymentStatusDTO> statusDTO =  ComponentUtils.promoteComponent(this, citrusClients, accessToken, clientChoreoComponent,
                clientComponentEnvironments, ComponentFlavour.BYOC , projectOne, clientDeploymentStatusDTO.getBuild().getCommit().getSha());
        clientPromotionStatusDTO = statusDTO.get(0);  //we'll consider only the first promotion
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponent,
                clientPromotionStatusDTO);
    }

    @Test(dependsOnMethods = {"promoteClientComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIStage_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponent, clientPromotionStatusDTO, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"invokeAPIStage_TestChoreoConnections"})
    @CitrusTest
    public void createNewVersionOfServicePublisherComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String branchName = SERVICE_PUBLISHER_COMPONENT_REPO_NEW_BRANCH_NAME;
        publicEndpointServiceComponentNewVersion = ComponentUtils.createComponentVersion(this, citrusClients,
                accessToken, publicEndpointServiceComponent, "v1.1", branchName);
        ComponentRepository repository = publicEndpointServiceComponentNewVersion.getRepository();
        repository.setBranchApp(branchName);
        publicEndpointServiceComponentNewVersion.setRepository(repository);
    }

    @Test(dependsOnMethods = {"createNewVersionOfServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void deployServicePublisherComponentNewVersion_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, publicEndpointServiceComponentNewVersion);
        ComponentDeploymentStatusDTO componentDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                publicEndpointServiceComponentNewVersion, environments, ComponentFlavour.BYOC);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, publicEndpointServiceComponentNewVersion,
                componentDeploymentStatusDTO);
    }

    @Test(dependsOnMethods = {"deployServicePublisherComponentNewVersion_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIDevWithNewPublisherServiceVersion_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                clientChoreoComponent);
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponent, clientDeploymentStatusDTO, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(),
                API_INVOCATION_REQUEST_URI, API_NEW_VERSION_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE,
                HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"invokeAPIDevWithNewPublisherServiceVersion_TestChoreoConnections"})
    @CitrusTest
    public void setUpEndpointForProxy_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Repository proxySourceRepo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();

        ChoreoComponent proxySourceComponent = ComponentUtils.getReusableComponent(this,accessToken,proxySourceRepo,
                PROXY_COMPONENT_SRC_SERVICE_NAME,citrusClients,ComponentFlavour.BYOC,Constant.displayType.byocService.name());
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                proxySourceComponent);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, proxySourceComponent);
        List<Endpoint> endpoints;
        int environmentListSize =  environments.size();
        //check the deployment status of prod environment
        ComponentDeploymentStatusDTO deployedProxySourceComponentStatus =
                ComponentUtils.validateComponentDeployment(this,citrusClients,accessToken,proxySourceComponent,latestCommit,
                        environments.subList(environmentListSize-1,environmentListSize),true);

        if(deployedProxySourceComponentStatus == null){
            PROXY_COMPONENT_ENDPOINT = ConnectionService.getEndpointForProxy(this,citrusClients,accessToken,
                    proxySourceComponent, environments);
            return;
        }
        if(!deployedProxySourceComponentStatus.getDeploymentStatusV2().equals("ACTIVE")){
            throw new ValidationException("loyalty-service-component is not in active state");
        }
        endpoints = ComponentUtils.getEndpoints(this,citrusClients,accessToken,proxySourceComponent,
                deployedProxySourceComponentStatus);
        PROXY_COMPONENT_ENDPOINT = endpoints.get(0).getPublicUrl();
    }

    @Test(dependsOnMethods = {"setUpEndpointForProxy_TestChoreoConnections"})
    @CitrusTest
    public void createPublicEndpointPublisherComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        //create a proxy component
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        String apiName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        Pair<ChoreoComponent, ProxyAPI> componentDetail = ComponentUtils.createProxyComponent(this, citrusClients,
                accessToken, componentName, apiName, projectOne);
        //update proxy with details
        ProxyAPI proxyApi = componentDetail.getRight();
        ApiDTO apiDTO = ApiDTO.builder().apiName(proxyApi.getName()).
                description(proxyApi.getDescription()).
                productionEndpoint(PROXY_COMPONENT_ENDPOINT).
                sandboxEndpoint(PROXY_COMPONENT_ENDPOINT).
                basePath(proxyApi.getContext() + "/v1.0").build();
        String apiPayload = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/proxyAPIUpdateRequestWithAPIRateLimit.mustache", apiDTO);  //This api is exposed in public
        Response response = APICreator.updateAPIWithRestAPIContent(proxyApi, apiPayload, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        //deploy proxy component
        proxyApiId = proxyApi.getId();
        proxyComponent = componentDetail.getLeft();
        proxyPublisherComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                proxyComponent);
        proxyAPIBuild = ComponentUtils.deployProxyComponent(this, citrusClients, accessToken,
                proxyComponent, proxyPublisherComponentEnvironments.subList(0, 1));
    }

    @Test(dependsOnMethods = {"createPublicEndpointPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToSecuredPublicService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                proxyComponent.getName(), NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for a secured service with public visibility", PUBLIC_SERVICE,
                true, proxyPublisherComponentEnvironments.subList(0, 1));

    }

    @Test(dependsOnMethods = {"invokeAPIStageForProxyBasedConnection_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToUnSecuredPublicService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
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
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
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
        ORG_VISIBILITY_SVC_COMPONENT_SERVICE_NAME = orgEndpointServiceComponent.getName();
    }


    @Test(dependsOnMethods = {"createOrgEndpointPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToSecuredOrgService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, orgEndpointServiceComponent, orgEndpointServiceDeploymentStatusDTO);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                orgEndpointServiceComponent.getName(), ORG_LVL_NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for a secured service with org visibility",ORGANIZATION_SERVICE,
                true,orgEndpointComponentDeployedEnvs.subList(0,1));
    }

    @Test(dependsOnMethods = {"createProjectLevelConnectionToSecuredOrgService_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToUnSecuredOrgService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this,citrusClients,accessToken,orgEndpointServiceComponent,orgEndpointServiceDeploymentStatusDTO);
        ConnectionService.disableEndpointSecurity(this,citrusClients,endpoints.get(0).getApimId(),accessToken);
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                orgEndpointServiceComponent);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, orgEndpointServiceComponent);
        orgEndpointServiceDeploymentStatusDTO = ComponentUtils.deployBuiltComponent(this, citrusClients, accessToken,
                orgEndpointServiceComponent, latestCommit, environments);
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, orgEndpointServiceComponent, orgEndpointServiceDeploymentStatusDTO);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                orgEndpointServiceComponent.getName(), ORG_LVL_NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for an unsecured service with org visibility",ORGANIZATION_SERVICE,
                false,orgEndpointComponentDeployedEnvs.subList(0,1));

    }

    @Test(dependsOnMethods = {"createProjectLevelConnectionToUnSecuredOrgService_TestChoreoConnections"})
    @CitrusTest
    public void promoteOrgEndpointServicePublisherComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, orgEndpointServiceComponent, orgEndpointComponentDeployedEnvs, ComponentFlavour.BYOC,
                projectOne);
        ComponentDeploymentStatusDTO servicePromotionStatusDTO = statusDTO.get(0);  // we'll consider only the first promotion
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, orgEndpointServiceComponent,
                servicePromotionStatusDTO);
        orgEndpointComponentDeployedEnvs = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, orgEndpointServiceComponent);
    }

    @Test(dependsOnMethods = {"promoteOrgEndpointServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createAPIKeyBasedConsumerService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(API_KEY_CLIENT_COMPONENT_REPO_URL).
                oasFilePath(OAS_FILE_PATH).
                dockerfilePath(CLIENT_COMPONENT_DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, projectOne, repo);

        apiKeyEnabledClientChoreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        apiKeyEnabledClientComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, apiKeyEnabledClientChoreoComponent);
    }

    @Test(dependsOnMethods = {"createAPIKeyBasedConsumerService_TestChoreoConnections"})
    @CitrusTest
    public void createComponentLevelConnectionToAPIKeyEnabledOrgService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this, citrusClients, accessToken,
                orgEndpointServiceComponent, orgEndpointServiceDeploymentStatusDTO);
        ConnectionService.enableAPIKeySecurityForAPI(this, citrusClients,endpoints.get(0).getApimId(),
                accessToken);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients, this, accessToken,
                ORG_VISIBILITY_SVC_COMPONENT_SERVICE_NAME, ORG_LVL_NETWORK_VISIBILITY_FILTER, "");
        componentLevelConnectionCreationReq = ConnectionService.createComponentLevelConnectionCreationReq(
                apiKeyEnabledClientComponentEnvironments, projectOne.getId(), apiKeyEnabledClientChoreoComponent.getId(),
                ORGANIZATION_SERVICE, serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String serviceId = serviceFound.getServiceId();
        String connectionId = ConnectionService.createChoreoConnection(this, httpClient,
                accessToken, componentLevelConnectionCreationReq, true,
                orgEndpointComponentDeployedEnvs, false);
        //update component-config.yaml file
        String serviceIdentifier = MarketplaceService.getChoreoServiceIdentifier(this,
                httpClient, accessToken, serviceId, ServiceVisibility.ORGANIZATION);
        Map<String, String> params = new HashMap<>();
        params.put("serviceIdentifier", serviceIdentifier);
        params.put("connectionId", connectionId);
        String updatedComponentConfigFileContent = MessageUtils.generateStringFromTemplate(
                "templates/marketplace/component-config-api-key.mustache", params);
        String encodedFileContent = Base64.getEncoder().
                encodeToString(updatedComponentConfigFileContent.getBytes(StandardCharsets.UTF_8));
        GitHub.mergeNewCode("connection-test-api-key", ".choreo/component-config.yaml",
                "Update component-config file", encodedFileContent, null);
    }

    @Test(dependsOnMethods = {"createComponentLevelConnectionToAPIKeyEnabledOrgService_TestChoreoConnections"})
    @CitrusTest
    public void deployAPIKeyBasedServiceConsumerComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        apiKeyEnabledClientComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, apiKeyEnabledClientChoreoComponent);
        apiKeyEnabledClientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                apiKeyEnabledClientChoreoComponent, apiKeyEnabledClientComponentEnvironments, ComponentFlavour.BYOC);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, apiKeyEnabledClientChoreoComponent,
                apiKeyEnabledClientDeploymentStatusDTO);
    }

    @Test(dependsOnMethods = {"deployAPIKeyBasedServiceConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIDevForAPIKeyEnabledOrgService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, apiKeyEnabledClientChoreoComponent);
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                apiKeyEnabledClientChoreoComponent, apiKeyEnabledClientDeploymentStatusDTO, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(),
                API_INVOCATION_REQUEST_URI, API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"invokeAPIDevForAPIKeyEnabledOrgService_TestChoreoConnections"})
    @CitrusTest
    public void promoteAPIKeyEnabledConsumerComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                accessToken, apiKeyEnabledClientChoreoComponent, apiKeyEnabledClientComponentEnvironments,
                ComponentFlavour.BYOC, projectOne);
        apiKeyEnabledClientPromotionStatusDTO = statusDTO.get(0);  // we'll consider only the first promotion
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, apiKeyEnabledClientChoreoComponent,
                apiKeyEnabledClientPromotionStatusDTO);
    }

    @Test(dependsOnMethods = {"promoteAPIKeyEnabledConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIStageForAPIKeyEnabledOrgService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, apiKeyEnabledClientChoreoComponent);
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                apiKeyEnabledClientChoreoComponent, apiKeyEnabledClientDeploymentStatusDTO, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(),
                API_INVOCATION_REQUEST_URI, API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"createProject_TestChoreoConnections"}, enabled = false)
    @CitrusTest
    public void createProjectEndpointPublisherComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
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
        projectEndpointServiceDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                projectEndpointServiceComponent, projectEndpointComponentDeployedEnvs, ComponentFlavour.BYOC);
        PROJECT_VISIBILITY_SVC_COMPONENT_SERVICE_NAME = projectEndpointServiceComponent.getName();

    }

    @Test(dependsOnMethods = {"createProjectEndpointPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createProjectLevelConnectionToProjectService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, projectEndpointServiceComponent, projectEndpointServiceDeploymentStatusDTO);
        ConnectionService.createProjectLevelConnection(citrusClients, this, accessToken,
                projectEndpointServiceComponent.getName(), PROJECT_LVL_NETWORK_VISIBILITY_FILTER, projectOne.getId(),
                connectionName, "Project level Connection for a service with project visibility",PROJECT_SERVICE,
                false,projectEndpointComponentDeployedEnvs.subList(0,1));
    }

    // Create a component level service connection to an unsecured internal service with public visibility
    @Test(dependsOnMethods = {"invokeAPIStage_TestChoreoConnections","createWebAppLevelConnectionToSecuredPublicService_TestChoreoConnections"})
    @CitrusTest
    public void createComponentLevelConnectionToUnsecuredPublicService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<Endpoint> endpoints = ComponentUtils.getEndpoints(this,citrusClients,accessToken,publicEndpointServiceComponent,
                publicEndpointServiceDeploymentStatusDTO);
        ConnectionService.disableEndpointSecurity(this,citrusClients,endpoints.get(0).getApimId(),accessToken);
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                publicEndpointServiceComponent);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, publicEndpointServiceComponent);
        ComponentUtils.deployBuiltComponent(this, citrusClients, accessToken,
                publicEndpointServiceComponent, latestCommit, environments);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, publicEndpointServiceComponent,
                publicEndpointServiceDeploymentStatusDTO);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,this,accessToken,SVC_COMPONENT_SERVICE_NAME,NETWORK_VISIBILITY_FILTER,"");
        componentLevelUnsecuredPublicConnectionCreationReq= ConnectionService.createComponentLevelConnectionCreationReq(clientComponentEnvironments,projectOne.getId(),
                clientChoreoComponent.getId(),PUBLIC_SERVICE,serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ConnectionService.createChoreoConnection(this, httpClient,
                accessToken, componentLevelUnsecuredPublicConnectionCreationReq,false, servicePublisherComponentEnvironments, false);
    }

    // Create a component level service connection to an internal service with project visibility
    @Test(dependsOnMethods = {"createProjectEndpointPublisherComponent_TestChoreoConnections","createServiceConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void createComponentLevelConnectionToProjectVisibilityService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, projectEndpointServiceComponent,
                projectEndpointServiceDeploymentStatusDTO);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,this,accessToken,PROJECT_VISIBILITY_SVC_COMPONENT_SERVICE_NAME,PROJECT_LVL_NETWORK_VISIBILITY_FILTER,projectOne.getId());
        componentLevelProjectVisibilityConnectionCreationReq= ConnectionService.createComponentLevelConnectionCreationReq(clientComponentEnvironments,projectOne.getId(),
                clientChoreoComponent.getId(),PROJECT_SERVICE,serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ConnectionService.createChoreoConnection(this, httpClient,
                accessToken, componentLevelProjectVisibilityConnectionCreationReq, false, projectEndpointComponentDeployedEnvs.subList(0,1), false);
    }

    // Create a component level webapp connection to an oauth2 protected internal service with public visibility
    @Test(dependsOnMethods = {"createProject_TestChoreoConnections", "deployServicePublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void createWebAppLevelConnectionToSecuredPublicService_TestChoreoConnections () throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);

        GraphqlDTO.ByocWebAppsConfig webAppsConfig = GraphqlDTO.ByocWebAppsConfig.builder()
                .dockerContext(WEBAPP_COMPONENT_DOCKER_CONTEXT)
                .srcGitRepoUrl(WEBAPP_COMPONENT_REPO_URL)
                .webAppType("React")
                .webAppBuildCommand("npm run build")
                .webAppPackageManagerVersion("18")
                .webAppOutputDirectory("/build")
                .build();
        GraphqlDTO dto = ComponentUtils.createWebappComponentRequest(componentName, projectOne, webAppsConfig);
        ChoreoComponent webAppComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.WEBAPP);

        List<Environment> webAppComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                webAppComponent);

        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, publicEndpointServiceComponent,
                publicEndpointServiceDeploymentStatusDTO);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,this,accessToken,SVC_COMPONENT_SERVICE_NAME,NETWORK_VISIBILITY_FILTER,"");
        ConnectionCreateRequest connectionCreationReq = ConnectionService.createComponentLevelConnectionCreationReq(webAppComponentEnvironments, 
                projectOne.getId(), webAppComponent.getId(), PUBLIC_SERVICE, serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ConnectionService.createChoreoConnection(this, httpClient,
                accessToken, connectionCreationReq, true, servicePublisherComponentEnvironments.subList(0,1), true);
    }

    //create a connection to service component which was previously deployed
    @Test(dependsOnMethods = {"deployServiceConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void createConnectionToDeployedService_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Repository publisherRepo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();

        Repository clientRepo = Repository.builder().
                repoUrl(CLIENT_COMPONENT_REPO_URL).
                oasFilePath(OAS_FILE_PATH).
                dockerfilePath(CLIENT_COMPONENT_DOCKER_FILE_PATH).build();

        ChoreoComponent deployedPublisherComponent = ComponentUtils.getReusableComponent(this,accessToken,publisherRepo,DEPLOYED_SVC_COMPONENT_SERVICE_NAME,citrusClients,ComponentFlavour.BYOC,Constant.displayType.byocService.name());
        ChoreoComponent createdClientComponent = ComponentUtils.getReusableComponent(this,accessToken,clientRepo,PREV_CREATED_CLIENT_COMPONENT_SERVICE_NAME,citrusClients,ComponentFlavour.BYOC,Constant.displayType.byocService.name());

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                deployedPublisherComponent);

        ComponentDeploymentStatusDTO deployedPublisherComponentStatus = ComponentUtils.deployAndValidateBuiltComponent(this, citrusClients, accessToken, deployedPublisherComponent,
                environments);

        if(deployedPublisherComponentStatus != null && !deployedPublisherComponentStatus.getDeploymentStatusV2().equals("ACTIVE")){
            throw new ValidationException("connections-publisher-component is not in active state");
        }

        if(deployedPublisherComponentStatus == null){
            deployedPublisherComponentStatus = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                    deployedPublisherComponent,environments, ComponentFlavour.BYOC);
        }

        ChoreoProject project = ComponentUtils.getProjectByName(DEPLOYED_COMPONENTS_PROJECT_NAME,accessToken);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, deployedPublisherComponent,
                deployedPublisherComponentStatus);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients,this,accessToken,DEPLOYED_SVC_COMPONENT_SERVICE_NAME,NETWORK_VISIBILITY_FILTER,"");

        List<Environment> deployedClientComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,createdClientComponent );
        deployedServiceConnectionCreationReq= ConnectionService.createComponentLevelConnectionCreationReq(deployedClientComponentEnvironments,project.getId(),
                createdClientComponent.getId(),PUBLIC_SERVICE,serviceFound);
        HttpClient httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ConnectionService.createChoreoConnection(this, httpClient,
                accessToken, deployedServiceConnectionCreationReq, true, deployedClientComponentEnvironments.subList(0,1), false);
    }

    //invoke a previously created connection
    @Test()
    @CitrusTest
    public void invokeOldConnection_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Repository publisherRepo = Repository.builder().
                repoUrl(SVC_COMPONENTS_REPO_URL).
                oasFilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+OAS_FILE_PATH).
                dockerfilePath(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT+SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(PUBLIC_ENDPOINTS_SVC_COMPONENT_DOCKER_CONTEXT).build();

        Repository clientRepo = Repository.builder().
                repoUrl(CLIENT_COMPONENT_REPO_URL).
                oasFilePath(OAS_FILE_PATH).
                dockerfilePath(CLIENT_COMPONENT_DOCKER_FILE_PATH).build();

        ChoreoComponent deployedPublisherComponent = ComponentUtils.getReusableComponent(this,accessToken,publisherRepo,DEPLOYED_SVC_COMPONENT_SERVICE_NAME,citrusClients,ComponentFlavour.BYOC,Constant.displayType.byocService.name());
        ChoreoComponent createdClientComponent = ComponentUtils.getReusableComponent(this,accessToken,clientRepo,DEPLOYED_CLIENT_COMPONENT_SERVICE_NAME,citrusClients,ComponentFlavour.BYOC,Constant.displayType.byocService.name(),"dev");

        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                deployedPublisherComponent);
        Commit publisherLatestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, deployedPublisherComponent);

        ComponentDeploymentStatusDTO deployedPublisherComponentStatus = ComponentUtils.validateComponentDeployment(this,citrusClients,accessToken,deployedPublisherComponent,publisherLatestCommit,environments,true);
        if(deployedPublisherComponentStatus != null && !deployedPublisherComponentStatus.getDeploymentStatusV2().equals("ACTIVE")){
            throw new ValidationException("connections-publisher-component is not in active state");
        }
        if(deployedPublisherComponentStatus == null){
            deployedPublisherComponentStatus = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                    deployedPublisherComponent, environments, ComponentFlavour.BYOC);
        }
        Commit clientLatestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, deployedPublisherComponent);
        ComponentDeploymentStatusDTO deployedClientComponentStatus = ComponentUtils.validateComponentDeployment(this,citrusClients,accessToken,createdClientComponent,clientLatestCommit,environments,true);
        if(deployedClientComponentStatus != null && !deployedClientComponentStatus.getDeploymentStatusV2().equals("ACTIVE")){
            throw new ValidationException("Client component is not in active state");
        }
        if(deployedClientComponentStatus == null){
            ChoreoProject project = ComponentUtils.getProjectByName(DEPLOYED_COMPONENTS_PROJECT_NAME,accessToken);
            ComponentUtils.validateEndpoints(this, citrusClients, accessToken, deployedPublisherComponent,
                    deployedPublisherComponentStatus);
            ConnectionService.createAndUseConnection(this,citrusClients,accessToken,deployedPublisherComponent.getName(),PUBLIC_SERVICE,project.getId(),createdClientComponent.getId(),environments,environments,repoName,"dev");
            deployedClientComponentStatus = ComponentUtils.deployComponent(this, citrusClients, accessToken,
                    createdClientComponent, environments, ComponentFlavour.BYOC);
        }
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                createdClientComponent, deployedClientComponentStatus, environments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"createProjectLevelConnectionToSecuredPublicService_TestChoreoConnections"})
    @CitrusTest
    public void useProjectLevelConnectionCreatedToProxyInAComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().
                repoUrl(CLIENT_COMPONENT_REPO_URL).
                oasFilePath(OAS_FILE_PATH).
                dockerfilePath(CLIENT_COMPONENT_DOCKER_FILE_PATH).build();

        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, projectOne, repo);
        newClientChoreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        newClientComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                newClientChoreoComponent);
        componentLevelNewConnectionId = ConnectionService.createAndUseConnection(this, citrusClients, accessToken,
                proxyComponent.getName(), PUBLIC_SERVICE, projectOne.getId(), newClientChoreoComponent.getId(),
                proxyPublisherComponentEnvironments, newClientComponentEnvironments, repoName);
    }

    @Test(dependsOnMethods = {"useProjectLevelConnectionCreatedToProxyInAComponent_TestChoreoConnections"})
    @CitrusTest
    public void deployNewServiceConsumerComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        newClientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, newClientChoreoComponent,
                newClientComponentEnvironments, ComponentFlavour.BYOC);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, newClientChoreoComponent,
                newClientDeploymentStatusDTO);
    }

    @Test(dependsOnMethods = {"deployNewServiceConsumerComponent_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIInDevForProxyBasedConnection_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                newClientChoreoComponent, newClientDeploymentStatusDTO, newClientComponentEnvironments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(), API_INVOCATION_REQUEST_URI,
                API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }

    @Test(dependsOnMethods = {"invokeAPIInDevForProxyBasedConnection_TestChoreoConnections"})
    @CitrusTest
    public void PromoteProxyPublisherComponent_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ComponentUtils.promoteProxyComponent(this, citrusClients, accessToken, proxyComponent,
                proxyPublisherComponentEnvironments, proxyAPIBuild);
    }

    @Test(dependsOnMethods = {"PromoteProxyPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void VerifyServiceStatusForProxyPromotion_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients, this, accessToken,
                proxyComponent.getName(), PUBLIC_SERVICE.toLowerCase(), "");

        if (serviceFound == null) {
            throw new ValidationException("Service not found");
        }
        if (serviceFound.getStatus() != ServiceStatus.PUBLISHED) {
            throw new ValidationException("Service is not in published state");
        }
    }
    
    @Test(dependsOnMethods = {"PromoteProxyPublisherComponent_TestChoreoConnections"})
    @CitrusTest
    public void refreshConnectionConfigurationsForProxyBasedConnection_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        HttpClient connectionServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        ServiceInfo serviceFound = ConnectionService.FindService(citrusClients, this, accessToken,
                proxyComponent.getName(), PUBLIC_SERVICE.toLowerCase(), "");
        ConnectionCreateRequest connectionReq = ConnectionService.createComponentLevelConnectionCreationReq(newClientComponentEnvironments,
                projectOne.getId(),
                newClientChoreoComponent.getId(), PUBLIC_SERVICE, serviceFound);
        ConnectionService.refreshChoreoConnection(this, connectionServiceClient,
                accessToken, componentLevelNewConnectionId, connectionReq,
                proxyPublisherComponentEnvironments, true, false);
    }

    @Test(dependsOnMethods = {"refreshConnectionConfigurationsForProxyBasedConnection_TestChoreoConnections"})
    @CitrusTest
    public void promoteClientComponentForProxy_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        List<ComponentDeploymentStatusDTO> newClientStatusDTO = ComponentUtils.promoteComponent(this,
                citrusClients, accessToken, newClientChoreoComponent,
                newClientComponentEnvironments, ComponentFlavour.BYOC, projectOne, 
                newClientDeploymentStatusDTO.getBuild().getCommit().getSha());
        newClientPromotionStatusDTO = newClientStatusDTO.get(0);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, newClientChoreoComponent,
                newClientPromotionStatusDTO);
    }

    @Test(dependsOnMethods = {"promoteClientComponentForProxy_TestChoreoConnections"})
    @CitrusTest
    public void invokeAPIStageForProxyBasedConnection_TestChoreoConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                newClientChoreoComponent, newClientPromotionStatusDTO, newClientComponentEnvironments);
        ComponentUtils.invokeApiPOST(this, invokeData.getRight().getApikey(), invokeData.getLeft(),
                API_INVOCATION_REQUEST_URI, API_INVOCATION_REQUEST_BODY, REST_API_EXPECTED_RESPONSE, HttpStatus.ACCEPTED);
    }
}
