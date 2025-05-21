# Expose a gRPC Endpoint via a Service

Choreo allows you to create and deploy applications in your preferred programming language. One powerful option is gRPC, a high-performance and language-agnostic remote procedure call (RPC) framework. It utilizes [Protocol Buffers](https://github.com/protocolbuffers/protobuf) to define services and generate client/server code.

In this guide, you will learn to use Choreo to create a service component that exposes a gRPC server implemented in [Go](https://go.dev/), enabling efficient and scalable communication with any gRPC client application. No prior knowledge of the Go language is necessary to follow this guide.

By following this guide, you will:

- Create a simple gRPC server using a Service component.
- Link the containerized gRPC service to the Choreo component using the Dockerfile. 
    The greeter service has a single RPC method named `sayHello`. Following is the proto file the server and the client application are using:

    ```proto
    syntax = "proto3";
    option go_package = "github.com/wso2/choreo-samples/go-grpc/pkg";

    service greeter {
      rpc SayHello (HelloRequest) returns (HelloReply) {}
    }

    message HelloRequest {
      string name = 1;
    }

    message HelloReply {
      string message = 1;
    }
    ```

- Deploy the gRPC server component in Choreo. 
- Create a gRPC client using a Manual Trigger component. 
- Deploy the gRPC client.
- Invoke the gRPC server using a gRPC client and check the response through the Choreo log view.


## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample service](https://github.com/wso2/choreo-samples/tree/main/go-grpc) implementation for this guide.

Let's get started!

### Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the greeter service.

!!! note 
    The following file paths are relative to the path `<sample-repository-dir>/go-grpc`.

|File Path                |Description                                                                                   |
|-------------------------|----------------------------------------------------------------------------------------------|
| `main.go`               | The greeter service code written in the Go language.                                         |
| `greeter_client/main.go`| The greeter client application written in the Go language.                                   |
| `Dockerfile.server`     | The Dockerfile to build the container image of the server application.                       |
| `Dockerfile.client`     | The Dockerfile to build the container image of the client application.                       |
| `.choreo/component.yaml`| The Choreo-specific configuration that provides information about how Choreo exposes the service.|
| `pkg/greeter.proto`     | The interface definition of the gRPC service. This is used to generate the server and client stubs for the Go application.|

### Configure the service port with endpoints

In Choreo, you can expose your services via endpoints.

Let's run the gRPC server service component on port 8080. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

Choreo looks for an `component.yaml` file inside the `.choreo` directory to configure the endpoint details of the containerized component. Ensure the `.choreo` directory is at the root of the Docker build context path.

In the gRPC server sample, the `component.yaml` file is at `go-grpc/.choreo/component.yaml`, where the build context path is `go-grpc`.

## Step 1: Create a service component with a gRPC endpoint

To create a containerized service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Under **Connect a Git Repository**, enter the following information:

    | **Field**               | **Value**               |
    |-------------------------|-------------------------|
    | **Organization**        | Your GitHub account     |
    | **GitHub Repository**   | **`choreo-samples`**    |
    | **Branch**              | **`main`**              |
    | **Component Directory** | `go-grpc`                  |

8. Select **Docker** as the buildpack.
9. Enter the following information:
    
    | **Field**                | **Value**                  |
    |--------------------------|----------------------------|
    | **Dockerfile**           | `go-grpc/Dockerfile.server`|

    !!! info
        1.  To successfully build your container with Choreo, it is essential to explicitly define a User ID (UID) under the USER instruction in your Dockerfile. For reference, see the [sample Dockerfile](https://github.com/wso2/choreo-samples/blob/main/go-grpc/Dockerfile.server).
        To ensure that the defined USER instruction is valid, it must conform to the following conditions:
            - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
            - Usernames are considered invalid and should not be used. For example, `my-custom-user-12221` or `my-custom-user` are invalid User IDs.

        2. The Dockerfile utilized in this guide is a multi-stage Dockerfile, which is designed to keep the final image size small and provides the ability to build the application with a specific version of tools and libraries.

4. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Go gRPC Server`        |
    | **Component Name**        | `go-grpc-server`        |
    | **Description**           | Send greetings          |
    
10. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service from a Dockerfile. Next, you can build and deploy the service.

## Step 2: Build and deploy

Now that you have connected the source repository, and configured the endpoint details, it's time to build the service and create an image. Then you can deploy the image and test the greeter service.

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

6. Once you have successfully deployed your service, navigate to the component overview page and copy the gRPC service URL. You need to provide that URL when setting up the client application later in this guide.

You have successfully deployed the gRPC server. Currently, the gRPC service is only accessible by the components deployed within the same project.

## Step 3: Invoke the gRPC service

Let's invoke the gRPC service that you created above, using a gRPC client. 

### Step 3.1: Create a manual task for the gRPC client

To create a manual task component, follow these steps:

1. On the Choreo Console header, click the **Project** list and select the project where you created the gRPC service component. This opens the project home page.
2. Go to the **Component Listing** section and click **+ Create**.
3. Click the **Manual Task** card.
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
    | **Component Directory**       | `go/grpc`                  |

6. Select **Docker** as the buildpack.
7. Enter the following information:
    
    | **Field**                | **Value**                  |
    |--------------------------|----------------------------|
    | **Dockerfile**           | `go/grpc/Dockerfile.client`|

8. Enter a display name, a unique name, and a description for the component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Go gRPC Client`        |
    | **Component Name**        | `go-grpc-client`        |
    | **Description**           | `Receive greetings`     |


9. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

### Step 3.2: Build the gRPC client component

To build the component, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 3.3: Setup environment variables and deploy the gRPC client

The client application, in this case, the gRPC client, requires the server URL of the gRPC server service. This is read from the client application as an environment variable. Follow the steps below to configure the environment variable for the client application:

1. On the Choreo Console left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane that opens, click **+ Add**.
4. Specify the following name and value to configure the environment variable for the client application:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `GREETER_SERVICE`       |
    | **Value**       | Paste the URL value copied after deploying the gRPC service via [step 2.2](#step-22-deploy) above.  Make sure to **drop the `http://` or `grpc://` part and the trailing `/` from the URL when copying**. For example, if the URL is `http://go-grpc-server-3192360657:8080/`, add only `go-grpc-server-3192360657:8080`.|

5.  Click **Add** and then click **Next**.
6.  Click **Deploy**.

### Step 3.4: Execute the gRPC client component

To execute the gRPC client, follow these steps:

1. In the left navigation menu, click **Execute**.
2. Click **Run Now**. This triggers the gRPC client and generates logs.
   You can trigger multiple runs to generate multiple logs.

For details on what you can monitor via the execute page, see [Monitor executions](../develop-integrations/develop-a-manual-task.md#step-7-monitor-executions). 
