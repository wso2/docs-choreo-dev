package com.wso2.choreo.integration.tests.security.deliveryInsights;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
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
    private static String startTime;
    private static String endTime;
    private static String projectId;
    private static String orgName;
    private static String repoName;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_DeliveryInsightsElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        dataPlaneId = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_DATAPLANE_ID);
        startTime = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_START_TIME);
        endTime = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_END_TIME);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_PROJECT_ID);
        orgName = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_ORG_NAME);
        repoName = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_REPO_NAME);
    }

    @Test
    @CitrusTest
    public void addIncidentScrapperConfigurations_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForAddIncidentScrapperConfigurations = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github";
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("dataPlaneId", dataPlaneId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForAddIncidentScrapperConfigurations.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForAddIncidentScrapperConfigurations, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postOrganizationMemberCount_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForAddIncidentScrapperConfigurations = Constant.CIO_QUERY_API;
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostOrganizationMemberCount.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForAddIncidentScrapperConfigurations, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postDeploymentsTimeSeriesData_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentsTimeSeriesData = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostDeploymentsTimeSeriesData.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentsTimeSeriesData, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postDeploymentFrequencySummary_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentFrequencySummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostDeploymentFrequencySummary.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentFrequencySummary, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postLeadTimeSummaryData_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentFrequencySummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostLeadTimeSummaryData.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentFrequencySummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateIncidentConfigRepository_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentFrequencySummary = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github/" + orgId + "/repository";
        Map<String, String> params = new HashMap<>();
        params.put("org_name", orgName);
        params.put("repo_name", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForUpdateIncidentConfigRepository.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentFrequencySummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateIncidentConfigSelectorCriteria_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateIncidentConfigSelectorCriteria = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/" + orgId + "/selectorCriteria";
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForUpdateIncidentConfigSelectorCriteria.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateIncidentConfigSelectorCriteria, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateIncidentConfigRejectorCriteria_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateIncidentConfigRejectorCriteria = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/" + orgId + "/rejectorCriteria";
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForUpdateIncidentConfigRejectorCriteria.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateIncidentConfigRejectorCriteria, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getScraperConfigurations_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetScraperConfigurations = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github/console?orgId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetScraperConfigurations, accessToken);
    }

    @Test
    @CitrusTest
    public void postFailureRateSummary_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostFailureRateSummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostFailureRateSummary.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostFailureRateSummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postFailureRateDetails_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostFailureRateSummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostFailureRateDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostFailureRateSummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postRecoveryTimeDetails_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostRecoveryTimeDetails = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostRecoveryTimeDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostRecoveryTimeDetails, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postRecoveryTimeSummary_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostRecoveryTimeSummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostRecoveryTimeSummary.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostRecoveryTimeSummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postTopPerformingProjects_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostTopPerformingProjects = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostTopPerformingProjects.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostTopPerformingProjects, body, accessToken);
    }

    @Test
    @CitrusTest
    public void checkRepoAccessibility_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostTopPerformingProjects = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github/repo-accessibility?orgId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForPostTopPerformingProjects, accessToken);
    }

    @Test
    @CitrusTest
    public void getDataplanes_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDataplanes = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/dataplanes/" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetDataplanes, accessToken);
    }

    @Test
    @CitrusTest
    public void postActiveDeveloperCount_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostActiveDeveloperCount = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostActiveDeveloperCount.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostActiveDeveloperCount, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteScraperConfigs_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteScraperConfigs = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations?dashboardKind=innov-perf-github";
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForDeleteScraperConfigs, accessToken);
    }
}
