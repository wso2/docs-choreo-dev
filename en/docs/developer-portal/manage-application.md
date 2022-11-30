# Manage Applications

An application in Choreo is a logical representation of a physical application such as a mobile app, web app, device, etc. To consume an API in Choreo, you need to create an application that maps to your physical application and then subscribes to the required API over a usage policy plan that gives you a usage quota. A single application can have multiple subscriptions to APIs. Using the consumer key and consumer secret, you can generate an access token that you can use to invoke all the APIs subscribed to the same application.

This section will walk you through the steps to create an application in Choreo.

Let's get started!

## Create an application

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, you can click on the Developer Portal link on the Choreo console header. 
    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-small}
2. Click **Applications** and then, click **+Create**.
3. Enter the application name, select the usage policy, and optionally, add the application description. Click **Create**.
4. You are redirected to the application overview page. You can view the throttling tier, the token type, workflow status, and the application owner of the API. 

## Generate Keys

Choreo provides an OAuth 2.0 bearer token-based authentication for API access. An API access token/key is a string that is passed as an HTTP header of an API request to authenticate the API access.

Once you create an application in Choreo, you can then generate the credentials for the application. When you generate the credentials for the first time, Choreo gives you a consumer key and consumer secret for the application. The consumer key becomes the unique identifier of the application, similar to a user's user name, and is used to authenticate the application or user. You can use this consumer key and consumer secret to generating an API access token by invoking the token endpoint. You can also revoke the access token by invoking the revoke endpoint. For testing purposes, you can generate a test token via the UI. However, we strongly recommend not to use the test token in your production environment.

This section will walk you through the steps to generating an API access token in Choreo. 

Let's get started!

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click the Developer Portal link on the Choreo console header. 
    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-small}
2. Click **Applications**.
3. Click **Production Keys** -> **OAuth 2.0 Tokens** on the left panel.
4. Expand the **Advanced Configurations** section and review the options. 

       ![Advanced Configurations](../assets/img/developer-portal/manage-applications/advanced-configurations.png){.cInlineImage-half}

   - **Grant Types:** The grant types used to generate the access token.
   - **Public Client:** Identify the application as a public client to allow authentication without a client secret. You can use this for applications  running on a browser or mobile device. 
   - **Application access token expiry time:** The access token expiry time (seconds).
   - **Refresh token expiry time:** The refresh token expiry time (seconds).
   - **ID token expiry time:** ID token expiry time (seconds).

5. Click **Generate Credentials** to generate the credentials for the application for the first time. 
6. Once you generate the keys, you can find the application's consumer key and consumer secret. 

       ![Credentials](../assets/img/developer-portal/manage-applications/credentials.png){.cInlineImage-small}

7. You can use the UI to generate a test token using the consumer key and consumer secret for the application, only for test purposes. We strongly recommend **NOT** using this token in your production environment. 
8. Click **:// CURL** to copy the cURL command template for generating the access token. You can add the consumer key and consumer secret to the URL and invoke the token endpoint to generate an API access token.

    ![cURL command](../assets/img/developer-portal/manage-applications/curl-command.png){.cInlineImage-small}
