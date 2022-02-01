package com.wso2.choreo.integration.common;

import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Handles retrieving a OAuth token to test API calls
 */
public class TokenHandler {
    private static final HttpClient client = HttpClient.newHttpClient();
    private final String clientId = Configuration.STS_CLIENT_ID;
    private final String clientSecret = Configuration.STS_CLIENT_SECRET;

    /**
     * Retrieve oauth token to be used when invoking choreo APIs
     *
     * @return oauth token
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws InterruptedException    if sending request is interrupted
     * @throws TokenRetrievalException if token retrieval fails
     */
    public String getTestToken() throws InterruptedException, TokenRetrievalException, IOException {
        String tokenAuthHeader = Constant.BASIC_PREFIX.concat(encodeCredentials(clientId, clientSecret));
        String tokenEndpoint = Configuration.STS_ENDPOINT.concat(Constant.TOKEN_ENDPOINT_SUFFIX);
        HashMap<String, String> requestBodyMap = new HashMap<>() {{
            put("grant_type", Constant.OAUTH_GRANT_TYPE);
            put("orgHandle", Configuration.TEST_CHOREO_ORG_HANDLE);
            put("scope", Constant.OAUTH_SCOPES);
        }};
        String form = requestBodyMap.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(requestBodyMap.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(tokenEndpoint))
                .headers(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .header(HttpHeaders.AUTHORIZATION, tokenAuthHeader)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode != HttpStatus.OK.value()) {
            throw new TokenRetrievalException(statusCode, response.body());
        }
        return new JsonParser().parse(response.body()).getAsJsonObject().getAsJsonPrimitive("access_token")
                .getAsString();
    }

    /**
     * Encode the credentials with Base64
     *
     * @param clientId     generated for test org application
     * @param clientSecret generated for test org application
     * @return a String of Base64 encoded credentials
     */
    private String encodeCredentials(String clientId, String clientSecret) {
        String concatenateCredentials = clientId.concat(":").concat(clientSecret);
        return Base64.getEncoder().encodeToString(concatenateCredentials.getBytes());
    }
}
