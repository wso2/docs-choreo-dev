# Consume a OAuth2 Secured Service

{% include "discovering-an-api-devportal.md" %}

## Create an application

{% include "create-an-application.md" %}

## Subscribe to an API

{% include "create-a-subscription.md" %}

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

## Consume an API

Use this generated access token to authenticate API requests by including it in the `Bearer` header when invoking the API.

Example:
    
```bash
curl -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" -X GET "https://my-sample-api.choreoapis.dev/greet"  
```

!!! note
    The name of the Authorization header may vary depending on the API provider’s configuration. Always refer to the API’s Swagger (OpenAPI) definition for the correct header format.
