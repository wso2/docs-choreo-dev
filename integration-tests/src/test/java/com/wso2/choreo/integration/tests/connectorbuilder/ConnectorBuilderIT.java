package com.wso2.choreo.integration.tests.connectorbuilder;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.connectors.ConnectorPublisher;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ApiLifecycleChangeException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.connectors.Connector;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.proxyapi.ProxyDeployment;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.tests.dp.DataProviderWrapper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConnectorBuilderIT extends TestNGCitrusSpringSupport {
    private String accessToken;
    private ChoreoComponent choreoComponent;
    private ChoreoProject choreoProject;
    private String apiName;
    private ProxyAPIBuild proxyAPIBuild;
    private ProxyAPI proxyAPI;
    List<Environment> environments;
    private List<ProxyDeployment> proxyDeployments;
    private String revisionId;
    private String orgUUID;
    private ChoreoOrganization org;
    private String orgHandle;
    private Connector connector;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup_ConnectorBuilderIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    }

    @Test()
    @CitrusTest
    public void createProject_ConnectorBuilderIT() throws Exception {
        apiName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        choreoProject = ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.US.toString());
    }

    @Test(dependsOnMethods = {"createProject_ConnectorBuilderIT"})
    @CitrusTest
    public void testCreateComponentForProxyAPI_ConnectorBuilderIT() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Pair<ChoreoComponent, ProxyAPI> componentCreationDetail = ComponentUtils.createProxyComponent(this, citrusClients,
                accessToken, componentName, apiName, choreoProject);
        choreoComponent = componentCreationDetail.getLeft();
        proxyAPI = componentCreationDetail.getRight();
    }

    @Test(dependsOnMethods = { "testCreateComponentForProxyAPI_ConnectorBuilderIT" })
    @CitrusTest
    public void testUpdateSwagger_ConnectorBuilderIT() throws IOException {
        String swaggerFileName = "templates/graphql/requests/proxyAPIUpdateRequestWithMethodRateLimit.mustache";
        Response response = APICreator.updateAPIWithSwaggerFile(proxyAPI, swaggerFileName, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = { "testUpdateSwagger_ConnectorBuilderIT" })
    @CitrusTest
    public void getDeploymentEnvironment_ConnectorBuilderIT() throws Exception {
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
    }

    @Test(dependsOnMethods = { "getDeploymentEnvironment_ConnectorBuilderIT" })
    @CitrusTest
    public void deployProxyAPI_ConnectorBuilderIT() throws Exception {
        proxyAPIBuild = ComponentUtils.deployProxyComponent(this, citrusClients, accessToken, choreoComponent,
                environments);
    }

    @Test(dependsOnMethods = { "deployProxyAPI_ConnectorBuilderIT" })
    @CitrusTest
    public void componentDevDeploymentStatus_ConnectorBuilderIT()
            throws Exception, ApiLifecycleChangeException {
        proxyDeployments = ComponentUtils.getProxyDeployments(this, citrusClients, accessToken, choreoComponent,
                environments);

        choreoComponent.getLatestApiVersion().changeApiLifeCycle(accessToken, org.getOrgUUID(),
                Constant.apiLIifCycleState.Publish);
        JsonArray revisions = choreoComponent.getRevisions(accessToken, choreoComponent.getLatestApiVersion()
                .getProxyId(), orgUUID);
        JsonObject revision = (JsonObject) revisions.get(0);
        revisionId = revision.get("id").getAsString();
    }

    @Test(dependsOnMethods = { "componentDevDeploymentStatus_ConnectorBuilderIT" })
    @CitrusTest
    public void promoteProxyAPI_ConnectorBuilderIT() throws Exception {
        ComponentUtils.promoteProxyComponent(this, citrusClients, accessToken, choreoComponent, environments,
                proxyAPIBuild);
    }

    @Test(dependsOnMethods = "promoteProxyAPI_ConnectorBuilderIT")
    @CitrusTest
    public void publishConnector_ConnectorBuilderIT() throws Exception {
        connector = Connector.builder().version(Constant.TEST_CONNECTOR_VERSION)
                .visibility(Constant.TEST_CONNECTOR_VISIBILITY).apiId(revisionId).orgUuid(orgUUID)
                .orgHandler(orgHandle).organizationId(orgUUID).componentId(choreoComponent.getId()).build();
        ConnectorPublisher.publishConnector(this, choreoTestClient, accessToken, connector, false);
    }

    @Test(dependsOnMethods = {"publishConnector_ConnectorBuilderIT"}, enabled = true)
    @CitrusTest
    public void getConnectorStatus_ConnectorBuilderIT() throws InterruptedException, IOException {
        ConnectorPublisher.getConnectorStatus(this, choreoTestClient, accessToken, connector);
    }

    @Test(dependsOnMethods = {"getConnectorStatus_ConnectorBuilderIT"}, enabled = true)
    @CitrusTest
    public void getConnector_ConnectorBuilderIT() throws IOException {
        ConnectorPublisher.getConnector(this, choreoTestClient, accessToken, connector);
    }

    @Test(dependsOnMethods = {"getConnector_ConnectorBuilderIT"}, enabled = true)
    @CitrusTest
    public void republishConnector_ConnectorBuilderIT() throws Exception {
        ConnectorPublisher.publishConnector(this, choreoTestClient, accessToken, connector, true);
    }
}
