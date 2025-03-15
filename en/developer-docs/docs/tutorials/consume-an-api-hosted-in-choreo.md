# Consume an OAuth2 Secured Service

Choreo is a platform that allows you to create, deploy, and consume services seamlessly. The Choreo Developer Portal simplifies the process of discovering and using APIs for developers. 

This guide is designed for application developers (internal or external to your organization) who want to consume APIs published in the Developer Portal to build their applications. You will learn how to:

- Discover APIs
- Create an application and generate credentials
- Subscribe to an API
- Consume a published REST API via a web application

## Prerequisites

If you don’t already have a published service to consume, follow the [Develop a Service](../develop-components/develop-services/develop-a-service.md) documentation to publish and deploy a sample REST API.

## Discover APIs

In the Choreo Developer Portal, developers can search for APIs by name. APIs and services created and published through the Choreo Console are visible in the Developer Portal based on their visibility settings:

- **Public**: The API is visible to everyone in the Developer Portal.
- **Private**: The API is visible only to users who sign in to the Developer Portal.
- **Restricted**: The API is visible only to users with specific roles. This allows for fine-grained access control.

To learn more about API visibility, see [Control API Visibility](../api-management/control-api-visibility.md).

The Developer Portal lists APIs by their major version.

![Developer Portal APIs](../assets/img/consume/developer-portal-apis.png)

The API overview page displays subscribed versions of the API along with subscription details such as the application name and creation date.

![API overview](../assets/img/consume/api-overview.png)

!!! tip
    To use an API, it’s recommended to use the latest version. Copy the **Endpoint(s)** value from the API overview page and use it in your client application. This ensures your application always invokes the latest API version.

## Create an application

{% include "../consuming-services/create-an-application.md" %}

## Subscribe to an API

{% include "../consuming-services/create-a-subscription.md" %}

## Consume the API via your web application

To securely invoke the API/service, you need to use your Identity Provider (IdP). Follow these steps:

1. Create a web application in Choreo.
2. Create an OAuth application in the IdP.
3. Configure the web application to authenticate API/service invocations using the IdP.
4. Deploy the web application.

For this guide, you’ll use:
- **WSO2 Asgardeo** as the IdP.
- **[choreo-samples/reading-list-app/reading-list-front-end](https://github.com/wso2/choreo-samples/tree/main/reading-list-app/reading-list-front-end)** as the web application. This is a React SPA that uses Axios to invoke the service. It is configured to work with the **[choreo-samples/reading-list-app/reading-list-service](https://github.com/wso2/choreo-samples/tree/main/reading-list-app/reading-list-service)**. You can modify this web application to work with your service or deploy the sample service in Choreo.

### Step 1: Create a web application component

!!! info
    You can use your own web application instead of the sample. For this guide, you’ll use the [choreo-samples/reading-list-app/reading-list-front-end](https://github.com/wso2/choreo-samples/tree/main/reading-list-app/reading-list-front-end).

To host the front-end application in Choreo, create a web application component:

1. In the Choreo Console, select the project for the reading list application from the project list in the header.
2. Click **Create** under the **Component Listing** section.
3. On the **Web Application** card, click **Create**.
4. Enter the following details:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Reading List Web App`  |
    | **Description** | `Frontend application for the reading list service` |

5. Click **Next**.
6. Click **Authorize with GitHub** to connect Choreo to your GitHub account.
7. In the **Connect Repository** pane, enter the following:

    | **Field**             | **Value**                               |
    |-----------------------|-----------------------------------------|
    | **GitHub Account**    | Your account                            |
    | **GitHub Repository** | **`choreo-samples`**                    |
    | **Branch**            | **`main`**                              |
    | **Buildpack**         | **React** (since it’s a React app built with Vite) |
    | **Build Context Path**| **`reading-list-app/reading-list-front-end`** |
    | **Build Command**     | **`npm install && npm run build`**      |
    | **Build Output**      | **`dist`**                              |
    | **Node Version**      | **`18`**                                |

8. Click **Create**. This initializes the service with the GitHub repository and takes you to the **Overview** page.

### Step 2: Create an OAuth application in the IdP

To invoke the API/service, you need an access token. Create an OAuth application in the IdP (for example, Asgardeo) with the following settings:

- **Allowed grant types**: Code
- **Public client**: Enable this option.
- **Authorized redirect URLs**: Add the web app URL.
- **Allowed origins**: Add the same URLs as authorized redirect URLs.
- **Access Token**: Set to JWT.

Choreo uses Asgardeo as the default IdP. When you create an application in the Choreo Developer Portal, it automatically creates a corresponding application in Asgardeo. Follow these steps to configure the Asgardeo OAuth application:

1. Sign in to [Asgardeo](https://console.asgardeo.io/) using the same credentials as Choreo.
2. Ensure you’re in the same organization used in the Choreo Developer Portal.
3. In the Asgardeo Console, click **Applications** in the left navigation. You’ll see the **readingListApp** created by Choreo.
4. Click the edit icon to edit the application.
5. Go to the **Protocol** tab and make the following changes:
    1. Under **Allowed grant types**, select **Code**.
    2. Select the **Public client** checkbox.
    3. In **Authorized redirect URLs**, enter the web app URL and click **+** to add it.
    4. In **Allowed origins**, add the same URLs.
    5. Under **Access Token**, select **JWT** as the token type.
    6. Click **Update**.

### Step 3: Configure the web application to connect to the IdP and invoke the service

Update the web app configurations to invoke the **Reading List Service** REST API. These configurations are environment-specific. For this guide, we’ll configure the development environment.

!!! note
    The web app reads environment-specific configurations from the `window` object at runtime via the `config.js` file. You’ll mount this file for the development environment. Repeat this process for other environments as needed.

To configure the front-end application:

1. On the web application component page, click **DevOps** in the left menu, then click **Configs and Secrets**.
2. Click **+ Create**.
3. Select the following options and click **Next**:

    | **Field**             | **Value**                               |
    |-----------------------|-----------------------------------------|
    | **Config Type**       | **Config Map**                          |
    | **Mount Type**        | **File Mount**                          |

4. Specify the following values:

    | **Field**             | **Value**                               |
    |-----------------------|-----------------------------------------|
    | **Config Name**       | **Web App Config**                      |
    | **Mount Path**        | **/usr/share/nginx/html/config.js**     |

5. Copy the following JSON configuration into the text area. Replace the placeholders with the values from earlier steps:

    ```javascript
    window.config = {
        redirectUrl: "<web-app-url>",
        asgardeoClientId: "<asgardeo-client-id>",
        asgardeoBaseUrl: "https://api.asgardeo.io/t/<your-org-name>",
        choreoApiUrl: "<reading-list-service-url>"
    };
    ```

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **redirectUrl**       | The web app URL you copied earlier.           |
    | **asgardeoClientId**  | The **Client ID** from the Asgardeo application. |
    | **asgardeoBaseUrl**   | The IdP API URL with your organization name (e.g., `https://api.asgardeo.io/t/<ORG_NAME>`). |
    | **choreoApiUrl**      | The **Reading List Service** URL from the endpoint table in the overview page. |

6. Click **Create**.

### Step 4: Deploy the web application

To deploy the web application:

1. In the left menu, click **Deploy**.
2. In the **Build Area** card, click **Deploy Manually**. Deployment may take a few minutes.
3. Once deployed, copy the **Web App URL** from the development environment card.
4. Navigate to the web app URL to verify it’s successfully hosted.

That’s it! You can now use a user from your IdP to sign in and invoke the service through your web application.
