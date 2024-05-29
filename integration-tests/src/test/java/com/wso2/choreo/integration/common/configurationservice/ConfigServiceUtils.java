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

package com.wso2.choreo.integration.common.configurationservice;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.configurationservice.ConfigurationService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.configservice.ConfigurationGroup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Utility class for Choreo configuration service related tests.
 */
public class ConfigServiceUtils {

    /**
     * Get configuration groups in a component.
     *
     * @param runner      Citrus test runner
     * @param client      Citrus http client
     * @param projectId   Project ID
     * @param componentId Component ID
     * @return List of configuration groups
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     * Note: ConfigGroups returned from this method do not contain the values of the configurations. 
     *       If you need the values, you have to get the groupUuid and call `getConfigGroupsWithValues` method.
     */
    public static List<ConfigurationGroup> getConfigGroupsInComponent(TestActionRunner runner, HttpClient client,
            String projectId, String componentId) throws TokenRetrievalException, IOException, URISyntaxException {

        return ConfigurationService.getConfigGroupsInComponent(runner, client, projectId, componentId);
    }

    /**
     * Get configuration groups in a component with values.
     *
     * @param runner   Citrus test runner
     * @param client   Citrus http client
     * @param groupUuid Group UUID
     * @return ConfigurationGroup object with values
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static ConfigurationGroup getConfigGroupsWithValues(TestActionRunner runner, HttpClient client, String groupUuid)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return ConfigurationService.getConfigGroupsWithValues(runner, client, groupUuid);
    }

    /**
     * Update a configuration group.
     *
     * @param runner              Citrus test runner
     * @param client              Citrus http client
     * @param groupUuid           Group UUID
     * @param updatedConfigGroup  Updated configurationGroup object
     * @return Updated ConfigurationGroup object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static ConfigurationGroup updateConfigGroup(TestActionRunner runner, HttpClient client, 
            ConfigurationGroup updatedConfigGroup) throws TokenRetrievalException, IOException, URISyntaxException {

        return ConfigurationService.updateConfigGroup(runner, client, updatedConfigGroup);
    }    
}
