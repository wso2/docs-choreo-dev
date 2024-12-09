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

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.models.devops.Dataplane;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplate;
import com.wso2.choreo.integration.models.devops.EnvironmentTemplatesListDTO;
import com.wso2.choreo.integration.models.devops.EnvironmentWithClusters;
import com.wso2.choreo.integration.models.devops.EnvironmentWithClustersListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
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

public class TestOrgEnvironments extends TestNGCitrusSpringSupport {

    private String orgHandle;
    private int orgId;
    private String orgUUID;
    private static String accessToken;
    private String projectId;
    private String environmentName;
    private String envTemplateId;
    private String dnsPrefix;
    private String region;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @Autowired
    private HttpClient choreoProjectsTestClient;

    @BeforeClass
    public void setup_TestOrgEnvironments()
            throws Exception {

        orgHandle = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_HANDLE);
        orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_ID));
        orgUUID = Configuration.getConfig(ConfigDefinition.SELF_SIGNUP_ORG_UUID);
        accessToken = TestContext.getSelfSignupTestAdminUserTokenHandler().getTestTokenForCPAPIs();
    }

    @Test
    @CitrusTest
    public void begin_TestOrgEnvironments_EU() throws Exception {
        region = "EU";
        environmentName = "test-" + String.format("%03x", new Random().nextInt(4096));
        dnsPrefix = "dns-" + String.format("%03x", new Random().nextInt(4096));

        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, region, orgId, orgHandle);
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"begin_TestOrgEnvironments_EU"})
    @CitrusTest
    public void createEnvironment_TestOrgEnvironments_EU() throws Exception {
        String clusterId = "";
        List<Dataplane> dataplanes = DevopsPortalApi.getDataplaneList(this, accessToken, orgUUID);

        for (Dataplane dataplane : dataplanes) {
            if (dataplane.getName().contains(region)) {
                clusterId = dataplane.getId();
                break;
            }
        }

        Assert.assertNotEquals(clusterId, "", "[EU] Cluster ID not found for region: " + region);
        DevopsPortalApi.createOrgEnvironment(this, accessToken, orgUUID, environmentName, clusterId, dnsPrefix, false);
    }

    @Test(dependsOnMethods = {"createEnvironment_TestOrgEnvironments_EU"})
    @CitrusTest
    public void getEnvironmentTemplatesAfterCreate_TestOrgEnvironments_EU() throws Exception {
        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        Assert.assertNotNull(environmentTemplates);
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (environmentTemplate.getEnvName().equals(environmentName)) {
                envTemplateId = String.valueOf(environmentTemplate.getId());
                return;
            }
        }
        Assert.fail("[EU] Environment template not found for environment: " + environmentName);
    }

    @Test(dependsOnMethods = {"getEnvironmentTemplatesAfterCreate_TestOrgEnvironments_EU"})
    @CitrusTest
    public void getProjectEnvironments_TestOrgEnvironments_EU() throws Exception {
        EnvironmentWithClustersListDTO environments =
                DevopsPortalApi.getEnvironmentsWithClusters(this, accessToken, orgUUID, projectId);
        Assert.assertNotNull(environments);
        List<EnvironmentWithClusters> environmentList = environments.getData();
        for (EnvironmentWithClusters environment : environmentList) {
            if (environment.getName().equals(environmentName)) {
                return;
            }
        }
        Assert.fail("[EU] Environment not found for project: " + projectId);
    }

    @Test(dependsOnMethods = {"getProjectEnvironments_TestOrgEnvironments_EU"})
    @CitrusTest
    public void deleteEnvironment_TestOrgEnvironments_EU() throws Exception {
        DevopsPortalApi.deleteOrgEnvironment(this, accessToken, orgUUID, envTemplateId);
        // waiting for the environment to be deleted
        TimeUnit.MINUTES.sleep(2);
    }

    @Test(dependsOnMethods = {"deleteEnvironment_TestOrgEnvironments_EU"})
    @CitrusTest
    public void getEnvironmentTemplatesAfterDelete_TestOrgEnvironments_EU() throws Exception {
        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        Assert.assertNotNull(environmentTemplates);
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (environmentTemplate.getEnvName().equals(environmentName)) {
                Assert.fail("[EU] Environment template found for environment: " + environmentName);
            }
        }
    }

    @Test(dependsOnMethods = {"getEnvironmentTemplatesAfterDelete_TestOrgEnvironments_EU"})
    @CitrusTest
    public void begin_TestOrgEnvironments_US() throws Exception {
        region = "US";
        environmentName = "test-" + String.format("%03x", new Random().nextInt(4096));
        dnsPrefix = "dns-" + String.format("%03x", new Random().nextInt(4096));

        ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, region, orgId, orgHandle);
        projectId = project.getId();
    }

    @Test(dependsOnMethods = {"begin_TestOrgEnvironments_US"})
    @CitrusTest
    public void createEnvironment_TestOrgEnvironments_US() throws Exception {
        String clusterId = "";
        List<Dataplane> dataplanes = DevopsPortalApi.getDataplaneList(this, accessToken, orgUUID);

        for (Dataplane dataplane : dataplanes) {
            if (dataplane.getName().contains(region)) {
                clusterId = dataplane.getId();
                break;
            }
        }

        Assert.assertNotEquals(clusterId, "", "[US] Cluster ID not found for region: " + region);
        DevopsPortalApi.createOrgEnvironment(this, accessToken, orgUUID, environmentName, clusterId, dnsPrefix, false);
    }

    @Test(dependsOnMethods = {"createEnvironment_TestOrgEnvironments_US"})
    @CitrusTest
    public void getEnvironmentTemplatesAfterCreate_TestOrgEnvironments_US() throws Exception {
        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        Assert.assertNotNull(environmentTemplates);
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (environmentTemplate.getEnvName().equals(environmentName)) {
                envTemplateId = String.valueOf(environmentTemplate.getId());
                return;
            }
        }
        Assert.fail("[US] Environment template not found for environment: " + environmentName);
    }

    @Test(dependsOnMethods = {"getEnvironmentTemplatesAfterCreate_TestOrgEnvironments_US"})
    @CitrusTest
    public void getProjectEnvironments_TestOrgEnvironments_US() throws Exception {
        EnvironmentWithClustersListDTO environments = DevopsPortalApi.getEnvironmentsWithClusters(this, accessToken, orgUUID, projectId);
        Assert.assertNotNull(environments);
        List<EnvironmentWithClusters> environmentList = environments.getData();
        for (EnvironmentWithClusters environment : environmentList) {
            if (environment.getName().equals(environmentName)) {
                return;
            }
        }
        Assert.fail("[US] Environment not found for project: " + projectId);
    }

    @Test(dependsOnMethods = {"getProjectEnvironments_TestOrgEnvironments_US"})
    @CitrusTest
    public void deleteEnvironment_TestOrgEnvironments_US() throws Exception {
        DevopsPortalApi.deleteOrgEnvironment(this, accessToken, orgUUID, envTemplateId);
        // waiting for the environment to be deleted
        TimeUnit.MINUTES.sleep(2);
    }

    @Test(dependsOnMethods = {"deleteEnvironment_TestOrgEnvironments_US"})
    @CitrusTest
    public void getEnvironmentTemplatesAfterDelete_TestOrgEnvironments_US() throws Exception {
        EnvironmentTemplatesListDTO environmentTemplatesListDTO =
                DevopsPortalApi.getEnvironmentTemplates(this, accessToken, orgId);
        Assert.assertNotNull(environmentTemplatesListDTO);
        List<EnvironmentTemplate> environmentTemplates = environmentTemplatesListDTO.getData();
        Assert.assertNotNull(environmentTemplates);
        for (EnvironmentTemplate environmentTemplate : environmentTemplates) {
            if (environmentTemplate.getEnvName().equals(environmentName)) {
                Assert.fail("[US] Environment template found for environment: " + environmentName);
            }
        }
    }
}
