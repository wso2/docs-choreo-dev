# Testing Secure API Access with Choreo built-in Security Token Service

API security refers to the measures and practices used to protect Application Programming Interfaces (APIs) from potential threats and vulnerabilities. APIs are essential for enabling communication and data exchange between different software applications and services, making them a critical component in modern software development. However, their openness and accessibility can also make them targets for various security risks. Authentication and authorization are key aspects of API security. Authentication is ensuring that only authorized users or applications can access the API. This can involve using API keys, tokens, or more advanced authentication methods like OAuth 2.0. Authorization is controlling what authenticated users or applications are allowed to do within the API. Authorization mechanisms restrict access to specific resources and actions based on user roles or permissions.

Choreo's built-in security token service has authorization capabilities to issue scopes based the relationship between scopes, roles and user groups. You have the capability to define roles and assign permissions and user groups to the roles. The user to group relationship can be configured through Choreo built-in IdP.

This guide explains how you can test invoking APIs secured with permissions using the Choreo built-in authorization capabilities. The steps include:

- Assign scopes to an API in Choreo.
- Create roles and assign permissions in Choreo.
- Assign user groups to roles.
- Test API invocation.
    - When Choreo is managed authentication is enabled.
    - When application is managing the authentication on it’s own.

## Prerequisites

To follow this guide, you need to satisfy the following prerequisites:

- Configure the Choreo built-in IdP with users.
- An API: If you don't already have an API in Choreo, develop a REST API or a REST API Proxy.
- Deploy and publish your API.
- A Web Application: To subscribe to the APIs. If you don't have an application in Choreo, create a web application
- Administrator rights to your Choreo organization: You need this to configure role-group and role-permission mapping.

## Step 1: Assign permissions to an API in Choreo
You can provide fine-grained access control to your API resources with scopes. Follow the steps below to assign a scope to the resources in the API:

1. In the **Component Listing** pane, click on the component you want to attach scopes to.
2. In the left navigation menu, click **Manage** and then **Permissions**.
3. Click **+ Add Permission (Scope)**.
4. In the Permission List pane, enter the permission value and click **+ Add New**.
5. Click the copy icon in front of the added scope to copy the fully qualified name of the scope. Save this value for future reference.
6. To attach a scope to a resource, click the Select Permissions list under the respective resource, and select the scopes you wish to attach.
7. Click **Save and Deploy**.
8. In the left navigation, click **Manage** and then **Lifecycle**.
9. Click **Publish** and continue to publish your API to the Choreo Developer Portal.

## Step 2: Create Roles and Assign Permissions

The permissions assigned to your APIs need to be associated with roles. Follow the steps below to create roles and assign permissions to the roles.

1. Select the project which contains your component. In the left navigation menu, click **Settings**.
2. Click  **Role Management**.
3. Click  **+ Role**.
4. In the Add page you will notice a list of permissions that can be assigned to the role. These permission are based on the scopes of the subscribed APIs of the project components as well.
5. Fill the role details, select the required permission to consume the API and click **Create Role**.

## Step 3: Assign user groups to Role

Created roles needs to be assigned to the user groups defined in your Choreo built-in IDP to ensure the authenticated users will be able to obtain access tokens with required permissions.

1. Navigate to **Settings** section of your **organization**.
2. Click on **Role Management**.
3. Roles defined within different projects can be seen in the listed.
4. Click on **Map Groups** for the created role.
5. Type the group name(s) and press Enter and Click **Save**.


## Step 4: Test API invocation

For API invocation a subscription needed to be created. For this, create a web application component. If your web application is a SPA web application, you also have the option to configure Choreo to manage the authentication on behalf of the application. This will minimize the requirement to implement OAuth protocol specific logic to your application. Subscribe to the API from the created web application

a) When Choreo managed authentication is enabled.

If Choreo managed authentication is enabled, Chore will automatically obtain the permissions required to invoke the subscribed APIs when requesting access tokens and you can invoke the APIs through the web application without further intervention.

b) When application is managing the authentication

If you are managing the authentication for your application on your own, you can generate the OAuth credentials required to obtain access tokens.

1. Click on the **Settings** section in your **web application component**.
2. Navigate to the **Authentication** section.
3. Select a desired environment and select **Choreo Built-In Identity Provider** from the drop-down.
4. Expand the Advanced Configuration section and make sure `code` and `refresh` grants are enabled. This is required to obtain access tokens with authorization code grant.
5. Configure the callback URL of the web application to receive the authorization code.
6. Click on **Generate Keys** to get the client ID and secret.
7. Get an access token using the Authorization code grant indicating the desired OAuth scopes (refer the endpoints define right hands side). and obtain the JWT access token.
    - When prompted for authentication, provide credentials of a user residing in the built-in IDP who has the required groups assigned.
    - Complete the OAuth flow and obtain the JWT access token.
8. Invoke the subscribed API presenting the obtained access token
