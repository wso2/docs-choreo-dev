package com.wso2.choreo.integration.common.keysetmanagement;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.wso2.choreo.integration.apis.keysetmanagement.KeysetManagementService;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.models.keymanager.OAuthAppUpdateResponseDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddRequestDTO;
import com.wso2.choreo.integration.models.keymanager.IdpAddResponseDTO;
import com.wso2.choreo.integration.models.keymanager.IdpDiscoveryResponseDTO;
import com.wso2.choreo.integration.models.keymanager.KeyManager;
import com.wso2.choreo.integration.models.keymanager.DetailedKeyManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for Key Manager related tests.
 */
public class KeysetManagementUtils {

    /**
     * Update OAuth App configuration.
     *
     * @param runner              Citrus test runner
     * @param client              Citrus http client
     * @param oAuthAppId          OAuth App ID
     * @param configUpdateRequest Configuration update request
     * @return OAuthAppUpdateResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static OAuthAppUpdateResponseDTO updateOAuthAppConfiguration(TestActionRunner runner, HttpClient client,
            String oAuthAppId, HashMap<String, Object> configUpdateRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeysetManagementService.updateKeysetConfigurations(runner, client, oAuthAppId, configUpdateRequest);
    }

    /**
     * Get Key Managers as an admin.
     *
     * @param runner        Citrus test runner
     * @param client        Citrus http client
     * @param environmentId Environment ID
     * @return List of KeyManager objects
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static List<KeyManager> getKeyManagersAsAdmin(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeysetManagementService.getKeyManagersAsAdmin(runner, client, environmentId).getList();
    }

    /**
     * Get Key Managers as a publisher.
     *
     * @param runner        Citrus test runner
     * @param client        Citrus http client
     * @param environmentId Environment ID
     * @return List of DetailedKeyManager objects
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static List<DetailedKeyManager> getKeyManagersAsPublisher(TestActionRunner runner, HttpClient client,
            String environmentId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeysetManagementService.getKeyManagersAsPublisher(runner, client, environmentId).getList();
    }

    /**
     * These non Citrus based methods are to be used in cases where the Citrus
     * framework is yet to be initialized, such as in the BeforeSuite
     */

    /**
     * Get IdP discovery information.
     *
     * @param wellKnownURL Well-known URL
     * @param type         Type
     * @return IdpDiscoveryResponseDTO object
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     * @throws URISyntaxException      If an error occurs while creating the URI
     */
    public static IdpDiscoveryResponseDTO getDiscoveryInfo(String wellKnownURL, String type)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return KeysetManagementService.getDiscoveryInfo(wellKnownURL, type);
    }

    /**
     * Add an external IdP.
     *
     * @param requestPayload IdP add request payload
     * @return IdpAddResponseDTO object
     * @throws URISyntaxException      If an error occurs while creating the URI
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     */
    public static IdpAddResponseDTO addExternalIdp(IdpAddRequestDTO requestPayload)
            throws URISyntaxException, TokenRetrievalException, IOException {

        return KeysetManagementService.addExternalIdp(requestPayload);
    }

    /**
     * Get Key Managers list as an admin.
     *
     * @return List of KeyManager objects
     * @throws URISyntaxException      If an error occurs while creating the URI
     * @throws TokenRetrievalException If an error occurs while retrieving the token
     * @throws IOException             If an error occurs while reading the response
     */
    public static List<KeyManager> getKeyManagersListAsAdmin()
            throws URISyntaxException, IOException, TokenRetrievalException {

        return KeysetManagementService.getKeyManagersListAsAdmin().getList();
    }
}
