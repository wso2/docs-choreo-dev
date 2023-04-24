# Quick Start Guide

Choreo is a full lifecycle cloud-native developer platform that enables developers to create, deploy, run, and govern APIs, integrations, and microservices on Kubernetes.

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

    1. Go to [https://wso2.com/choreo/](https://wso2.com/choreo/) and click **Try Choreo Now**.

    2. Sign in to Choreo using your Google, GitHub, or Microsoft account.

    3. Enter a unique organization name. For example, `Stark Industries`.

        ![Create an organization in Choreo](../assets/img/references/enterprise-login/create-choreo-organization.png){.cInlineImage-small}

    4. Read and accept the privacy policy and terms of use.

    5. Click **Create** to add the new organization.

    This takes you to the **Home** page of the Choreo Console.

    !!! info "Enable Asgardeo as the key manager"
         
         If you created your organization in Choreo before the 21st of February 2023, and you have not already enabled Asgardeo as the key manager, follow these steps to enable Asgardeo as the default key manager:

         1. In the Choreo Console, click your username in the top right corner.
         2. In the drop-down menu, click **Settings**.
         3. In **Settings**, click **API Management** and then click **Enable Asgardeo Key Manager**.
         4. In the confirmation dialog that opens, click **Yes**.

3. Create a new project as follows:
    
    1. Go to the **Home** page of the Choreo Console, click the **Project** list and then click **+ Create Project**.

    2. Enter a unique name and a description for the project, and click **Create**.


## Step 1: Create a service component and publish it as a REST API

In this step, you are playing the role of an API developer. You will create a service and publish it as a REST API for your web application to consume.

### Step 1.1: Create the service

Follow the steps below to create the service:

1. On the **Home** page of the Choreo Console, click on the project you created.

2. In the **Service** card, click **Create**.

3. Enter a unique name and a description for the service. For example, you can enter the name and the description given below:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Reading List Service`           |
    | **Description** | `Manages reading lists` |

4. Click **Next**.

5. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.

6. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created by forking [https://github.com/wso2/choreo-examples](https://github.com/wso2/choreo-examples) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


7. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | **`choreo-examples`** |
    | **Branch**            | **`main`**                               |
    | **Build Preset**      | Click **Ballerina** because you are creating the REST API from a [Ballerina](https://ballerina.io/) project, and Choreo needs to run a Ballerina build to build it.|
    | **Path**              | **`reading-list-service`**                    |

8. Click **Create** to initialize the service with the implementation from your GitHub repository.

This takes you to a page with an overview of the service.

###  Step 1.2: Deploy the service

For the REST endpoint of the service to be invokable, you need to deploy it. To deploy the service, follow the steps given below:

1. In the left navigation menu, click **Deploy**, then click **Deploy Manually**.
        
2. In the **Configure & Deploy** pane that opens, you can see the **Readinglist** endpoint ready to be deployed. Click the edit icon next to the **Readinglist** endpoint.

3. Change the **Network Visibility** to **Public**. This allows you to expose the endpoint to your web application securely.

4. Click **Deploy**.

    !!! info
         In this example, you deploy a Ballerina service as a REST endpoint. Therefore, the REST endpoint is generated automatically. If you deploy a non-Ballerina service, you must manually add the REST endpoint and set the network visibility to **Public**.

    This deploys the REST endpoint of the service to the development environment.

### Step 1.3: Test the service

To test the **Readinglist** REST endpoint via the integrated OpenAPI Console in Choreo, follow the steps given below:

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

    Check the **Server Response** section. On successful invocation, you will receive the `200` HTTP code.

Similarly, you can expand and try out the **GET** and **DELETE** methods.


### Step 1.4: Publish the service

Now you can publish the tested REST endpoint to make it available for the web application to consume.

#### Step 1.4.1: Update the CORS configuration

Since you expect to deploy the web application in Vercel, you would want to call the **Readinglist** backend service deployed in Choreo through the web application on a Vercel domain. By default, web browsers block such calls for security reasons. To enable the web application to call the backend service, you must update the CORS configuration as follows:

1. In the left navigation menu, click **Manage**, and then click **Settings**.

2. Under **API Settings**, click **Edit**.

3. Toggle the **CORS Configuration** switch to enable the CORS configuration.

4. Select the **Access Control Allow Credentials** checkbox.

5. In the **Apply to Development** pane that opens, enter a meaningful message and click **Apply**.

6. Click **Save**.

#### Step 1.4.2: Publish the service endpoint as a REST API

To publish the service endpoint as a REST API, follow the steps given below:

1. In the **Manage** view, click **Lifecycle**.

2. Click **Publish** to publish the REST API to the Developer Portal. External applications can subscribe to the API via the Developer Portal.

3. To access the Developer Portal, click **Go to DevPortal**.

    The **Reading List Service** REST API opens in the Developer Portal.

## Step 2: Consume the REST API

You have published the **Reading List Service** REST API to the Developer Portal where application developers can find it and subscribe their applications to it.

In the previous steps, you played the role of backend developer and developed a service. In this step, you will play the role of the web application developer who consumes the service.

To consume the **Reading List Service** REST API, you must create an application, subscribe it to the REST API, generate keys, and invoke the API.

### Step 2.1: Create an application

An application in the Developer Portal is a logical representation of a physical application such as a mobile application, web application, device, etc.

To create an application to consume the **Reading List Service** REST API, follow the steps given below:

1. In the top menu of the Developer Portal, click **Applications**.

2. Click **Create**.

3. Enter a name for the application (for example, `readingListApp` and click **Create**. This creates the application and takes you to the application overview page.

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

In this step, let's deploy a predesigned front-end application. This application is designed to personalize the reading lists based on the user ID that it obtains from its identity provider. To enable the application to obtain the user ID, let's configure Asgardeo as the identity provider.


#### Step 2.3.1: Configure Asgardeo to integrate with your application

Choreo uses Asgardeo as the identity provider for Choreo applications. When you create an application in the Choreo Developer Portal, it automatically creates a corresponding application in Asgardeo. You can go to the Asgardeo application to specify the configurations required for end users to sign in to the front-end application.

1. Access Asgardeo at [https://console.asgardeo.io/](https://console.asgardeo.io/) and sign in with the same credentials with which you signed in to Choreo.

2. Make sure you are in the same organization that you were when you created the application in the Choreo Developer Portal. You can click the **Organization** list in the Asgardeo Console top menu and ensure you are in the correct organization.  

3. In the Asgardeo Console, click **Develop** and then click **Applications**. You will see the **readingListApp** that is automatically created.

4. Click on the application to edit it.

5. Click the **Protocol** tab and apply the following changes:

    1. Under **Allowed grant types**, select **Code**.
    2. Select the **Public client** checkbox.
    3. In the **Authorized redirect URLs** field, enter `http://localhost:5173` and click the **+** icon to add the entry.

        !!! note
               The `http://localhost:5173` entry is to try out the web application locally. When you deploy your web application to Vercel, you must add the Vercel web application URL as an authorized redirect URL. 

    4. In the **Allowed origins** field, add the same URLs that you added as authorized redirect URLs.
    5. Under **Access Token**, select **JWT** as the **Token type**.
    6. Click **Update**.


#### Step 2.3.2: Configure the front-end application

In this step, you must define the ports it needs to run on, add the endpoint to the **Reading List Service** REST API, etc. 

To configure the front-end application, follow the steps given below:

1. Clone your fork of [https://github.com/wso2/choreo-examples](https://github.com/wso2/choreo-examples).

2. Open the cloned repository using an IDE such as Visual Studio Code, and make the following changes:

    1. Expand the `cloud-native-app-developer` folder. Then expand the `reading-list-front-end` folder, and click the `.env.development` file.

    2. Enter values for the parameters in the `.env.development` file, as specified in the following table:

        | **Parameter**            | **Value to specify** |
        |--------------------------------------------------|----------------------------|
        | `VITE_REDIRECT_URL`                     | Specify `http://localhost:5173` to try out the web application locally. When you deploy on Vercel, specify the Vercel web application URL.         |
        | `VITE_ASGARDEO_CLIENT_ID`             | Copy the **Client ID** from the **Protocol** tab of the **readingListApp** application in Asgardeo and paste the value.       |
        | `VITE_ASGARDEO_BASE_URL`                    | Specify the Asgardeo API URL with your organization name. i.e., `https://api.asgardeo.io/t/<ORG_NAME>`.                 |
        | `VITE_CHOREO_BACKEND_URL`                         | Go to the **Reading List Service** component overview page in the Choreo Console and copy the **Public URL** under **Endpoints**. Make sure you copy the URL from the specific environment you want.                 |

    3. Save the changes.

Now you have configured your front-end application. Next, you must configure Asgardeo as the identity provider for your application.

#### Step 2.3.3: Create a user in Asgardeo

To sign in to the **readingListApp** application and create private reading lists, the end users require user IDs. End users can self-register these user IDs in Asgardeo or request an Asgardeo user with administrative privileges to register them. For more information, see [Asgardeo Documentation - Manage users](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#onboard-a-user).

In this step, you play the role of an Asgardeo user with administrative privileges who can register user IDs.

To define a user for the **readingListApp** application, follow the steps given below:

1. Go to the [Asgardeo Console](https://console.asgardeo.io/), click **Manage** and then click **Users**.

2. On the **Users** page, click **Add User**.

3. In the **Add User** dialog, enter values for **Username (Email)**, **First Name**, and **Last Name**.

4. Under **Select the method to set the user password**, select **Invite the user to set the user password**, and make sure you select **Invite Via Email**.

5. Click **Finish**.

    Asgardeo will send you an email to set your password.  It will also open your user profile on a separate page.

6. In your user profile, toggle the **Lock User** switch to unlock your profile.

7. In the email you receive from Asgardeo (with the subject **Here is your new account in the organization <ORGANIZATION_ID>**), click **Set Password**.

8. In the **Enter new password** and **Confirm password** fields, enter a password that complies with the given criteria, and then click **Proceed**.

!!! tip 
    You can create multiple users to test your front-end application.

#### Step 2.3.4: Invoke the REST API
To run the front-end application and send requests to the **Reading List Service** REST API via it, follow the steps given below:

!!! info
    To render the front-end application as a web application, you can use the [Node Package Manager](https://www.npmjs.com) software.

1. In a new terminal, navigate to the `choreo-reading-list-application-example/reading-list-front-end` directory in the clone of your GitHub repository.

2. Issue the following commands in the given order:

    - `npm i`

        This command installs the latest [Node Package Manager](https://www.npmjs.com) version in the local directory where you have the front-end application.

    - `npm run dev`

        This command runs the front-end application as a web application.

    The front-end application will start running at `http://localhost:5173/`.

3. Access the front-end application via `http://localhost:5173/`.

4. Click **Login**, and sign in with the credentials of a user that you created in Asgardeo.

5. To allow your application to access your Choreo account, select the **User Account** checkbox and click **Allow**.

    The application opens as follows.

    ![Front-end application](../assets/img/get-started/front-end-application.png){.cInlineImage-half}

6. Add three new reading items with different statuses.

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

    Three tabs open for each status. To delete a reading list item, you can click **Delete**.

To verify whether the reading list is personalized for each user, you can sign in as a different user. The reading list items you entered above will not appear for the other user.

