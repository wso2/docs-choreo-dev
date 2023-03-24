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
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.NoLatestApiVersionFoundException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.componentstatus.Status;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;


public class TestProxyApiEUDp extends TestBase {
    private static String accessToken;

    Environment[] environments;
    Environment devEnv;
    Environment prodEnv;
    ChoreoComponent choreoComponent;
    ProxyAPIBuild proxyAPIBuild;
    String devInvokeBaseURL;
    String prodInvokeBaseURL;
    String apiKey;

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
        dp.setChoreoComponent(choreoComponent);
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
    public void createAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        ProxyAPI proxyAPI = APICreator.createAPI(dp.getFirstName(), dp.getContext(), accessToken).getEntity();
        dp.setProxyAPI(proxyAPI);
        Assert.assertNotNull(proxyAPI.getId());
    }

    @Test(dependsOnMethods = {"createAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testCreateComponentForProxyAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        ProxyResponse<ChoreoComponent> response = GraphQL.createGraphqlQueryForComponentCreation(dp.getFirstName(), dp.getChoreoProject().getId(), dp.getProxyAPI().getId(), accessToken);
        choreoComponent = response.getEntity();
        Assert.assertEquals(response.getResponse().getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testCreateComponentForProxyAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentRetrieval_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        choreoComponent = GraphQL.getComponentDetails(dp.getChoreoProject().getId(), choreoComponent.getHandler(), accessToken);
        dp.setChoreoComponent(choreoComponent);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_ProxyApiEUDpIT"}, dataProvider = "dps")
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
    public void getDeploymentEnvironment_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        environments = GraphQL.getComponentDeploymentEnvironment(dp.getChoreoProject().getId(), accessToken);
        devEnv = choreoComponent.getEnvironment(environments, Constant.Environment.Development);
        prodEnv = choreoComponent.getEnvironment(environments, Constant.Environment.Production);
        dp.setDevEnv(devEnv);
        dp.setPrdEnv(prodEnv);
    }

    @Test(dependsOnMethods = {"getDeploymentEnvironment_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void initiateProxyDeployment_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException, NoLatestApiVersionFoundException {
        ProxyResponse<Status> statusProxyResponse = APICreator.initiateDeployment(dp.getChoreoComponent().getId(), dp.getChoreoComponent().getLatestApiVersion().getId(), dp.getDevEnv().getId(), accessToken);
        Assert.assertEquals(statusProxyResponse.getResponse().getStatusCode(), HttpStatus.OK.value());
        Assert.assertTrue(statusProxyResponse.getEntity().isSuccess());

    }

    @Test(dependsOnMethods = {"initiateProxyDeployment_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void getProxyAPIBuilds_ProxyApiEUDpIT(DataProviderWrapper dp) throws NoLatestApiVersionFoundException {
        proxyAPIBuild = APICreator.getAPIBuilds(dp.getChoreoComponent().getId(), dp.getChoreoComponent().getLatestApiVersion().getId(), accessToken);
        dp.setProxyAPIBuild(proxyAPIBuild);
    }


    @Test(dependsOnMethods = {"getProxyAPIBuilds_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void deployProxyAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        String buildId = dp.getProxyAPIBuild().getBuilds()[0].getBuildId();
        ProxyResponse<Status> res = APICreator.deployProxyAPI(dp.getChoreoComponent().getId(), dp.getProxyAPI().getId(), buildId, dp.getDevEnv().getId(), accessToken);
        Assert.assertEquals(res.getResponse().getStatusCode(), HttpStatus.OK.value());
    }


    @Test(dependsOnMethods = {"deployProxyAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void promoteProxyAPI_ProxyApiEUDpIT(DataProviderWrapper dp) throws NoLatestApiVersionFoundException, IOException {
        String revisionId = proxyAPIBuild.getBuilds()[0].getRevisionId();
        String buildId = dp.getProxyAPIBuild().getBuilds()[0].getBuildId();
        APICreator.promoteProxyAPI(dp.getChoreoComponent().getId(), dp.getChoreoComponent().getLatestApiVersion().getId(), dp.getDevEnv().getId(), dp.getPrdEnv().getId(), buildId, accessToken);
    }

    @Test(dependsOnMethods = {"promoteProxyAPI_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void componentDevDeploymentStatus_ProxyApiEUDpIT(DataProviderWrapper dp) throws Exception {
        devInvokeBaseURL = GraphQL.getProxyAPIDeploymentDetails(dp.getChoreoComponent().getId(), dp.getChoreoComponent().getLatestApiVersion().getId(), dp.getDevEnv().getId(), accessToken).getInvokeUrl();
        prodInvokeBaseURL = GraphQL.getProxyAPIDeploymentDetails(dp.getChoreoComponent().getId(), dp.getChoreoComponent().getLatestApiVersion().getId(), dp.getPrdEnv().getId(), accessToken).getInvokeUrl();
        dp.setDevInvokeUrl(devInvokeBaseURL);
        dp.setProdInvokeUrl(prodInvokeBaseURL);
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testDevDeployment_ProxyApiEUDpIT(DataProviderWrapper dp) throws IOException {
        apiKey = APICreator.getAPIKey(dp.getProxyAPI().getId(), accessToken).getApikey();
        String devURL = dp.getDevInvokeUrl() + "/users";
        Response dev = HttpClientUtil.httpGET(devURL, "", apiKey);
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value());
        dp.setApiId(apiKey);
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiEUDpIT"}, dataProvider = "dps")
    @CitrusTest
    public void testProdDeployment_ProxyApiEUDpIT(DataProviderWrapper dp) {
        String devURL = dp.getProdInvokeUrl() + "/users";
        Response dev = HttpClientUtil.httpGET(devURL, "", dp.getApiKey());
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value());
    }
}
