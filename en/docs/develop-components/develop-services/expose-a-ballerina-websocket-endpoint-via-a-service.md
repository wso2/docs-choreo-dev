# Expose a Ballerina WebSocket Endpoint via a Service

Choreo allows you to create and deploy applications in your preferred programming language, offering a seamless platform for building dynamic and scalable solutions. This guide walks you through the steps to deploy a WebSocket service using the Ballerina language. No prior knowledge of the Ballerina language is required to follow this guide.

WebSockets provide real-time, two-way communication between a client and server over a persistent TCP connection. This makes WebSocket APIs ideal for applications that require real time updates.

In this guide, you will:

- Build a simple WebSocket service using a sample implementation in Ballerina.
- Deploy the servicen in Choreo, allowing any WebSocket client application to establish a connection and exchange real-time messagess with your service.

## Prerequisites

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample WebSocket service](https://github.com/wso2/choreo-samples/tree/main/chat-service-websocket) implementation for this guide.

Let's get started!

## Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the Ballerina chat service.

!!! note
    The following file paths are relative to the path `<sample-repository-dir>/chat-service-websocket`.

|Filepath                 | Description                                                     |
|-------------------------|-----------------------------------------------------------------|
| `service.bal`           | The chat service code written in the Ballerina language.        |
| `Ballerina.toml`        | The Ballerina configuration file.                               |
| `.choreo/endpoints.yaml`| The configuration file with endpoint details.                   |

## Step 1: Create a service component 

To create a Ballerina service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Ballerina Chat Service`|
    | **Component Name**        | `ballerina-chat-service`|
    | **Description**           | Manage a Chat service   |
    
5. Click the **GitHub** tab.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Under **Connect Your Repository**, enter the following information:

    | **Field**               | **Value**               |
    |-------------------------|-------------------------|
    | **Organization**        | Your GitHub account     |
    | **GitHub Repository**   | **`choreo-samples`**    |
    | **Branch**              | **`main`**              |

8. Select **Ballerina** as the buildpack.
9. Enter the following information:
    
    | **Field**                      | **Value**                 |
    |--------------------------------|---------------------------|
    | **Ballerina Project Directory**| `chat-service-websocket`  |

10. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service component that exposes a GraphQL API written in the Ballerina language. Next, let's build and deploy the service.

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
2. On the **Set Up** card, click **Configure &  Deploy**.
3. In the **Configurations** pane that opens, click **Next** to skip the configuration.
4. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed your service, you can [test](../../testing/test-websocket-endpoints-via-the-websocket-console.md), [manage](../../api-management/lifecycle-management.md), and observe it like any other component type in Choreo.
