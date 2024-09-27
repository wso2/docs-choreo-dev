/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.configmgt;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.environments.Environment;
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
    private static final String CONTEXT = "/config-mgt/1.0.0/";
    public static void addConfiguration(TestActionRunner runner, HttpClient client,
                                        ChoreoComponent component, List<Commit> commitHistory, Environment environment,
                                        BalConfig... balconfigs) throws Exception {
        String accessToken = TestContext.getTestUserTokenHandler().getTestTokenForCPAPIs();
        String componentId = component.getId();
        String envIdToDeploy = environment.getId();
        String latestVersionId = component.getLatestApiVersion().getId();
        String latestCommitSha = component.getLatestCommitHash(commitHistory.toArray(Commit[]::new));
        String orgHandle = component.getOrgHandler();
        String projectId = component.getProjectId();

        String configurationsUpdateRequestURI = CONTEXT.concat("/orgs/").concat(orgHandle).concat("/projects/")
                .concat(projectId).concat("/components/").concat(componentId).concat("/envs/")
                .concat(envIdToDeploy).concat("/").concat(latestVersionId).concat("/configurations");

        Map<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("moduleName", component.getName());
                put("commitHash", latestCommitSha);
                put("applyNow", false);
                put("operation", 0);
                put("sourceUuid", "");
                put("configs", balconfigs);
            }
        };

        String configurationsRequestBody = MessageUtils.generateJson(requestBodyMap).replace("required", "isRequired");

        // Update configurations
        runner.$(repeatOnError()
                .until("i = 10")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(configurationsUpdateRequestURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(configurationsRequestBody),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.OK)));
    }
}
