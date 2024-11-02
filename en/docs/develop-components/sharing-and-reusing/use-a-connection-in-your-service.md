# Use a Connection in Your Service

Choreo allows you to share and reuse your services, accelerating development and enhancing efficiency in building integrated applications through connections.

For step-by-step on instruction on creating a connection, see [Create a Connection](create-a-connection.md).

To learn more about Choreo Connections, see the documentation on [Connections](../../choreo-concepts/connections.md).

## Consume a service through a connection

You can consume a Choreo-deployed service within another service. Consuming connections from within Choreo services is seamless and straightforward. Follow the steps below to consume a Choreo service:

### Step 1: Add connection configurations

To integrate another service into your application, click the appropriate tab below based on your current configuration file and follow the step-by-step instructions:

=== "Component.yaml file (v1.1)"

    1. Copy and paste the snippet from the in-line developer guide into the `component.yaml` file.

        The following is a sample snippet: 

        ``` yaml

        dependencies:
            connectionReferences:
            - name: <CONNECTION_NAME>
              resourceRef: <RESOURCE_IDENTIFIER>

        ```

          | Field            | Description                                                         |
          |------------------|---------------------------------------------------------------------|
          | name             | The name given to the connection.                                   |
          | resourceRef      | A unique, readable identifier of the service being connected to.    |


    2. If you've previously added a `connectionReferences` section under `dependencies`, append this as another item under `connectionReferences`. Upon deploying the component, Choreo automatically creates a subscription if applicable and the necessary configurations to establish the connection will be injected into the Choreo-defined environment variables.
      
          The following table details the Choreo-defined environment variables:

          | Configuration Key       | Choreo-Defined Environment Variable Name                       |
          |-------------------------|----------------------------------------------------------------|
          | ServiceURL              | CHOREO_<CONNECTION_NAME\>_SERVICEURL                           |
          | ConsumerKey             | CHOREO_<CONNECTION_NAME\>_CONSUMERKEY                          |
          | ConsumerSecret          | CHOREO_<CONNECTION_NAME\>_CONSUMERSECRET                       |
          | TokenURL                | CHOREO_<CONNECTION_NAME\>_TOKENURL                             |


          If you'd like to use custom environment variable names instead of the Choreo-defined ones, add the dependency as a service reference under `dependencies` in the same file. For more details, refer to the instructions under the `component.yaml file (v1.0)` tab.


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
    const serviceURL = process.env.CHOREO_<CONNECTION_NAME>_SERVICEURL;
    ```

=== "Component.yaml file (v1.0)"

    !!! note
        This `component.yaml v1.0` is a legacy configuration format. For new projects, we recommend using the latest version (v1.1) of `component.yaml` for improved usability and features.
  
    1. Copy and paste the snippet from the in-line developer guide into the `component.yaml` file.
      
        The following is a sample snippet:

        ``` yaml

        dependencies:
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
          | name             | The name of the service you are connecting to.              |
          | connectionConfig | The unique connection identifier for the connection.        |
          | env              | The environment variable mapping.                           |
          | from             | The key of the configuration entry.                         |
          | to               | The environment variable name to which Choreo will inject the value of the key.|


    2. Replace `<YOUR_ENV_VARIABLE_NAME_HERE>` with an appropriate environment variable name of your choice. If you have previously added a service reference section under `dependencies`, append this as another item under `serviceReferences`. 

          Upon deploying the component, Choreo automatically creates a subscription if applicable and populates the specified environment variables with actual values.


          The following table provides details on the configuration keys associated with the connection:

          | Name           |  Type      |  Description                          |Optional       | Sensitive    |
          |----------------|------------|---------------------------------------|---------------|--------------|
          | ServiceURL     | string     | Service URL of the Choreo service     | false         | false        |
          | ConsumerKey    | string     | Consumer key of the Choreo service    | false         | false        |
          | ConsumerSecret | string     | Consumer secret of the Choreo service | false         | true         |
          | TokenURL       | string     | Token URL of the STS                  | false         | false        |

    <h3> Step 2: Read configurations within the application </h3>

    Once you add the connection configuration snippet, you can proceed to read those configurations within your application. The steps to follow depend on the programming language you are using.

    The following is a sample code snippet in NodeJS:

    ``` java
    const serviceURL = process.env.SVC_URL;
    ```

=== "Component-config.yaml file"

    !!! note
        This `component-config.yaml` is a legacy configuration format. For new projects, we recommend using the latest version (v1.1) of `component.yaml` for improved usability and features.

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
          | name             | The name of the service you are connecting to.              |
          | connectionConfig | The unique connection identifier for the connection.        |
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

    <h3> Step 2: Read configurations within the application </h3>

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

## Configure resiliency for the connection

To ensure your service remains robust and responsive when consuming other Choreo services through connections, you can configure resiliency patterns. Choreo supports configuring retries for connections.

    !!! note
        Currently, the resiliency configuration is only available in the CDPs and only for paid users. It will be available for PDPs soon.

### Configure retry

To configure retry for your connection, follow these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Project** list. This opens the project home page.
3. In the left navigation menu, click **Dependencies** and then click **Connections**.
4. Click on a required **Connection** to view its details.
5. Expand the **Advanced Configuration** section.
6. Enable the **Retry** toggle under **Resiliency**.
7. Set appropriate values for the retry parameters. For more information, see [Retry configuration parameters](#retry-configuration-parameters).
8. Click **Save**.

    !!! note
        To use the retry feature, include the `Choreo-API-Key` header in your request. For details on obtaining the Choreo API key, see [Step 1](#step-1-add-connection-configurations).

### Retry configuration parameters

- **Retry count**: The number of retry attempts if a request fails. For example, setting this to 3 allows Choreo to make up to 3 additional attempts after the initial failure.

- **Retry Backoff Interval**: The base time delay for exponential backoff between retries. This prevents overwhelming the target service with rapid, repeated requests.

- **Timeout Per Retry**: The maximum duration in seconds for each retry attempt. If there is no response within this time, the attempt is considered a failure. For example, setting this to 30 seconds means each retry will wait up to 30 seconds before timing out.

- **Condition**: The conditions under which retries are triggered. Choreo supports the following options, and you can select one or more:

    - **5xx**: Retry on any 5xx response code or when the upstream server fails to respond (disconnect/reset/read timeout). This includes connection failures and refused-stream errors.
    - **gateway-error**: Retry only on 502, 503, or 504 response codes.
    - **reset**: Retry when the upstream server does not respond (disconnect/reset/read timeout).
    - **connect-failure**: Retry on connection failures to the upstream server, such as connection timeouts.
    - **retriable-4xx**: Retry on retriable 4xx response codes. Currently limited to 409 responses.
    - **refused-stream**: Retry if the upstream server resets the stream with a `REFUSED_STREAM` error code.
