# Develop a UDP Service

Choreo offers the flexibility to design and deploy applications in the programming language of your choice. One compelling choice for certain network communication needs is UDP (User Datagram Protocol). UDP is a high-speed, connectionless protocol ideal for scenarios where lightweight, real-time data transmission is essential.

In this guide, you will learn to use Choreo to create a Service component that exposes a UDP server implemented in [Go](https://go.dev/), enabling efficient and scalable communication with any UDP client application. No prior knowledge of the Go language is necessary to follow this guide.

By following this guide, you will:

- Setup a UDP Server
    - Create a simple UDP server using a Service component.
    - Link the containerized UDP service to the Choreo component using the Dockerfile. 
    - Deploy the UDP server component in Choreo. 
- Setup a UDP Client
    - Create a UDP client using a Manual Trigger component. 
    - Link the containerized UDP client to the Choreo component using the Dockerfile. 
    - Deploy the UDP client component in Choreo.
- Invoke the UDP server using a UDP client and check the response through the Choreo log view.


## Prerequisites

1. To deploy a containerized component, you will need a GitHub account with a repository that contains a Dockerfile. Fork the [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
    - Read access to issues and metadata.
    - Read and write access to code, pull requests, and repository hooks.

Let's get started!

### Learn the repository file structure

Let's familiarize ourselves with the key files in the sample greeter application. The below table gives a brief overview of the important files in the greeter service.

!!! note 
    The following file paths are relative to the path `<sample-repository-dir>/go/udp-service`.

|File Path                |Description                                                                                   |
|------------------------|----------------------------------------------------------------------------------------------|
| server/main.go                | The greeter service code is written in the Go language.                                       |
| client/main.go | The greeter client application is written in the Go language.                                    |
| Dockerfile.server      | Dockerfile to build the container image of the server application.|
| Dockerfile.client      | Dockerfile to build the container image of the client application.|
| .choreo/endpoints.yaml |  The Choreo-specific configuration provides information about how Choreo exposes the service.|

### Configure the service port with endpoints

Let's run the UDP server Service component on port 5050. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. In Choreo, you can expose your services with endpoints. You can read more about endpoints in our [endpoint documentation](https://wso2.com/choreo/docs/develop-components/develop-services/develop-a-service/#configure-endpoints).

Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory to configure the endpoint details of a containerized component. Place the `.choreo` directory at the root of the Docker build context path.

In our gRPC server sample, the `endpoints.yaml` file is at `go/udp-service/.choreo/endpoints.yaml`. Our build context path is `go/udp-service`.

## Step 1: Create a service component with a UDP endpoint

Let's create a containerized Service component by following these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **Service** card and click **Create**.
4. You can enter a unique name and a description of the service. For this guide, let's enter the following values:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Go UDP Server`        |
    | **Description** | `Sends greetings`       |

5. Select **GitHub** tab.
6. If you have not already connected your GitHub repository to Choreo, to allow Choreo to connect to your GitHub account, click **Authorize with GitHub** and enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:

         - Read and write access to code and pull requests.
         - Read access to issues and metadata.
             
          You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**               | **Description**                 |
    |-------------------------|---------------------------------|
    | **GitHub Account**      | Your account                    |
    | **GitHub Repository**   | **`choreo-sample-apps`**        |
    | **Branch**              | **`main`**                      |
    | **Buildpack**        | **Dockerfile**                  |
    | **Dockerfile Path**     | **go/udp-service/Dockerfile.server** |
    | **Docker Context Path** | **go/udp-service**                     |


    !!! info
        1.  To successfully build your container with Choreo, it is essential to explicitly define a User ID (UID) under the USER instruction in your Dockerfile. For reference, see [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/udp-service/Dockerfile.server).
        To ensure that the defined USER instruction is valid, it must conform to the following conditions:
            - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
            - Usernames are considered invalid and should not be used. For example, `my-custom-user-12221` or `my-custom-user` are invalid User IDs.

        2. The Dockerfile utilized in this guide is a multi-stage Dockerfile, which is designed to keep the final image size small and provides the ability to build the application with a specific version of tools and libraries.

8. Click **Create**. Once the component creation is complete, you will see the component's overview page.

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

6. Once you have successfully deployed your service, navigate to the component overview page and copy the UDP service address. You need to provide that address when setting up the client application later in this guide.

You have successfully deployed the UDP server. Currently, the UDP service is only accessible for the components deployed within the same project.

## Step 3: Invoke the UDP service

Let's invoke the UDP service that you created above, using a UDP client. To do this, you can make use of a Manual Trigger component. We recommend this approach because, in this example, it's more efficient to have a client that connects to the server, sends a request, and then stops. A continuously executing task isn't required. Furthermore, if you use a Manual Trigger component, you won't need to expose an endpoint in the client for invocation, unlike with an API.

### Step 3.1: Create a manual trigger for the UDP client

Let's create a containerized Manual Trigger component by following these steps:

1. Click on the **Project** list to go to the **Components** page. Alternatively, you can expand the **Components** list and click **+ Create New**.
2. On the **Components** page, click **Create**.
3. On the **Manual Trigger** card, click **Create**.
4. Enter a unique name and a description for the client component. For this guide, let's enter the following values:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Go UDP Client`        |
    | **Description** | `Receive greetings`     |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. Select the following values to connect the repository:

    | **Field**             | **Description**                 |
    |-----------------------|---------------------------------|
    | **GitHub Account**    | Your account                    |
    | **GitHub Repository** | **`choreo-sample-apps`**        |
    | **Branch**            | **`main`**                      |
    | **Buildpack**      | **Dockerfile**                  |
    | **Dockerfile Path**   | **go/udp-service/Dockerfile.client** |
    | **Docker Context Path** | **go/udp-service**                   |

8. Click **Create** . Once the component creation is complete, you will see the component overview page.

### Step 3.2: Setup environment variables

The client application, in this case, the UDP client, needs the server address of the UDP server service. Choreo reads this from the client application as an environment variable. Follow the steps below to configure the environment variable for the client application:

1. Navigate to the **UDP Client** component's **DevOps** page from the left navigation and click on **Configs and Secrets**.
3. Click **+ Create**.
4. Select **Config Map** as the **Config Type** and **Environment Variables** as the **Mount Type**.
5. Click **Next**.
6. Provide the following values to add the environment variables:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Config Name** | `service-configuration`            |
    | **Name**        | `SERVER_ADDRESS`       |
    | **Value**       | Paste the URL value copied from the UDP service in the previous step of the guide.  Make sure to **drop the `udp://` part and the trailing `/` from the URL when copying**. For example, add only `go-udp-greeter-3192360657:5050`.|

7. Click **Create**.

### Step 3.3: Build and deploy the UDP client component

Now that you have connected the source repository, and configured the environment variable details, let's build and run the greeter client.

To build and run the client, follow these steps:

1. Go to the **Deploy** page and click **Configure & Deploy**.
2. On the **Environment Variables** side pane, leave the fields empty, and click **Next**.
3. On the **Config File**, leave the fields empty, and click **Deploy** .
4. To trigger the UDP client and generate logs, go to the **Development** card and click **Run Once**. Perform multiple runs to generate multiple logs.
5. Navigate to the **Observability** page from the left navigation menu and view **Logs**.
If the logs are not present give it a bit more time to fetch the logs. You will see several log lines corresponding to each run of the client trigger in the log view.
6. Navigate to UDP Server **Observability** page and view the UDP server service **Logs**.
