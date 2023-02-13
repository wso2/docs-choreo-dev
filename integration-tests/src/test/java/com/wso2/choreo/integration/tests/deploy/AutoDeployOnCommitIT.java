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
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.FileUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
/**
 * $(http()
 * tests related to component deployment using auto deploy on commit trigger on
 */
    public class AutoDeployOnCommitIT extends TestNGCitrusSpringSupport {
        private static String accessToken;
        private String orgHandle;
        private String projectId;
        private final String repoName = "empty-repo";
        private static ChoreoComponent choreoComponent;
        private ChoreoProject project;
        @Autowired
        private HttpClient choreoTestClient;
        @BeforeClass
        public void setup_DeployIT()
                throws Exception {
            accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
            project = GraphQL.createProject(accessToken);
            projectId = project.getId();
            orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        }
        @Test
        @CitrusTest
        public void createUserManagedComponentFor_DeployIT() throws Exception {
            String componentName = Constant.TEST_COMPONENT_NAME.concat(String.valueOf(new Date().getTime()));
            GraphqlDTO dto = GraphqlDTO.builder().
                    name(componentName).
                    triggerID("null").
                    srcGitRepoUrl("https://github.com/choreo-test-apps/empty-repo").
                    projectId(projectId).
                    displayType(Constant.displayType.graphql.name()).
                    build();
            choreoComponent = GraphQL.createUserManagedComponent(project, dto, accessToken);
            Assert.assertNotNull(choreoComponent.getId());
        }
        @Test(dependsOnMethods = {"createUserManagedComponentFor_DeployIT"})
        @CitrusTest
        public void createdComponentStatus_DeployIT() {
            // Poll component create status
            $(repeatOnError()
                    .until("i = 50")
                    .index("i")
                    .autoSleep(5000)
                    .actions(
                            http()
                                    .client(choreoTestClient)
                                    .send()
                                    .get("/orgs/"
                                            .concat(orgHandle)
                                            .concat("/projects/")
                                            .concat(projectId)
                                            .concat("/components/")
                                            .concat(choreoComponent.getId())
                                            .concat("/init/status"))
                                    .message()
                                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                                    .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                            http().client(choreoTestClient)
                                    .receive()
                                    .response(HttpStatus.OK)
                                    .message()
                                    .body(new ClassPathResource(
                                            "templates/createComponent/get_create_status_success.json"))
                                    .validate(json()
                                            .ignore("$.message"))));
        }
        @Test(dependsOnMethods = {"createdComponentStatus_DeployIT"})
        @CitrusTest
        public void handleConfigInit_DeployIT() throws Exception {
            GraphQL.handleConfigInit(accessToken,choreoComponent.getId());
        }
        @Test(dependsOnMethods = {"handleConfigInit_DeployIT"})
        @CitrusTest
        public void mergeNewCode_DeployIT() throws IOException {
            String serviceBal = FileUtil.readFileEncodedContent("src/test/resources/templates/autodeploy/service.bal");
            String timeStamp = String.valueOf(new Date().getTime());
            Map<String, String> params = new HashMap<>();
            params.put("timeStamp", timeStamp);
            String srcCode = MessageUtils.generateStringFromTemplate(
                    "templates/autodeploy/service.mustache", params);
            String encodedCode = Base64.getEncoder().encodeToString(srcCode.getBytes(StandardCharsets.UTF_8));
            GitHub.mergeNewCode(repoName, "service.bal", " change on DeployIT ", encodedCode);
        }
        @Test(dependsOnMethods = {"mergeNewCode_DeployIT"})
        @CitrusTest
        public void deploymentStatusByVersion_DeployIT() throws Exception {
            GraphQL.deploymentStatusByVersion(choreoComponent, accessToken);
        }
        @Test(dependsOnMethods = {"deploymentStatusByVersion_DeployIT"})
        @CitrusTest
        public void componentDevDeploymentStatus_DeployIT() throws Exception {
            GraphQL.componentDeployment(choreoComponent, "dev", accessToken);
        }
    }
