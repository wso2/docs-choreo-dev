# Expose a WebSocket Endpoint via a Service

Choreo allows you to create and deploy applications in your preferred programming language, offering a seamless platform for building dynamic and scalable solutions. This guide walks you through the steps to deploy a WebSocket service using the NodeJS buildpack.

WebSockets provide real-time, two-way communication between a client and server over a persistent TCP connection. This makes WebSocket APIs ideal for applications that require real-time updates.

In this guide, you will:

- Build a simple WebSocket service using a sample implementation in NodeJS.
- Deploy the service in Choreo, allowing any WebSocket client application to establish a connection and exchange real-time messages with your service.

## Prerequisites

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample WebSocket service](https://github.com/wso2/choreo-samples/tree/main/websocket-chat-app) implementation for this guide.

Let's get started!

## Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the NodeJS chat Application.

!!! note
    The following file paths are relative to the path `<sample-repository-dir>/websocket-chat-app/websocket-chat-service-nodejs`.

|Filepath                        | Description                                                     |
|--------------------------------|-----------------------------------------------------------------|
| `server.js`                    | The NodeJS chat service.                                        |
| `.choreo/component.yaml`       | The configuration file with endpoint details.                   |

!!! note
    Choreo currently supports defining WebSocket APIs using the AsyncAPI 2.0 specification.

## Step 1: Create a service component 

To create a NodeJS service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

5. Under **Connect a Git Repository**, enter the following information:

    | **Field**               | **Value**               |
    |-------------------------|-------------------------|
    | **Organization**        | Your GitHub account     |
    | **GitHub Repository**   | **`choreo-samples`**    |
    | **Branch**              | **`main`**              |
    | **Component Directory**   | `websocket-chat-app/websocket-chat-service-nodejs`  |

6. Select **NodeJS** as the buildpack and **18.x.x** as the Language Version

8. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:

    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `NodeJS Chat Service`   |
    | **Component Name**        | `nodejs-chat-service`   |
    | **Description**           | Manage a chat service   |

9. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service component that provides a WebSocket API built with NodeJS. The next step is to proceed with building and deploying the service.

## Step 2: Build and deploy

Now that you have connected the source repository, it's time to build and deploy the chat service.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 2.2: Deploy

To deploy the service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure & Deploy**.
3. In the **Configurations** pane that opens, click **Next** to skip the configuration.
4. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed your service, you can [test](../../testing/test-websocket-endpoints-via-the-websocket-console.md), [manage](../../api-management/lifecycle-management.md), and observe it like any other component type in Choreo.

During testing, once the WebSocket connection is established, you can send {"type": "Connect", "username": "user1"} to the WebSocket endpoint to connect to the chat service. You can then send chat messages by using {"type": "Data", "message": "Hello, World!"}.

!!! note
     Some clients, such as certain browsers, may not support adding headers to the WebSocket handshake. In these cases, you can include the access token or test key required for WebSocket API invocation within the `sec-websocket-protocol` header, along with any specified subprotocols.

     For example: `sec-websocket-protocol: choreo-oauth2-token, {access token}, subprotocols`

     If you are using a test key, replace `choreo-oauth2-token` with `choreo-test-key`.
