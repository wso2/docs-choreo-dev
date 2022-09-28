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

package com.wso2.choreo.integration.tests.observability;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.RestApiChoreoComponentBuilder;
import com.wso2.choreo.integration.common.exceptions.*;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class SysObsAPITestCase extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String namespace;
    private static String releaseId;
    private static String obsId;

    @Autowired
    private HttpClient choreoCPTestClient;

    @BeforeClass
    public void beforeClass()
            throws IOException, InterruptedException, ProjectCreationException, GetCommitHistoryException,
            NoLatestCommitHashFoundException, AddConfigurationsException, NoLatestAppEnvIdFoundException, GetDeploymentsStatusCheckException,
            ComponentCreationStatusCheckException, ComponentDeploymentException,
            ComponentDeploymentStatusCheckException, ComponentCreationException, ComponentRetrieveException,
            ApiLifecycleChangeException, ComponentCreationTimeoutException, ComponentDeploymentTimeoutException,
            NoLatestApiVersionFoundException, ComponentDeploymentFailureException, TokenRetrievalException,
            ComponentInvokeInformationCheckException, InvokeInformationNotFoundException, APIKeyGenerationCheckException, ApiKeyNotFoundException, InvokeAPICheckException, ReleaseIdNotFoundException, ObservabilityIdNotFoundException, ObservabilityIdCheckException, ObservabilityDataNotFoundException, EnvironmentDetailsCheckException, NamespaceNotFoundException, ObservabilityDataCheckException, ObservabilityLogsNotFoundException, URISyntaxException, ObservabilityLogsCheckException, ObservabilitySystemMetricsCheckException, ObservabilitySystemMetricsNotFoundException {
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        String orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        ChoreoOrganization org = new ChoreoOrganization(orgHandle, orgId, orgUuid);
        ChoreoProject project = org.createProject(accessToken);
        RestApiChoreoComponentBuilder restApiComponentBuilder = new RestApiChoreoComponentBuilder(project, org);
        RestApiChoreoComponent restApiComponent = (RestApiChoreoComponent) project.createChoreoComponent(accessToken, restApiComponentBuilder);
        restApiComponent.setProject(project);
        restApiComponent.setOrganization(org);
        restApiComponent.addConfigurations(accessToken, org.getOrgHandle(), Constant.DEV_ENVIRONMENT);
        restApiComponent.deploy(accessToken, org.getOrgHandle(), org.getOrgUUID());
        restApiComponent.invokeGetApplication(accessToken, "restAPI", "Development", 4);
        releaseId = restApiComponent.getReleaseIdForEnvironment("dev");
        namespace = restApiComponent.getNamespaceForEnvironment(accessToken, "dev");
        obsId = restApiComponent.getComponentObservabilityIdForReleaseId(accessToken, releaseId).getObsId();
        restApiComponent.waitForObservabilitySystemMetrics(accessToken, obsId, releaseId, namespace);

    }

    @Test
    @CitrusTest
    public void testSystemMetrics() {
        String requestPath = Constant.OBSERVABILITY_SYS_OBS_ENDPOINT_SUFFIX
                .concat(obsId)
                .concat("/metricsV2");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        $(http()
                .client(choreoCPTestClient)
                .send()
                .get(requestPath)
                .message()
                .queryParam("startTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusSeconds(60 * 60 * 24)))
                .queryParam("endTime", fmt.format(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)))
                .queryParam("interval", "15")
                .queryParam("releaseId", releaseId)
                .queryParam("namespace", namespace)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        $(http()
                .client(choreoCPTestClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.keySet()", hasItems("columns", "rows"))
                        .expression("$.columns[*].name", hasItems("cpu", "memory", "cpuPercentage", "memoryPercentage", "TimeGenerated"))
                        .expression("$.columns[*].type", hasItems("dynamic", "dynamic", "dynamic", "dynamic", "dynamic"))
                        .expression("$.rows.size()", greaterThan(0))
                        .expression("$.rows[*]", allOf(is(not(emptyString()))))
                )
        );
    }
}
