# Develop a Service

Choreo allows you to create and deploy applications in your preferred programming language. 

In this guide, you will:

- Build a simple greeting service using a sample service implementation. The sample implementation will have a single resource named `greet` that accepts a single query parameter as input.
- Deploy the service in Choreo using a `Go` buildpack. The service will run on port 9090.
- Test the service.

## Prerequisites

1. If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

2. Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample greetings service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go) implementation in `Go`.

### Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the greeter service:

!!! note 
    The specified file paths are relative to `<sample-repository-dir>/greeting-service-go`.

| **Filepath**             | **Description**                                                               |
|--------------------------|-------------------------------------------------------------------------------|
| `main.go`                | The Go-based greeter service code.                                            |
| `.choreo/component.yaml` | Choreo-specific configuration that provides information about how Choreo exposes the service. |
| `openapi.yaml`           | The OpenAPI contract of the greeter service. This is required to publish the service as a managed API. This `openapi.yaml` file is referenced by the `.choreo/component.yaml` file. |

### Configure the service port with endpoints

In Choreo, you can expose your services via endpoints. To run the greeter service on port 9090 and securely expose it through Choreo, you must provide the port and other required information in the `component.yaml` file. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

In the greeter sample, the `component.yaml` file is located in the `greeting-service-go/.choreo/` directory.

## Step 1: Create a service component

To create a containerized service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Click **Authorize with GitHub** to connect Choreo to your GitHub account. If you haven't connected your GitHub repository to Choreo, enter your credentials and select the repository you forked earlier to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Public Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires:
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

5. Under **Connect a Git Repository**, enter the following details:

    | **Field**              | **Value**          |
    |------------------------|--------------------|
    | **Organization**       | Your GitHub account|
    | **GitHub Repository**  | choreo-samples     |
    | **Branch**             | main               |
    | **Component Directory** | `/greeting-service-go` |

6. Select **Go** as the buildpack.
7. Enter the following details:

    | **Field**                | **Value**              |
    |--------------------------|------------------------|
    | **Language Version**     | 1.x                    |

8. Provide component display name, name and description:

    !!! info
        The **Component Name** field must be unique and cannot be changed after creation.

    | **Field**                 | **Value**          |
    |---------------------------|--------------------|
    | **Component Display Name**| Greetings          |
    | **Component Name**        | greetings          |
    | **Description**           | Send greetings     |

9. Click **Create**. This creates the component and lists it under **Component Listing** on the project home page.

You have successfully created the service. The next step is to build and deploy it.

## Step 2: Build and deploy

Now that you have connected the source repository and configured the endpoint details, it's time to build and deploy the greeter service.

### Step 2.1: Build

1. On the project home page, click on the `Greetings` component you created. This takes you to the component overview page.
2. In the left navigation menu, click **Build**.
3. On the **Build** page, click **Build Latest**.

    !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.

### Step 2.2: Deploy

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane, click **Next** to skip the configuration.
4. In the **File Mount** pane, click **Next** to skip the configuration.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take some time. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed the service, you can [test](../../testing/test-rest-endpoints-via-the-openapi-console.md), [manage](../../api-management/lifecycle-management.md), and [observe](../../monitoring-and-insights/observability-overview.md) it like any other component type in Choreo.
