# Expose a Service as a Managed API with Choreo API Manager

Choreo API Manager simplifies securely exposing existing services as managed APIs. It also allows you to effectively manage all aspects of an API's lifecycle, security, throttling, and governance so that you can focus more on service development. 

In this quick start guide, you will use Choreo API Manager to expose a service as a REST API proxy and publish it to the Choreo Developer Portal for application developers to consume.

## Prerequisites

Before you try out this guide, complete the following:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your Google, GitHub, or Microsoft account.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.


## Step 1: Create a REST API proxy

To create a REST API proxy, you can either upload an OpenAPI specification or provide an OpenAPI specification URL. In this quick start guide, you will use a URL of a sample OpenAPI specification. 

Follow the steps given below:

1. On the **Project Home** page, go to the **REST API Proxy** card and click **Create**.

2. In the **Create API Proxy** pane that opens, click **Try with sample URL**.

3. Click **Next**.

4.  Update the populated API proxy details with the values given in the following table: 

    |  **Field**    | **Value**                                   |
    |---------------|---------------------------------------------|
    | **Name**      | `Petstore`                                    |
    | **Base Path** | `petstore`                   |
    | **Version**   | `1.0.0`                                     |
    | **Target**    | `https://petstore3.swagger.io/api/v3` |
    |**Access Mode**| `External: API is publicly accessible`      |

5.  Click **Create**.
   
   This takes you to the **Develop** view, where you can see the operations of the REST API proxy.

## Step 2: Deploy the REST API proxy

To deploy the REST API proxy to the development environment, follow the steps given below:

1. In the left navigation menu, click **Deploy**.

2. In the **Build Area** card, click **Configure & Deploy**. This opens the **Configure & Deploy** pane, where you can specify endpoint details depending on your requirement. In this guide, you will proceed with the populated endpoint details.

3. Click **Save and Deploy**. The **Development** card indicates the **Deployment Status** as **Active** when the API proxy is successfully deployed.

Now you are ready to test the REST API proxy.

## Step 3: Test the REST API proxy

You can test the REST API proxy in the development environment before promoting it to production. Choreo provides the following three options to test your REST API proxy:
- OpenAPI Console
- cURL
- Postman

In this guide, you will use the OpenAPI Console.

To test the REST API proxy via the OpenAPI Console, follow the steps given below:

1. In the left navigation menu, click **Test**.
    !!! tip
          Since the REST API proxy is secured when it is deployed, you will need a key to invoke it. Choreo automatically generates a key when you navigate to the **Test** view.

2. In the **OpenAPI Console** pane, select **Development** from the environment drop-down list.
   
3. Expand the `GET /pet/findByStatus` operation and click **Try it Out** to test it.

4. Select **available** as the status and click **Execute**. You will see a response similar to the following:

    ![API proxy response](../assets/img/quick-start-guides/api-proxy-response.png){.cInlineImage-full}

   This indicates that your REST API Proxy is working as expected.

## Step 4: Promote the REST API proxy to production

Once you verify that the REST API proxy is working as expected in the development environment, you can follow the steps given below to promote it to production:

1. In the left navigation menu, click **Deploy**.

2. In the **Development** card, click **Promote**.

3. In the **Configure & Deploy** pane that opens, click **Next**.
    !!! tip
          If you want to specify a different endpoint for your production environment, you can do the change in the **Configure & Deploy** pane.

   The **Production** card indicates the **Deployment Status** as **Active** when the API proxy is successfully deployed to production.

   If you want to verify that the API proxy is working as expected in production, you can go to the **Test** view and test the REST API in the production environment.

   Now that your API is deployed in both development and production environments and can be invoked, the next step is to publish it so that consumers can discover and subscribe to it.

## Step 5: Publish the REST API proxy

To publish the API to the Choreo Developer Portal, follow the steps given below:

1. In the left navigation menu, click **Manage** and then click **Lifecycle**. This opens the **Lifecycle Management** pane, where you can see the different lifecycle stages that an API can be in. You can see that the current lifecycle stage is **Created**.

2. In the **Lifecycle Management** pane, click **Publish**. A message appears where you can specify whether you want to publish a connector for this REST API proxy. Creating a connector for this REST API proxy makes it available in the Marketplace. In this guide, you will not publish a connector for the API.

3. Click **No, Thanks**. 

You can observe that the API lifecycle stage has changed to **Published**. Now the API is available for consumption. API consumers can consume the API via the Choreo Developer Portal.


## Step 6: Invoke the API 

To generate credentials for the published API and to invoke it via the Developer Portal, follow the steps below:

1. In the **Lifecycle Management** pane, click **Go to DevPortal**. This takes you to the Petstore API published to the Choreo Developer Portal.

2. To generate credentials for testing the API, follow the steps given below:

   1. In the Developer Portal left navigation menu, click **Credentials**.
   2. Click **Generate Credentials**. Choreo generates new tokens and populates the **Consumer Key** and **Consumer Secret** fields.

3. To invoke the API, follow the steps given below:

   1. In the Developer Portal left navigation menu, click **Try Out**.
   2. In the **Endpoint** list, select **Development** as the environment to try out the API.
   3. Click **Get Test Key**. This generates an access token.
   4. Expand the `GET /pet/findByStatus` operation and click **Try it out**.
   5. Select **available** as the status and click **Execute**.

Congratulations! You have successfully used Choreo API Manager to create a REST API proxy, deploy, test, and publish it. You have also used the Choreo Developer Portal to generate credentials for the published API and invoke it.
