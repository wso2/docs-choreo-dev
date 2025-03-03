# Use a Connection in Your Web Application

Choreo simplifies the process of integrating services into your web application. The approach to connect to a service can vary based on your web application. 

To connect to a selected service, follow the step-by-step instructions given below depending on the authentication mechanism used in your web application.

=== "Managed authentication"
    
    Choreo-managed authentication allows you to seamlessly handle authentication for your web application. You can configure your web application to work with the built-in identity provider of Choreo or any external identity provider that supports OIDC/OAuth2.0

    !!! note 
         Choreo's managed authentication is currently available only for web applications created with **React**, **Angular**, or **Vue.js** buildpacks.

    Follow the steps below to use an existing connection within your web application: 

    <h2> Step 1: Add the connection configuration</h2>

    To integrate a service into your application, you must first add the connection configuration as follows: 

    1. For single page applications (SPAs), you must add the connection configuration as a file mount. You can mount a file via the **Configurations** pane on the **Deploy** page. You must mount a file (for example, `config.js`) and add the configuration provided in the inline developer documentation into it. 

        The following is a sample configuration:

        ``` yaml
             window.configs = {
                 apiUrl: '<SERVICE_URL>',        
             };

        ```

    2.  To ensure accessibility of the `config.js` file via JavaScript at runtime, add a script tag as follows in the `index.html` file to reference the `config.js` file:

        ``` html
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8" />
            <title>My React App</title>
        </head>
        <body>
            <div id="root"></div>
            <script src="/public/config.js"></script>
        </body>
        </html>
        ``` 

        !!! note
             If you use an external IdP, you must add the IdP's configuration to the same file.
    
             For more information on working with IdPs, see [Configure Asgardeo as an External Identity Provider](https://wso2.com/choreo/docs/administer/configure-an-external-idp/configure-asgardeo-as-an-external-idp/).


    <h2> Step 2: Read the configuration</h2>

    Once you add the connection configuration, you can proceed to read the configuration from your application. The steps to read depend on the programming language you use.

    The following is a sample code snippet in NodeJS:

    ``` java
         const serviceURL = window?.configs?.apiUrl ? window.configs.apiUrl : "/";
    ```

    <h2> Step 3: Invoke the service</h2>

    If you use Choreo-managed authentication, Choreo handles the security handshaking for the application during deployment. The connected service will be accessible under the same domain as your application. Therefore, you can call the configured path directly using your preferred HTTP client.

    The following is a sample code snippet in NodeJS:

    ``` java
         const response = await axios.get(serviceURL/{RESOURCE_PATH});
    ```

    !!! note
         If you are using an external IdP provider instead of Choreo-managed authentication, you must obtain an access token from your IdP and add it to the HTTP authorization header with the bearer prefix.


=== "Custom authentication or no authentication"

    If you are not using Choreo-managed authentication or your web application lacks authentication, follow the steps below to connect to a service from your web application:

    <h2> Step 1: Add the connection configuration</h2>

    1. For single-page applications (SPAs), you must add the connection configuration as a file mount. You can mount a file via the **Configurations** pane on the **Deploy** page. You must mount a file (for example, `config.js`) and add the configuration provided in the inline developer documentation into it.
 
        The following is a sample configuration:

        ``` yaml
             window.configs = {
                 apiUrl: '<SERVICE_URL>',
                 consumerKey: '<CONSUMER_KEY>',
                 consumerSecret: '<CONSUMER_SECRET>',
                 tokenUrl: '<TOKEN_URL>',
             };
        ```

    2.  To ensure accessibility of the `config.js` file via JavaScript at runtime, add a script tag as follows in the `index.html` file to reference the `config.js` file:

        ``` html
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8" />
            <title>My React App</title>
        </head>
        <body>
            <div id="root"></div>
            <script src="/public/config.js"></script>
        </body>
        </html>
        ``` 

    For other types of web applications, you must add the respective configuration into your application.

    <h2> Step 2: Read the configuration</h2>

    Once you have added the connection configuration, you can proceed to read the configuration from your application. The steps to read depend on the programming language you use.

    The following is a sample code snippet in NodeJS:

    ``` java
         const serviceURL = window?.configs?.apiUrl ? window.configs.apiUrl : "/";
    ```

    <h2> Step 3: Acquire an OAuth 2.0 access token</h2>

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

    <h2> Step 4: Invoke the service</h2>

    You can invoke the service as follows:

    - For languages with OAuth 2.0-aware HTTP clients, you can invoke the service in a straightforward manner. The HTTP client seamlessly manages OAuth 2.0 authentication without requiring additional intervention.

        As the service URL you can use the URL that you resolved in step 2 above. For sample requests and responses, see the API definition provided via the Choreo marketplace for the service.

    - For languages without OAuth 2.0-aware HTTP clients, you can use the token obtained in step 3 above to make calls to the dependent service. Subsequently, add the obtained token to the HTTP authorization header with the bearer prefix.

        As the service URL you can use the URL that you resolved in step 2 above. For sample requests and responses, see the API definition of the service provided via the Choreo marketplace.

        The following is a sample code snippet in NodeJS:

        ```java 
            const response = await axios.get(serviceURL/{RESOURCE_PATH}, {
                headers: {
                'Authorization': `Bearer ${accessToken}`
                }
            })
        ```
