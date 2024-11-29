package com.wso2.choreo.integration.apis.oauth;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.models.oauth.TokenResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.wso2.choreo.integration.common.oauth.OAuthConstants.DEFAULT_TOKEN_ENDPOINT;

/**
 * Utility class for OAuth related operations.
 */
public class OAuthService {

    /**
     * Invokes the client credentials grant type flow.
     *
     * @param runner           Citrus test runner
     * @param client           HTTP client
     * @param clientId         Client ID
     * @param clientSecret     Client secret
     * @param oAuthRequest     OAuth request
     * @return ClientCredentialsResponseDTO
     * @throws JsonMappingException   JsonMappingException
     * @throws JsonProcessingException JsonProcessingException
     */
    public static TokenResponseDTO invokeTokenCall(TestActionRunner runner, HttpClient client, String clientId,
                                                   String clientSecret, MultiValueMap<String, Object> oAuthRequest)
            throws JsonMappingException, JsonProcessingException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(DEFAULT_TOKEN_ENDPOINT)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthorizationHeader(clientId, clientSecret))
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(oAuthRequest),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    try {
                                        TokenResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(), TokenResponseDTO.class);
                                        if (response.getAccess_token() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), TokenResponseDTO.class);
    }

    private static String getBasicAuthorizationHeader(String clientId, String clientSecret) {
        return "Basic " + java.util.Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());
    }

}
