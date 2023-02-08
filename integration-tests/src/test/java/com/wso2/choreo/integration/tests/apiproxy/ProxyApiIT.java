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

package com.wso2.choreo.integration.tests.apiproxy;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
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
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.response.ProxyResponse;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;


public class ProxyApiIT extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String projectId;
    private static String firstAPIName;
    private static String firstContext;
    private ProxyAPI proxyAPI;

    Environment[] environments;
    Environment devEnv;
    Environment prodEnv;
    ChoreoComponent choreoComponent;
    ProxyAPIBuild proxyAPIBuild;
    String devInvokeBaseURL;
    String prodInvokeBaseURL;
    String apiKey;

    @BeforeClass
    public void setup_ProxyApiIT() throws IOException, TokenRetrievalException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        ChoreoProject testProject = GraphQL.createProject(accessToken);
        projectId = testProject.getId();
        firstAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        firstContext = APICreator.generateContext(firstAPIName);
    }


    @Test
    @CitrusTest
    public void verifyAPIName_ProxyApiIT() throws IOException {
        Response response = APICreator.validateAPIName(firstAPIName, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND.value());
    }

    @Test(dependsOnMethods = {"verifyAPIName_ProxyApiIT"})
    @CitrusTest
    public void createAPI_ProxyApiIT() throws IOException {
        proxyAPI = APICreator.createAPI(firstAPIName, firstContext, accessToken).getEntity();
        Assert.assertNotNull(proxyAPI.getId());
    }

    @Test(dependsOnMethods = {"createAPI_ProxyApiIT"})
    @CitrusTest
    public void testCreateComponentForProxyAPI_ProxyApiIT() throws IOException {
        ProxyResponse<ChoreoComponent> response = GraphQL.createGraphqlQueryForComponentCreation(firstAPIName, projectId, proxyAPI.getId(), accessToken);
        choreoComponent = response.getEntity();
        Assert.assertEquals(response.getResponse().getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testCreateComponentForProxyAPI_ProxyApiIT"})
    @CitrusTest
    public void componentRetrieval_ProxyApiIT() throws IOException {
        choreoComponent = GraphQL.getComponentDetails(projectId, choreoComponent.getHandler(), accessToken);
        Assert.assertNotNull(choreoComponent);
    }

    @Test(dependsOnMethods = {"componentRetrieval_ProxyApiIT"})
    @CitrusTest
    public void testExistingAPI_ProxyApiIT() throws IOException {
        Response response = APICreator.validateAPIName(firstAPIName, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testExistingAPI_ProxyApiIT"})
    @CitrusTest
    public void testAPIBasePathValidationForAPIProxyCreation_ProxyApiIT() throws IOException {
        // Create a unique API Name.
        String secondAPIName = Constant.DEFAULT_API_NAME.concat(String.valueOf(new Date().getTime()));
        Response response = APICreator.createAPI(secondAPIName, firstContext, accessToken).getResponse();
        Assert.assertEquals(response.getStatusCode(), HttpStatus.CONFLICT.value());

    }

    @Test(dependsOnMethods = {"testAPIBasePathValidationForAPIProxyCreation_ProxyApiIT"})
    @CitrusTest
    public void testUpdateSwagger_ProxyApiIT() throws IOException {
        Response response = APICreator.updateAPI(proxyAPI, accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"testUpdateSwagger_ProxyApiIT"})
    @CitrusTest
    public void getDeploymentEnvironment_ProxyApiIT() throws IOException {
        environments = GraphQL.getComponentDeploymentEnvironment(projectId, accessToken);
        devEnv = choreoComponent.getEnvironment(environments, Constant.Environment.Development);
        prodEnv = choreoComponent.getEnvironment(environments, Constant.Environment.Production);
    }

    @Test(dependsOnMethods = {"getDeploymentEnvironment_ProxyApiIT"})
    @CitrusTest
    public void initiateProxyDeployment_ProxyApiIT() throws IOException, NoLatestApiVersionFoundException {
        ProxyResponse<Status> statusProxyResponse = APICreator.initiateDeployment(choreoComponent.getId(), choreoComponent.getLatestApiVersion().getId(), devEnv.getId(), accessToken);
        Assert.assertEquals(statusProxyResponse.getResponse().getStatusCode(), HttpStatus.OK.value());
        Assert.assertTrue(statusProxyResponse.getEntity().isSuccess());

    }

    @Test(dependsOnMethods = {"initiateProxyDeployment_ProxyApiIT"})
    @CitrusTest
    public void getProxyAPIBuilds_ProxyApiIT() throws NoLatestApiVersionFoundException {
        proxyAPIBuild = APICreator.getAPIBuilds(choreoComponent.getId(), choreoComponent.getLatestApiVersion().getId(), accessToken);
    }


    @Test(dependsOnMethods = {"getProxyAPIBuilds_ProxyApiIT"})
    @CitrusTest
    public void deployProxyAPI_ProxyApiIT() throws IOException {
        String buildId = proxyAPIBuild.getBuilds()[0].getBuildId();
        ProxyResponse<Status> res = APICreator.deployProxyAPI(choreoComponent.getId(), proxyAPI.getId(), buildId, devEnv.getId(), accessToken);
        Assert.assertEquals(res.getResponse().getStatusCode(), HttpStatus.OK.value());
    }


    @Test(dependsOnMethods = {"deployProxyAPI_ProxyApiIT"})
    @CitrusTest
    public void promoteProxyAPI_ProxyApiIT() throws NoLatestApiVersionFoundException, IOException {
        String revisionId = proxyAPIBuild.getBuilds()[0].getRevisionId();
        String buildId = proxyAPIBuild.getBuilds()[0].getBuildId();
        APICreator.promoteProxyAPI(choreoComponent.getId(), choreoComponent.getLatestApiVersion().getId(),devEnv.getId(), prodEnv.getId(), buildId,accessToken);
    }

    @Test(dependsOnMethods = {"promoteProxyAPI_ProxyApiIT"})
    @CitrusTest
    public void componentDevDeploymentStatus_ProxyApiIT() throws Exception {
        devInvokeBaseURL = GraphQL.getProxyAPIDeploymentDetails(choreoComponent.getId(), choreoComponent.getLatestApiVersion().getId(), devEnv.getId(), accessToken).getInvokeUrl();
        prodInvokeBaseURL = GraphQL.getProxyAPIDeploymentDetails(choreoComponent.getId(), choreoComponent.getLatestApiVersion().getId(), prodEnv.getId(), accessToken).getInvokeUrl();
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiIT"})
    @CitrusTest
    public void testDevDeployment_ProxyApiIT() throws IOException {
        apiKey = APICreator.getAPIKey(proxyAPI.getId(), accessToken).getApikey();
        String devURL = devInvokeBaseURL + "/users";
        Response dev = HttpClientUtil.httpGET(devURL, "", apiKey);
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value());
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_ProxyApiIT"})
    @CitrusTest
    public void testProdDeployment_ProxyApiIT() throws IOException {
        String devURL = prodInvokeBaseURL + "/users";
        Response dev = HttpClientUtil.httpGET(devURL, "", apiKey);
        Assert.assertEquals(dev.getStatusCode(), HttpStatus.OK.value());
    }
}
