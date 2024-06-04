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

package com.wso2.choreo.integration.tests.appdevUserManagement;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.appdevUserManagement.AppdevUserManagementConstants;
import com.wso2.choreo.integration.common.appdevUserManagement.AppdevUserManagementUtils;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.appdevUserManagement.CreateUserStoreResponseDTO;
import com.wso2.choreo.integration.models.appdevUserManagement.UserStore;
import com.wso2.choreo.integration.models.appdevUserManagement.UsersListResponseDTO;
import com.wso2.choreo.integration.models.environments.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppdevUserManagementTests extends TestNGCitrusSpringSupport {

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    String devEnvironmentId;
    String createdUserStoreId;

    @Test()
    @CitrusTest
    public void getOrgEnvironments_AppdevUserManagementTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .orgUuid(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID)).build();

        List<Environment> environments = GraphQL.getEnvironments(this, appServiceClient, accessToken, graphqlDTO);

        environments.forEach(environment -> {
            if (environment.getName().equals(AppdevUserManagementConstants.DEV_ENV_NAME)) {
                devEnvironmentId = environment.getTemplateId();
            }
        });

        Assert.assertNotNull(devEnvironmentId);
    }

    @Test(dependsOnMethods = "getOrgEnvironments_AppdevUserManagementTests")
    @CitrusTest
    public void createUserStoreInDevEnvironment_AppdevUserManagementTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        HashMap<String, Object> createUserStoreRequest = new HashMap<>();
        createUserStoreRequest.put("name", "testUserStore");
        createUserStoreRequest.put("userstoreFile",
                new File("src/test/resources/templates/appdevUserManagement/user-store-file.csv"));

        CreateUserStoreResponseDTO createdUserStore = AppdevUserManagementUtils.createUserStoreInEnvironment(
                this, appServiceClient, devEnvironmentId, createUserStoreRequest);

        Assert.assertNotNull(createdUserStore);

        createdUserStoreId = createdUserStore.getId();
    }

    @Test(dependsOnMethods = "createUserStoreInDevEnvironment_AppdevUserManagementTests")
    @CitrusTest
    public void listUserStoresInDevEnvironment_AppdevUserManagementTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        List<UserStore> userStores = AppdevUserManagementUtils.listUserStoreInEnvironment(
                this, appServiceClient, devEnvironmentId);

        Assert.assertNotNull(userStores);
        Assert.assertTrue(userStores.size() > 0);
    }

    @Test(dependsOnMethods = "listUserStoresInDevEnvironment_AppdevUserManagementTests")
    @CitrusTest
    public void listUsersInUserStoreInDevEnvironment_AppdevUserManagementTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        UsersListResponseDTO usersListResponseDTO = AppdevUserManagementUtils.listUsersInUserStore(
                this, appServiceClient, createdUserStoreId);

        Assert.assertNotNull(usersListResponseDTO);
        Assert.assertTrue(usersListResponseDTO.getResources().size() > 0);
    }

    @Test(dependsOnMethods = "listUsersInUserStoreInDevEnvironment_AppdevUserManagementTests")
    @CitrusTest
    public void reUploadUserStoreInDevEnvironment_AppdevUserManagementTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        HashMap<String, Object> createUserStoreRequest = new HashMap<>();
        createUserStoreRequest.put("name", "updatedTestUserStore");
        createUserStoreRequest.put("userstoreFile",
                new File("src/test/resources/templates/appdevUserManagement/user-store-file.csv"));

        CreateUserStoreResponseDTO createdUserStore = AppdevUserManagementUtils.reCreateUserStoreInEnvironment(
                this, appServiceClient, createdUserStoreId, createUserStoreRequest);

        Assert.assertNotNull(createdUserStore);
        Assert.assertEquals(createdUserStore.getName(), "updatedTestUserStore");
    }

    @Test(dependsOnMethods = "reUploadUserStoreInDevEnvironment_AppdevUserManagementTests")
    @CitrusTest
    public void deleteUserStoreInDevEnvironment_AppdevUserManagementTests()
            throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        AppdevUserManagementUtils.deleteUserStoreInEnvironment(this, appServiceClient, createdUserStoreId);
    }
}
