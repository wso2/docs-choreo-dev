# Develop a Service

Choreo allows you to create and deploy applications in your preferred programming language. 

In this guide, you will:

- Build a simple greeting service using a sample service implementation. The sample implementation will have a single resource named `greet` that accepts a single query parameter as input.
- Deploy the service in Choreo using a `Go` buildpack. The service will run on port 9090.
- Test the service.

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample greetings service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go) implementation in `Go`.

### Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the greeter service:

!!! note 
    The specified file paths are relative to `<sample-repository-dir>/greeting-service-go`

|**Filepath**             |**Description**                                                               |
|-------------------------|------------------------------------------------------------------------------|
| `main.go`               | The Go-based greeter service code.                                           |
| `.choreo/endpoints.yaml`| Choreo-specific configuration that provides information about how Choreo exposes the service.|
| `openapi.yaml`          | The OpenAPI contract of the greeter service. This is required to publish the service as a managed API. This `openapi.yaml` file is referenced by the `.choreo/endpoints.yaml` file.|

Let's get started!

### Configure the service port with endpoints

In Choreo, you can expose your services via endpoints.

You are going to run the greeter service on port 9090. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

To configure the endpoint details of a containerized component, Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory. Be sure to place the `.choreo` directory at the root of the Docker build context path.

In the greeter sample, the `endpoints.yaml` file is in the `greeting-service-go/.choreo/` directory. 

## Step 1: Create a service component

To create a containerized service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**          |
    |---------------------------|--------------------|
    | **Component Display Name**| `Greetings`        |
    | **Component Name**        | `greetings`        |
    | **Description**           | Send greetings     |

5. Go to the **GitHub** tab.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Under **Connect Your Repository**, enter the following information:

    | **Field**              | **Value**          |
    |------------------------|--------------------|
    | **Organization**       | Your GitHub account|
    | **GitHub Repository**  | choreo-samples     |
    | **Branch**             | **`main`**         |

8. Select **Go** as the buildpack.
9. Enter the following information:
    
    | **Field**                | **Value**              |
    |--------------------------|------------------------|
    | **Go Project Directory** | `/greeting-service-go` |
    | **Language Version**     | 1.x                    |

10. Click **Create**. This creates the component and lists it under **Component Listing** on the project home page.

You have successfully created the service. The next step is to build and deploy it.

## Step 2: Build and deploy

Now that you have connected the source repository and configured the endpoint details, it's time to build and deploy the greeter service.

### Step 2.1: Build

To build the service, follow these steps:

1. On the project home page, click on the `Greetings` component you created. This takes you to the component overview page.
2. In the left navigation menu, click **Build**.
3. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 2.2: Deploy

To deploy the service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure &  Deploy**.
3. In the **Environment Configurations** pane that opens, click **Next** to skip the configuration.
4. In the **File Mount** pane, click **Next** to skip the configuration.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed the service, you can [test](../../testing/test-rest-endpoints-via-the-openapi-console.md), [manage](../../api-management/lifecycle-management.md), and [observe](../../monitoring-and-insights/observability-overview.md) it like any other component type in Choreo.
