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

package com.wso2.choreo.integration.common.configservice;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.configservice.ConfigService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.configservice.ConfigGroup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ConfigServiceUtils {

    // Note: ConfigGroups returned from this method do not contain the values of the configurations.
    // If you need the values, you have to get the groupUuid and call `getConfigGroupsWithValues` method.
    public static List<ConfigGroup> getConfigGroupsInComponent(TestActionRunner runner, HttpClient client,
            String projectId, String componentId) throws TokenRetrievalException, IOException, URISyntaxException {

        return ConfigService.getConfigGroupsInComponent(runner, client, projectId, componentId);
    }

    public static ConfigGroup getConfigGroupsWithValues(TestActionRunner runner, HttpClient client, String groupUuid)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return ConfigService.getConfigGroupsWithValues(runner, client, groupUuid);
    }
}
