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

package com.wso2.choreo.integration.common.scim;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.scim.Users;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.scim.UserCreateRequest;
import com.wso2.choreo.integration.models.scim.UserCreateResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Utility class for SCIM2 API related tests.
 */
public class ScimUtils {

    public static UserCreateResponse createUser(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                                String accessToken, String orgHandle, UserCreateRequest userCreateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient asgardeoClient = citrusClients.get(Endpoints.ASGARDEO_ENDPOINT);
        return Users.createUser(runner, asgardeoClient, accessToken, orgHandle, userCreateRequest);
    }

    public static void deleteUser(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                  String accessToken, String orgHandle, String userIdpId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient asgardeoClient = citrusClients.get(Endpoints.ASGARDEO_ENDPOINT);
        Users.deleteUser(runner, asgardeoClient, accessToken, orgHandle, userIdpId);
    }
}
