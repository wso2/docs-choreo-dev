package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;
import com.wso2.choreo.integration.models.proxyapi.DeploymentStatus;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.revision.DeploymentInfo;
import com.wso2.choreo.integration.models.revision.Revision;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestRestAPIDp extends TestBase {

    private String accessToken;
    private Environment[] en;
    @Autowired
    private HttpClient choreoCPTestClient;
    @Autowired
    private HttpClient choreoTestClient;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @DataProvider(name = "env-provider")
    public Object[][] envProvider() {
        return DataProviderWrapper.convertToDataProvider(Arrays.asList(Constant.Environment.values()));
    }

    @BeforeClass
    public void setup_TestRestAPIIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }


    @Test(dataProvider = "dps")
    @CitrusTest
    public void createUserManagedRestAPI_TestRestAPIIT(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = GraphQL.createProject(dp.getRegion(), accessToken);
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).triggerID("null").
                srcGitRepoUrl("https://github.com/choreo-test-apps/rest-api").
                projectId(project.getId()).
                displayType(Constant.displayType.restAPI.name()).
                build();
        ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto, ComponentFlavour.STANDARD);
        dp.setChoreoProject(project);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertEquals(project.getRegion(), dp.getRegion());
        Assert.assertNotNull(choreoComponent.getId());
    }

    @Test(dependsOnMethods = {"createUserManagedRestAPI_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDeploy_TestRestAPIIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), ComponentFlavour.STANDARD);
        String devInvokeURL = statusDTO.getInvokeUrl();
        dp.setDevInvokeUrl(devInvokeURL);
        dp.setBuildId(statusDTO.getBuild().getBuildId());
    }


    @Test(dependsOnMethods = {"componentDeploy_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void promote_TestRestAPIIT(DataProviderWrapper dp) throws Exception {
        ComponentDeploymentStatusDTO statusDTO = ComponentUtils.promoteComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), ComponentFlavour.STANDARD);
        String prodInvokeURL = statusDTO.getInvokeUrl();
        String apiId = statusDTO.getApiId();
        dp.setApiId(apiId);
        dp.setProdInvokeUrl(prodInvokeURL);
    }


    @Test(dependsOnMethods = {"promote_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIInDev_TestBYOCEUDataPlane(DataProviderWrapper dp) throws Exception {
        KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken, dp.getApiId());
        ComponentUtils.invokeApiGET(this, keyData.getApikey(), dp.getDevInvokeUrl(), "/isOdd?number=34", "false");
        dp.setKeyData(keyData);
    }

    @Test(dependsOnMethods = {"promote_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void invokeAPIInProd_TestBYOCEUDataPlane(DataProviderWrapper dp) throws Exception {
        ComponentUtils.invokeApiGET(this, dp.getKeyData().getApikey(), dp.getProdInvokeUrl(), "/isOdd?number=34", "false");
    }

    @Test(dependsOnMethods = {"promote_TestRestAPIIT"}, dataProvider = "dps")
    @CitrusTest
    public void ComponentDeploymentAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot(
            DataProviderWrapper dp) throws Exception {

        String revisionUUID = null;
        int revisionId = 0;
        RevisionWrapper revisionList = ApiManager.getApiRevision(dp.getChoreoComponent().getApiId(), accessToken);
        for (Revision revision : revisionList.getList()) {
            if (revision.getDeploymentInfo() == null) {
                return;
            }
            for (DeploymentInfo deploymentInfo : revision.getDeploymentInfo()) {
                if ("dev-us-east-azure".equals(deploymentInfo.getName())) {
                    revisionUUID = deploymentInfo.getRevisionUuid();
                    revisionId = Integer.parseInt(revision.getDisplayName()
                            .split(" ")[1]);
                    break;
                }
            }
            if (revisionUUID != null) {
                break;
            }
        }
        Assert.assertNotNull(revisionUUID, "Revision ID is null");

        String orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        ProxyAPI api = APICreator.getProxyAPI(dp.getChoreoComponent().getApiId(), orgUUID, accessToken).getEntity();
        String apiYamlFilename = "templates/graphql/requests/restAPIV2UpdateAPIWithRateLimit.mustache";
        Map<String, String> apiYamlParams = new HashMap<>();
        apiYamlParams.put("apiId", api.getId());
        apiYamlParams.put("apiName", api.getName());
        apiYamlParams.put("basePath", api.getContext() + "/2.0.0");
        apiYamlParams.put("revisionId", String.valueOf(0));
        String apiPayload = ObjectMapperUtil.mapObjectToString(apiYamlFilename, apiYamlParams);

        DeploySettings deploySettings = APICreator.deployRevision(dp.getChoreoComponent().getId(),
                dp.getChoreoComponent().getLatestApiVersion().getId(),
                dp.getDevEnv().getId(), orgUUID,
                revisionUUID, dp.getBuildId(), api.getId(), accessToken, apiPayload, null);
        boolean requestSuccess = false;
        DeploymentStatus deploymentStatus = null;
        int count = 0;
        while (!requestSuccess && count < 10) {
            deploymentStatus = APICreator.checkDeploymentStatus(dp.getChoreoComponent().getId(),
                    dp.getChoreoComponent().getLatestApiVersion().getId(),
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

    @Test(dependsOnMethods = {"ComponentDeploymentAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot"},
            dataProvider = "dps")
    @CitrusTest
    public void invokeRestAPIAfterAPIRateLimitUpdate_TestUserManagedNonEmptyCreateComponentRoot(DataProviderWrapper dp)
            throws Exception {
        // To give a time to deploy the API.
        Thread.sleep(10000);
        // Rate limiting counter resets based on the system clock.
        long timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        if (timeRemainingTillNextMinute < 15000) {
            Thread.sleep(timeRemainingTillNextMinute + 5000);
        }
        boolean isThrottled = false;
        try {
            ComponentUtils.invokeApiEndpointMultipleTimes(accessToken, dp.getChoreoComponent(),
                    Constant.Environment.Development, 15);
        } catch (InvokeAPICheckException e) {
            isThrottled = true;
        }

        Assert.assertTrue(isThrottled, "Requests are not rate limited at the desired limit");
        timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        Thread.sleep(timeRemainingTillNextMinute + 5000);
        try {
            ComponentUtils.invokeApiEndpoint(accessToken, dp.getChoreoComponent(), Constant.Environment.Production);
        } catch (Exception e) {
            Assert.fail("Unexpected error occured while invoking API : " + e.getMessage());
        }
    }

}
