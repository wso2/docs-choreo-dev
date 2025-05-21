/*
 *
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 *  You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.choreo.integration.common.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * Email searching util functions for test cases. It is based on the Google gmail rest api.
 */
public class RestAPIBasedEmailUtils {
    private static final Logger log = LogManager.getLogger(RestAPIBasedEmailUtils.class);

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);
    private static final String GMAIL_AUTH_URI = "https://accounts.google.com/o/oauth2/auth";
    private static final String GMAIL_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final int DEFAULT_RE_TRY_COUNT = 300;

    private static final String DEFAULT_USER_ID = "me";
    private static final String DEFAULT_INBOX_LABEL = "INBOX";
    private static final String SUBJECT_SEARCH_QUERY_PREFIX = "subject:";

    private final GoogleClientSecrets googleClientSecrets;
    private final String refreshToken;

    public RestAPIBasedEmailUtils(String clientId, String clientSecret, String refreshToken) {
        this.refreshToken = refreshToken;
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        details.setAuthUri(GMAIL_AUTH_URI);
        details.setTokenUri(GMAIL_TOKEN_URI);
        googleClientSecrets = new GoogleClientSecrets();
        googleClientSecrets.setInstalled(details);
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        InMemoryDataStoreFactory factory = new InMemoryDataStoreFactory(this.refreshToken);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(HTTP_TRANSPORT, JSON_FACTORY, this.googleClientSecrets, SCOPES)
                        .setDataStoreFactory(factory)
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private List<Message> searchMailBySubject(String label, String subject)
            throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Gmail.Users.Messages.List request = service.users().messages().list(DEFAULT_USER_ID);
        request.setLabelIds(List.of(label));
        request.setUserId(DEFAULT_USER_ID);
        request.setMaxResults(1L);
        request.setQ(SUBJECT_SEARCH_QUERY_PREFIX.concat(subject));
        ListMessagesResponse response = request.execute();
        return response.getMessages();
    }

    public boolean reTrySearch(String searchText) throws Exception {
        return reTrySearch(DEFAULT_INBOX_LABEL, searchText);
    }

    public boolean reTrySearch(String label, String searchText) throws Exception {
        int i = 0;
        while (i++ < DEFAULT_RE_TRY_COUNT) {
            List<Message> messages = searchMailBySubject(label, searchText);
            if (messages != null && messages.size() > 0) {
                log.info("Found '{}' emails for the given criteria", messages.size());
                // todo: list the email subjects
                return true;
            }
            if (i % 10 == 0) {
                log.info("Searching emails with search text '{}' in the subject.", searchText);
            }
            Thread.sleep(1000);
        }
        return false;
    }
}
