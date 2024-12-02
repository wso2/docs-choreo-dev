package com.wso2.choreo.integration.apis.oauth;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wso2.choreo.integration.common.oauth.OAuthConstants.STSEndpoints;
import com.wso2.choreo.integration.models.oauth.TokenResponseDTO;
import com.wso2.choreo.integration.models.oauth.WellKnownResponseDTO;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
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
     * @param authorizeHeader  Authorization header
     * @param oAuthRequest     OAuth request
     * @return ClientCredentialsResponseDTO
     * @throws JsonMappingException   JsonMappingException
     * @throws JsonProcessingException JsonProcessingException
     */
    public static TokenResponseDTO invokeTokenCall(TestActionRunner runner, HttpClient client, String authorizeHeader,
                                                   MultiValueMap<String, Object> oAuthRequest)
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
                                .post(STSEndpoints.TOKEN)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, authorizeHeader)
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

    public static String invokeHttpGetAndGetLocationHeader(TestActionRunner runner, HttpClient client,
                                                           String authorizeUrl) {

        AtomicReference<String> response = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(authorizeUrl)
                                .message()
                                .accept(MediaType.ALL_VALUE),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.FOUND.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    URI location = (URI)message.getHeader("Location");
                                    response.set(location.toString());
                                })));
        return response.get();
    }

    public static String submitCredentials(TestActionRunner runner, HttpClient client,
                                           String loginUrl, String username, String password) {
        
        AtomicReference<String> csrfToken = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(loginUrl)
                                .message(),
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
                                    String responseBody = message.getPayload(String.class);
                                    Document doc = Jsoup.parse(responseBody);
                                    String token = doc.selectFirst("input[name=_csrf]").val();
                                    if (StringUtils.isEmpty(token)) {
                                        throw new ValidationException("CSRF token not found in the response");
                                    }
                                    csrfToken.set(token);
                                })));


        MultiValueMap<String, Object> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", username);
        loginRequest.add("password", password);
        loginRequest.add("_csrf", csrfToken.get());

        AtomicReference<String> response = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .post(loginUrl)
                                .message()
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .accept(MediaType.ALL_VALUE)
                                .body(loginRequest),
                        http()
                                .client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.FOUND.value()) {
                                        throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                                    }
                                    URI location = (URI)message.getHeader("Location");
                                    response.set(location.toString());
                                })));
        return response.get();
    }


    public static WellKnownResponseDTO invokeWellKnownEndpoint(TestActionRunner runner, HttpClient client) {

        AtomicReference<WellKnownResponseDTO> responseDTO = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(30000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(STSEndpoints.WELL_KNOWN)
                                .message()
                                .accept(MediaType.ALL_VALUE),
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
                                        WellKnownResponseDTO response = new ObjectMapper()
                                                .readValue(message.getPayload().toString(), WellKnownResponseDTO.class);
                                        if (response == null) {
                                            throw new RuntimeException("Response fields are empty");
                                        }
                                        responseDTO.set(response);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                })));
        return responseDTO.get();

    }
}
