**# Create Your First Containerized Service Component

During this tutorial you will learn how to deploy a containerized service component on Choreo written in Go programming language.
This tutorial does not require any prior knowledge of the Go programming language. The features you will learn in this tutorial are fundamental to building and deploying a containerized application in any language using Choreo.

## What are we building?

In this tutorial, we are building a simple greeting service. Since our intention is to learn how you can deploy Dockerfile-based application in Choreo, we want to keep our program code as simple as possible. 
Choreo will use the provided Dockerfile to build the deployable greeter service.

Our `greeter` service has a single resource named `greet` which takes a single query param as input. And our service runs on port 9090.

#### Request:

```bash
$ curl GET http://localhost:9090/greeter/greet?name=Choreo
```

#### Response

```bash
$ hello Choreo!
```

Our next step is to set up what is needed to follow the tutorial, including our sample greeter application and the [Choreo GitHub app](https://github.com/marketplace/choreo-apps)

## Prerequisites

1. To deploy a containerized component, you will need a GitHub account with a repository that contains a Dockerfile. For this tutorial, let's fork and use the [choreo-sample-apps repository](https://github.com/wso2/choreo-sample-apps).
2. The **Choreo GitHub App** requires the following permissions:
    * Read access to issues and metadata
    * Read and write access to code, pull requests, and repository hooks

!!! info
    Please refer [Containerized Application Deployment](../../deploy/containerized-application.md) for more information.

Let's get started!

## Repository file structure

Our sample application has several key files that we need to know about before proceeding with the tutorial. The following table provides a brief overview of the important files in our greeter service.

!!! note
    Following file paths are relative to the path `<sample-repository-dir>/go/greeter`

| Filepath                   | Description                                                                                                                                                                         |
|----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **main.go**                | Greeter service code written in Go language.                                                                                                                                        |
| **Dockerfile**             | Dockerfile used by Choreo to build the container image of the application.                                                                                                          |
| **.choreo/endpoints.yaml** | Choreo specific configuration that provides information about the how the service get exposed.                                                                                      |
| **openapi.yaml**           | OpenAPI contract of the greeter service. This is needed when we want to publish our service as a managed API. This openapi.yaml file is referenced by the `.choreo/endpoints.yaml`. |

## Create a service component from a Dockerfile

Let's create a containerized service component by following these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev](https://console.choreo.dev).
2. Create a project to add the service component. You can follow the instructions under [Prerequisites in the Connect Your Own GitHub Repository to Choreo](../../develop/manage-repository/connect-your-own-github-repository-to-choreo.md#prerequisites-create-a-project) tutorial.
3. On the **Components** page, click **Create** on the **Service** card.
4. Enter a unique name and a description for the service. For this tutorial, let's enter the following values:
    
    | **Field**       | **Value**         |
    |-----------------|-------------------|
    | **Name**        | `Greetings`       |
    | **Description** | `Sends greetings` |

    Click <strong>Next</strong>.

5. Click **Authorize with GitHub** to allow Choreo to access your GitHub repository with the forked greeting service code in the prerequisites section above.
    If you have not already authorized Choreo applications, click **Authorize Choreo Apps** when prompted.
    Once you perform the authorization, the **GitHub Account** field in the **Connect Repository** dialog box will display the GitHub account you authorized.
6. Select the following values to connect the repository:
    
    | **Field**                     | **Description**                                                                                                                                                                                                                                                                                                                   | **Value**                  |
    |-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------|
    | **GitHub Account**            | Your GitHub account. If you want to add another GitHub account, you can expand the list, click <strong>+ Add</strong>, and repeat step 6.                                                                                                                                                                                         | select your GitHub account |
    | **GitHub Repository**         | The repository that contains the containerized service code. For this tutorial, you can select your fork of the choreo-sample-apps repository. If the repository is not listed, select **+ Connect More Repositories** and follow the instructions to grant the Choreo plugin to access the forked choreo-sample-apps repository. | `choreo-sample-apps`       |
    | **Branch**                    | The branch of the repository                                                                                                                                                                                                                                                                                                      | `main`                     |
    | **Build Preset**              | Determines the implementation of the component: Since we are building a containerized service component select Dockerfile build preset                                                                                                                                                                                            | `Dockerfile`               |
    | **Dockerfile path**           | The path to your Dockerfile. This path is defined relative to the repository root.                                                                                                                                                                                                                                                | `go/greeter/Dockerfile`    |
    | **Docker build Context Path** | Docker build context path as you would provide using the docker build command. Eg: if the dockerfile was written relative to the repository root use **.**                                                                                                                                                                        | `go/greeter`               |
    
    !!! info
        To successfully build your container with Choreo, it is essential to explicitly define a User ID (`UID`) under the `USER` instruction in your Dockerfile. You can refer to the [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/greeter/Dockerfile) for guidance.
    
        ```
         To ensure that the defined USER instruction is valid, it must conform to the following conditions:
        
         - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
         - Usernames are not considered as valid and should not be used, for example, `my-custom-user-12221` or `my-custom-user` are not valid User IDs.
        ```

    !!! info
        The Dockerfile used in this tutorial is a [Multi-stage Dockerfile](https://docs.docker.com/build/building/multi-stage/) which is useful for keeping the final image size small and allows you to build your application with a provided version of tools and libraries.
          
7. Click **Create**. Once the component creation is complete, you will be navigated to the component overview page.

You have successfully created a **Service** component from a Dockerfile. Now let's build and deploy the service.

## Configure the service port with endpoints

As mentioned earlier, our greeter service runs on port 9090. We need to provide this port information as well as several other pieces of information so that our service gets securely exposed through Choreo. 
In Choreo, we expose our services with endpoints. You can read more about endpoints in our [Service](../../develop/components/service.md#what-are-endpoints-in-service-components) component documentation.

To configure the endpoint details of a containerized component, Choreo looks for an `endpoints.yaml` file inside the `.choreo` directory. This `.choreo` directory needs to be located at the root of the Docker build context path.

In our greeter sample, the `endpoints.yaml` file is located at `go/greeter/.choreo/endpoints.yaml`. Our build context path is `go/greeter`.

## Build and deploy

Now that we have connected the source repository, and configured the endpoint details, it's time to build and deploy the greeter service.

To build and deploy the service, follow these steps:

1. On the **Deploy** page, click **Deploy Manually**

    !!! note
        Deploying the service component may take a while. You can track the progress by observing the logs.
        Once the deployment is complete, the deployment status changes to **Active** in the corresponding environment card.

2. Check the deployment progress by observing the console logs on the right of the page.

    You can access the following scans under **Build**.
    * **The Dockerfile scan**: This scan checks the Dockerfile to ensure that a non-root user ID is assigned to the Docker container, and fails the build if no non-root user is specified.
    * **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. If you click **Container (Trivy) vulnerability scan**, the details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.

    !!! info
        If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

Once you have successfully deployed your service, you can test, manage, and observe it just like any other component type in Choreo.

For detailed instructions, see the following sections:

* [Step 3: Test](../../test/invoke-apis-via-console.md)
* [Step 4: Manage](../../manage/api-management.md)

## Manage the deployment

If you want to view Kubernetes-level insights to carry out a more detailed diagnosis of this Dockerfile-based REST API, click **DevOps Portal** in the top menu.

![Access DevOps Portal](../../assets/img/byoc/access-devops-portal.png){.cInlineImage-small}

For more information about the information, see [DevOps Portal](../../devops/devops-portal.md).
