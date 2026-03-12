# Develop an integration as an API service

{{ product_name }} simplifies the process of building, deploying, and managing integration components, making it easy to expose integrations as APIs. This guide walks you through the steps to expose an integration as a managed API service in {{ product_name }}.

In this guide, you will:
- Create a component to expose an integration using a sample from the [{{ product_name }} samples repository](https://github.com/wso2/choreo-samples).
- Deploy, test, and observe the integration component.
- Publish the integration component as an API to the {{ product_name }} Developer Portal.

!!! note
    To develop integrations for **Manual Task** and **Schedule Task** components in {{ product_name }}, use the **automation mode** in the WSO2 Micro Integrator. See [Running MI in Automation Mode](https://mi.docs.wso2.com/en/latest/install-and-setup/install/running-the-mi-in-automation-mode/).

## Prerequisites

1. If you're signing in to the {{ product_name }} Console for the first time, create an organization:
    - Go to the [{{ product_name }} Console](https://console.choreo.dev/) and sign in.
    - Enter a unique organization name. For example, `Stark Industries`.
    - Read and accept the privacy policy and terms of use.
    - Click **Create**. This creates the organization and opens the **Project Home** page.

## Step 1: Create the integration component

1. Go to the [{{ product_name }} Console](https://console.choreo.dev/) and sign in. This opens the **Project Home** page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Click **Use Public GitHub Repository**.
5. Enter the following repository details:

    | **Field**             | **Description**              |
    |-----------------------|----------------------------- |
    | **Repository URL**    | [https://github.com/wso2/choreo-samples](https://github.com/wso2/choreo-samples) |
    | **Branch**            | **`main`**                   |
    | **Component Directory** | `/hello-world-mi`             |

6. Provide a unique name and description for the component:

    | **Field**          | **Value**              |
    |--------------------|------------------------|
    | **Display Name** | `Hello World`          |
    | **Name**   | This will be automatically generated based on the display name.       |
    | **Description**    | `MI Hello World REST API` |

7. Select `WSO2 MI` as the **Build Preset**.

8. Click **Create and Deploy**. {{ product_name }} initializes the component, builds it, and deploys it to the development environment.

## Step 2: Test the integration

1. In the {{ product_name }} Console left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console, select **Development** from the environment drop-down list.
3. Expand the **GET /** operation.
4. Click **Try it out**, then click **Execute**. This sends a request to your deployed integration.
5. Check the response:
    - In the **Response body** section, observe the response. If the integration works as expected, you should see:

     ```json
     {"Hello" : "Integration"}
     ```

## Step 4: Observe the integration

1. In the {{ product_name }} Console left navigation menu, click **Observability** and then select one of the following options to monitor the performance of the integration component:
    - **Alerts**: View and configure alerts based on predefined conditions.
    - **Metrics**: Monitor performance metrics and system statistics.
    - **Runtime Logs**: Access and analyze runtime execution logs.

   For more details, see [Observability Overview](../../monitoring-and-insights/observability-overview.md).

## Step 5: Publish the integration component

1. In the {{ product_name }} Console left navigation menu, click **Manage** and then click **Lifecycle**.
2. The **Lifecycle Management** pane shows the current lifecycle stage as **Created**.
3. Click **Publish**. This changes the lifecycle stage to **Published** and exposes the integration as an API in the {{ product_name }} Developer Portal.
   - To view the published API, click **Go to Devportal**. In the Developer Portal, you can manage subscriptions and generate access tokens for testing.

Now you have successfully exposed a WSO2 MI integration as a REST API in {{ product_name }}!
