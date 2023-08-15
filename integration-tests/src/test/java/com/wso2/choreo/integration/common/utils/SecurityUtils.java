package com.wso2.choreo.integration.common.utils;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class SecurityUtils {
    public static void elevatedAccessCheckForGetRequests(TestActionRunner runner, HttpClient client,
                                    String requestUrl, String accessToken) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .get(requestUrl)
                .message()
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.AUTHORIZATION, accessToken));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    public static void elevatedAccessCheckForDeleteRequests(TestActionRunner runner, HttpClient client,
                                                         String requestUrl, String accessToken) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .delete(requestUrl)
                .message()
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.AUTHORIZATION, accessToken));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    public static void elevatedAccessCheckForPostRequests(TestActionRunner runner, HttpClient client,
                                                            String requestUrl, String body, String accessToken) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .post(requestUrl)
                .message()
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(body));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    public static void elevatedAccessCheckForPutRequests(TestActionRunner runner, HttpClient client,
                                                          String requestUrl, String body, String accessToken) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .put(requestUrl)
                .message()
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(body));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    public static void elevatedAccessCheckForPatchRequests(TestActionRunner runner, HttpClient client,
                                                         String requestUrl, String body, String accessToken) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .patch(requestUrl)
                .message()
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(body));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.UNAUTHORIZED)
                .message()
                .type(MessageType.JSON));
    }

    public static void forbiddenCheckForPostRequests(TestActionRunner runner, HttpClient client,
                                                          String requestUrl, String body, String accessToken) throws IOException {
        runner.$(http()
                .client(client)
                .send()
                .post(requestUrl)
                .message()
                .header(HttpHeaders.ACCEPT, "*/*")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(body));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.FORBIDDEN)
                .message()
                .type(MessageType.JSON));
    }
}
