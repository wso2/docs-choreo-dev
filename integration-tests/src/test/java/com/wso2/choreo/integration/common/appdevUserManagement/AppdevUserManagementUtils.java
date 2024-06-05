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

package com.wso2.choreo.integration.common.appdevUserManagement;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.appdevUserManagement.AppdevUserManagementService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.appdevUserManagement.CreateUserStoreResponseDTO;
import com.wso2.choreo.integration.models.appdevUserManagement.UserStore;
import com.wso2.choreo.integration.models.appdevUserManagement.UsersListResponseDTO;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Utility class for Appdev User Management
 */
public class AppdevUserManagementUtils {

    /**
     * Create a user store in an environment
     * 
     * @param runner                 Citrus test runner
     * @param client                 Citrus http client
     * @param environmentId          Environment ID
     * @param createUserStoreRequest Request body to create user store
     * @return CreateUserStoreResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateUserStoreResponseDTO createUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String environmentId, LinkedMultiValueMap<String, Object> createUserStoreRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevUserManagementService.createUserStoreInEnvironment(runner, client, environmentId,
                createUserStoreRequest);
    }

    /**
     * Delete a user store in an environment
     * 
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param userStoreId User store ID
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static void deleteUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String userStoreId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        AppdevUserManagementService.deleteUserStoreInEnvironment(runner, client, userStoreId);
    }

    /**
     * Re-create a user store in an environment
     * 
     * @param runner                 Citrus test runner
     * @param client                 Citrus http client
     * @param userStoreId            User store ID
     * @param createUserStoreRequest Request body to create user store
     * @return CreateUserStoreResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateUserStoreResponseDTO reCreateUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String userStoreId, LinkedMultiValueMap<String, Object> createUserStoreRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevUserManagementService.reCreateUserStoreInEnvironment(runner, client, userStoreId,
                createUserStoreRequest);
    }

    /**
     * List user stores in an environment
     * 
     * @param runner        Citrus test runner
     * @param client        Citrus http client
     * @param environmentId Environment ID
     * @return List of UserStore objects
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static List<UserStore> listUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevUserManagementService.listUserStoreInEnvironment(runner, client, environmentId);
    }

    /**
     * List users in a user store
     * 
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param userStoreId User store ID
     * @return UsersListResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static UsersListResponseDTO listUsersInUserStore(TestActionRunner runner, HttpClient client,
            String userStoreId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return AppdevUserManagementService.listUsersInUserStore(runner, client, userStoreId);
    }
}
