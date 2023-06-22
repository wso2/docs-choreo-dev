/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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
package com.wso2.choreo.integration.tests.deploy;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.github.GitHub;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.code.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * $(http()
 * tests related to component deployment using auto deploy on commit trigger on
 */
    public class AutoDeployOnCommitIT extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private final String repoName = "empty-repo";
        private static ChoreoComponent choreoComponent;
        private ChoreoProject project;

        @Autowired
        private HttpClient choreoTestClient;

        @Autowired
        Map<Endpoints, HttpClient> citrusClients;

        @BeforeClass
        public void setup_AutoDeployOnCommitIT()
                throws Exception {
            accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
            project = GraphQL.createProject(accessToken);
        }

        @Test
        @CitrusTest
        public void createUserManagedComponentFor_AutoDeployOnCommitIT() throws Exception {
            String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));

            Repository repo = Repository.builder().repoUrl("https://github.com/choreo-test-apps/empty-repo").branch("main").subPath("").build();
            GraphqlDTO dto = ComponentUtils.createRestApiComponentRequest(componentName, project, repo);

            choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken, dto,
                    ComponentFlavour.STANDARD);
            Assert.assertNotNull(choreoComponent.getId());
        }

        @Test(dependsOnMethods = {"createUserManagedComponentFor_AutoDeployOnCommitIT"})
        @CitrusTest
        public void handleConfigInit_AutoDeployOnCommitIT() throws Exception {
            GraphQL.handleConfigInit(this, choreoTestClient, accessToken, choreoComponent.getId());
        }

        @Test(dependsOnMethods = {"handleConfigInit_AutoDeployOnCommitIT"})
        @CitrusTest
        public void mergeNewCode_AutoDeployOnCommitIT() throws IOException {
            String serviceBal = FileUtil.readFileEncodedContent("src/test/resources/templates/autodeploy/service.bal");
            String timeStamp = String.valueOf(new Date().getTime());
            Map<String, String> params = new HashMap<>();
            params.put("timeStamp", timeStamp);
            String srcCode = MessageUtils.generateStringFromTemplate(
                    "templates/autodeploy/service.mustache", params);
            String encodedCode = Base64.getEncoder().encodeToString(srcCode.getBytes(StandardCharsets.UTF_8));
            GitHub.mergeNewCode(repoName, "service.bal", " change on DeployIT ", encodedCode);
        }

        @Test(dependsOnMethods = {"mergeNewCode_AutoDeployOnCommitIT"})
        @CitrusTest
        public void deploymentStatusByVersion_AutoDeployOnCommitIT() throws Exception {
            GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
        }

        @Test(dependsOnMethods = {"deploymentStatusByVersion_AutoDeployOnCommitIT"})
        @CitrusTest
        public void componentDevDeployment_AutoDeployOnCommitIT() throws Exception {
            GraphQL.componentDeployment(choreoComponent, "dev", accessToken);
        }
    }
