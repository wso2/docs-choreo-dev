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
import org.springframework.core.io.ClassPathResource;
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
     * @param accessToken            Access token
     * @param orgUuid                Organization UUID
     * @param environmentId          Environment ID
     * @param userStoreName          UserStore name
     * @return CreateUserStoreResponseDTO
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateUserStoreResponseDTO createUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
                                                                          String accessToken, String orgUuid,
                                                                          String environmentId, String userStoreName)
            throws IOException, URISyntaxException {

        LinkedMultiValueMap<String, Object> createUserStoreRequest = getCreateUserStoreRequest(userStoreName);
        return AppdevUserManagementService.createUserStoreInEnvironment(runner, client, accessToken, orgUuid,
                environmentId,
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
     * @param userStoreName          UserStore name
     * @return CreateUserStoreResponseDTO
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static CreateUserStoreResponseDTO reCreateUserStoreInEnvironment(TestActionRunner runner, HttpClient client,
            String userStoreId, String userStoreName)
            throws TokenRetrievalException, IOException, URISyntaxException {

        LinkedMultiValueMap<String, Object> createUserStoreRequest = getCreateUserStoreRequest(userStoreName);
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

    /**
     * These non Citrus based implementation is to be used in cases where the Citrus
     * framework is yet to be initialized, such as in the BeforeSuite
     */

    /**
     * List user stores in all environments
     *
     * @param accessToken Access token
     * @param orgUuid     Organization UUID
     * @return List of UserStore objects
     * @throws IOException        If an error occurs while reading the response
     * @throws URISyntaxException If an error occurs while creating the URI
     */
    public static List<UserStore> getAllUserStores(String accessToken, String orgUuid) throws IOException,
            URISyntaxException {

        return AppdevUserManagementService.getAllUserStores(accessToken, orgUuid);
    }

    /**
     * Delete a user store
     *
     * @param accessToken Access token
     * @param orgUuid     Organization UUID
     * @param userStoreId User store ID
     * @throws IOException        If an error occurs while reading the response
     * @throws URISyntaxException If an error occurs while creating the URI
     */
    public static void deleteUserStore(String accessToken, String orgUuid, String userStoreId) throws IOException,
            URISyntaxException {

        AppdevUserManagementService.deleteUserStore(accessToken, orgUuid, userStoreId);
    }

    private static LinkedMultiValueMap<String, Object> getCreateUserStoreRequest(String userStoreName) {

        LinkedMultiValueMap<String, Object> createUserStoreRequest = new LinkedMultiValueMap<>();
        createUserStoreRequest.add("name", userStoreName);
        createUserStoreRequest.add("userstoreFile",
                new ClassPathResource("templates/appdevUserManagement/user-store-file.csv"));

        return createUserStoreRequest;
    }
}
