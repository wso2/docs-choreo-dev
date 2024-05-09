package com.wso2.choreo.integration.common.keymanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.keymanager.KeyManagerService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.keymanager.KeyManagerConstants.TestKeyGenRequestData;
import com.wso2.choreo.integration.models.keymanager.ConfigUpdateResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class KeyManagerUtils {

    public static KeyGenResponseDTO generateKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, String invokeURL)
            throws TokenRetrievalException, IOException, URISyntaxException {

        HashMap<String, Object> keyGenRequest = new HashMap<>() {
            {
                put("appTokenExpiry", TestKeyGenRequestData.APP_TOKEN_EXPIRY);
                put("callbackUrls", List.of(invokeURL));
                put("grantTypes", TestKeyGenRequestData.GRANT_TYPES);
                put("pkceMandatory", TestKeyGenRequestData.PKCE_MANDATORY);
                put("publicClient", TestKeyGenRequestData.IS_PUBLIC_CLIENT);
                put("refreshTokenExpiry", TestKeyGenRequestData.REFRESH_TOKEN_EXPIRY);
                put("userTokenExpiry", TestKeyGenRequestData.USER_TOKEN_EXPIRY);

            }
        };

        return KeyManagerService.generateKeys(runner, client, projectId, componentId, environmentId, keyGenRequest);
    }

    public static KeyGenResponseDTO regenerateKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, String oAuthAppId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.regenerateKeysets(runner, client, projectId, componentId, environmentId, oAuthAppId);
    }

    public static ConfigUpdateResponseDTO updateOAuthAppConfiguration(TestActionRunner runner, HttpClient client,
            String oAuthAppId, HashMap<String, Object> configUpdateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.updateKeysetConfigurations(runner, client, oAuthAppId, configUpdateRequest);
    }

    public static List<KeyManager> getKeyManagers(TestActionRunner runner, HttpClient client, String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.getKeyManagers(runner, client, environmentId).getList();
    }

    public static void addExternalIdpKeys(TestActionRunner runner, HttpClient client, String projectId,
            String componentId, String environmentId, HashMap<String, Object> keyMappingRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        KeyManagerService.addExternalIdpKeys(runner, client, projectId, componentId, environmentId, keyMappingRequest);
    }

    public static String addConflictingExternalIdpKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, HashMap<String, Object> keyMappingRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {
                
        return KeyManagerService.addConflictingExternalIdpKeys(runner, client, projectId, componentId, environmentId,
                keyMappingRequest);
    }

}
