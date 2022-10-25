# Quick Start Guide

Choreo is a cloud-native low-code engineering platform that allows you to create APIs, API Proxies, triggers, and scheduled tasks to implement your integration solution. 

!!! tip
        Take a look at the [Choreo playlist](https://youtube.com/playlist?list=PLdjnDnxI3SA85otCxxvyGBJp5Bc19NpaS) for video tutorials that guide you through the steps to create, publish, deploy, consume, and observe your APIs. 

This quick start guide walks you through the steps to create, develop, publish, deploy, and invoke a REST API using Choreo. 

## Create your first REST API 

### Step 1: Create a REST API

First, let's create a new REST API:

1. Sign in to the Choreo Console at [https://console.choreo.dev](https://console.choreo.dev).

    You will view the **Home** page.

2. In the **Create from a Sample** section, click **Echo Service**. 

Choreo starts creating an API using the template in this sample.
 
Once Choreo completes adding the API, you can see the overview of this API.

Next, let's deploy this API and make it invokable.
 
### Step 2: Deploy the REST API 

Choreo maintains two environments by default: development and production. Let's deploy the API to the development environment.

1. Click **Deploy** in the left navigation menu. 

2. Go to the **Build Area** card and click **Build and Deploy**.

    ![Deploy API](../assets/img/get-started/deploy-rest-api.png){.cInlineImage-full}

   This deploys the API to the development environment to make it invokable. 

Now let's test the REST API to verify whether it works as expected.

### Step 3: Test the REST API 

To test the REST API, follow these sub-steps:

1. Click **Test** in the left pane.

2. Expand the **POST** method with the **/** resource path. 
    ![POST method](../assets/img/get-started/post-method.png){.cInlineImage-full}

3. Click **Try it out**.

4. Add a meaningful string as the request body. For example, `Welcome to Choreo`.
    ![Request Payload](../assets/img/get-started/request-payload.png){.cInlineImage-full}

5. Click **Execute**. You'll see the string you entered in step 4 above returned as the server response.

Congratulations! You have now tested the REST API and are ready to implement the business logic.

## Implement the business logic

Developing an API involves writing the business logic for it. APIs often need to integrate with third-party systems to create a comprehensive integration solution. With Choreo, you can create your integration solution easily by implementing the business logic using its low-code editor, pro-code editor, or both.

### Step 1: Develop in low-code

Follow this procedure to further develop the API by adding a new resource and simple business logic:

1. Click **Overview** in the left navigation menu.

2. Click **Edit Code**.
 
    !!! info
        The Web Editor can take some time to open if you are a first-time user.
 
    ![Edit with VS Code Online](../assets/img/get-started/edit-with-vs-code.png){.cInlineImage-full}

3. Click **+** below the low-code diagram to add a new resource and configure it as follows: 
  
    ![Add New Resource](../assets/img/get-started/add-new-resource.png){.cInlineImage-half}

     | **Field** | **Value** |
     |-----------------|-----------------------|
     | **HTTP Method** | `GET` |
     | **Resource Path** | `personalizedMessage` |
     | **Return Type** | `string | error` |
     

4. Click **+ Add Parameter** to add a query parameter.  Enter the following values to get a name as input:

     | **Field** | **Value** |
     |-----------------|-----------------------|
     | **Type** | `string` |
     | **Param Name** | `name` |

     ![Configure Query Parameter](../assets/img/get-started/configure-query-param.png){.cInlineImage-full}


 5. Click **Save** to save the query parameter, and then click **Save** again to save the resource.

 6. In the low-code diagram, click **+** after **Start**.

    ![Create Variable](../assets/img/get-started/create-variable.png){.cInlineImage-full}

     In the **Add Constructs** pane, click **Variable**.

 7. Update the given variable statement syntax (i.e., **`var variable = <add-expression>`**) to generate a personalized message by following these steps:

    1. To generate the message in string format, click **`var`**, and click **string**.

        ![Change variable type](../assets/img/get-started/change-variable-type.png){.cInlineImage-half}
    
    2. Double-click **`variable`** which is the default variable name to edit it. Enter `message` as the new variable name.
    
    3. To generate the personalized message, double-click **`<add-expression>`** and enter the following expression:

        ```
         "Hello "+ name
        ```
       
        Now the variable statement is updated as follows:
    
        ```
        string message = "Hello "+ name;
        ```

 8. Click **Save**.

 9. To return the personalized message, click **+** below the variable you created and click **Return**. 

     ![Add Return Type](../assets/img/get-started/add-return-type.png){.cInlineImage-full}
 
     In the **Return** pane, the return statement syntax is displayed as **`return <add-expression>`**.

     Click **`<add-expression`**, and click **message** in the list of suggestions that appear below the return statement syntax.

      ![Select return expression](../assets/img/get-started/select-return-expression.png){.cInlineImage-half}
   
 10. Click **Save**.

!!!info
    You can click the **</>** icon on the top left corner of the editor if you want to work in the code view. Depending on your expertise, you can work in the low-code view or the pro-code view. You can also work on both views simultaneously.    
    ![Editor View](../assets/img/get-started/low-code-pro-code-side-by-side.png){.cInlineImage-full}

### Step 2: Run and test the REST API

Once you update the REST API configuration as described above, you can verify whether it works as expected by following these steps:

1. Click the menu icon for the listener construct and then click **Run**.

    ![Run the REST API](../assets/img/get-started/run-the-rest-api.png){.cInlineImage-half}

    A terminal opens with logs that indicate the status of the REST API. If you updated the REST API as instructed above, you will see the following log:

    ```
    Running executable
    ```
    
2. To invoke the REST API, click the menu icon for the listener construct and then click **Try it**.
   
    ![Try REST API](../assets/img/get-started/try-rest-api.png){.cInlineImage-half}

    This opens the Swagger view with details of your REST API.

3. Expand the GET resource and click **Try it out**.

    ![Try out REST API](../assets/img/get-started/try-out-rest-api.png){.cInlineImage-full}

4. In the **name** field, enter any value (for example, `Alice`).

5. Click **Execute**.

    The Swagger view displays the response under **Response Body** as shown in the image below:

    ![REST API response](../assets/img/get-started/rest-api-response.png){.cInlineImage-half}

### Step 3: Commit changes to GitHub

Choreo maintains source code in a private GitHub repository. When you create a component, Choreo creates it in the private GitHub repository associated with your account. Therefore, you must commit and push your changes if you want to change the API Implementation.

Follow this procedure to commit and push your changes to GitHub.

1. On the bottom bar of the editor, click **Sync with Choreo upstream**.  
    ![Sync Choreo With upstream](../assets/img/get-started/sync-with-choreo-upstream.png){.cInlineImage-full}

    Alternatively, you can click source control on the left pane or click **Sync changes with Choreo** in the pop-up displayed at the bottom right corner. 

    ![Alternative Commit](../assets/img/get-started/alternative-commit.png){.cInlineImage-full}

2. Click **Sync my changes with Choreo** in the window displayed. 

     ![Sync Changes](../assets/img/get-started/sync-changes.png){.cInlineImage-full}

3. Enter a meaningful commit message and click **✓** on the toolbar. This commits the changes to the locally cloned copy of your private GitHub repository.

     ![Commit](../assets/img/get-started/commit-message.png){.cInlineImage-full}

     Select Yes in the message that appears to specify that you need the changes to be staged.

4. On the top toolbar, click **...** and then click **Push**. 

    ![Push changes](../assets/img/get-started/push-changes.png){.cInlineImage-full}

    Click **OK** in the message that appears to confirm that you want to push the changes.

    This pushes the local changes to your private GitHub repository.

!!! tip
    If you want to redeploy new changes, see [Deploy Your REST API](#deploy-your-rest-api). 

 Now let's publish the API and invoke it via the API Developer Portal. 

## Manage your REST API

Choreo provides full API management capabilities so that you can manage your REST API lifecycle, apply security, apply rate-limiting policies, configure usage plans, and add documentation.

### Step 1: Publish the API

Follow this procedure to publish the API to the API Developer Portal so that external consumers can view and consume the API:

1. Click **Manage** in the left pane.

2. On the **Lifecycle Management** page, click **Publish**.

3. Click **Go to Devportal**. 
 
    ![Go to API Developer Portal](../assets/img/get-started/go-to-dev-portal.png){.cInlineImage-full}
 
    You will see the published API:
 
    ![List of published APIs in the API Developer Portal](../assets/img/get-started/developer-portal.png){.cInlineImage-full}

### Step 2: Invoke the API 
Follow this procedure to invoke the REST API via the API Developer Portal:

1. Click **Credentials** in the left pane of the API Developer Portal.

2. Click **Generate Credentials**. Choreo creates credentials for an internal application and subscribes this API to that internal application.

     ![Generate Credentials](../assets/img/get-started/generate-credentials.png){.cInlineImage-full}

3. Click **Generate Access Token**, copy the generated access token, and click **Close**.

     ![Generate Access Token](../assets/img/get-started/generate-access-token.png){.cInlineImage-full}

4. Click **Try Out** in the left pane. 

5. Paste the copied access token in the **Access Token** field.
     ![Add access token ](../assets/img/get-started/add-access-token.png){.cInlineImage-full}

6. Click the `GET` resource you added to expand it.

7. Click **Try it out** and enter a name for the query parameter.
     ![Invoke API ](../assets/img/get-started/invoke-api.png){.cInlineImage-full} 

8. Click **Execute**. You'll see the personalized message as the response from the API.

Congratulations! You have successfully created a REST API, published it on the API Developer Portal, and invoked it via the API Developer Portal.
