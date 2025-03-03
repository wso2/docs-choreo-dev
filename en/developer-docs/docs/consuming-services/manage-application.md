# Manage Applications

{% include "create-an-application.md" %}

## Grant types

Choreo uses OAuth 2.0 for authentication. In OAuth 2.0, grant types are methods that allow client applications to obtain an access token. The type of grant used depends on the resource owner, the application type, and the trust relationship between the authorization server and the resource owner.

### Authorization code grant

The Authorization Code flow is a secure way for a client application to obtain an access token without exposing the user's credentials. The user authenticates with the authorization server, which issues an authorization code. This code is then exchanged for an access token.

This method protects user credentials and prevents them from being compromised by malicious client applications.

### Refresh token grant

A refresh token allows you to obtain a new access token when the current one expires or when a new token is needed. The refresh token grant type is used for this purpose. Refresh tokens are optional and, if issued, are included in the response along with the access token. You can use the refresh token to request a new access token from the authorization server. Choreo's default authorization server, Asgardeo, issues refresh tokens for all grant types except the **Client Credentials** grant type, as recommended by the OAuth 2.0 specification.

!!! note
    - Treat refresh tokens as securely as access tokens.
    - No user interaction is required to obtain a new access token using the Refresh Token grant type.

### Client credentials grant

The Client Credentials flow allows client applications to obtain an access token without user authentication. This is useful when the client application needs to access its own resources, such as data storage or APIs, but does not require access to user data. Ensure that client credentials are kept secure, as anyone with these credentials can obtain access tokens and access the client's resources.

### Implicit grant

The Implicit Grant flow allows a client application to obtain an access token directly from the authorization server without an intermediate authorization code exchange. This flow is commonly used in browser-based applications.

However, the access token is exposed in the browser's URL fragment, making it vulnerable to attacks like cross-site scripting (XSS). As a result, this flow is not recommended for applications requiring high security.

### Password grant

The Password Grant flow allows a client application to obtain an access token by directly providing the user's username and password to the authorization server. This method is less secure than other grant types because the client application handles and transmits the user's credentials.

This grant type is typically used in highly trusted client applications where user experience is prioritized over security. It is not recommended for public-facing applications or scenarios involving sensitive data.

## Revoke access tokens

Revoking JWT access tokens can be challenging because they are self-validating. Once issued, a token contains all the information needed to validate its authenticity without requiring server-side lookups.

It is recommended to set an expiry time of no more than 900 seconds.

In traditional session-based authentication, the server can revoke a session by invalidating its session ID. However, JWTs do not rely on a central authority to track valid or invalid tokens. Revoking a JWT requires techniques like denylists or allowlists, which can complicate the authentication process and may not always be foolproof.

To address these challenges, use short-lived JWT access tokens and refresh them regularly. This reduces the risk of unauthorized access if a token is stolen or leaked. Additionally, implementing strong encryption and secure token storage can further enhance JWT-based authentication security.

By default, the Choreo Developer Portal sets the token lifespan to 15 minutes (900 seconds). Application developers can increase this time if necessary, but it is recommended to keep it as short as possible.