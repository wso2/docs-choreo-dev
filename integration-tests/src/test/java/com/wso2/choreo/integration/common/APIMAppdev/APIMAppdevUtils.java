/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 * 
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.common.APIMAppdev;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.APIMAppdev.APIMAppdevService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.apimAppdev.ConsumableScopesResponseDTO;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Utility class for APIM Appdev related tests.
 */
public class APIMAppdevUtils {

    /**
     * Get consumable scopes.
     *
     * @param runner    Citrus test runner
     * @param client    Citrus http client
     * @param projectId Project ID
     * @return ConsumableScopesResponseDTO object
     * @throws URISyntaxException      If an error occurs while creating the URI
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     */
    public static ConsumableScopesResponseDTO getConsumableScopes(TestActionRunner runner, HttpClient client,
            String projectId) throws URISyntaxException, TokenRetrievalException, IOException {

        return APIMAppdevService.getConsumableScopes(runner, client, projectId);
    }
}
