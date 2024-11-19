package com.wso2.choreo.integration.apis.oauth;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.oauth.ClientCredentialsResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

/**
 * Utility class for OAuth related operations.
 */
public class OAuthService {

    /**
     * Invokes the client credentials grant type flow.
     *
     * @param runner           Citrus test runner
     * @param client           HTTP client
     * @param tokenEndpointURL Token endpoint URL
     * @param clientId         Client ID
     * @param clientSecret     Client secret
     * @param oAuthRequest     OAuth request
     * @return ClientCredentialsResponseDTO
     * @throws JsonMappingException   JsonMappingException
     * @throws JsonProcessingException JsonProcessingException
     */
    public static ClientCredentialsResponseDTO invokeClientCredentialsAuthFlow(TestActionRunner runner,
            HttpClient client, String tokenEndpointURL, String clientId, String clientSecret,
            HashMap<String, Object> oAuthRequest) throws JsonMappingException, JsonProcessingException {

        AtomicReference<String> responseDTO = new AtomicReference<>();
        String requestBody = ObjectMapperUtil.mapToString(oAuthRequest);

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post("/")
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthorizationHeader(clientId, clientSecret))
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                                .accept(String.valueOf(MediaType.APPLICATION_JSON))
                                .body(requestBody),
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
                                        ClientCredentialsResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(),
                                                        ClientCredentialsResponseDTO.class);
                                        if (response.getAccess_token() == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(message.getPayload(String.class));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));

        return new ObjectMapper().readValue(responseDTO.get(), ClientCredentialsResponseDTO.class);

    }

    private static String getBasicAuthorizationHeader(String clientId, String clientSecret) {
        return "Basic " + java.util.Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());
    }

}
