# Secure Web Applications with Managed Authentication

The managed authentication capability of Choreo simplifies adding authentication and authorization to a single-page web application.

As a developer, you can easily set up Choreo's managed authentication to seamlessly integrate authentication into your web application. You just need to enable Choreo’s managed authentication, configure the built-in identity provider, and connect to Choreo without having to deal with the complexities of underlying OIDC/OAuth2.0 protocols.

Choreo's managed authentication follows the backend for frontend (BFF) architecture, which is a secure pattern recommended for browser-based applications that utilize OIDC/OAuth2.0 for authentication and authorization. This architecture ensures that OAuth tokens remain secure from browser-side code, making them immune to potential attacks like cross-site scripting (XSS).

!!! note
     Choreo's managed authentication is currently available only for web applications created with **React**, **Angular**, or **Vue.js** buildpacks.

!!! warning
     Managed authentication uses the 'SAMESITE' cookie attribute to prevent CSRF attacks. Therefore, it is recommended to use managed authentication with modern browsers that support the 'SAMESITE' attribute.

## Step 1: Set up managed authentication for your web application

To secure your web application, you must implement authentication and authorization for it. 

To easily set up authentication for your web application with Choreo's managed authentication, follow the steps given below. Before you move on to the next section, see [Develop Web Applications Locally with Choreo’s Managed Authentication](../develop-components/develop-web-applications/develop-web-applications-locally-with-managed-authentication.md) to ensure a seamless authentication experience when developing your web application on your local machine. You can also refer to the [sample React app with managed authentication](https://github.com/wso2/choreo-samples/tree/main/reading-list-app/reading-list-front-end-with-managed-auth).

### Step 1.1: Implement the sign-in functionality

To allow Choreo to manage the sign-in functionality for your web application, you must implement a sign-in button that redirects users to the `/auth/login` path on click. You can use the following code snippet or any custom button component from a preferred UI component library:

``` javascript
<button onClick={() => {window.location.href="/auth/login"}}>Login</button>
```

This code snippet works as follows:

When a user clicks sign in on your web application, Choreo will redirect the user to the configured identity provider and handle the authentication process, conforming to the OICD/OAuth2.0 protocols. On successful sign-in, Choreo will set the relevant session cookies and redirect the user to the post-sign-in path (default is `/`). The user can then invoke any Choreo-deployed APIs depending on the permission granted.

