# Develop a Ballerina REST API

Choreo offers the flexibility to develop and deploy applications in a language of your preference. This guide demonstrates how to leverage Choreo to deploy a service component that exposes a REST API using the [Ballerina language](https://ballerina.io/). No prior knowledge of the Ballerina language is required to follow this guide. 

REST API is a web service that adheres to the Representational State Transfer (REST) principles and utilizes HTTP methods to access and manage resources. By following this guide, you will build a service component in Ballerina that can be deployed on Choreo and utilized by any HTTP client application.

In this guide, you will:

- Build a simple greeting service. The greeter service has a single resource named “greet” and accepts a single query parameter as input.
    - Request:

        `$ curl GET http://localhost:9090/greeter/greet?name=Ballerina`

    - Response:

        `$ hello Ballerina!`

- Deploy the service in Choreo. It runs on port 9090.
- Test the service.

## Prerequisites

1. You will need a GitHub account with a repository that contains a Ballerina implementation. Fork the [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
    - Read access to issues and metadata.
    - Read and write access to code, pull requests, and repository hooks.

### Learn the repository file structure

Let's familiarize ourselves with the key files in the sample greeter application. The below table gives a brief overview of the important files in the greeter service.

!!! note 
    The following file paths are relative to the path <sample-repository-dir>/ballerina/greeter

|Filepath                 |Description                                                                   |
|-------------------------|------------------------------------------------------------------------------|
| service.bal             | Greeter service code written in the Ballerina language.                          |
| tests/service_test.bal  | Test files related to the service.bal file.                                  |
| Ballerina.toml          | Ballerina configuration file.                                                |

Let's get started!

## Step 1: Create a service component

Let's create a Ballerina service component by following these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
2. Create a project to add the service component. 
3. On the **Components** page, click on the **Service** card.
4. Enter a unique name and a description of the service. For this guide, let's enter the following values:

    |Field          |     Value              |
    |---------------|------------------------|
    |Name           | `Ballerina Greeter`    |
    |Description    | `Sends greetings`      |

5. Select **GitHub** tab.
6. If you have not already connected your GitHub repository to Choreo, to allow Choreo to connect to your GitHub account, click **Authorize with GitHub** and enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**             | **Description**    |
    |-----------------------|--------------------|
    | **GitHub Account**    | Your account       |
    | **GitHub Repository** | choreo-sample-apps |
    | **Branch**            | **`main`**         |
    | **Buildpack**      | Ballerina          |
    | **Project Path**      | ballerina/greeter  |
  
8. Click **Create**. Once the component creation is complete, you will see the component overview page.

You have successfully created a Service component that exposes a REST API written in the Ballerina language. Next, let's build and deploy the service.

## Step 2: Build and deploy

Now that we have connected the source repository, and configured the endpoint details, it's time to build and deploy the greeter service.

To build and deploy the service, follow the steps below:

1. In the left navigation click **Deploy** and navigate to the **Deploy** page.
2. In the **Deploy** page, click **Configure &  Deploy**.
3. In the **Configure & Deploy** pane that opens on the right, click the edit icon to edit the **Endpoint**.

4. Under **Network Visibility**, select **Public** and click **Update**.
   
    !!! note
        To test the service over the web you need to expose the service to the public. This is done in a secured manner by selecting this option. Read more about Network Visibility under endpoints

5. Click **Deploy**.

    !!! note
        Deploying the service component may take a while. You can track the progress by observing the logs. Once the deployment is complete, the deployment status changes to Active in the corresponding environment card.

Once you have successfully deployed your service, you can test, manage, and observe it like any other component type in Choreo.

For detailed instructions, see the following sections:

- [Step 3: Test](https://wso2.com/choreo/docs/test/invoke-apis-via-console/)
- [Step 4: Manage](https://wso2.com/choreo/docs/manage/api-management/)

## Manage the deployment

If you want to view Kubernetes-level insights to perform a more detailed diagnosis of this Ballerina REST API, see Choreo's [DevOps capabilities](https://wso2.com/choreo/docs/devops-and-ci-cd/builds-and-deployments/).


