package com.wso2.choreo.integration.tests.byoc;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.GitHub;
import com.wso2.choreo.integration.apis.GraphQL;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ProjectCreationException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.exceptions.UnexpectedResponseException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.createcomponentresponse.CreateComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

public class TestBYOC extends TestNGCitrusSpringSupport {


    private static ChoreoComponent choreoComponent;
    private String orgHandle;
    private String projectId;
    private String repoName;
    private String accessToken;
    private CreateComponent response;
    private ChoreoOrganization org;


    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup() throws TokenRetrievalException, IOException, ProjectCreationException, InterruptedException {

        repoName = Constant.TEST_REPO_NAME_PREFIX.concat(String.valueOf(new Date().getTime()));
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        org = TestContext.getTestOrg();
        ChoreoProject project = org.createProject(accessToken);
        projectId = project.getId();


    }


    @Test
    @CitrusTest
    public void testCreateUserManagedComponentForJwt() throws IOException, UnexpectedResponseException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GitHub.initGitHubRepo(repoName, true, true, "nanoc");
        // response = GraphQL.createBYORComponent(repoName, componentName, projectId, accessToken);
    }


    @Test(dependsOnMethods = {"testCreateUserManagedComponentForJwt"})
    @CitrusTest
    public void testPullUserRepos() throws IOException {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Response res = GraphQL.createBYOCcomponent(componentName, projectId, repoName, accessToken);

    }

}
