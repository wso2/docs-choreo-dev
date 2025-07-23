
An application in Choreo is a logical representation of a physical application, such as a mobile app, web app, or device. To consume an API in Choreo, you need to create an application that maps to your physical application and subscribe to the required API under a usage policy plan. This plan provides a usage quota. A single application can have multiple API subscriptions. Using the consumer key and consumer secret, you can generate an access token to invoke all APIs subscribed to the same application.

This guide walks you through the steps to create an application in Choreo.

## Step 1: Create an application

To create an application in the Choreo Developer Portal, follow these steps:

1. Go to the [Choreo Developer Portal](https://devportal.choreo.dev) and sign in.

2. In the Developer Portal header, click **Applications** and then click **+Create**.

3. Enter application details. Provide a name and description for your application.

4. Click **Create**.

This creates the application and opens the **Application Overview** page. Here, you can view details such as the token type, workflow status, and the application owner.

## Step 2: Generate keys

Choreo uses OAuth 2.0 bearer token-based authentication for API access. An API access token is a string passed as an HTTP header in API requests to authenticate access.

Once you create an application, you can generate credentials for it. Choreo provides a consumer key and consumer secret when you generate credentials for the first time. The consumer key acts as the unique identifier for the application and is used for authentication.

### Generate environment-specific keys and tokens

You can generate keys and tokens to invoke production and non-production endpoints separately.

!!! info "Note"
    Access to production endpoints depends on your role. If you have the necessary permissions, you can generate keys and tokens for production endpoints.

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.

2. On the **My Applications** page, click on the application for which you want to generate keys and tokens.

3. In the left navigation menu, click the desired environment under **Credentials**. This opens the **Application Keys** pane for that environment.

4. Expand **Advanced configurations** and review the following options:
    - **Grant types**: Select the grant types to use when generating the access token.
    - **Public client**: Enable **Allow authentication without the client secret** if your application is a public client (e.g., a browser or mobile app).
    - **PKCE for enhanced security**: Set to **Mandatory** if you want the application to send a code challenge in the authorization request and a code verifier in the token request. Asgardeo supports SHA-256 and plain.
    - **Application access token expiry time**: Set the access token expiry time in seconds.
    - **Refresh token expiry time**: Set the refresh token expiry time in seconds.
    - **ID token expiry time**: Set the ID token expiry time in seconds.

5. Click **Generate Credentials**. The **Application Keys** pane will display the consumer key and consumer secret.

You can use the consumer key and consumer secret to generate an API access token by invoking the token endpoint. You can also revoke the access token by invoking the revoke endpoint.

To generate a test token for testing purposes, click **Generate Token** and copy the displayed token. Alternatively, click **cURL** to copy the generated cURL command and obtain a test token using a cURL client.

!!! warning
    Do not use the test token in your production environment.
