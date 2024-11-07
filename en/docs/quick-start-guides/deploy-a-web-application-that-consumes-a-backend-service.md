# Deploy a Web Application that Consumes a Backend Service

Choreo is an Internal Developer Platform (IDevP) that streamlines the entire process of building, deploying, monitoring, and managing your cloud-native applications easily.

In this quick start guide, you will explore how to expose a service endpoint via Choreo and securely consume the service from a web application. You will use a simple reading list web application with a sign-in page and functionality to interact with a secure backend service. You will also use Choreo's managed authentication to easily set up authentication for your web application without having to dive into the details of security protocols. The application will allow users to sign in and view their reading lists, add books to a reading list, delete books from the reading list, and sign out of the application effortlessly.

This guide walks you through the following steps:

- Deploy and test a service component.
- Create a web application to consume the exposed service.
- Create a connection to the deployed service.
- Enable managed authentication and deploy the web application.
- Consume the deployed service via the web application.


## Prerequisites

Before you try out this guide, complete the following:

1. Create a GitHub repository to save the service implementation. For this guide, you can fork the [Choreo sample book list app repository](https://github.com/wso2/choreo-sample-book-list-app).

2. If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

## Step 1: Create a project

Follow the steps given below to create a project:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter a display name, unique name, and description for the project. You can enter the values given below:
    
    !!! tip
         In the **Name** field, you must specify a name to uniquely identify your project in various contexts. The value is editable only at the time you create the project. You cannot change the name after you create the project.

    | **Field**                | **Value**             |
    |--------------------------|-----------------------|
    | **Project Display Name** | `Sample project`      |
    | **Name**                 | `sample-project`      |
    | **Project Description**  | `My sample project`   |

4. Click **Create**. This creates the project and opens the project home page.

## Step 2: Connect your sample repository and configure the sample service

To connect to the repository you forked in the prerequisites and configure the sample service, follow the steps given below: 

1. On the project home page, click **Start** under **Create Multiple Components**.
2. Go to the **GitHub** tab.
3. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo sample book list app repository](https://github.com/wso2/choreo-sample-book-list-app) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

4. Enter the following information:

    | **Field**                    | **Value**                   |
    |------------------------------|-----------------------------|
    | **Organization**             | Your GitHub account         |
    | **Repository**               | choreo-sample-book-list-app |
    | **Branch**                   | **`main`**                  |

5. In the **Add Component Directories** pane under **Configure Components**, click the **+** icon corresponding to `reading-list-service`.
6. In the **Component Configuration** dialog that opens, specify values as follows for each of the fields:

    | **Field**                 | **Value**                                     |
    |---------------------------|-----------------------------------------------|
    | **Component Display Name**| Reading List Service                          |
    | **Component Name**        | reading-list-service                          |
    | **Path**                  | reading-list-service                          |
    | **Component Type**        | Service                                       |
    | **Buildpack**             | NodeJS                                        |
    | **Language Version**      | 20.x.x                                        |     

7. Click **Save**. This adds the `Reading List Service` component to the **Configured Components** pane.
8. Click **Finish**. This initializes the service with the implementation from your GitHub repository and takes you to the project home page.

    You can see the `Reading List Service` component listed under **Component Listing** on the project home page.

## Step 3: Build the service

To build the service, follow the steps given below:

1. On the project home page, click the `Reading List Service` component listed under **Component Listing**. This takes you to the component overview page.
2. In the left navigation menu, click **Build**.
3. In the **Builds** pane, click **Build Latest**.

   !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

## Step 4: Deploy the service

For the REST endpoint of the service to be invokable, you need to deploy it. To deploy the service, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane that opens, click **Next** to skip the configuration.
4. In the **File Mount** pane that opens, click **Next** to skip the configuration.
5. In the **Endpoint Details** pane that opens, verify that the **Network Visibility** is set to **Public**. This setting securely exposes the endpoint for consumption.
6. Click **Deploy**. This deploys the service to the development environment and lists the service in the [Choreo Marketplace](../choreo-concepts/choreo-marketplace.md). 

## Step 5: Test the service

To test the endpoint via the integrated OpenAPI Console in Choreo, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console that opens, select **Development** from the environment drop-down list.
3. In the **Endpoint** list, select **Books REST Endpoint**.
4. Expand the **GET/books** method, click **Try it out**, then click **Execute**.
5. Click .
6. Check the **Server Response** section. You will see an empty response. You can add an entry using the POST method and retry the **GET/books** method again. 
7. Expand the **POST/books** method and click **Try it out**.
8. Update the request body so that the parameters have the values given below:

    | **Parameter** | **Value**     |
    |---------------|---------------|
    | **author**    | Bram Stoker   |
    | **status**    | to_read       |
    | **title**     | Dracula       |

    The request body should look as follows:

    ```json
      {
        "author": "Bram Stoker",
        "status": "to_read",
        "title": "Dracula"
      }
    ```
9. Click **Execute**.

    Check the **Server Response** section. On successful invocation, you will receive the `201` HTTP code.

Similarly, you can expand and try out the **GET** and **DELETE** methods.

## Step 6: Consume the service

Now that the `Reading List Service` is deployed and available in the Choreo Marketplace, application developers can discover the service via the Marketplace and consume it.  

In this section of the guide, you will deploy a sample front-end application to consume the service. This application will serve as the interface for users to interact with the reading list. The sample application used in this guide is designed to personalize the book  lists based on the user ID that it obtains from its identity provider. 

To host the front-end application in Choreo, you will create a web application component, set up authentication for it, and deploy it. To establish a connection between your web application and the deployed service, you will create a Connection. 

### Step 6.1: Create a web application to consume the service

To create a web application component, follow the steps given below:

1. In the Choreo Console header, click the **Project** list and select the project that you created in step 1.
2. On the project home page, click **+ Create** under **Component Listing**. 
3. Click the **Web Application** card.
4. Enter a display name, unique name, and a description to create the web application. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    |Field                 |     Value              |
    |----------------------|------------------------|
    |Component Display Name| Reading List Web App   |
    |Component Name        | reading-list-web-app   |
    |Description           | Front-end application for the reading list service |

5. Go to the **GitHub** tab.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo sample Book List Service repository](https://github.com/wso2/choreo-sample-book-list-service) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**             | **Description**                  |
    |-----------------------|----------------------------------|
    | **Organization**      | Your GitHub account              |
    | **Repository**        | choreo-sample-book-list-app      |
    | **Branch**            | main                             |

8. Select **React** as the buildpack because the sample front-end application is a React application built with Vite.
9. Enter the following information:

    | **Field**             | **Value**               |
    |-----------------------|-------------------------|
    | **Project Directory** | /choreo-sample-book-list-app/reading-list-front-end-with-managed-auth |
    | **Build Command**     | npm install && npm run build         |
    | **Build Path**        | dist                                 |
    | **Node Version**      | 18                                   |

10. Click **Create**. This initializes the component with the implementation from your GitHub repository and takes you to the **Overview** page of the component. 

### Step 6.2: Create a connection between the web application and the deployed service

A connection allows you to integrate the service you intend to deploy on Choreo with other services on Choreo or external resources. For more information on Choreo Connections refer to the [Connection](../choreo-concepts/connections.md) documentation.

To establish a connection between the web application you created and the deployed service, follow the steps given below:

1. In the left navigation menu, click **Dependencies** and then click **Connections**.
2. Click **+ Create**.
3. In the **Select a Service** pane, click `Reading List Service`.`
4. Specify values as follows for each of the fields:

    | **Field**        | **Value**                     |
    |------------------|-------------------------------|
    | **Name**         | Reading List Connection       |
    | **Description**  | Connection to the reading list|
    
5. Click **Create**. This creates the connection and displays the service URL of the connection for
each environment the service is deployed in. In this guide, you will see the service URL for the Development environment. You can copy the service URL to use when you configure the web application before deploying it.

### Step 6.3: Build the web application component

To build the web application, follow the steps given below:

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build Latest**.

   !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 6.4: Configure and deploy the web application

In this step, you will configure managed authentication, create a user to access the web application, and then deploy the web application. For more information on Choreo's managed authentication capability, see the [Managed Authentication](../authentication-and-authorization/secure-web-applications-with-managed-authentication.md) documentation.

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

    !!! tip
         You can refer to the configuration file mounted at `/app/public` as `./public/config.js` within your web application. 

4. Click **Next**. This opens the **Authentication** pane.
5. Under **Authentication Settings**, make sure that you have the **Managed authentication with Choreo** toggle enabled.

    !!! tip
         Managed authentication is enabled by default when you create a web application using **React**, **Angular**, or **Vue.js** buildpacks. 

6. Specify values as follows for each of the fields:

    | **Field**           | **Value**  |
    |---------------------|------------|
    | **Post Login Path** | /          |
    | **Post Logout Path**| /          |
    | **Error Path**      | /          |

Next, you can create a user to access the web application.

To create a user to access the **readingListApp** application, follow the steps given below:

1. Under **Manage Users**, click **+ Create**.
2. To proceed with creating a user with the populated username and password, click **Create**. Make sure you copy the populated username and password to use when you test the front-end application.

    !!! tip
         - By default, your test user base consists of a demo user. For instructions on how to modify the test user base, see [Configure a User Store with the Built-In Identity Provider](../administer/configure-a-user-store-with-built-in-idp.md).

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
     | The Museum of Innocence   | Orhan Pamuk       | reading    |
     | The Remains of the Day    | Kazuo Ishiguro    | to_read    |
     | David Copperfield         | Charles Dickens   | read       |

    To add each record, follow the steps given below:

    1. Click **+ Add New**.
    2. Enter values for the **Name**, **Author**, and **Status** fields.
    3. Click **Save**.

    Three tabs open for each status. To delete a reading list item, you can click **Delete**.

To verify whether the reading list is personalized for each user, you can sign in as a different user. The reading list items you entered above will not appear for the other user.

Congratulations! You have successfully exposed a service endpoint via Choreo and securely consumed it from a web application.

After you have successfully tested your service and web application, you can now try out various other Choreo features such as [managing](../api-management/lifecycle-management.md), [observing](../monitoring-and-insights/observability-overview.md), [DevOps](../devops-and-ci-cd/view-runtime-details.md), etc., similar to any other component type within Choreo.
