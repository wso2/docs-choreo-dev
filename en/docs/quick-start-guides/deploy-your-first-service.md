# Deploy Your First Service

Choreo is an Internal Developer Platform (IDevP) that makes it easy to deploy, monitor, and manage your cloud-native services. This allows you to focus on innovation and implementation.

With Choreo, you can deploy services written in your preferred programming language in just a few simple steps.

In this guide, you will:

- Use a pre-built service that manages a book list.
- Build and deploy the service in Choreo using the `Node.js` buildpack. The service runs on port 8080.
- Test the service.

For a video tutorial, check out [Deploy Your First Service with Choreo](https://www.youtube.com/watch?v=-qoweQWCiYM).

## Prerequisites

1. GitHub account: Fork the [Choreo sample book list service repository](https://github.com/wso2/choreo-sample-book-list-service/), which contains the sample for this guide.

2. If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the organization home page.

### Repository file structure

Let's review the key files in the sample application:

!!! note 
    The file paths are relative to `<choreo-sample-book-list-service>/`.
| Filepath               | Description                                                                   |
|------------------------|-------------------------------------------------------------------------------|
| `app.mjs`              | The Node.js (JavaScript) service code.                                        |
| `.choreo/component.yaml` | Choreo-specific configuration for exposing the service.                       |
| `openapi.yaml`         | OpenAPI contract for the service, required to publish it as a managed API.    |

Let's get started!

## Step 1: Create a project

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the organization home page.
2. On the organization home page, click **+ Create Project**.
3. Enter the following details:

    !!! info
        The **Name** field must be unique and cannot be changed after creation.

    | **Field**                | **Value**                          |
    |--------------------------|------------------------------------|
    | **Project Display Name** | Book List Project                  |
    | **Name**                 | book-list-project                  |
    | **Project Description**  | My sample project                  |

4. Click **Create**. This creates the project and takes you to the project home page.

## Step 2: Create a service component

1. On the project home page, click **Service** under **Create a Component**.
2. Click **Authorize with GitHub** to connect Choreo to your GitHub account. If you haven't connected your GitHub repository to Choreo, enter your credentials and select the repository you forked earlier to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo sample Book List Service repository](https://github.com/wso2/choreo-sample-book-list-service) URL in the **Provide Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires:
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

3. Enter the following information:

    | **Field**             | **Description**                  |
    |-----------------------|----------------------------------|
    | **Organization**      | Your GitHub account              |
    | **Repository**        | choreo-sample-book-list-service  |
    | **Branch**            | main                             |
    | **Component Directory** | /                 |

4. Select the **NodeJS** buildpack.
5. Enter the following details:

    | **Field**                    | **Description**   |
    |------------------------------|-------------------|    
    | **Language Version**         | 20.x.x            |

6. Enter the following details for Display name and Description:

    | **Field**                 | **Value**              |
    |---------------------------|------------------------|
    | **Component Display Name**| Book List              |
    | **Description**           | Gets the book list     |

7. Click **Create**.

You have successfully created a Service component using the NodeJS buildpack. Now, let's build and deploy the service.

## Step 3: Build and deploy

### Step 3.1: Build

1. On the project home page, click the `Book List` component under **Component Listing**. This takes you to the component overview page.
2. In the left navigation, click **Build**.
3. Click **Build Latest**.

    !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.

### Step 3.2: Deploy

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane, click **Next**.
4. In the **File Mount** pane, click **Next**.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deployment may take some time. Once complete, the status changes to **Active** on the **Development** environment card.

## Step 4: Test the service

1. In the Choreo Console left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console, select **Development** from the environment drop-down.
3. In the **Endpoint** list, select **Books REST Endpoint**.
4. Expand the **GET /books** method and click **Try it out**.
5. Click **Execute**.
6. Check the **Server Response** section.

You can also try out other methods in the OpenAPI Console.

After successfully testing your service, explore other Choreo features like [managing](../api-management/lifecycle-management.md), [observing](../monitoring-and-insights/observability-overview.md), and [DevOps](../devops-and-ci-cd/view-runtime-details.md).
