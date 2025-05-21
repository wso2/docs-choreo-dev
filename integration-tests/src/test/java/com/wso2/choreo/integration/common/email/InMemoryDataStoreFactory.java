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

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

import java.io.IOException;

/**
 * Custom data store to maintain access token.
 */
public class InMemoryDataStoreFactory extends AbstractDataStoreFactory {
    private final StoredCredential credential;
    public InMemoryDataStoreFactory(String refreshToken) {
        credential = new StoredCredential();
        credential.setRefreshToken(refreshToken);
    }

    @Override
    protected DataStore createDataStore(String id) throws IOException {
        InMemoryDataStore dataStore = new InMemoryDataStore(this, id);
        dataStore.set("user", credential);
        return dataStore;
    }

    static class InMemoryDataStore extends AbstractMemoryDataStore<StoredCredential> {
        InMemoryDataStore(DataStoreFactory dataStore, String id) {
            super(dataStore, id);
        }
    }
}
