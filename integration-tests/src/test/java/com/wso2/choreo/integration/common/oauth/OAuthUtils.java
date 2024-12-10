/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.common.oauth;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.TestActionRunner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wso2.choreo.integration.apis.oauth.OAuthService;
import com.wso2.choreo.integration.common.oauth.OAuthConstants.Grants;
import com.wso2.choreo.integration.common.oauth.OAuthConstants.STSEndpoints;
import com.wso2.choreo.integration.common.oauth.OAuthConstants.TokenParams;
import com.wso2.choreo.integration.models.oauth.TokenResponseDTO;
import com.wso2.choreo.integration.models.oauth.WellKnownResponseDTO;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.wso2.choreo.integration.common.oauth.OAuthConstants.DEFAULT_SCOPES;

/**
 * Utility class for OAuth related operations.
 */
public class OAuthUtils {

    /**
     * Invokes the client credentials grant type flow.
     *
     * @param runner           Citrus test runner
     * @param httpClient       Http client
     * @param clientId         Client ID
     * @param clientSecret     Client secret
     * @return TokenResponseDTO
     * @throws JsonMappingException   JsonMappingException
     * @throws JsonProcessingException JsonProcessingException
     */
    public static TokenResponseDTO invokeClientCredentialsAuthFlow(TestActionRunner runner, HttpClient httpClient,
                                                                   String clientId, String clientSecret)
            throws JsonMappingException, JsonProcessingException {

        MultiValueMap<String, Object> oAuthRequest = new LinkedMultiValueMap<>();
        oAuthRequest.add(TokenParams.GRANT_TYPE, Grants.CLIENT_CREDENTIALS);
        oAuthRequest.add(TokenParams.SCOPE, DEFAULT_SCOPES);
        String basicAuthorizationHeader = getBasicAuthorizationHeader(clientId, clientSecret);
        return OAuthService.invokeTokenCall(runner, httpClient, basicAuthorizationHeader, oAuthRequest);
    }

    public static TokenResponseDTO testAuthCodeFlow(TestActionRunner runner, HttpClient httpClient, String clientId,
                                                    String clientSecret,
                                                    String redirectUrl, String scope, String username, String password)
            throws MalformedURLException, JsonProcessingException {

        String scopes = URLEncoder.encode(DEFAULT_SCOPES + " " + scope, StandardCharsets.UTF_8);
        String codeChallenge = "Whubzdv9zyTyeqdpEpouWE1QVQ0tGlMpbn3eJpTuHog";
        String authorizePath = constructAuthorizeUrl(clientId, redirectUrl, scopes, codeChallenge);
        OAuthService.invokeHttpGetAndGetLocationHeader(runner, httpClient, authorizePath);
        String authCodeRequestUrl = OAuthService.submitCredentials(runner, httpClient, "/login", username, password);
        String authCodeRequestPath = getAuthCodeRequestPath(authCodeRequestUrl);
        String authCodeUrl = OAuthService.invokeHttpGetAndGetLocationHeader(runner, httpClient, authCodeRequestPath);
        String authCode = extractAuthCode(authCodeUrl);

        MultiValueMap<String, Object> oAuthRequest = new LinkedMultiValueMap<>();
        oAuthRequest.add(TokenParams.GRANT_TYPE, Grants.AUTHORIZATION_CODE);
        oAuthRequest.add(TokenParams.SCOPE, scopes);
        oAuthRequest.add(TokenParams.CODE, authCode);
        oAuthRequest.add(TokenParams.REDIRECT_URI, redirectUrl);
        oAuthRequest.add(TokenParams.CODE_VERIFIER, "47DEQpj8HBSa-_TImW-5JCeuQeRkm5NMpJWZG3hSuFU");
        String basicAuthorizationHeader = getBasicAuthorizationHeader(clientId, clientSecret);
        return OAuthService.invokeTokenCall(runner, httpClient, basicAuthorizationHeader, oAuthRequest);
    }

    public static TokenResponseDTO invokeRefreshTokenFlow(TestActionRunner runner, HttpClient stsClient,
                                                          String clientId, String clientSecret,
                                                          String scope, String refreshToken)
            throws JsonProcessingException {

        MultiValueMap<String, Object> oAuthRequest = new LinkedMultiValueMap<>();
        oAuthRequest.add(TokenParams.GRANT_TYPE, Grants.REFRESH_TOKEN);
        oAuthRequest.add(TokenParams.SCOPE, DEFAULT_SCOPES + " " + scope);
        oAuthRequest.add(TokenParams.REFRESH_TOKEN, refreshToken);
        String basicAuthorizationHeader = getBasicAuthorizationHeader(clientId, clientSecret);
        return OAuthService.invokeTokenCall(runner, stsClient, basicAuthorizationHeader, oAuthRequest);
    }

    public static WellKnownResponseDTO invokeWellKnownEndpoint(TestActionRunner runner, HttpClient stsClient) {

        return OAuthService.invokeWellKnownEndpoint(runner, stsClient);
    }
    private static String getAuthCodeRequestPath(String authCodeRequestUrl) throws MalformedURLException {

        URL url = new URL(authCodeRequestUrl);
        return url.getPath() + "?" + url.getQuery();
    }

    private static String extractAuthCode(String authCodeUrl) throws MalformedURLException {

        String query = new URL(authCodeUrl).getQuery();
        String[] queryParams = query.split("&");
        for (String queryParam : queryParams) {
            String[] paramParts = queryParam.split("=");
            if (paramParts[0].equals("code")) {
                return paramParts[1];
            }
        }
        return null;
    }

    private static String constructAuthorizeUrl(String clientId, String redirectUrl, String scope
            , String codeChallenge) {

        return STSEndpoints.AUTHORIZE + "?response_type=code&client_id=" + clientId + "&redirect_uri=" +
                redirectUrl + "&scope=" + scope + "&code_challenge=" +
                codeChallenge + "&code_challenge_method=S256";
    }

    public static HttpClient createStsClient(String stsUrl) {
        return CitrusEndpoints
                .http()
                .client()
                .requestFactory(requestFactory())
                .restTemplate(restTemplate())
                .requestUrl(stsUrl)
                .build();
    }

    private static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(300000);
        ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(300000);
        return restTemplate;
    }

    private static ClientHttpRequestFactory requestFactory() {

        CookieStore cookieStore = new BasicCookieStore();
        Registry<CookieSpecProvider> cookieSpecRegistry = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.DEFAULT, new RFC6265CookieSpecProvider())
                .build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setRedirectsEnabled(false)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieSpecRegistry(cookieSpecRegistry)
                .disableRedirectHandling()
                .setRedirectStrategy(new DefaultRedirectStrategy(new String[0]))
                .setDefaultCookieStore(cookieStore)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private static String getBasicAuthorizationHeader(String clientId, String clientSecret) {
        return "Basic " + java.util.Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}
