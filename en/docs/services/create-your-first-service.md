# Create Your First Choreo Service

To learn how to create a Choreo service, let's try a simple scenario. In this example, you are creating a service that consumes a public API that fetches the current active COVID-19 cases in a specified country, testing it, deploying it, and then observing its performance.

## Step 1: Create the Choreo service

To create the Choreo service, follow the procedure below: 

1. Access the Choreo Development Console via the following URL.

    `https://console.choreo.dev/`
    
    Sign in using either your Google or GitHub credentials.
    
2. In the **Services** page, click **Create**.

3. Under **Create with Choreo**, enter `covid-stats` as the name of your Choreo service.

4. In the **Configure API Trigger** form, select **GET** as the HTTP method, and enter `activecases` as the relative path from host.

    ![Resource Configuration](/assets/img/services/configure-api-trigger.png)
    
    Then click **Save API**. Now you have saved the API trigger that starts your application.
    
5. In the next form that appears, do as follows to configure the HTTP connection that connects the public API used in this scenario to the API trigger you configured.

    1. Click **Connections**.

    2. In the **New HTTP Connection** form, enter `https://api.covid19api.com` as the URL and select **GET** as the operation.
    
        ![New HTTP Connection](/assets/img/services/new-http-connection.png)
        
        Then click **Save & Next**.
        
    3. In the **Select Payload Type** field, select **JSON**.
    
        ![Select Payload Type](/assets/img/services/select-payload-type.png)
        
    4. Open the code view of the application by clicking the **<>** icon to the top right of the page.
    
        Update the `http:Response getResponse = <http:Response>check httpEndpoint->get("/");` line as `http:Response getResponse = <http:Response>check httpEndpoint->get("/total/country/united-states");`. This specifies that the GET statement in the HTTP connection invokes the public API to fetch the current number of active COVID-19 in the United States.
        
    5. Click **Save & Done**. Now your API trigger is connected to the public API that fetches the active COVID-19 cases in the selected country.
    
6. To cast all the data retrieved via the API connected to the application before further processing, add a new custom statement as follows:

    1. Click on the **+** icon below the JSON payload. 

        ![Add New Statement](/assets/img/services/add-new-statement.png)
    
    2. Then click the **+** icon again to select the new statement you need to add.
    
    3. In the form that appears, click **Custom Statement**.

    4. In the **Custom Statement** form that appears, enter the following two lines as the statement.
    
        ```ballerina
        json[] jsonArray = <json[]>jsonPayload;
        json[] response = [];
        ```

        ![Add Custom Statement](/assets/img/services/custom-statement.png)
    
    5. Then click **Save**.
    
7. To filter the dates on which the active cases have exceeded 50,000, let's add a statement of the `ForEach` type as follows: 

    1. Click the last **+** icon that is visible in the low code view of the service in its current state.

        ![Add Custom Statement](/assets/img/services/add-to-the-service.png)
    
    2. In the form that appears, click **ForEach**.

    3. In the **ForEach** form, enter `jsonArray` as the iterable expression.

        ![Add ForEach Statement](/assets/img/services/add-foreach-statement.png)
    
        Then click **Save**.
    
    4. To apply the filter mentioned, add a custom statement within the ForEach statement. To do this, click the **+** icon just below the last ForEach statement you added.

        ![Add Custom Statement After Foreach Statement](/assets/img/services/add-custom-statement-after-foreach-statement.png)
    
    5. In the form that appears, click **Custom Statement**. Then enter the following as the statement.

        ```ballerina
        int active = <int>(check item.Active);
        if (active > 5000) {
            response.push(item);
        }
        ```
        
        Then click **Save**.
    
8. To add a statement of the `Respond` type so that the result of the above processing is returned as a response, click the last **+** icon that is visible in the low code view of the service in its current state.

    ![Add Respond Statement](/assets/img/services/add-respond-statement.png)
    
    In the **Respond** form that appears, enter the following as the respond expression.

    ```ballerina
    response
    ```
    
    ![Respond Expression](/assets/img/services/respond-expression.png)
    
Now you have completed designing your Choreo application. It looks as follows.

- In the Low Code View

    ![Low Code View](/assets/img/services/choreo-service-low-code-view.png)

- In the Code View

    ![Low Code View](/assets/img/services/choreo-service-code-view.png)

    
To validate the Choreo application, click **Run & Test**. The following is logged to indicate that you have successfully started the service.

![Service Started Log](/assets/img/services/service-started-notification.png)
    
Congratulations! You have successfully created your first Choreo service.
   
## Step 2: Test the Choreo service

To test the `covid-stats` Choreo application you created, follow the procedure below:

1. Click the **Test** icon in the left pane.

    ![Test Icon](/assets/img/services/test-icon.png)

2. In the section that opens to the right of the page, click **GET**.

3. Click **Try it out**, and then click **Execute**.

The search results for COVID-19 statistics of United States are displayed as the server response as shown below.

![Server Response](/assets/img/services/server-response.png)

Now you have verified that the `covid-stats` service works as expected. Therefore, you can deploy it.

## Step 3: Deploy the Choreo service

To deploy the `covid-stats` service, follow the procedure below:

1. Click the **Go Live** icon in the left pane.

    ![Test Icon](/assets/img/services/deploy-icon.png)

2. To deploy the service, click **Deploy**.

    The following message appears to indicate that you have successfully deployed the service.

    ![Successfully Deployed Notification](/assets/img/services/successfully-deployed-notification.png)

3. Once the service is deployed, click on the **Go Live** tab below the low code design view. Then click **://cURL** and copy the CURL command that is displayed.

    ![Get cURL command](/assets/img/services/copy-curl-command.png)
    
    Invoke the `covid-stats` service by issuing a few CURL commands using the Postman application.
    
    The responses are logged as shown below.
    
    ![covid-stats-log](/assets/img/services/covid-stats-log.png)

    In the Choreo Development Console, **Go Live** tab, the requests are logged as follows.
    
    ![Execution History](/assets/img/services/execution-history.png)
    
To observe the `covid-stats` service by checking the statistics generated as a result of the CURL commands you issued, proceed to Step 4. 

## Step 4: Observe the Choreo service.

To observe the `covid-stats` service, click the **Observe** icon in the left panel.
![Test Icon](/assets/img/services/observe-icon.png)

The throughput and the latency of the `covid-stats` service are visualized as follows:
![Visualization of Throughput and Latency](/assets/img/services/successfully-deployed-notification.png)
