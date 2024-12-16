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

package com.wso2.choreo.integration.tests.pat;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.pat.PATManagement;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ProjectRetrievalException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.pat.PATListResponseDTO;
import com.wso2.choreo.integration.models.pat.PATResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class PATManagementTests extends TestNGCitrusSpringSupport {

    private static final String PAT_ALIAS_PREFIX = "automation_pat";

    private static final String[] PAT_SCOPES_LIST = new String[] {
            "apim:admin",
            "apim:tier_manage",
            "apim:api_manage",
            "apim:subscription_manage",
            "apim:publisher_settings",
            "apim:dcr:app_manage",
            "apim:api_publish",
            "apim:document_manage",
            "apim:api_settings",
            "apim:subscription_view",
            "environments:view_prod",
            "environments:view_dev",
            "choreo:deployment_manage",
            "choreo:prod_env_manage",
            "choreo:non_prod_env_manage",
            "choreo:log_view_prod",
            "choreo:project_manage",
            "choreo:component_manage",
            "choreo:project_view"
    };

    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    private ChoreoOrganization testOrganization;
    private String patId;
    private String pat;

    @BeforeClass
    public void setup_PATManagementTests() throws TokenRetrievalException, IOException, URISyntaxException {

        testOrganization = new ChoreoOrganization(
                Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE),
                Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID)),
                Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID));
    }

    // Clear remaining any stale PATs from previous test runs.
    @Test
    @CitrusTest
    public void clearStalePATData() throws TokenRetrievalException, IOException, URISyntaxException {

        clearStalePATs();
    }

    // Test case to generate a Personal Access Token.
    @Test(dependsOnMethods = "clearStalePATData")
    @CitrusTest
    public void generatePAT() throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient patClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String patAlias = NameGenerator.generateUniqueName(PAT_ALIAS_PREFIX).substring(0, 50);
        PATResponseDTO responseDTO = PATManagement.generatePAT(this, patClient, patAlias,
                PAT_SCOPES_LIST, 7);
        patId = responseDTO.getId();
        pat = responseDTO.getPat();
    }

    // Test case to retrieve project list using the PAT.
    @Test(dependsOnMethods = "generatePAT")
    @CitrusTest
    public void retrieveProjectList() throws ProjectRetrievalException {

        testOrganization.clearProjects();
        String accessToken = Constant.BEARER_PREFIX + pat;
        List<ChoreoProject> projectList = testOrganization.getProjectsList(accessToken);
        Assert.assertNotNull(projectList);
    }

    // Test case to revoke the PAT.
    @Test(dependsOnMethods = "retrieveProjectList")
    @CitrusTest
    public void revokePAT() throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient patClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        PATManagement.deletePAT(this, patClient, patId);
    }

    // Test case to retrieve the project list after revoking the PAT.
    @Test(dependsOnMethods = "revokePAT")
    @CitrusTest
    public void retrieveProjectListAfterRevoke() {

        testOrganization.clearProjects();
        String accessToken = Constant.BEARER_PREFIX + pat;
        Assert.assertThrows(ProjectRetrievalException.class, () -> testOrganization.getProjectsList(accessToken));
    }

    private void clearStalePATs() throws TokenRetrievalException, IOException, URISyntaxException {

        HttpClient patClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        PATListResponseDTO patList = PATManagement.listPATsOfUser(this, patClient);
        for (PATResponseDTO pat : patList.getList()) {
            if (pat.getAlias().startsWith(PAT_ALIAS_PREFIX)) {
                PATManagement.deletePAT(this, patClient, pat.getId());
            }
        }
    }
}
