/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.marketplace;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.consol.citrus.validation.json.JsonTextMessageValidator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.models.marketplace.ConnectionCreateRequest;
import com.wso2.choreo.integration.models.marketplace.ConnectionInfo;
import com.wso2.choreo.integration.models.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.comparesEqualTo;

public class ConnectionService extends ControlPlaneAPI {

    private static final String CONTEXT = "connections/v1";
    private static final Logger log = LogManager.getLogger();

    public static String createChoreoConnection(TestActionRunner runner, HttpClient client, String accessToken,
                                                ConnectionCreateRequest connectionReq) throws IOException {
        String createChoreoConnectionURI = CONTEXT.concat("/configurations/service-configs/choreo-connections");
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        AtomicReference<String> connectionId = new AtomicReference<>();
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(createChoreoConnectionURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestPayload),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                .validate((message, context) -> {
                                            String payload = message.getPayload(String.class);
                                            JsonObject connectionJsonObject = new JsonParser().parse(payload).getAsJsonObject();
                                            connectionId.set(connectionJsonObject.get("groupUuid").getAsString());
                                        }
                                )));
        return connectionId.get();
    }

    public static void refreshChoreoConnection(TestActionRunner runner, HttpClient client, String accessToken,
                                                 String connectionId, ConnectionCreateRequest connectionReq) throws Exception {
        String refreshChoreoConnectionURI = CONTEXT.
                concat("/configurations/service-configs/choreo-connections/refresh/")
                .concat(connectionId)
                .concat("?generateCreds=true");
        String requestPayload = ObjectMapperUtil.mapObjectToString(connectionReq);
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(refreshChoreoConnectionURI)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestPayload),
                        http()
                                .client(client)
                                .receive()
                                .response(HttpStatus.CREATED)
                                ));
    }

    public static void deleteChoreoConnection(String accessToken,
                                                String  connectionId) throws IOException {
        log.info("Deleting connection with connection Id: " + connectionId );
        String deleteChoreoConnectionURI =CHOREO_APP_SERVICE_URL.concat("/")+ CONTEXT.concat("/configurations/service-configs/choreo-connections/").concat(connectionId);
        Response response = HttpClientUtil.httpDELETE(deleteChoreoConnectionURI, accessToken, "");
        if(response.getStatusCode() != HttpStatus.OK.value()){
                log.warn("Error while deleting the connection "+connectionId + "error is: "+response.getRes());
        }
    }


    public static ConnectionInfo[] getChoreoConnections(String accessToken, String projectId){
        String getChoreoConnectionsURI=CHOREO_APP_SERVICE_URL.concat("/")+CONTEXT.concat("/configurations/service-configs/connections").concat("?projectId=").concat(projectId);
        Response response = HttpClientUtil.httpGET(getChoreoConnectionsURI, accessToken, "");
        ConnectionInfo[] connectionListing = ObjectMapperUtil.mapStringToObject(ConnectionInfo[].class, response.getRes(), "");
        return connectionListing;

    }
}
