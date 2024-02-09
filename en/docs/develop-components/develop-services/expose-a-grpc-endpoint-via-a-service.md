# Develop a gRPC Service

Choreo allows you to create and deploy applications in your preferred programming language. One powerful option is gRPC, a high-performance and language-agnostic Remote Procedure Call (RPC) framework. It utilizes [Protocol Buffers](https://github.com/protocolbuffers/protobuf) to define services and generate client/server code.

In this guide, you will learn to use Choreo to create a service component that exposes a gRPC server implemented in [Go](https://go.dev/), enabling efficient and scalable communication with any gRPC client application. No prior knowledge of the Go language is necessary to follow this guide.

By following this guide, you will:

- Create a simple gRPC server using a Service component.
- Link the containerized gRPC service to the Choreo component using the Dockerfile. 
    The greeter service has a single RPC method named `sayHello`. Following is the proto file the server and the client application are using:

    ```proto
    syntax = "proto3";
    option go_package = "gtihub.com/wso2/choreo-sample-apps/go/grpc-greeter/pkg";

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

1. To deploy a containerized component, you will need a GitHub account with a repository that contains a Dockerfile. Fork the [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
    - Read access to issues and metadata.
    - Read and write access to code, pull requests, and repository hooks.

Let's get started!

### Learn the repository file structure

Let's familiarize ourselves with the key files in the sample greeter application. The below table gives a brief overview of the important files in the greeter service.

!!! note 
    The following file paths are relative to the path `<sample-repository-dir>/go/grpc`.

|File Path                |Description                                                                                   |
|------------------------|----------------------------------------------------------------------------------------------|
| main.go                | The greeter service code written in the Go language.                                       |
| greeter_client/main.go | The greeter client application written in the Go language.                                    |
| Dockerfile.server      | Dockerfile to build the container image of the server application.|
| Dockerfile.client      | Dockerfile to build the container image of the client application.|
| .choreo/endpoints.yaml |  The Choreo-specific configuration provides information about how Choreo exposes the service.|
| pkg/greeter.proto      |Interface definition of the gRPC service. This is used to generate the server and client stubs for the Go application.|

### Configure the service port with endpoints

Let's run the gRPC server Service component on port 8080. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. In Choreo, you can expose your services with endpoints. You can read more about endpoints in our [endpoint documentation](https://wso2.com/choreo/docs/develop-components/develop-services/develop-a-service/#what-are-endpoints-in-service-components).

Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory to configure the endpoint details of a containerized component. Place the `.choreo` directory at the root of the Docker build context path.

In our gRPC server sample, the `endpoints.yaml` file is at `go/grpc/.choreo/endpoints.yaml`. Our build context path is `go/grpc`.

## Step 1: Create a service component with a gRPC endpoint

Let's create a containerized Service component by following these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click **Service** card.
4. You can enter a unique name and a description of the service. For this guide, let's enter the following values:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Go gRPC Server`        |
    | **Description** | `Sends greetings`       |
5. If you have not already connected your GitHub repository to Choreo, to allow Choreo to connect to your GitHub account, click **Authorize with GitHub** and enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:

         - Read and write access to code and pull requests.
         - Read access to issues and metadata.
             
          You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

6. Enter the following information:

    | **Field**               | **Description**                 |
    |-------------------------|---------------------------------|
    | **GitHub Account**      | Your account                    |
    | **GitHub Repository**   | **`choreo-sample-apps`**        |
    | **Branch**              | **`main`**                      |
    | **Buildpack**        | **Dockerfile**                  |
    | **Dockerfile Path**     | **go/grpc/Dockerfile.server** |
    | **Docker Context Path** | **go/grpc**                     |


    !!! info
        1.  To successfully build your container with Choreo, it is essential to explicitly define a User ID (UID) under the USER instruction in your Dockerfile. For reference, see [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/greeter/Dockerfile).
        To ensure that the defined USER instruction is valid, it must conform to the following conditions:
            - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
            - Usernames are considered invalid and should not be used. For example, `my-custom-user-12221` or `my-custom-user` are invalid User IDs.

        2. The Dockerfile utilized in this guide is a multi-stage Dockerfile, which is designed to keep the final image size small and provides the ability to build the application with a specific version of tools and libraries.

7. Click **Create**. Once the component creation is complete, you will see the component's overview page.

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

6. Once you have successfully deployed your service, navigate to the component overview page and copy the gRPC service URL. You need to provide that URL when setting up the client application later in this guide.

You have successfully deployed the gRPC server. Currently, the gRPC service is only accessible for the components deployed within the same project.

## Step 3: Invoke the gRPC service

Let's invoke the gRPC service that you created above, using a gRPC client. 

### Step 3.1: Create a manual Task for the gRPC client

Let's create a containerized manual trigger component by following these steps:

1. Click on the **Project** list to go to the **Components** page. Alternatively, you can expand the **Components** list and click **+ Create New**.
2. On the **Components** page, click **Create**.
3. Click **Manual Task** card.
4. Enter a unique name and a description for the client component. For this guide, let's enter the following values:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Go gRPC Client`        |
    | **Description** | `Receive greetings`     |

5. Select **GitHub** tab.
6. Select the following values to connect the repository:

    | **Field**             | **Description**                 |
    |-----------------------|---------------------------------|
    | **GitHub Account**    | Your account                    |
    | **GitHub Repository** | **`choreo-sample-apps`**        |
    | **Branch**            | **`main`**                      |
    | **Buildpack**      | **Dockerfile**                  |
    | **Dockerfile Path**   | **go/grpc/Dockerfile.client** |
    | **Docker Context Path** | **go/grpc**                   |

7. Click **Create** . Once the component creation is complete, you will see the component overview page.

### Step 3.2: Setup environment variables

The client application, in this case, the gRPC client,  needs the server URL of the gRPC server service. This is read from the client application as an environment variable. Follow the steps below to configure the environment variable for the client application:

1. Navigate to the **gRPC Server** component's **DevOps** page from the left navigation and click on **Configs and Secrets**.
3. Click **+ Create**.
4. Select **Config Map** as the **Config Type** and **Environment Variables** as the **Mount Type**.
5. Click **Next**.
6. Provide the following values to add the environment variables:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Config Name** | `serviceurl`            |
    | **Name**        | `GREETER_SERVICE`       |
    | **Value**       | Paste the URL value copied from the gRPC service in the previous step of the guide.  Make sure to **drop the `http://` or `grpc://` part and the trailing `/` from the URL when copying**. For example, add only `go-grpc-greeter-3192360657:8080`.|

7. Click **Create**.

### Step 4.3: Build and deploy the gRPC client component

Now that you have connected the source repository, and configured the environment variable details, let's build and run the greeter client.

To build and run the client, follow these steps:

1. Go to the **Deploy** page and click **Deploy Manually**.
2. To trigger the gRPC client and generate logs, go to the **Development** card and click **Run Once**. Perform multiple runs to generate multiple logs.
3. Navigate to the **Observability** page from the left navigation menu and view **Logs**.
If the logs are not present give it a bit more time to fetch the logs. You will see several log lines corresponding to each run of the client trigger in the log view.



