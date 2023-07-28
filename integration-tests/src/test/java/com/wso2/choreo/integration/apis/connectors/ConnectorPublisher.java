package com.wso2.choreo.integration.apis.connectors;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonMessageValidationContext.Builder.json;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.connectors.Connector;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ConnectorPublisher extends ControlPlaneAPI {

    public static void publishConnector(TestActionRunner runner, HttpClient client, String accessToken,
            Connector connectorDTO, boolean isRePublish) throws IOException {
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/connectorbuilder/publish_connector.mustache", connectorDTO);
        String path = Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(connectorDTO.getOrgHandler())
                        .concat("/").concat(connectorDTO.getComponentId());
        if (isRePublish) {
            path = Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/").concat(connectorDTO.getOrgHandler())
                        .concat("/").concat(connectorDTO.getComponentId()).concat("/republish");
        }

        runner.$(http()
                .client(client)
                .send()
                .post(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .body(queryString)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.CREATED)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/connectorbuilder/publish_success_ok.json")));
    }

    public static void getConnectorStatus(TestActionRunner runner, HttpClient client, String accessToken,
        Connector connectorDTO) throws IOException {
            runner.$(repeatOnError()
                .until("i = 15")
                .index("i")
                .autoSleep(5000)
                .actions(
                    http()
                        .client(client)
                        .send()
                        .get(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/")
                                .concat(connectorDTO.getOrgHandler()).concat("/")
                                .concat(connectorDTO.getComponentId()).concat("/status"))
                        .message()
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                        .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                    http()
                        .client(client)
                        .receive()
                        .response(HttpStatus.OK)
                        .message()
                        .body(new ClassPathResource("templates/connectorbuilder/publish_status_completed.json"))
                            .validate(json()
                                .ignore("$.id")
                                .ignore("$.created_at")
                                .ignore("$.updated_at"))));
    }

    public static void getConnector(TestActionRunner runner, HttpClient client, String accessToken, 
        Connector connectorDTO) throws IOException {
            runner.$(http()
                .client(client)
                .send()
                .get(Constant.USER_CONNECTORS_ENDPOINT_SUFFIX.concat("/")
                    .concat(connectorDTO.getOrgHandler()).concat("/")
                    .concat(connectorDTO.getComponentId()))
                .queryParam("version=".concat(connectorDTO.getVersion()))
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header("x-correlation-id", Constant.X_CORRELATION_UUID)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));
            runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .body(new ClassPathResource("templates/connectorbuilder/get_connector_success.json"))
                .validate(json()
                        .ignore("$.name")
                        .ignore("$.org")
                        .ignore("$.modules")
                        .ignore("$.createdDate")
                )
        );
    }
}
