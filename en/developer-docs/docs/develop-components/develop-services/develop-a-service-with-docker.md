# Develop a Service with Docker

Choreo allows you to create and deploy applications in any language. This guide demonstrates how to develop and deploy a containerized service component using a Dockerfile.

In this guide, you will:

 - Create a containerized service component using a sample service implementation. The sample service has a single resource named `greet` that accepts a query parameter as input.
 - Deploy the service in Choreo using a Dockerfile. The service will run on port 9090.
 - Test the service.

If you want to create a service component that exposes a Ballerina service, see [Develop a Ballerina Service](develop-a-ballerina-service.md).

## Prerequisites

1. If you're signing in to the Choreo Console for the first time, create an organization:
    - Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in.
    - Enter a unique organization name. For example, `Stark Industries`.
    - Read and accept the privacy policy and terms of use.
    - Click **Create**. This creates the organization and opens the **Project Home** page.

2. Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample greetings service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go) implementation with the Dockerfile.

### Learn the repository file structure

It’s important to understand the purpose of key files in the sample service. The table below provides an overview of each file in the greeter service:

!!! note
    The file paths are relative to `<sample-repository-dir>/greeting-service-go`.

| **Filepath**               | **Description**                                                              |
|----------------------------|------------------------------------------------------------------------------|
| `main.go`                  | The Go-based greeter service code.                                           |
| `Dockerfile`               | The Dockerfile to build the container image of the application.              |
| `.choreo/component.yaml`   | Choreo-specific configuration that provides information about how Choreo exposes the service. |
| `openapi.yaml`             | The OpenAPI contract of the greeter service. This is required to publish the service as a managed API. This file is referenced by the `.choreo/component.yaml` file. |

### Configure the service port with endpoints

In Choreo, you can expose your services via endpoints. The greeter service runs on port 9090. To securely expose the service through Choreo, you must provide the port and other required information in the `component.yaml` file. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

In the greeter sample, the `component.yaml` file is located in the `greeting-service-go/.choreo/` directory.

## Step 1: Create a service component from a Dockerfile

To create a containerized service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the **Project Home** page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Click **Authorize with GitHub** to connect your GitHub account. If you haven’t connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you forked in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires the following permissions:
         - Read and write access to code and pull requests.
         - Read access to issues and metadata.
        
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if needed. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

5. Enter the following repository details:

    | **Field**              | **Value**          |
    |------------------------|--------------------|
    | **Organization**       | Your GitHub account|
    | **Repository**         | choreo-samples     |
    | **Branch**             | `main`         |
    | **Component Directory** | `/greeting-service-go`         |

6. Select **Docker** as the buildpack.
7. Enter the following details:

    | **Field**                | **Value**                        |
    |--------------------------|----------------------------------|
    | **Dockerfile**           | `/greeting-service-go/Dockerfile`|

    !!! info
        1. To successfully build your container with Choreo, explicitly define a user ID (UID) under the `USER` instruction in your Dockerfile. For reference, see the [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/greeter/Dockerfile).
           - A valid User ID is a numeric value between 10000-20000 (e.g., `10001` or `10500`).
           - Usernames are invalid and should not be used (e.g., `my-custom-user-12221` or `my-custom-user`).

        2. The Dockerfile used in this guide is a multi-stage Dockerfile, which keeps the final image size small and allows building the application with specific versions of tools and libraries.

8. Provide a display name, a unique name, and a description for the service component:

    !!! info
        The **Component Name** field specifies a unique identifier for the component. This name cannot be changed after creation.

    | **Field**                 | **Value**          |
    |---------------------------|--------------------|
    | **Component Display Name**| `Greetings`        |
    | **Component Name**        | `greetings`        |
    | **Description**           | Send greetings     |

9. Click **Create**. This creates the component and takes you to the **Overview** page.

You have successfully created a service from a Dockerfile. Next, you can build and deploy the service.

## Step 2: Build and deploy

Now that you’ve connected the source repository and configured the endpoint details, it’s time to build the service and create an image. Then, you can deploy the image and test the greeter service.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service may take a while. Track the progress via the logs in the **Build Details** pane. Once complete, the build status changes to **Success**.

    You can access the following scans under **Build**:
    - **Dockerfile Scan**: Choreo checks if a non-root user ID is assigned to the Docker container for security. If no non-root user is specified, the build will fail.
    - **Container (Trivy) Vulnerability Scan**: This detects vulnerabilities in the final Docker image. If critical vulnerabilities are found, the build will fail.

    !!! info
        If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

### Step 2.2: Deploy

To deploy the service, follow these steps:

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane, click **Next** to skip the configuration.
4. In the **File Mount** pane, click **Next** to skip the configuration.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service may take a while. Once deployed, the **Development** environment card shows the **Deployment Status** as **Active**.

Once deployed, you can [test](../../testing/test-rest-endpoints-via-the-openapi-console.md), [manage](../../api-management/lifecycle-management.md), and [observe](../../monitoring-and-insights/observability-overview.md) the service like any other component in Choreo.

To view Kubernetes-level insights for detailed diagnosis of this Dockerfile-based REST API, see [Choreo's DevOps capabilities](../../devops-and-ci-cd/view-runtime-details.md).
