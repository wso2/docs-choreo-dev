# Deploy Your First Static Web Application

Choreo is an internal developer platform as a service that takes care of the complexities of building platforms, allowing application developers to focus on innovation and implementation. Choreo streamlines the entire process of building, deploying, monitoring, and managing your cloud-native applications.

In this quick start guide, you will explore how easy it is to deploy a web application using Choreo. Here, you will use a simple web application designed to add tasks to a to-do list. 

This guide walks you through the following steps:

  - Create a project.
  - Create a Web Application component and connect it to the GitHub repository that includes the web application implementation.
  - Build the web application.
  - Deploy the web application and access it.

For a video tutorial that walks you through these steps, see [Deploy a Static Web App on Choreo](https://www.youtube.com/watch?v=YPBSuLG5l5g).

## Prerequisites

Before you try out this guide, complete the following:

1. Fork the [choreo-sample-todo-list-app
 repository](https://github.com/wso2/choreo-sample-todo-list-app), which contains the sample for this guide.
2. If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

## Step 1: Create a project

Follow the steps given below to create a project:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter a display name, unique name, and description for the project. You can enter the values given below:
    
    !!! tip
         In the **Name** field, you must specify a name to uniquely identify your project in various contexts. The value is editable only at the time you create the project. You cannot change the name after you create the project.

    | **Field**                | **Value**             |
    |--------------------------|-----------------------|
    | **Project Display Name** | `Sample project`      |
    | **Name**                 | `sample-project`      |
    | **Project Description**  | `My sample project`   |

4. Click **Create**. This creates the project and opens the project home page.

## Step 2: Create a Web Application component

To create a Web Application component, follow the steps given below:

1. On the project home page, click **Web Application** under **Create a Component**.
2. Enter a unique name and a description for the web application.
3. Go to the **GitHub** tab.
4. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [choreo-sample-todo-list-app repository](https://github.com/wso2/choreo-sample-todo-list-app) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

5. Enter the following information:

    | **Field**                    | **Value**                   |
    |------------------------------|-----------------------------|
    | **Organization**             | Your GitHub account         |
    | **Repository**               | choreo-sample-todo-list-app |
    | **Branch**                   | **`main`**                  |

6. Select **NodeJS** as the **Buildpack**
7. Enter the following information:
   
    | **Field**                    | **Value**                   |
    |------------------------------|-----------------------------|
    | **NodeJS Project Directory** | /                           |
    | **Language Version**         | **20.x.x**                  |
    | **Port**                     | `8080`                      |

8. Click **Create**. Choreo initializes the component with the sample implementation and opens the **Overview** page of the component.

Now let's build and deploy the web application.

## Step 3: Build your web application

To build the web application, follow the steps given below:

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build Latest**.

    !!! note
        Building the component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

Now you can proceed to deploy your web application.

## Step 4: Deploy and access your web application

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure and Deploy**. This opens the **Configure & Deploy** pane, where you can add a file mount if necessary. In this guide you will not add a file mount.
3. Click **Deploy**. The deployment to the Development environment may take a few minutes to complete. On successful deployment, you will see the **Deployment Status** as **Active** in the **Development** card. 
4. To verify that you have successfully hosted the web application, click the **Web App URL** on the **Development** card. This takes you to the web application. You can try creating one or more new tasks by specifying an appropriate task ID and task label.     
   
After you have successfully tested your web application, you can now try out various other Choreo features such as [observability](../monitoring-and-insights/observability-overview.md), [DevOps](../devops-and-ci-cd/view-runtime-details.md), etc., similar to any other component type within Choreo.
