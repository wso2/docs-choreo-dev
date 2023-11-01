## Overview

Choreo Managed Authentication provides a simplified way to add authentication and authorization to a single page web application.

Managed Authentication internally handles the complexities of OIDC/OAuth2 flows and provides the developer a convenient way to add authentication for a web application. A web application developer does not need to have an understanding of the OIDC/OAuth2 protocol to use managed authentication. The developer simply has to configure any OIDC provider and add authentication to the web application as explained in [how to write your app](#how-to-write-your-app).

In addition, Managed Authentication introduces a [BFF](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-browser-based-apps#section-6.2) for your web application which is a secure architecture pattern recommended for browser based applications using OIDC/OAuth2 for authentication and authorization. The pattern ensures that OAuth tokens are not accessible to browser side code  making the tokens unsusceptible for attacks such as Cross Site Scripting (XSS).

!!! note
    Managed Authentication is currently available only for React, Angular, and Vue.js buildpacks.

## Setup Managed Authentication

### Enable Managed Authentication for you Web Application

#### Enable Managed Authentication during Component creation

1. Use the toggle `Managed authentication with Choreo` under `Authentication` section to enable Managed Authentication.


#### Toggle Managed Authentication after Component creation

1. On the Deploy page, click Authentication Settings on the Set up card.
2. Enable/Disable Managed Authentication using the toggle in the sidebar.


### Configure Managed Authentication

1. On the Deploy page, click Authentication Settings on the Set up card.
2. Configure the following details.


| Option            |  Description      | Default           |
| ----------------- | ----------------- | ----------------- |
| Post Login Path   | Relative path to be redirected to after a successful login. Logic to process the userinfo cookie set by the Managed Authentication has to be implemented in your code. See [Login with userinfo](#login-with-userinfo)       | /                      |
| Post Logout Path  | Relative path to be redirected to after a successful logout.  | /                      |
| Error Path        | Relative path to be redirected to if an error occurs during a Redirection based flow (i.e. Login, Logout)             | Builtin error page     |
| Additional Scopes | All additional scopes required by the web app. Following scopes will be added by default. `openid`, `profile`, `email` and scopes required to invoke subscribed APIs.               | none                   |


### Managing OAuth Keys

1. Navigate to Authentication tab on the component settings page.
2. Select an Identity Provider.
3. If you are using the Built-in Identity Provider, click on generate credentials button. If you are using an external Identity Provider, configure the Identity Provider as instructed on the screen and add the client keys.


## How to write your app

### Prerequisite

Managed Authentication has to be [enabled](#enable-managed-authentication-for-you-web-application) and [configured](#configure-managed-authentication).


### Login

To add login capability to your web application, add the following button to your application.

    <button onClick={() => {window.location.href="/auth/login"}}>Login</button>

You can use any custom button component from your preferred UI component library. Only requirement is to redirect to `/auth/login` on button click.

Clicking on the Login button will redirect the users to the configured Identity Provider. After a successful login, the relevant session cookies will be set and the user will be redirected to the post login path (default `/`). After that, the user will be able to invoke the choreo deployed APIs permitted to them.


### Login with userinfo

After a successful login, Managed Authentication sets a `userinfo` cookie accessible from the post login path you have configured (defaults to `/`). The `userinfo` cookie contains the encoded userinfo claims shared by the Identity Provider. The cookie is intentionally set to have a short lifespan of 2 minutes. 

We recommend that you read the userinfo from the cookie and clear the cookie. 

A sample code snippet that can be added in your post login path is given below.


    import Cookies from 'js-cookie';

    // Read userinfo cookie value.
    const encodedUserInfo = Cookies.get('userinfo')

    // Decode the value. 
    const userInfo = JSON.parse(atob(encodedUserInfo))

    // Store the value in a preferred browser based storage if needed.

    // Clear the cookie.
    Cookies.remove('userinfo', { path: <post-login-path> })

!!! note
    The decision on how to use the userinfo retrieved is up to the developer. We recommend storing the userInfo in a secure storage. This stored userinfo can double as a way to check the logged in state of a user.

!!! note
    The example uses `js-cookie` library for cookie parsing. You can use any cookie parsing library of your choice. 


### Logout

To add logout capability to your web application, add the following button to your application.


    <button onClick={async () => {
        window.location.href = `/auth/logout?session_hint=${Cookies.get('session_hint')}`;
    }}>Login</button>`

!!! note
    It is recommended to clear the userinfo (if stored) at logout.

!!! note
    The example uses `js-cookie` library for cookie parsing. You can use any cookie parsing library of your choice.   

You can use any custom button component from your preferred UI component library. Only requirement is to redirect to `/auth/logout` attaching the `session_hint` cookie value on button click.

Clicking on the Login button will clear the session cookies and redirect the users to the OIDC logout endpoint of configured Identity Provider if available.  


### Userinfo

In addition to the `userinfo` cookie set after a successful [login](#login), Managed Authentication provides a GET endpoint, `/auth/userinfo`, which can be used to query logged in userinfo. This endpoint doubles as a way to check the logged in state of a user. 

A sample request to this endpoint is given in the code snippet below.

    const response = await fetch('/auth/userinfo')

If the user is logged in, a 200 OK response with the userinfo in the body in json format will be sent.

If the user is not logged in, a 401 Unauthorized response will be sent.


### API calls

Choreo APIs in the same organization as a web application can be invoked from the web application by using the relative path `/choreo-apis/<api-suffix>` irrespective of whether managed authentication is enabled for the web application or not. 

If managed authentication is enabled, the developer does not need to manually add any logic to attach an access token in the API call because Choreo APIs will accept the cookies set by Managed Authentication.

    const response = await fetch('/choreo-apis/<api-suffix>')

If Managed Authentication is disabled, the web application developer should ensure that a valid access token is attached to the API call.


### Token Refresh

If the access token provided by your configured Identity Provider expires, you need to refresh the tokens before you can successfully call a choreo deployed API. Inorder for token refresh to work, the Identity Provider should be configured to issue a refresh token along with the access token. 

You can refresh the tokens by making a POST request to the `/auth/refresh` endpoint.

    const response = await fetch("/auth/refresh", { method: "POST" })

If the refresh token is valid, the tokens will be refreshed and a 204 No Content response will be sent.

If the refresh token is also expired, a 401 Unauthorized response will be sent.

You can wrap the requests to choreo APIs with the refresh logic to automatically try a token refresh if the choreo API sends a 401 unauthorized response. A sample code snippet which wraps GET requests is given below.

    export const performGetWithRetry = async (url) => {
        try {
            // API call
            return await fetch('/choreo-apis/<api-suffix>');
        } catch (error) {
            if (error instanceof HttpError && error.status === 401) {
                // Recieved 401 Unauthorized response from API GW. Access token maybe expired.

                // Try to refresh the token
                const refreshResponse = await fetch("/auth/refresh", {
                    method: "POST"
                });

                const status = refreshResponse.status;
                if (status === 401) {
                    // Session has expired (i.e Refresh token has also expired).
                    // Redirect to login page
                    window.location.href = "/auth/login";
                }
                if (status !== 204) {
                    // Token cannot be refresh due to a server error.
                    console.log("Failed to refresh token. Status: " + status);

                    // Hence just throw the 401 error from API Gateway.
                    throw error;
                }
                // Token refresh successful. Retry the API call.
                return await fetch('/choreo-apis/<api-suffix>');
            } else {
                throw error;
            }
        }
    };
