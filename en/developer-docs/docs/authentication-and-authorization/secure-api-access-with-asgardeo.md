# Secure API Access with Asgardeo

API security refers to the measures and practices used to protect Application Programming Interfaces (APIs) from potential threats and vulnerabilities. APIs are essential for enabling communication and data exchange between different software applications and services, making them a critical component in modern software development. However, their openness and accessibility can also make them targets for various security risks. Authentication and authorization are key aspects of API security. Authentication is ensuring that only authorized users or applications can access the API. This can involve using API keys, tokens, or more advanced authentication methods like OAuth 2.0. Authorization is controlling what authenticated users or applications are allowed to do within the API. Authorization mechanisms restrict access to specific resources and actions based on user roles or permissions. 

Organizations using Asgardeo for identity and access management (IAM) can seamlessly integrate it with Choreo as an external Identity Provider (IdP). This guide will walk you through setting up Choreo to authenticate API invocations through Asgardeo which is configured as an external IdP.

This guide walks you through the following steps:

- Assign scopes to an API in Choreo. 
- Create an API in Asgardeo.
- Create an application in Asgardeo and consume the Asgardeo API.
- Create an application in Choreo and enable external IdP authentication.
- Invoke the API with scopes.

## Prerequisites

To follow this guide, you need to satisfy the following prerequisites:

- [Configured Asgardeo as an external IdP](../administer/configure-an-external-idp/configure-asgardeo-as-an-external-idp.md) 
- If you don't already have a service in Choreo, [develop a service](../develop-components/develop-services/develop-a-service.md) or an [API Proxy](../develop-components/develop-an-api-proxy.md).
- Deploy and publish your API. 

## Step 1: Assign scopes to an API in Choreo

You can provide fine-grained access control to your API resources with scopes. Follow the steps below to assign a scope to the resources in the API:

1. In the **Component Listing** pane, click on the component you want to attach scopes to.
2. In the left navigation menu, click **Manage** and then **Permissions**.
3. Click **+ Add Permission (Scope)**. 
4. In the **Permission List** pane, enter the permission value and click **+ Add New**.
5. Click the copy icon in front of the added scope to copy the fully qualified name of the scope. Save this value for future reference. 
6. To attach a scope to a resource, click the **Select Permissions** list under the respective resource, and select the scopes you wish to attach.
7. Click **Save and Deploy**.
8. In the left navigation, click **Manage** and then **Lifecycle**.
9. Click **Publish** and continue to publish your API to the Choreo Developer Portal. 


## Step 2: Create an API and an application in Asgardeo

 Follow the [Asgardeo API Authorization guide](https://wso2.com/asgardeo/docs/guides/api-authorization/) to create an application and an API in Asgardeo and to enable API authorization.

!!! note
     - Use the fully qualified name of the scope when adding scopes. 
     - Do the following under the protocol tab:
        - Select `JWT` as the **Access Token**.
        - Select the appropriate grant types.
        - Copy the client ID and client secret of the application for future reference.

## Step 3: Create an application in Choreo and enable external IdP authentication

Follow the steps below to consume the Choreo API and use an external IdP for authentication:

1. Sign in to the Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev).
2. Click **Applications**. and then click **+Create**.
3. Enter a name and description for the application. 
4. Click **Create**.
5. In the left navigation, under **Credentials** and click **Production**.
6. Select the **Identity Provider** as `Asgardeo`.
7. Enter the **Client ID** you copied in [step 2](#step-2-create-an-api-and-an-application-in-asgardeo).
8. Click **+Add**.

    !!! note 
        - You can only use the Client ID in one application.
        - The Identity Provider dropdown is visible only to organizations where you have configured external IdPs. 

9. In the left navigation menu, click **Subscriptions**.
10. In the **Subscription Management** pane that opens, click **+ Add APIs**.
11. Select the API you assigned scopes to in [step 1](#step-1-assign-scopes-to-an-api-in-choreo) and click **Add**. 

## Step 4: Invoke the Choreo API with scopes

1. On the Choreo Developer Portal, go to your application. 
2. In the left navigation menu, under **Credentials** and click **Production**.
3. Under **Endpoints**, copy the **Token Endpoint** URL. 
4. Obtain an access token by invoking the token endpoint as follows:
   
    !!! note
        - If you are using the production credentials, you need to deploy your component(endpoint) to the production environment by promoting it from the development environment.
        - If you are using the sandbox credentials, you can use the endpoints deployed in the development environment.

    === "Format"

        ``` sh
        curl -X POST '<TOKEN_ENDPOINT>?grant_type=password&scope=<REQUIRED_SCOPES>&username=<USER_NAME>&password=<USER_PASSWORD>' \
         --header 'Content-Type: application/x-www-form-urlencoded' \
         --header 'Authorization: Basic <BASE64-ENCODED ASGARDEO_APP_CLIENT_ID:ASGARDEO_APP_CLIENT_SECRET>'
        ```

    === "Example"

        ``` sh
        curl -X POST 'https://dev.api.asgardeo.io/t/orgHandle/oauth2/token?grant_type=password&scope=<REQUIRED_SCOPES>&username=<USER_NAME>&password=<USER_PASSWORD>' \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --header 'Authorization: Basic <BASE64-ENCODED CLIENT_ID:CLIENT_SECRET>'
        ```



5. Once you receive the access token, you can [test invoking the resource using the OpenAPI console](../testing/test-rest-endpoints-via-the-openapi-console.md) in Choreo by specifying the scope. 

