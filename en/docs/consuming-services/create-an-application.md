An application in Choreo is a logical representation of a physical application such as a mobile app, web app, device, etc. To consume an API in Choreo, you need to create an application that maps to your physical application and then subscribes to the required API over a usage policy plan that gives you a usage quota. A single application can have multiple subscriptions to APIs. Using the consumer key and consumer secret, you can generate an access token that you can use to invoke all the APIs subscribed to the same application.

This section walks you through the steps to create an application in Choreo.

Let's get started!

### Step 1: Create an application

To create an application in the Choreo Developer Portal, follow the steps given below:

1. Sign in to the [Choreo Developer Portal](https://devportal.choreo.dev).   
2. Click **Applications**. and then click **+Create**.
3. Enter a name and description for the application. 
4. Click **Create**.

This creates the application and opens the application overview page. You can view details such as the token type, workflow status, and the application owner of the API. 

### Step 2: Generate keys

Choreo provides an OAuth 2.0 bearer token-based authentication for API access. An API access token/key is a string that is passed as an HTTP header of an API request to authenticate access to the API.

Once you create an application in Choreo, you can generate credentials for it. When you generate credentials for the first time, Choreo provides a consumer key and consumer secret for the application. The consumer key becomes the unique identifier of the application and is used to authenticate the application. 

The following section walks you through the steps to generate an API access token in Choreo. 

#### Generate environment-specific keys and tokens 

You can generate keys and tokens to invoke production and non-production endpoints separately.

!!! info "Note"
    The capability to access production endpoints depends on your role. If you have permission to access production endpoints, you can generate keys and tokens to invoke production endpoints.

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.
2. On the **My Applications** page, click on the application for which you want to generate keys and tokens.
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