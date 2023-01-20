# Create Your First Integration

An integration is a component with logic to integrate APIs, microservices, applications, and data in different languages and formats.

Consider a scenario where a developer has created an integration in [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) and wants to use it for API-led integration by exposing it in Choreo. In this tutorial, you will learn how to do the following actions to address this requirement:


- Expose an integration created in [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) as a Choreo component by connecting the GitHub repository in which it resides to Choreo.
- Deploy the Integration component to the development environment.
- Test the Integration component.
- Observe statistics for the Integration component.
- Publish the Integration component and try it out in the production environment.

For this tutorial, let's use a basic sample application that returns `Hello` as the response when you invoke it.


!!! tip "Before you begin!"
    To try this tutorial, you can use a sample integration designed via [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/).<br/><br/>To do this, fork the [Hello World Project repository](https://github.com/chameerar/hello-world-project).

## Step 1: Create

Let's add the integration by following the steps given below:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. On the **Home** page, click on the project in which you want to create the integration. Alternatively, you can click **+ Create Project** and add a new project.

3. If your project has one or more components, click **+ Create**. If not, proceed to the next step.

4. On the **Integration** card, click **Create**.

5. On the REST API card, click **Connect an MI Repo** to start creating a micro integrator REST API.

6. Enter a name and a description for the micro integrator REST API. For example, you can enter `MI Hello World` as the name and `MI Hello World REST API` as the description.

7. To the micro integrator REST API as a publicly accessible API, leave the selection in the **Access Mode** list unchanged.

8. Click **Next**.

9. Authorize Choreo to connect to your GitHub account by clicking **Authorize with GitHub**.

10. If you have not already authorized Choreo applications, click **Authorize Choreo Apps** when prompted.

11. Enter information related to the GitHub repository you want to connect as follows:

     | **Field**             | **Value**                        |
     |-----------------------|-------------------------------------------------------------------------------------------------------------|
     | **GitHub Account**    | Select your GitHub account.                                                                                 |
     | **GitHub Repository** | Select your fork of the [Hello World Project repository](https://github.com/chameerar/hello-world-project). |
     | **Branch**            | `main`                                                                                                      |

12. Under **Build Preset**, click **Micro Integrator**.

     !!! info
         The build preset specifies the type of build that Choreo needs to run for the component (for example, Choreo needs to run a micro integrator build for components developed via the [WSO2 Integration Studio]((https://wso2.com/integration/integration-studio/), a Ballerina build for a component added via a Ballerina project etc.).

14. Click **Create**.

The micro integrator REST API opens on a separate page.

## Step 2: Deploy

Let's deploy the micro integrator REST API you created to the developer environment by following the steps given below:

1. In the left navigation menu, click **Deploy**.

2. Click **Deploy Manually**.

    !!! info
        Automatic deployment is enabled for the component by default. You are required to carry out only the first deployment manually.

Once Choreo has deployed the micro integrator REST API, you can proceed to the next step to test it.

## Step 3: Test

Choreo assists you to generate a cURL command to invoke the micro integrator REST API you created. To generate this cURL command and issue it, follow these steps:

1. In the left navigation menu, click **Test**.

2. In the **Method** list, select **Get**.

3. Enter `/HelloWorld` as the path.

4. To copy the cURL command, click the icon for copying

    !!! note
        Always use the icon to copy the cURL command instead of copying it manually because the displayed cURL command is only for display purposes and does not include the API token.

5. Issue the cURL command that you copied. You will get the following response.

    `{"Hello":"Integration"}`

The above response indicates that your micro integrator REST API is working as expected.

## Step 4: Observe

To observe statistics for your micro integrator REST API, click **Observe** in the left navigation menu. The graphs for throughput and latency will display the request you sent in [Step 3: Test](#step-3-test).

For more information about observing components, see [Observability Overview](../../observe-and-analyze/observe/observability-overview.md).

## Step 5: Manage

For a micro integrator REST API, you can perform the following API management actions:

- Manage the lifecycle: You can publish your API to the Developer Portal to make it available for subscriptions by other applications, or deploy it as a prototype so that application developers can try it out and provide feedback for improvement. Once you publish an API, you can deprecate it or block it from being used if required.

- Validate subscriptions: You can configure subscription validation to mandate subscriptions.

- Add documents: This involves attaching files with information about the API for users.

- Select/switch usage plans: You can select a usage plan for your API out of Bronze Silver, Gold, and Unlimited based on the level of traffic that you expect the API to receive. You can change the usage plan when required.

- Edit properties: You can enter/update the names and email addresses of the business owner and the technical owner of the API.

Let's publish the micro integrator REST API to make it available for subscriptions in the Developer Portal.

1. In the left navigation menu, click **Manage**, and then click **Lifecycle**.

2. Click **Publish**.

    The lifecycle status of the API will change to **Published**.

3. To view the API in the Developer Portal, click **Go to Devportal**. The API opens on a separate page in the Developer Portal.

Congratulations! You have successfully exposed an integration designed in the [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) as a REST API in Choreo and published it!









