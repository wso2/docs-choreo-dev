package com.wso2.choreo.integration.tests.security.devOps.Clusters;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class ClustersElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;
    private static String componentId;
    private static String orgId;
    private static String projectId;
    private static String tokenId;
    private static String namespace;
    private static String clusterId;
    private static String orgIntId;

    @BeforeClass
    public void setup_ClustersElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getConfig(ConfigDefinition.DEVOPS_COMPONENT_ID);
        orgId = Configuration.getConfig(ConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getConfig(ConfigDefinition.DEVOPS_PROJECT_ID);
        tokenId = Configuration.getConfig(ConfigDefinition.DEVOPS_TOKEN_ID);
        namespace = Configuration.getConfig(ConfigDefinition.DEVOPS_NAMESPACE);
        clusterId = Configuration.getConfig(ConfigDefinition.DEVOPS_CLUSTER_ID);
        orgIntId = Configuration.getConfig(ConfigDefinition.DEVOPS_ORG_INT_ID);
    }

    @Test
    @CitrusTest
    public void getKind_ClustersElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetToken = Constant.DEVOPS_CLUSTERS +
                "/" + clusterId + "/query/v1/Pod?organization_id=" + orgId +
                "&project_id=" + projectId + "&namespace=" + namespace + "&name=&labelSelector=&fieldSelector=&limit=0";
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetToken).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void postPodLogs_ClustersElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetToken = Constant.DEVOPS_CLUSTERS +
                "/" + clusterId + "/pod/logs?organization_id=" + orgId +
                "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                post(requestUrlForGetToken).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    @Test
    @CitrusTest
    public void getDataplanes_ClustersElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDataplanes = Constant.DEVOPS_CLUSTERS +
                "/dataplanes?org_id=" + orgIntId + "&project_id=" + projectId;
        $(http().
                client(choreoCPTestClient).
                send().
                get(requestUrlForGetDataplanes).
                message().
                header(HttpHeaders.ACCEPT, "*/*").
                header(HttpHeaders.AUTHORIZATION, accessToken));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }
}
