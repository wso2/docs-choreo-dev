# Manage Applications

{% include "create-an-application.md" %}

## Grant types

Choreo authentication is based on OAuth 2.0. In OAuth 2.0, grant types are methods that allow client applications to obtain an access token depending on the type of the resource owner, the type of the application, and the trust relationship between the authorization server and the resource owner. 

### Authorization code grant

The Authorization code flow provides a secure way for a client application to obtain an access token without exposing the user's credentials to the client application. The user only authenticates with the authorization server, which then issues an authorization code that can be exchanged for an access token.

This helps to protect user credentials and prevents credentials from being compromised by malicious client applications.

### Refresh token grant

A refresh token is a token that you can use to get a new access token when your current access token is expired or when you need a new access token. You can use the refresh token grant type for this purpose. Issuing a refresh token is optional for the authorization server. If the authorization server issues a refresh token, it includes it in the response with the access token. You can use this refresh token and send it to the authorization server to obtain a new access token. Choreo's default authorization server, Asgardeo, issues refresh tokens for all grant types other than the **client credentials** grant type, as recommended by the OAuth 2.0 specification.

!!! note
    
    - Keep your refresh token private, similar to the access token. 
    - The process to get a new access token using the Refresh Token grant type requires no user interaction.

### Client credentials grant

The client credentials flow provides a secure way for client applications to obtain an access token without user authentication. This is useful in scenarios where the client application needs to access its own resources, such as data storage or APIs, but does not require access to user data. However, it is important to ensure that the client credentials are kept secure because any party who has these credentials can obtain access tokens and access the client's resources.

### Implicit grant

The implicit grant flow is an OAuth 2.0 grant type that enables a client application to obtain an access token directly from the authorization server without an intermediate authorization code exchange. This flow is commonly used in browser-based applications where the client application runs in a web browser.

However, it is important to note that the access token is exposed in the browser's URL fragment, which can make it vulnerable to certain types of attacks, such as cross-site scripting (XSS). As a result, this flow is typically not recommended for applications that require high security.

### Password grant

The password grant flow is an OAuth 2.0 grant type that enables a client application to obtain an access token by presenting the user's username and password directly to the authorization server. This flow is generally considered less secure than other grant types, as it requires the client application to handle and transmit the user's credentials.

The password grant is primarily used in scenarios where the client application is highly trusted, and the user experience is prioritized over security concerns. It is generally not recommended for use in public-facing applications or scenarios where sensitive data is accessed.


## Revoke access tokens

Revoking JWT access tokens can be challenging due to their self-validating nature. Once a token is issued, it contains all the necessary information within itself to validate its authenticity, without requiring additional server-side lookups or interactions.

It is recommended to use an expiry time that is not more than 900 seconds.

In traditional session-based authentication, the server can easily revoke a session by invalidating its associated session ID. However, in the case of JWTs, there is no central authority that maintains a list of valid or invalid tokens. As a result, revoking a JWT token requires the use of blacklisting or whitelisting techniques, which can add additional complexity to the authentication flow and may not always be foolproof.

To mitigate these challenges, it is recommended to use short-lived JWT access tokens and regularly refresh them. This reduces the risk of unauthorized access if a token is stolen or leaked, as the token will expire after a short period of time. Additionally, implementing other security measures such as strong encryption and secure token storage can further enhance the security of JWT-based authentication.

The Choreo Developer Portal keeps the lifespan of a token to 15 minutes (900 seconds) by default. Application Developers can increase the time if necessary, but as mentioned above, it is recommended to keep it to the minimal possible value.
