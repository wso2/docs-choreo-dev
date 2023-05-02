# Manage Applications

An application in Choreo is a logical representation of a physical application such as a mobile app, web app, device, etc. To consume an API in Choreo, you need to create an application that maps to your physical application and then subscribes to the required API over a usage policy plan that gives you a usage quota. A single application can have multiple subscriptions to APIs. Using the consumer key and consumer secret, you can generate an access token that you can use to invoke all the APIs subscribed to the same application.

This section walks you through the steps to create an application in Choreo.

Let's get started!

## Step 1: Create an application

To create an application in the Choreo Developer Portal, follow the steps given below:

1. Sign in to the [Choreo Developer Portal](https://devportal.choreo.dev).   
2. Click **Applications**. and then click **+Create**.
3. Enter the application name and select the usage policy. Optionally, add the application description. 
4. Click **Create**.

This creates the application and opens the application overview page. You can view the throttling tier, the token type, workflow status, and the application owner of the API. 

## Step 2: Generate keys

Choreo provides an OAuth 2.0 bearer token-based authentication for API access. An API access token/key is a string that is passed as an HTTP header of an API request to authenticate access to the API.

Once you create an application in Choreo, you can generate credentials for it. When you generate credentials for the first time, Choreo provides a consumer key and consumer secret for the application. The consumer key becomes the unique identifier of the application and is used to authenticate the application. 

The following section walks you through the steps to generate an API access token in Choreo. 

### Generate environment-specific keys and tokens 

You can generate keys and tokens to invoke production and non-production endpoints separately.

!!! info "Note"
    The capability to access production endpoints depends on your role. If you have permission to access production endpoints, you can generate keys and tokens to invoke production endpoints.

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.
2. In the **My Applications** page, click on the application for which you want to generate keys and tokens.
3. In the left navigation menu, click the required environment under **Credentials**. This opens the **Application Keys** pane of the specific environment.
4. Click to expand **Advanced Configurations** and review the options.
    - **Grant Types**: Select the grant types to use when generating the access token.
    - **Public Client**: Select **Allow authentication without the client secret** if your application can be considered as a public client such as an application running on a browser or mobile device.
    - **PKCE for enhanced security**: Select **Mandatory** if you want the application to send a code challenge in the authorization request and the corresponding code verifier in the token request. Asgardeo supports SHA-256 and plain.
    - **Application access token expiry time**: Specify the access token expiry time in seconds.
    - **Refresh token expiry time**: Specify the refresh token expiry time in seconds.
    - **ID token expiry time**: Specify the ID token expiry time in seconds.

5. Click **Generate Credentials**. This opens the **Application Keys** pane with values populated for the credentials. 

You can use this consumer key and consumer secret values to generate an API access token by invoking the token endpoint. You can also revoke the access token by invoking the revoke endpoint.

To generate a test token for testing purposes, you can click **Generate Token** and copy the test token that is displayed. Alternatively, click **cURL** and copy the generated cURL command to use via a cURL client and obtain a test token.

!!! warning
        Make sure you do not use the test token in your production environment.



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

Revoking JWT access tokens can be challenging due to the self-validating nature. Once a token is issued, it contains all the necessary information within itself to validate its authenticity, without requiring additional server-side lookups or interactions.

It is recommended to use an expiry time which is not more than 900 seconds.

In traditional session-based authentication, the server can easily revoke a session by invalidating its associated session ID. However, in the case of JWTs, there is no central authority that maintains a list of valid or invalid tokens. As a result, revoking a JWT token requires the use of blacklisting or whitelisting techniques, which can add additional complexity to the authentication flow and may not always be foolproof.

To mitigate these challenges, it is recommended to use short-lived JWT access tokens and regularly refresh them. This reduces the risk of unauthorized access if a token is stolen or leaked, as the token will expire after a short period of time. Additionally, implementing other security measures such as strong encryption and secure token storage can further enhance the security of JWT-based authentication.

The Choreo Developer Portal keeps the lifespan of a token to 15 minutes (900 seconds) by default. Application Developers can increase the time if necessary, but as mentioned above, it is recommended to keep it to the minimal possible value.
