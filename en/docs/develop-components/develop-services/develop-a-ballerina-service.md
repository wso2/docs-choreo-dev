# Develop a Ballerina Service

Choreo allows you to develop and deploy applications using your preferred programming language. This guide demonstrates how to deploy a service component that exposes a REST API using the [Ballerina language](https://ballerina.io/). No prior knowledge of the Ballerina language is required to follow this guide. 

A REST API is a web service adhering to Representational State Transfer (REST) principles, using HTTP methods to access and manage resources. This guide walks you through building a Ballerina service component, deploying it on Choreo, and using it with an HTTP client application.

In this guide, you will:

- Build a simple greeting service using a sample service implementation. The sample implementation will have a single resource named `greet` that accepts a single query parameter as input.
    - Request:

        `$ curl GET http://localhost:9090/greeter/greet?name=Ballerina`

    - Response:

        `$ hello Ballerina!`

- Deploy the service in Choreo. The service will run on port 9090.
- Test the service.

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample greetings service](https://github.com/wso2/choreo-samples/tree/main/greeting-service) implementation in `Ballerina`.

         
### Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the greeter service:

!!! note 
    The specified file paths are relative to `<sample-repository-dir>/greeting-service`.

|**Filepath**              |**Description**                                                |
|--------------------------|---------------------------------------------------------------|
| `service.bal`            | The greetings service code written in the Ballerina language. |
| `tests/service_test.bal` | Test files related to the `service.bal` file.                 |
| `Ballerina.toml`         | The Ballerina configuration file.                             |

Let's get started!

## Step 1: Create a service component

To create a Ballerina service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Ballerina Greetings`   |
    | **Component Name**        | `ballerina-greetings`   |
    | **Description**           | Send greetings          |

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

8. Select **Ballerina** as the buildpack.
9. In the **Ballerina Project Directory**, specify `/greeting-service`.
10. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service that exposes a REST API written in the Ballerina language. Next, let's build and deploy the service.

## Step 2: Build and deploy

Now that you have connected the source repository and configured the endpoint details, it's time to build and deploy the service.

!!! note
    If you are rebuilding the Ballerina service component after changing the Ballerina version, ensure that before building the code, the version of the Ballerina distribution mentioned in the `Ballerina.toml` file matches the distribution version specified in the `Dependencies.toml` file.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.


### Step 2.2: Deploy

To deploy the service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure &  Deploy**.
3. In the **Configurations** pane that opens, click **Next** to skip the configuration.
4. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.
To build and deploy the service, follow the steps below:

Once you have successfully deployed your service, you can test, manage, and observe it like any other component type in Choreo.

For detailed instructions, see the following sections:

- [Step 3: Test](../../testing/test-rest-endpoints-via-the-openapi-console.md)
- [Step 4: Manage](../../api-management/lifecycle-management.md)

## Manage the deployment

If you want to view Kubernetes-level insights to perform a more detailed diagnosis of this Ballerina REST API, see Choreo's [DevOps capabilities](../../devops-and-ci-cd/view-runtime-details.md).
