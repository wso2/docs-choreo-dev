# Build and Deploy a Mock Server

Choreo supports deploying [Prism-based](https://docs.stoplight.io/docs/prism/674b27b261c3c-prism-overview) mock servers using OpenAPI specifications. With the Choreo Prism Mock Service component, you can serve multiple mock servers from different OpenAPI specifications as separate endpoints.

Mock servers are configured to serve static content by default based on the examples provided in the configured OpenAPI Specification (OAS). 

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [prism mock service](https://github.com/wso2/choreo-samples/tree/main/prism-mock-service) artifacts.

## Step 1: Create a prism mock service component

To create a prism mock service component, follow these steps:
    
1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in.

2. Create your organization and a project if you haven't already.

3. Open an existing project or create a new project.

4. Click **+ Create** and select **Service** and choose **Prism Mock** buildpack.

5. Provide a name and description for the component.

6. Use `https://github.com/wso2/choreo-samples` as the repository URL, and select the branch as `main`.

7. Select `prism-mock-service` as the project path.

8. Click **Create**.

## Step 2: Build and deploy the component

Now that you have connected the source repository, it's time to build and deploy the prism mock service.

### Step 2.1: Build

To build the service, follow these steps:

1. Navigate to **Build** page of the component.
2. Click **Build Latest** to build the service from the latest commit.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 2.2: Deploy

To deploy the service, follow these steps:

1. Navigate to the **Deploy** page of the component.
2. On the **Set Up** card, click **Configure & Deploy**.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed the service, you can [test](../../testing/test-rest-endpoints-via-the-openapi-console.md), [manage](../../api-management/lifecycle-management.md), and [observe](../../monitoring-and-insights/observability-overview.md) it like any other component type in Choreo.

## Serving dynamic contents

The prism mock service is capable of serving dynamic contents when the OpenAPI specification contains `x-faker` attribute to the properties. To use the prism mock service with dynamic contents, the clients should add `prefer:dynamic=true` in the request headers.

Refer to the [prism documentation](https://docs.stoplight.io/docs/prism/9528b5a8272c0-dynamic-response-generation-with-faker) for more information.
