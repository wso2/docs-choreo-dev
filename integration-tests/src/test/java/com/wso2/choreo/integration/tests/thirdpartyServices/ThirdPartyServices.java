package com.wso2.choreo.integration.tests.thirdpartyServices;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.marketplace.ConnectionService;
import com.wso2.choreo.integration.apis.marketplace.MarketplaceService;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.marketplace.Config;
import com.wso2.choreo.integration.models.marketplace.ConnectionSchemaEntry;
import com.wso2.choreo.integration.models.marketplace.Idl;
import com.wso2.choreo.integration.models.marketplace.ResourceType;
import com.wso2.choreo.integration.models.marketplace.SchemaInfo;
import com.wso2.choreo.integration.models.marketplace.ServiceStatus;
import com.wso2.choreo.integration.models.marketplace.ServiceType;
import com.wso2.choreo.integration.models.marketplace.ThirdPartyConnectionCreationRequest;
import com.wso2.choreo.integration.models.marketplace.ThirdPartyService;
import com.wso2.choreo.integration.models.marketplace.ThirdPartyServiceConnectionResponse;
import com.wso2.choreo.integration.models.marketplace.ThirdPartyServiceCreateResponse;
import com.wso2.choreo.integration.models.marketplace.ThirdPartyServiceEndpointConfig;
import com.wso2.choreo.integration.models.marketplace.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdPartyServices extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private String orgUuid;
    private ChoreoProject projectOne;

    private ThirdPartyServiceCreateResponse thirdPartyServiceResponse;

    private ThirdPartyService thirdPartyService;

    private ThirdPartyServiceConnectionResponse connectionInfo;

    HttpClient httpClient;

    private byte[] THIRD_PARTY_SERVICE_IDL;

    private List<Environment> projectEnvironments;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ThirdPartyServices() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        THIRD_PARTY_SERVICE_IDL = new ClassPathResource(
                "templates/thirdpartyService/openapi_content.yaml").getInputStream().readAllBytes();
    }

    @Test
    @CitrusTest
    public void testCreateProject() throws Exception{
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        projectOne = ComponentUtils.createProject(this, citrusClients, accessToken,
                Constant.region.US.toString());
        GraphqlDTO dto = GraphqlDTO.builder()
                .projectId(projectOne.getId())
                .orgUuid(orgUuid)
                .build();
        projectEnvironments = GraphQL.getEnvironments(this, httpClient, accessToken, dto);
    }

    @Test(dependsOnMethods = {"testCreateProject"})
    @CitrusTest
    public void testCreateThirdPartyService() throws TokenRetrievalException, IOException, URISyntaxException {
        // Test: Create a third party service
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        Idl idl = new Idl();
        idl.setIdlType("OpenAPI");
        idl.setContent(Base64.getEncoder().encodeToString(THIRD_PARTY_SERVICE_IDL));
        idl.setEnvironmentId("third-party-service");

        SchemaInfo[] connectionSchemas = new SchemaInfo[1];
        SchemaInfo schemaInfo = new SchemaInfo();
        schemaInfo.setDefault(true);
        schemaInfo.setName("TestThirdPartyService - Connection Configuration");
        schemaInfo.setDescription("Connection configuration for the third party service");
        schemaInfo.setEntries(new ConnectionSchemaEntry[]{
                ConnectionSchemaEntry.builder()
                        .description("invoke url of the service")
                        .name("ServiceURL")
                        .type("string")
                        .isOptional(false)
                        .isOptional(false).build(),
                ConnectionSchemaEntry.builder()
                        .description("api key header")
                        .name("APIKey")
                        .type("string")
                        .isOptional(false)
                        .isOptional(false).build()

        });
        connectionSchemas[0] = schemaInfo;

        ThirdPartyService serviceRequest = ThirdPartyService.builder()
                .isThirdParty(true)
                .name("TestThirdPartyService")
                .serviceType(ServiceType.REST)
                .resourceType(ResourceType.SERVICE)
                .idl(idl)
                .summary("Test third party service")
                .projectId(projectOne.getId())
                .organizationId(orgUuid)
                .status(ServiceStatus.CREATED)
                .version("1.0.0")
                .connectionSchemas(connectionSchemas)
                .visibility(new String[]{"PROJECT"})
                .build();

        thirdPartyServiceResponse = MarketplaceService.createThirdPartyService(
                this, httpClient, accessToken, serviceRequest);
    }

    @Test(dependsOnMethods = {"testCreateThirdPartyService"})
    @CitrusTest
    public void testGetThirdPartyService() throws IOException {
        // Test: Create a third party service
        thirdPartyService = MarketplaceService.getServiceById(
                this, httpClient, accessToken, thirdPartyServiceResponse.getId());

    }

    @Test(dependsOnMethods = {"testGetThirdPartyService"})
    @CitrusTest
    public void testCreateThirdPartyServiceEndpoints() throws IOException {

        // Test: Create a third party service
        Map<String, Config> configMap = new HashMap<>();
        Config prodEndpoint = new Config();
        prodEndpoint.setName("ProdEndpoint");
        String[] envIds = projectEnvironments.stream().map(Environment::getTemplateId).toArray(String[]::new);
        prodEndpoint.setEnvironmentTemplateIds(envIds);
        prodEndpoint.setValues(new Config.Value[]{
                new Config.Value("ServiceURL", "https://prod.api.com"),
                new Config.Value("APIKey", "prod-api-key")
        });
        configMap.put("ProdEndpoint", prodEndpoint);
        ThirdPartyServiceEndpointConfig endpointConfig = new ThirdPartyServiceEndpointConfig();
        endpointConfig.setConfigs(configMap);
        MarketplaceService.createThirdPartyServiceEndpoints(
                this, httpClient, accessToken, thirdPartyServiceResponse.getId(),
                thirdPartyService.getConnectionSchemas()[0].getId(), endpointConfig);
    }

    @Test(dependsOnMethods = {"testCreateThirdPartyServiceEndpoints"})
    @CitrusTest
    public void testUpdateServiceStatus() throws IOException {
        // Test: Create a third party service
        MarketplaceService.updateServiceStatus(
                this, httpClient, accessToken, thirdPartyServiceResponse.getId(), ServiceStatus.PUBLISHED);

    }

    @Test(dependsOnMethods = {"testUpdateServiceStatus"})
    @CitrusTest
    public void testCreateConnectionToThirdPartyService() throws IOException {
        // Test: Create a third party service
        Map<String, ArrayList<String>> envEndpointMap = MarketplaceService.getThirdPartyServiceEndpointForEnv(
                this, httpClient, accessToken, thirdPartyServiceResponse.getId(), thirdPartyService.getConnectionSchemas()[0].getId(),
                projectEnvironments.stream().map(Environment::getTemplateId).toArray(String[]::new));

        Map<String, ThirdPartyConnectionCreationRequest.ResourceReference> envMapping = new HashMap<>();
        for (Environment env : projectEnvironments) {
            String templateId = env.getTemplateId();
            ArrayList<String> references = envEndpointMap.get(env.getTemplateId());
            if (references == null) {
                continue;
            }
            envMapping.put(templateId, ThirdPartyConnectionCreationRequest.ResourceReference.builder()
                    .resourceId(templateId)
                    .parameterReference(references.get(0))
                    .build());
        }
        HashMap<String, ThirdPartyConnectionCreationRequest.Configuration> configurationMap = new HashMap<>();

        for (Environment env : projectEnvironments) {
            ThirdPartyConnectionCreationRequest.Configuration configuration = new ThirdPartyConnectionCreationRequest.Configuration();
            configuration.setEnvironmentUuid(env.getTemplateId());
            configuration.setCritical(false);

            HashMap<String, ThirdPartyConnectionCreationRequest.ConfigEntry> entries = new HashMap<>();
            entries.put("ServiceURL", ThirdPartyConnectionCreationRequest.ConfigEntry.builder()
                    .key("ServiceURL")
                    .value("")
                    .isSensitive(false)
                    .isFile(false)
                    .build());
            entries.put("APIKey", ThirdPartyConnectionCreationRequest.ConfigEntry.builder()
                    .key("APIKey")
                    .value("")
                    .isSensitive(false)
                    .isFile(false)
                    .build());
            configuration.setEntries(entries);
            configurationMap.put(env.getTemplateId(), configuration);
        }


        ThirdPartyConnectionCreationRequest request = ThirdPartyConnectionCreationRequest.builder()
                .serviceId(thirdPartyServiceResponse.getId())
                .visibilities(
                        new Visibility[]{
                                Visibility.builder()
                                        .organizationUuid(thirdPartyService.getOrganizationId())
                                        .projectUuid(thirdPartyService.getProjectId())
                                        .build()
                        })
                .schemaReference(thirdPartyService.getConnectionSchemas()[0].getId())
                .envMapping(envMapping)
                .configurations(configurationMap)
                .name("TestThirdPartyServiceConnection")
                .build();

        connectionInfo = ConnectionService.createThirdPartyServiceConnection(
                this, httpClient, accessToken, request);
    }

    @CitrusTest
    @Test(dependsOnMethods = {"testCreateConnectionToThirdPartyService"})
    public void testDeleteThirdPartyConnection() throws IOException {
        ConnectionService.deleteChoreoConnection(accessToken, connectionInfo.getGroupUuid());
    }

    @Test(dependsOnMethods = {"testDeleteThirdPartyConnection"})
    @CitrusTest
    public void testDeleteThirdPartyService() throws IOException {
        MarketplaceService.deleteThirdPartyService(this, httpClient, accessToken, thirdPartyServiceResponse.getId());
    }
}
