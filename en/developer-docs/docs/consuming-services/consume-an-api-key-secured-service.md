# Consume an API Key Secured Service

{% include "discovering-an-api-devportal.md" %}

## Creating an API Key

To consume an API secured with an API Key, an API Key is required. To obtain an API Key, an application must first be created in the Choreo Developer Portal. This application represents a physical entity (such as a mobile app, web app, or device) and serves as the means to subscribe to APIs under a defined usage policy. The API Key is associated with a specific application, and an application can be created during the API Key generation process if needed.

---

### Steps to Create an API Key

1. Navigate to the [Choreo Developer Portal](https://devportal.choreo.dev) and sign in.
2. Click on **APIs** in the Developer Portal header.
3. Select the desired API that requires an API Key for access.
4. This will take you to the API overview page, where you can manage credentials.

#### Generating Environment-Specific API Keys.

Choreo allows you to generate API keys for production and sandbox environments.

!!! note
    Access to production endpoints may be restricted based on your user role. Ensure you have the required permissions before generating production keys.

Follow these steps to generate an API Key:

1. In the left navigation menu, select the desired environment under **Credentials**. The **API Keys** pane for the chosen environment will open.
2. If any API keys already exist, they will be listed here.
3. Click **Generate API Key** and configure the following options:
    - **Key Name**: Provide a suitable name for the API key.
    - **Application**: Select an existing application or create a new one.
    - **Subscription Policy**: Choose an appropriate subscription policy.
4. Click **Generate**. The newly created API Key will be displayed.


!!! note
    If the selected application is already subscribed to the chosen API, the subscription selection step will be skipped.
    If the selected API has multiple endpoints, an endpoint needs to be selected during API key generation.

## Consume an API

Use this API Key to authenticate API requests by including it in the `api-key` header when invoking the API.

Example:
```bash
curl -H "api-key: <YOUR_API_KEY>" -X GET "https://my-sample-api.choreoapis.dev/greet"
```

For managing API keys, see [Manage API Keys](./manage-api-keys.md)