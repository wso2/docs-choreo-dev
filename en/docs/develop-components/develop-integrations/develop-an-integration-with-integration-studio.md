# Develop an Integration with Integration Studio

The seamless integration of APIs, microservices, applications, and data across different languages and formats requires the ability to expose integrations as APIs. Choreo iPaaS simplifies building, deploying, and managing integration components, making it easy for you to quickly expose integrations as APIs.

This guide walks you through the steps to expose an integration you created in WSO2 Integration Studio as an API in Choreo. 

In this guide, you will:

  - Create a component to expose the sample integration in the [Choreo examples repository](https://github.com/wso2/choreo-examples) as an API. 
  - Deploy, test, and observe the integration component.
  - Publish the integration component as an API to the Choreo Developer Portal.

## Prerequisites

Before you try out the steps in this guide, complete the following:

 - If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.

 - Fork the [Choreo examples repository](https://github.com/wso2/choreo-examples), which contains the sample integration for this guide.
 - Download and install [WSO2 Integration Studio](https://wso2.com/micro-integrator/) locally.

## Step 1: Create the integration component

1. Sign in to [Choreo iPaaS](https://console.choreo.dev/ipaas)<https://console.choreo.dev/ipaas>. This opens the **Project Home** page in the Choreo Console. 
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **+Integration as an API** card and click **Create**.
3. Enter a unique name and a description for the component. For example, you can enter the name and the description given below:

    | **Field**       | **Value**              |
    |-----------------|------------------------|
    | **Name**        | `Hello World`          |
    | **Description** | `Hello World REST API` |

4. Select **External** as the **Access Mode**, and click **Next**.
5. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
6. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


7. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | The repository you created by following the steps in the prerequisites section |
    | **Branch**            | **`main`**                               |
    | **Build Preset**      | Click **WSO2 MI** because you are creating the REST API from a [WSO2 Integration Studio](https://wso2.com/micro-integrator/) project|
    | **Access Mode**       | To make the REST API publicly accessible, leave the selection unchanged as **External**.  |
    | **Project Path**      | `ipaas/micro-integrator/hello-world-api` |
    | **OpenAPI File Path** | `ipaas/micro-integrator/hello-world-api/openapi.yaml` |

8. Click **Create**. Choreo initializes the component with the sample integration.

## Step 2: Deploy the integration component

To deploy the integration component to the development environment, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. In the **Configure & Deploy** pane, click **Deploy**. The **Development** card indicates the **Deployment Status** as **Active** when the integration is successfully deployed.

    !!! tip
         Automatic deployment is enabled for the component by default. Therefore, you are required to perform only the first deployment manually.

Now you can test the integration.

## Step 3: Test the integration

Choreo allows you to test your integration using either the integrated OpenAPI Console, cURL, or Postman.

In this guide, you will test the integration using the OpenAPI Console. Follow the steps given below:

!!! note
      - For instructions on how to test using cURL, see [Test with cURL](../testing/test-apis-with-curl.md).
      - For instructions on how to test using Postman, see [Test with Postman](../testing/test-apis-using-postman.md).

1. In the Choreo Console left navigation menu, click **Test**.
2. In the **OpenAPI Console** pane that opens, select **Development** from the environment drop-down list.
3. Click to expand the **GET /HelloWorld** operation.
4. Click **Try it out** and then click **Execute**. This sends a request to your deployed integration.
5. Go to the **Response body** section and observe the response returned by the integration. If the integration works as expected, you should see a response similar to the following:

    `{"Hello":"Integration"}`

## Step 4: Observe the integration

The observability view in Choreo displays graphs that depict details such as throughput, latency, diagnostic data, and logs to identify and troubleshoot anomalies in components you deploy.

To visualize and monitor the performance of the integration component you deployed, click **Observe** in the left navigation menu. You can observe the following:

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
