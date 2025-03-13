# Expose a UDP Server via a Service

Choreo offers the flexibility to design and deploy applications in the programming language of your choice. One compelling choice for certain network communication needs is UDP (User Datagram Protocol). UDP is a high-speed, connectionless protocol ideal for scenarios where lightweight, real-time data transmission is essential.

In this guide, you will learn to use Choreo to create a Service component that exposes a UDP server implemented in [Go](https://go.dev/), enabling efficient and scalable communication with any UDP client application. No prior knowledge of the Go language is necessary to follow this guide.

By following this guide, you will:

- Setup a UDP server
    - Create a simple UDP server using a Service component.
    - Link the containerized UDP service to the Choreo component using the Dockerfile. 
    - Deploy the UDP server component in Choreo. 
- Setup a UDP client
    - Create a UDP client using a manual task component. 
    - Link the containerized UDP client to the Choreo component using the Dockerfile. 
    - Deploy the UDP client component in Choreo.
- Invoke the UDP server using a UDP client and check the response through the Choreo log view.

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample service](https://github.com/wso2/choreo-samples/tree/main/docker-udp-service) implementation for this guide.

Let's get started!

### Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the sample service.

!!! note 
    The following file paths are relative to the path `<sample-repository-dir>/docker-udp-service`.

|File Path                |Description                                                                                   |
|-------------------------|----------------------------------------------------------------------------------------------|
| `server/main.go`        | The greeter service code written in the Go language.                                         |
| `client/main.go`        | The greeter client application written in the Go language.                                   |
| `Dockerfile.server`     | The Dockerfile to build the container image of the server application.                       |
| `Dockerfile.client`     | The Dockerfile to build the container image of the client application.                       |
| `.choreo/component.yaml`| The Choreo-specific configuration provides information about how Choreo exposes the service. |

### Configure the service port with endpoints

In Choreo, you can expose your services via endpoints.

Let's run the UDP server service component on port 5050. To securely expose the service through Choreo, you must provide the port and other required information to Choreo. For detailed information on each attribute of an endpoint, see [Configure Endpoints](../configure-endpoints.md).

Choreo looks for an `component.yaml` file inside the `.choreo` directory to configure the endpoint details of a containerized component. Place the `.choreo` directory at the root of the Docker build context path.

In our gRPC server sample, the `component.yaml` file is at `docker-udp-service/.choreo/component.yaml`, where the build context path is `docker-udp-service`.

## Step 1: Create a service component with a UDP endpoint

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
    | **Component Directory**   | `docker-udp-service`    |    

8. Select **Docker** as the buildpack.
9. Enter the following information:
    
    | **Field**                | **Value**                             |
    |--------------------------|---------------------------------------|
    | **Dockerfile**           | `docker-udp-service/Dockerfile.server`|

    !!! info
        1.  To successfully build your container with Choreo, it is essential to explicitly define a User ID (UID) under the USER instruction in your Dockerfile. For reference, see the [sample Dockerfile](https://github.com/wso2/choreo-samples/blob/main/docker-udp-service/Dockerfile.server).
        To ensure that the defined USER instruction is valid, it must conform to the following conditions:
            - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
            - Usernames are considered invalid and should not be used. For example, `my-custom-user-12221` or `my-custom-user` are invalid User IDs.

        2. The Dockerfile utilized in this guide is a multi-stage Dockerfile, which is designed to keep the final image size small and provides the ability to build the application with a specific version of tools and libraries.

4. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Go UDP Server`         |
    | **Component Name**        | `go-udp-server`         |
    | **Description**           | Send greetings          |

10. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service from a Dockerfile. Next, you can build and deploy the service.

## Step 2: Build and deploy

Now that you have connected the source repository, and configured the endpoint details, it's time to build the service and create an image. Then you can deploy the image and test the service.

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

6. Once you have successfully deployed your service, navigate to the component overview page and copy the UDP service address. You must provide that address when setting up the client application later in this guide.

You have successfully deployed the UDP server. Currently, the UDP service is only accessible by the components deployed within the same project.

## Step 3: Invoke the UDP service

Now let's use a UDP client to invoke the UDP service that you created above. To do this, you can create a manual trigger component as the UDP client. This is the recommended approach because, in this example, it's more efficient to have a client that connects to the server, sends a request, and then stops. Here, a continuously executing task isn't required. Furthermore, if you use a manual trigger component, you won't need to expose an endpoint in the client for invocation, unlike with an API.

### Step 3.1: Create a manual trigger for the UDP client

To create a manual task component, follow these steps:

1. On the Choreo Console header, click the **Project** list and select the project where you created the UDP service component. This opens the project home page.
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
    | **Component Directory** | `docker-udp-service`    |

6. Select **Docker** as the buildpack.
7. Enter the following information:
    
    | **Field**                | **Value**                             |
    |--------------------------|---------------------------------------|
    | **Dockerfile**           | `docker-udp-service/Dockerfile.client`|

8. Enter a display name, a unique name, and a description for the component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**              |
    |---------------------------|------------------------|
    | **Component Display Name**| `Go UDP Client`        |
    | **Component Name**        | `go-udp-client`        |
    | **Description**           | `Receive greetings`    |


9. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

### Step 3.2: Build the UDP client component

To build the component, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 3.3: Setup environment variables and deploy the UDP client

The client application, in this case, the UDP client, requires the server URL of the UDP server service. This is read from the client application as an environment variable. Follow the steps below to configure the environment variable for the client application:

1. On the Choreo Console left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane that opens, click **+ Add**.
4. Specify the following name and value to configure the environment variable for the client application:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `SERVER_ADDRESS`       |
    | **Value**       | Paste the URL value copied after deploying the UDP service via [step 2.2](#step-22-deploy) above.  Make sure to **drop the `udp://` part and the trailing `/` from the URL when copying**. For example, add only `go-udp-server-1097922445:8080`.|

5.  Click **Add** and then click **Next**.
6.  Click **Deploy**.

### Step 3.4: Execute the UDP client component

To execute the UDP client, follow these steps:

1. In the left navigation menu, click **Execute**.
2. Click **Run Now**. This triggers the UDP client and generates logs.
   You can trigger multiple runs to generate multiple logs.

For details on what you can monitor via the execute page, see [Monitor executions](../develop-integrations/develop-a-manual-task.md#step-7-monitor-executions).
