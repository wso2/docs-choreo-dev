package com.wso2.choreo.integration.tests.createUserManagedNonEmptyComponent;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.CreateNewVersionResponseDTO;
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
        public void setup_TestUserManagedNonEmptyCreateComponentSub()
                        throws IOException, InterruptedException, ProjectCreationException, TokenRetrievalException {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
                orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
                orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
                githubOrg = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
                githubPAT = Configuration.getConfig(ConfigDefinition.GITHUB_PAT);
                ChoreoProject project = GraphQL.createProject(accessToken);
                projectId = project.getId();
        }

        @Test
        @CitrusTest
        public void createUserManagedComponent_TestUserManagedNonEmptyCreateComponentSub() throws Exception {

                // Creating component
                String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
                // The GH repository is initiated everyday with a new Ballerina project using a GH Action Workflow
                String srcGitHubURL = Constant.GITHUB_URL.concat(githubOrg).concat("/")
                                .concat(repoName).concat("/tree/").concat(repoBranch).concat("/").concat(repoSubpath);
                GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").srcGitRepoUrl(srcGitHubURL)
                                .projectId(projectId).orgId(Integer.parseInt(orgId))
                                .orgHandler(orgHandle)
                                .repositoryType(repoType)
                                .repositoryBranch(repoBranch)
                                .repositorySubPath(repoSubpath)
                                .displayType(Constant.displayType.restAPI.name()).build();
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
                CreateNewVersionResponseDTO response = ComponentUtils.createNewVersion(this, citrusClients, accessToken,
                                dto);
                Assert.assertEquals("2.0.0", response.getApiVersion());
                Assert.assertEquals(response.getApiVersion(), "2.0.0");
        }

        @Test(dependsOnMethods = { "createNewVersion_TestUserManagedNonEmptyCreateComponentSub" })
        @CitrusTest
        public void componentDeployment_TestUserManagedNonEmptyCreateComponentSub() throws Exception {
                Status status = Orgs.createdComponentStatus(choreoComponent.getProjectId(), choreoComponent.getId(), 
                        accessToken);
                Assert.assertEquals(status.getData().getConclusion(), "success");
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
