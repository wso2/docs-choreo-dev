package com.wso2.choreo.integration.tests.security.devOps.Clusters;

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

public class DevOpsClustersElevatedAccessCheck extends TestNGCitrusSpringSupport {
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
    private static String podName;
    private static String containerName;

    @BeforeClass
    public void setup_DevOpsClustersElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_COMPONENT_ID);
        orgId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_ID);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_PROJECT_ID);
        tokenId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_TOKEN_ID);
        namespace = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_NAMESPACE);
        clusterId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_CLUSTER_ID);
        orgIntId = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_ORG_INT_ID);
        podName = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_POD_NAME);
        containerName = Configuration.getSecurityConfig(SecurityConfigDefinition.DEVOPS_CONTAINER_NAME);
    }

    @Test
    @CitrusTest
    public void getKind_DevOpsClustersElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetToken = Constant.DEVOPS_CLUSTERS +
                "/" + clusterId + "/query/v1/Pod?organization_id=" + orgId +
                "&project_id=" + projectId + "&namespace=" + namespace + "&name=&labelSelector=&fieldSelector=&limit=0";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetToken,
                accessToken);
    }

    @Test
    @CitrusTest
    public void postPodLogs_DevOpsClustersElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetToken = Constant.DEVOPS_CLUSTERS +
                "/" + clusterId + "/pod/logs?organization_id=" + orgId +
                "&project_id=" + projectId;
        Map<String, String> params = new HashMap<>();
        params.put("namespace", namespace);
        params.put("pod_name", podName);
        params.put("container_name", containerName);
        String body = MessageUtils.
                generateStringFromTemplate("templates/devOps/queryForCreatePodLogs.mustache", params);
    }

    @Test
    @CitrusTest
    public void getDataplanes_DevOpsClustersElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDataplanes = Constant.DEVOPS_CLUSTERS +
                "/dataplanes?org_id=" + orgIntId + "&project_id=" + projectId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetDataplanes,
                accessToken);
    }
}
