# Quick Start Guide

Choreo is a cloud-native low-code engineering platform that allows you to create APIs, API Proxies, triggers, and scheduled tasks to implement your integration solution. 

This quick start guide walks you through the steps to create, develop, publish, deploy, and invoke a REST API using Choreo. 

## Create your first REST API 

### Step 1: Create a REST API

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. On the **Home** page, scroll to the **Use a sample and get started** section. 

3. Go to the **Echo Service** card and click **Get Started**.
 
Now you have successfully created an API using a template and you can see the flow diagram of the integration logic implemented for this API.

Next, let's deploy this API and make it invokable.
 
### Step 2: Deploy the REST API 

Choreo maintains 2 environments by default: development and production. Let's deploy the API to the development environment.

1. Click **Deploy** in the left pane. 

2. Go to the **Build Area** card and click **Deploy**.

    ![Deploy API](../assets/img/get-started/deploy-rest-api.png){.cInlineImage-full}

   This deploys the API to the development environment to make it invokable. 

Now let's test the REST API to verify whether it works as expected.

### Step 3: Test the REST API 

1. Click **Test** in the left pane.

2. Expand the **POST** method with the **/** resource path. 
    ![POST method](../assets/img/get-started/post-method.png){.cInlineImage-full}

3. Click **Try it out**.

4. Add a meaningful string as the request body. For example, `Welcome to Choreo`.
    ![Request Payload](../assets/img/get-started/request-payload.png){.cInlineImage-full}

5. Click **Execute**. You'll see the string you entered in step 4 above displayed as the server response.

Congratulations! You have now tested the REST API and are ready to implement the business logic.

## Implement the business logic

Developing the API involves writing the business logic for the API. APIs often need to integrate with third-party systems to create a comprehensive integration solution. With Choreo, you can create your integration solution easily using its low-code editor, pro-code editor, or both to publish your API at a faster time-to-market.

In this guide, you will further develop the API by adding a new resource and simple business logic to it:

### Step 1: Develop in low-code

1. Click **Develop** in the left pane.

2. Click **Edit with VS Code Online**.
 
    !!! info
        The VS Code Editor can take some time to open if you are a first-time user.
 
    ![Edit with VS Code Online](../assets/img/get-started/edit-with-vs-code.png){.cInlineImage-full}

3. Click the **+** icon on the low-code diagram, click **Resource**, and configure the resource as follows: 
  
    ![Add New Resource](../assets/img/get-started/add-new-resource.png){.cInlineImage-full}

     | **Field** | **Value** |
     |-----------------|-----------------------|
     | **HTTP Method** | `GET` |
     | **Path** | `personalizedMessage` |
     | **Return Type** | `string` |


4. Click **Advanced** and then click **+ Add Query Param** to add a query parameter with the following values to get a name as an input:

     | **Field** | **Value** |
     |-----------------|-----------------------|
     | **Type** | `string` |
     | **Value** | `name` |

     ![Configure Query Parameter](../assets/img/get-started/configure-query-param.png){.cInlineImage-full}


 5. Click **Save**.

 6. Expand the **GET personalizedMessage** resource you created. This displays the low-code diagram.

 7. Click **Variable** and configure it as follows: 
    ![Create Variable](../assets/img/get-started/create-variable.png){.cInlineImage-full}

     | **Field** | **Value** |
     |-----------------|-----------------------|
     | **Type** | `string` |
     | **name** | `message` |
     | **string** |`"Hello "+ name`|

     ![Configure Variable](../assets/img/get-started/configure-variable.png){.cInlineImage-full}

8. Click **Save**.

9. To return the personalized message, click the **+** icon below the variable you created and click **Return**.
   ![Add Return Type](../assets/img/get-started/add-return-type.png){.cInlineImage-full}

   ![Select Return](../assets/img/get-started/select-return.png){.cInlineImage-full}
   
10. Enter `message` as the **Return Expression** value and click **Save**.

!!!info
    You can click the **</>** icon on the top left corner of the editor if you want to work in the code view. Depending on your expertise, you can choose to work in the low-code or pro-code view or both views simultaneously.  
    ![Editor View](../assets/img/get-started/low-code-pro-code-side-by-side.png){.cInlineImage-full}

### Step 2: Commit Changes to GitHub

Choreo maintains source code in a private GitHub repository. When you create a component in Choreo, it creates it in the private-GitHub repository associated with your account. Therefore, when you change the API Implementation, you need to commit and push your changes. 

1. Click on the **Sync with Choreo upstream** link at the bottom of the editor. 
    ![Sync Choreo With upstream](../assets/img/get-started/sync-with-choreo-upstream.png){.cInlineImage-full}

    Alternatively, you can click on the source control button on the left navigation or click on the pop-up that appears at the bottom right corner. 

    ![Alternative Commit](../assets/img/get-started/alternative-commit.png){.cInlineImage-full}

2. Click **Sync my changes with Choreo** in the pop-up that appears. 

     ![Sync Changes](../assets/img/get-started/sync-changes.png){.cInlineImage-full}

3. Enter a meaningful commit message and click on the tick sign from the toolbar. This will commit your changes to your private GitHub repository.

     ![Commit](../assets/img/get-started/commit-message.png){.cInlineImage-full}

4. Next, you need to push the changes to GitHub. Click on the **...** on the top toolbar and select **Push** from the list. 

!!! tip
    You need to re-deploy the new changes. Follow steps in section [Deploy Your REST API](#deploy-your-rest-api). 

 Next, let's publish the API 

## Manage your REST API

Choreo provides you with full API management capabilities. You can manage your REST API's lifecycle, apply security, apply rate-limiting policies, configure usage plans, and add documentation.

In this guide, we will publish and invoke the API we created. 

### Step 1: Publish the API
Follow this procedure to publish the API to the API Developer Portal so that external consumers can view and consume the API:

1. Click **Publish** in the left pane.

2. On the **Lifecycle Management** page, click **Publish**.

3. Click **Go to Developer Portal**. 
    ![Go to API Developer Portal](../assets/img/get-started/go-to-dev-portal.png){.cInlineImage-full}
 
    You can see the published API in the API Developer Portal:
 
    ![List of published APIs in the API Developer Portal](../assets/img/get-started/developer-portal.png){.cInlineImage-full}

### Step 2: Invoke the API 
Follow this procedure to invoke the REST API from the API Developer Portal:

1. Click **Credentials** in the left pane.

2. Click **Generate Credentials**. Choreo creates credentials for an internal application and subscribes this API to that internal application.
     ![Generate Credentials](../assets/img/get-started/generate-credentials.png){.cInlineImage-full}


2. Click **Generate Access Token**. Copy the generated access token.

     ![Generate Access Token](../assets/img/get-started/generate-access-token.png){.cInlineImage-full}

3. Click **Try Out** in the left pane. 

4. Paste the copied access token in the **Access Token** field.
     ![Get Test Key ](../assets/img/get-started/get-test-key.png){.cInlineImage-full}

5. Click the `GET` resource you added to expand it.

6. Click **Try it out** and add a name for the query parameter.
     ![Invoke API ](../assets/img/get-started/invoke-api.png){.cInlineImage-full} 

7. Click **Execute**. You can see the personalized message response from the API.

Congratulations! You have successfully created a REST API, published it on the API Developer Portal, and finally, invoked the API via the API Developer Portal.
