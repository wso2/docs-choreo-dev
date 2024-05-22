# Use a Connection in Your Service

Choreo allows you to share and reuse your services, accelerating development and enhancing efficiency in building integrated applications through connections.

For step-by-step on instruction on creating a connection, see [Create a Connection](create-a-connection.md).

To learn more about Choreo Connections, see the documentation on [Connections](../../choreo-concepts/connections.md).

## Consume a service through a connection

You can consume a Choreo-deployed service within another service. Consuming connections from within Choreo services is seamless and straightforward. Follow the steps below to consume a Choreo service:

### Step 1: Add connection configurations

To integrate another service into your application, follow the steps below:

1. Copy and paste the snippet from the in-line developer guide into the `component-config` file under the `spec` section.

    The following is a sample snippet:

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
      | Name             | The name of the service you are connecting to.              |
      | ConnectionConfig | The unique connection identifier for the connection.        |
      | env              | The environment variable mapping.                           |
      | from             | The key of the configuration entry.                         |
      | to               | The environment variable name to which Choreo will inject the value of the key.|


2. Replace `<YOUR_ENV_VARIABLE_NAME_HERE>` with an appropriate environment variable name of your choice. If you have previously added an outbound service reference, append this as another item under `serviceReferences`. 

      Upon deploying the component, Choreo automatically creates a subscription if applicable and populates the specified environment variables with actual values.


      The following table provides details on the configuration keys associated with the connection:

      | Name           |  Type      |  Description                          |Optional       | Sensitive    |
      |----------------|------------|---------------------------------------|---------------|--------------|
      | ServiceURL     | string     | Service URL of the Choreo service     | false         | false        |
      | ConsumerKey    | string     | Consumer key of the Choreo service    | false         | false        |
      | ConsumerSecret | string     | Consumer secret of the Choreo service | false         | true         |
      | TokenURL       | string     | Token URL of the STS                  | false         | false        |


### Step 2: Read configurations within the application

Once you add the connection configuration snippet, you can proceed to read those configurations within your application. The steps to follow depend on the programming language you are using.

The following is a sample code snippet in NodeJS:

``` java
const serviceURL = process.env.SVC_URL;
```

### Step 3: Acquire an OAuth 2.0 access token

To consume a Choreo service with the visibility level set to organization or public and secured by the OAuth 2.0 security scheme, you must obtain an OAuth 2.0 token from the token endpoint. Subsequently, you can use the token to invoke the service.

- For languages with OAuth 2.0-aware HTTP clients, you must pass the OAuth 2.0-related configurations such as client id, client secret and so on, obtained when creating the connection to your HTTP client configuration. The HTTP client autonomously manages token retrieval and refreshing.

- For languages without OAuth 2.0-aware HTTP clients, you must manually initiate a call to the token endpoint. This includes fetching the token and managing token expiration and refresh directly within your application code. The following is a sample curl command to obtain a token:


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

- For languages with OAuth 2.0-aware HTTP clients, you can invoke the service in a straightforward manner. The HTTP client seamlessly manages OAuth 2.0 authentication without requiring additional intervention.

    As the service URL you can use the URL that you resolved in [step 2](#step-2-read-configurations-within-the-application). For sample requests and responses, see the API definition provided via the Choreo marketplace for the service.

- For languages without OAuth 2.0-aware HTTP clients, you can use the token obtained in [step 3](#step-3-acquire-an-oauth-20-access-token) to make calls to the dependent service. Subsequently, add the obtained token to the HTTP authorization header with the bearer prefix.
As the service URL you can use the URL that you resolved in [step 2](#step-2-read-configurations-within-the-application). For sample requests and responses, see the API definition of the service provided via the Choreo marketplace.

    The following is a sample code snippet in NodeJS:

    ``` java
    const response = await axios.get(serviceURL/{RESOURCE_PATH}, {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
    });
    ```

    !!! note
        If you want to consume a Choreo service at the project visibility level, you don't need to obtain a token. You can directly invoke the service using the resolved URL.
        
