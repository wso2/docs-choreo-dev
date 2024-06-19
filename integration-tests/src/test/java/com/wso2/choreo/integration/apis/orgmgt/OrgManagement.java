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

package com.wso2.choreo.integration.apis.orgmgt;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.orgmgt.ApprovalRequestList;
import com.wso2.choreo.integration.models.orgmgt.ApprovalStatus;
import com.wso2.choreo.integration.models.orgmgt.SelfSignupConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Service class for Org management related operations.
 */
public class OrgManagement extends ControlPlaneAPI {

    private static String ORG_MGT_BASE_PATH = "/org-mgt/1.0.0";

    public static SelfSignupConfig updateSelfSignupConfig(TestActionRunner runner, HttpClient client,
                                                          String accessToken, String orgUuid,
                                                          SelfSignupConfig selfSignupConfigRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapObjectToString(selfSignupConfigRequest);

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(getSelfSignupConfigUpdateEndpoint(orgUuid))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        SelfSignupConfig response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        SelfSignupConfig.class);
                                        if (response.getId() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        System.out.println("Deserialized Response: " + response);
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), SelfSignupConfig.class);
    }

    public static ApprovalRequestList getApprovalRequests(TestActionRunner runner, HttpClient client,
                                                          String accessToken, String orgUuid)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(getApprovalRequestsEndpoint(orgUuid))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        ApprovalRequestList response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ApprovalRequestList.class);
                                        if (response.getList() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ApprovalRequestList.class);
    }

    public static ApprovalStatus updateApprovalRequestStatus(TestActionRunner runner, HttpClient client,
                                                             String accessToken, String orgUuid,
                                                             ApprovalStatus approvalRequestStatus)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapObjectToString(approvalRequestStatus);

        runner.$(repeatOnError()
                .until("i = 3")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .put(getSelfSignupApprovalRequestUpdateEndpoint(orgUuid))
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    try {
                                        ApprovalStatus response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ApprovalStatus.class);
                                        if (response.getStatus() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ApprovalStatus.class);
    }

    private static String getSelfSignupConfigUpdateEndpoint(String orgUuid) {

        return ORG_MGT_BASE_PATH + "/orgs/" + orgUuid + "/self-signup/config";
    }

    private static String getApprovalRequestsEndpoint(String orgUuid) {

        return ORG_MGT_BASE_PATH + "/orgs/" + orgUuid + "/self-signup/approval-requests";
    }

    private static String getSelfSignupApprovalRequestUpdateEndpoint(String orgUuid) {

        return ORG_MGT_BASE_PATH + "/orgs/" + orgUuid + "/self-signup/approval-requests/change-status";
    }
}
