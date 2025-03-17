# Deploy a Web Application that Consumes a Backend Service

Choreo is an Internal Developer Platform (IDevP) that simplifies building, deploying, monitoring, and managing cloud-native applications.

In this guide, you will learn how to:

- Expose a service endpoint via Choreo.
- Securely consume the service from a web application.
- Use Choreo's managed authentication to set up authentication for your web application without dealing with complex security protocols.

The sample web application allows users to:
- Sign in and view their reading lists.
- Add books to a reading list.
- Delete books from the reading list.
- Sign out of the application.

This guide walks you through the following steps:

1. Deploy and test a service component.
2. Create a web application to consume the service.
3. Create a connection to the deployed service.
4. Enable managed authentication and deploy the web application.
5. Consume the deployed service via the web application.

## Prerequisites

1. GitHub account: Fork the [Choreo sample book list app repository](https://github.com/wso2/choreo-sample-book-list-app), which contains the sample for this guide.

2. If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

## Step 1: Create a project

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter the following details:

    !!! tip
        The **Name** field must be unique and cannot be changed after creation.

    | **Field**                | **Value**             |
    |--------------------------|-----------------------|
    | **Project Display Name** | Sample project        |
    | **Name**                 | sample-project        |
    | **Project Description**  | My sample project     |

4. Click **Create**. This creates the project and opens the project home page.

## Step 2: Connect your sample repository and configure the service

1. On the project home page, click **Start** under **Create Multiple Components**.
2. Go to the **GitHub** tab.
3. Click **Authorize with GitHub** to connect Choreo to your GitHub account. If you haven't connected your GitHub repository to Choreo, enter your credentials and select the repository you forked earlier to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, paste the [Choreo sample book list app repository](https://github.com/wso2/choreo-sample-book-list-app) URL in the **Provide Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires:
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

4. Enter the following information:

    | **Field**                    | **Value**                   |
    |------------------------------|-----------------------------|
    | **Organization**             | Your GitHub account         |
    | **Repository**               | choreo-sample-book-list-app |
    | **Branch**                   | main                        |

5. In the **Add Component Directories** pane under **Configure Components**, click the **+** icon next to `reading-list-service`.
6. In the **Component Configuration** dialog, specify the following:

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

1. On the project home page, click the `Reading List Service` component under **Component Listing**. This takes you to the component overview page.
2. In the left navigation menu, click **Build**.
3. In the **Builds** pane, click **Build Latest**.

    !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.

## Step 4: Deploy the service

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane, click **Next** to skip the configuration.
4. In the **File Mount** pane, click **Next** to skip the configuration.
5. In the **Endpoint Details** pane, verify that **Network Visibility** is set to **Public**. This securely exposes the endpoint for consumption.
6. Click **Deploy**. This deploys the service to the development environment and lists it in the [Choreo Marketplace](../choreo-concepts/choreo-marketplace.md).

## Step 5: Test the service

1. In the Choreo Console left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console, select **Development** from the environment drop-down.
3. In the **Endpoint** list, select **Books REST Endpoint**.
4. Expand the **GET/books** method, click **Try it out**, then click **Execute**.
5. Check the **Server Response** section. You will see an empty response. You can add an entry using the POST method and retry the **GET/books** method.
6. Expand the **POST/books** method and click **Try it out**.
7. Update the request body with the following values:

    | **Parameter** | **Value**     |
    |---------------|---------------|
    | **author**    | Bram Stoker   |
    | **status**    | to_read       |
    | **title**     | Dracula       |

    The request body should look like this:

    ```json
    {
      "author": "Bram Stoker",
      "status": "to_read",
      "title": "Dracula"
    }
    ```

8. Click **Execute**.

    Check the **Server Response** section. On successful invocation, you will receive the `201` HTTP code.

You can also try out the **GET** and **DELETE** methods.

## Step 6: Consume the service

Now that the `Reading List Service` is deployed and available in the Choreo Marketplace, you can discover and consume it. In this section, you will deploy a front-end application to interact with the service.

### Step 6.1: Create a web application to consume the service

1. In the Choreo Console header, click the **Project** list and select the project you created in Step 1.
2. On the project home page, click **+ Create** under **Component Listing**.
3. Click the **Web Application** card.
4. Click **Authorize with GitHub** to connect Choreo to your GitHub account. If you haven't connected your GitHub repository to Choreo, enter your credentials and select the repository you forked earlier to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo sample Book List Service repository](https://github.com/wso2/choreo-sample-book-list-service) URL in the **Provide Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires:

        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

5. Enter the following information:

    | **Field**             | **Description**                  |
    |-----------------------|----------------------------------|
    | **Organization**      | Your GitHub account              |
    | **Repository**        | choreo-sample-book-list-app      |
    | **Branch**            | main                             |
    | **Component Directory** | /choreo-sample-book-list-app/reading-list-front-end-with-managed-auth |

6. Select **React** as the buildpack because the sample front-end application is a React application built with Vite.
7. Enter the following details:

    | **Field**             | **Value**               |
    |-----------------------|-------------------------|
    | **Build Command**     | npm install && npm run build         |
    | **Build Path**        | dist                                 |
    | **Node Version**      | 18                                   |

8. Enter the following details:

    !!! info
        The **Component Name** field must be unique and cannot be changed after creation.

    | **Field**                 | **Value**                                      |
    |---------------------------|------------------------------------------------|
    | **Component Display Name**| Reading List Web App                           |
    | **Component Name**        | reading-list-web-app                           |
    | **Description**           | Front-end application for the reading list service |


9. Click **Create**. This initializes the component with the implementation from your GitHub repository and takes you to the **Overview** page of the component.

### Step 6.2: Create a connection between the web application and the deployed service

A connection allows you to integrate the service with other services or external resources. For more information, refer to the [Connection](../choreo-concepts/connections.md) documentation.

1. In the left navigation menu, click **Dependencies** and then click **Connections**.
2. Click **+ Create**.
3. In the **Select a Service** pane, click `Reading List Service`.
4. Specify the following:

    | **Field**        | **Value**                     |
    |------------------|-------------------------------|
    | **Name**         | Reading List Connection       |
    | **Description**  | Connection to the reading list|

5. Click **Create**. This creates the connection and displays the service URL for the Development environment. Copy the service URL for later use.

### Step 6.3: Build the web application component

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build Latest**.

   !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.

### Step 6.4: Configure and deploy the web application

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure and Deploy**.
3. In the `config.js` file mount, replace `<Service URL>` with the value you copied when creating the connection to the `Reading List Service` in [Step 6.2](#step-62-create-a-connection-between-the-web-application-and-the-deployed-service).

    ```javascript
    window.configs = {
        apiUrl: '<Service URL>',
    };
    ```

    !!! tip
        You can refer to the configuration file mounted at `/app/public` as `./public/config.js` within your web application.

4. Click **Next** to open the **Authentication** pane.
5. Under **Authentication Settings**, ensure that **Managed authentication with Choreo** is enabled.

    !!! tip
        Managed authentication is enabled by default for **React**, **Angular**, or **Vue.js** buildpacks.

6. Specify the following:

    | **Field**           | **Value**  |
    |---------------------|------------|
    | **Post Login Path** | /          |
    | **Post Logout Path**| /          |
    | **Error Path**      | /          |

7. Under **Manage Users**, click **+ Create**.
8. Click **Create** to proceed with the default username and password. Copy the credentials for testing.

    !!! tip
        By default, your test user base consists of a demo user. For instructions on modifying the test user base, see [Configure a User Store with the Built-In Identity Provider](../administer/configure-a-user-store-with-built-in-idp.md).

9. In the **Authentication** pane, click **Deploy**. The deployment may take a few minutes.
10. Once deployed, copy the **Web App URL** from the development environment card.
11. Navigate to the web app URL to verify that the web application is hosted successfully.

## Step 7: Test the front-end application

1. Access the front-end application via its web URL.
2. Click **Login** and sign in with the credentials you created.

    The application opens as follows:

    ![Front-end application](../assets/img/quick-start-guides/front-end-application.png){.cInlineImage-half}

3. Add three new reading items with different statuses. For example:

    | **Title**                 | **Author**        | **Status** |
    |---------------------------|-------------------|------------|
    | The Museum of Innocence   | Orhan Pamuk       | reading    |
    | The Remains of the Day    | Kazuo Ishiguro    | to_read    |
    | David Copperfield         | Charles Dickens   | read       |

    To add each record:
    1. Click **+ Add New**.
    2. Enter values for the **Name**, **Author**, and **Status** fields.
    3. Click **Save**.

    To delete a reading list item, click **Delete**.

To verify that the reading list is personalized for each user, sign in as a different user. The reading list items you entered will not appear for the other user.

Congratulations! You have successfully exposed a service endpoint via Choreo and securely consumed it from a web application.

After testing your service and web application, explore other Choreo features like [managing](../api-management/lifecycle-management.md), [observing](../monitoring-and-insights/observability-overview.md), and [DevOps](../devops-and-ci-cd/view-runtime-details.md).
