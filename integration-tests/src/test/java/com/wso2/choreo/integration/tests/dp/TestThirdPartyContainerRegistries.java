package com.wso2.choreo.integration.tests.dp;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.devopsportalapi.ThirdPartyContainerRegistryDTO;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;

public class TestThirdPartyContainerRegistries extends TestBase {
    private static String accessToken;
    private String orgUUID;
    private ThirdPartyContainerRegistryDTO registry;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_TestThirdPartyContainerRegistries() throws Exception {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
    }

    @Test
    @CitrusTest
    public void createPublicThirdPartyRegistryCredentialCreation() throws Exception {
        // I need to create a string starting with E2E and have 4 char length 
        String name = "E2E " + String.valueOf(new Date().getTime());
        registry = DevopsPortalApi.createThirdPartyRegistryCredential(
            this, accessToken, orgUUID, name, "Docker Hub", "public", "registry.docker.com");
    }

    @Test(dependsOnMethods = {"createAndDeleteThirdPartyRegistryCredentialCreation"})
    @CitrusTest
    public void deletePublicThirdPartyRegistryCredentialCreation() throws Exception {
        DevopsPortalApi.deleteThirdPartyRegistryCredential(this, accessToken, orgUUID, registry.getId());
    }

}
