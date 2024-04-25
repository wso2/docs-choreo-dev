# Sharing and Reusing Services

Choreo allows you to share and reuse your services, promoting faster development and increased efficiency in building integrated applications, through Connections.

Connections in Choreo allows you to integrate Choreo components, or to integrate Choreo components to external services or resources. Connections provide a simple and uniform way to integrate with services and resources.

To create a service follow the [Create a Connection](create-a-connection.md) guide.

To learn more about Choreo Connections refer to the [documentation](../../choreo-concepts/connections.md).

## Consuming a service through a connection

You can consume a Choreo-deployed service within another service. Consuming connections from within Choreo services is seamless and straightforward. Follow the steps below to consume a Choreo service:

### Step 1: Add connection configurations

To integrate another service to your application follow the steps below:

1. Copy and paste the snippet shown in the developer guide into the `component-config` file under the `spec` section.

A sample snippet is shown below:

``` yaml

outbound:
    serviceReferences:
    - name: <SERVICE_NAME>
      connectionConfig: <CONNECTION_ID>
      env:
      - from: ServiceURL
        to: <YOUR_ENV_VARIABLE_NAME_HERE>
      - from: ConsumerKey
        to: <YOUR_ENV_VARIABLE_NAME_HERE>
      - from: ConsumerSecret
        to: <YOUR_ENV_VARIABLE_NAME_HERE>
      - from: TokenURL
        to: <YOUR_ENV_VARIABLE_NAME_HERE>

```

| Field            | Description                                                 |
|------------------|-------------------------------------------------------------|
| Name             | The name of the service you are connecting to.               |
| ConnectionConfig | The unique connection id of this connection.                |
| env              | This section encapsulates the environment variable mapping. |
| from             | From value represents the key of the configuration entry.   |
| to               | From value represents the key of the configuration entry.   |


2. Replace `<YOUR_ENV_VARIABLE_NAME_HERE>` with the appropriate environment variable name of your choice. If you've previously added an outbound service reference, append this as another item under serviceReferences. 

Upon deploying the component, Choreo will automatically create a subscription (if applicable) and populate the specified environment variables with actual values.


The below table details the configuration keys associated with this connection:

| Name           |  Type      |  Description                          |isOptional     | isSensitive  |
|----------------|------------|---------------------------------------|---------------|--------------|
| ServiceURL     | string     | Service URL of the Choreo service     | false         | false        |
| ConsumerKey    | string     | Consumer key of the Choreo service    | false         | false        |
| ConsumerSecret | string     | Consumer secret of the Choreo service | false         | true         |
| TokenURL       | string     | Token URL of the STS                  | false         | false        |


### Step 2: Read configurations within the application

Once you have added the connection configuration snippet, the next step is to read those configurations within your application. The exact steps of that will depend on the language you are using.

The following is a sample code snippet in NodeJS:

``` java
const serviceURL = process.env.SVC_URL;
```

### Step 3: Acquire OAuth 2.0 access token

To consume a Choreo service with the visibility level set to organization or public and secured by the OAuth 2.0 security scheme, you must initially obtain an OAuth 2.0 token from the token endpoint. Subsequently, you can use this token to invoke the service.

#### Languages with OAuth 2.0 - aware HTTP clients

For languages equipped with HTTP clients that support OAuth 2.0, simply pass the OAuth 2.0-related configurations (client id, client secret, etc), obtained when creating the connection to your HTTP client configuration. The HTTP client will autonomously manage token retrieval and refreshing.

#### Languages without OAuth 2.0 - aware HTTP clients

When dealing with programming languages that do not have inherent support for OAuth 2.0 in their HTTP clients, you are required to manually initiate a call to the token endpoint. This involves fetching the token and managing token expiration and refresh directly within your application code. The following is an example curl command illustrating how to obtain a token:


```bash
CONSUMER_KEY="your_consumer_key"
CONSUMER_SECRET="your_consumer_secret"
TOKEN_URL="your_token_url"

# Encode client credentials as Base64
CLIENT_CREDENTIALS=$(echo -n "$CLIENT_ID:$CLIENT_SECRET" | base64)

curl -X POST $TOKEN_URL \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Basic $CLIENT_CREDENTIALS" \
  --data-urlencode "grant_type=client_credentials"

```

### Step 4: Invoke the Service

You can invoke the service as follows:

#### Languages without OAuth 2.0 - aware HTTP clients

For languages equipped with OAuth 2.0 - aware HTTP clients, invoking the service is straightforward. The HTTP client seamlessly manages OAuth 2.0 authentication without requiring additional intervention.

You can utilize the URL resolved in the above step as the service URL. For sample requests and responses, refer to the API definition provided through the Choreo marketplace for the service.

#### Languages without OAuth 2.0 - aware HTTP clients

In the case of languages lacking built-in support for OAuth 2.0 in their HTTP clients, utilize the token obtained in the [step 3](./sharing-and-reusing-services.md#step-3-acquire-oauth2-access-token) to make calls to the dependent service. Add the obtained token to the HTTP Authorization header with the Bearer prefix.
As the service URL you can use the URL that you resolved in the [step 2](./sharing-and-reusing-services.md#step-2-read-configurations-within-the-application). For sample requests and responses, refer to the API definition of the service provided through the Choreo marketplace.

The following is a sample code snippet in NodeJS:

``` java
const response = await axios.get(serviceURL/{RESOURCE_PATH}, {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
});
```

!!! note
    To consume a Choreo service at the project visibility level, you don't need to obtain a token, you can directly invoke the service using the resolved URL.