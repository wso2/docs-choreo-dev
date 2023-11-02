# Testing Secure API Access with Choreo built-in Security Token Service

API security involves protecting APIs from potential threats and vulnerabilities, with authentication and authorization playing key roles. Authentication is ensuring that only authorized users or applications can access the API. This can involve using API keys, tokens, or more advanced authentication methods like OAuth 2.0. Authorization is controlling what authenticated users or applications are allowed to do within the API. Authorization mechanisms restrict access to specific resources and actions based on user roles or permissions.

Choreo simplifies security testing for developers, allowing them to easily test APIs with permissions in non-critical environments. With its integrated security token service, Choreo provides authorization features that generate scopes based on the correlation between scopes, roles, and user groups. Developers can create roles, assign permissions, and set up user-group mappings using Choreo's built-in Identity Provider (IdP).

This guide outlines the steps to test the invocation of secured APIs with permissions using Choreo's built-in authorization capabilities:

- Assign scopes to an API in Choreo.
- Create roles and assign permissions in Choreo.
- Assign user groups to roles.
- Test API invocation.
    - When Choreo manages the authentication (Managed Authentication enabled).
    - When the application independently manages the authentication (Managed Authentication disabled).

## Prerequisites

Before you try out this guide, ensure you have set up the following:

- [Configure the Choreo built-in IdP with users](../administer/configure-built-in-idp/configure-built-in-idp-userstore/).
- An API: If you don't already have an API in Choreo, [develop a REST API](../develop-components/develop-services/develop-a-rest-api.md) or an [API Proxy](../develop-components/develop-an-api-proxy/).
- Deploy and publish your API.
- A Web Application: To subscribe to the APIs you need a Web Application. If you do not have an application in Choreo, [create a web application](../develop-components/develop-a-web-application/).
- Administrator rights to your Choreo organization: You need this to configure role-group and role-permission mapping.

## Step 1: Assign permissions to an API in Choreo

You can provide fine-grained access control to your API resources with scopes. Follow the steps below to assign a scope to the resources in the API:

1. In the **Component Listing** pane, click on the component you want to attach scopes to.
2. In the left navigation menu, click **Manage** and then **Permissions**.
3. Click **+ Add Permission (Scope)**.
4. In the **Permission List** pane, enter the permission value and click **+ Add New**.
5. Click the copy icon in front of the added scope to copy the fully qualified name of the scope. Save this value for future reference.
6. To attach a scope to a resource, click the Select Permissions list under the respective resource, and select the scopes you wish to attach.
7. Click **Save and Deploy**.
8. In the left navigation, click **Manage** and then **Lifecycle**.
9. Click **Publish** and continue to publish your API to the Choreo Developer Portal.

## Step 2: Create Roles and Assign Permissions

The permissions assigned to your APIs need to be associated with roles. Follow the steps below to create roles and assign permissions to the roles.

1. Select the project that contains your component. In the left navigation menu, click **Settings**.
2. Click  **Role Management**.
3. Click  **+ Role**.
4. You can assign permissions from the list on this page. These permissions are based on the scopes of the subscribed APIs of the project components as well as the scopes of the APIs exposed from the project component
5. Fill in the role details, select the required permission to consume the API, and click **Create Role**.

## Step 3: Assign user groups to Role

Created roles need to be assigned to the user groups defined in your Choreo built-in IdP to ensure the authenticated users can obtain access tokens with the required permissions.

1. Navigate to **Settings** section of your **organization**.
2. Click on **Role Management**.
3. Roles defined within different projects can be seen in the list.
4. Click on **Map Groups** for the created role.
5. Type the group name(s) and press Enter and Click **Save**.

## Step 4: Test API invocation

To enable API invocation, you must first create a subscription. To do this, develop a web application component. If your web application is a SPA, you have the option to let Choreo handle authentication on behalf of the application. This approach will reduce the need to incorporate OAuth protocol-specific logic into your application. You can then proceed to subscribe to the API from within the web application you've created.

### When Choreo-managed authentication is enabled.

If Managed Authentication is enabled for your web application, Choreo will automatically handle the acquisition of necessary permissions for API invocation. This process occurs when access tokens are requested, allowing you to seamlessly invoke the subscribed APIs through your web application without any additional intervention.

### When the application is managing the authentication

If your application handles authentication independently, you can generate the necessary OAuth credentials to acquire access tokens.
1. Click on the **Settings** section in your **web application component**.
2. Navigate to the **Authentication** section.
3. Select a desired environment and select **Choreo Built-In Identity Provider** from the drop-down.
4. Expand the Advanced Configuration section and make sure the `code` and `refresh` grants are enabled. This is required to obtain access tokens with an authorization code grant.
5. Configure the callback URL of the web application to receive the authorization code.
6. Click on **Generate Keys** to get the client ID and secret.
7. Retrieve an access token using the Authorization Code Grant, specifying the necessary OAuth scopes (see endpoints on the right-hand side), and acquire the JWT access token.
   - When prompted for authentication, input the credentials of a user located within the built-in Identity Provider (IDP) who possesses the requisite assigned groups.
   - Navigate through the OAuth flow and procure the JWT access token.
9. Invoke the subscribed API using the obtained access token.
