/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.marketplace.ConnectionService;
import com.wso2.choreo.integration.apis.platformServices.PlatformServices;
import com.wso2.choreo.integration.common.*;
import com.wso2.choreo.integration.common.PlatformServices.PlatformServicesUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplate;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplatesListDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.marketplace.*;
import com.wso2.choreo.integration.models.platformServices.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Tests related to Choreo database connections creation and usage.
 */
public class DatabaseConnectionsTest extends TestNGCitrusSpringSupport {
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    HttpClient httpClient;
    private String orgUUID;
    private int orgId;
    private static CreatedDatabaseServer mysqlDatabaseServer;
    private static CreatedDatabaseServer redisDatabaseServer;

    private String mysqlDatabaseServerName = "automation-test-mysql-database-for-connections";
    private String redisDatabaseServerName = "automation-test-redis-database-for-connections";

    private final String devDatabaseName = "devHrDb";
    private final String prodDatabaseName = "prodHrDb";
    private Database devDatabase;
    private Database prodDatabase;
    private DatabaseCredentials devDbCredential;
    private DatabaseCredentials prodDbCredential;
    private DatabaseCredentials redisDbServerCredentials;

    private String devEnvironmentId;
    private String prodEnvironmentId;
    private CommonResource dbConnectingInDev;
    private CommonResource dbConnectingInProd;
    private ChoreoProject consumerProject;
    private ChoreoComponent clientChoreoComponent;
    private ChoreoComponent clientChoreoComponentNewVersion;
    private final String REPO_NAME = "choreo-samples";
    private static  String CLIENT_COMPONENT_REPO_URL = "";
    private static final String CLIENT_COMPONENT_DOCKET_CONTEXT = "appointment-management/appointment-service";
    private static final String CLIENT_COMPONENT_CHOREO_FOLDER_PATH = "appointment-management/appointment-service/.choreo";

    private static final String OAS_FILE_PATH = "openapi.yaml";

    private static ConnectionInfo databaseConnection;

    private String databaseConnectionName = "database_connection";
    private String databaseServerConnectionName = "integration-test-db-connection-for-db-server";
    private String REQ_BODY;
    private String DEV_KEY_NAME = "Test in Dev";
    private String PROD_KEY_NAME = "Test in Prod";

    @BeforeClass
    public void setup_TestDatabaseConnectionsTestCase() throws Exception {
        String GITHUB_ORG = Configuration.getConfig(ConfigDefinition.GITHUB_TEST_USER_ORG);
        CLIENT_COMPONENT_REPO_URL = "https://github.com/".concat(GITHUB_ORG).concat("/choreo-samples");
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        REQ_BODY = "{\n" +
                "    \"appointmentDate\": \"2024-11-08T10:00:00Z\",\n" +
                "    \"email\": \"test@example.com\",\n" +
                "    \"name\": \"%s\",\n" +
                "    \"phoneNumber\": \"1234567890\",\n" +
                "    \"service\": \"General Consultation\"\n" +
                "}";
    }

