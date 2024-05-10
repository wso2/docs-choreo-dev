package com.wso2.choreo.integration.common.keymanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.keymanager.KeyManagerService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.keymanager.ConfigUpdateResponseDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddRequestDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddResponseDTO;
import com.wso2.choreo.integration.models.keymanager.IdpDiscoveryResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManager;
import com.wso2.choreo.integration.models.keymanager.DetailedKeyManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

public class KeyManagerUtils {

    public static ConfigUpdateResponseDTO updateOAuthAppConfiguration(TestActionRunner runner, HttpClient client,
            String oAuthAppId, HashMap<String, Object> configUpdateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.updateKeysetConfigurations(runner, client, oAuthAppId, configUpdateRequest);
    }

    public static List<KeyManager> getKeyManagersAsAdmin(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.getKeyManagersAsAdmin(runner, client, environmentId).getList();
    }

    public static List<DetailedKeyManager> getKeyManagersAsPublisher(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.getKeyManagersAsPublisher(runner, client, environmentId).getList();
    }

    public static IdpDiscoveryResponseDTO getDiscoveryInfo(String wellKnownURL, String type)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeyManagerService.getDiscoveryInfo(wellKnownURL, type);
    }

    public static IdpAddResponseDTO addExternalIdp(IdpAddRequestDTO requestPayload)
            throws URISyntaxException, TokenRetrievalException, IOException {

        return KeyManagerService.addExternalIdp(requestPayload);
    }

    public static List<KeyManager> getKeyManagersListAsAdmin()
            throws URISyntaxException, IOException, TokenRetrievalException {

        return KeyManagerService.getKeyManagersListAsAdmin().getList();
    }

}
