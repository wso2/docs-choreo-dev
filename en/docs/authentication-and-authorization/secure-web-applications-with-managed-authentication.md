# Secure Web Applications with Managed Authentication

Choreo's Managed Authentication simplifies the process of adding authentication and authorization to a single-page web application. It handles the complexities of authentication internally, freeing the application developer from the need to understand the OIDC/OAuth2.0 protocol. By enabling Choreo's Managed Authentication, configuring the IdP, and allowing the web application to connect to Choreo, developers can seamlessly integrate authentication into their web applications without dealing with the intricacies of the underlying protocol.

Moreover, Choreo's Managed Authentication introduces the concept of a Backend For Frontend (BFF) architecture, a secure pattern recommended for browser-based applications that utilize OIDC/OAuth2.0 for authentication and authorization. This architecture ensures that OAuth tokens remain secure from browser-side code, making them immune to potential attacks like Cross-Site Scripting (XSS). 

!!! note
    Managed Authentication is currently available only for React, Angular, and Vue.js buildpacks.

!!! warn
    Managed Authentication employs the 'SAMESITE' cookie attribute to safeguard against CSRF attacks. It is recommended for use with modern browsers that support this attribute.

## Step 1: Utilizing Managed Authentication in your web application

To secure your web application, you need to implement authentication. You can utilize Choreo's Managed Authentication to secure your web application as follows: 

### Login

To enable Choreo to manage the login functionality of your web application, you need to implement a login button that redirects users to /auth/login when clicked. You can use the following code snippet or any custom button component from your preferred UI component library:

```
<button onClick={() => {window.location.href="/auth/login"}}>Login</button>
```

When a user clicks on the login button within your web application, Choreo will redirect them to the preconfigured Identity Provider and handle the authentication process, conforming to the OICD/OAuth2.0 protocols. After a successful login, Choreo will set the relevant session cookies and redirect the user to the post-login path (default is `/`). The user can then invoke any Choreo-deployed APIs permitted to them.

### Obtaining user info claims

Choreo's Managed Authentication allows you to access user info claims that the Identity Provider returns post login, either via a cookie or by invoking a GET resource.


#### Obtain user info via the userinfo cookie

Upon successful login, Choreo's Managed Authentication establishes a userinfo cookie, which is accessible from the post-login path you've configured (by default, set to /). This `userinfo` cookie, provided by the Identity Provider, contains encoded user info claims.

!!! note
    - The `userinfo` cookie is intentionally set to have a short lifespan of only 2 minutes.
    - The developer can decide how to utilize the retrieved user info. We recommend securely storing the user info, as the stored information can also serve as a means to verify the logged-in state of a user.
    - The below example uses the `js-cookie` library for cookie parsing. You can use any cookie-parsing library of your choice. 

We recommend you retrieve the user info from the cookie and subsequently clear the cookie. Below is a sample code snippet that you can include in your post-login path for this purpose:

```
    import Cookies from 'js-cookie';

    // Read userinfo cookie value.
    const encodedUserInfo = Cookies.get('userinfo')

    // Decode the value. 
    const userInfo = JSON.parse(atob(encodedUserInfo))

    // Store the value in a preferred browser-based storage if needed.

    // Clear the cookie.
    Cookies.remove('userinfo', { path: <post-login-path> })
```
#### Obtain user info via a GET endpoint

Choreo's Managed Authentication provides a GET endpoint, `/auth/userinfo`, in addition to the userinfo cookie that it sets after a successful login. You can utilize this endpoint to query logged-in user info, and it can also serve as a way to check the logged-in state of a user.'

Below is an example of a request to this endpoint:

```
const response = await fetch('/auth/userinfo')
```

If the user has logged in, the server will send a 200 OK response with the user info in the body in JSON format. However, if the user has not logged in, the server will send a 401 Unauthorized response.

### Logout

To enable Choreo-managed logout capability to your web application, you can implement a login button redirecting users to `/auth/logout` along with the `session_hint` cookie value when clicked. You can use the following code snippet or any custom button component from your preferred UI component library.

!!! note
    - It is recommended to clear the user info (if stored) at logout.
    - The below example uses the `js-cookie` library for cookie parsing. You can use any cookie-parsing library of your choice.   
    