!!! note
    Refer to [configure the identity provider section](#step-3-configure-the-identity-provider-for-the-web-application) for details on configuring an identity provider for the web application. 

#### Optional: Pass additional query parameters to the identity provider 

If you want to pass additional query parameters to the identity provider, include them in the `/auth/login` request. Choreo appends these parameters to the `authorize` request sent to the identity provider.

For example,

``` javascript
<button onClick={() => {window.location.href="/auth/login?fidp=myfederatedidp"}}>Login</button>
```

### Step 1.2: Obtain user information claims

Choreo's managed authentication allows you to access user information claims that the identity provider returns post-sign-in, either via a cookie or by invoking a GET resource.


#### Obtain user information via the `userinfo` cookie

Upon successful sign-in, Choreo's managed authentication establishes a `userinfo` cookie that is accessible from the post-sign-in path you configured (by default, set to /). This `userinfo` cookie, provided by the identity provider, contains encoded user information claims.

!!! note
    - The `userinfo` cookie is intentionally set to have a short lifespan of only 2 minutes.
    - As a developer, you can decide how to utilize the user information that you retrieve. You must securely store the user information because the stored information can also serve as a means to verify the logged-in state of a user.
    - The following example uses the `js-cookie` library for cookie parsing. You can use any cookie-parsing library of your choice. 

The recommended approach is to retrieve user information from the cookie and subsequently clear the cookie. The following is a sample code snippet that you can include in your post-sign-in path to retrieve user information from the cookie and subsequently clear the cookie:

``` javascript
    import Cookies from 'js-cookie';

    // Read userinfo cookie value.
    const encodedUserInfo = Cookies.get('userinfo')

    // Decode the value. 
    const userInfo = JSON.parse(atob(encodedUserInfo))

    // Store the value in a preferred browser-based storage if needed.

    // Clear the cookie.
    Cookies.remove('userinfo', { path: <post-login-path> })
```
#### Obtain user information via a GET endpoint

Choreo's managed authentication provides the GET endpoint `/auth/userinfo` in addition to the `userinfo` cookie that it sets after successful sign-in. You can use this endpoint to query information about users who have signed in. It also serves as a mechanism to check the state of a user who has signed in.

The following is an example of a request to this endpoint:

``` javascript
const response = await fetch('/auth/userinfo')
```

If a user has signed in, the server sends a `200 OK` response with the user information in JSON format in the response body. However, if the user is not signed in, the server sends a `401 Unauthorized` response.

### Step 1.3: Implement the sign-out functionality

To allow Choreo to manage the sign-out functionality of your web application, you can implement a sign-out button to redirect users to the `/auth/logout` path along with the `session_hint` cookie value on click. You can use the following code snippet or any custom button component from a preferred UI component library:

!!! note
    - It is recommended to clear any user information (if stored) at the time of sign-out.
    - The following example uses the `js-cookie` library for cookie parsing. You can use any cookie-parsing library of your choice.   
    
``` javascript
<button onClick={async () => {
    window.location.href = `/auth/logout?session_hint=${Cookies.get('session_hint')}`;
}}>Logout</button>`
```

When a user clicks the sign-out button, Choreo will clear the session cookies and redirect the users to the OIDC logout endpoint of the configured identity provider (if available).  

### Step 1.4: Invoke APIs

To invoke Choreo APIs within the same organization as your web application, you can use the relative path `/choreo-apis/<api-suffix>`, regardless of whether managed authentication is enabled for the web application or not.

!!! note
    To invoke a Choreo API from a web application, you need to [create a Connection](../develop-components/sharing-and-reusing/create-a-connection.md) from the web application to the Choreo API. 

For example, if the API URL is `https://2d9ec1f6-2f04-4127-974f-0a3b20e97af5-dev.e1-us-east-azure.choreoapis.dev/rbln/item-service/api-e04/1.0.0`, the `<api-suffix>` would be `/rbln/item-service/api-e04/1.0.0`. You can invoke the API using the `/choreo-apis/rbln/item-service/api-e04/1.0.0` relative path from your single-page application.

!!! info
     To copy the exact service URL of a Connection, you can follow the steps given below:
      1. In the Choreo Console, go to the appropriate web application component.
      2. In the left navigation menu, click **Connections** under **Dependencies**.
      3. Click on the required Connection and copy the service URL.

If you enable Choreo's managed authentication, you don't have to manually add any logic to attach an access token to the API call because Choreo APIs accept the cookies set by Choreo's managed authentication. You can directly invoke the API as follows:

```
    const response = await fetch('/choreo-apis/<api-suffix>')
```

If Choreo's managed authentication is disabled, you must ensure that your web application attaches a valid access token to the API call.


### Step 1.5: Handle session expiry

When a user session exceeds the configured session expiry time, it automatically expires. A `401 Unauthorized` response status code for a Choreo API request from a logged-in user indicates that the session may have expired, requiring the user to re-login.

To programmatically handle session expiry and automatically re-login upon receiving a `401 Unauthorized` response from a Choreo API, you can encapsulate the request with re-login logic. The following sample code snippet shows how to wrap GET requests:


``` javascript
    export const performGet = async (url) => {
        try {
            // API call
            return await fetch('/choreo-apis/<api-suffix>');
        } catch (error) {
            if (error instanceof HttpError && error.status === 401) {
                // Re-login
                window.location.href = "/auth/login";
            } else {
                throw error;
            }
        }
    };
```

### Step 1.6: Set up a custom error page

You can set up Choreo's managed authentication to redirect to a customized error page within your web application by defining the error path in the configuration. In the event of an error during a redirection-based process, such as sign in or sign out, Choreo will automatically redirect the user to the designated custom error page.

!!! note
    If you have not configured an error path, Choreo's managed authentication will use its default error page whenever an error occurs.

Choreo's managed authentication will include the following query parameters in the URL when redirecting to the custom error page:

| Parameter      |  Description                                    |
|----------------|-------------------------------------------------|
| code           | A short textual error code indicating the error |
| message        | The description of the error                    |


Now have successfully implemented Choreo's managed authentication for your web application. The next step is to enable managed authentication for the component, and subsequently deploy it.

## Step 2: Enable managed authentication and configure the paths

To ensure that your web application functions seamlessly with managed authentication, it is essential to enable managed authentication for your web application component within Choreo.

You can enable managed authentication for your web application component at the time you deploy the component.

!!! tip
     Managed authentication is enabled by default when you create a web application using **React**, **Angular**, or **Vue.js** buildpacks.

1. Sign in to the [Choreo Console](https://console.choreo.dev/). This opens the project home page.
2. In the **Component Listing** pane, click on the web application for which you want to enable managed authentication.
3. In the left navigation menu, click **Deploy**.
4. In the **Set Up** card, click **Configure & Deploy**.
5. Add the necessary configurations for your component if applicable and click **Next**.
6. Make sure **Managed Authentication with Choreo** toggle is enabled.
7. Specify appropriate values for the following fields:

    | Field            |  Description      | Default value      |
    | ----------------- | ----------------- | ----------------- |
    | Post Login Path   | The relative path that the application will be redirected to on successful sign-in. In your code, you must implement the necessary logic to obtain signed-in user's information from the `userinfo` cookie set by managed authentication. See **Obtain user information via the `userinfo` cookie** section in [Obtain user information claims](#step-12-obtain-user-information-claims). | /                      |
    | Post Logout Path  | The relative path to which Choreo redirects you on successful sign-out.  | /                      |
    | Error Path        | The relative path to which Choreo redirects you when an error occurs during a redirection-based flow (i.e., sign in or sign out). See [Set up a custom error page](#step-16-set-up-a-custom-error-page).             | Built-in error page     |
    | Session Expiry Time | The time in minutes after which the user session expires. For a seamless experience, the session expiry value should match the refresh token expiry time of the OIDC application in your identity provider.               | 10080 Minutes (7 Days)                   |
    | Additional Scopes | All additional scopes required by the web application. The `openid`, `profile`, and `email` scopes are added by default together with the scopes required to invoke subscribed APIs.               | none                   |

    !!! note
         If you need to change these configurations after you deploy the component, you can click **Authentication Settings** on the **Set Up** card, make the necessary changes, and deploy the component once again.

## Step 3: Configure the identity provider for the web application

You can configure your web application to work with the Choreo built-in identity provider, Asgardeo, or any external identity provider which supports OIDC/OAuth2.0 . 

!!! note
    The identity provider configured in this step should contain the users for the web application.

Click the respective tab for details depending on which identity provider you need to configure: 

=== "Configure Choreo built-in identity provider"

     Follow the steps given below to configure the built-in identity provider by generating authentication keys:

    !!! note
         Choreo built-in identity provider is configured by default. Therefore, this step is optional.

     1. In the Choreo Console, go to the component for which you want to manage OAuth keys.
     2. In the left navigation menu, click **Settings**.
     3. Click the **Authentication Keys** tab and then click on the environment for which you want to generate keys.
     4. In the **Identity Provider** list, select **Choreo Built-In Identity Provider**.
     5. Click **Generate Secret**. 

        !!! Note
             If the **Regenerate Secret** button is shown instead of the **Generate Secret** button, it indicates that OAuth keys are already generated for the component for the selected environment.

    !!! tip
        Refer to [Configure a User Store with the Built-In Identity Provider](../../administer/configure-a-user-store-with-built-in-idp/) for details on adding test users in Choreo built-in identity provider.

    !!! tip
        If you need to invoke APIs secured with role-based access control, you can test this within Choreo by creating roles for the application and mapping those roles to relevant permissions (scope) and user groups. For more information, see [create roles and assign permissions](../test-secure-api-access-with-choreo-built-in-security-token-service/#step-2-create-roles-and-assign-permissions) and [assign roles to user groups](../test-secure-api-access-with-choreo-built-in-security-token-service/#step-3-assign-roles-to-user-groups) sections in [Test Secure API Access with Choreo Built-In Security Token Service](../test-secure-api-access-with-choreo-built-in-security-token-service).

=== "Configure Asgardeo"

     **Step 3.1: Create and configure an OIDC/OAuth2.0 application in Asgardeo**

     1. Sign in to [Asgardeo](https://console.asgardeo.io/).
     2. In the top navigation menu, click the **Organization** list and select your organization.
     3. In the Asgardeo Console left navigation menu, click **Applications**.
     4. Click **+ New Application**.
     5. Click **Standard-Based Application**.
     6. Specify a name for the application and select **OAuth2.0 OpenID Connect** as the protocol.
     7. Click **Register**.
     8. Click the **Protocol** tab and follow these steps:

         1. Select `Code` and `Refresh Token` as the **Allowed grant types**.
         2. Specify the following as **Authorized redirect URLs**:
             - [your-web-application-url]/auth/login/callback
             - [your-web-application-url]/auth/logout/callback
         3. Specify your web application URL under **Allowed origins**.
         4. In the **Access Token** section, select `JWT` as the **Token type**.
         5. Click **Update**. 

            !!! tip
                 If you need to invoke APIs secured with role-based access control, you must create roles in the application and map those roles to relevant permissions (scope). Then those roles should be assigned to user groups. For more information, see the [Asgardeo API authorization guide](https://wso2.com/asgardeo/docs/guides/api-authorization/).

         6. Copy the **Client ID** and **Client Secret** of the application. You will need to use these values in the next step to link the OIDC/OAuth2.0 application to your Choreo component.

     **Step 3.2: Link the OIDC/OAuth2.0 application to the Choreo web application component**

     1. In the Choreo Console, go to the component for which you want to manage OAuth keys.
     2. In the left navigation menu, click **Settings**.
     3. Click the **Authentication Keys** tab and then click on the environment for which you want to generate keys.
     4. In the **Identity Provider** list, select **Asgardeo - [your-org-name]**.
     5. Paste the **Client ID** and **Client Secret** of the OIDC/OAuth2.0 application you created in Asgardeo. 
     6. Click **Add Keys**.

=== "Configure an external identity provider"

     **Step 3.1: Create and configure an OIDC/OAuth2.0 application in the external identity provider**

     1. Create an OIDC/OAuth2.0 application in your external identity provider.
     2. Configure the OIDC/OAuth2.0 application as follows:

         1. Set `Code` and `Refresh Token` as allowed grant types.
         2. Add the following as authorized redirect URL.
         3. Specify the following as authorized redirect URLs:
         4. Specify the access token type as JWT.

            !!! tip
                 If you want to invoke APIs secured with role-based access control, you must ensure that users are assigned a role mapping that grants the necessary permission for API invocation. The approach of mapping application roles to users can vary depending on the identity provider.

     **Step 3.2: Link the OIDC/OAuth2.0 application to the Choreo component**

     1. In the Choreo Console, go to the component for which you want to manage OAuth keys.
     2. In the left navigation menu, click **Settings**.
     3. Click the **Authentication Keys** tab and then click on the environment for which you want to generate keys.
     4. In the **Identity Provider** list, select your identity provider.
     5. Paste the **Client ID** and **Client Secret** of the OIDC/OAuth2.0 application you created in your external identity provider.
     6. Click **Add Keys**.
