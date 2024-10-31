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
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
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
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplate;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplatesListDTO;
import com.wso2.choreo.integration.models.marketplace.CommonResource;
import com.wso2.choreo.integration.models.marketplace.DatabaseConnectionCreateRequest;
import com.wso2.choreo.integration.models.marketplace.Visibility;
import com.wso2.choreo.integration.models.platformServices.DBServerPowerAction;
import com.wso2.choreo.integration.models.platformServices.Database;
import com.wso2.choreo.integration.models.platformServices.DatabaseCredentials;
import com.wso2.choreo.integration.models.platformServices.DatabaseServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Tests related to Choreo database connections creation and use.
 */
public class DatabaseConnections extends TestNGCitrusSpringSupport {
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    HttpClient httpClient;
    private String orgUUID;
    private int orgId;
    private static DatabaseServer mysqlDatabaseServer;
    private static DatabaseServer redisDatabaseServer;

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
    private  CommonResource dbConnectingInDev;
    private CommonResource dbConnectingInProd;
    private ChoreoProject consumerProject;
    private ChoreoComponent clientChoreoComponent;
    private static final String CLIENT_COMPONENT_REPO_URL = "https://github.com/hanzjk/choreo-tutorial";
    private static  final String CLIENT_COMPONENT_DOCKET_CONTEXT = "appointment-management/appointment-service/";
    private static final String OAS_FILE_PATH = "openapi.yaml";

    @BeforeClass
    public void setup_TestDatabaseConnectionsTestCase() throws Exception {
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        httpClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
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
        PlatformServicesUtils.createReusableDatabase(this, httpClient, mysqlDatabaseServer.getId(), Arrays.asList(devDatabaseName, prodDatabaseName), orgUUID,accessToken);
    }

