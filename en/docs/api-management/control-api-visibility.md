# Control API Visibility 

By default, the APIs published in Choreo are visible to anyone who visits the Choreo Developer Portal. By default, Choreo sets the visibility of the API to `Public`. However, developers can control the visibility of their APIs by changing the default option to `Private` or `Restricted`. 

Visibility settings control users from viewing and modifying APIs. API visibility can be one of the following options:

 - **Public** : The API is **visible to all** in the developer portal.

 - **Private** : The API is visible to the **users who only sign in to the Developer Portal**.

 - **Restricted**: The API is **visible to only the user that has the roles that you specify**. This option helps developers to enforce fine-grained access control to the API.

## Change API visibility

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the REST API (Service) for which you want to control API visibility. 
3. In the left navigation menu, click **Manage** and then click **API Info**.
4. On the **API Info** page that opens, click the **Developer Portal** tab.
5. Under **General Details**, select the required visibility setting from the **Visibility** list. 

    !!! note "Enable fine-grained role-based access control to the API"
         1. To enable fine-grained role-based access control to an API in the Developer Portal, select `Restricted` from the **API visibility** list. Once selected, you will see the roles available in your organization in the **Visible Roles** list. 
         2. Select any combination of roles. Only the users with the given roles can access the APIs through the Dev Portal. 
         3. Alternatively, You can create a new role and assign it to an API by following the steps below: 
             1. Click **+ Create New Role** in the list.
             2. Add the role name and description. 
             3. Click **Next**.
             4. Assign the relevant permissions to the new role.
             5. Click **Create**.
             6. Select the newly created role from the **Visible Roles** list.

6. Click **Save**.
