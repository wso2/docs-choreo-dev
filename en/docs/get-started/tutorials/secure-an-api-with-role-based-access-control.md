# Secure an API with Role-Based Access Control

Role-based access control is a flexible and scalable approach to manage access to API resources. In this approach, each user or group is assigned a specific role that determines the permission granted to perform operations on an API resource.

This tutorial explains how to implement role-based access control using Choreo and Asgardeo. It includes a real-world scenario with instructions to create and publish a REST API proxy component in Choreo and apply role-based access control.

## Scenario

An organization needs to implement a user management service to keep track of users. The service needs to perform the following four operations:

  - List existing users
  - List a user
  - Create new users
  - Delete a user

There are two types of users in the organization:

  - Human Resource Manager (HR manager)
  - Human Resource Officer (HR officer)

Each user type requires different levels of access to the user management service.

  - The HR manager must have permission to perform the following operations: list existing users, list a user, create new users, and delete a user.
  - The HR officer must have permission to perform the following operations: list existing users and list a user.

## Implement role-based access control with Choreo and Asgardeo

Let’s take a look at the steps to follow to implement the scenario described above using Choreo and Asgardeo.

### Prerequisites

If you created your organization in Choreo before 21st of February 2023, follow these steps to enable Asgardeo as the default key manager:
  
!!!tip

     If you created your organization in Choreo after the 21st of February, 2023, Asgardeo will be the default key manager.

   1. Go to the Choreo Console, click **Settings**, and then click **API Management**.
   2. On the **API Management** page, click **Enable Asgardeo Key Manager**.

       ![Enable key manager](../../assets/img/tutorials/role-based-auth/enable-asgardeo-key-manager.png){.cInlineImage-threeQuarter}.
  
    !!!info

            Enabling Asgardeo as the default key manager has the following effects:

            - Existing applications created via the Developer Portal in your organization will continue to function as usual. However, any new application you create within the organization will use Asgardeo as the key manager.
            - New grant types such as **Password**, **Authorization Code**, **PKCE**, and **Implicit**, which were not available in Choreo will become available.
            - The capability to add an external IdP will be disabled because Asgardeo does not support token exchange. However, you can use the federation capabilities of Asgardeo to achieve similar functionality.


### Step 1: Create a REST API proxy component and deploy it

Follow these steps to create a REST API proxy component, add resources, and deploy it:

