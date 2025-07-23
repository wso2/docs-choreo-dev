# Develop a Ballerina Service

Choreo allows you to develop and deploy applications using your preferred programming language. This guide demonstrates how to deploy a service component that exposes a REST API using the [Ballerina language](https://ballerina.io/). No prior knowledge of Ballerina is required to follow this guide.

A REST API is a web service that adheres to Representational State Transfer (REST) principles, using HTTP methods to access and manage resources. This guide walks you through building a Ballerina service component, deploying it on Choreo, and testing it with an HTTP client application.

In this guide, you will:

 - Build a simple greeting service using a sample implementation. The service has a single resource named `greet` that accepts a query parameter as input.
 - Deploy the service in Choreo.
 - Test the service.

## Prerequisites

1. If you're signing in to the Choreo Console for the first time, create an organization:
    - Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in.
    - Enter a unique organization name. For example, `Stark Industries`.
    - Read and accept the privacy policy and terms of use.
    - Click **Create**. This creates the organization and opens the **Project Home** page.

2. Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample greetings service](https://github.com/wso2/choreo-samples/tree/main/greeting-service) implementation in Ballerina.

### Learn the repository file structure

It’s important to understand the purpose of key files in the sample service. The table below provides an overview of each file in the greeter service:

!!! note
    The file paths are relative to `<sample-repository-dir>/greeting-service`.

| **Filepath**              | **Description**                                                |
|---------------------------|---------------------------------------------------------------|
| `service.bal`             | The greetings service code written in Ballerina.               |
| `tests/service_test.bal`  | Test files related to the `service.bal` file.                  |
| `Ballerina.toml`          | The Ballerina configuration file.                              |

Let’s get started!

## Step 1: Create a service component

To create a Ballerina service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the **Project Home** page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Click **Authorize with GitHub** to connect your GitHub account. If you haven’t connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you forked in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires the following permissions:
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.
        
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if needed. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

5. Provide the following repository details:

    | **Field**              | **Value**          |
    |------------------------|--------------------|
    | **Organization**       | Your GitHub account|
    | **Repository**         | choreo-samples     |
    | **Branch**             | **`main`**         |
    |**Component Directory** | `/greeting-service` |

6. Select **Ballerina** as the buildpack.

7. Provide a display name, a unique name, and a description for the service component:

    !!! info
        The **Component Name** field specifies a unique identifier for the component. This name cannot be changed after creation.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Ballerina Greetings`   |
    | **Component Name**        | `ballerina-greetings`   |
    | **Description**           | Send greetings          |

8. Click **Create**. This creates the component and takes you to the **Overview** page.

You have successfully created a service that exposes a REST API written in Ballerina. Next, let’s build and deploy the service.

## Step 2: Build and deploy

Now that you’ve connected the source repository and configured the endpoint details, it’s time to build and deploy the service.

!!! note
    If you’re rebuilding the Ballerina service component after changing the Ballerina version, ensure that the version in the `Ballerina.toml` file matches the version in the `Dependencies.toml` file.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service may take a while. Track the progress via the logs in the **Build Details** pane. Once complete, the build status changes to **Success**.

### Step 2.2: Deploy

To deploy the service, follow these steps:

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure & Deploy**.
3. In the **Configurations** pane, click **Next** to skip the configuration.
4. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service may take a while. Once deployed, the **Development** environment card shows the **Deployment Status** as **Active**.

Once deployed, you can test, manage, and observe the service like any other component in Choreo.

For detailed instructions, see the following sections:
- [Step 3: Test](../../testing/test-rest-endpoints-via-the-openapi-console.md)
- [Step 4: Manage](../../api-management/lifecycle-management.md)

## Manage the deployment

To view Kubernetes-level insights for detailed diagnosis of this Ballerina REST API, see Choreo’s [DevOps capabilities](../../devops-and-ci-cd/view-runtime-details.md).
