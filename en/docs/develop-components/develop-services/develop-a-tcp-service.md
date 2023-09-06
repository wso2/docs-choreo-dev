# Develop a TCP Service

Choreo offers the flexibility to design and deploy applications in the programming language of your choice. One viable choice for specific network communication needs is TCP (Transmission Control Protocol). TCP provides a reliable, stream-oriented communication mechanism that ensures data integrity and orderliness during transmission. It's the go-to option for scenarios where guaranteed data delivery and error recovery are paramount.

In this guide, you will learn to use Choreo to create a service component that exposes a TCP server implemented in [Go](https://go.dev/), enabling efficient and scalable communication with any TCP client application. No prior knowledge of the Go language is necessary to follow this guide.

By following this guide, you will:

- Setup a TCP Server
    - Create a simple TCP server using a Service component.
    - Link the containerized TCP service to the Choreo component using the Dockerfile. 
    - Deploy the TCP server component in Choreo. 
- Setup a TCP Client
    - Create a TCP client using a Manual Trigger component. 
    - Link the containerized TCP client to the Choreo component using the Dockerfile. 
    - Deploy the TCP client component in Choreo.
- Invoke the TCP server using a TCP client and check the response through the Choreo log view.

## Prerequisites

1. To deploy a containerized component, you will need a GitHub account with a repository that contains a Dockerfile. Fork the [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
    - Read access to issues and metadata.
    - Read and write access to code, pull requests, and repository hooks.

Let's get started!

### Learn the repository file structure

Let's familiarize ourselves with the key files in the sample greeter application. The below table gives a brief overview of the important files in the greeter service.

!!! note 
    The following file paths are relative to the path `<sample-repository-dir>/go/tcp-service`.

|File Path                |Description                                                                                   |
|------------------------|----------------------------------------------------------------------------------------------|
| server/main.go                | The greeter service code is written in the Go language.                                       |
| client/main.go | The greeter client application is written in the Go language.                                    |
| Dockerfile.server      | Dockerfile to build the container image of the server application.|
| Dockerfile.client      | Dockerfile to build the container image of the client application.|
| .choreo/endpoints.yaml |  The Choreo-specific configuration provides information about how Choreo exposes the service.|

## Step 1: Create a service component with a TCP endpoint

Let's create a containerized Service component by following these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **Service** card and click **Create**.
4. You can enter a unique name and a description of the service. For this guide, let's enter the following values:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Go TCP Server`        |
    | **Description** | `Sends greetings`       |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created by forking [https://github.com/wso2/choreo-sample-apps](https://github.com/wso2/choreo-sample-apps) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:

         - Read and write access to code and pull requests.
         - Read access to issues and metadata.
             
          You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

8. In the **Connect Repository** pane, enter the following information:

    | **Field**               | **Description**                 |
    |-------------------------|---------------------------------|
    | **GitHub Account**      | Your account                    |
    | **GitHub Repository**   | **`choreo-sample-apps`**        |
    | **Branch**              | **`main`**                      |
    | **Build Preset**        | **Dockerfile**                  |
    | **Dockerfile Path**     | **go/tcp-service/Dockerfile.server** |
    | **Docker Context Path** | **go/tcp-service**                     |


    !!! info
        1.  To successfully build your container with Choreo, it is essential to explicitly define a User ID (UID) under the USER instruction in your Dockerfile. For reference, see [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/tcp-service/Dockerfile.server).
        To ensure that the defined USER instruction is valid, it must conform to the following conditions:
            - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
            - Usernames are considered invalid and should not be used. For example, `my-custom-user-12221` or `my-custom-user` are invalid User IDs.

        2. The Dockerfile utilized in this guide is a multi-stage Dockerfile, which is designed to keep the final image size small and provides the ability to build the application with a specific version of tools and libraries.

9. Click **Create**. Once the component creation is complete, you will see the component's overview page.

You have successfully created a Service component from a Dockerfile. Now let's build and deploy the service.

## Step 2: Configure the service port with endpoints

Let's run the TCP server Service component on port 5050. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. In Choreo, you can expose your services with endpoints. You can read more about endpoints in [Configure Endpoints](https://wso2.com/choreo/docs/develop-components/develop-services/develop-a-service/#configure-endpoints).

Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory to configure the endpoint details of a containerized component. Place the `.choreo` directory at the root of the Docker build context path.

In our TCP server sample, the `endpoints.yaml` file is at `go/tcp-service/.choreo/endpoints.yaml`. Our build context path is `go/tcp-service`.

## Step 3: Build and deploy

Having connected the source repository and configured the endpoint details, you can now proceed to build and deploy the TCP server Service component.

To build and deploy the service, follow these steps:

1. In the left navigation menu, click **Deploy**.

2. On the Deploy page, click **Configure & Deploy**.

    !!! note
        Deploying the service component may take a while. You can track the progress by observing the logs. Once the deployment is complete, the deployment status changes to **Active** in the corresponding environment card.

3. Check the deployment progress by observing the console logs on the right of the page.
    You can access the following scans under **Build**. 

    - **The Dockerfile scan**: Choreo performs a scan to check if a non-root user ID is assigned to the Docker container to ensure security. If no non-root user is specified, the build will fail.
    - **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. 
    -  **Container (Trivy) vulnerability scan**: The details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.

!!! info
    If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

4. On the **Configure & Deploy** pane, click **Deploy**.
5. Click **Next**, on the **Environmental Variables** pane and the **Config File** pane. We do not need to provide any configurations for the TCP server component. 
6. On the **Endpoints** pane, click **Deploy**. 
7. Once you have successfully deployed your service, navigate to the component overview page and copy the TCP service address. You need to provide that address when setting up the client application later in this guide.

You have successfully deployed the TCP server. Currently, the TCP service is only accessible for the components deployed within the same project.

## Step 4: Invoke the TCP service

Let's invoke the TCP service that you created above, using a TCP client. To do this, you can make use of a Manual Trigger component. We recommend this approach because, in this example, it's more efficient to have a client that connects to the server, sends a request, and then stops. A continuously executing task isn't required. Furthermore, if you use a Manual Trigger component, you won't need to expose an endpoint in the client for invocation, unlike with an API.

### Step 4.1: Create a manual trigger for the TCP client

Let's create a containerized manual trigger component by following these steps:

1. Click on the **Project** list and go to the **Components** page. Alternatively, you can expand the **Components** list and click **+ Create New**.
2. On the **Components** page, click **Create**.
3. On the **Manual Trigger** card, click **Create**.
4. Enter a unique name and a description for the client component. For this guide, let's enter the following values:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Go TCP Client`        |
    | **Description** | `Receive greetings`     |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. Select the following values to connect the repository:

    | **Field**             | **Description**                 |
    |-----------------------|---------------------------------|
    | **GitHub Account**    | Your account                    |
    | **GitHub Repository** | **`choreo-sample-apps`**        |
    | **Branch**            | **`main`**                      |
    | **Build Preset**      | **Dockerfile**                  |
    | **Dockerfile Path**   | **go/tcp-service/Dockerfile.client** |
    | **Docker Context Path** | **go/tcp-service**                   |

8. Click **Create** . Once the component creation is complete, you will see the component overview page.

### Step 4.2: Setup environment variables

The client application, in this case, the TCP client, needs the server address of the TCP server service. Choreo reads this from the client application as an environment variable. Follow the steps below to configure the environment variable for the client application:

1. Navigate to the **TCP Client** component's **DevOps** page from the left navigation and click on **Configs and Secrets**.
3. Click **+ Create**.
4. Select **Config Map** as the **Config Type** and **Environment Variables** as the **Mount Type**.
5. Click **Next**.
6. Provide the following values to add the environment variables:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Config Name** | `service-configuration`            |
    | **Name**        | `SERVER_ADDRESS`       |
    | **Value**       | Paste the URL value copied from the TCP service in the previous step of the guide.  Make sure to **drop the `tcp://` part and the trailing `/` from the URL when copying**. For example, add only `go-tcp-greeter-3192360657:5050`.|

7. Click **Create**.

### Step 4.3: Build and deploy the TCP client component

Now that you have connected the source repository, and configured the environment variable details, let's build and run the greeter client.

To build and run the client, follow these steps:

1. Go to the **Deploy** page and click **Configure & Deploy**.
2. On the **Environment Variables** side pane, leave the fields emopty, and click **Next**.
3. On the **Config File**, leave the fields empty, and click **Deploy** .
2. Once the Choreo deploys the component successfully, to trigger the TCP client and generate logs, go to the **Development** card and click **Run Once**. Perform multiple runs to generate logs.
3. Navigate to the **Observability** page from the left navigation menu and view **Logs**.
If the logs are not present, try again after a few minutes to fetch the logs. You will see several log lines corresponding to each run of the client trigger in the log view.
4. Navigate to TCP Server **Observability** page and view the TCP server service **Logs**.
