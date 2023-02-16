package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class TestClientJwTValidation extends TestNGCitrusSpringSupport {
    private String accessToken;
    private ChoreoProject project;
    private ChoreoComponent choreoComponent;

    private String devInvokeURL;
    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup_TestClientJwTValidation() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = GraphQL.createProject(accessToken);
    }


    @Test
    @CitrusTest
    public void createUserManagedComponentFor_TestClientJwTValidation() throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/jwt-encoder").
                projectId(project.getId()).displayType(Constant.displayType.restAPI.name()).build();
        choreoComponent = ComponentUtils.createComponent(this, choreoTestClient, accessToken, dto);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_TestClientJwTValidation"})
    @CitrusTest
    public void addDeploymentConfiguration_TestClientJwTValidation() throws Exception {
        Orgs.getConfigurationMapping(choreoComponent, accessToken);
        Orgs.addConfiguration(choreoComponent, "dev", accessToken);
    }


    @Test(dependsOnMethods = {"addDeploymentConfiguration_TestClientJwTValidation"})
    @CitrusTest
    public void deploy_TestClientJwTValidation() throws Exception {
        Status status = GraphQL.deployComponent(choreoComponent, accessToken);
        Assert.assertTrue(status.isSuccess());
    }

    @Test(dependsOnMethods = {"deploy_TestClientJwTValidation"})
    @CitrusTest
    public void deploymentStatusByVersion_TestClientJwTValidation() throws Exception {
        GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
    }


    @Test(dependsOnMethods = {"deploymentStatusByVersion_TestClientJwTValidation"})
    @CitrusTest
    public void componentDevDeploymentStatus_TestClientJwTValidation() throws Exception {
        devInvokeURL = GraphQL.componentDeployment(choreoComponent, Constant.DEV_ENVIRONMENT, accessToken).getInvokeUrl();
    }


    @Test(dependsOnMethods = {"componentDevDeploymentStatus_TestClientJwTValidation"})
    @CitrusTest
    public void invokeAPI_TestClientJwTValidation() throws Exception {
        String apiKey = APICreator.getAPIKey(choreoComponent.getApiId(), accessToken).getApikey();
        String apiInvocationRequestURI = "/getJwt";

        http().
                client(devInvokeURL).
                send().
                get(apiInvocationRequestURI).
                message().
                header(HttpHeaders.ACCEPT, "text/plain").
                header("API-Key", apiKey);

        http().client(devInvokeURL).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/jwt/decoded_jwt.json")).
                validate(JsonMessageValidationContext.Builder.json());
    }


    @Test(dependsOnMethods = {"invokeAPI_TestClientJwTValidation"}, alwaysRun = true)
    @CitrusTest
    public void deleteComponent_TestClientJwTValidation() throws IOException {
        Response response = GraphQL.deleteComponent(choreoComponent.getId(), project.getId(), accessToken);
        ChoreoComponent[] components = GraphQL.getProjectComponents(project.getId(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
        Assert.assertEquals(components.length, 0);
    }

}
