/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.dp;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.proxydeployer.ProxyDeployer;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;
import com.wso2.choreo.integration.models.proxyapi.DeploymentStatus;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.proxyapi.ProxyDeployment;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.revision.DeploymentInfo;
import com.wso2.choreo.integration.models.revision.Revision;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TestProxyApiDp extends TestBase {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_ProxyApiEUDpIT() throws IOException, TokenRetrievalException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void creteProject_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        String firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        String firstContext = APICreator.generateContext(firstAPIName);
        ChoreoProject project = GraphQL.createProject(dp.getRegion(), accessToken);
        dp.setChoreoProject(project);
        dp.setFirstName(firstAPIName);
        dp.setContext(firstContext);
        Assert.assertEquals(project.getRegion(), dp.getRegion());
    }

    @Test(dependsOnMethods = {"creteProject_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void verifyAPIName_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.validateAPIName(dp.getFirstName(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }

    @Test(dependsOnMethods = {"verifyAPIName_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void createAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        ProxyAPI proxyAPI = ComponentUtils.createApiProxy(this, citrusClients, accessToken, dp.getFirstName());
        dp.setProxyAPI(proxyAPI);
        Assert.assertNotNull(proxyAPI.getId());
    }

    @Test(dependsOnMethods = {"createAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testCreateComponentForProxyAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        GraphqlDTO dto = ComponentUtils.createProxyComponentRequest(dp.getFirstName(), dp.getChoreoProject(), dp.getProxyAPI().getId());

        ChoreoComponent choreoComponent = ComponentUtils.createProxyComponent(this, citrusClients, accessToken, dto);

        dp.setChoreoComponent(choreoComponent);
    }

    @Test(dependsOnMethods = {"testCreateComponentForProxyAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testExistingAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.validateAPIName(dp.getFirstName(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testExistingAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testAPIBasePathValidationForAPIProxyCreation_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        // Create a unique API Name.
        String secondAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        Response response = APICreator.createAPI(secondAPIName, dp.getFirstName(), accessToken).getResponse();
        dp.setSecondName(secondAPIName);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.CREATED.value());

    }

    @Test(dependsOnMethods = {"testAPIBasePathValidationForAPIProxyCreation_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testUpdateSwagger_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.updateAPI(dp.getProxyAPI(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testUpdateSwagger_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void getDeploymentEnvironment_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients, accessToken, dp.getChoreoComponent());
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"getDeploymentEnvironment_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void deployProxyAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        dp.setProxyAPIBuild(ComponentUtils.deployProxyComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments()));
    }


    @Test(dependsOnMethods = {"deployProxyAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void promoteProxyAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        ComponentUtils.promoteProxyComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments(), dp.getProxyAPIBuild());
    }

    @Test(dependsOnMethods = {"promoteProxyAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDevDeploymentStatus_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        dp.setProxyDeployments(ComponentUtils.getProxyDeployments(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments()));
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testDevDeployment_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        for (ProxyDeployment proxyDeployment : dp.getProxyDeployments()) {
            KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                    dp.getProxyAPI().getId(), proxyDeployment.getEnvironment());
            ComponentUtils.invokeApiGET(this, keyData.getApikey(), proxyDeployment.getInvokeUrl(), "/users", "{\"hello\": \"world\"}");
            if (proxyDeployment.getEnvironment().equals(dp.getEnvironments().get(0).getName())) {
                dp.setDevKeyData(keyData);
            } else {
                dp.setProdKeyData(keyData);
            }
        }
    }

    @Test(dependsOnMethods = {"testDevDeployment_ProxyApiEUDpIT"},
            dataProvider = "dps")
    @CitrusTest
    public void testUpdateSwaggerWithOperationRateLimit_ProxyApiEUDpIT(DataProviderWrapper dp)
            throws Exception {
        ChoreoComponent component = dp.getChoreoComponent();
        String buildId = dp.getProxyAPIBuild().getBuilds()[0].getBuildId();
        String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        String apiId = dp.getProxyAPI().getId();
        String revisionUUID = null;
        ApiDTO apiDTO = ApiDTO.builder().apiName(dp.getProxyAPI().getName())
                .description(dp.getProxyAPI().getDescription())
                .productionEndpoint(Constant.DEFAULT_ENDPOINT)
                .sandboxEndpoint(Constant.DEFAULT_ENDPOINT)
                .basePath(dp.getProxyAPI().getContext() + "/1.0.0")
                .build();
        String swaggerContent = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/proxyapiUpdateRequestWithMethodRatelimit.mustache", apiDTO);
        RevisionWrapper revisionList = ApiManager.getApiRevision(apiId, accessToken);
        for (Revision revision : revisionList.getList()) {
            if (revision.getDeploymentInfo() == null) {
                return;
            }
            for (DeploymentInfo deploymentInfo : revision.getDeploymentInfo()) {
                if (dp.getEnvironments().get(0).getApiEnvName().equals(deploymentInfo.getName())) {
                    revisionUUID = deploymentInfo.getRevisionUuid();
                    break;
                }
            }
            if (revisionUUID != null) {
                break;
            }
        }
        Assert.assertNotNull(revisionUUID);
        DeploySettings deploySettings = APICreator.deployRevision(component.getId(),
                component.getLatestApiVersion().getId(),
                dp.getEnvironments().get(0).getId(),
                orgId,
                revisionUUID, buildId, apiId, accessToken, null, swaggerContent);
        HttpClient choreoEPClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        ProxyDeployer.getProxyAPIDeploymentStatus(this, choreoEPClient, accessToken, component.getId(),
                component.getLatestApiVersion().getId(), deploySettings.getRequestId());
    }

    @Test(dependsOnMethods = {"testUpdateSwaggerWithOperationRateLimit_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void deployProxyAPIAfterOperationRateLimitUpdate_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        dp.setProxyAPIBuild(ComponentUtils.deployProxyComponent(this, citrusClients, accessToken, dp.getChoreoComponent(), dp.getEnvironments()));
    }

    @Test(dependsOnMethods = {"deployProxyAPIAfterOperationRateLimitUpdate_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testDevDeploymentAfterOperationRateLimitUpdate_ProxyApiEUDpIT(DataProviderWrapper dp)
            throws InterruptedException {

        // To give a time to deploy the API.
        Thread.sleep(10000);
        String devURL = dp.getProxyDeployments().get(0).getInvokeUrl() + "/users";

        // Rate limiting counter resets based on the system clock.
        long timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        if (timeRemainingTillNextMinute < 15000) {
            Thread.sleep(timeRemainingTillNextMinute + 5000);
        }

        boolean isRateLimitExceeded = false;
        int count = 0;
        long starttime = 0;
        long endtime = 0;

        // Repeat the check until the rate limit is exceeded or all requests are sent within the same minute
        while (isRateLimitExceeded == false) {
            starttime = System.currentTimeMillis();
            for (int i = 0; i < 8; i++) {
                Response dev = HttpClientUtil.httpGET(devURL, "", dp.getDevKeyData().getApikey());
                count++;
                if (dev.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                    isRateLimitExceeded = true;
                    break;
                }
                Thread.sleep(500);
            }
            endtime = System.currentTimeMillis();

            // Break the loop if all requests are sent within the same minute
            if (endtime / 60000 == starttime / 60000) {
                break;
            }
        }

        Assert.assertTrue(isRateLimitExceeded, "Requests are not rate limited");
        Assert.assertTrue(count > 5, "Requests are not rate limited at the desired count " + count);
        timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        Thread.sleep(timeRemainingTillNextMinute + 5000);
        Response dev = HttpClientUtil.httpGET(devURL, "", dp.getDevKeyData().getApikey());
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value(), "Rate limit counter did not reset");
    }

    @Test(dependsOnMethods = {"testDevDeploymentAfterOperationRateLimitUpdate_ProxyApiEUDpIT"},
            dataProvider = "dps")
    @CitrusTest
    public void testUpdateSwaggerWithAPIRateLimit_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {

        ApiDTO apiDTO = ApiDTO.builder().apiName(dp.getProxyAPI().getName())
                .description(dp.getProxyAPI().getDescription()).productionEndpoint(Constant.DEFAULT_ENDPOINT).
                sandboxEndpoint(Constant.DEFAULT_ENDPOINT).basePath(dp.getProxyAPI().getContext() + "/1.0.0").build();
        String apiPayload = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/proxyAPIUpdateAPIWithAPIRateLimit.mustache", apiDTO);
        ChoreoComponent component = dp.getChoreoComponent();
        String buildId = dp.getProxyAPIBuild().getBuilds()[0].getBuildId();
        String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        String apiId = dp.getProxyAPI().getId();
        String revisionUUID = null;
        RevisionWrapper revisionList = ApiManager.getApiRevision(apiId, accessToken);
        for (Revision revision : revisionList.getList()) {
            if (revision.getDeploymentInfo() == null) {
                return;
            }
            for (DeploymentInfo deploymentInfo : revision.getDeploymentInfo()) {
                if (dp.getEnvironments().get(0).getApiEnvName().equals(deploymentInfo.getName())) {
                    revisionUUID = deploymentInfo.getRevisionUuid();
                    break;
                }
            }
            if (revisionUUID != null) {
                break;
            }
        }
        Assert.assertNotNull(revisionUUID);
        DeploySettings deploySettings = APICreator.deployRevision(component.getId(),
                component.getLatestApiVersion().getId(),
                dp.getEnvironments().get(0).getId(),
                orgId,
                revisionUUID, buildId, apiId, accessToken, apiPayload, null);
        HttpClient choreoEPClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        ProxyDeployer.getProxyAPIDeploymentStatus(this, choreoEPClient, accessToken, component.getId(),
                component.getLatestApiVersion().getId(), deploySettings.getRequestId());
    }

    @Test(dependsOnMethods = {"testUpdateSwaggerWithAPIRateLimit_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void initiateProxyDeploymentAfterAPIRateLimitUpdate_ProxyApiEUDpIT(DataProviderWrapper dp)
            throws IOException, NoLatestApiVersionFoundException {
        ProxyResponse<Status> statusProxyResponse = APICreator.initiateDeployment(dp.getChoreoComponent().getId(),
                dp.getChoreoComponent().getLatestApiVersion().getId(), dp.getEnvironments().get(0).getId(), accessToken);
        Assert.assertEquals(statusProxyResponse.getResponse().getStatusCode(), HttpStatus.OK.value());
        Assert.assertTrue(statusProxyResponse.getEntity().isSuccess());
    }

    @Test(dependsOnMethods = {"initiateProxyDeploymentAfterAPIRateLimitUpdate_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void getProxyAPIBuildsAfterAPIRateLimitUpdate_ProxyApiEUDpIT(DataProviderWrapper dp)
            throws NoLatestApiVersionFoundException {
        ProxyAPIBuild proxyAPIBuild = APICreator.getAPIBuilds(dp.getChoreoComponent().getId(),
                dp.getChoreoComponent().getLatestApiVersion().getId(), accessToken);
        dp.setProxyAPIBuild(proxyAPIBuild);
    }

    @Test(dependsOnMethods = {"getProxyAPIBuildsAfterAPIRateLimitUpdate_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void deployProxyAPIAfterAPIRateLimitUpdate_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        String buildId = dp.getProxyAPIBuild().getBuilds()[0].getBuildId();
        ProxyResponse<Status> res = APICreator.deployProxyAPI(dp.getChoreoComponent().getId(),
                dp.getProxyAPI().getId(), buildId, dp.getEnvironments().get(0).getId(), accessToken);
        Assert.assertEquals(res.getResponse().getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"deployProxyAPIAfterAPIRateLimitUpdate_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testDevDeploymentAfterAPIRateLimitUpdate_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException, InterruptedException {

        // To give a time to deploy the API.
        Thread.sleep(10000);
        String devURL = dp.getProxyDeployments().get(0).getInvokeUrl() + "/users";

        // Rate limiting counter resets based on the system clock.
        long timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        if (timeRemainingTillNextMinute < 15000) {
            Thread.sleep(timeRemainingTillNextMinute + 5000);
        }

        boolean isRateLimitExceeded = false;
        int count = 0;
        long starttime = 0;
        long endtime = 0;

        // Repeat the check until the rate limit is exceeded or all requests are sent within the same minute
        while (isRateLimitExceeded == false) {
            starttime = System.currentTimeMillis();
            for (int i=0; i< 15; i++) {
                Response dev = HttpClientUtil.httpGET(devURL, "", dp.getDevKeyData().getApikey());
                count++;
                if (dev.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                    isRateLimitExceeded = true;
                    break;
                }
                Thread.sleep(500);
            }
            endtime = System.currentTimeMillis();

            // Break the loop if all requests are sent within the same minute
            if (endtime / 60000 == starttime / 60000) {
                break;
            }
        }

        Assert.assertTrue(isRateLimitExceeded, "Requests are not rate limited");
        Assert.assertTrue(count > 10, "Requests are not rate limited at the desired method");
        timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        Thread.sleep(timeRemainingTillNextMinute + 5000);
        Response dev = HttpClientUtil.httpGET(devURL, "", dp.getDevKeyData().getApikey());
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value(), "Rate limit counter did not reset");
    }
}
