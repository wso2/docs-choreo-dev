/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package com.wso2.choreo.integration.tests.environments;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.wso2.choreo.integration.models.devops.Dataplane;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplate;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplatesListDTO;
import com.wso2.choreo.integration.tests.dp.DataProviderWrapper;
import com.wso2.choreo.integration.tests.dp.TestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import org.testng.Assert;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;

public class TestOrgEnvironments extends TestBase {

    private String orgHandle;
    private int orgId;
    private String orgUUID;
    private static String accessToken;
    private String projectId;
    private String environmentName;
    private String dnsPrefix;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @DataProvider(name = "dps")
    public Object[][] provideData() {
        return this.setUp();
    }

    @BeforeClass
    public void setup_TestOrgEnvironments()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        orgUUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        environmentName = "test-" + String.format("%03x", new Random().nextInt(4096));
        dnsPrefix = "dns-" + String.format("%03x", new Random().nextInt(4096));
    }

    @Test(dataProvider = "dps")
    @CitrusTest
    public void createProject_TestOrgEnvironments(DataProviderWrapper dp) throws Exception {
        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"createEnvironment_TestOrgEnvironments"}, dataProvider = "dps")
    @CitrusTest
    public void createEnvironment_TestOrgEnvironments(DataProviderWrapper dp) throws Exception {
        String environmentName = "test-" + String.format("%03x", new Random().nextInt(4096));
        String dnsPrefix = "dns-" + String.format("%03x", new Random().nextInt(4096));
        String clusterId = "";
        List<Dataplane> dataplanes = DevopsPortalApi.getDataplaneList(this, accessToken, orgUUID);

        for (Dataplane dataplane : dataplanes) {
            if (dataplane.getName().contains(dp.getRegion())) {
                clusterId = dataplane.getId();
                break;
            }
        }

        Assert.assertNotEquals(clusterId, "", "Cluster ID not found for region: " + dp.getRegion());
        DevopsPortalApi.createOrgEnvironment(this, accessToken, orgUUID, environmentName, clusterId, dnsPrefix, false);
    }

    @Test(dependsOnMethods = {"createEnvironment_TestOrgEnvironments"}, dataProvider = "dps")
    @CitrusTest
    public void getEnvironmentTemplatesAfterCreate_TestOrgEnvironments(DataProviderWrapper dp) throws Exception {
        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = EnvironmentTemplatesListDTO.builder().build().getData();
        Assert.assertNotNull(environmentTemplates);
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (environmentTemplate.getEnvName().equals(environmentName)) {
                return;
            }
        }
        Assert.fail("Environment template not found for environment: " + environmentName);
    }

    @Test(dependsOnMethods = {"createEnvironment_TestOrgEnvironments"}, dataProvider = "dps")
    public void deleteEnvironment_TestOrgEnvironments(DataProviderWrapper dp) throws Exception {
        DevopsPortalApi.deleteOrgEnvironment(this, accessToken, orgUUID, environmentName);
        // waiting for the environment to be deleted
        TimeUnit.MINUTES.sleep(2);
    }

    @Test(dependsOnMethods = {"deleteEnvironment_TestOrgEnvironments"}, dataProvider = "dps")
    public void getEnvironmentTemplatesAfterDelete_TestOrgEnvironments(DataProviderWrapper dp) throws Exception {
        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = EnvironmentTemplatesListDTO.builder().build().getData();
        Assert.assertNotNull(environmentTemplates);
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (environmentTemplate.getEnvName().equals(environmentName)) {
                Assert.fail("Environment template found for environment: " + environmentName);
            }
        }
    }

}
