# Quick Start Guide

Choreo is a full lifecycle cloud-native developer platform that enables your developers to create, deploy, and run new digital components like APIs, microservices, and automation in serverless mode on any Kubernetes cluster.

This guide walks you through the following:

- Developing, deploying, testing, and publishing a REST API.
- Consuming the published REST API via an external application.
- Releasing a new version of the REST API with added functionality.

Let's consider a use case where a web application developer designs an application that allows you to maintain reading lists. A user can create a private reading list by adding books. The user can also view and delete them when required. A Choreo developer develops a REST API for this web application to consume. The two developers will create two versions of the solution in two iterations. The second version of the application will add data persistence as a new feature.

The following diagram summarizes the use case:

![Use case summary](../assets/img/get-started/use-case-summary.png){.cInlineImage-full}

Let's get started!

## Prerequisites

Before you try out this guide, complete the following:

1. Create a GitHub repository to save the REST API implementation. For this guide, you can fork [https://github.com/wso2/choreo-examples/tree/version-1](https://github.com/wso2/choreo-examples/tree/version-1).

2. If you are logging in to Choreo Console for the first time, create an organization as follows:

    1. In [https://wso2.com/choreo/](https://wso2.com/choreo/), click **Try Choreo Now**.

    2. Sign in to Choreo using your Google, GitHub, or Microsoft account.

    3. Enter a unique organization name. For example, `Stark Industries`.

        ![Create an organization in Choreo](../assets/img/references/enterprise-login/create-choreo-organization.png){.cInlineImage-small}

    4. Read and accept the privacy policy and terms of use before you proceed.

    5. Click **Create** to add the new organization.

    You will be viewing the **Home** page of the Choreo Console.

3. Create a new project as follows:
    
    1. On the **Home** page of the Choreo Console, click **+ Create Project**.

    2. Enter a unique name and a description for the project, and click **Create**.


## Step 1: Create and publish a REST API

In this step, you are playing the role of the API developer. You will create and publish the REST API that the web application needs to consume.

### Step 1.1: Create the REST API

Let's create your first REST API.

1. On the **Home** page, click on the project you created.

2. Click **Create** in the  REST API card.

3. Enter a unique name and a description for the API. For example, you can enter the name and the description given below:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `readingList`           |
    | **Description** | `Manages reading lists` |

4. In the **Access Mode** field, leave the default selection (i.e., **External: API is publicly accessible**) unchanged so that users outside your organization can access your API.

5. Click **Next**.

6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.

7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the private repository you created by forking [https://github.com/wso2/choreo-examples/tree/version-1](https://github.com/wso2/choreo-examples/tree/version-1) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

     ![Authorize GitHub app](../assets/img/tutorials/connect-own-repo/authorize-github-app.png){.cInlineImage-half}


8. In the **Connect Repository** dialog box, enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | **`choreo-examples`** |
    | **Branch**            | **`version-1`**                               |
    | **Build Preset**      | Click **Ballerina** because you are creating the REST API from a [Ballerina](https://ballerina.io/) project and Choreo needs to run a Ballerina build to build it.|
    | **Path**              | **`reading-list-service`**                    |

9. Click **Create** to initialize a REST API with the implementation from your GitHub repository.

The REST API opens on a separate page where you can see its overview.

###  Step 1.2: Deploy the REST API

For the REST API to be invokable, you need to deploy it. To deploy the REST API, follow the steps given below:

1. Navigate to the Choreo Console. You will be viewing an overview of the `readingList` REST API.

2. In the left pane, click **Deploy**, and then click **Configure & Deploy**.

    !!! info
        Automatic deployment is enabled for the REST API by default. You are required to carry out only the first deployment manually.<br/><br/> When automatic deployment is enabled, your REST API is automatically deployed every time you push a commit to the GitHub repository in which its implementation resides. You can disable automatic deployment if required.
        
3. In the **Configure & Deploy** pane, click **Deploy** without entering a sandbox endpoint. 

    !!! info
        In this example, you are testing the REST API only in the Choreo Console and not in a sandbox environment. Therefore, you do not need to enter a sandbox endpoint.

    This deploys the API to the development environment.

### Step 1.3: Test the REST API

Let's test the `readingList` REST API via Choreo's Open API Console by following the steps given below:

1. Click **Test** in the left pane, and be sure that you are in the **OpenAPI Console** view. If not, click **OpenAPI Console** in the left pane.

2. Expand the **POST** method and click **Try it out**.

4. Update the request body so that the parameters have the values given below:

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
   
5. Click **Execute**.

    Check the **Server Response** section. On successful invocation, you will receive the `200` HTTP code.

Similarly, you can expand and try out the **GET** and **DELETE** methods.


### Step 1.4: Publish the REST API

Now that your API is tested, let's publish it and make it available for applications to consume.

#### Step 1.4.1: Update the CORS configuration

The application developer in this scenario calls the API from a different domain to Choreo (i.e., Vercel). By default, web browsers block these calls for security reasons. To enable the application to call the API, update the CORS configuration as follows:

1. In the left pane, click **Manage**.

2. Click **Settings**.

3. Under **API Settings** click **Edit**.

4. Toggle the **CORS Configuration** switch to enable the CORS configuration.

5. Select the **Access Control Allow Credentials** checkbox.

6. In the **Apply to Development** pane that opens on the right-hand side of the page, enter a meaningful message. Then click **Apply**.

7. Click **Save**.

#### Step 1.4.2: Publish the REST API

We are now ready to publish the REST API. To do so, follow the steps given below:

1. In the **Manage** tab, click **Lifecycle**.

2. Click **Publish** to publish the REST API to the Developer Portal. External applications can subscribe to the API via the Developer Portal.

3. To access the Developer Portal, click **Go to DevPortal**.

    The readingList REST API will open in the Developer Portal.

## Step 2: Consume the REST API

You have published the readingList REST API to the Developer Portal where application developers can find it and subscribe their applications to it.

In the previous steps, you played the role of a REST API developer and developed a REST API. In this step, you will play the role of the web application developer who will consume this REST API.

To consume the `readingList` REST API, let's create an application, subscribe it to the REST API, generate keys, and invoke the API.

### Step 2.1: Create an application

An application in the Developer Portal is a logical representation of a physical application such as a mobile app, web app, device, etc.

Let's create the application to consume the `readingList` REST API by following the steps given below:

1. In the top menu of the Developer Portal, click **Applications**.

2. Click **Create**.

3. Enter a name for the application (for example, `readingListApp` and click **Create**.

    Your Application will open on a separate page.

### Step 2.2: Subscribe to the API

To consume the REST API, the `readingListApp` application needs to subscribe to it. To subscribe your application to the API, follow the steps given below:

1. In the left navigation menu, click **Subscriptions**.

2. Click **Add APIs**.

3. Find your REST API and click **Add**.

Now your application has subscribed to the `readingList` REST API.


### Step 2.3: Deploy a Web application and invoke the REST API

At present, any user can invoke the `readingList` REST API via the `readingListApp` application (i.e., using its token) and update the reading list. However, if a user sends a request to retrieve the reading list, the response will also show entries by other users. To allow multiple users to use the application and maintain personal reading lists, you need a front-end application that allows each user to log in with a unique user ID.

In this step, let's deploy a predesigned front-end application. This application is designed to personalize the readingList based on the user ID that it obtains from its identity provider. To enable the application to obtain the user ID, let's configure Asgardeo as the identity provider.


#### Step 2.3.1: Configure Asgardeo to integrate with your application

To generate the configurations required for end users to log in to the front-end application (for example, the access token, redirect URLs, etc.), let's create an application in Asgardeo by following the steps given below:

1. Access Asgardeo at [https://console.asgardeo.io/](https://console.asgardeo.io/) and log in with the same credentials with which you logged in to Choreo.

2. Click **Single-Page Application** to start adding a single-page application.

3. Enter a name for the single-page application and enter `https://localhost:5173` as the authorized redirect URL.

4. Click **Register**.

5. Click **Protocol** to open the **Protocol** tab, and make the following changes:

    1. In the **Authorized redirect URLs** field, enter `http://localhost:5173/`. Next, click the **Add** icon.
   
    2. Under **Access Token**, select **JWT** as the token type.
   
    3. Scroll down to the **ID Token** section and enter the following value in the **Audience** field.

        `https://sts.choreo.dev/oauth2/token`

         Click **Add URL** to save this value.
   
    4. Click **Update**.

7. Click **Quick Start**, and then click the **React** icon (because you will be using a preconfigured sample react front-end application).

    A separate page opens for your application with **Integrate Your Application** option selected by default.

    !!! info   
        Under this option, you will see some instructions to configure the application. Some of these steps are already completed for the pre-configured front-end application you will be using.
        
Next, you need to pass the configurations displayed on this page to your front-end application. To do so, proceed to [Step 2.4.2 - Configure the front-end application](#step-242-configure-the-front-end-application).

#### Step 2.3.2: Configure the front-end application

To configure the front-end application by defining the ports it needs to run on, adding the endpoint to the `readingList API`, etc., follow the steps given below:

1. Clone `version-1` branch in your fork of [https://github.com/wso2/choreo-examples/tree/version-1](https://github.com/wso2/choreo-examples/tree/version-1).

2. Open the cloned repository using an IDE (for example Visual Studio Code), and make the following changes.

    1. Navigate to the `choreo-reading-list-application-example/reading-list-front-end/.env.example` file and rename it to `.env`.

    2. Enter values for the parameters in the `.env` file as given below.

    3. On the page of the application you created in Asgardeo, **Configure the AuthProvider** section displays some parameters with values. You can copy those values for some of these parameters in the `choreo-reading-list-application-example/reading-list-front-end/.env` file based on the mapping given in the following table:

        | **.env File Parameter**     | **AuthProvider Parameter** |
        |-----------------------------|----------------------------|
        | `VITE_SIGNIN_REDIRECT_URL`  | `signInRedirectURL`        |
        | `VITE_SIGNOUT_REDIRECT_URL` | `signOutRedirectURL`       |
        | `VITE_ASG_CLIENT_ID`        | `clientID`                 |
        | `VITE_BASE_URL`             | `baseUrl`                  |

    4. Enter values for the rest of the parameters as given below:

        - `VITE_RESOURCE_SERVER_URL`

            1. On the page for the `readingList` REST API in the Choreo Console, click **Test** to open the **Test** tab.

            2. In the **Open API Console** pane, copy the API endpoint.

            3. Add `/books` to the end of the API endpoint you copied to complete the resource server URL.

        - `VITE_CHOREO_CLIENT_ID`

            1. Access the Choreo Developer Portal at [https://devportal.choreo.dev/](https://devportal.choreo.dev/).

            2. In the top menu, click **Applications** and then click the **readingListApp** application.

            3. In the left navigation menu, click **Production Keys**.

            4. Copy the value in the **Consumer Key** field and paste it as the value of the `VITE_CHOREO_CLIENT_ID` parameter.

        - `VITE_ORG_HANDLE`

            This is the value displayed next to the **Asgardeo** logo in the Asgardeo Console.

            ![Organization handle](../assets/img/get-started/organization-handle.png){.cInlineImage-threeQuarters}

        - `VITE_STS_TOKEN_ENDPOINT`

            1. In the Choreo Developer Portal, open the **readingListApp** application.

            2. Copy the value in the **Token Endpoint** field and paste it as the value of the `VITE_STS_TOKEN_ENDPOINT` parameter.

Now you have configured your front-end application. Next, you need to configure Asgardeo as the identity provider for your application.

#### Step 2.3.3: Configure Asgardeo as an identity provider for Choreo

Let's deploy a predesigned front-end application. This application is designed to personalize the readingList based on the user ID that it obtains from its identity provider. To enable the application to obtain the user ID, let's configure Asgardeo as the identity provider by following the steps given below:

!!! info
    To configure an identity provider for Choreo, you need administration privileges in Choreo.

1. In the Choreo Console, open the **Home** page.

2. In the left navigation menu, click **Settings**.

3. Click API Management and under **Identity Providers**, click **+ Add Provider**.

4. In the list of identity providers, click **Asgardeo**.

5. In the **Asgardeo** dialog, enter the following information:

    1. In the **Name** and **Description** fields, enter a name and a description for the identity provider.

    2. To get the well-known URL, follow the steps given below:
    
        1. Navigate to Asgardeo and open the single-page application you created in [Step 2.4.2: Configure the front-end application](#step-242-configure-asgardeo-as-an-identity-provider).

        2. Click **Info** to view the endpoints of the application.

        3. Copy the endpoint in the **Discovery** field.

        4. Navigate to the Choreo Console and paste the URL you copied in the **Well-Known URL** field in the **Asgardeo** dialog.

    3. Click **Next**, and then click **Add**.
    
Now you have successfully configured Asgardeo as the identity provider for the front-end application.

#### Step 2.3.4: Create a user in Asgardeo

To sign in to the `readingListApp` application and create private reading lists, the end users require user IDs. The end users can self-register these user IDs in Asgardeo or request an Asgardeo user with administration privileges to add them. For more information, see [Asgardeo Documentation - Manage users](https://wso2.com/asgardeo/docs/guides/users/manage-customers/#onboard-a-user).

For this use case, you will play the role of an Asgardeo user with administration privileges who registers the user IDs.

To define a user for the readingListApp application, follow the steps given below:

1. Navigate to the Asgardeo Console.

2. On the **Home** page, click **View users** to open the **Users** page.

3. Click **Add User**.

4. In the **Add User** dialog, enter your email, first name, and last name, and click **Finish**.

    Asgardeo will send you an email to set your password.  It will also open your user profile on a separate page.

5. In your user profile, toggle the **Lock User** switch to unlock your profile.

6. In the email you received from Asgardeo (with the subject **Here is your new account in the organization <ORGANIZATION_ID>**), click **Set Password**.

7. Enter a password that matches the given criteria in the **Enter new password** and **Confirm password** fields, and click **Proceed**.

!!! tip 
    You can create more users to test your front-end application.

#### Step 2.3.5: Invoke the REST API
To run the front-end application and send requests to the `readingList` REST API via it, follow the steps given below:

!!! info
    To render the front-end application as a web application, you will be using the [Node Package Manager](https://www.npmjs.com) software.

1. In the terminal, navigate to the `choreo-reading-list-application-example/reading-list-front-end` directory in the clone of your GitHub repository.

2. Issue the following commands in the given order:

    - `npm i`

        This command installs the latest [Node Package Manager](https://www.npmjs.com) version in the local directory where the front-end application resides.

    - `npm run dev`

        This command runs the front-end application as a web application.

    The front-end application will start running at http://localhost:5173/ as logged in the terminal.

3. Access the front-end application via http://localhost:5173/.

4. Click **Login**, and sign in with the credentials of a user that you have created in Asgardeo.

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

    Three tabs will appear for each status. To delete a reading list item, you can click **Delete** for it.

To verify whether the reading list is personalized for each user, you can log in as a different user. The reading list items you entered above will not appear for the other user.

## Step 3: Create and publish a new version of the REST API

Currently, the updates to the `readinglist` REST API are saved only as long as the deployment status of the `readingList` API remains **Active**. If you undeploy and redeploy the `readingList` API, Choreo deploys it in a new container. Therefore you cannot retrieve the reading list with the updates you made after the last deployment. To persist the data even after undeploying and redeploying the REST API, let's connect it to a MySQL database. Then let's create and publish a new version of the REST API with the ability to persist the data to the MySQL database.

### Step 3.1: Provision a database

To persist your updates to the reading list, provision a MySQL database on a cloud-based platform. For example, see [AWS Documentation - Provision a database](https://aws.amazon.com/getting-started/hands-on/inventory-system-for-gaming-app-with-amazon-aurora-serverless/3/).

To allow Choreo to interact with the database, add the following IPs to the allowlist of the provisioned database:
    
    - 20.22.170.144/28
    - 20.22.170.176/28

For more information, see [Connect with Protected Third Party Applications](../reference/connect-with-protected-third-party-applications.md)

### Step 3.2: Create a new version of the REST API

Let's create a new version of the `readingList` REST API that can connect to a MySQL database on a cloud platform to persist data, and then redeploy it. To do this, follow the steps given below:

1. In the Choreo Console, open the `readingList` REST API (if it is not already open) and click the **Deploy** icon to open the **Deploy** tab.

2. Expand the list of versions and click **+ Create New**.

    ![Create new version](../assets/img/get-started/create-new-version.png){.cInlineImage-threeQuarters}

3. Select **version-2** as the associated GitHub branch and enter `2.0.0` as the version name. Next click **Create**.

    !!! info
        The `version-2` GitHub branch has another version of the REST API implementation with the required configurations to connect to the sample MySQL.

4. Click **Config & Deploy**. When the **Config & Deploy** pane opens, enter values for the **dbHost**, **dbUser**, **dbPassword**, **dbName**, and **dbPort** fields based on the database you provisioned in [Step 3.1: Provision a database](#step-31-provision-a-database).

5. Click **Deploy**.

Now you have successfully deployed a new version of your REST API.

### Step 3.3: Publish the new REST API version

To publish the new version of the REST API you created, repeat [Step 6.2: Publish the REST API](#step-62-publish-the-rest-api)

### Step 3.4: Consume the new REST API version

You can try out the new version via the front-end application. For more information about accessing and using the front-end application, see [Step 2.4.4: Consume the REST API via the front-end application](#step-244-consume-the-rest-api-via-the-front-end-application).

!!! info
    You do not need to subscribe the `readingListApp` application on the Developer Portal to the new REST API version because Choreo automatically creates this subscription.
