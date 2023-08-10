package com.wso2.choreo.integration.tests.security.observability;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class ChoreoElevatedAccessCheck_Observability extends TestNGCitrusSpringSupport  {
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
    public void setup_ChoreoElevatedAccessCheck_Observability() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        ballerinaComponentObsId = Configuration.getConfig(ConfigDefinition.BALLERINA_COMPONENT_OBS_ID);
        ballerinaComponentVersionId = Configuration.getConfig(ConfigDefinition.BALLERINA_COMPONENT_VERSION_ID);
        byocComponentReleaseId = Configuration.getConfig(ConfigDefinition.BYOC_COMPONENT_RELEASE_ID);
        from = Configuration.getConfig(ConfigDefinition.FROM);
        to = Configuration.getConfig(ConfigDefinition.TO);
        ballerinaComponentModuleId = Configuration.getConfig(ConfigDefinition.BALLERINA_COMPONENT_MODULE_ID);
        ballerinaComponentEntryPointFuncModule = Configuration.getConfig(ConfigDefinition.
                BALLERINA_COMPONENT_ENTRYPOINT_FUNC_MODULE);
        ballerinaComponentEntryPointSvcName = Configuration.getConfig(ConfigDefinition.
                BALLERINA_COMPONENT_ENTRYPOINT_SVC_NAME);
        ballerinaComponentEntryPointFuncName = Configuration.getConfig(ConfigDefinition.
                BALLERINA_COMPONENT_ENTRYPOINT_FUNC_NAME);
        ballerinaComponentEntryPointFuncName = Configuration.getConfig(ConfigDefinition.
                BALLERINA_COMPONENT_ENTRYPOINT_FUNC_NAME);
        ballerinaComponentEntryPointResourceAccessor = Configuration.getConfig(ConfigDefinition.
                BALLERINA_COMPONENT_ENTRYPOINT_RESOURCE_ACCESSOR);
        environment = Configuration.getConfig(ConfigDefinition.ENVIRONMENT);
        ballerinaComponentProjectId = Configuration.getConfig(ConfigDefinition.BALLERINA_COMPONENT_PROJECT_ID);
        region = Configuration.getConfig(ConfigDefinition.OBS_REGION);
        releaseId = Configuration.getConfig(ConfigDefinition.OBS_RELEASE_ID);
        namespace = Configuration.getConfig(ConfigDefinition.OBS_NAMESPACE);
        limit = Configuration.getConfig(ConfigDefinition.OBS_LIMIT);
        bin = Configuration.getConfig(ConfigDefinition.OBS_BIN);
        sort = Configuration.getConfig(ConfigDefinition.OBS_SORT);
        interval = Configuration.getConfig(ConfigDefinition.OBS_INTERVAL);
        logLevel = Configuration.getConfig(ConfigDefinition.OBS_LOG_LEVEL);
        orgId = Configuration.getConfig(ConfigDefinition.OBS_ORG_ID);
        envId = Configuration.getConfig(ConfigDefinition.OBS_ENVIRONMENT_ID);
        componentId  = Configuration.getConfig(ConfigDefinition.OBS_COMPONENT_ID);
        apiId = Configuration.getConfig(ConfigDefinition.OBS_API_ID);
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentDiagram_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("obsId", ballerinaComponentObsId);
        params.put("version", ballerinaComponentVersionId);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForAst.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewBYOCComponentRequestMetrics_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("byocComponentReleaseId", byocComponentReleaseId);
        params.put("from", from);
        params.put("to", to);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/queryForBYOCComponentRequestMetrics.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentStats_ChoreoElevatedAccessCheck_Observability() throws Exception {
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
        $(http().
                client(choreoCPTestClient).
                send().
                post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewProjectServiceGraph_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("environment", environment);
        params.put("ballerinaComponentProjectId", ballerinaComponentProjectId);
        params.put("from", from);
        params.put("to", to);
        String body = MessageUtils.
                generateStringFromTemplate("templates/observability/graphql/" +
                        "queryForViewProjectServiceGraph.mustache", params);
        $(http().
                client(choreoCPTestClient).
                send().
                post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentFramegraph_ChoreoElevatedAccessCheck_Observability() throws Exception {
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
        $(http().
                client(choreoCPTestClient).
                send().
                post(Constant.OBSERVABILITY_OBS_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken).
                body(body));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewComponentGroupedLogs_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewComponentGroupedLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_GROUP_LOGS + "?startTime=" + from + "&endTime=" + to +
                "&region=" + region + "&releaseId=" + releaseId + "&namespace=" + namespace +
                "&limit=" + limit + "&bin=" + bin;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewComponentGroupedLogs).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewComponentLogs_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewComponentLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_LOGS + "?startTime=" + from + "&region=" + region +
                "&endTime=" + to + "&releaseId=" + releaseId +
                "&namespace=" + namespace + "&limit=" + limit + "&sort=" + sort;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewComponentLogs).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewBallerinaComponentMetrics_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewBallerinaComponentMetrics = Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_METRICS + "?startTime=" + from +
                "&endTime=" + to + "&interval=" + interval + "&region=" + region +
                "&releaseId=" + releaseId + "&namespace=" + namespace;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewBallerinaComponentMetrics).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewOrganizationLogs_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewOrganizationLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_ORG_LOGS + "?startTime=" + from +
                "&region=" + region + "&endTime=" + to + "&namespace=" + namespace +
                "&logLevel=" + logLevel + "&limit=" + limit + "&sort=" + sort;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewOrganizationLogs).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewProjectLogs_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewProjectLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_PROJECT_LOGS + "?startTime=" + from +
                "&endTime=" + to + "&projectId=" + ballerinaComponentProjectId +
                "&namespace=" + namespace + "&region=" + region + "&environment=" + environment +
                "&limit=" + limit + "&sort=" + sort;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewProjectLogs).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewAuditLogs_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForViewAuditLogs = Constant.OBSERVABILITY_AUDIT_LOGS + "/orgs/" + orgId +
                "/audit-logs?startTime=" + from + "&endTime=" + to;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewAuditLogs).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void viewProxyAPIComponentGatewayAccessLogs_ChoreoElevatedAccessCheck_Observability() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_CP_GW_ENDPOINT);
        String requestUrlForViewProxyAPIComponentGatewayAccessLogs = Constant.OBSERVABILITY_LOGS_ENDPOINT_SUFFIX +
                Constant.OBSERVABILITY_GATEWAY_ACCESS_LOGS + "?startTime=" + from +
                "&endTime=" + to + "&environmentId=" + envId +
                "&componentId=" + componentId + "&apiId=" + apiId +
                "&region=" + region + "&logType=singleLine&limit=" + limit + "&sort=" + sort;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForViewProxyAPIComponentGatewayAccessLogs).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http().
                client(choreoCPTestClient).
                receive().
                response(HttpStatus.UNAUTHORIZED).
                message().
                type(MessageType.JSON));
    }
}
