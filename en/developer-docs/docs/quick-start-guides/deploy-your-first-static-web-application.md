# Deploy Your First Static Web Application

{{ product_name }} is an internal developer platform as a service that simplifies building, deploying, monitoring, and managing cloud-native applications. It allows developers to focus on innovation and implementation by handling platform complexities.

In this guide, you will learn how to deploy a static web application using {{ product_name }}. The sample application is a simple to-do list where users can add tasks.

This guide walks you through the following steps:

1. Create a project.
2. Create a Web Application component and connect it to a GitHub repository.
3. Build the web application.
4. Deploy the web application and access it.

For a video tutorial, see [Deploy a Static Web App on {{ product_name }}](https://www.youtube.com/watch?v=YPBSuLG5l5g).

## Prerequisites

1. GitHub account: Fork the [{{ product_name }}-sample-todo-list-app repository](https://github.com/wso2/choreo-sample-todo-list-app), which contains the sample for this guide.

2. If you're signing in to the {{ product_name }} Console for the first time, create an organization:
    1. Go to the [{{ product_name }} Console](https://console.choreo.dev/) and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

## Step 1: Create a project

1. Go to the [{{ product_name }} Console](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter the following details:

    !!! tip
        The **Name** field must be unique and cannot be changed after creation.

    | **Field**                | **Value**             |
    |--------------------------|-----------------------|
    | **Project Display Name** | Sample project        |
    | **Name**                 | sample-project        |
    | **Project Description**  | My sample project     |

4. Click **Create**. This creates the project and opens the project home page.

## Step 2: Create a web application component

1. On the project home page, click **Web Application** under **Create a Component**.
2. Select **Authorize with GitHub** from the **Connect a Git Repository** section to connect {{ product_name }} to your GitHub account. If you haven't connected your GitHub repository to {{ product_name }}, enter your credentials and select the repository you forked earlier to install the [{{ product_name }} GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, paste the [{{ product_name }}-sample-todo-list-app repository](https://github.com/wso2/choreo-sample-todo-list-app) URL in the **Provide Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [{{ product_name }} GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **{{ product_name }} GitHub App** requires:
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; {{ product_name }} will not push changes directly to your repository.

3. Enter the following information:

    | **Field**                    | **Value**                   |
    |------------------------------|-----------------------------|
    | **Organization**             | Your GitHub account         |
    | **Repository**               | {{ product_name }}-sample-todo-list-app |
    | **Branch**                   | main                        |

4. Select **NodeJS** as the **Build preset**.
5. Enter the following details:

    | **Field**                    | **Value**                   |
    |------------------------------|-----------------------------|
    | **NodeJS Project Directory** | /                           |
    | **Language Version**         | 20.x.x                      |
    | **Port**                     | 8080                        |

6. Enter a unique name and description for the web application.

7. Click **Create**. {{ product_name }} initializes the component with the sample implementation and opens the **Overview** page of the component.

Now let's build and deploy the web application.

## Step 3: Build your web application

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build Latest**.

    !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.

Now you can proceed to deploy your web application.

## Step 4: Deploy and access your web application

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure and Deploy**. This opens the **Configure & Deploy** pane. In this guide, you do not need to add a file mount.
3. Click **Deploy**. The deployment to the Development environment may take a few minutes. Once complete, the **Deployment Status** will show as **Active** in the **Development** card.
4. To verify that the web application is hosted successfully, click the **Web App URL** on the **Development** card. This takes you to the web application. You can try adding tasks by specifying a task ID and task label.

After successfully testing your web application, explore other {{ product_name }} features like [observability](../monitoring-and-insights/observability-overview.md) and [DevOps](../devops-and-ci-cd/view-runtime-details.md).
