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
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.Constant;
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

public class TestProxyApiDpWithOperationRateLimit extends TestBase {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_ProxyApiDpWithOperationRateLimit() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createProject_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws Exception {
        String firstAPIName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.DEFAULT_API_NAME);
        String firstContext = APICreator.generateContext(firstAPIName);
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        dp.setChoreoProject(project);
        dp.setFirstName(firstAPIName);
        dp.setContext(firstContext);
    }

    @Test(dependsOnMethods = {"createProject_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void verifyAPIName_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.validateAPIName(dp.getFirstName(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }
    
    @Test(dependsOnMethods = {"verifyAPIName_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testCreateComponentForProxyAPI_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws Exception {
        String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
        Pair<ChoreoComponent, ProxyAPI> componentDetail = ComponentUtils.createProxyComponent(this, citrusClients,
                accessToken, componentName, dp.getFirstName(), dp.getChoreoProject());
        dp.setChoreoComponent(componentDetail.getLeft());
        dp.setProxyAPI(componentDetail.getRight());
    }

    @Test(dependsOnMethods = {"testCreateComponentForProxyAPI_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testExistingAPI_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws IOException {
        Response response = APICreator.validateAPIName(dp.getFirstName(), accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testExistingAPI_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testAPIBasePathValidationForAPIProxyCreation_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp)
            throws IOException {

        // Create a unique API Name.
        String secondAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        Response response = APICreator.createAPI(secondAPIName, dp.getFirstName(), accessToken).getResponse();
        dp.setSecondName(secondAPIName);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.CREATED.value());

    }

    @Test(dependsOnMethods = {"testAPIBasePathValidationForAPIProxyCreation_ProxyApiDpWithOperationRateLimit"},
            dataProvider = "dps")
    @CitrusTest
    public void testUpdateSwagger_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws IOException {
        String swaggerFileName = "templates/graphql/requests/proxyAPIUpdateRequestWithMethodRateLimit.mustache";
        Response response = APICreator.updateAPIWithSwaggerFile(dp.getProxyAPI(), swaggerFileName, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testUpdateSwagger_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void getDeploymentEnvironment_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws Exception {
        List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                accessToken, dp.getChoreoComponent());
        dp.setEnvironments(environments);
    }

    @Test(dependsOnMethods = {"getDeploymentEnvironment_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void deployProxyAPI_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws Exception {
        dp.setProxyAPIBuild(ComponentUtils.deployProxyComponent(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getEnvironments()));
    }

    @Test(dependsOnMethods = {"deployProxyAPI_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void promoteProxyAPI_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) throws Exception {
        ComponentUtils.promoteProxyComponent(this, citrusClients, accessToken, dp.getChoreoComponent(),
                dp.getEnvironments(), dp.getProxyAPIBuild());
    }

    @Test(dependsOnMethods = {"promoteProxyAPI_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void componentDevDeploymentStatus_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp)
            throws Exception {
        dp.setProxyDeployments(ComponentUtils.getProxyDeployments(this, citrusClients, accessToken,
                dp.getChoreoComponent(), dp.getEnvironments()));
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void setKeyData_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp) {
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

    @Test(dependsOnMethods = {"setKeyData_ProxyApiDpWithOperationRateLimit"}, dataProvider = "dps")
    @CitrusTest
    public void testDevDeployment_ProxyApiDpWithOperationRateLimit(DataProviderWrapper dp)
            throws Exception {

        String devURL = dp.getProxyDeployments().get(0).getInvokeUrl() + "/users";

        ComponentUtils.testAPIReady(devURL, dp.getDevKeyData().getApikey());
        Thread.sleep(60000);
        Pair<Boolean, Integer> pair = ComponentUtils.testDeploymentWithRateLimit(devURL, dp.getDevKeyData().getApikey(),8);
        Boolean isRateLimitExceeded = pair.getLeft();
        int count = pair.getRight();

        Assert.assertTrue(isRateLimitExceeded, "Requests are not rate limited");
        Assert.assertTrue(count > 5, "Requests are not rate limited at the desired count " + count);

        Thread.sleep(60000);
        Response dev = HttpClientUtil.httpGET(devURL, "", dp.getDevKeyData().getApikey());
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value(), "Rate limit counter did not reset");
    }
}
