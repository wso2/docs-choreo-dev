# Test Secure API Access with Choreo Built-In Security Token Service

API security can protect APIs from potential threats and vulnerabilities, with authentication and authorization playing key roles. Authentication ensures that only authorized users or applications can access the API. This involves using API keys, tokens, or more advanced authentication methods like OAuth 2.0. Authorization governs the actions permitted for authenticated users or applications within the API. Authorization mechanisms restrict access to specific resources and actions based on user roles or permissions.

Choreo simplifies security testing for developers, allowing them to easily test APIs with permissions in non-critical environments. With its integrated security token service, Choreo provides authorization features that generate scopes based on the correlation between scopes, roles, and user groups. Developers can create roles, assign permissions, and set up user-group mappings using Choreo's built-in identity provider (IdP).

This guide walks you through the following steps to test the invocation of secured APIs with permissions using Choreo's built-in authorization capability:

- Assign scopes to an API in Choreo.
- Create roles and assign permissions in Choreo.
- Assign roles to user groups.
- Test the API invocation.
    - When Choreo manages the authentication (i.e., managed authentication enabled).
    - When the application independently handles the authentication (i.e., managed authentication disabled).

## Prerequisites

Before you try out this guide, ensure you have set up the following:

- Configure the Choreo built-in identity provider with users. For step-by-step instructions, see [Configure a User Store with the Built-In Identity Provider](../administer/configure-a-user-store-with-built-in-idp.md).
- Deploy and publish an API via Choreo. If you don't have an existing service in Choreo, you can either [develop a service](../develop-components/develop-services/develop-a-service.md) or an [API Proxy](../develop-components/develop-an-api-proxy.md).
- A web application for API subscription. If you don't have an application in Choreo, you must [create a web application](../develop-components/develop-web-applications/build-and-deploy-a-single-page-web-application.md)
- Administrator rights in your Choreo organization. You need this access to configure role-group and role-permission mappings.

## Step 1: Assign permissions to an API in Choreo

You can provide fine-grained access control to your API resources with permissions. Follow the steps below to assign permissions to the resources in the API:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in.
2. In the Choreo Console top navigation menu, click the **Project** list and select the project that contains your component.
3. In the **Component Listing** pane, click on the component for which you want to attach permissions.
4. In the left navigation menu, click **Deploy**.
5. On the **Deploy** page, go to the **Set Up** card and click **Endpoint Configurations**. This opens the **Endpoint Configurations** pane.
6. Go to the **Permissions List** section and click **+ Add Permission(Scope)**.
7. In the **Permissions List** section, enter a permission value and click **+ Add New**.
8. Click the copy icon in front of the added permission to copy the fully qualified name of it. Save this value for future reference.
9. To attach permissions to a resource, click the **Select Permissions** list under the respective resource and select the permissions you want to attach.
10. Click **Apply**.
11. To apply the latest permissions to the deployed component, you must redeploy it. Follow the steps below to redeploy:
    1. Go to the **Set Up** card and click **Configure & Deploy**.
    2. In the **Configurations** pane that opens, click **Next**. This opens the **Endpoint Details** pane.
    3. Click **Deploy**.

12. To publish your API to the Choreo Developer Portal, follow the steps given below:
    1.  In the left navigation menu, click **Manage** and then click **Lifecycle**.
    2.  Click **Publish**.

## Step 2: Create roles and assign permissions

The permissions assigned to your API need to be associated with roles. Follow the steps below to create roles and assign permissions to the roles.

1. In the Choreo Console, go to the top navigation menu, click the **Project** list, and select the project that contains your component. 
2. In the left navigation menu, click **Settings**.
3. Click the **Application Security** tab.
4. Click **+ Role**.
5. Specify an appropriate **Role Name** and **Role description**. 
6. Select the permissions you want to assign to the role, and then click **Create**.
  
    !!!tip

            The permissions(scopes) defined for APIs exposed via components in the project and the permissions(scopes) required by connections created for components in the project are listed here. 

## Step 3: Assign roles to user groups

You must assign roles to the user groups defined in your Choreo built-in IdP to ensure that authenticated users can obtain access tokens with the required permissions.

1. In the Choreo Console, go to the top navigation menu, click the **Organization** list, and select the organization where you created your component.
2. Click the **Application Security** tab and then click **Role Management**.
  
    !!!tip

            The roles defined within different projects in the organization are listed here.

3. Click **Map Groups** corresponding to a role that you want to assign to a group.
4. Specify a group name and enter to add it. You can add multiple groups if necessary.
5. Click **Save**.

## Step 4: Test the API invocation

To test an API invocation, you must first create a connection to your API. To do this, you must have a web application created. You can use the web application you created while setting up the prerequisites.

To create a connection to the web application, follow the steps given below:

1. In the Choreo Console, go to the top navigation menu, click the **Project** list, and select the project where you created the web application.
2. On the project home page, click the web application listed under **Component Listing**.
3. In the left navigation menu, click **Dependencies** and then click **Connections**.
4. Create a connection to the API you deployed in [Step 1](#step-1-assign-permissions-to-an-api-in-choreo). 
   
Now you can proceed to deploy the web application.

When deploying, if your web application is a single-page application (SPA), you have the option to allow Choreo to handle authentication on behalf of the application. This approach eliminates the need to incorporate OAuth protocol-specific logic into your application.

### Test the invocation when Choreo-managed authentication is enabled

If managed authentication is enabled for your web application, Choreo automatically handles obtaining the necessary permission for API invocation. This occurs during the request for access tokens, allowing you to seamlessly invoke the subscribed APIs through your web application without additional intervention. 

!!! note
    If you change the permissions of an existing connection or create a new connection with permissions, you must redeploy your web application to ensure proper API invocation with managed authentication.

### Test the invocation when the application manages the authentication

If your application manages authentication independently, follow the steps below to generate the necessary OAuth credentials to obtain access tokens:

1. In the left navigation menu, click **Settings**. This opens the settings of the web application component.
2. Click the **Authentication Keys** tab.
3. Click on an environment tab depending on the environment for which you want to generate credentials.
4. Select **Choreo Built-In Identity Provider** as the identity provider.
5. Click to expand **Advanced Configurations** and make sure the `code` and `refresh` grant types are selected. This is required to obtain access tokens with an authorization code grant.
6. Configure the callback URL of the web application to receive the authorization code.
7. Click **Update Configurations**.
8. Click **Regenerate Secret** and make a note of the client ID and secret that is generated.
9. Retrieve an access token using the authorization code grant, specifying the necessary OAuth scopes (You can see the endpoint details on the right side).
    - When prompted for authentication, enter the credentials of a user within the built-in identity provider (IdP) who possesses the required assigned groups.
    - Navigate through the OAuth flow to obtain the JWT access token.
10. Invoke the subscribed API using the access token.
