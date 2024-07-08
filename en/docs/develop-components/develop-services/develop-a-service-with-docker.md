# Develop a Service with Docker

Choreo is a platform that allows you to create and deploy applications in any language. 

In this guide, you will:

- create a containerized service component using a sample service implementation. The sample implementation will have a single resource named `greet` that accepts a single query parameter as input.
- Deploy it in Choreo using a Dockerfile. The service will run on port 9090.
- Test the service.

If you want to create a service component that exposes a Ballerina service, see [Develop a Ballerina Service ](develop-a-ballerina-service.md).

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample greetings service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go) implementation with the Dockerfile.


### Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the greeter service:

!!! note 
    The specified file paths are relative to `<sample-repository-dir>/greeting-service-go`

|**Filepath**             |**Description**                                                               |
|-------------------------|------------------------------------------------------------------------------|
| `main.go`               | The Go-based greeter service code.                                           |
| `Dockerfile`            | The Dockerfile to build the container image of the application.              |
| `.choreo/endpoints.yaml`| Choreo-specific configuration that provides information about how Choreo exposes the service.|
| `openapi.yaml`          | The OpenAPI contract of the greeter service. This is required to publish the service as a managed API. This `openapi.yaml` file is referenced by the `.choreo/endpoints.yaml` file.|

Let's get started!

### Configure the service port with endpoints

In Choreo, you can expose your services via endpoints.

You are going to run the greeter service on port 9090. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

To configure the endpoint details of a containerized component, Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory. Be sure to place the `.choreo` directory at the root of the Docker build context path.

In the greeter sample, the `endpoints.yaml` file is in the `greeting-service-go/.choreo/` directory. 

## Step 1: Create a service component from a Dockerfile

To create a containerized service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
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
    | **Repository**         | choreo-samples     |
    | **Branch**             | **`main`**         |

8. Select **Docker** as the buildpack.
9. Enter the following information:
    
    | **Field**                | **Value**                        |
    |--------------------------|----------------------------------|
    | **Docker Context**       | `/greeting-service-go`           |
    | **Dockerfile**           | `/greeting-service-go/Dockerfile`|

    !!! info
        1.  To successfully build your container with Choreo, it is essential to explicitly define a user ID (UID) under the USER instruction in your Dockerfile. For reference, see [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/greeter/Dockerfile).
        To ensure that the defined USER instruction is valid, it must conform to the following conditions:
            - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
            - Usernames are considered invalid and should not be used. For example, `my-custom-user-12221` or `my-custom-user` are invalid User IDs.

        2. The Dockerfile used in this guide is a multi-stage Dockerfile, which is designed to keep the final image size small and provides the ability to build the application with a specific version of tools and libraries.

10. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service from a Dockerfile. Next, you can build and deploy the service.

## Step 2: Build and deploy

Now that we have connected the source repository, and configured the endpoint details, it's time to build the service and create an image. Then we can deploy that image test the greeter service.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

    You can access the following scans under **Build**. 

      - **The Dockerfile scan**: Choreo performs a scan to check if a non-root user ID is assigned to the Docker container to ensure security. If no non-root user is specified, the build will fail.
      - **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. 
      - **Container (Trivy) vulnerability scan**: The details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.
     
        !!! info
            If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

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

To perform a more detailed diagnosis of this Dockerfile-based REST API by viewing Kubernetes-level insights, see [Choreo's DevOps capabilities](../../devops-and-ci-cd/view-runtime-details.md).
