/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.common;

import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Handles retrieving a OAuth token to test API calls
 */
public class TokenHandler {
    private static final HttpClient client = HttpClient.newHttpClient();

    private final String asgardeoClientId = Configuration.ASGARDEO_CLIENT_ID;
    private final String asgardeoClientSecret = Configuration.ASGARDEO_CLIENT_SECRET;
    private final String testUserEmail = Configuration.TEST_USER_EMAIL;
    private final String testUserPassword = Configuration.TEST_USER_PASSWORD;
    private final String stsClientId = Configuration.STS_CLIENT_ID;
    private final String stsClientSecret = Configuration.STS_CLIENT_SECRET;

    /**
     * Retrieve oauth token to be used when invoking choreo APIs
     *
     * @return oauth token
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws InterruptedException    if sending request is interrupted
     * @throws TokenRetrievalException if token retrieval fails
     */
    public String getTestToken() throws InterruptedException, TokenRetrievalException, IOException {
        String userToken = getTestUserToken(asgardeoClientId, asgardeoClientSecret);
        String stsToken = getStsToken(stsClientId, stsClientSecret, userToken);
        return stsToken;
    }

    /**
     * Retrieve oauth token to be used when invoking choreo APIs
     *
     * @param asgardeoClientId     client id for Asgardeo SP
     * @param asgardeoClientSecret client secret for Asgardeo SP
     * @return user token
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws TokenRetrievalException if token retrieval fails
     */
    public String getTestUserToken(String asgardeoClientId, String asgardeoClientSecret)
            throws TokenRetrievalException, IOException {
        String tokenAuthHeader =
                Constant.BASIC_PREFIX.concat(encodeCredentials(asgardeoClientId, asgardeoClientSecret));
        String asgardeoTokenEndpoint = Configuration.ASGARDEO_ENDPOINT.concat(Constant.TOKEN_ENDPOINT_SUFFIX);

        HttpPost request = new HttpPost(asgardeoTokenEndpoint);

        request.setHeader(HttpHeaders.AUTHORIZATION, tokenAuthHeader);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", Constant.OAUTH_PASSWORD_GRANT_TYPE));
        urlParameters.add(new BasicNameValuePair("username", testUserEmail));
        urlParameters.add(new BasicNameValuePair("password", testUserPassword));

        request.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.OK.value()) {
                throw new TokenRetrievalException(statusCode, responseBody);
            }

            return new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonPrimitive("access_token")
                    .getAsString();
        }
    }

    /**
     * @param stsClientId     client id for STS SP
     * @param stsClientSecret client secret for STS SP
     * @param userToken       test user token
     * @return sts access token
     * @throws TokenRetrievalException
     * @throws IOException
     */
    public String getStsToken(String stsClientId, String stsClientSecret, String userToken)
            throws TokenRetrievalException, IOException {
        String tokenAuthHeader = Constant.BASIC_PREFIX.concat(encodeCredentials(stsClientId, stsClientSecret));
        String stsEndPoint = Configuration.STS_ENDPOINT.concat(Constant.TOKEN_ENDPOINT_SUFFIX);

        HttpPost request = new HttpPost(stsEndPoint);

        request.setHeader(HttpHeaders.AUTHORIZATION, tokenAuthHeader);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", Constant.OAUTH_TOKEN_EXCHANGE_GRANT_TYPE));
        urlParameters.add(new BasicNameValuePair("subject_token", userToken));
        urlParameters.add(new BasicNameValuePair("subject_token_type", Constant.SUBJECT_TOKEN_TYPE));
        urlParameters.add(new BasicNameValuePair("requested_token_type", Constant.REQUESTED_TOKEN_TYPE));
        urlParameters.add(new BasicNameValuePair("orgHandle", Configuration.TEST_CHOREO_ORG_HANDLE));
        urlParameters.add(new BasicNameValuePair("scope", Constant.OAUTH_SCOPES));

        request.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode != HttpStatus.OK.value()) {
                throw new TokenRetrievalException(statusCode, responseBody);
            }

            return new JsonParser().parse(responseBody).getAsJsonObject().getAsJsonPrimitive("access_token")
                    .getAsString();
        }
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
