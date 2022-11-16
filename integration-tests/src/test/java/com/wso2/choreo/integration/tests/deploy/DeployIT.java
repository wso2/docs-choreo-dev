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

package com.wso2.choreo.integration.tests.deploy;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.config.Constant.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * deployment related tests.
 */
public class DeployIT extends TestNGCitrusSpringSupport {
    private String accessToken;
    private ChoreoComponent restApiComponent;

    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void beforeClassDIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        restApiComponent = ComponentUtils.createRestAPI(accessToken);
    }

    @Test
    @CitrusTest
    public void testAddDeploymentConfigurationDIT() throws Exception {
        Orgs.addConfiguration(choreoTestClient, this, restApiComponent, "dev");
    }

    @Test(dependsOnMethods = {"testAddDeploymentConfigurationDIT"})
    @CitrusTest
    public void testDeployDIT() throws Exception {
        GraphQL.deployComponent(restApiComponent,accessToken);
    }

    @Test(dependsOnMethods = {"testDeployDIT"})
    @CitrusTest
    public void testDeploymentStatusByVersionDIT() throws Exception {
        GraphQL.deploymentStatusByVersion(restApiComponent,accessToken);
    }

    @Test(dependsOnMethods = {"testDeploymentStatusByVersionDIT"})
    @CitrusTest
    public void testComponentDevDeploymentStatusDIT() throws Exception {
        GraphQL.componentDeployment( restApiComponent, "dev",accessToken);
    }

    @Test(dependsOnMethods = {"testComponentDevDeploymentStatusDIT"})
    @CitrusTest
    public void testApiDevInvocationDIT() throws Exception {
        ComponentUtils.invokeApiEndpoint(accessToken, restApiComponent, Environment.Development);
    }

    @Test(dependsOnMethods = {"testApiDevInvocationDIT"})
    @CitrusTest
    public void testAddPromoteConfigurationDIT() throws Exception {
        Orgs.addConfiguration(restApiComponent, "prod", accessToken);
    }

    @Test(dependsOnMethods = {"testAddPromoteConfigurationDIT"})
    @CitrusTest
    public void testPromoteDIT() throws Exception {
        GraphQL.promoteComponent( restApiComponent,accessToken);
    }

    @Test(dependsOnMethods = {"testPromoteDIT"})
    @CitrusTest
    public void testComponentProdDeploymentStatusDIT() throws Exception {
        GraphQL.componentDeployment( restApiComponent, "prod",accessToken);
    }

    @Test(dependsOnMethods = {"testComponentProdDeploymentStatusDIT"})
    @CitrusTest
    public void testApiProdInvocationDIT() throws Exception {
        ComponentUtils.invokeApiEndpoint(accessToken, restApiComponent, Environment.Production);
    }
}
