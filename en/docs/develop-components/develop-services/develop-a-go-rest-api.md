# Develop a Go REST API

Choreo allows you to create and deploy applications in your preferred programming language. Here, we are going to deploy the same rest api in [Develop a REST API ](develop-a-rest-api.md) guide without a Dockerfile.

In this guide, you will:

- Build a simple greeting service. The greeter service has a single resource named “greet” and accepts a single query parameter as input.
- Deploy it in Choreo using a `Go` buildpack. It runs on port 9090.
- Test the service.

## Prerequisites

1. You will need a GitHub account with a repository that contains a Go implementation Fork the [Choreo examples repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
    - Read access to issues and metadata
    - Read and write access to code, pull requests, and repository hooks.

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

We expect to run our greeter service on port 9090. To securely expose the service through Choreo, we must provide the port and other required information to Choreo. In Choreo, we expose our services with endpoints. You can read more about endpoints in our [endpoint documentation](https://wso2.com/choreo/docs/develop-components/develop-services/develop-a-service/#what-are-endpoints-in-service-components).

Choreo looks for an endpoints.yaml file inside the `.choreo` directory to configure the endpoint details of a containerized component. Place the `.choreo` directory at the root of the Docker build context path.

In our greeter sample, the endpoints.yaml file is at go/greeter/.choreo/endpoints.yaml. 

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

5. Select **GitHub** Tab
6. If you have not already connected your GitHub repository to Choreo, to allow Choreo to connect to your GitHub account, click **Authorize with GitHub** and enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


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
