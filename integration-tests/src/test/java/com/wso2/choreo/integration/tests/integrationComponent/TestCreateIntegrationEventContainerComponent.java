/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.integrationComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.graphql.CreateByocComponentResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

public class TestCreateIntegrationEventContainerComponent extends TestNGCitrusSpringSupport {

    public static final String CONTAINERIZED_EVENT_HANDLER = "byocEventHandler";
    public static final String WS_URL = "wss://ws.postman-echo.com/raw/";

    private static String accessToken;
    private String orgHandle;
    private String orgId;
    private String orgUUID;
    private String projectId;
    private static String componentHandler;
    private String githubOrg;

    private final Map<String, String> varMap = new HashMap<>();

    private static ChoreoComponent testComponent;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ComponentDeploymentStatusDTO componentDeploymentStatusDTO;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void setup_TestCreateIntegrationEventContainerComponent()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);

        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void createProject_TestCreateIntegrationEventContainerComponent() throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, 
            Constant.region.US.toString());
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"createProject_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void createComponent_TestCreateIntegrationEventContainerComponent() throws Exception {

        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        final String repoName = "ipaas-containerized-event-listener";
        final String repoBranch = "main";
        String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/").concat(repoName);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().name(componentName.toLowerCase()).orgId(Integer.parseInt(orgId)).
                orgHandler(orgHandle).displayName(componentName).componentType(CONTAINERIZED_EVENT_HANDLER).
                projectId(projectId).srcGitRepoUrl(srcGitHubURL).repositorySubPath("").
                repositoryBranch(repoBranch).dockerfilePath("Dockerfile").build();
        Optional<CreateByocComponentResponseDTO> byocComponent = GraphQL.createBYOCComponent(this,
                choreoProjectsTestClient, graphqlDTO, accessToken);
        componentHandler = byocComponent.get().getHandle();
    }

    @Test(dependsOnMethods = {"createComponent_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentRetrieval_TestCreateIntegrationEventContainerComponent() throws Exception {

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(projectId).componentHandler(componentHandler).build();
        testComponent = GraphQL.retrieveComponent(this, choreoProjectsTestClient, accessToken,
                graphqlDTO);
    }

    @Test(dependsOnMethods = {"componentRetrieval_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void createEnvVariables_TestCreateIntegrationEventContainerComponent() throws Exception {

        varMap.put("WS_URL", WS_URL);
        String devEnvId = testComponent.getLatestAppEnvId(Constant.DEV_ENVIRONMENT);
        String devReleaseId = testComponent.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        devEnvId, devReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("WS_URL"), varMap.get("WS_URL"));
    }

    @Test(dependsOnMethods = {"createEnvVariables_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentDeployment_TestCreateIntegrationEventContainerComponent() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                testComponent);
        componentDeploymentStatusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, testComponent, 
            environments, ComponentFlavour.CONTAINERIZED_EVENT_HANDLER);
    }

    @Test(dependsOnMethods = {"componentDeployment_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void createEnvVariablesInProd_TestCreateIntegrationEventContainerComponent() throws Exception {
        varMap.put("WS_URL", WS_URL);
        String prodEnvId = testComponent.getLatestAppEnvId(Constant.PROD_ENVIRONMENT);
        String prodReleaseId = testComponent.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT);

        Map<String, String> createdEnvVariables =
                DevopsPortalApi.createUpdateEnvVariables(this, accessToken, varMap, testComponent.getId(),
                        prodEnvId, prodReleaseId, orgUUID, projectId);
        Assert.assertEquals(createdEnvVariables.size(), varMap.size());
        Assert.assertEquals(createdEnvVariables.get("WS_URL"), varMap.get("WS_URL"));
    }

    @Test(dependsOnMethods = {"createEnvVariablesInProd_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void componentPromotionToProd_TestCreateIntegrationEventContainerComponent() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken,
                testComponent);
        ComponentUtils.promoteComponent(this, citrusClients, accessToken, testComponent, environments,
                ComponentFlavour.CONTAINERIZED_EVENT_HANDLER);
    }

    @Test(dependsOnMethods = {"componentPromotionToProd_TestCreateIntegrationEventContainerComponent"})
    @CitrusTest
    public void undeployComponentDev_TestCreateIntegrationEventContainerComponent() throws Exception {
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(testComponent.getId()).orgHandler(orgHandle)
                .componentType(CONTAINERIZED_EVENT_HANDLER).releaseId(componentDeploymentStatusDTO.getReleaseId()).build();
        GraphQL.stopDeployment(this, choreoProjectsTestClient, accessToken, graphqlDTO);
    }
}
