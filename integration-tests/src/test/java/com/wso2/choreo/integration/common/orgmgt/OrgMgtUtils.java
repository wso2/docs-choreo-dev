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

package com.wso2.choreo.integration.common.orgmgt;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.orgmgt.OrgManagement;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.orgmgt.ApprovalRequestList;
import com.wso2.choreo.integration.models.orgmgt.ApprovalStatus;
import com.wso2.choreo.integration.models.orgmgt.SelfSignupConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Utility class for Organization management related tests.
 */
public class OrgMgtUtils {

    public static SelfSignupConfig updateSelfSignupConfig(TestActionRunner runner,
                                                          Map<Endpoints, HttpClient> citrusClients, String accessToken,
                                                          String orgUuid, SelfSignupConfig selfSignupConfigRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        return OrgManagement.updateSelfSignupConfig(runner, choreoCPTestClient, accessToken, orgUuid,
                selfSignupConfigRequest);
    }

    public static ApprovalStatus updateApprovalRequestStatus(TestActionRunner runner,
                                                             Map<Endpoints, HttpClient> citrusClients,
                                                             String accessToken, String orgUuid,
                                                             ApprovalStatus approvalRequestStatus)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        return OrgManagement.updateApprovalRequestStatus(runner, choreoCPTestClient, accessToken, orgUuid,
                approvalRequestStatus);
    }

    public static ApprovalRequestList getApprovalRequests(TestActionRunner runner,
                                                          Map<Endpoints, HttpClient> citrusClients,
                                                          String accessToken, String orgUuid)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        return OrgManagement.getApprovalRequests(runner, choreoCPTestClient, accessToken, orgUuid);
    }
}
