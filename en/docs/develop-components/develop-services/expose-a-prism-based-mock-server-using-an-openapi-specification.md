# Expose a Prism-Based Mock Server Using an OpenAPI Specification

Choreo supports deploying [Prism-based](https://docs.stoplight.io/docs/prism/674b27b261c3c-prism-overview) mock servers using OpenAPI specifications. With the Choreo Prism mock service component, you can serve multiple mock servers, each based on a different OpenAPI specification, as separate endpoints.

By default, these mock servers serve static content generated from the examples in the configured OpenAPI Specification (OAS). 

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [Prism mock service](https://github.com/wso2/choreo-samples/tree/main/prism-mock-service) artifacts for this guide.

## Step 1: Create a Prism mock service component

To create a Prism mock service component, follow these steps:
    
1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

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
    |**Component Directory**  | `prism-mock-service`    |

6. Select **Prism Mock** as the buildpack.
7. Enter a display name, a unique name, and a description for the service component.
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

8. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

## Step 2: Build and deploy the component

Now that you have successfully created the Prism mock service, it's time to build and deploy it.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**. This builds the service from the latest commit.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 2.2: Deploy

To deploy the service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure &  Deploy**.
3. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed the service, you can [test](../../testing/test-rest-endpoints-via-the-openapi-console.md), [manage](../../api-management/lifecycle-management.md), and [observe](../../monitoring-and-insights/observability-overview.md) it like any other component type in Choreo.

## Serve dynamic content

The Prism mock service can serve dynamic content according to the schema definitions in the OpenAPI specification. To use the Prism mock service with dynamic content, clients must add the `prefer:dynamic=true` header in the request.

To generate meaningful dynamic responses, you can use the `x-faker` attribute in schema definitions of the OpenAPI specification. For more information on dynamically generated responses with the `x-faker` attribute, see the [Prism documentation](https://docs.stoplight.io/docs/prism/9528b5a8272c0-dynamic-response-generation-with-faker).