    @Test()
    @CitrusTest
    public void getEnvironmentIds_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        EnvironmentTemplatesListDTO environmentTemplatesListDTO = DevopsPortalApi.getEnvironmentTemplates(this,accessToken,orgId);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        environmentTemplates. forEach(environment -> {
            if (environment.getRegion().equals(Constant.region.EU.toString())){
                if (environment.getEnvName().equals(Constant.Environment.Development.name())) {
                    devEnvironmentId = environment.getId().toString();
                }
                if (environment.getEnvName().equals(Constant.Environment.Production.name())) {
                    prodEnvironmentId = environment.getId().toString();
                }
            }
        });
    }
    @Test(dependsOnMethods = {"createDatabase_TestDatabaseConnections","getEnvironmentIds_TestDatabaseConnections"})
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
        consumerProject = ComponentUtils.createProject(this, citrusClients, accessToken,
                Constant.region.EU.toString());
    }

    @Test(dependsOnMethods = {"createConsumerProject_TestDatabaseConnections"})
    @CitrusTest
    public void createConsumer_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);

        Repository repo = Repository.builder().
                repoUrl(CLIENT_COMPONENT_REPO_URL).
                oasFilePath(CLIENT_COMPONENT_DOCKET_CONTEXT.concat(OAS_FILE_PATH)).
                buildContext(CLIENT_COMPONENT_DOCKET_CONTEXT).build();
        GraphqlDTO dto = ComponentUtils.createBuildpackComponentRequest(componentName, consumerProject,repo, Buildpack.NODEJS);
        clientChoreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                dto,  ComponentFlavour.BUILDPACK);
    }

    @Test(dependsOnMethods = {"addDatabaseToMarketplace_TestDatabaseConnections","createConsumer_TestDatabaseConnections"})
    @CitrusTest
    public void createDatabaseConnection_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        dbConnectingInDev = ConnectionService.FindDatabase(citrusClients, this, accessToken, mysqlDatabaseServer.getId(), devDatabaseName);
        dbConnectingInProd = ConnectionService.FindDatabase(citrusClients, this, accessToken, mysqlDatabaseServer.getId(), prodDatabaseName);
        String dbConnectingInDevResourceId = dbConnectingInDev.getResourceId();
        String dbConnectingInProdResourceId = dbConnectingInProd.getResourceId();

        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ArrayList<Visibility> visibilities = new ArrayList<>();
        Visibility componentVisibility = Visibility.builder()
                            .organizationUuid(orgUUID)
                            .projectUuid(consumerProject.getId())
                            .componentUuid(clientChoreoComponent.getId())
                            .build();

        visibilities.add(componentVisibility);

        Map<String, DatabaseConnectionCreateRequest.ResourceReference> envMapping = new HashMap<>();
        envMapping.put(devEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(dbConnectingInDevResourceId).parameterReference(devDbCredential.getId()).build());
        envMapping.put(prodEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(dbConnectingInProdResourceId).parameterReference(prodDbCredential.getId()).build());

        DatabaseConnectionCreateRequest connectionCreateRequest = DatabaseConnectionCreateRequest.builder()
                .name(connectionName)
                .description("test database connection")
                .schemaReference(dbConnectingInDev.getConnectionSchemas().get(0).getId())
                .visibilities(visibilities.toArray(new Visibility[0]))
                .serviceId(dbConnectingInDevResourceId)
                .componentType("service")                .envMapping(envMapping)
                .build();
        ConnectionService.CreateChoreoDatabaseConnection(this, httpClient, accessToken, connectionCreateRequest);
    }

    @Test(dependsOnMethods = {"createDatabaseServer_TestDatabaseConnections","getEnvironmentIds_TestDatabaseConnections"})
    @CitrusTest
    public void addDatabaseServerToMarketplace_TestDatabaseConnections() throws  Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        Boolean isValidPowerState = PlatformServicesUtils.validateDbServerPowerStatus(this,httpClient,redisDatabaseServer.getId(),orgUUID,accessToken);
        if (!isValidPowerState) {
            throw  new ValidationException("Database server is not in active state");
        }
        redisDbServerCredentials = PlatformServices.createDatabaseCredentials(this, httpClient, redisDatabaseServer.getId(), redisDatabaseServer.getName(), Arrays.asList(devEnvironmentId,prodEnvironmentId), accessToken);
        redisDatabaseServer = PlatformServices.UpdateDatabaseServerMarketplaceStatus(this, httpClient, redisDatabaseServer.getId(), redisDatabaseServer.getName(), true, accessToken);
        Assert.assertTrue(redisDatabaseServer.getDisplay_on_marketplace());
    }

    @Test(dependsOnMethods = {"createConsumerProject_TestDatabaseConnections","addDatabaseServerToMarketplace_TestDatabaseConnections"})
    @CitrusTest
    public void createDatabaseConnectionToAServer_TestDatabaseConnections() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        CommonResource connectingDbServer = ConnectionService.FindDatabase(citrusClients, this, accessToken, redisDatabaseServer.getId(), redisDatabaseServer.getName());
        String connectingDbServerResourceId = connectingDbServer.getResourceId();

        String connectionName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_CONNECTION_NAME);
        ArrayList<Visibility> visibilities = new ArrayList<>();
        Visibility componentVisibility = Visibility.builder()
                .organizationUuid(orgUUID)
                .projectUuid(consumerProject.getId())
                .build();

        visibilities.add(componentVisibility);
        Map<String, DatabaseConnectionCreateRequest.ResourceReference> envMapping = new HashMap<>();
        String resourceId = connectingDbServer.getResourceId();
        envMapping.put(devEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(resourceId).parameterReference(redisDbServerCredentials.getId()).build());
        envMapping.put(prodEnvironmentId, DatabaseConnectionCreateRequest.ResourceReference.builder().resourceId(resourceId).parameterReference(redisDbServerCredentials.getId()).build());
        DatabaseConnectionCreateRequest connectionCreateRequest = DatabaseConnectionCreateRequest.builder()
                .name(connectionName)
                .description("project level database connection")
                .schemaReference(connectingDbServer.getConnectionSchemas().get(0).getId())
                .visibilities(visibilities.toArray(new Visibility[0]))
                .serviceId(connectingDbServerResourceId)
                .envMapping(envMapping)
                .build();
        ConnectionService.CreateChoreoDatabaseConnection(this, httpClient, accessToken, connectionCreateRequest);
    }

    @Test(dependsOnMethods = {"createDatabaseConnection_TestDatabaseConnections","createDatabaseConnectionToAServer_TestDatabaseConnections"})
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
        if (prodDbCredential != null){
            PlatformServices.DeleteDatabaseCredentials(this, httpClient, mysqlDatabaseServer.getId(), prodDbCredential.getId(), accessToken);
        }
        if (redisDbServerCredentials != null){
            PlatformServices.DeleteDatabaseCredentials(this, httpClient, redisDatabaseServer.getId(), redisDbServerCredentials.getId(), accessToken);
        }
    }

    @AfterClass
    public void powerOffDatabaseServer() throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        if (mysqlDatabaseServer != null){
            PlatformServices.UpdateDatabaseServerPowerStatus(this,httpClient,mysqlDatabaseServer.getId(), DBServerPowerAction.OFF,accessToken);
        }
        if (redisDatabaseServer != null){
            PlatformServices.UpdateDatabaseServerPowerStatus(this,httpClient,redisDatabaseServer.getId(), DBServerPowerAction.OFF,accessToken);
        }
    }


}
