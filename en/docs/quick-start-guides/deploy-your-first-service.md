# Deploy Your First Service

Choreo, an Internal Developer Platform (IDevP), simplifies the deployment, monitoring, and management of your cloud-native services, allowing you to focus on innovation and implementation.

Choreo allows you to easily deploy services you've created in your preferred programming language in just a few steps.

In this guide, you will:

- Use a pre-implemented service that has resources to maintain a book list. 
- Build and deploy the service in Choreo using the `Nodejs` buildpack. It runs on port 8080.
- Test the service.

## Prerequisites

1.  You will need a GitHub account with a repository that contains your service implementation. To follow this guide you can fork the [Choreo sample book list service repository](https://github.com/wso2/choreo-sample-book-list-service/), which contains the sample for this guide.

2. If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

### Learn the repository file structure

Let's familiarize ourselves with the key files in this sample application. The below table gives a brief overview of the important files in the sample book list service.

!!! note 
    The following file paths are relative to the path <choreo-sample-book-list-service>/
    
|Filepath               |Description                                                                   |
|-----------------------|------------------------------------------------------------------------------|
|app.mjs	            |The Node.js (JavaScript) based service code.|
|.choreo/endpoints.yaml	|Choreo-specific configuration that provides information about how Choreo exposes the service.|
|openapi.yaml	        |OpenAPI contract of the service. This is required to publish our service as a managed API. This openapi.yaml file is referenced by the .choreo/endpoints.yaml.|

Let's get started!

## Step 1: Create a multi repository project 

Follow the steps given below to create a multi repository project:

!!! info
     A multi repository project allows you to maintain multiple repositories and dedicate each of them to specific components or modules in your project. 

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter a unique name and description for the project. You can enter the name and description given below:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | Book List project       |
    | **Description** | My sample multi repository project |

4. Select **Multi Repository**.
5. Click **Create**.

## Step 2: Create a service component

Let's create a service component by following these steps:

1. In the landing page, under **Connect Your Code**, click on the **Service** card.
2. Enter a unique name and a description of the service. For this guide, let's enter the following values:

    |Field          |     Value              |
    |---------------|------------------------|
    |Name           | Book List              |
    |Description    | Gets the book list     |

3. Select the **GitHub** tab.
4. If you have not already connected your GitHub repository to Choreo, click **Authorize with GitHub**, enter your GitHub credentials, and select the repository you created by forking [Choreo sample book list service repository](https://github.com/wso2/choreo-sample-book-list-service/)s to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps). 

    !!! info
         The **Choreo GitHub App** requires the following permissions:
         
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.
             
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.



5. Enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | choreo-sample-book-list-service |
    | **Branch**            | main                             |

6. Select the buildpack **NodeJS**.
7. Enter the following information.

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|    
    | **NodeJS Project Directory**       | / |
    | **Language Version**              | 20.x.x |

8. Click **Create**. Once the component creation is complete, you will see the component overview page.

You have successfully created a Service component with the NodeJS buildpack. Now let's build and deploy the service.

## Step 3: Build and deploy
Now that you have connected the source repository, and configured the endpoint details, it's time to build the service. Choreo will create an image in the build process. You can then deploy the built image and test the book list service.

### Step 3.1: Build

To build the service, follow these steps:

1. Select the component you created from the **Components Listing**.
1. From the left navigation, go to the **Build** page and click **Build**.
2. Select the latest commit and click **Build**.
3. Check the deployment progress by observing the console logs on the right of the page.

    !!! note
        Building the service component may take a while. You can track the progress by observing the logs. Once the build process is complete, the build status changes to **Success**.

### Step 3.2: Deploy

Next, to deploy this service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure &  Deploy**.
3. Skip configuring the **Environment Configurations** and click **Next**.
4. Skip adding a **File Mount**. Click **Next**.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. You can track the progress by observing the logs. Once the deploying is complete, the build status changes to **Active** on the **Development** environment card.

After you have successfully deployed your service, you can now try out various other Choreo features such as [testing](../testing/test-rest-endpoints-via-the-openapi-console.md), [managing](../api-management/lifecycle-management.md), [observing](../monitoring-and-insights/observability-overview.md), and [DevOps](../devops-and-ci-cd/view-runtime-details.md) it like any other component type in Choreo.
