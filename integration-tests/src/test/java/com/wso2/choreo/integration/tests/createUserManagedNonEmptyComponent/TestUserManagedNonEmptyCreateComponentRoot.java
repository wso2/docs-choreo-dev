package com.wso2.choreo.integration.tests.createUserManagedNonEmptyComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * $(http()
 * tests related to component creation from user managed non empty repo root.
 */
public class TestUserManagedNonEmptyCreateComponentRoot extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private String orgHandle;
        private String orgId;
        private String orgUUID;
        private String projectId;

        private ChoreoProject project;
        private final String repoName = "byor-greetings-app1";
        private String githubOrg;
        private final String repoBranch = "dev";
        private final String repoBranchV2 = "dev-v2";

        private List<Environment> environments;

        @Autowired
        Map<Endpoints, HttpClient> citrusClients;

        ChoreoComponent choreoComponent;

        @Autowired
        private HttpClient choreoProjectsTestClient;

        @BeforeClass
        public void setup_TestUserManagedNonEmptyCreateComponentRoot()
                        throws Exception {
                orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
                orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
                orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
                githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                project = GraphQL.createProject(accessToken);
                projectId = project.getId();
        }

        @Test
        @CitrusTest
        public void createUserManagedComponent_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                // Creating component
                String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

                Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/byor-greetings-app1").branch(repoBranch).subPath("").build();
                GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);

                choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                                ComponentFlavour.STANDARD);
                Assert.assertNotNull(choreoComponent.getId());

                environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        }

        @Test(dependsOnMethods = { "createUserManagedComponent_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void createNewVersion_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                // Creating new branch
                GitHub.createNewBranch(githubOrg, repoName, repoBranch, repoBranchV2);
                GraphqlDTO dto = GraphqlDTO.builder()
                                .orgHandler(orgHandle)
                                .projectId(projectId)
                                .orgUuid(orgUUID)
                                .componentId(choreoComponent.getId())
                                .componentType(choreoComponent.getType())
                                .branch(repoBranchV2)
                                .apiId(choreoComponent.getApiId())
                                .build();
                ComponentUtils.createNewVersion(this, citrusClients, accessToken, dto);
        }

        @Test(dependsOnMethods = { "createNewVersion_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void componentDeployment_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                ComponentUtils.deployComponent(this, citrusClients, accessToken,
                        choreoComponent, environments, ComponentFlavour.STANDARD, null);
        }

        @Test(dependsOnMethods = {"componentDeployment_TestUserManagedNonEmptyCreateComponentRoot"})
        @CitrusTest
        public void componentPromotionToProd_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                ComponentUtils.promoteComponent(this, citrusClients, accessToken, choreoComponent, environments,
                        ComponentFlavour.STANDARD, null);
        }

        @Test(dependsOnMethods = { "componentPromotionToProd_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void invokeAPIEP_TestUserManagedNonEmptyCreateComponentRoot() throws Exception {
                SleepUtil.sleep(30);
                ComponentUtils.invokeApiEndpoint(accessToken, choreoComponent, Constant.Environment.Development);
                ComponentUtils.invokeApiEndpoint(accessToken, choreoComponent, Constant.Environment.Production);
        }

        @Test(dependsOnMethods = { "invokeAPIEP_TestUserManagedNonEmptyCreateComponentRoot" })
        @CitrusTest
        public void deleteBranchV2_TestUserManagedNonEmptyCreateComponentRoot() throws IOException {
                Response response = GitHub.deleteBranch(githubOrg, repoName, repoBranchV2);
                Assert.assertEquals(response.getStatusCode(), 204);
        }
}
