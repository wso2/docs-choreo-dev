# Generate an Access Token

Using access tokens for request authorization enhances security by preventing certain types of denial-of-service (DoS) attacks on published APIs. API consumers generate access tokens to access APIs, including them as string values in HTTP header requests.

When you register an application in the Developer Portal, you can generate a consumer key and consumer secret. These credentials represent the application's identity. The consumer key acts as the unique identifier for the application, similar to a username, and is used to authenticate API requests. Choreo issues an access token for the application based on the consumer key.

This guide walks you through the steps to generate an access token for your application in Choreo.

## Prerequisites

Before proceeding, ensure you have the following:

1. An application in the [Choreo Developer Portal](https://devportal.choreo.dev). If you don’t have one, [create a new application](https://wso2.com/choreo/docs/consuming-services/manage-application/#step-1-create-an-application).
2. [Generate keys for the application](https://wso2.com/choreo/docs/consuming-services/create-an-application/#step-2-generate-keys).
3. [Subscribe APIs to the application](https://wso2.com/choreo/docs/consuming-services/create-a-subscription/#manage-subscriptions).

## Generate an access token via curl

Follow these steps to generate an access token for your application using cURL:

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.

2. On the **My Applications** page, click on the application for which you want to generate the token.

3. In the left navigation menu, click the desired environment under **Credentials**. This opens the **Application Keys** pane for that environment.

4. Copy the **Consumer key**, **Consumer secret**, and **Token endpoint** values.

5. Use the following template and replace the placeholders with the values you copied:

    === "Format"
    ```bash
    curl -k -X POST <token_endpoint> -d "grant_type=client_credentials" -H "Authorization: Basic <base64encode(consumer-key:consumer-secret)>"
    ```

6. Run the curl command to generate an access token.

## Generate an access token via the Developer Portal UI (for testing)

To generate an access token for **testing purposes**, follow these steps:

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.

2. On the **My Applications** page, click on the application for which you want to generate keys and tokens.

3. In the left navigation menu, click the desired environment under **Credentials**. This opens the **Application Keys** pane for that environment.

4. Click **Generate Token** to create a test access token.
