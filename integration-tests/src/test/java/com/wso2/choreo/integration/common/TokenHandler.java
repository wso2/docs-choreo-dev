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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * Handles retrieving a OAuth token to test API calls.
 */
public class TokenHandler {



    static class Scopes {
        @JsonProperty("scopes")
        List<String> scopes;
    }

    public static class Builder {
        private String asgardeoClientId;
        private String asgardeoClientSecret;
        private final String testChoreoOrgHandle;
        private final String testUserEmail;
        private final String testUserPassword;
        private String cpAppClientId;
        private String cpAppClientSecret;

        public Builder(String testChoreoOrgHandle, String testUserEmail, String testUserPassword) {
            this.testChoreoOrgHandle = testChoreoOrgHandle;
            this.testUserEmail = testUserEmail;
            this.testUserPassword = testUserPassword;
        }

        public Builder asgardeoClientId(String asgardeoClientId) {
            this.asgardeoClientId = asgardeoClientId;
            return this;
        }

        public Builder asgardeoClientSecret(String asgardeoClientSecret) {
            this.asgardeoClientSecret = asgardeoClientSecret;
            return this;
        }

        public Builder cpAppClientId(String cpAppClientId) {
            this.cpAppClientId = cpAppClientId;
            return this;
        }

        public Builder cpAppClientSecret(String cpAppClientSecret) {
            this.cpAppClientSecret = cpAppClientSecret;
            return this;
        }

        public TokenHandler build() {
            return new TokenHandler(this);
        }
    }

    private String asgardeoClientId;
    private String asgardeoClientSecret;
    private String testChoreoOrgHandle;
    private String testUserEmail;
    private String testUserPassword;
    private String cpAppClientId;
    private String cpAppClientSecret;

    private String stsAccessToken = "";
    private long tokenExpiryTime = 0;

    private boolean isManualMode = false;

    private TokenHandler(Builder builder) {
        asgardeoClientId = builder.asgardeoClientId;
        asgardeoClientSecret = builder.asgardeoClientSecret;
        testChoreoOrgHandle = builder.testChoreoOrgHandle;
        testUserEmail = builder.testUserEmail;
        testUserPassword = builder.testUserPassword;
        cpAppClientId = builder.cpAppClientId;
        cpAppClientSecret = builder.cpAppClientSecret;
    }

    public TokenHandler(String accessToken) {
        stsAccessToken = accessToken;
        isManualMode =  true;
    }


    /**
     * Retrieve oauth token to be used when invoking Control Plane exposed choreo APIs
     *
     * @return oauth token
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws TokenRetrievalException if token retrieval fails
     */
    public String getTestTokenForCPAPIs() throws TokenRetrievalException, IOException, URISyntaxException {
        if (!isManualMode) {
            if (!isTokenValid()) {
                synchronized (TokenHandler.class) {
                    if (!isTokenValid()) {
                        String userToken = getTestUserToken(asgardeoClientId, asgardeoClientSecret);
                        stsAccessToken = getStsToken(cpAppClientId, cpAppClientSecret, userToken);
                        readTokenExpiryTime();
                    }
                }
            }
        }

        return Constant.BEARER_PREFIX.concat(stsAccessToken);
    }

    public String getAsgardeoUserToken(String orgHandle) throws TokenRetrievalException, IOException, URISyntaxException {
        synchronized (TokenHandler.class) {
            return getOrgSpecificTestUserToken(asgardeoClientId, asgardeoClientSecret, orgHandle);
        }
    }

    /**
     * Re-retrieve oauth token to be used when invoking Control Plane exposed choreo APIs
     *
     * @return oauth token
     * @throws IOException             if an IO error occurs when sending or receiving request
     * @throws TokenRetrievalException if token retrieval fails
     */
    public String refetchTestTokenForCPAPIs() throws TokenRetrievalException, IOException, URISyntaxException {
        
        if (!isManualMode) {
            synchronized (TokenHandler.class) {
                String userToken = getTestUserToken(asgardeoClientId, asgardeoClientSecret);
                stsAccessToken = getStsToken(cpAppClientId, cpAppClientSecret, userToken);
                readTokenExpiryTime();
            }
        }

        return Constant.BEARER_PREFIX.concat(stsAccessToken);
    }

