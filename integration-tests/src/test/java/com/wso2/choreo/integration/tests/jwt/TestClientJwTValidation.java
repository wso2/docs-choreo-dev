package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;
import com.wso2.choreo.integration.models.proxyapi.DeploymentStatus;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class TestClientJwTValidation extends TestNGCitrusSpringSupport {
    private String accessToken;
    private ChoreoProject project;
    private ChoreoComponent choreoComponent;
    private String devInvokeURL;
    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private String orgUUID;
    private String revisionUUID;
    private String apiId;
    private ComponentDeploymentStatusDTO statusDTO;
    private String buildId;
    private String componentName;

    @BeforeClass
    public void setup_TestClientJwTValidation() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        project = GraphQL.createProject(accessToken);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
    }

    @Test
    @CitrusTest
    public void createUserManagedComponentFor_TestClientJwTValidation() throws Exception {
        componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/jwt-encoder").
                projectId(project.getId()).displayType(Constant.displayType.restAPI.name()).build();
        choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                ComponentFlavour.STANDARD);
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedComponentFor_TestClientJwTValidation"})
    @CitrusTest
    public void deploy_TestClientJwTValidation() throws Exception {
        statusDTO = ComponentUtils.deployComponent(this, citrusClients,
                accessToken, choreoComponent, ComponentFlavour.STANDARD);
        devInvokeURL = statusDTO.getInvokeUrl();
        apiId = statusDTO.getApiId();
        buildId = statusDTO.getBuild().getBuildId();
        revisionUUID = statusDTO.getApiRevision().getId();
        Thread.sleep(1000);
    }

    @Test(dependsOnMethods = {"deploy_TestClientJwTValidation"})
    @CitrusTest
    public void invokeAPI_TestClientJwTValidation() throws Exception {
        String apiKey = APICreator.getAPIKey(apiId, accessToken).getApikey();
        String apiInvocationRequestURI = "/getJwt";
        $(http().
                client(devInvokeURL).
                send().
                get(apiInvocationRequestURI).
                message().
                header(HttpHeaders.ACCEPT, "text/plain").
                header("API-Key", apiKey));
        $(http().
                client(devInvokeURL).
                receive().
                response(HttpStatus.INTERNAL_SERVER_ERROR).
                message().
                type(MessageType.PLAINTEXT));

    }

    @Test(dependsOnMethods = {"invokeAPI_TestClientJwTValidation"})
    @CitrusTest
    public void update_TestClientJWTValidation()
            throws Exception {
        ProxyAPI api = APICreator.getProxyAPI(apiId, orgUUID, accessToken).getEntity();
        String apiYamlFilename = "templates/jwt/updateApiWithBackendJWT.mustache";
        Map<String, String> apiYamlParams = new HashMap<>();
        apiYamlParams.put("apiId", api.getId());
        apiYamlParams.put("apiName", api.getName());
        apiYamlParams.put("basePath", api.getContext() + "/1.0.0");
        apiYamlParams.put("revisionId", String.valueOf(revisionId));
        String apiPayload = ObjectMapperUtil.mapObjectToString(apiYamlFilename, apiYamlParams);
        DeploySettings deploySettings = APICreator.deployRevision(choreoComponent.getId(),
                choreoComponent.getLatestApiVersion().getId(),
                choreoComponent.getLatestAppEnvId("dev"), orgUUID,
                revisionUUID, buildId, apiId, accessToken, apiPayload, null);
        boolean requestSuccess = false;
        DeploymentStatus deploymentStatus = null;
        int count = 0;
        while (!requestSuccess && count < 10) {
            deploymentStatus = APICreator.checkDeploymentStatus(choreoComponent.getId(),
                    choreoComponent.getLatestApiVersion().getId(),
                    deploySettings.getRequestId(), accessToken);
            if ("completed".equals(deploymentStatus.getStatus())) {
                requestSuccess = true;
            } else {
                Thread.sleep(2000);
                count++;
            }
        }
        Assert.assertTrue(requestSuccess, "Deployment request failed : Current Action : " +
                deploymentStatus.getCurrent_Action() + " Status : " + deploymentStatus.getStatus());
    }

    @Test(dependsOnMethods = {"update_TestClientJWTValidation"})
    @CitrusTest
    public void invokeAPIAfterUpdate_TestClientJwTValidation() throws Exception {
        String apiKey = APICreator.getAPIKey(apiId, accessToken).getApikey();
        String apiInvocationRequestURI = "/getJwt";
        $(http().
                client(devInvokeURL).
                send().
                get(apiInvocationRequestURI).
                message().
                header(HttpHeaders.ACCEPT,"text/plain").
                header("API-Key", apiKey));
        $(http().client(devInvokeURL).
                receive().
                response(HttpStatus.OK).
                message().
                type(MessageType.JSON).
                body(new ClassPathResource("templates/jwt/decoded_jwt.json")).
        validate(JsonMessageValidationContext.Builder.json()));
    }

}
