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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * deployment related tests.
 */
public class DeployIT extends TestNGCitrusSpringSupport {
    private String accessToken;
    private ChoreoComponent restApiComponent;


    @BeforeClass
    public void setup_DeployIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        restApiComponent = ComponentUtils.createRestAPI(accessToken);
    }

    @Test
    @CitrusTest
    public void addDeploymentConfiguration_DeployIT() throws Exception {
        Orgs.addConfiguration( restApiComponent, "dev",accessToken);
    }

    @Test(dependsOnMethods = {"addDeploymentConfiguration_DeployIT"})
    @CitrusTest
    public void deploy_DeployIT() throws Exception {
        GraphQL.deployComponent(restApiComponent,accessToken);
    }

    @Test(dependsOnMethods = {"deploy_DeployIT"})
    @CitrusTest
    public void deploymentStatusByVersion_DeployIT() throws Exception {
        GraphQL.deploymentStatusByVersion(restApiComponent,accessToken);
    }

    @Test(dependsOnMethods = {"deploymentStatusByVersion_DeployIT"})
    @CitrusTest
    public void componentDevDeploymentStatus_DeployIT() throws Exception {
        GraphQL.componentDeployment( restApiComponent, "dev",accessToken);
    }

    @Test(dependsOnMethods = {"componentDevDeploymentStatus_DeployIT"})
    @CitrusTest
    public void apiDevInvocation_DeployIT() throws Exception {
        ComponentUtils.invokeApiEndpoint(accessToken, restApiComponent, Environment.Development);
    }

    @Test(dependsOnMethods = {"apiDevInvocation_DeployIT"})
    @CitrusTest
    public void addPromoteConfiguration_DeployIT() throws Exception {
        Orgs.addConfiguration(restApiComponent, "prod", accessToken);
    }

    @Test(dependsOnMethods = {"addPromoteConfiguration_DeployIT"})
    @CitrusTest
    public void promote_DeployIT() throws Exception {
        GraphQL.promoteComponent( restApiComponent,accessToken);
    }

    @Test(dependsOnMethods = {"promote_DeployIT"})
    @CitrusTest
    public void componentProdDeploymentStatus_DeployIT() throws Exception {
        GraphQL.componentDeployment( restApiComponent, "prod",accessToken);
    }

    @Test(dependsOnMethods = {"componentProdDeploymentStatus_DeployIT"})
    @CitrusTest
    public void apiProdInvocation_DeployIT() throws Exception {
        ComponentUtils.invokeApiEndpoint(accessToken, restApiComponent, Environment.Production);
    }
}
