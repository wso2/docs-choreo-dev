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

    ![Resource Configuration](/en/assets/img/services/configure-api-trigger.png)
    
    Then click **Save API**. Now you have saved the API trigger that starts your application.
    
5. Click **API Calls**, and then enter information as follows to configure the HTTP connection that connects the public API used in this scenario to the API trigger you configured.

    1. Click **HTTP**.
    
        ![Select HTTP Connection](/en/assets/img/services/select-http-connection.png)
        
    2. Enter information as follows:
    
        | **Field**           | **Value**                    |
        |---------------------|------------------------------|
        | **Connection Name** | `httpEndpoint`               |
        | **URL**             | `https://api.covid19api.com` |
        
        Click **Save**.
        
    3. Click the **+** icon below the HTTP API call you added. 
    
        ![Update Existing Connection](/en/assets/img/services/update-existing-connection.png)
        
    4. Click **API Calls**, and then click on the existing connection.
    
        ![Select Existing connection](/en/assets/img/services/select-existing-connection.png)
        
        Then enter information as follows:
        
        ![Configure HTTP Connection](/en/assets/img/services/select-existing-connection.png)
        
        | **Field**               | **Value**                      |
        |-------------------------|--------------------------------|
        | **OPERATION**           | **get**                        |
        | **Resource Path**       | `/total/country/united-states` |
        | **Select Payload Type** | **JSON**                       |
        
        Click **Save & Done**.
        
        Now your API trigger is connected to the public API that fetches the active COVID-19 cases in the selected country.
    
6. To cast all the data retrieved via the API connected to the application before further processing, add a new custom statement as follows:

    1. Click the last **+** icon in your low code diagram.

        ![Add New Statement](/en/assets/img/services/add-custom-statement.png)
    
    2. Click **Other** and then enter the following in the **Statement** field.
    
    
        ```
        json[] jsonArray = <json[]>jsonPayload;
        json[] response = [];
        ```
        Then click **Save**.
    
7. To filter the dates on which the active cases have exceeded 50,000, let's add a statement of the `ForEach` type as follows: 

    1. Click the last **+** icon in your low code diagram.
    
    2. Click **ForEach**.

    3. In the **Iterable Expression** field, enter `jsonArray`. Then click **Save**.
    
    4. To apply the filter mentioned, add a custom statement within the ForEach statement. To do this, click the **+** icon just below the last ForEach statement you added.

        ![Add Custom Statement After Foreach Statement](/en/assets/img/services/add-custom-statement-after-foreach-statement.png)
    
    5. In the form that appears, click **Other**. Then enter the following in the **Statement** field.

        ```ballerina
        int active = <int>(check item.Active);
        if (active > 5000) {
            response.push(item);
        }
        ```
        
        Then click **Save**.
    
8. To add a statement of the `Respond` type so that the result of the above processing is returned as a response, click the last **+** icon in your low code diagram.

    ![Add Respond Statement](/en/assets/img/services/add-respond-statement.png)
    
    In the **Respond Expression** field, enter `response`, and then click **Save**.

    
Now you have completed designing your Choreo application. It looks as follows.

- In the Low Code View

    ![Low Code View](/en/assets/img/services/choreo-service-low-code-view.png)

- In the Code View

    ![Low Code View](/en/assets/img/services/choreo-service-code-view.png)

    
To validate the Choreo application, click **Run & Test**. The following is logged to indicate that you have successfully started the service.

![Service Started Log](/en/assets/img/services/service-started-notification.png)
    
Congratulations! You have successfully created your first Choreo service.
   
## Step 2: Test the Choreo service

To test the `covid-stats` Choreo application you created, follow the procedure below:

1. Click the **Test** icon in the left pane.

    ![Test Icon](/en/assets/img/services/test-icon.png)

2. In the section that opens to the right of the page, click **GET**.

3. Click **Try it out**, and then click **Execute**.

The search results for COVID-19 statistics of United States are displayed as the server response as shown below.

![Server Response](/en/assets/img/services/server-response.png)

Now you have verified that the `covid-stats` service works as expected. Therefore, you can deploy it.

## Step 3: Deploy the Choreo service

To deploy the `covid-stats` service, follow the procedure below:

1. Click the **Go Live** icon in the left pane.

    ![Test Icon](/en/assets/img/services/deploy-icon.png)

2. To deploy the service, click **Deploy**.

    The following message appears to indicate that you have successfully deployed the service.

    ![Successfully Deployed Notification](/en/assets/img/services/successfully-deployed-notification.png)

3. Once the service is deployed, click on the **Go Live** tab below the low code design view. Then click **://cURL** and copy the cURL command that is displayed.

    ![Get cURL command](/en/assets/img/services/copy-curl-command.png)
    
    Invoke the `covid-stats` service by issuing a few cURL commands using the Postman application.
    
    The responses are logged as shown below.
    
    ![covid-stats-log](/en/assets/img/services/covid-stats-log.png)

    In the Choreo Development Console, **Go Live** tab, the requests are logged as follows.
    
    ![Execution History](/en/assets/img/services/execution-history.png)
    
To observe the `covid-stats` service by checking the statistics generated as a result of the cURL commands you issued, proceed to Step 4. 

## Step 4: Observe the Choreo service.

To observe the `covid-stats` service, click the **Observe** icon in the left panel.
![Test Icon](/en/assets/img/services/observe-icon.png)

The throughput and the latency of the `covid-stats` service are visualized as follows:
![Visualization of Throughput and Latency](/en/assets/img/services/successfully-deployed-notification.png)
