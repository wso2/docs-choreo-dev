package com.wso2.choreo.integration.tests.connectorbuilder;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.apis.connectors.ConnectorPublisher;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.ComponentFlavour;
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
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.connectors.Connector;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Connector publishing related tests
 */
public class ConnectorBuilderIT extends TestNGCitrusSpringSupport {

    private static String accessToken;
    private ChoreoComponent choreoComponent;
    private String projectId;
    private ChoreoProject project;
    private String orgHandle;
    private String orgUUID;
    private static String revisionId;
    private String githubOrg;
    private String githubPAT;
    private String devInvokeURL;
    private List<Environment> environments;

    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    ChoreoOrganization org;
    Connector connector;

    @BeforeClass
    public void setup_ConnectorBuilderIT() throws Exception {

        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        project = GraphQL.createProject(accessToken);
        projectId = project.getId();
        org = new ChoreoOrganization(orgHandle, orgId, orgUUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
        githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
        orgHandle=Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);

    }

    @Test
    @CitrusTest
    public void createComponent_ConnectorBuilderIT() throws Exception, ApiLifecycleChangeException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/rest-api").branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);

        //Deploying component
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, environments, ComponentFlavour.STANDARD);
        devInvokeURL = statusDTO.getInvokeUrl();

        //change the API lifecycle
        choreoComponent.getLatestApiVersion().changeApiLifeCycle(accessToken, org.getOrgUUID(), Constant.apiLIifCycleState.Publish);
        JsonArray revisions = choreoComponent.getRevisions(accessToken, choreoComponent.getLatestApiVersion().getProxyId(), orgUUID);
        JsonObject revision = (JsonObject) revisions.get(0);
        revisionId = revision.get("id").getAsString();
    }

    @Test(dependsOnMethods = "createComponent_ConnectorBuilderIT")
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
