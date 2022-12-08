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

package com.wso2.choreo.integration.tests.addConfigurations;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.Orgs;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * add configurations related tests.
 */
public class AddConfigurationsIT extends TestNGCitrusSpringSupport {
    private ChoreoComponent component;
    private String accessToken;
    @Autowired
    private HttpClient choreoTestClient;

    @BeforeClass
    public void setup_AddConfigurationsIT() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();

        component = ComponentUtils.getReusableComponent(TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs(),
                AddConfigurationsIT.class.getSimpleName());
    }


    @Test
    @CitrusTest
    public void addConfigurations_AddConfigurationsIT() throws Exception {
        Response response = Orgs.addConfiguration(component, "dev", accessToken);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK.value());
    }
}
