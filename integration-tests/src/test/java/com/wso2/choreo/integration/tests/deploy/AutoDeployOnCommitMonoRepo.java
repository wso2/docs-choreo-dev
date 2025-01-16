/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package com.wso2.choreo.integration.tests.deploy;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.component.Component;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.response.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $(http()
 * tests related to component deployment using auto deploy on commit trigger on
 */
public class AutoDeployOnCommitMonoRepo extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private final String repoName = "mono-repo";
    private static ChoreoComponent choreoComponentA;
    private static ChoreoComponent choreoComponentB;
    private ChoreoProject project;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_AutoDeployOnCommit()
            throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void createProject_AutoDeployOnCommit() throws Exception {
        project =  ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.US.toString());
    }

    @Test(dependsOnMethods = {"createProject_AutoDeployOnCommit"})
    @CitrusTest
    public void createUserManagedComponentA_AutoDeployOnCommit() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/mono-repo").branch("main").subPath("serviceA").build();
        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);
        choreoComponentA = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        dto.setComponentId(choreoComponentA.getId());
        dto.setLatestVersionId(choreoComponentA.getLatestApiVersion().getId());
        String runId = GraphQL.getRunId(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, dto);
        Component.waitForComponentBuildDeployComplete(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, project.getId(), choreoComponentA.getId(), runId, 50);
        Assert.assertNotNull(choreoComponentA.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentA_AutoDeployOnCommit"})
    @CitrusTest
    public void createUserManagedComponentB_AutoDeployOnCommit() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/mono-repo").branch("main").subPath("serviceB").build();
        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);
        choreoComponentB = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        dto.setComponentId(choreoComponentB.getId());
        dto.setLatestVersionId(choreoComponentB.getLatestApiVersion().getId());
        String runId = GraphQL.getRunId(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, dto);
        Component.waitForComponentBuildDeployComplete(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, project.getId(), choreoComponentB.getId(), runId, 50);
        Assert.assertNotNull(choreoComponentB.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentB_AutoDeployOnCommit"})
    @CitrusTest
    public void enableAutoBuildComponentA_AutoDeployOnCommit() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponentA);

        GraphqlDTO dto = GraphqlDTO.builder().componentId(choreoComponentA.getId()).environmentId(environments.get(0).getId()).versionId(choreoComponentA.getLatestApiVersion().getId()).build();

        GraphQL.enableAutoBuild(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken,
                dto);
    }

    @Test(dependsOnMethods = {"enableAutoBuildComponentA_AutoDeployOnCommit"})
    @CitrusTest
    public void enableAutoBuildComponentB_AutoDeployOnCommit() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponentB);

        GraphqlDTO dto = GraphqlDTO.builder().componentId(choreoComponentB.getId()).environmentId(environments.get(0).getId()).versionId(choreoComponentB.getLatestApiVersion().getId()).build();

        GraphQL.enableAutoBuild(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken,
                dto);
    }

    @Test(dependsOnMethods = {"enableAutoBuildComponentB_AutoDeployOnCommit"})
    @CitrusTest
    public void enableAutoDeployComponentA_AutoDeployOnCommit() throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(choreoComponentA.getId()).versionId(choreoComponentA.getLatestApiVersion().getId()).build();
        GraphQL.enableAutoDeploy(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken,
                dto);
    }

    @Test(dependsOnMethods = {"enableAutoDeployComponentA_AutoDeployOnCommit"})
    @CitrusTest
    public void enableAutoDeployComponentB_AutoDeployOnCommit() throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(choreoComponentB.getId()).versionId(choreoComponentB.getLatestApiVersion().getId()).build();
        GraphQL.enableAutoDeploy(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken,
                dto);
    }

    @Test(dependsOnMethods = {"enableAutoDeployComponentB_AutoDeployOnCommit"})
    @CitrusTest
    public void mergeNewCodeComponentA_AutoDeployOnCommit() throws IOException {
        String timeStamp = String.valueOf(new Date().getTime());
        Map<String, String> params = new HashMap<>();
        params.put("timeStamp", timeStamp);
        String srcCode = MessageUtils.generateStringFromTemplate(
                "templates/autodeploy/service.mustache", params);
        String encodedCode = Base64.getEncoder().encodeToString(srcCode.getBytes(StandardCharsets.UTF_8));
        GitHub.mergeNewCode(repoName, "serviceA/service.bal", " change on DeployIT ", encodedCode, null);
        // add delay to trigger auto deploy
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(dependsOnMethods = {"mergeNewCodeComponentA_AutoDeployOnCommit"})
    @CitrusTest
    public void deploymentStatusByVersionComponentA_AutoDeployOnCommit() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponentA);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, choreoComponentA);
        ComponentUtils.validateComponentDeployment(this, citrusClients, accessToken, choreoComponentA,
                latestCommit, environments);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersionComponentA_AutoDeployOnCommit"})
    @CitrusTest
    public void deploymentStatusByVersionComponentB_AutoDeployOnCommit() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponentB);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, choreoComponentB);
        ComponentDeploymentStatusDTO status = ComponentUtils.validateComponentDeployment(this, citrusClients, accessToken, choreoComponentB,
                latestCommit, environments, true);
        // Auto build to component A should not trigger the build for component B
        Assert.assertNull(status);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersionComponentB_AutoDeployOnCommit"})
    @CitrusTest
    public void deleteComponentB_AutoDeployOnCommit() throws IOException {
        Response res = GraphQL.deleteComponent(choreoComponentA.getId(), project.getId(), accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"deleteComponentB_AutoDeployOnCommit"})
    @CitrusTest
    public void deleteComponentA_AutoDeployOnCommit() throws IOException {
        Response res = GraphQL.deleteComponent(choreoComponentB.getId(), project.getId(), accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }
}
