# Create and Publish Your First API

Choreo’s API management capabilities allow you to create, publish, and manage all aspects of an API and its lifecycle.
This quick start guide walks you through the steps to quickly create and publish a REST API using Choreo. 

## Prerequisites
- A mock REST service to use when creating the API. 
    
    !!!note
        In this quick start guide, let's use the mock service at [http://www.mocky.io/v2/5185415ba171ea3a00704eed](http://www.mocky.io/v2/5185415ba171ea3a00704eed), which is made available by [https://www.mocky.io/](https://www.mocky.io/). Click the mock service URL to test the service. You'll see the following JSON message: `{"hello": "world"}`.
 
## Step 1: Create an API from scratch
Follow this procedure to create an API using the mock REST service you tested in the prerequisites section:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).
2. Go to the **APIs** card and click **Get Started**. This takes you to the **Create API** page. 
    
    !!!note
        If this is not the first API you are creating, you must click **+ Create** to go to the **Create API** page.

3. On the **Create API** page, go to the **Start from scratch card** and click **Next**.
4. In the **Create API from REST API** form, enter the details as follows:
    
    | **Field**        | **Value**                                        |
    |------------------|--------------------------------------------------|
    | **API Name**     | `HelloWorld`                                     |
    | **API Version**  | `1.0.0`                                          |
    | **Endpoint**     | `http://www.mocky.io/v2/5185415ba171ea3a00704eed`|

5. Click **Create**. This displays an overview of the API.

    ![API overview](../assets/img/apis/api-overview.png){.cInlineImage-full}
       
6. Under **API Configuration**, click **Resources**. Then follow this procedure to add a resource to the API:
    1. Select `GET` as the HTTP verb.
    2. Enter `hello` as the URI pattern.
    3. Click the **+** icon to add the resource.
    4. Click **Save**. You can see a confirmation message to proceed with the changes.
    5. Click **Save** to proceed.

Now you have created the API and added a GET resource to it.

## Step 2: Deploy the API
Follow this procedure to create a revision of the API and deploy it so that you can try it out:

1. Click **Deploy** in the left pane.
2. On the **Deployments** page, click **Create Revision and Deploy**.
3. In the **Create Revision and Deploy** form, enter `Demo revision` as the description and then click **Deploy**.

Now you are ready to test the deployed API.

## Step 3: Test the API
Follow this procedure to test the API revision that you deployed:

1. Click **Test** in the left pane.
2. Click the `GET` resource you added to expand it.

    ![API GET resource](../assets/img/apis/api-resource.png){.cInlineImage-full}

3. Click **Try it out** and then click **Execute**. You can see `{"hello": "world"}` as the response from the API.

    ![Response received from the service execution](../assets/img/apis/response.png){.cInlineImage-full}

Now you are ready to publish the API to the API Developer Portal so that external consumers can consume the API.

## Step 4: Publish the API
Follow this procedure to publish the API and view the published API on the API Developer Portal:

1. Click **Publish** in the left pane.
2. On the **Lifecycle Management** page, click **Publish**.

    ![Publish the API](../assets/img/apis/publish.png){.cInlineImage-full}

3. Click **Go to Developer Portal**.

    ![Go to API Developer Portal](../assets/img/apis/go-to-devportal.png){.cInlineImage-full}

     You can see the published API in the API Developer Portal.

    ![List of published APIs in the API Developer Portal](../assets/img/apis/devportal.png){.cInlineImage-full}

Congratulations! You have successfully created a REST API and published it on the API Developer Portal.
