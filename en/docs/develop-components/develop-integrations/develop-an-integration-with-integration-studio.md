# Develop an Integration with Integration Studio

The seamless integration of APIs, microservices, applications, and data across different languages and formats requires the ability to expose integrations as APIs. Choreo simplifies building, deploying, and managing integration components, making it easy for you to quickly expose integrations as APIs.

!!! note
    - To develop integrations that you need to use in Manual Task and Schedule Task components in Choroeo, you need to use the automation mode in the WSO2 Micro Integrator. See [Running MI in Automation Mode](https://apim.docs.wso2.com/en/latest/install-and-setup/install/running-the-mi-in-automation-mode/). 
    - To develop integrations for any other component types (Service, webhook, event-handler, etc) you need to use the server mode. 

This guide walks you through the steps to expose an integration you created in WSO2 Integration Studio as an API in Choreo. 

In this guide, you will:

  - Create a component to expose the sample integration in the [Choreo samples repository](https://github.com/wso2/choreo-samples) as an API. 
  - Deploy, test, and observe the integration component.
  - Publish the integration component as an API to the Choreo Developer Portal.

## Prerequisites

Before you try out the steps in this guide, complete the following:

 - If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.

 - Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples), which contains the sample integration for this guide.

## Step 1: Create the integration component

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. Enter a unique name and a description for the component. You can enter the name and description given below:

    | **Field**          | **Value**              |
    |--------------------|------------------------|
    | **Component Name** | `Hello World`          |
    | **Description**    | `Hello World REST API` |

5. Click the **GitHub** tab
6. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created by forking [https://github.com/wso2/choreo-samples](https://github.com/wso2/choreo-samples) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


7. Enter the following information:

    | **Field**             | **Description**              |
    |-----------------------|----------------------------- |
    | **Organization**      | Your GitHub account          |
    | **Repository**        | `choreo-samples`            |
    | **Branch**            | **`main`**                   |
    | **Buildpack**         | **WSO2 MI** because you are creating the REST API from a [WSO2 Integration Studio](https://wso2.com/micro-integrator/) project|
    | **Project Directory** | `hello-world-mi` |

8. Click **Create**. Choreo initializes the component with the sample integration.

## Step 2: Deploy the integration component

To deploy the integration component to the development environment, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. In the **Configurations** pane, click **Next**. This displays details of the endpoint ready to be deployed.
4. Click **Deploy**. This deploys the integration component to the development environment.
The **Development** card indicates the **Deployment Status** as **Active** when the integration is successfully deployed.

    !!! tip
         Choreo uses [endpoints](../../choreo-concepts/endpoint.md) to expose **Service** component to the network. You can read more about configuring endpoints in [Configure Endoints](../configure-endpoints.md).

    !!! tip
         Automatic deployment is enabled for the component by default. Therefore, you are required to perform only the first deployment manually.

Now you can test the integration.

## Step 3: Test the integration

Choreo allows you to test your integration using either the integrated OpenAPI Console or Postman.

In this guide, you will test the integration using the OpenAPI Console. Follow the steps given below:

!!! note
      - For instructions on how to test using Postman, see [Test with Postman](../../testing/test-apis-using-postman.md).

1. In the Choreo Console left navigation menu, click **Test** and then click **Console**.
2. In the OpenAPI Console that opens, select **Development** from the environment drop-down list.
3. Click to expand the **GET /integration** operation.
4. Click **Try it out** and then click **Execute**. This sends a request to your deployed integration.
5. Go to the **Response body** section and observe the response returned by the integration. If the integration works as expected, you should see a response similar to the following:

    `{"Hello" : "Integration"}`

## Step 4: Observe the integration

The observability view in Choreo displays graphs that depict details such as throughput, latency, diagnostic data, and logs to identify and troubleshoot anomalies in components you deploy.

To visualize and monitor the performance of the integration component you deployed, click **Observability** in the left navigation menu. You can observe the following:

 - The throughput and latencies of requests served over a given period.
 - The logs that are generated over a given period.
 - The flame graph (Diagnostics View) that is generated over a given period.
  
To learn more about the observability details you can view via Choreo observability, see [Observability Overview](../../monitoring-and-insights/observability-overview.md).

## Step 5: Publish the integration component

To publish the integration component, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Manage** and then click **Lifecycle**. This opens the **Lifecycle Management** pane, where you can see the different lifecycle stages that an API can be in. You can see that the current lifecycle stage is **Created**.
2. In the **Lifecycle Management** pane, click **Publish**. This changes the API lifecycle stage to **Published** and exposes the integration as an API in the Choreo Developer Portal. 
   
   To open the published API in the Developer Portal via the **Lifecycle Management** pane, click **Go to Devportal**. In the Choreo Developer Portal, you can view the published API, manage subscriptions for it, and generate access tokens for testing purposes.

Now, you have gained hands-on experience in exposing an integration designed using WSO2 Integration Studio as a REST API in Choreo.
