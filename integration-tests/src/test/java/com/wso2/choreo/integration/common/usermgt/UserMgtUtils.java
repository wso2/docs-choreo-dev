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

package com.wso2.choreo.integration.common.usermgt;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.usermgt.UserManagement;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.usermgt.RegisterEnterpriseUserRequest;
import com.wso2.choreo.integration.models.usermgt.RegisterEnterpriseUserResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Utility class for User management API related tests.
 */
public class UserMgtUtils {

    public static RegisterEnterpriseUserResponse addEnterpriseUser(TestActionRunner runner,
                                                                   Map<Endpoints, HttpClient> citrusClients,
                                                                   String accessToken)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        RegisterEnterpriseUserRequest addEnterpriseUserRequest = RegisterEnterpriseUserRequest.builder()
                .isDevportalUser(true).build();
        return UserManagement.addEnterpriseUser(runner, choreoCPTestClient, accessToken, addEnterpriseUserRequest);
    }

    public static void validateUser(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                    String accessToken,  HttpStatus status)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        UserManagement.validateUser(runner, choreoCPTestClient, accessToken, status);
    }
}
