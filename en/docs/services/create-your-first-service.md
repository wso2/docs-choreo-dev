# Create Your First Choreo Service

To learn how to create a Choreo service, let's try a simple scenario. In this example, you are creating a service that consumes a public API that lists all the universities in a specified country, testing it, deploying it and then observing its performance.

## Step 1: Create the Choreo service

To create the Choreo service, follow the procedure below: 

1. Access the Choreo Development Console via the following URL.

    `https://console.choreo.dev/`
    
    Sign in using either your Google or Github credentials.
    
2. In the **Services** page, click **Create**.

3. Under **Create with Choreo**, enter `FindUniversity` as the name of your Choreo service.

4. In the **Configure API Trigger** form, select **GET** as the HTTP method, and enter `universities` as the relative path from host.

    ![Resource Configuration](assets/img/services/configure-api-trigger.png)
    
    Then click **Save API**. Now you have saved the API trigger that starts your application.
    
5. In the next form that appears, do as follows to configure the HTTP connection that connects the public API used in this scenario to the API trigger you configured.

    1. Click **Connections**.

    2. In the **New HTTP Connection** form, enter `http://universities.hipolabs.com` as the URL and select **Get** as the operation.
    
        ![New HTTP Connection](assets/img/services/new-http-connection.png)
        
        Then click **Save & Next**.
        
    3. In the **Select Payload Type** field, select **JSON**.
    
        ![Select Payload Type](assets/img/services/select-payload-type.png)
        
    4. Open the code view of the application by clicking the **<>** icon to the top right of the page.
    
        Update the `http:Response getResponse = <http:Response>check httpEndpoint->get("/");` line as `http:Response getResponse = <http:Response>check httpEndpoint->get("/search?country=United+States");`. This specifies that the GET statement in the HTTP connection invokes the public API to search for all the universities in United States.
        
    5. Click **Save & Done**. Now your API trigger is connected to the public API that lists all the universities of the selected country.
    
6. To add a new statement, click on the **+** icon below the JSON payload. 

    ![Add New Statement](assets/img/services/add-new-statement.png)
    
    Then click **+** again to select the new statement you need to add.
    
7. In the form that appears, click **Respond**.

8. In the **Respond** form that appears, enter `jsonPayload` as the respond expression.

    ![Add Respond Expression](assets/img/services/add-respond-expression.png)
    
    Then click **Save**.
    
9. To validate the Choreo application, click **Run & Test**. The following is logged to indicate that the service is successfully started.

    ![Service Started Log](assets/img/services/service-started-notification.png)
    
Congratulations! You have successfully created your first Choreo service.
   
## Step 2: Test the Choreo service

To test the `FindUniversity` Choreo application you created, follow the procedure below:

1. Click the **Test** icon in the left pane.

    ![Test Icon](assets/img/services/test-icon.png)

2. In the section that opens to the right of the page, click **GET**.

3. Click **Try it out**, and then click **Execute**.

The search results for universities of United States are displayed as the server response as shown below.

![Server Response](assets/img/services/server-response.png)

Now you have verified that the `FindUniversity` service works as expected. Therefore, you can deploy it.

## Step 3: Deploy the Choreo service

To deploy the `FindUniversity` service, follow the procedure below:

1. Click the **Go Live** icon in the left pane.

    ![Test Icon](assets/img/services/deploy-icon.png)

2. To start the service, click **Start**.

The following message appears to indicate that the service is successfully deployed.

![Successfully Deployed Notification](assets/img/services/successfully-deployed-notification.png)

Now you can observe the performance of the `FindUniversity` service.

## Step 4: Observe the Choreo service.

To observe the `FindUniversity` service, click the **Observe** icon in the left panel.

![Test Icon](assets/img/services/observe-icon.png)

The throughput and the latency of the `FindUniversity` service is visualized as follows:

![Visualization of Throughput and Latency](assets/img/services/successfully-deployed-notification.png)

