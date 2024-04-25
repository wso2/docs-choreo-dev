# Sharing and Reusing Web Applications

Connecting to a service from a web application can be achieved through various methods, depending on the type of web application. The following sections will outline the steps required to connect to the chosen service based on the authentication mechanism used in your web application.

## Managed authentication

Choreo managed authentication allows you to seamlessly handle your web application authentication. You can configure your web application to work with the Choreo built-in identity provider or any external identity provider that supports OIDC/OAuth2.0

!!! note 
    Choreo's managed authentication is currently available only for web applications created with React, Angular, or Vue.js build packs.

Follow the steps below to use the created connection within your web application: 

## Step 1: Add connection configuration

To integrate another service to your application first you need to add the connection configurations as follows: 

1. For SPAs, configurations should be added as a file mount. You can mount a file via the deploy page in the configure and deploy pane. Mount a file(config.js) and add the content displayed in the developer guide. 

A sample content is as below.

    ``` yaml
    window.configs = {
        serviceURL: '<SERVICE_URL>',        
    };

    ```

2.  In your index.html file inside the public directory, add a script tag as follows to include the config.js file inside the tag. The config.js file will be accessible via JavaScript at runtime.

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
    If you are using an external IDP, then you will also need the configurations related to your IDP to be injected depending on the IDP and authentication mechanism(consumer key, consumer secret, token URL). You can add those details to the same file as well.
    
    For more information on working with IDPs, refer to [this](https://wso2.com/choreo/docs/administer/configure-an-external-idp/configure-asgardeo-as-an-external-idp/) documentation.


## Step 2: Read configurations

Once you have added the connection configuration snippet, the next step is to read those configurations within your application. The exact steps of that will depend on the language you are using.

The following is a sample code snippet in NodeJS:

    ``` java
    const serviceURL = window?.configs?.serviceURL ? window.configs.serviceURL : "/";
    ```

## Step 3: Invoke the service (Choreo managed authentication)

If you are using Choreo managed authentication, once deployed Choreo will handle all the security handshaking on behalf of the application as you have enabled Choreo authentication. Connected service will be made available under the same domain as per your application. Hence you can directly call the above configured path using your favorite HTTP client.

The following is a sample code snippet in NodeJS:

    ``` java
    const response = await axios.get(serviceURL/{RESOURCE_PATH});
    ```

!!! note
    If you are using an external IDP provider without using choreo managed authentication, obtain an access token from your external IDP and add the obtained token to the HTTP Authorization header with the Bearer prefix.

## Custom authentication or no authentication

If you are not using choreo managed authentication, or if your web application lacks any form of authentication, follow the below steps to use the created connection within your web application:

### Step 1: Add connection configuration

For SPAs, configurations should be added as a file mount. This functionality is available on the deploy page in the configure and deploy pane. Mount a file there(config.js) and add the below content. 

For other types of web applications, to integrate the mentioned configurations into your application, copy and paste the below snippet into the component-config file under the spec section.

### Step 2: Read configuration

Once you have added the connection configuration snippet, the next step is to read those configurations within your application. The exact steps of that will depend on the language you are using.

The following is a sample code snippet in NodeJS

``` java
const serviceURL = window?.configs?.serviceURL 
```

### Step 3: Acquire OAuth 2.0 access token

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
