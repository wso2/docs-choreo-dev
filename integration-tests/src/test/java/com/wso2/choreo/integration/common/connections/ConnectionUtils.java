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

package com.wso2.choreo.integration.common.connections;

import java.util.Map;

import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;

import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;

public class ConnectionUtils {

    public static ChoreoComponent createByocComponent(TestNGCitrusSpringSupport runner, Map<Endpoints, 
            HttpClient> citrusClients, String accessToken, String componentName, ChoreoProject project, Repository repo) throws Exception {
    
        GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, project, repo);
        ChoreoComponent component = ComponentUtils.createComponent(runner, citrusClients, accessToken,
                dto, ComponentFlavour.BYOC);
        ComponentUtils.waitForComponentInitialBuildComplete(runner, citrusClients, accessToken, component);
        return component;
    }
}
