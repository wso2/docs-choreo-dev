# Build Your First Cloud Native Application with Choreo

Choreo is an Internal Developer Platform (IDevP) that streamlines the entire process of building, deploying, monitoring, and managing your cloud-native applications easily.

In this quick start guide, you will explore how to expose a service endpoint via Choreo and securely consume the service from a web application. You will use a simple reading list web application with a sign-in page and functionality to interact with a secure backend service. You will also use Choreo's managed authentication to easily set up authentication for your web application without having to dive into the details of security protocols. The application will allow users to sign in and view their reading lists, add books to a reading list, delete books from the reading list, and sign out of the application effortlessly.

This guide walks you through the following steps:

- Develop, deploy, and test a service component.
- Create a web application to consume the exposed service.
- Create a connection to the deployed service.
- Enable managed authentication and deploy the web application.
- Consume the deployed service via the web application.


## Prerequisites

Before you try out this guide, complete the following:

1. Create a GitHub repository to save the service implementation. For this guide, you can fork [https://github.com/wso2/choreo-samples](https://github.com/wso2/choreo-samples).
2. If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

## Step 1: Create a project and configure the sample service 

In this step, you are playing the role of an API developer. You will create a project, configure the sample service, and publish it as a REST API for your web application to consume.

### Step 1.1: Create a mono repository project

Follow the steps given below to create a mono repository project:

!!! info
     A mono repository project allows you to link a specific Git-based repository and branch to the project during project creation. This streamlines deployment by establishing a dedicated deployment track tied to a selected branch within the project. Creating a mono repository project also eliminates the need for repetitive selection of Git accounts, repositories, and branches during component creation.

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter a unique name and description for the project. You can enter the name and description given below:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Sample project`        |
    | **Description** | `My sample mono repository project` |

4. Select **Mono Repository**.
5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created by forking [https://github.com/wso2/choreo-samples](https://github.com/wso2/choreo-samples) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only needed to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

8. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Value**                                     |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | **`choreo-samples`**                         |
    | **Branch**            | **`main`**                                    |

9. Click **Next**. This scans the repository branch you connected and displays the components that are already available in it.

### Step 1.2: Configure the sample service and publish it as a REST API

1. In the **Import Component Code** pane, click **Configure** corresponding to the `Reading List Service` component.
2. In the **Component Configuration** dialog, specify values as follows for each of the fields:

    | **Field**             | **Value**                                     |
    |-----------------------|-----------------------------------------------|
    | **Component Name**    | Reading List Service                          |
    | **Buildpack**         | **Ballerina**                                 |
    | **Component Type**    | **Service**                                   |

3. Click **Save**. You will see that the status of the `Reading List Service` component has changed to **Configured** and the checkbox to select the component is enabled.
4. Select the `Reading List Service` component and click **Create**. This creates a mono repository project, initializes the service with the implementation from your GitHub repository, and takes you to the project home page.

    You can see the `Reading List Service` component listed under **Component Listing** on the project home page.

## Step 2: Build the service

To build the service, follow the steps given below:

1. On the project home page, click the `Reading List Service` component listed under **Component Listing**. This takes you to the component overview page.
2. In the left navigation menu, click **Build**.
3. In the **Builds** pane, click **Build**. This opens the **Commits** pane where you can see all the commits related to the component.
4. Select the latest commit and click **Build**. This triggers the build process and displays the build progress in the **Build Logs** pane.

    !!! info
         The build process can take a while to complete. When the build process is complete, the build will be listed in the **Builds** pane along with the build status. 

   Here, you will see the build status as **Success**.   

## Step 3: Deploy the service

For the REST endpoint of the service to be invokable, you need to deploy it. To deploy the service, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure & Deploy**.
3. In the **Configurations** pane that opens, click **Next**.
4. In the **Endpoint Details** pane that opens, you can see the **Readinglist** endpoint ready to be deployed. Click the edit icon next to the **Readinglist** endpoint.
5. Change the **Network Visibility** to **Public**. This setting securely exposes the endpoint for consumption.
6. Click **Update**.

    !!! info
         In this example, you deploy a Ballerina service as a REST endpoint. Therefore, Choreo generates the REST endpoint automatically. If you deploy a non-Ballerina service, you must manually add the REST endpoint and set the network visibility to **Public**.

7. Click **Deploy**. This deploys the service to the development environment and lists the service in the Choreo Marketplace. 

## Step 4: Test the service

To test the **Readinglist** REST endpoint via the integrated OpenAPI Console in Choreo, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console that opens, select **Development** from the environment drop-down list.
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
You can proceed to the next section to fix the JWT error and test the service once again.

## Step 5: Fix the JWT error and retest the service

To fix the JWT error and test the service once again, follow the steps given below:

1. In the left navigation menu, click **Manage** and then click **Settings**.
2. Under **API Settings**, click **Edit**.
3. Click the **CORS Configuration** toggle to enable the setting.
4. To allow cross-origin calls to send credentials, select the **Access Control Allow Credentials** checkbox.
5. To allow passing the JWT to the backend service, enable the **Pass Security Context To Backend** toggle.
6. Under **API Settings**, click **Save**.
7. In the **Apply to Development** pane that opens, enter a meaningful message and click **Apply**.

To retest the API and confirm that it works as expected, follow the steps given below:

1. In the left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console that opens, select **Development** from the environment drop-down list.
3. In the **Endpoint** list, select **Readinglist**.
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

## Step 6: Consume the service

Now that the `Reading List Service` is deployed and available in the Choreo Marketplace, application developers can discover the service via the Marketplace and consume it.  
In the previous steps, you played the role of backend developer and developed a service. In this step, you will play the role of a web application developer who consumes the service.

In this guide, you will deploy a sample front-end application to consume the service. This application will serve as the interface for users to interact with the reading list. The sample application used in this guide is designed to personalize the reading lists based on the user ID that it obtains from its identity provider. 

To host the front-end application in Choreo, you will create a web application component, set up authentication for it, and deploy it. To establish a connection between your web application and the deployed service, you will create a Connection. 

### Step 6.1: Create a web application to consume the service

To create a web application component, follow the steps given below:

1. In the Choreo console header, go to the component list and close the `Reading List Service` component. This takes you to the home page of the mono repository project you created in [Step 1.1](#step-11-create-a-mono-repository-project).
2. On the project home page, click **+ Create** under **Component Listing**.
3. Click the **Web Application** card.
4. To create the web application, specify the following values for each of the fields:

    | **Field**             | **Value**               |
    |-----------------------|-------------------------|
    | **Component Name**    | `Reading List Web App`  |
    | **Description**       | `Front-end application for the reading list service` |
    | **Buildpack**         | Select **React** because the sample front-end application is a React application built with Vite|
    | **Project Directory** | **`reading-list-app/reading-list-front-end-with-managed-auth`** |
    | **Build Command**     | **`npm install && npm run build`**            |
    | **Build Path**        | **`dist`**                                    |
    | **Node Version**      | **`18`**                                      |

5. Click **Create**. This initializes the component with the implementation from your GitHub repository and takes you to the **Overview** page of the component. 

### Step 6.2: Create a connection to the deployed service

To establish a connection between the web application you created and the deployed service, follow the steps given below:

1. In the left navigation menu, click **Dependencies** and then click **Connections**.
2. Click **+ Create**.
3. In the **Create Connection** pane, click `Reading List Service-Readinglist`.`
4. Specify values as follows for each of the fields:

    | **Field**        | **Value**                       |
    |------------------|---------------------------------|
    | **Name**         | `Reading List Connection`       |
    | **Description**  | `Connection to the reading list`|
    
5. Click **Next**. This displays the service URL of the connection for
each environment the service is deployed in. In this guide, you will see the service URL for the Development environment.
6. Click **Finish**. This opens the detailed view of the connection you created. You can copy the Service URL displayed here to use when you configure the web application before deploying it.

### Step 6.3: Build the component

To build the web application, follow the steps given below:

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build**. This opens the **Commits** pane, where you can see all the commits related to the component.
3. Select the latest commit and click **Build**. This triggers the build process and displays the build progress in the **Build Logs** pane.

    !!! info
         The build process can take a while to complete. When the build process is complete, the build will be listed in the **Builds** pane along with the build status. 

   Here, you will see the build status as **Success**.

### Step 6.4: Configure and deploy the web application

In this step, you will configure managed authentication, create a user to access the web application, and then deploy the web application.

To configure managed authentication, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure and Deploy**. This opens the **Configure & Deploy** pane, where you can specify values for the mount file.
3. Specify the following in the `config.js` file mount.
    - You must replace `<Service URL>` with the value that you copied when creating a connection to the `Reading List Service` in [step 6.2](#step-62-create-a-connection-to-the-deployed-service).

        ```javascript
        window.configs = {
            apiUrl: '<Service URL>',
        };
        ```

4. Click **Next**. This opens the **Authentication** pane.
5. Under **Authentication Settings**, make sure that you have the **Managed authentication with Choreo** toggle enabled.

    !!! tip
         Managed authentication is enabled by default when you create a web application using **React**, **Angular**, or **Vue.js** buildpacks. 

6. Specify values as follows for each of the fields:

    | **Field**           | **Value**  |
    |---------------------|------------|
    | **Post Login Path** | `/`        |
    | **Post Logout Path**| `/`        |
    | **Error Path**      | `/`        |

Next, you can create a user to access the web application.

To create a user to access the **readingListApp** application, follow the steps given below:

1. Under **Manage Users**, click **+ Create**.
2. To proceed with creating a user with the populated username and password, click **Create**. Make sure you copy the populated username and password to use when you test the front-end application.

    !!! tip
         You can create multiple users to test your front-end application.

Now, you can deploy the web application.

To deploy the web application and obtain the URL to access it, follow the steps given below:

1. In the **Authentication** pane, click **Deploy**. The deployment may take a few minutes to complete.
2. Once you deploy the web application, copy the **Web App URL** from the development environment card.
3. Navigate to the web app URL. You can verify that you have successfully hosted the web application.

## Step 7: Test the front-end application

To test the front-end application and send requests to the **Reading List Service** via it, follow the steps given below:

1. Access the front-end application via its web URL that you copied in the above step.
2. Click **Login**, and sign in with the credentials of the user that you created.

    The application opens as follows.

    ![Front-end application](../assets/img/quick-start-guides/front-end-application.png){.cInlineImage-half}

3. Add three new reading items with different statuses.

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

Congratulations! You have successfully exposed a service endpoint via Choreo and securely consumed it from a web application.
