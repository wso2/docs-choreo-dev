package com.wso2.choreo.integration.tests.security.deliveryInsights;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class DeliveryInsightsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String dataPlaneId;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_DeliveryInsightsElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        dataPlaneId = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_DATAPLANE_ID);
    }

    @Test
    @CitrusTest
    public void addIncidentScrapperConfigurations_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForAddIncidentScrapperConfigurations = "/cio-incident-configurator/1.0.0/configurations/github";
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("dataPlaneId", dataPlaneId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForAddIncidentScrapperConfigurations.mustache", params);
        SecurityUtils.forbiddenCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForAddIncidentScrapperConfigurations, body, accessToken);

    }
}