    /**
     * Retrieve oauth token to be used when invoking choreo APIs
     *
     * @param asgardeoClientId     client id for Asgardeo SP
     * @param asgardeoClientSecret client secret for Asgardeo SP
     * @return user token
     * @throws TokenRetrievalException if token retrieval fails
     */
    private String getTestUserToken(String asgardeoClientId, String asgardeoClientSecret) throws TokenRetrievalException {
        String tokenAuthHeader = Constant.BASIC_PREFIX.concat(encodeCredentials(asgardeoClientId, asgardeoClientSecret));
        String asgardeoTokenEndpoint = Configuration.getConfig(ConfigDefinition.ASGARDEO_ENDPOINT).concat(Constant.TOKEN_ENDPOINT_SUFFIX);

        HttpPost request = new HttpPost(asgardeoTokenEndpoint);

        request.setHeader(HttpHeaders.AUTHORIZATION, tokenAuthHeader);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", Constant.OAUTH_PASSWORD_GRANT_TYPE));
        urlParameters.add(new BasicNameValuePair("username", testUserEmail));
        urlParameters.add(new BasicNameValuePair("password", testUserPassword));

        try {
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
        } catch (IOException e) {
            throw new TokenRetrievalException("Error while getting Asgardio token", e);
        }
    }

    private String getOrgSpecificTestUserToken(String asgardeoClientId, String asgardeoClientSecret, String orgHandle)
            throws TokenRetrievalException {
        String tokenAuthHeader = Constant.BASIC_PREFIX.concat(encodeCredentials(asgardeoClientId, asgardeoClientSecret));
        String asgardeoTokenEndpoint = Configuration.getConfig(ConfigDefinition.ASGARDEO_ENDPOINT)
                .concat("/t/" + orgHandle).concat(Constant.TOKEN_ENDPOINT_SUFFIX);

        HttpPost request = new HttpPost(asgardeoTokenEndpoint);

        request.setHeader(HttpHeaders.AUTHORIZATION, tokenAuthHeader);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", Constant.OAUTH_PASSWORD_GRANT_TYPE));
        urlParameters.add(new BasicNameValuePair("username", testUserEmail));
        urlParameters.add(new BasicNameValuePair("password", testUserPassword));
        urlParameters.add(new BasicNameValuePair("scope", "openid email internal_user_mgt_create internal_user_mgt_delete internal_user_mgt_list internal_user_mgt_view internal_user_mgt_update internal_org_user_mgt_view internal_org_user_mgt_list internal_org_user_mgt_update internal_org_user_mgt_create internal_org_user_mgt_delete"));

        try {
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
        } catch (IOException e) {
            throw new TokenRetrievalException("Error while getting Asgardio token", e);
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
    private String getStsToken(String stsClientId, String stsClientSecret, String userToken)
            throws TokenRetrievalException, URISyntaxException, IOException {
        String tokenAuthHeader = Constant.BASIC_PREFIX.concat(encodeCredentials(stsClientId, stsClientSecret));
        String stsEndPoint = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT)
                .concat(Constant.TOKEN_ENDPOINT_SUFFIX);

        HttpPost request = new HttpPost(stsEndPoint);

        request.setHeader(HttpHeaders.AUTHORIZATION, tokenAuthHeader);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", Constant.OAUTH_TOKEN_EXCHANGE_GRANT_TYPE));
        urlParameters.add(new BasicNameValuePair("subject_token", userToken));
        urlParameters.add(new BasicNameValuePair("subject_token_type", Constant.SUBJECT_TOKEN_TYPE));
        urlParameters.add(new BasicNameValuePair("requested_token_type", Constant.REQUESTED_TOKEN_TYPE));
        urlParameters.add(new BasicNameValuePair("orgHandle", testChoreoOrgHandle));
        urlParameters.add(new BasicNameValuePair("scope", getOAuthScopes()));
        urlParameters.add(new BasicNameValuePair("client_id", stsClientId));


        try {
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
        } catch (IOException e) {
            throw new TokenRetrievalException("Error while getting Choreo STS token", e);
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

    private boolean isTokenValid() {
        if (!stsAccessToken.isEmpty()) {
            long currentTime = Instant.now().getEpochSecond();

            return tokenExpiryTime - currentTime > 600;
        }

        return false;
    }

    private void readTokenExpiryTime() {
        String[] splits = stsAccessToken.split("\\.");

        if (splits.length != 3) {
            throw new IllegalStateException("Access token does not consist of 3 parts");
        }

        String payload = new String(Base64.getDecoder().decode(splits[1]));
        tokenExpiryTime = new JsonParser().parse(payload).getAsJsonObject().getAsJsonPrimitive("exp").getAsLong();
    }


    private static String getOAuthScopes() throws URISyntaxException, IOException {
        List<String> scopes = readScopesFromConfig();

        StringBuilder stringBuilder = new StringBuilder();
        for (String scope : scopes) {
            stringBuilder.append(scope).append(" ");
        }

        return stringBuilder.toString().trim();
    }

    private static List<String> readScopesFromConfig() throws URISyntaxException, IOException {
        String scopesYaml = Configuration.getConfig(ConfigDefinition.TOKEN_SCOPES);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        Scopes scopes = mapper.readValue(new File(Objects.requireNonNull(TokenHandler.class.getClassLoader().
                getResource(scopesYaml)).toURI()), Scopes.class);

        return  scopes.scopes;
    }


}