    @Test
    @CitrusTest
    public void createDatabaseServer_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        mysqlDatabaseServer = PlatformServicesUtils.createReusableDatabaseServer(this, httpClient, mysqlDatabaseServerName, Constant.MYSQL_SERVICE_PLAN_ID, orgUUID, accessToken);
        redisDatabaseServer = PlatformServicesUtils.createReusableDatabaseServer(this, httpClient, redisDatabaseServerName, Constant.REDIS_SERVICE_PLAN_ID, orgUUID, accessToken);
    }

    @Test(dependsOnMethods = {"createDatabaseServer_TestDatabaseConnections"})
    @CitrusTest
    public void createDatabase_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        PlatformServicesUtils.createReusableDatabase(this, httpClient, mysqlDatabaseServer.getId(), Arrays.asList(devDatabaseName, prodDatabaseName), orgUUID, accessToken);
    }

    @Test()
    @CitrusTest
    public void getEnvironmentIds_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        EnvironmentTemplatesListDTO environmentTemplatesListDTO = DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        environmentTemplates.forEach(environment -> {
            if (environment.getRegion().equals(Constant.region.EU.toString())) {
                if (environment.getEnvName().equals(Constant.Environment.Development.name())) {
                    devEnvironmentId = environment.getId().toString();
                }
                if (environment.getEnvName().equals(Constant.Environment.Production.name())) {
                    prodEnvironmentId = environment.getId().toString();
                }
            }
        });
    }

    @Test(dependsOnMethods = {"createDatabase_TestDatabaseConnections", "getEnvironmentIds_TestDatabaseConnections"})
    @CitrusTest
    public void createDatabaseCredentials_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        devDbCredential = PlatformServices.createDatabaseCredentials(this, httpClient, mysqlDatabaseServer.getId(), devDatabaseName, Arrays.asList(devEnvironmentId), accessToken);
        prodDbCredential = PlatformServices.createDatabaseCredentials(this, httpClient, mysqlDatabaseServer.getId(), prodDatabaseName, Arrays.asList(prodEnvironmentId), accessToken);
    }

    @Test(dependsOnMethods = {"createDatabaseCredentials_TestDatabaseConnections"})
    @CitrusTest
    public void addDatabaseToMarketplace_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        devDatabase = PlatformServices.UpdateDatabaseMarketplaceStatus(this, httpClient, mysqlDatabaseServer.getId(), devDatabaseName, true, accessToken);
        prodDatabase = PlatformServices.UpdateDatabaseMarketplaceStatus(this, httpClient, mysqlDatabaseServer.getId(), prodDatabaseName, true, accessToken);
        Assert.assertTrue(devDatabase.isDisplay_on_marketplace());
        Assert.assertTrue(prodDatabase.isDisplay_on_marketplace());
    }

    @Test
    @CitrusTest
    public void createConsumerProject_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        consumerProject = ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.EU.toString());
    }

    @Test(dependsOnMethods = {"createConsumerProject_TestDatabaseConnections"})
    @CitrusTest
    public void createConsumer_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);

        Repository repo = Repository.builder().repoUrl(CLIENT_COMPONENT_REPO_URL).buildContext(CLIENT_COMPONENT_DOCKET_CONTEXT).build();
        GraphqlDTO dto = ComponentUtils.createBuildpackComponentRequest(componentName, consumerProject, repo, Buildpack.NODEJS);
        clientChoreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.BUILDPACK);
    }

    @Test(dependsOnMethods = {"addDatabaseToMarketplace_TestDatabaseConnections", "createConsumer_TestDatabaseConnections"})
    @CitrusTest
    public void createDatabaseConnection_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        dbConnectingInDev = ConnectionService.FindDatabase(citrusClients, this, accessToken, mysqlDatabaseServer.getId(), devDatabaseName);
        dbConnectingInProd = ConnectionService.FindDatabase(citrusClients, this, accessToken, mysqlDatabaseServer.getId(), prodDatabaseName);
        String dbConnectingInDevResourceId = dbConnectingInDev.getResourceId();
        String dbConnectingInProdResourceId = dbConnectingInProd.getResourceId();

        ArrayList<Visibility> visibilities = new ArrayList<>();
        Visibility componentVisibility = Visibility.builder().organizationUuid(orgUUID).projectUuid(consumerProject.getId()).componentUuid(clientChoreoComponent.getId()).build();

        visibilities.add(componentVisibility);

        Map<String, DatabaseConnectionCreateRequest.ResourceReference> envMapping = new HashMap<>();
        envMapping.put(devEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(dbConnectingInDevResourceId).parameterReference(devDbCredential.getId()).build());
        envMapping.put(prodEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(dbConnectingInProdResourceId).parameterReference(prodDbCredential.getId()).build());

        DatabaseConnectionCreateRequest connectionCreateRequest = DatabaseConnectionCreateRequest.builder()
                                                                    .name(databaseConnectionName)
                                                                    .description("test database connection")
                                                                    .schemaReference(dbConnectingInDev.getConnectionSchemas().get(0).getId())
                                                                    .visibilities(visibilities.toArray(new Visibility[0]))
                                                                    .serviceId(dbConnectingInDevResourceId)
                                                                    .componentType("service")
                                                                    .envMapping(envMapping)
                                                                    .build();
        databaseConnection = ConnectionService.CreateChoreoDatabaseConnection(this, httpClient, accessToken, connectionCreateRequest);
    }

    @Test(dependsOnMethods = {"createDatabaseConnection_TestDatabaseConnections"})
    @CitrusTest
    public void consumeDatabaseConnectionWithComponentFileV11_TestDatabaseConnections() throws Exception {
        ConnectionService.UpdateSourceConfigurationFile(REPO_NAME, "main", databaseConnection.getName(), "database:".concat(mysqlDatabaseServerName).concat("/").concat(devDatabaseName),
                    SourceConfigurationFileTypes.COMPONENT_V1D1, CLIENT_COMPONENT_CHOREO_FOLDER_PATH);

        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        DatabaseServer dbServer = PlatformServices.getDatabaseServer(this,httpClient,mysqlDatabaseServer.getId(),orgUUID,accessToken);

        //deploy to dev environment
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, clientChoreoComponent);
        ComponentDeploymentStatusDTO clientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, clientChoreoComponent, environments, ComponentFlavour.BUILDPACK);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponent,clientDeploymentStatusDTO);
        // invoke in dev
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponent, clientDeploymentStatusDTO, environments);
        String requestBody = String.format(REQ_BODY, DEV_KEY_NAME);
        JsonObject createdAppointment = ComponentUtils.invokeApiPOST(this,invokeData.getRight().getApikey(),invokeData.getLeft(),"/appointments",requestBody,  HttpStatus.CREATED);
        JsonArray appointmentsList = ComponentUtils.invokeApiGETResponse(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/appointments");
        ConnectionService.ValidateDatabaseConnection(appointmentsList,createdAppointment,DEV_KEY_NAME);

        //promote to environments
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, clientChoreoComponent,
                environments, ComponentFlavour.BUILDPACK, consumerProject);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponent,
                statusDTO.get(0)); //consider only the first promotion
        //invoke in promoted environment
        invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponent, statusDTO.get(0), environments);
        requestBody = String.format(REQ_BODY, PROD_KEY_NAME);
        createdAppointment = ComponentUtils.invokeApiPOST(this,invokeData.getRight().getApikey(),invokeData.getLeft(),"/appointments",requestBody,  HttpStatus.CREATED);
        appointmentsList = ComponentUtils.invokeApiGETResponse(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/appointments");
        ConnectionService.ValidateDatabaseConnection(appointmentsList,createdAppointment,PROD_KEY_NAME);
    }

    @Test(dependsOnMethods = {"consumeDatabaseConnectionWithComponentFileV11_TestDatabaseConnections"},alwaysRun = true)
    @CitrusTest
    public void consumeDatabaseConnectionWithComponentFileV10_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
         ComponentUtils.createComponentVersion(this, citrusClients,
                accessToken, clientChoreoComponent, "v1.1", "component-yaml-v10");

        Optional<ChoreoComponent> clientChoreoComponentNewVer = ComponentUtils.getComponentByName(this, accessToken, citrusClients,consumerProject,clientChoreoComponent.getName());
        clientChoreoComponentNewVersion = clientChoreoComponentNewVer.get();
        ConnectionService.UpdateSourceConfigurationFile(REPO_NAME, "component-yaml-v10", databaseConnection.getGroupUuid(), "database:".concat(devDatabaseName),
                SourceConfigurationFileTypes.COMPONENT_V1D0, CLIENT_COMPONENT_CHOREO_FOLDER_PATH);

        DatabaseServer dbServer = PlatformServices.getDatabaseServer(this,httpClient,mysqlDatabaseServer.getId(),orgUUID,accessToken);

        //deploy to dev environment
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, clientChoreoComponentNewVersion);
        ComponentDeploymentStatusDTO clientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, clientChoreoComponentNewVersion, environments, ComponentFlavour.BUILDPACK);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponentNewVersion,clientDeploymentStatusDTO);
       // invoke in dev
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponentNewVersion, clientDeploymentStatusDTO, environments);
        String requestBody = String.format(REQ_BODY, DEV_KEY_NAME);
        JsonObject createdAppointment = ComponentUtils.invokeApiPOST(this,invokeData.getRight().getApikey(),invokeData.getLeft(),"/appointments",requestBody,  HttpStatus.CREATED);
        JsonArray appointmentsList = ComponentUtils.invokeApiGETResponse(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/appointments");
        ConnectionService.ValidateDatabaseConnection(appointmentsList,createdAppointment,DEV_KEY_NAME);

        //promote to environments
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, clientChoreoComponentNewVersion,
                environments, ComponentFlavour.BUILDPACK, consumerProject);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponentNewVersion,
                statusDTO.get(0)); //consider only the first promotion
        //invoke in promoted environment
        invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponentNewVersion, statusDTO.get(0), environments);
        requestBody = String.format(REQ_BODY, PROD_KEY_NAME);
        createdAppointment = ComponentUtils.invokeApiPOST(this,invokeData.getRight().getApikey(),invokeData.getLeft(),"/appointments",requestBody,  HttpStatus.CREATED);
        appointmentsList = ComponentUtils.invokeApiGETResponse(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/appointments");
        ConnectionService.ValidateDatabaseConnection(appointmentsList,createdAppointment,PROD_KEY_NAME);
    }
    @Test(dependsOnMethods = {"consumeDatabaseConnectionWithComponentFileV10_TestDatabaseConnections"},alwaysRun = true)
    @CitrusTest
    public void consumeDatabaseConnectionWithComponentConfigFile_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        ComponentUtils.createComponentVersion(this, citrusClients, accessToken, clientChoreoComponent, "v1.2", "component-config-yaml");
        Optional<ChoreoComponent> clientChoreoComponentNewVer = ComponentUtils.getComponentByName(this, accessToken, citrusClients,consumerProject,clientChoreoComponent.getName());
        clientChoreoComponentNewVersion = clientChoreoComponentNewVer.get();

        ConnectionService.UpdateSourceConfigurationFile(REPO_NAME, "component-config-yaml", databaseConnection.getGroupUuid(), "database:".concat(devDatabaseName),
                    SourceConfigurationFileTypes.COMPONENT_CONFIG, CLIENT_COMPONENT_CHOREO_FOLDER_PATH);

        DatabaseServer dbServer = PlatformServices.getDatabaseServer(this,httpClient,mysqlDatabaseServer.getId(),orgUUID,accessToken);

        //deploy to dev environment
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, clientChoreoComponentNewVersion);
        ComponentDeploymentStatusDTO clientDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, clientChoreoComponentNewVersion, environments, ComponentFlavour.BUILDPACK);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponentNewVersion,clientDeploymentStatusDTO);
        // invoke in dev
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponentNewVersion, clientDeploymentStatusDTO, environments);
        String requestBody = String.format(REQ_BODY, DEV_KEY_NAME);
        JsonObject createdAppointment = ComponentUtils.invokeApiPOST(this,invokeData.getRight().getApikey(),invokeData.getLeft(),"/appointments",requestBody,  HttpStatus.CREATED);
        JsonArray appointmentsList = ComponentUtils.invokeApiGETResponse(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/appointments");
        ConnectionService.ValidateDatabaseConnection(appointmentsList,createdAppointment, DEV_KEY_NAME);

        //promote to environments
        List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, clientChoreoComponentNewVersion,
                environments, ComponentFlavour.BUILDPACK, consumerProject);
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, clientChoreoComponentNewVersion,
                statusDTO.get(0)); //consider only the first promotion
        //invoke in promoted environment
        invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                clientChoreoComponentNewVersion, statusDTO.get(0), environments);
        requestBody = String.format(REQ_BODY, PROD_KEY_NAME);
        createdAppointment = ComponentUtils.invokeApiPOST(this,invokeData.getRight().getApikey(),invokeData.getLeft(),"/appointments",requestBody,  HttpStatus.CREATED);
        appointmentsList = ComponentUtils.invokeApiGETResponse(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/appointments");
        ConnectionService.ValidateDatabaseConnection(appointmentsList,createdAppointment,PROD_KEY_NAME);
    }

    @Test(dependsOnMethods = {"createDatabaseServer_TestDatabaseConnections", "getEnvironmentIds_TestDatabaseConnections"})
    @CitrusTest
    public void addDatabaseServerToMarketplace_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Boolean isValidPowerState = PlatformServicesUtils.validateDbServerPowerStatus(this, httpClient, redisDatabaseServer.getId(), orgUUID, accessToken);
        if (!isValidPowerState) {
            throw new ValidationException("Database server is not in active state");
        }
        redisDbServerCredentials = PlatformServices.createDatabaseCredentials(this, httpClient, redisDatabaseServer.getId(), redisDatabaseServer.getName(), Arrays.asList(devEnvironmentId, prodEnvironmentId), accessToken);
        redisDatabaseServer = PlatformServices.UpdateDatabaseServerMarketplaceStatus(this, httpClient, redisDatabaseServer.getId(), redisDatabaseServer.getName(), true, accessToken);
        Assert.assertTrue(redisDatabaseServer.getDisplay_on_marketplace());
    }

    @Test(dependsOnMethods = {"createConsumerProject_TestDatabaseConnections", "addDatabaseServerToMarketplace_TestDatabaseConnections"})
    @CitrusTest
    public void createDatabaseConnectionToAServer_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        CommonResource connectingDbServer = ConnectionService.FindDatabase(citrusClients, this, accessToken, redisDatabaseServer.getId(), redisDatabaseServer.getName());
        String connectingDbServerResourceId = connectingDbServer.getResourceId();

        ArrayList<Visibility> visibilities = new ArrayList<>();
        Visibility componentVisibility = Visibility.builder().organizationUuid(orgUUID).projectUuid(consumerProject.getId()).build();

        visibilities.add(componentVisibility);
        Map<String, DatabaseConnectionCreateRequest.ResourceReference> envMapping = new HashMap<>();
        String resourceId = connectingDbServer.getResourceId();
        envMapping.put(devEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(resourceId).parameterReference(redisDbServerCredentials.getId()).build());
        envMapping.put(prodEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(resourceId).parameterReference(redisDbServerCredentials.getId()).build());
        DatabaseConnectionCreateRequest connectionCreateRequest = DatabaseConnectionCreateRequest.builder()
                                                                    .name(databaseServerConnectionName)
                                                                    .description("project level database connection")
                                                                    .schemaReference(connectingDbServer.getConnectionSchemas().get(0).getId())
                                                                    .visibilities(visibilities.toArray(new Visibility[0]))
                                                                    .serviceId(connectingDbServerResourceId)
                                                                    .envMapping(envMapping)
                                                                    .build();
        ConnectionService.CreateChoreoDatabaseConnection(this, httpClient, accessToken, connectionCreateRequest);
    }

    @Test(dependsOnMethods = {"consumeDatabaseConnectionWithComponentConfigFile_TestDatabaseConnections","consumeDatabaseConnectionWithComponentFileV10_TestDatabaseConnections",
                             "consumeDatabaseConnectionWithComponentFileV11_TestDatabaseConnections", "createDatabaseConnectionToAServer_TestDatabaseConnections"})
    @CitrusTest
    public void removeDatabaseFromMarketplace_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        devDatabase = PlatformServices.UpdateDatabaseMarketplaceStatus(this, httpClient, mysqlDatabaseServer.getId(), devDatabaseName, false, accessToken);
        prodDatabase = PlatformServices.UpdateDatabaseMarketplaceStatus(this, httpClient, mysqlDatabaseServer.getId(), prodDatabaseName, false, accessToken);
        redisDatabaseServer = PlatformServices.UpdateDatabaseServerMarketplaceStatus(this, httpClient, redisDatabaseServer.getId(), redisDatabaseServer.getName(), false, accessToken);
        Assert.assertFalse(devDatabase.isDisplay_on_marketplace());
        Assert.assertFalse(prodDatabase.isDisplay_on_marketplace());
        Assert.assertFalse(redisDatabaseServer.getDisplay_on_marketplace());

    }

    @Test(dependsOnMethods = {"removeDatabaseFromMarketplace_TestDatabaseConnections"})
    @CitrusTest
    public void deleteCredentials_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        if (devDbCredential != null) {
            PlatformServices.DeleteDatabaseCredentials(this, httpClient, mysqlDatabaseServer.getId(), devDbCredential.getId(), accessToken);
        }
        if (prodDbCredential != null) {
            PlatformServices.DeleteDatabaseCredentials(this, httpClient, mysqlDatabaseServer.getId(), prodDbCredential.getId(), accessToken);
        }
        if (redisDbServerCredentials != null) {
            PlatformServices.DeleteDatabaseCredentials(this, httpClient, redisDatabaseServer.getId(), redisDbServerCredentials.getId(), accessToken);
        }
    }

    @AfterClass
    public void powerOffDatabaseServer() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        if (mysqlDatabaseServer != null) {
            PlatformServices.UpdateDatabaseServerPowerStatus(this, httpClient, mysqlDatabaseServer.getId(), DBServerPowerAction.OFF, accessToken);
        }
        if (redisDatabaseServer != null) {
            PlatformServices.UpdateDatabaseServerPowerStatus(this, httpClient, redisDatabaseServer.getId(), DBServerPowerAction.OFF, accessToken);
        }
    }


}
