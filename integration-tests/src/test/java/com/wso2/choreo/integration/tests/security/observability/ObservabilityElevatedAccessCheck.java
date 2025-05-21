package com.wso2.choreo.integration.tests.security.observability;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class ObservabilityElevatedAccessCheck extends TestNGCitrusSpringSupport  {
    private static String accessToken;
    private static String ballerinaComponentObsId;
    private static String ballerinaComponentVersionId;
    private static String byocComponentReleaseId;
    private static String from;
    private static String to;
    private static String ballerinaComponentModuleId;
    private static String ballerinaComponentEntryPointFuncModule;
    private static String ballerinaComponentEntryPointSvcName;
    private static String ballerinaComponentEntryPointFuncName;
    private static String ballerinaComponentEntryPointResourceAccessor;
    private static String environment;
    private static String ballerinaComponentProjectId;
    private static String region;
    private static String releaseId;
    private static String namespace;
    private static String limit;
    private static String bin;
    private static String sort;
    private static String interval;
    private static String logLevel;
    private static String orgId;
    private static String envId;
    private static String componentId;
    private static String apiId;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ObservabilityElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        ballerinaComponentObsId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_OBSERVEID);
        ballerinaComponentVersionId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_VERSION_ID);
        byocComponentReleaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_BYOC_COMPONENT_RELEASE_ID);
        from = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_FROM);
        to = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_TO);
        ballerinaComponentModuleId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_MODULE_ID);
        ballerinaComponentEntryPointFuncModule = Configuration.getSecurityConfig(SecurityConfigDefinition.
                OBS_ENTRYPOINT_FUNC_MODULE);
        ballerinaComponentEntryPointSvcName = Configuration.getSecurityConfig(SecurityConfigDefinition.
                OBS_ENTRYPOINT_SVC_NAME);
        ballerinaComponentEntryPointFuncName = Configuration.getSecurityConfig(SecurityConfigDefinition.
                OBS_ENTRYPOINT_FUNC_NAME);
        ballerinaComponentEntryPointFuncName = Configuration.getSecurityConfig(SecurityConfigDefinition.
                OBS_ENTRYPOINT_FUNC_NAME);
        ballerinaComponentEntryPointResourceAccessor = Configuration.getSecurityConfig(SecurityConfigDefinition.
                OBS_ENTRYPOINT_RESOURCE_ACCESSOR);
        environment = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_ENVIRONMENT);
        ballerinaComponentProjectId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_PROJECT_ID);
        region = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_REGION);
        releaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_RELEASE_ID);
        namespace = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_NAMESPACE);
        limit = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_LIMIT);
        bin = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_BIN);
        sort = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_SORT);
        interval = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_INTERVAL);
        logLevel = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_LOG_LEVEL);
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_ORG_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_ENVIRONMENT_ID);
        componentId  = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_COMPONENT_ID);
        apiId = Configuration.getSecurityConfig(SecurityConfigDefinition.OBS_API_ID);
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentDiagram_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("obsId", ballerinaComponentObsId);
        params.put("version", ballerinaComponentVersionId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForAst.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX, body, accessToken);
    }

    @Test
    @CitrusTest
    public void viewBYOCComponentRequestMetrics_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("byocComponentReleaseId", byocComponentReleaseId);
        params.put("from", from);
        params.put("to", to);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForBYOCComponentRequestMetrics.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX, body, accessToken);
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentStats_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("ballerinaComponentObsId", ballerinaComponentObsId);
        params.put("ballerinaComponentVersionId", ballerinaComponentVersionId);
        params.put("ballerinaComponentModuleId", ballerinaComponentModuleId);
        params.put("ballerinaComponentEntryPointFuncModule", ballerinaComponentEntryPointFuncModule);
        params.put("ballerinaComponentEntryPointSvcName", ballerinaComponentEntryPointSvcName);
        params.put("ballerinaComponentEntryPointFuncName", ballerinaComponentEntryPointFuncName);
        params.put("ballerinaComponentEntryPointResourceAccessor", ballerinaComponentEntryPointResourceAccessor);
        params.put("from", from);
        params.put("to", to);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/" +
                        "queryForBallerinaComponentStats.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX, body, accessToken);
    }

    @Test
    @CitrusTest
    public void viewProjectServiceGraph_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("environment", environment);
        params.put("ballerinaComponentProjectId", ballerinaComponentProjectId);
        params.put("from", from);
        params.put("to", to);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/" +
                        "queryForViewProjectServiceGraph.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX, body, accessToken);
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentFramegraph_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("ballerinaComponentObsId", ballerinaComponentObsId);
        params.put("versionId", ballerinaComponentVersionId);
        params.put("ballerinaComponentModuleId", ballerinaComponentModuleId);
        params.put("from", from);
        params.put("to", to);
        params.put("ballerinaComponentEntryPointFuncModule", ballerinaComponentEntryPointFuncModule);
        params.put("ballerinaComponentEntryPointSvcName", ballerinaComponentEntryPointSvcName);
        params.put("ballerinaComponentEntryPointFuncName", ballerinaComponentEntryPointFuncName);
        params.put("ballerinaComponentEntryPointResourceAccessor", ballerinaComponentEntryPointResourceAccessor);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/" +
                        "queryForViewBallerinaComponentFramegraph.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX, body, accessToken);
    }

    @Test
    @CitrusTest
    public void viewComponentGroupedLogs_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewComponentGroupedLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_GROUP_LOGS + "?startTime=" + from + "&endTime=" + to +
                "&region=" + region + "&releaseId=" + releaseId + "&namespace=" + namespace +
                "&limit=" + limit + "&bin=" + bin;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewComponentGroupedLogs, accessToken);
    }

    @Test
    @CitrusTest
    public void viewComponentLogs_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewComponentLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_LOGS + "?startTime=" + from + "&region=" + region +
                "&endTime=" + to + "&releaseId=" + releaseId +
                "&namespace=" + namespace + "&limit=" + limit + "&sort=" + sort;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewComponentLogs, accessToken);
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentMetrics_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewBallerinaComponentMetrics = Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_METRICS + "?startTime=" + from +
                "&endTime=" + to + "&interval=" + interval + "&region=" + region +
                "&releaseId=" + releaseId + "&namespace=" + namespace;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewBallerinaComponentMetrics, accessToken);
    }

    @Test
    @CitrusTest
    public void viewOrganizationLogs_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewOrganizationLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_ORG_LOGS + "?startTime=" + from +
                "&region=" + region + "&endTime=" + to + "&namespace=" + namespace +
                "&logLevel=" + logLevel + "&limit=" + limit + "&sort=" + sort;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewOrganizationLogs, accessToken);
    }

    @Test
    @CitrusTest
    public void viewProjectLogs_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewProjectLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_PROJECT_LOGS + "?startTime=" + from +
                "&endTime=" + to + "&projectId=" + ballerinaComponentProjectId +
                "&namespace=" + namespace + "&region=" + region + "&environment=" + environment +
                "&limit=" + limit + "&sort=" + sort;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewProjectLogs, accessToken);
    }

    @Test
    @CitrusTest
    public void viewAuditLogs_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForViewAuditLogs = Constant.OBSERVABILITY_AUDIT_LOGS +
                "/audit-logs?startTime=" + from + "&endTime=" + to;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewAuditLogs, accessToken);
    }

    @Test
    @CitrusTest
    public void viewProxyAPIComponentGatewayAccessLogs_ObservabilityElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewProxyAPIComponentGatewayAccessLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_GATEWAY_ACCESS_LOGS + "?startTime=" + from +
                "&endTime=" + to + "&environmentId=" + envId +
                "&componentId=" + componentId + "&apiId=" + apiId +
                "&region=" + region + "&logType=singleLine&limit=" + limit + "&sort=" + sort;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForViewProxyAPIComponentGatewayAccessLogs, accessToken);
    }
}