1. To create a REST API proxy component, see the instructions in the [Create Your First REST API Proxy tutorial: Step 1](../tutorials/create-your-first-rest-api-proxy.md#step-1-create-a-rest-api-proxy).
2. In the **Develop** view, click **Resources**, and follow these steps to add resources:

     - Select **GET** as the **HTTP Verb**, enter `/users` as the **URI Pattern**, and click **+** to add the resource.
     - Select **GET** as the **HTTP Verb**, enter `/users/{userID}` as the **URI Pattern**, and click **+** to add the resource.
     - Select **POST** as the **HTTP Verb**, enter `/users` as the **URI Pattern**, and click **+** to add the resource.
     - Select **DELETE** as the **HTTP Verb**, enter `/users/{userID}` as the **URI Pattern**, and click **+** to add the resource.
     
3. Click **Save**. The API resources will be as follows:

    ![API resources](../../assets/img/tutorials/role-based-auth/api-resources.png){.cInlineImage-full}

4. In the left navigation menu, click **Deploy**.
5. Go to the **Build Area** card and click **Configure & Deploy**. The **Configure & Deploy** pane opens with the endpoint populated as `https://samples.choreoapps.dev/company/hr`.
6. Click **Save & Deploy**.

### Step 2: Apply permission to resources and publish the API

Follow these steps:

1. In the left navigation menu, click **Manage**, and then click **Permissions**. 
2. In the **Permissions** pane, click **+ Add Permission(Scope)**.
3. Follow these steps to add the permission values to the permission list:
   - In the **Permission List** pane, enter `get_user_list` as the permission value and click **+Add New**.
     Similarly, add `get_user`, `create_user`, and `delete_user` as permission values.
4. Select the permission values as follows for each of the resources:

    |  **Resource**             | **Permission** |
    |---------------------------|----------------|
    | **GET/users**             | `get_user_list`|
    | **GET/users/{userID}**    | `get_user`     |
    | **POST/users**            | `create_user`  |
    | **DELETE/users/{userID}** | `delete_user`  |

5. Click **Save and Deploy**.
  
    !!!note

            To verify whether the permission(scope) values have been applied to the resources, click **Settings**, then click the **Resources** tab, expand a resource, and check the attached permission value.

            ![Resource permission](../../assets/img/tutorials/role-based-auth/resource-permission.png){.cInlineImage-full}

Now you are ready to promote the API to production.

6. In the left navigation menu, click **Deploy**.
7. Go to the **Development** card and click **Promote**.  The **Configure & Deploy** pane opens with `Use Development Endpoint Configuration` selected by default.
8. Click **Next** to promote the API to production. Now you can publish the API
9. In the **Manage** view, click **Lifecycle**.
10. In the **Lifecycle Management** pane, click **Publish**.
    A message appears where you can specify whether you want to publish a connector for this REST API proxy. Creating a connector for this REST API proxy makes it available in the Marketplace. In this tutorial we will not publish a connector for the API.
11. Click **No, Thanks**. This changes the status of the API to **Published**.

Now, an application developer can discover the API, subscribe to it and then invoke it.

### Step 3: Subscribe to the Published API

In this step, you take the role of an application developer who consumes the published REST API.
To consume the REST API, you must create an application, generate keys, and subscribe to the API.

Follow these steps:

1. Go to the [API Developer Portal](https://devportal.choreo.dev/) and click **Applications** on the top menu.
2. Click **+Create**.
3. Enter `User Management App` as the **Application Name**, select **10PerMin** as the **Per Token Quota**, and then click **Create**. This creates the application and takes you to the application overview page.
4. On the left navigation menu, click **Production Keys**.
5. Click to expand **Advanced Configurations** and follow these steps to generate credentials for the application:
    1. Select **Code** as the grant type.
    2. Enter the hosted URL of the application as the **Callback URL**.
    3. Click **Generate Credentials**.
6. To subscribe to the API follow these steps:
    1. On the left navigation menu, click **Subscriptions**.
    2. In the **Subscription Management** pane, click **Add APIs**.
    3. In the **Add APIs** list, go to the API you created, and click **Add**.

### Step 4: Define roles and assign them to groups

In this step, you define roles, create necessary groups and assign appropriate roles to groups so that you can easily manage user permission.

Follow these steps:

1. In the [API Developer Portal](https://devportal.choreo.dev/), click **Manage Permission** on the top right. This takes you to the **Roles** tab in Asgardeo, where you can add applicable roles.
2. Follow these steps to add roles:
    1. Click **+ New Role**,  enter `admin` as the **Role Name** and click **Next**.
    2. Select `get_user_list`, `get_user`, `create_user`, and `delete_user`as **Role Permissions**.
    3. Click **Finish**.
    4. Similarly, add another role with `user` as the **Role Name** and `get_user_list`, and `get_user` as **Role Permissions**.
    
    Now you can proceed to create groups.
    
3. Click **Manage** on the top menu, then click **Groups** on the left navigation menu.
4. Follow these steps to add groups:
    1. Click **+ New Group**,  enter `HR-Manager` as the **Group Name** and click **Finish**. 
    2. Similarly, add another group with `HR-Officer` as the **Group Name**.

   Now, let's assign roles to groups.

5. Follow these steps to assign roles:
    1. Click to edit the **HR-Manager** group.
    2. Click the **Roles** tab and then click **+Assign Roles**.
    3. Select **admin** and click **Save**.
    4. Similarly, assign the **user** role to the **HR-Officer** group.

### Step 5: Define users and assign them to groups

In this step, you define users and assign them to groups depending on the job function of the user. This allows you to manage permission at the group level instead of for individual users.

Follow these steps:

1. Define two users named `Cameron` and `Alex`.  For step-by-step instructions on adding a user, see [Manage users](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#onboard-a-user) in the Asgardeo documentation.
2. Assign `Cameron` to the **HR-Manager** group and assign `Alex` to the **HR-Officer** group. For step-by-step instructions on assigning a user to a group, see [Assign groups](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#assign-groups) in the Asgardeo documentation.

### Step 6: Obtain an access token and try out the API

Follow these steps:

1. Construct the authorization URL as follows by replacing the placeholders with appropriate values:

     ```
     <authorize_URL>?response_type=code&client_id=<clientID>&redirect_uri=<redirect_URL>&scope=<scopes>
     ```

    - Replace `<authorize URL>` with the URL populated as the **Authorize Endpoint** in the **Application Keys** pane.

        ![Authorize endpoint](../../assets/img/tutorials/role-based-auth/authorize-endpoint.png){.cInlineImage-full}

    - Replace `<redirect_URL>` with the value specified as the **Callback URL** under **Advanced Configurations** in the **Application Keys** pane.

        ![Callback URL](../../assets/img/tutorials/role-based-auth/callback-url.png){.cInlineImage-full}

     - Replace `<scopes>` with the permission(scopes) applicable to the user. You can copy the applicable permission(scopes) from the **API Overview** page. Here let’s copy the permission(scopes) applicable to the user `Alex`.

        ![Scopes](../../assets/img/tutorials/role-based-auth/scopes.png){.cInlineImage-full}

     - Replace `<clientID>` with the value populated as **Consumer Key** in the **Application Keys** pane.

        ![Consumer key](../../assets/img/tutorials/role-based-auth/consumer-key.png){.cInlineImage-full}

2. Open the constructed URL via a web browser. This will take you to a sign-in page provided by Asgardeo.

      ![Sign-in page](../../assets/img/tutorials/role-based-auth/sign-in.png){.cInlineImage-small}

3. Sign in with credentials of `Alex` and click **Allow** to approve the consent.

      ![Approve consent](../../assets/img/tutorials/role-based-auth/consent.png){.cInlineImage-small}

    This redirects you to the callback URL of the application. The URL includes a code as a query parameter.

       ![Code](../../assets/img/tutorials/role-based-auth/code.png){.cInlineImage-half}

4. Copy the code and use it to replace `<code>` in the following command to invoke the authorization code grant token request:

     ```
     curl <token_url> -d "grant_type=authorization_code&code=<code>&redirect_uri=<redirect_uri>" -H "Authorization: Basic <base64(clientId:clientSecret)>"
     ```

    - Replace `<token_url>` with the URL populated as the **Token Endpoint** in the **Application Keys** pane.

           ![Token endpoint](../../assets/img/tutorials/role-based-auth/token-endpoint.png){.cInlineImage-full}

    - Replace `<redirect_uri>` with the value specified as the **Callback URL** under **Advanced Configurations** in the **Application Keys** pane.
    
    - Use a Base64 encoder to encode your client ID and client secret in the following format and replace `<base64(clientId:clientSecret)>` with the encoded value:

        **`<clientId>:<clientSecret>`**
  
        !!!note

                WSO2 does not recommend the use of online Base64 encoders for this purpose.

5. Extract the access token from the response that you get.

      ![Access token](../../assets/img/tutorials/role-based-auth/access-token.png){.cInlineImage-full}

6. Go to the [API Developer Portal](https://devportal.choreo.dev/), try out the HR API using the access token you extracted, and observe the responses.

    You’ll observe that it is only possible to access the following resources because the access token only contains the permission(scopes) to invoke those resources:

    - GET /users
    - GET /users/{userID}

    Invoking any other resource gives you a scope validation failed response becasue the token doesn't include the required scopes to invoke them.
       ![Scope validation failed](../../assets/img/tutorials/role-based-auth/scope-validation-failed.png){.cInlineImage-full}    

   Similarly, if you use Cameron’s credentials and sign in to obtain an access token, you'll observe that you can invoke all four resources because it would contain all the required permission(scopes).

Congratulations! You have successfully implemented role-based access control with Choreo and Asgardeo.
