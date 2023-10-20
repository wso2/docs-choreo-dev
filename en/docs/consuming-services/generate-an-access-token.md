# Generate an Access Token

Using access tokens for request authorization strengthens security measures, particularly in preventing certain types of denial-of-service (DoS) attacks aimed at published APIs. API consumers generate access tokens to access the API, incorporating them into their HTTP header requests as simple string values.

When you register an application on the Developer Portal, you can generate a consumer key and a consumer secret for it. The consumer key and the consumer secret represent the credentials of the application. Similar to a user's username, the consumer key becomes the unique identifier of the application so that you can use it to authenticate the request to the API. Choreo issues an access token for the application against the mentioned consumer key.

This section walks you through the steps to generate an access token for your application in Choreo.

Let's get started!

## Prerequisites

Before you try out this guide, be sure you have the following:

1. An application in the [Choreo Developer Portal](https://devportal.choreo.dev). If you do not have an application created, [create a new application](https://wso2.com/choreo/docs/consuming-services/manage-application/#step-1-create-an-application)
2. [Generate keys for the application](https://wso2.com/choreo/docs/consuming-services/create-an-application/#step-2-generate-keys). 
3. [Subscribe APIs to the application](https://wso2.com/choreo/docs/consuming-services/create-a-subscription/#manage-subscriptions). 


## Generate an access token via cURL

You can follow the steps below to generate an access token for your application via cURL: 

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.
2. On the **My Applications** page, click on the application for which you want to generate the token.
3. In the left navigation menu, click the required environment under **Credentials**. This opens the **Application Keys** pane of the specific environment.
4. Copy the **Consumer Key**, **Consumer Secret**, and **Token Endpoint** values.
5. Use the following template and compile the cURL command with the values you copied in the above step. 

    === "Format"
    ```
    curl -k -X POST <token_endpoint> -d "grant_type=client_credentials" -H "Authorization: Basic <base64encode(consumer-key:consumer-secret)>"
    ```
6. Execute the  cURL command to generate an access token. 

## Generate an access token via the Choreo Console (for testing purposes)

You can follow the steps below to generate an access token for **testing purposes**: 

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.
2. On the **My Applications** page, click on the application for which you want to generate keys and tokens.
3. In the left navigation menu, click the required environment under **Credentials**. This opens the **Application Keys** pane of the specific environment.
4. Click **Generate Token**.