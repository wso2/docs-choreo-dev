/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.tests.configurations;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.tests.dp.TestBase;
import com.wso2.choreo.integration.tests.dp.DataProviderWrapper;
import com.wso2.choreo.integration.common.ComponentFlavour;
import com.wso2.choreo.integration.common.ComponentUtils;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.common.schemaconfigservice.SchemaConfig;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TestSchemaConfigurations extends TestBase {

        private String accessToken;
        private final List<DataProviderWrapper> dps = new ArrayList<>();
        @Autowired
        Map<Endpoints, HttpClient> citrusClients;

        @BeforeClass
        public void setup_TestBYOCEUDp() throws Exception {
                accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        }

        @DataProvider(name = "dps")
        public Object[][] provideData() {
                return this.setUp();
        }

        @Test(dataProvider = "dps")
        @CitrusTest
        public void createComponent_TestSchemaDp(DataProviderWrapper dp) throws Exception {
                String componentName = NameGenerator.generateThreadUniqueNameWithPrefix(Constant.TEST_COMPONENT_NAME);
                ChoreoProject project = ComponentUtils.createProject(this, citrusClients, accessToken, dp.getRegion());
                Repository repo = Repository.builder().repoUrl("https://github.com/wso2/choreo-samples")
                                .buildContext("user-management-service").isPublicRepo(true).build();

                GraphqlDTO dto = ComponentUtils.createBuildpackComponentRequest(componentName, project, repo);

                ChoreoComponent choreoComponent = ComponentUtils.createComponent(this, citrusClients, accessToken,
                                dto, ComponentFlavour.BUILDPACK);
                dp.setChoreoProject(project);
                dp.setChoreoComponent(choreoComponent);
                Assert.assertNotNull(choreoComponent.getId());
                ComponentUtils.waitForComponentInitialBuildComplete(this, citrusClients, accessToken, choreoComponent);

                List<Environment> environments = ComponentUtils.getDeploymentEnvironments(this, citrusClients,
                                accessToken, choreoComponent);
                dp.setEnvironments(environments);
        }

        @Test(dependsOnMethods = { "createComponent_TestSchemaDp" }, dataProvider = "dps")
        @CitrusTest
        public void addConfigurations_TestSchemaDp(DataProviderWrapper dp) throws Exception {
                AtomicReference<String> environmentTemplateIdRef = new AtomicReference<>(null);
                List<Environment> environments = dp.getEnvironments();
                environments.forEach(environment -> {
                        if (environment.getName().equals(Constant.Environment.Development.name())) {
                                environmentTemplateIdRef.set(environment.getTemplateId().toString());
                        }
                });
                String envId = environmentTemplateIdRef.get();
                SchemaConfig[] schemaConfigs = TestHelper.generateAddConfigurationPayload(envId);

                ConfigManagement.addConfigurations(this, citrusClients, dp.getChoreoComponent(), envId,
                                schemaConfigs);
        }

        @Test(dependsOnMethods = { "addConfigurations_TestSchemaDp" }, dataProvider = "dps")
        @CitrusTest
        public void deployComponent_TestSchemaDp(DataProviderWrapper dp) throws Exception {
                ComponentDeploymentStatusDTO statusDTO = ComponentUtils.deployAndValidateBuiltComponent(this,
                                citrusClients,
                                accessToken, dp.getChoreoComponent(),
                                dp.getEnvironments());
                dp.setDeploymentStatusDTO(statusDTO);
        }

        @Test(dependsOnMethods = { "deployComponent_TestSchemaDp" }, dataProvider = "dps")
        @CitrusTest
        public void invokeAPIDev_TestSchemaDp(DataProviderWrapper dp) throws Exception {
                Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients, accessToken,
                                dp.getChoreoComponent(), dp.getDeploymentStatusDTO(), dp.getEnvironments());
                ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), invokeData.getLeft(), "/user",
                                TestHelper.EXPECTED_SCHEMA_API_RESPONSE);
        }

        @Test(dependsOnMethods = { "invokeAPIDev_TestSchemaDp" }, dataProvider = "dps")
        @CitrusTest
        public void promoteComponent_TestSchemaDp(DataProviderWrapper dp) throws Exception {
                AtomicReference<String> environmentTemplateIdRef = new AtomicReference<>(null);
                List<Environment> environments = dp.getEnvironments();
                environments.forEach(environment -> {
                        if (environment.getName().equals(Constant.Environment.Production.name())) {
                                environmentTemplateIdRef.set(environment.getTemplateId().toString());
                        }
                });
                String envId = environmentTemplateIdRef.get();
                SchemaConfig[] schemaConfigs = TestHelper.generateAddConfigurationPayload(envId);

                ConfigManagement.addConfigurations(this, citrusClients, dp.getChoreoComponent(), envId,
                                schemaConfigs);
                List<ComponentDeploymentStatusDTO> statusDTO = ComponentUtils.promoteComponent(this, citrusClients,
                                accessToken, dp.getChoreoComponent(), dp.getEnvironments(), ComponentFlavour.BYOC,
                                dp.getChoreoProject());
                dp.setPromoteStatusDTO(statusDTO);
        }

        @Test(dependsOnMethods = { "promoteComponent_TestSchemaDp" }, dataProvider = "dps")
        @CitrusTest
        public void invokeAPIProd_TestSchemaDp(DataProviderWrapper dp) throws Exception {
                for (ComponentDeploymentStatusDTO statusDTO : dp.getPromoteStatusDTO()) {
                        Pair<String, KeyData> invokeData = ComponentUtils.getInvokeInfo(this, citrusClients,
                                        accessToken,
                                        dp.getChoreoComponent(), statusDTO, dp.getEnvironments());
                        ComponentUtils.invokeApiGET(this, invokeData.getRight().getApikey(), invokeData.getLeft(),
                                        "/user",
                                        TestHelper.EXPECTED_SCHEMA_API_RESPONSE);
                }
        }
}
