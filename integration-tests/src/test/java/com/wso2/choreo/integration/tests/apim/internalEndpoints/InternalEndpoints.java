package com.wso2.choreo.integration.tests.apim.internalEndpoints;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.proxyapi.TestSessionResponse;
import com.wso2.choreo.integration.models.proxyapi.TestSessionResponsePayload;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class InternalEndpoints extends TestNGCitrusSpringSupport {
    private static final String SVC_COMPONENTS_REPO_URL = "https://github.com/choreo-test-apps/internal-endpoint-test-book-listing-service";
    private static final String SVC_COMPONENT_DOCKER_CONTEXT = ".";
    private static final String SVC_COMPONENT_DOCKER_FILE_PATH = "Dockerfile";
    private static final String OAS_FILE_PATH = "oas.yaml";
    private static String DEPLOYED_SVC_COMPONENT_SERVICE_NAME = "internal-endpoint-testing-";
    private String accessToken;
    private String orgHandle;
    private int orgId;
    private String orgUUID;
    private String userIdpId;
    private ChoreoComponent serviceComponent;
    private List<Environment> serviceComponentEnvironments;

    private List<Endpoint> serviceComponentEndpoints;
    private ComponentDeploymentStatusDTO deployedServiceComponentStatus;
    private TestSessionResponsePayload testSessionResponsePayload;
    private String API_INVOCATION_REQUEST_BODY;
    private String API_INVOCATION_RESOURCE_PATH;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestChoreoInternalEndpointTestCase() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        userIdpId = Configuration.getConfig(ConfigDefinition.TEST_USER_IDP_ID);
        API_INVOCATION_REQUEST_BODY = "[{\"id\":1,\"title\":\"Book 1\",\"author\":\"Author 1\"},{\"id\":2,\"title\":\"Book 2\",\"author\":\"Author 2\"}]";
        API_INVOCATION_RESOURCE_PATH = "books";
    }

    @Test
    @CitrusTest
    public void createServiceComponent_TestInternalEndpoints() throws Exception {
        Repository serviceRepo = Repository.builder().repoUrl(SVC_COMPONENTS_REPO_URL).oasFilePath(OAS_FILE_PATH)
                .dockerfilePath(SVC_COMPONENT_DOCKER_FILE_PATH).
                dockerContext(SVC_COMPONENT_DOCKER_CONTEXT).build();
        serviceComponent = ComponentUtils.getReusableComponent(this, accessToken,
                serviceRepo, DEPLOYED_SVC_COMPONENT_SERVICE_NAME, citrusClients, ComponentFlavour.BYOC,
                Constant.displayType.byocService.name());
        serviceComponentEnvironments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, serviceComponent);
        Commit latestCommit = ComponentUtils.getLatestCommit(this, citrusClients, accessToken, serviceComponent);
        deployedServiceComponentStatus = ComponentUtils
                .validateComponentDeployment(this, citrusClients, accessToken, serviceComponent,
                        latestCommit, serviceComponentEnvironments, true);

        if(deployedServiceComponentStatus != null && !deployedServiceComponentStatus.getDeploymentStatusV2().equals("ACTIVE")){
            throw new ValidationException("internal-endpoint-testing-component is not in active state");
        }

        if(deployedServiceComponentStatus == null){
            deployedServiceComponentStatus = ComponentUtils.deployAndValidateBuiltComponent(this, citrusClients, accessToken,
                    serviceComponent,serviceComponentEnvironments);
        }
        ComponentUtils.validateEndpoints(this, citrusClients, accessToken, serviceComponent,
                deployedServiceComponentStatus);
    }

    @Test(dependsOnMethods = {"createServiceComponent_TestInternalEndpoints"})
    @CitrusTest
    public void generateTestSession_TestInternalEndpoints() throws Exception {
        serviceComponentEndpoints = ComponentUtils.getEndpoints(this, citrusClients, accessToken, serviceComponent,
                deployedServiceComponentStatus);
        TestSessionResponse testSessionResponse = ComponentUtils.generateTestSession(this, citrusClients, accessToken,
                serviceComponent, serviceComponentEnvironments, serviceComponentEndpoints, userIdpId);
        testSessionResponsePayload = testSessionResponse.getData();
    }

    @Test(dependsOnMethods = {"generateTestSession_TestInternalEndpoints"})
    @CitrusTest
    public void invokeInternalEndpoint_TestInternalEndpoints() throws Exception {
        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfoInternalEndpoint(this, citrusClients,
                accessToken, serviceComponent, deployedServiceComponentStatus, serviceComponentEnvironments);
        URL internalURL = new URL(invokeData.getLeft());
        URI uri = internalURL.toURI();
        String newPath = "/_t" + uri.getPath();
        URI newURI = new URI(uri.getScheme(), testSessionResponsePayload.getVhosts().getApimExternalVhost(), newPath,
                uri.getQuery(), uri.getFragment());
        URL newURL = newURI.toURL();
        ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), newURL.toString(),
                testSessionResponsePayload.getSessionId(), API_INVOCATION_RESOURCE_PATH, API_INVOCATION_REQUEST_BODY);
        ComponentUtils.deleteTestSession(this, citrusClients, accessToken, serviceComponent,
                serviceComponentEnvironments, serviceComponentEndpoints, userIdpId, testSessionResponsePayload.getSessionId());
    }
}