```
<button onClick={async () => {
    window.location.href = `/auth/logout?session_hint=${Cookies.get('session_hint')}`;
}}>Login</button>`
```

When a user clicks on the logout button, Choreo will clear the session cookies and redirect the users to the OIDC logout endpoint of the configured Identity Provider (if available).  

### API calls

You can invoke Choreo APIs within the same organization as your web application using the relative path /choreo-apis/<api-suffix>, regardless of whether managed authentication is enabled for the web application or not.

For example, if the API URL is `https://2d9ec1f6-2f04-4127-974f-0a3b20e97af5-dev.e1-us-east-azure.choreoapis.dev/rbln/item-service/api-e04/1.0.0`, the `<api-suffix>` would be `/rbln/item-service/api-e04/1.0.0`. From your Single Page Application, you can invoke the API using `/choreo-apis/rbln/item-service/api-e04/1.0.0` relative path.

!!! info
    The relative path will be shown in the relavent **Connections** page under **Dependencies** section in the left navigation of the web application component view.

If you enable Choreo's Managed Authentication, you don't have to manually add any logic to attach an access token in the API call, as Choreo APIs will accept the cookies set by Choreo's Managed Authentication.
You can directly invoke the API. For example:

```
    const response = await fetch('/choreo-apis/<api-suffix>')
```

If Choreo's Managed Authentication is disabled, you need to ensure that your web application attaches a valid access token to the API call.


### Token Refresh

If the access token provided by your configured Identity Provider expires, you must refresh the tokens before successfully calling a Choreo-deployed API. For token refresh to function,  you need to set up the Identity Provider to issue a refresh token in addition to the access token.

To refresh the tokens, you can make a POST request to the `/auth/refresh` endpoint.

For example:

```
const response = await fetch("/auth/refresh", { method: "POST" })
```

If the refresh token is valid, it will undergo a refresh, and the system will respond with a 204 No Content status.

However, if the refresh token has expired, the system will respond with a 401 Unauthorized status.

To automate the token refresh process when receiving a 401 unauthorized response from the Choreo API, you can encapsulate the requests with the refresh logic. Below is an example code snippet that wraps GET requests.

```
    export const performGetWithRetry = async (url) => {
        try {
            // API call
            return await fetch('/choreo-apis/<api-suffix>');
        } catch (error) {
            if (error instanceof HttpError && error.status === 401) {
                //Received 401 Unauthorized response from API GW. The access token may have expired.

                // Try to refresh the token
                const refreshResponse = await fetch("/auth/refresh", {
                    method: "POST"
                });

                const status = refreshResponse.status;
                if (status === 401) {
                    // Session has expired (i.e., Refresh token has also expired).
                    // Redirect to login page
                    window.location.href = "/auth/login";
                }
                if (status !== 204) {
                    // Tokens cannot be refreshed due to a server error.
                    console.log("Failed to refresh token. Status: " + status);

                    //  Throw the 401 error from API Gateway.
                    throw error;
                }
                // Token refresh successful. Retry the API call.
                return await fetch('/choreo-apis/<api-suffix>');
            } else {
                throw error;
            }
        }
    };
```

### Custom Error Page

You can set up Choreo's Managed Authentication to redirect to a customized error page within your Web Application by defining the Error Path in the configuration. In the event of an error during a redirection-based process, such as login or logout, Choreo will automatically direct the users to this designated custom error page.

!!! note
    If you have not configured an Error Path, Managed Authentication will utilize its default error page whenever an error occurs.

Choreo's Managed Authentication will include the following query parameters in the URL when redirecting to the custom error page:

| parameter name | parameter description                         |
|----------------|-----------------------------------------------|
| code           | short textual error code indicating the error |
| message        | description of the error                      |


You have successfully implemented Choreo's Managed Authentication for your web application. Your next step involves creating a web application component in Choreo, enabling Managed Authentication for the component, and subsequently deploying it.

## Step 2: Enable Managed Authentication

To ensure that your web application functions seamlessly with Managed Authentication, it is essential to enable Managed Authentication for your web application component within Choreo.

There are two ways that you can enable Managed Authentication for your Web Application component Choreo: 
- At component creation
- At component deployment

### Enable Managed Authentication at component creation

When creating a web application component, click the toggle **Managed authentication with Choreo** under **Authentication**  to enable Managed Authentication.

!!! note
    Managed Authentication is currently available only for React, Angular, and Vue.js buildpacks.

### Enable Managed Authentication at component deployment

1. Go to the **Deploy** view of your component.
2. Click **Authentication Settings** on the **Set Up** card.
3. Enable/Disable Managed Authentication using the toggle in the sidebar.

