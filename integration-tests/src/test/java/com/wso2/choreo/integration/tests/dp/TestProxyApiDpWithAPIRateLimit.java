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
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyDeployment;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.commons.lang3.tuple.Pair;
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

public class TestProxyApiDpWithAPIRateLimit extends TestBase {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_ProxyApiDpWithAPIRateLimit() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createProject_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        String firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        String firstContext = APICreator.generateContext(firstAPIName);
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        dp.setChoreoProject(project);
        dp.setFirstName(firstAPIName);
        dp.setContext(firstContext);
    }

    @Test(dependsOnMethods = {"createProject_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void verifyAPIName_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.validateAPIName(dp.getFirstName(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }
    
    @Test(dependsOnMethods = {"verifyAPIName_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testCreateComponentForProxyAPI_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
        Pair<ChoreoComponent, ProxyAPI> componentDetail = ComponentUtils.createProxyComponent(this, citrusClients,
                accessToken, componentName, dp.getFirstName(), dp.getChoreoProject());
        dp.setChoreoComponent(componentDetail.getLeft());
        dp.setProxyAPI(componentDetail.getRight());
    }

    @Test(dependsOnMethods = {"testCreateComponentForProxyAPI_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testExistingAPI_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.validateAPIName(dp.getFirstName(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testExistingAPI_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testAPIBasePathValidationForAPIProxyCreation_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp)
            throws IOException {

        // Create a unique API Name.
        String secondAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        Response response = APICreator.createAPI(secondAPIName, dp.getFirstName(), accessToken).getResponse();
        dp.setSecondName(secondAPIName);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.CREATED.value());

    }

    @Test(dependsOnMethods = {"testAPIBasePathValidationForAPIProxyCreation_ProxyApiDpWithAPIRateLimit"},
            dataProvider = "dps")
    @CitrusTest
    public void testUpdateSwagger_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws IOException {
        ApiDTO apiDTO = ApiDTO.builder().apiName(dp.getProxyAPI().getName()).
                            description(dp.getProxyAPI().getDescription()).
                            productionEndpoint(Constant.DEFAULT_ENDPOINT).
                            sandboxEndpoint(Constant.DEFAULT_ENDPOINT).
                            basePath(dp.getProxyAPI().getContext() + "/1.0.0").build();

        String apiPayload = ObjectMapperUtil.mapObjectToString(
                "templates/graphql/requests/proxyAPIUpdateRequestWithAPIRateLimit.mustache", apiDTO);
        Response response = APICreator.updateAPIWithRestAPIContent(dp.getProxyAPI(), apiPayload, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testUpdateSwagger_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void getDeploymentEnvironment_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, dp.getChoreoComponent());
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"getDeploymentEnvironment_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void deployProxyAPI_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        dp.setProxyAPIBuild(ComponentUtils.deployProxyComponent(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getEnvironments()));
    }

    @Test(dependsOnMethods = {"deployProxyAPI_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void promoteProxyAPI_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        ComponentUtils.promoteProxyComponent(this, citrusClients, accessToken, dp.getChoreoComponent(),
                dp.getEnvironments(), dp.getProxyAPIBuild());
    }

    @Test(dependsOnMethods = {"promoteProxyAPI_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void componentDevDeploymentStatus_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        dp.setProxyDeployments(ComponentUtils.getProxyDeployments(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getEnvironments()));
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void setKeyData_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {
        for (ProxyDeployment proxyDeployment : dp.getProxyDeployments()) {
            KeyData keyData = ApiManager.getApiKey(this, citrusClients.get(Endpoints.STS_ENDPOINT), accessToken,
                    dp.getProxyAPI().getId(), ComponentUtils.getKeyType(proxyDeployment.getEnvironment(), dp.getEnvironments()));
            if (proxyDeployment.getEnvironment().getId().equals(dp.getEnvironments().get(0).getId())) {
                dp.setDevKeyData(keyData);
            } else {
                dp.setProdKeyData(keyData);
            }
        }
    }

    @Test(dependsOnMethods = {"setKeyData_ProxyApiDpWithAPIRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testDevDeployment_ProxyApiDpWithAPIRateLimit(DataProviderWrapper dp) throws Exception {

        // To give a time to deploy the API.
        Thread.sleep(10000);
        String devURL = dp.getProxyDeployments().get(0).getInvokeUrl() + "/users";

        Pair<Boolean, Integer> pair = ComponentUtils.testDeploymentWithRateLimit(devURL, dp.getDevKeyData().getApikey(),15);
        Boolean isRateLimitExceeded = pair.getLeft();
        int count = pair.getRight();

        Assert.assertTrue(isRateLimitExceeded, "Requests are not rate limited");
        Assert.assertTrue(count > 10, "Requests are not rate limited at the desired method");

        long timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        Thread.sleep(timeRemainingTillNextMinute + 5000);
        Response dev = HttpClientUtil.httpGET(devURL, "", dp.getDevKeyData().getApikey());
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value(), "Rate limit counter did not reset");
    }
}
