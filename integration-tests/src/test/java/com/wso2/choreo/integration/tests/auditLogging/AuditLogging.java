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

package com.wso2.choreo.integration.tests.auditLogging;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.auditLogging.AuditLoggingUtils;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import com.wso2.choreo.integration.config.TimeRangeISO;
import com.wso2.choreo.integration.models.auditLogging.AuditLogList;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class AuditLogging extends TestNGCitrusSpringSupport {

    private static final String GRAPHQL_API_AUDIT_ACTION = "initiate deployment";
    private static final String RUNTIME_API_AUDIT_ACTION = "create and update configurations";
    private static final String APIM_PROXY_DEPLOYER_API_AUDIT_ACTION = "initiate component deployment";
    private static final String APIM_API_AUDIT_ACTION = "update API definition";

    private String orgUuid;
    private String accessToken;
    private String userIdpId;
    private ChoreoProject projectA;

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_AuditLoggingTests() throws Exception {
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        userIdpId = Configuration.getConfig(ConfigDefinition.TEST_USER_IDP_ID);
    }

    @Test
    @CitrusTest
    public void createProject_AuditLoggingTests() throws Exception {
        projectA = ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.US.toString());
    }

    @Test
    @CitrusTest
    public void publishGraphQLAPIAuditEvents_AuditLoggingTests() throws Exception {
        projectA = ComponentUtils.createProject(this, citrusClients, accessToken, Constant.region.US.toString());
        TimeRangeISO timeRangeISO = AuditLoggingUtils.getTimeRangeISO(600);
        AuditLogList auditLogList = AuditLoggingUtils.filterAuditLogsByTime(this, citrusClients, orgUuid,
                timeRangeISO);

        boolean actionAndTypeFound = auditLogList.getList().stream()
                .anyMatch(auditLog -> GRAPHQL_API_AUDIT_ACTION.equals(auditLog.getAction())
                        && "Component".equals(auditLog.getEntityType()));
        Assert.assertTrue(actionAndTypeFound);
    }

    @Test
    @CitrusTest
    public void publishRuntimeAPIAuditEvents_AuditLoggingTests() throws Exception {
        TimeRangeISO timeRangeISO = AuditLoggingUtils.getTimeRangeISO(600);
        AuditLogList auditLogList = AuditLoggingUtils.filterAuditLogsByTime(this, citrusClients, orgUuid,
                timeRangeISO);

        boolean actionFound = auditLogList.getList().stream()
                .anyMatch(auditLog -> RUNTIME_API_AUDIT_ACTION.equals(auditLog.getAction()));
        Assert.assertTrue(actionFound);
    }

    @Test
    @CitrusTest
    public void publishAPIMProxyDeployerAPIAuditEvents_AuditLoggingTests() throws Exception {
        TimeRangeISO timeRangeISO = AuditLoggingUtils.getTimeRangeISO(600);
        AuditLogList auditLogList = AuditLoggingUtils.filterAuditLogsByTime(this, citrusClients, orgUuid,
                timeRangeISO);

        boolean actionAndComponentTypeFound = auditLogList.getList().stream()
                .anyMatch(auditLog -> APIM_PROXY_DEPLOYER_API_AUDIT_ACTION.equals(auditLog.getAction())
                        && auditLog.getInfo() != null
                        && "proxy".equals(auditLog.getInfo().get("componentType")));
        Assert.assertTrue(actionAndComponentTypeFound);
    }

    @Test
    @CitrusTest
    public void publishAPIMAPIAuditEvents_AuditLoggingTests() throws Exception {
        TimeRangeISO timeRangeISO = AuditLoggingUtils.getTimeRangeISO(600);
        AuditLogList auditLogList = AuditLoggingUtils.filterAuditLogsByTime(this, citrusClients, orgUuid,
                timeRangeISO);

        boolean actionFound = auditLogList.getList().stream()
                .anyMatch(auditLog -> APIM_API_AUDIT_ACTION.equals(auditLog.getAction()));
        Assert.assertTrue(actionFound);
    }

    @Test
    @CitrusTest
    public void filterAuditLogsByOutcome_AuditLoggingTests() throws Exception {
        AuditLoggingUtils.filterAuditLogsByOutcome(this, citrusClients, orgUuid,
                List.of("succeeded", "failed"));
    }

    @Test
    @CitrusTest
    public void filterAuditLogsByUser_AuditLoggingTests() throws Exception {
        AuditLoggingUtils.filterAuditLogsByUser(this, citrusClients, orgUuid,
                List.of(userIdpId));
    }

    @Test(dependsOnMethods = {"createProject_AuditLoggingTests"})
    @CitrusTest
    public void filterAuditLogsByProject_AuditLoggingTests() throws Exception {
        SleepUtil.sleep(10); // Wait for the audit logs to be generated
        AuditLoggingUtils.filterAuditLogsByProject(this, citrusClients, orgUuid,
                List.of(projectA.getId()));
    }

    @Test
    @CitrusTest
    public void filterAuditLogsByTime_AuditLoggingTests() throws Exception {
        TimeRangeISO timeRangeISO = AuditLoggingUtils.getTimeRangeISO(600);
        AuditLoggingUtils.filterAuditLogsByTime(this, citrusClients, orgUuid,
                timeRangeISO);
    }
}
