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

import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.schemaconfigservice.SchemaConfig;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.configservice.ConfigurationGroup;
import com.wso2.choreo.integration.models.configservice.MappingConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@Log4j2
public class ConfigManagement {
        private static final String SCHEMA_SVC_CONTEXT = "/configuration-schema/v1.0/";
        private static final String MAPPING_SVC_CONTEXT = "config-mapping-svc/v1.0";

        public static void addConfigurations(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients,
                        ChoreoComponent component, String environmentTemplateId,
                        SchemaConfig[] schemaconfigs) throws Exception {
                String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                HttpClient choreoProjectsTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
                String componentId = component.getId();
                String latestVersionId = component.getLatestApiVersion().getId();
                String projectId = component.getProjectId();

                List<Commit> commitHistory = GraphQL.getCommitHistory(runner, choreoProjectsTestClient, componentId, accessToken,
                                "main");
                Commit latestCommit = Commit.getLatestCommit(commitHistory);
                String sha = latestCommit.getSha();

                String configurationsUpdateRequestURI = SCHEMA_SVC_CONTEXT.concat("/projects/")
                                .concat(projectId).concat("/components/").concat(componentId).concat("/env-template/")
                                .concat(environmentTemplateId).concat("/deployment-track/").concat(latestVersionId)
                                .concat("/configurations");

                Map<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("commitHash", sha);
                                put("mappingId", "");
                                put("configurations", schemaconfigs);
                        }
                };

                String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap);

                runner.$(repeatOnError()
                                .until("i = 3")
                                .index("i")
                                .autoSleep(30000)
                                .actions(
                                                http()
                                                                .client(choreoProjectsTestClient)
                                                                .send()
                                                                .post(configurationsUpdateRequestURI)
                                                                .message()
                                                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                                                .body(configurationsRequestBody),
                                                http()
                                                                .client(choreoProjectsTestClient)
                                                                .receive()
                                                                .response(HttpStatus.CREATED)));
        }

        public static void createConfigurationMapping(TestNGCitrusSpringSupport runner, HttpClient client,
                                                      ChoreoComponent component, ConfigurationGroup configGroup,
                                                      String environmentTemplateId) throws Exception {

                String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
                List<MappingConfiguration> mappingConfigs = TestHelper.generateMappingConfigurations(configGroup,
                        environmentTemplateId);

                Map<String, Object> requestBodyMap = new HashMap<>() {
                        {
                                put("componentId", component.getId());
                                put("projectId", component.getProjectId());
                                put("envTemplateId", environmentTemplateId);
                                put("deploymentTrackId", component.getLatestApiVersion().getId());
                                put("configurations", mappingConfigs);
                        }
                };
                String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap);

                runner.$(repeatOnError()
                        .until("i = 3")
                        .index("i")
                        .autoSleep(30000)
                        .actions(
                                http()
                                        .client(client)
                                        .send()
                                        .post(getConfigMappingEndpoint())
                                        .message()
                                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                        .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                        .body(configurationsRequestBody),
                                http()
                                        .client(client)
                                        .receive()
                                        .response(HttpStatus.OK)
                        )
                );
        }

        private static String getConfigMappingEndpoint() {
                return MAPPING_SVC_CONTEXT + "/configs/mappings";
        }
}
