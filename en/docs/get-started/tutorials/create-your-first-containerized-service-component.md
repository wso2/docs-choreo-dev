# Deploy Your First Containerized Service Component

You will build and deploy a simple greeting service written in Go language during this tutorial. This tutorial does not require any knowledge of Go language. The features you are learning in this tutorial are fundamental to building a containerized application in any language with Choreo.

## What are we building?

In this tutorial we are building a simple greeting service. Since our intention is to get to know how Choreo handle Dockerfile based builds we want to keep our program code as simple as possible. And Choreo will use the provided Dockerfile to build the deployable greeter service.

Our `greeter` service has a single resource named `greet` which takes a single query param as input. And our service runs on port 9090.

#### Request:

```bash
$ curl GET http://localhost:9090/greeter/greet?name=Asitha
```

#### Response

```bash
$ hello Asitha!
```

Our next step is to setup what is needed to follow the tutorial, including our sample greeter application and Choreo GitHub app

## Prerequisites

1. To deploy a containerized component, you need a GitHub account with a repository that contains a Dockerfile. For this tutorial, let's fork and use the [choreo-sample-apps repository](https://github.com/wso2/choreo-sample-apps).
2. The **Choreo GitHub App** requires the following permission:
    * Read access to issues and metadata
    * Read and write access to code, pull requests, and repository hooks

You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. Choreo needs write access only to send pull requests to a user repository. Choreo does not directly push any changes to a repository.

Let's get started!

## Greeter service file structure

Our sample application has several key files that we need to know before going ahead with the tutorial. Following table gives a brief overview of the important files in our greeter service.

!!! Note
    Following filepaths are relative to the path `<sample-repository-dir>/go/greeter`

| Filepath               | Description                                                                                                                                                                        |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| main.go                | Greeter service code written in Go language                                                                                                                                        |
| Dockerfile             | Dockerfile used by Choreo to build the container image of the application                                                                                                          |
| .choreo/endpoints.yaml | Provides information about the service port, API contract, protocol used etc.                                                                                                      |
| openapi.yaml           | OpenAPI contract of the greeter service. This is needed when we want to publish our service as a managed API. This openapi.yaml file is referenced by the `.choreo/endpoints.yaml` |

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
5. Click **Authorize with GitHub** to allow Choreo to access your GitHub repository with the forked greetings service code in the prerequsites section above.
<br>
    If you have not already authorized Choreo applications, click **Authorize Choreo Apps** when prompted.
    Once you perform the authorization, the **GitHub Account** field in the **Connect Repository** dialog box displays the GitHub account you authorized.
6. Select the following values to connect the repository:
<br>
    !!! info
    To successfully build your container with Choreo, it is essential that you explicitly define a User ID (`UID`) under the `USER` instruction in your Dockerfile. You can refer to the [sample Dockerfile](https://github.com/wso2/choreo-sample-apps/blob/main/go/greeter/Dockerfile) for guidance.

    ```
     To ensure that the defined USER instruction is valid, it must conform to the following conditions:
    
     - A valid User ID is a numeric value between 10000-20000, such as `10001` or `10500`.
     - Usernames are not considered valid and should not be used, for example, `my-custom-user-12221` or `my-custom-user`.
    ```


    | **Field** | **Description** | **Value** |
    | ----- | ----------- | ----- |
    | **GitHub Account** | Your GitHub account. If you want to add another GitHub account, you can expand the list, click <strong>+ Add</strong>, and repeat step 6. | select your GitHub account |
    | **GitHub Repository** | The repository that has the containerized service code. For this tutorial, you can select your fork of the choreo-sample-apps repository. If the repository is not listed, select **+ Connect More Repositories** and follow the instructions to grant Choreo plugin access to the forked choreo-sample-apps repository. | `choreo-sample-apps` |
    | **Branch** | The branch of the repository | `main` |
    | **Build Preset** | Determines the implementation of the component: Since we are building a containerized service component select Dockerfile build preset | `Dockerfile` |
    | **Dockerfile path** | The path to your Dockerfile. This path is defined relative to the repository root. | `go/greeter/Dockerfile` |
    | **Docker build Context Path** | Docker build context path as you would provide using the docker build command. Eg: if the dockerfile was written relative to the repository root use **.** | `go/greeter` |

7\. Click <strong>Create</strong>. Once the component creation is complete you will be navigated to the component overview page.

You have successfully added a Dockerfile-based service component from a Dockerfile. Now let's build and deploy the service.

## Configure the service port with endpoints

As mentioned earlier our greeter service runs on port 9090. We need to provide this port information as well as several other information so that our service gets exposed securely through Choreo. In Choreo we expose our services with endpoints. You can read more about endpoints in our [service components](../../develop/components/service.md) documentation.

To configure endpoint details of a containerized component Choreo looks for an `endpoints.yaml` inside the `.choreo` directory. This `.choreo` directory needs to be at the root of the Docker build context path.

In our greeter sample `endpoints.yaml` is located at `go/greeter/.choreo/endpoints.yaml`. Our build context path is `go/greeter`.

## Build and deploy

Now that we have connected the source repository, and configured the endpoint details, it's time to build and deploy the greeter service.

To build and deploy the service, follow these steps:

1. On the **Deploy** page, click **Deploy Manually**
<br>
    !!! note
    Deploying the service component may take a while. You can track the progress by observing the logs.
<br>
    Once the deployment is complete, the deployment status changes to <strong>Active</strong>. Environment card in the Development environment
2. Check the deployment progress by observing the console logs on the right of the page.
<br>
    You can access the following scans under <strong>Build</strong>.
    * <strong>The Dockerfile scan</strong>: This scans the Dockerfile for a non-root user ID and fails the build if no non-root user is assigned to the Docker container.
    * <strong>Container (Trivy) vulnerability scan</strong>: This detects vulnerabilities in the Dockerfile-based image and in the third-party packages that the source code uses. If you click <strong>Container (Trivy) vulnerability scan</strong>, the details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.
<br>
        !!! info
        If you want to bypass these vulnerabilities, you need to run Choreo on a private data plane.

You can test, manage, and observe the service you created from a Dockerfile the sameway you would do with any other component type.

For detailed instructions, see the following sections of the [Create Your First REST API tutorial](../tutorials/create-your-first-rest-api.md).

* [Step 3: Test](../tutorials/create-your-first-rest-api.md#step-3-test)
* [Step 4: Manage](../tutorials/create-your-first-rest-api.md#step-4-manage)

## Manage the deployment

If you want to view Kubernetes-level insights to carry out a more detailed diagnosis of this Dockerfile-based REST API, click **DevOps Portal** in the top menu.

![Access DevOps Portal](../../assets/img/byoc/access-devops-portal.png){.cInlineImage-small}

For more information about the information, see [DevOps Portal](../../devops/devops-portal.md).
