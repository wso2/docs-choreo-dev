package com.wso2.choreo.integration.tests.graphqlservice;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.createcomponentresponse.ComponentCreationResponse;
import com.wso2.choreo.integration.models.pullrequests.PullRequest;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

public class GraphQLServiceIT extends TestNGCitrusSpringSupport {

    private String accessToken;
    private String projectId;
    private String repoName;
    private ComponentCreationResponse response;
    private static ChoreoComponent choreoComponent;

    private String apiKey;
    private String devInvokeURL;
    private String prodInvokeURL;

    private  static final  String   QUERY="query{greeting(name:\""+"John"+"\")}";
    private static final String MUTATION="mutation{createUser(name:\""+"John"+"\")}";

    @BeforeClass
    public void setup_GraphQLServiceIT() throws Exception {
        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
         accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject project = GraphQL.createProject(accessToken);
        projectId = project.getId();

    }

    @Test
    @CitrusTest
    public void createUserManagedComponentFor_GraphQLServiceIT() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").srcGitRepoUrl(GitHub.getGitHubRepoUrl(repoName)).projectId(projectId).displayType(Constant.displayType.graphql.name()).build();
        response = GraphQL.createUserManagedComponent(dto, accessToken);
        Assert.assertNotNull(response.getId());
    }


    @Test(dependsOnMethods = {"createUserManagedComponentFor_GraphQLServiceIT"})
    @CitrusTest
    public void createdComponentStatus_GraphQLServiceIT() throws UnexpectedResponseException {
        Status status = Orgs.createdComponentStatus(projectId, response.getId(), accessToken);
        Assert.assertTrue(status.isSuccess());
    }


    @Test(dependsOnMethods = {"createdComponentStatus_GraphQLServiceIT"})
    @CitrusTest
    public void initialPRGeneration_GraphQLServiceIT() throws IOException, UnexpectedResponseException {
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 1);
        Assert.assertEquals(prs.length, 1);
    }

    @Test(dependsOnMethods = {"initialPRGeneration_GraphQLServiceIT"})
    @CitrusTest
    public void mergePR_GraphQLServiceIT() throws IOException, UnexpectedResponseException {
        GitHub.mergePR(repoName, "1");
        PullRequest[] prs = GraphQL.getComponentPullRequests(response.getId(), accessToken, 0);
        Assert.assertEquals(prs.length, 0);
    }


    @Test(dependsOnMethods = {"mergePR_GraphQLServiceIT"})
    @CitrusTest
    public void mergeNewCode_GraphQLServiceIT() throws IOException {
        String encodedContent = FileUtil.readFileEncodedContent("src/test/resources/templates/encodedbal/gql.bal");
        Response res = GitHub.mergeNewCode(repoName, "sample.bal", "update code", encodedContent);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"mergeNewCode_GraphQLServiceIT"})
    @CitrusTest
    public void componentRetrieval_GraphQLServiceIT() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, response.getHandler(), accessToken);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_GraphQLServiceIT"})
    @CitrusTest
    public void addDeploymentConfiguration_GraphQLServiceIT() throws Exception {
        Orgs.addConfiguration(choreoComponent, "dev", accessToken);
    }

    @Test(dependsOnMethods = {"addDeploymentConfiguration_GraphQLServiceIT"})
    @CitrusTest
    public void deploy_GraphQLServiceIT() throws Exception {
        GraphQL.deployComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"deploy_GraphQLServiceIT"})
    @CitrusTest
    public void deploymentStatusByVersion_GraphQLServiceIT() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_GraphQLServiceIT"})
    @CitrusTest
    public void componentDevDeploymentStatus_GraphQLServiceIT() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, "dev", accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_GraphQLServiceIT"})
    @CitrusTest
    public void addPromoteConfiguration_GraphQLServiceIT() throws Exception {
        Response res = Orgs.addConfiguration(choreoComponent, "prod", accessToken);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_GraphQLServiceIT"})
    @CitrusTest
    public void promote_GraphQLServiceIT() throws Exception {
        GraphQL.promoteComponent(choreoComponent, accessToken);
    }

    @Test(dependsOnMethods = {"promote_GraphQLServiceIT"})
    @CitrusTest
    public void componentProdDeploymentStatus_GraphQLServiceIT() throws Exception {
        prodInvokeURL = GraphQL.componentDeployment(choreoComponent, "prod", accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentProdDeploymentStatus_GraphQLServiceIT"})
    @CitrusTest
    public void invokeQueryInDev_TestBYOC() throws Exception {
        apiKey = ComponentUtils.getApiKey(choreoComponent, accessToken);
       Response res =  GqlServiceTestHelper.sendRequest(devInvokeURL,QUERY,apiKey);
       Assert.assertEquals(res.getStatusCode(),HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeQueryInDev_TestBYOC"})
    @CitrusTest
    public void invokeQueryInProd_TestBYOC() throws Exception {
        apiKey = ComponentUtils.getApiKey(choreoComponent, accessToken);
        Response res =  GqlServiceTestHelper.sendRequest(prodInvokeURL,QUERY,apiKey);
        Assert.assertEquals(res.getStatusCode(),HttpStatus.OK.value());
    }


    @Test(dependsOnMethods = {"invokeQueryInProd_TestBYOC"})
    @CitrusTest
    public void invokeMutationInDev_TestBYOC() throws Exception {
        apiKey = ComponentUtils.getApiKey(choreoComponent, accessToken);
        Response res =  GqlServiceTestHelper.sendRequest(devInvokeURL,MUTATION,apiKey);
        Assert.assertEquals(res.getStatusCode(),HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"invokeMutationInDev_TestBYOC"})
    @CitrusTest
    public void invokeMutationInProd_TestBYOC() throws Exception {
        apiKey = ComponentUtils.getApiKey(choreoComponent, accessToken);
        Response res =  GqlServiceTestHelper.sendRequest(prodInvokeURL,MUTATION,apiKey);
        Assert.assertEquals(res.getStatusCode(),HttpStatus.OK.value());
    }


}
