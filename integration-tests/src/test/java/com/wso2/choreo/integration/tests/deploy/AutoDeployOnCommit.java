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
package com.wso2.choreo.integration.tests.deploy;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
public class AutoDeployOnCommit extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private final String repoName = "empty-repo";
    private static ChoreoComponent choreoComponent;
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
    public void createUserManagedComponent_AutoDeployOnCommit() throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/empty-repo").branch("main").subPath("").build();
        GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        dto.setComponentId(choreoComponent.getId());
        dto.setLatestVersionId(choreoComponent.getLatestApiVersion().getId());
        String runId = GraphQL.getRunId(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, dto);
        Component.waitForComponentBuildDeployComplete(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken, project.getId(), choreoComponent.getId(), runId, 50);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponent_AutoDeployOnCommit"})
    @CitrusTest
    public void enableAutoBuild_AutoDeployOnCommit() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);

        GraphqlDTO dto = GraphqlDTO.builder().componentId(choreoComponent.getId()).environmentId(environments.get(0).getId()).versionId(choreoComponent.getLatestApiVersion().getId()).build();

        GraphQL.enableAutoBuild(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken,
                dto);
    }

    @Test(dependsOnMethods = {"enableAutoBuild_AutoDeployOnCommit"})
    @CitrusTest
    public void enableAutoDeploy_AutoDeployOnCommit() throws Exception {
        GraphqlDTO dto = GraphqlDTO.builder().componentId(choreoComponent.getId()).versionId(choreoComponent.getLatestApiVersion().getId()).build();
        GraphQL.enableAutoDeploy(this, citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT), accessToken,
                dto);
    }

    @Test(dependsOnMethods = {"enableAutoDeploy_AutoDeployOnCommit"})
    @CitrusTest
    public void mergeNewCode_AutoDeployOnCommit() throws IOException {
        String timeStamp = String.valueOf(new Date().getTime());
        Map<String, String> params = new HashMap<>();
        params.put("timeStamp", timeStamp);
        String srcCode = MessageUtils.generateStringFromTemplate(
                "templates/autodeploy/service.mustache", params);
        String encodedCode = Base64.getEncoder().encodeToString(srcCode.getBytes(StandardCharsets.UTF_8));
        GitHub.mergeNewCode(repoName, "service.bal", " change on DeployIT ", encodedCode);
    }

    @Test(dependsOnMethods = {"mergeNewCode_AutoDeployOnCommit"})
    @CitrusTest
    public void deploymentStatusByVersion_AutoDeployOnCommit() throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, choreoComponent);
        ComponentUtils.validateComponentDeployment(this, citrusClients, accessToken, choreoComponent,
                latestCommit, environments);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersion_AutoDeployOnCommit"})
    @CitrusTest
    public void deleteComponent_AutoDeployOnCommit() throws IOException {
        Response res = GraphQL.deleteComponent(choreoComponent.getId(), project.getId(), accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }
}
