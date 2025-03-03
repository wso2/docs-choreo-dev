# Secure an API with Role-Based Access Control

Role-based access control (RBAC) is a flexible and scalable approach to manage access to API resources. In this approach, each user or group is assigned a specific role that determines the permissions granted to perform operations on an API resource.

This tutorial explains how to implement RBAC using Choreo and Asgardeo. It includes a real-world scenario with instructions to create and publish an API proxy component in Choreo and apply role-based access control.

## Scenario

An organization needs to implement a user management service to keep track of users. The service needs to perform the following operations:

- List existing users
- List a specific user
- Create new users
- Delete a user

There are two types of users in the organization:

- **Human resource manager (HR manager)**: Can perform all operations (list users, list a user, create users, and delete a user).
- **Human resource officer (HR officer)**: Can only list existing users and list a specific user.

## Implement role-based access control with Choreo and Asgardeo

Let’s take a look at the steps to implement the scenario described above using Choreo and Asgardeo.

### Prerequisites

- If you're signing in to the Choreo Console for the first time, create an organization:
   1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your preferred method.
   2. Enter a unique organization name. For example, `Stark Industries`.
   3. Read and accept the privacy policy and terms of use.
   4. Click **Create**.

This creates the organization and opens the home page of the default project created for you.

### Step 1: Create an API proxy component and deploy it

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. To create an API proxy component, follow the instructions in [Develop an API Proxy: Step 1](../develop-components/develop-an-api-proxy.md#step-1-create-an-api-proxy). This opens the **Resources** pane, where you can define resources for the API proxy.
3. In the **Resources** pane, add the following resources:
    - **GET** `/users`
    - **GET** `/users/{userID}`
    - **POST** `/users`
    - **DELETE** `/users/{userID}`
4. Remove the five default resources that start with `/*` by clicking the delete icon corresponding to each resource.
5. Click **Save**. The API resources will look like this:

    ![API resources](../assets/img/tutorials/role-based-auth/api-resources.png)

6. In the left navigation menu, click **Deploy**.
7. Go to the **Build Area** card and click **Configure & Deploy**.
8. In the **Configure API Access Mode** pane, select **External** to make the API publicly accessible.
9. Click **Deploy**.

### Step 2: Apply permissions to resources and publish the API

1. On the **Deploy** page, go to the **Build Area** card and click **Security Settings**.
2. In the **Security Settings** pane, go to the **Permissions List** section and click **+ Add Permission(Scope)**.
3. Add the following permission values:
    - `get_user_list`
    - `get_user`
    - `create_user`
    - `delete_user`
4. In the **Permissions** section, assign permissions to resources as follows:

    | **Resource**             | **Permission** |
    |--------------------------|----------------|
    | **GET /users**           | `get_user_list`|
    | **GET /users/{userID}**  | `get_user`     |
    | **POST /users**          | `create_user`  |
    | **DELETE /users/{userID}**| `delete_user`  |

5. Click **Apply**.
6. Redeploy the API to apply the latest permissions:
    1. Go to the **Build Area** card and click **Configure & Deploy**.
    2. Select **External** as the access mode and click **Deploy**.
7. Promote the API to production:
    1. In the left navigation menu, click **Deploy**.
    2. Go to the **Development** card and click **Promote**.
    3. In the **Configure & Deploy** pane, click **Next** to promote the API to production.
8. Publish the API:
    1. In the left navigation menu, click **Manage** and then click **Lifecycle**.
    2. In the **Lifecycle Management** pane, click **Publish**. This changes the API status to **Published**.

Now, application developers can discover the API, subscribe to it, and invoke it.

### Step 3: Subscribe to the published API

1. Go to the [API Developer Portal](https://devportal.choreo.dev/) and click **Applications** on the top menu.
2. Click **+Create**.
3. Enter `User Management App` as the **Application Name** and click **Create**.
4. In the Developer Portal left navigation menu, click **Production** under **Credentials**.
5. Expand **Advanced Configurations** and:
    1. Select **Code** as the grant type.
    2. Enter the hosted URL of the application as the **Callback URL**.
    3. Click **Generate Credentials**.
6. Subscribe to the API:
    1. In the Developer Portal left navigation menu, click **Subscriptions**.
    2. In the **Subscription Management** pane, click **Add APIs**.
    3. In the **Add APIs** list, go to the API you created and click **Add**.

### Step 4: Define roles and assign them to groups

1. In the Developer Portal left navigation menu, click **Production** under **Credentials**.
2. In the **Permissions** section, click **Manage Permissions**. This takes you to the **Roles** tab of the `User Management App` application in Asgardeo.
3. Add the following roles:
    - **admin**: Assign permissions `get_user_list`, `get_user`, `create_user`, and `delete_user`.
    - **user**: Assign permissions `get_user_list` and `get_user`.
4. Create groups and assign roles:
    - **HR-Manager**: Assign the **admin** role.
    - **HR-Officer**: Assign the **user** role.

### Step 5: Define users and assign them to groups

1. Define two users: `Cameron` and `Alex`. For instructions, see [Manage Users](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#onboard-a-user) in the Asgardeo documentation.
2. Assign `Cameron` to the **HR-Manager** group and `Alex` to the **HR-Officer** group. For instructions, see [Assign Groups](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#assign-groups).

### Step 6: Obtain an access token and try out the API

1. Construct the authorization URL as follows:

    ```
    <authorize_URL>?response_type=code&client_id=<clientID>&redirect_uri=<redirect_URL>&scope=<scopes>
    ```

    - Replace `<authorize_URL>` with the **Authorize Endpoint** URL.
    - Replace `<redirect_URL>` with the **Callback URL**.
    - Replace `<scopes>` with the applicable permissions (e.g., `get_user_list get_user` for `Alex`).
    - Replace `<clientID>` with the **Consumer Key**.

2. Open the constructed URL in a web browser and sign in with `Alex`'s credentials. Click **Allow** to approve the consent.
3. Copy the code from the callback URL and use it to replace `<code>` in the following cURL command:

    ```
    curl <token_url> -d "grant_type=authorization_code&code=<code>&redirect_uri=<redirect_uri>" -H "Authorization: Basic <base64(clientId:clientSecret)>"
    ```

    - Replace `<token_url>` with the **Token Endpoint** URL.
    - Replace `<redirect_uri>` with the **Callback URL**.
    - Replace `<base64(clientId:clientSecret)>` with the Base64-encoded value of `clientId:clientSecret`.

4. Extract the access token from the response.
5. Go to the [API Developer Portal](https://devportal.choreo.dev/) and try out the API using the access token. Observe that `Alex` can only access the following resources:
    - **GET /users**
    - **GET /users/{userID}**

    Attempting to access other resources will result in a scope validation error.

    Similarly, if you use `Cameron`'s credentials, you can access all four resources because the token includes all required permissions.

Now you have gained hands-on experience in implementing role-based access control with Choreo and Asgardeo.