## Step 3: Configure Managed Authentication

To configure the necessary paths and scopes for managed authentication, follow the steps below:

1. Go to the **Deploy** view of your component.
2. Click **Authentication Settings** on the **Set Up** card.
2. Configure the following fields:


| Option            |  Description      | Default           |
| ----------------- | ----------------- | ----------------- |
| Post Login Path   | This represents the relative path that the application will be redirected to following a successful login. In your code, you will need to implement the necessary logic to handle the userinfo cookie set by Managed Authentication. See [ Handling user info post-login](#handling-user-info-post-login)       | /                      |
| Post Logout Path  | The relative path to which Choreo redirects after a successful logout.  | /                      |
| Error Path        | The relative path to which Choreo redirects to if an error occurs during a redirection-based flow (i.e. Login, Logout). See [Custom Error Page](#custom-error-page)             | Builtin error page     |
| Additional Scopes | All additional scopes required by the web application. The following scopes will be added by default. `openid`, `profile`, `email`, and scopes required to invoke subscribed APIs.               | none                   |


## Step 4.a: Manage OAuth Keys with Choreo Built-in Identity Provider

!!! note
    This step is optional. When you are using `Choreo Built-in Identity Provider`, we auto generate keys during the component deployment if keys are not available.

1. In the left navigation of your component view, click **Settings**.
2. Click **Authentication Keys** tab.
3. Select the environment that you need to manage keys for.
4. Select **Choreo Built-In Identity Provider**.
5. Click on the **Generate Keys** button. 

!!! note
    If the **Generate Keys** button is not visible, it means that OAuth keys have already been generated for the component for the selected environment.

## Step 4.b: Manage OAuth Keys with Asgardeo

### Create OIDC/OAuth2.0 Application in Asgardeo

1. Login to [Asgardeo](https://console.asgardeo.io/).
2. Select your organization.
3. Click **Applications** in the left navigation.
4. Click **+ New Application**.
5. Select **Standard Based Application**.
6. Provide a name for the application.
7. Select **OAuth2.0 OpenID Connect** as the protocol.
8. Click Register.
9. Go to **Protocol** tab of the top navigation of the created application.
10. Select `Code` and `Refresh Token` as the **Allowed grant types**.
11. Add the following as **Authorized redirect URLs**.
    - [your-web-application-url]/auth/login/callback
    - [your-web-application-url]/auth/logout/callback
12. Add [your-web-application-url] under **Allowed origins**
13. Under **Access Token** section, select JWT as **Token Type**.
14. Click **Update**.
15. If you need to invoke APIs secured with Role Based Access Control, you need to create roles in the application and map those roles to relevant permissions (scope). Then those roles should be assigned to user groups. Refer the [Asgardeo API Authorization guide](https://wso2.com/asgardeo/docs/guides/api-authorization/) for additional details.  
16. Copy the **Client ID** and **Client Secret** of the application. 

### Link OIDC/OAuth2.0 Application to the Choreo Component

1. In the left navigation of your component view, click **Settings**.
2. Click **Authentication Keys** tab.
3. Select the environment that you need to manage keys for.
4. Select **Asgardeo - [your-org-name]**.
5. Paste the **Client ID** and **Client Secret** of the OIDC/OAuth2.0 Application created in Asgardeo. 
6. Click **Add Keys**.

## Step 4.c: Manage OAuth Keys with an External Identity Provider

### Create OIDC/OAuth2.0 Application in the External Identity Provider

1. Create a OIDC/OAuth2.0 Application.
2. Configure `Code` and `Refresh Token` as **Allowed grant types**.
3. Add the following as **Authorized redirect URLs**.
    - [your-web-application-url]/auth/login/callback
    - [your-web-application-url]/auth/logout/callback
4. Set Access token type to **JWT**.
5. Set Refresh token expiry time to 1 Day.
6. If you need to invoke APIs secured with Role Based Access Control, you need to make sure that the users have a role mapping which sets relevant permissions to invoke the APIs. 

!!! note
    The specific implementation of how application roles are mapped to users will depend on the Identity Provider.

### Link OIDC/OAuth2.0 Application to the Choreo Component

1. In the left navigation of your component view, click **Settings**.
2. Click **Authentication Keys** tab.
3. Select the environment that you need to manage keys for.
4. Select the External Identity Provider.
5. Paste the **Client ID** and **Client Secret** of the OIDC/OAuth2.0 Application created in the External Identity Provider. 
6. Click **Add Keys**.
