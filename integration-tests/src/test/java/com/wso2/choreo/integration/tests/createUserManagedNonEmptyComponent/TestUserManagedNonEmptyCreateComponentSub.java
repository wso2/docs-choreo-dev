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
 *
 * tests related to component creation from user managed non empty repo subpath.
 */
public class TestUserManagedNonEmptyCreateComponentSub extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private String orgHandle;
        private String orgId;
        private String orgUUID;
        private String projectId;

        private ChoreoProject project;
        private static final String repoName = "byor-greetings-app2";
        private static final String repoSubpath = "hello_service";
        private static final String repoType = "UserManagedNonEmpty";
        private static final String repoBranch = "feature";
        private String githubOrg;
        private String githubPAT;
        private static ChoreoComponent choreoComponent;
        private String repoBranchV2 = "feature-v2";

        private List<Environment> environments;

        @Autowired
        Map<Endpoints, HttpClient> citrusClients;

        @BeforeClass
        public void setup_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
                orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
                orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
                githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
                githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
                project = GraphQL.createProject(accessToken);
                projectId = project.getId();
        }

        @Test
        @CitrusTest
        public void createUserManagedComponent_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
                // Creating component
                String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

                Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/byor-greetings-app2").branch(repoBranch).subPath(repoSubpath).build();
                GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);

                choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                                ComponentFlavour.STANDARD);
                choreoComponent.setBranch(repoBranch);
                Assert.assertNotNull(choreoComponent.getId());

                environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, choreoComponent);
        }

        @Test(dependsOnMethods = {"createUserManagedComponent_TestUserManagedNonEmptyCreateComponentSub"})
        @CitrusTest
        public void createNewVersion_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
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

        @Test(dependsOnMethods = { "createNewVersion_TestUserManagedNonEmptyCreateComponentSub" })
        @CitrusTest
        public void componentDeployment_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
                ComponentUtils.deployComponentInBranch(this, citrusClients, accessToken, choreoComponent,
                        environments, ComponentFlavour.STANDARD, repoBranch, null);
        }

        @Test(dependsOnMethods = {"componentDeployment_TestUserManagedNonEmptyCreateComponentSub"})
        @CitrusTest
        public void componentPromotionToProd_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
                ComponentUtils.promoteComponentInBranch(this, citrusClients, accessToken, choreoComponent,
                        environments, ComponentFlavour.STANDARD, repoBranch, null);
        }

        @Test(dependsOnMethods = { "componentPromotionToProd_TestUserManagedNonEmptyCreateComponentSub" })
        @CitrusTest
        public void invokeAPIEP_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
                SleepUtil.sleep(30);
                ComponentUtils.invokeApiEndpoint(accessToken, choreoComponent, Constant.Environment.Development);
                ComponentUtils.invokeApiEndpoint(accessToken, choreoComponent, Constant.Environment.Production);
        }

        @Test(dependsOnMethods = { "invokeAPIEP_TestUserManagedNonEmptyCreateComponentSub" })
        @CitrusTest
        public void deleteBranchV2_TestUserManagedNonEmptyCreateComponentSub() throws IOException {
                Response response = GitHub.deleteBranch(githubOrg, repoName, repoBranchV2);
                Assert.assertEquals(response.getStatusCode(), 204);
        }
}
