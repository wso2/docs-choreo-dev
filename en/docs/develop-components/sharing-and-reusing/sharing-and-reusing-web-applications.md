# Share and Reuse Web Applications

To connect to a service from your web application, the approach you need to take can vary depending on your web application. The following sections outline the steps to connect to a selected service based on the authentication mechanism used in your web application.

## Managed authentication

Choreo managed authentication allows you to seamlessly handle authentication for your web application. You can configure your web application to work with the Choreo built-in identity provider or any external identity provider that supports OIDC/OAuth2.0

!!! note 
    Choreo's managed authentication is currently available only for web applications created with **React**, **Angular**, or **Vue.js** buildpacks.

Follow the steps below to use an existing connection within your web application: 

## Step 1: Add the connection configuration

To integrate a service into your application, you must add the connection configurations as follows: 

1. For SPAs, you must add the connection configuration as a file mount. You can mount a file via the **Configurations** pane on the **Deploy** page. You must mount a file (config.js) and add the configuration provided in the in-line developer documentation. 

The following is a sample configuration:

    ``` yaml
    window.configs = {
        serviceURL: '<SERVICE_URL>',        
    };

    ```

2.  To make the `config.js` file accessible via JavaScript at runtime, you must add a script tag as follows to reference the `config.js` file from the `index.html` file in your repository:

    ``` html
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>My React App</title>
    </head>
    <body>
        <div id="root"></div>
        <script src="%PUBLIC_URL%/config.js"></script>
    </body>
    </html>
    ``` 

!!! note
    If you are using an external IdP, you must use the configuration related to your IdP depending on the IdP's authentication mechanism (consumer key, consumer secret, token URL). You can add the configuration to the same file.
    
    For more information on working with IdPs, see [Configure Asgardeo as an External Identity Provider ](https://wso2.com/choreo/docs/administer/configure-an-external-idp/configure-asgardeo-as-an-external-idp/).


## Step 2: Read the configuration

Once you add the connection configuration snippet, you can proceed to read the configuration from your application. The steps to read the configuration depend on the language you use.

The following is a sample code snippet in NodeJS:

    ``` java
    const serviceURL = window?.configs?.serviceURL ? window.configs.serviceURL : "/";
    ```

## Step 3: Invoke the service (Choreo-managed authentication)

If you use Choreo-managed authentication, Choreo handles all the security handshaking on behalf of the application during deployment. The connected service will be made available under the same domain as per your application. Therefore, you can directly call the configured path using your preferred HTTP client.

The following is a sample code snippet in NodeJS:

    ``` java
    const response = await axios.get(serviceURL/{RESOURCE_PATH});
    ```

!!! note
    If you are using an external IdP provider without using Choreo-managed authentication, you must obtain an access token from your external IdP and add the obtained token to the HTTP authorization header with the bearer prefix.

## Custom authentication or no authentication

If you are not using Choreo-managed authentication, or if your web application lacks any form of authentication, follow the steps below to use an existing connection within your web application:

### Step 1: Add the connection configuration

For SPAs, configurations should be added as a file mount. This functionality is available on the deploy page in the configure and deploy pane. Mount a file there(config.js) and add the below content. 

For other types of web applications, to integrate the mentioned configurations into your application, copy and paste the below snippet into the `component-config` file under the spec section.

### Step 2: Read the configuration

Once you have added the connection configuration snippet, the next step is to read those configurations within your application. The exact steps of that will depend on the language you are using.

The following is a sample code snippet in NodeJS:

``` java
const serviceURL = window?.configs?.serviceURL 
```

### Step 3: Acquire an OAuth 2.0 access token

#### Languages with OAuth 2.0 - aware HTTP clients
For languages equipped with HTTP clients that support OAuth 2.0, simply pass the OAuth 2.0-related configurations (client id, client secret, etc), obtained in the previous step to your HTTP client configuration. The HTTP client will autonomously manage token retrieval and refreshing.

#### Languages without OAuth 2.0 - aware HTTP clients
When dealing with programming languages that do not have inherent support for OAuth 2.0 in their HTTP clients, you are required to manually initiate a call to the token endpoint. This involves fetching the token and managing token expiration and refresh directly within your application code. The following is an example curl command illustrating how to obtain a token:

``` bash
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

### Step 4: Invoke the service (custom authentication or no authentication)

#### Languages without OAuth 2.0 - aware HTTP clients

For languages equipped with OAuth 2.0 - aware HTTP clients, invoking the service is straightforward. The HTTP client seamlessly manages OAuth 2.0 authentication without requiring additional intervention.
As the service URL you can use the URL that you resolved in the earlier step. For sample requests and responses, you can refer to the API definition of the service provided through the marketplace.

#### Languages without OAuth 2.0 - aware HTTP clients

In the case of languages lacking built-in support for OAuth 2.0 in their HTTP clients, utilize the token obtained in the previous step to make calls to the dependent service. Add the obtained token to the HTTP Authorization header with the Bearer prefix.
As the service URL you can use the URL that you resolved in the earlier step. For sample requests and responses, refer to the API definition of the service provided through the marketplace.

The following is a sample code snippet in NodeJS:

```java 
const response = await axios.get(serviceURL/{RESOURCE_PATH}, {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
})
```
