# Develop a Service

Choreo allows you to create and deploy applications in your preferred programming language. 

In this guide, you will:

- Build a simple greeting service. The greeter service has a single resource named “greet” and accepts a single query parameter as input.
- Deploy it in Choreo using a `Go` buildpack. It runs on port 9090.
- Test the service.

## Prerequisites

- You will need a GitHub account with a repository that contains a Go implementation Fork the [Choreo examples repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.

### Learn the repository file structure

Let's familiarize ourselves with the key files in the sample greeter application. The below table gives a brief overview of the important files in the greeter service.

!!! note 
    The following file paths are relative to the path <sample-repository-dir>/go/greeter

|Filepath               |Description                                                                   |
|-----------------------|------------------------------------------------------------------------------|
| main.go               | The Go-based Greeter service code.
|.choreo/endpoints.yaml | Choreo-specific configuration that provides information about how Choreo exposes the service.|
|openapi.yaml           | OpenAPI contract of the greeter service. This is needed to publish our service as a managed API. This openapi.yaml file is referenced by the .choreo/endpoints.yaml.|

Let's get started!

### Configure the service port with endpoints

You are going to run the greeter service on port 9090. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. In Choreo, you expose services via endpoints. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

To configure the endpoint details of a containerized component, Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory. Be sure to place the `.choreo` directory at the root of the Docker build context path.

In the greeter sample, the `endpoints.yaml` file is in the `go/greeter/.choreo/` directory. 

## Step 1: Create a service component

Let's create a containerized service component by following these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. Create a project to add the service component. You can follow the instructions under Prerequisites in the Connect Your Own GitHub Repository to Choreo guide.
3. On the Components page, click on the Service card.
4. Enter a unique name and a description of the service. For this guide, let's enter the following values:

    |Field          |     Value              |
    |---------------|------------------------|
    |Name           | Greetings              |
    |Description    | Sends greetings        |

5. Go to the **GitHub** tab.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | choreo-sample-apps |
    | **Branch**            | **`main`**                               |
    | **Buildpack**      | Go|
    | **Select Go Project Directory**       | go/greeter |
    | **Select Language Version**              | 1.x |

8. Click Create. Once the component creation is complete, you will see the component overview page.

You have successfully created a Service component from a Dockerfile. Now let's build and deploy the service.

## Step 2: Build and deploy
Now that we have connected the source repository, and configured the endpoint details, it's time to build the service and create an image. Then we can deploy that image test the greeter service.

### Step 2.1: Build

To build the service, follow these steps:

1. On the **Build** page, click **Build**.
2. Select the latest commit and click **Build**.
3. Check the deployment progress by observing the console logs on the right of the page.


    !!! note
        Building the service component may take a while. You can track the progress by observing the logs. Once the build process is complete, the build status changes to **Success**.

You can access the following scans under **Build**. 

- **The Dockerfile scan**: Choreo performs a scan to check if a non-root user ID is assigned to the Docker container to ensure security. If no non-root user is specified, the build will fail.
- **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. 
-  **Container (Trivy) vulnerability scan**: The details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.

!!! info
    If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

### Step 2.2: Deploy

Next, to deploy this service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure &  Deploy**.
3. Skip configuring the **Environment Configurations** and click **Next**.
4. Skip adding a **File Mount**. Click **Deploy**.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. You can track the progress by observing the logs. Once the deploying is complete, the build status changes to **Active** on the **Development** environment card.

Once you have successfully deployed your service, you can [test](../../testing/test-rest-endpoints-via-the-openapi-console.md), [manage](../../api-management/lifecycle-management.md), and [observe](../../monitoring-and-insights/observability-overview.md) it like any other component type in Choreo.
