# Build Your First Cloud Native Application with Choreo

Choreo is an Internal Developer Platform (IDevP) that allows you to build, deploy, monitor, and manage your cloud-native applications effortlessly.

In this quick start guide, you will explore how to expose a service endpoint as a REST API via Choreo and securely consume the API from a web application.
You will build a simple reading list web application with a sign-in page and functionality to interact with a secure backend REST API. You will use Asgardeo, WSO2's Identity as a Service (IDaaS) solution, to secure user authentication to the web application. The application will allow users to sign in and view the reading list. On signing in, a user can add books to a reading list and delete books from the reading list. The application will also allow users to sign out of the application.

This guide walks you through the following steps:

- Develop, deploy, and test a service component.
- Publish the service endpoint as a REST API for your web application to consume.
- Expose the REST API via Choreo API management.
- Securely consume the published REST API via your web application.
- Deploy your web application and use Asgardeo as the IDaaS provider to secure user authentication to the web application.

## Prerequisites

Before you try out this guide, complete the following:

1. Create a GitHub repository to save the service implementation. For this guide, you can fork [https://github.com/wso2/choreo-examples](https://github.com/wso2/choreo-examples).
2. If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the home page of the default project created for you.

    !!! info "Enable Asgardeo as the key manager"

         If you created your organization in Choreo before the 21st of February 2023, and you have not already enabled Asgardeo as the key manager, follow these steps to enable Asgardeo as the default key manager:

         1. In the Choreo Console, go to the top navigation menu and click **Organization**. This takes you to the organization's home page.
         2. In the left navigation menu, click **Settings**.
         3. Click the **API Management** tab and then click **Enable Asgardeo Key Manager**.
         4. In the confirmation dialog that opens, click **Yes**.

## Step 1: Create a service component and publish it as a REST API

In this step, you are playing the role of an API developer. You will create a service and publish it as a REST API for your web application to consume.

### Step 1.1: Create the service

Follow the steps below to create the service:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **Service** card and click **Create**.
4. Enter a unique name and a description of the service. You can enter the name and description given below:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Reading List Service`  |
    | **Description** | `Manages reading lists` |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created by forking [https://github.com/wso2/choreo-examples](https://github.com/wso2/choreo-examples) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only needed to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


8. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | **`choreo-examples`** |
    | **Branch**            | **`main`**                               |
    | **Build Preset**      | Click **Ballerina** because you are creating the REST API from a [Ballerina](https://ballerina.io/) project, and Choreo needs to run a Ballerina build to build it.|
    | **Project Path**              | **`cloud-native-app-developer/reading-list-service`**                    |

9. Click **Create**. This initializes the service with the implementation from your GitHub repository and takes you to the **Overview** page of the component.

###  Step 1.2: Deploy the service

For the REST endpoint of the service to be invokable, you need to deploy it. To deploy the service, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Deploy Manually**.
3. In the **Endpoint** pane that opens, you can see the **Readinglist** endpoint ready to be deployed. Click the edit icon next to the **Readinglist** endpoint.
4. Change the **Network Visibility** to **Public**. This setting securely exposes the endpoint for consumption.
5. Click **Update**.

    !!! info
         In this example, you deploy a Ballerina service as a REST endpoint. Therefore, Choreo generated the REST endpoint automatically. If you deploy a non-Ballerina service, you must manually add the REST endpoint and set the network visibility to **Public**.

6. Click **Deploy**. This deploys the service to the development environment.

### Step 1.3: Test the service

To test the **Readinglist** REST endpoint via the integrated OpenAPI Console in Choreo, follow the steps given below:

1. In the left navigation menu, click **Test** and then click **Console**.
2. In the **OpenAPI Console** pane that opens, select **Development** from the environment drop-down list.
3. In the **Endpoint** list, select **Readinglist**.
4. Expand the **GET** method and click **Try it out**.
5. Click **Execute**.
6. Check the **Server Response** section. You will see a response similar to the following:

    ```json
    {
    "error": "Bad Request",
    "error_description": "Error while getting the JWT token"
    }
    ```
To fix the JWT error response, you must enable passing the security context to the backend service through API management.
You can proceed to the next section to fix the JWT error and publish the service.

### Step 1.4: Publish the service

To fix the JWT error and publish the service, follow the steps given below:

#### Step 1.4.1: Fix the JWT error

1. In the left navigation menu, click **Manage** and then click **Settings**.
2. Under **API Settings**, click **Edit**.
3. Click the **CORS Configuration** toggle to enable the setting.
4. To allow cross-origin calls to send credentials, select the **Access Control Allow Credentials** checkbox.
5. To allow passing the JWT to the backend service, enable the **Pass Security Context To Backend** toggle.
6. Click **Save**.
7. In the **Apply to Development** pane that opens, enter a meaningful message and click **Apply**.

Now you can retest the API to confirm that it works as expected.

#### Step 1.4.2: Retest the API

To retest the API, follow the steps given below:

1. In the left navigation menu, click **Test** and then click **Console**.
2. In the **OpenAPI Console** pane that opens, select **Development** from the environment drop-down list.
3. In the **Public Endpoint** list, select **Readinglist**.
4. Expand the **POST** method and click **Try it out**.
5. Update the request body so that the parameters have the values given below:

    | **Parameter** | **Value**       |
    |---------------|-----------------|
    | **author**    | `Bram Stoker`   |
    | **status**    | `to_read`       |
    | **title**     | `Dracula`       |

    The request body should look as follows:

    ```json
      {
        "author": "Bram Stoker",
        "status": "To Read",
        "title": "Dracula"
      }
    ```

6. Click **Execute**.

    Check the **Server Response** section. On successful invocation, you will receive the `201` HTTP code.

Similarly, you can expand and try out the **GET** and **DELETE** methods.

#### Step 1.4.3: Publish the service endpoint as a REST API

To publish the REST endpoint and make it available for web applications to consume, follow the steps given below:

1. In the left navigation menu, click **Manage** and then click **Lifecycle**.
2. In the **Lifecycle Management** pane, click **Publish**. This publishes the REST API to the Developer Portal so that external applications can subscribe to the API.
3. To open the REST API in the Developer Portal via the **Lifecycle Management** pane, click **Go to Devportal**.

    The **Reading List Service** REST API opens in the Developer Portal.

## Step 2: Consume the REST API

You have published the **Reading List Service** REST API to the Developer Portal where application developers can find it and subscribe their applications to it.

In the previous steps, you played the role of backend developer and developed a service. In this step, you will play the role of the web application developer who consumes the service.

To consume the **Reading List Service** REST API, you must create an application, subscribe the application to the REST API, generate keys, and invoke the API.

### Step 2.1: Create an application

An application in the Developer Portal is a logical representation of a physical application such as a mobile application, web application, device, etc.

To create an application to consume the **Reading List Service** REST API, follow the steps given below:

1. In the top menu of the Developer Portal, click **Applications**.
2. Click **+ Create Application**.
3. Enter a name for the application (for example, `readingListApp`) and click **Create**. This creates the application and takes you to the application overview page.
4. On the left navigation menu, click **Production** under **Credentials**.
5. Click **Generate Credentials**. This generates credentials for the application.

### Step 2.2: Subscribe to the API

To consume the **Reading List Service** REST API, your application must subscribe to it. To subscribe to the API from the application, follow the steps given below:

1. In the left navigation menu, click **Subscriptions**.
2. Click **Add APIs**.
3. Find your REST API and click **Add**.

Now your application has subscribed to the **Reading List Service** REST API.

### Step 2.3: Deploy a web application and invoke the REST API

At the moment, any user can invoke the **Reading List Service** REST API via the **readingListApp** application (i.e., using its token) and update the reading list. However, if a user sends a request to retrieve the reading list, the response will also show entries by other users. To allow multiple users to use the application and maintain personal reading lists, you need a front-end application that allows each user to sign in with a unique user ID.

Let's deploy a front-end application to consume the API. This application is designed to personalize the reading lists based on the user ID that it obtains from its identity provider.

#### Step 2.3.1: Create a web application component

{% include "create-web-application-qsg.md" %}

#### Step 2.3.2: Deploy the web application component

Once you create the web application component, you can deploy it to the Choreo runtime. To deploy the web application component, follow the steps below:

1. In the left menu, click **Deploy**.
2. In the **Build Area** card, click **Build and Deploy**. The deployment may take a few minutes to complete.
3. Once you deploy the web application, copy the **Web App URL** from the development environment card.
4. Navigate to the web app URL. You can verify that you have successfully hosted the web application.

Although you hosted the web application, you have not configured the web application to securely invoke the service. Let's create an OAuth app in the IdP (Asgardeo) and configure the web app.

#### Step 2.3.3: Configure Asgardeo (IdP) to integrate with your application

Choreo uses Asgardeo as the default identity provider for Choreo applications. When you create an application in the Choreo Developer Portal, it automatically creates a corresponding application in Asgardeo. You can go to the Asgardeo application to specify the configurations required for end users to sign in to the front-end application.

1. Access Asgardeo at [https://console.asgardeo.io/](https://console.asgardeo.io/) and sign in with the same credentials with which you signed in to Choreo.
2. Make sure you are in the same organization that you were when you created the application in the Choreo Developer Portal. You can click the **Organization** list in the Asgardeo Console top menu and ensure you are in the correct organization.
3. In the Asgardeo Console's left navigation, click **Applications**. You will see the **readingListApp** that Choreo automatically created for you.
4. Click on the edit icon to edit the application.
5. Click the **Protocol** tab and apply the following changes:

    1. Under **Allowed grant types**, select **Code**.
    2. Select the **Public client** checkbox.
    3. In the **Authorized redirect URLs** field, enter the web app URL you copied before and click the **+** icon to add the entry.
    4. Add another `http://localhost:5173` following the above steps for authorized redirect URLs. The `http://localhost:5173` entry is to try out the web application locally. You can remove this entry when you deploy the web application to a production environment.
    5. In the **Allowed origins** field, add the same URLs that you added as authorized redirect URLs.
    6. Under **Access Token**, select **JWT** as the **Token type**.
    7. Click **Update**.

#### Step 2.3.4: Configure the front-end application

{% include "configure-front-end-application-qsg.md" %}

#### Step 2.3.5: Deploy the web application component

Once the web application component is created, you can deploy it to the Choreo runtime. To deploy the web application component, follow the steps below:

1. In the left menu, click **Deploy**.
2. In the **Development**  environment card, click **Stop**. The deployment may take a few minutes to stop.
3. Click **Re-deploy**. The deployment may take a few minutes to complete.
3. Once the web application is deployed, copy the **Web App URL** from the development environment card.
4. Navigate to the web app URL. You can now view the login button. This verifies that the web app is successfully configured.

Next, let's create a user to access the web application.

#### Step 2.3.5: Create a user in Asgardeo

To sign in to the **readingListApp** application and create private reading lists, the end users require user IDs. End users can self-register these user IDs in Asgardeo or request an Asgardeo user with administrative privileges to register them. For more information, see [Asgardeo Documentation - Manage users](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#onboard-a-user).

In this step, you play the role of an Asgardeo user with administrative privileges who can register user IDs.

To define a user for the **readingListApp** application, follow the steps given below:

1. Go to the [Asgardeo Console](https://console.asgardeo.io/) and click **Users**.
2. On the **Users** page, click **+ New User**.
3. In the **Add User** dialog, enter values for **Username (Email)**, **First Name**, and **Last Name**.
4. Under **Select the method to set the user password**, select **Invite the user to set the user password**, and make sure you select **Invite Via Email**.

5. Click **Finish**.

    Asgardeo will send you an email to set your password.  It will also open your user profile on a separate page.

6. In your user profile, toggle the **Lock User** switch to unlock your profile.
7. In the email you receive from Asgardeo (with the subject **Here is your new account in the organization <ORGANIZATION_ID>**), click **Set Password**.
8. In the **Enter new password** and **Confirm password** fields, enter a password that complies with the given criteria, and then click **Proceed**.

!!! tip
    You can create multiple users to test your front-end application.

#### Step 2.3.6: Log in and test the front-end application

To test the front-end application and send requests to the **Reading List Service** REST API via it, follow the steps given below:

1. Access the front-end application via its web URL mentioned in the web application overview page.
2. Click **Login**, and sign in with the credentials of a user that you created in Asgardeo.
**Allow**.

    The application opens as follows.

    ![Front-end application](../assets/img/quick-start-guides/front-end-application.png){.cInlineImage-half}

4. Add three new reading items with different statuses.

    For example, the details can be as follows:

     | **Title**                 | **Author**        | **Status** |
     |---------------------------|-------------------|------------|
     | `The Museum of Innocence` | `Orhan Pamuk`     | `reading`  |
     | `The Remains of the Day`  | `Kazuo Ishiguro`  | `to_read`  |
     | `David Copperfield`       | `Charles Dickens` | `read`     |

    To add each record, follow the steps given below:

    1. Click **+ Add New**.
    2. Enter values for the **Name**, **Author**, and **Status** fields.
    3. Click **Save**.

    Three tabs are open for each status. To delete a reading list item, you can click **Delete**.

To verify whether the reading list is personalized for each user, you can sign in as a different user. The reading list items you entered above will not appear for the other user.

Congratulations! You have successfully exposed a service endpoint as a REST API via Choreo and securely consumed the API from a web application.
