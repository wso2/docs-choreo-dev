package com.wso2.choreo.integration.apis.codeChallengeEval;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class CodeChallengeEval extends ControlPlaneAPI {

    private static final String ENDPOINT = "code-challenge-eval/v1";

    public static String getScoreSummary(TestActionRunner runner, HttpClient client, String accessToken, String orgUuid) {
        final String url = ENDPOINT.concat("/summary/" + orgUuid);
        AtomicReference<String> responseMessage = new AtomicReference<>();
        runner.$(http()
                .client(client)
                .send()
                .get(url)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
        );

        runner.$(http().client(client)
                .receive().response(HttpStatus.OK)
                .message()
                .validate((message, context) -> {
                    String payload = message.getPayload(String.class);
                    responseMessage.set(payload);
                })
        );
        return responseMessage.get();
    }
}
