# Integrating Services

In this tutorial, you can learn how to integrate multiple services via Choreo. Here, let's consider a simple example where an online shoe store needs an application that captures online orders. For each order, the store also needs to generate a notification for the sales manager as well as update the inventory records.

## Before you begin

The following are required to try out this tutorial:

- A Google account. 
- A Twilio account

## Step 1: Create a service to manage inventory records

In this step, you are creating a service that consumes the orders captured by the `orders` service that you previously created, and then updates the inventory records based on the number of items ordered.

To create this service, follow the procedure below:

1. Access the Choreo Development Console and click **Services**. Then click **Create**.

2. Enter `inventory` as the name and click **Create**.

3. Click **PUT** and then click **Save API**.

    ![Resource Configuration](/assets/img/tutorials/order-service-api-trigger.png)

4. Click **Variable**. Then select/enter information as follows:

    | **Field**         | **Value**                     |
    |-------------------|-------------------------------|
    | **Type**          | **string**                    |     
    | **Name**          | `responseString`              |
    | **Expression**    | `""`                    |

    Click **Save**.
    
5. To add another variable, click the **+** below the last variable statement you added, and then click **+** again. Enter information for the variable as follows:

    ![queryParams Variable](/assets/img/tutorials/inventory-service-add-queryparams-variable.png)

    | **Field**         | **Value**                  |
    |-------------------|----------------------------|
    | **Type**          | **other**                  |     
    | **Other Type**    | `map<string[]>`            |
    | **Name**          | `queryParams`              |
    | **Expression**    | `request.getQueryParams()` |

    Click **Save**.
    
6. To add another variable, click the **+** below the last variable statement you added, and then click **+** again. Enter information for the variable as follows:

    ![inventoryItemId Variable](/assets/img/tutorials/inventory-service-add-inventoryitemid-variable.png)

    | **Field**         | **Value**                    |
    |-------------------|------------------------------|
    | **Type**          | **string**                   |     
    | **Name**          | `inventoryItemId`            |
    | **Expression**    | `queryParams.get("item")[0]` |
    
    Click **Save**.
    
7. To add another variable, click the **+** below the last variable statement you added, and then click **+** again. Enter information for the variable as follows:

    ![quantity Variable](/assets/img/tutorials/inventory-service-add-quantity-variable.png)

     | **Field**         | **Value**                                           |
     |-------------------|-----------------------------------------------------|
     | **Type**          | **int**                                             |     
     | **Name**          | `quantity`                                          |
     | **Expression**    | `check int:fromString(queryParams.get("count")[0])` |  
     
 8. To connect to the Google sheet where inventory records are maintained, add a Google Sheet connection as follows: 
 
    !!! tip "Before you carry out this step:"
        In your Google account, open the Google Drive and create a Google sheet named `inventory` with the following table.
        ![Google Sheet Extract](/assets/img/tutorials/inventory-service-google-sheet-extract.png)
        | **item**    | **count** |
        |-------------|-----------|
        | trainers    | 100       |
        | loafers     | 80        |
        | kittenheels | 60        |
        | maryjanes   | 40        |
            
    1. Click the **+** below the last variable statement you added, and then click **+** again.
    
    2. Click **Connections**, and then click **Google Sheets**
    
    3. If the connection to your Google Account shows in the list, click on it. If not, connect to your Google account by clicking **Add Another Account**.
    
    4. Once you have selected the required Google account, select **getColumn** in the **Operation** field, and then enter information in the rest of the fields as follows:
    
        | **Field**         | **Value**                                                                                                       |
        |-------------------|-----------------------------------------------------------------------------------------------------------------|
        | **SpreadsheetId**x| The ID of your google sheet.                                                                                    |  
        | **SheetName**     | The name of the sheet with the inventory records.                                                               |
        | **Column**        | The ID of column in which you have added values for the `item` variable (i.e., `A` in the given example image). |
        
        Click **Save**. 
        
        This connection retrieves the number of items currently available in the column `A`, `Sheet1` of the `inventory` Google Sheet.
        
 9. To cast the response received, add another variable statement as follows:
 
    1. Click the **+** below the Google Sheet connection you added, and then click **+** again.
    
    2. Click **Variable**, and then add information as follows:
    
        ![respondMessages Variable](/assets/img/tutorials/inventory-service-respondmessages-variable.png)
    
        | **Field**         | **Value**        |
        |-------------------|------------------|
        | **Type**          | **other**        |     
        | **Other Type**    | `string[]`       |
        | **Name**          | `respondMessages`|
        | **Expression**    | `[]`             |
        
        Then click **Save**.
        
 10. To log the response, add a `log` statement as follows:
 
    1. Click the **+** below the `respondMessages` variable statement you added, and then click **+** again.
    
    2. Click **Log**. 
    
    3. In the **Expression** field, enter `"print(itemIDs.toJsonString()"`. Then click **Save**.
    
        ![Log Statement](/assets/img/tutorials/inventory-service-log-statement.png)
    
11. To define a parameter for the inventory record, add a new variable as follows:

    1. Click the **+** below the log statement you added, and then click **+** again.
    
    2. Click **Variable**, and then add information as follows:
    
        ![i Variable Statement](/assets/img/tutorials/inventory-service-i-variable-statement.png)
    
        | **Field**      | **Value** |
        |----------------|-----------|
        | **Type**       | **int**   |     
        | **Name**       | `i`       |
        | **Expression** | `1`       |
        
        Then click **Save**.
        
12. To define a parameter for the inventory index, add a new variable as follows:
    
    1. Click the **+** icon below the last `i` variable statement you added, and then click **+** again.
    
    2. Click **Variable**, and then add information as follows:
    
        ![Index Variable Statement](/assets/img/tutorials/inventory-service-index-variable-statement.png)
    
        | **Field**      | **Value** |
        |----------------|-----------|
        | **Type**       | **int**   |     
        | **Name**       | `index`   |
        | **Expression** | `-1`      |
        
        Then click **Save**.
        
13. To define a parameter for the item ID, add a string variable as follows:   

    1. Click the **+** icon below the last `index` variable statement you added, and then click **+** again.
    
    2. Click **Variable**, and then add information as follows:
    
        ![itemIDString Statement](/assets/img/tutorials/inventory-service-itemidstring-variable-statement.png)   
 
         | **Field**      | **Value**      |
         |----------------|----------------|
         | **Type**       | **string**     |     
         | **Name**       | `itemIDString` |
         | **Expression** | `""`           | 
         
         Then click **Save**.
         
14. Add a `foreach` statement as follows:
   
    1. Click the **+** below the last `itemIDString` variable statement you added, and then click **+** again.
    
    2. Click **ForEach**, and then enter information as follows:
    
        ![Foreach Statement](/assets/img/tutorials/inventory-service-foreach-statement.png)
    
         | **Field**                  | **Value**                          |
         |----------------------------|------------------------------------|
         | **Current Value Variable** | **ItemID**                         |     
         | **Iterable Expression**    | `itemIDString`   |
         
         Then click **Save**.
         
    3. Add a `custom` statement as follows:
     
        1. Click the **+** icon below the `foreach` statement you added, and then click **+** again. Then click **Custom**.
        
            ![Custom Statement](/assets/img/tutorials/inventory-service-add-custom-statement.png)
        
        2. In the **Statement** field, enter `itemIDString = <string>itemID;`.
        
            ![Custom Statement](/assets/img/tutorials/inventory-service-custom-statement-2.png)
        
            Then click **Save**.
            
    4. Add another custom statement as follows:
    
        1. Click the **+** icon below the last custom statement you added, and then click **+** again. 
        
            ![Custom Statement](/assets/img/tutorials/inventory-service-add-custom-statement-2.png)
            
        2. Click **Custom**.
                   
        3. Enter the following in the **Statement** field.
        
            ```text
            if (itemIDString == inventoryItemId) {
                index = i;
                break;
            } 
            i = i + 1;
            ```
           ![Custom Statement](/assets/img/tutorials/inventory-service-custom-statement-1.png)           
           
           Then click **Save**.
           
15. Add an `if` statement as follows:

    1. Click the last **+** icon in the diagram of your low code view, and then click **+** again.
    
    2. Click **If**.
    
    3. In the **Condition** field, enter `index > 0`.
    
        Then click **Save**.
        
    4. To add a custom statement, click the **+** icon below the `if` statement you added, and then click **+** again. Then click **Custom**.
    
        In the **Statement** field, enter the following.
        
        ```
        string cellName = "C" + index.toString();
        var cellValue = checkpanic googleapis_sheetsEndpoint->getCell("1-0h0mW8KoDIauCgZ5fRup3SaRqR0wv4VI7Pmz28O3DU", "Sheet1", cellName);
        log:print(cellValue.toJsonString());
        int intCellValue = checkpanic 'int:fromString(cellValue.toJsonString());
        ```
       
    5. Click the **+** icon below the `custom` statement you added, and then click **+** again. Then click **if** to add another `if` statement as follows:
    
        1. In the **Condition** field, enter the following condition.
        
            ```
            intCellValue > quantity
            ```
       
            Then click **Save**.
       
        2. Add a custom statement as follows:
        
            1. Click the **+** icon below the `if` statement you added, and then click **+** again.
            
            2. Click **Custom**.
            
            3. In the **Statement** field, enter the following:
            
                ```
                int newStockValue = intCellValue - quantity;
                checkpanic googleapis_sheetsEndpoint->setCell("1-0h0mW8KoDIauCgZ5fRup3SaRqR0wv4VI7Pmz28O3DU", "Sheet1", cellName, newStockValue);
                respondMessages.push("Sucessfully updated the item: " + itemIDString);
                ```
               
               Then click **Save**.
               
16. To respond with the updated inventory record, add a `response` statement as follows:

    1. Click the last **+** icon in the diagram. Then click **+** again.
    
    2. Click **Respond**.
    
    3. In the **Respond Expression** field, enter `respondMessages`. 
    
    4. Click **Save**.

# Step 2: Create a service to capture and respond to orders

First, let's create a service that captures the online orders as follows:

1. Access the Choreo Development Console via the following URL.

    `https://console.choreo.dev/`
    
    Sign in using either your Google or GitHub credentials.
    
2. In the **Services** page, click **Create**.

3. Under **Create with Choreo**, enter `orders` as the name of your Choreo service.

4. In the **Configure API Trigger** form, select **PUT** as the HTTP method, and then click **Save API**.

    ![Resource Configuration](/assets/img/tutorials/order-service-api-trigger.png)

5. To capture the query parameters of orders received, add a `variable` statement as follows:

    1. Click **Variable**.

        ![Add Variable Statement](/assets/img/tutorials/order-service-add-variable-statement.png)
        
    2. Enter information as follows:
    
        ![Query Parameters Variable](/assets/img/tutorials/order-service-query-parameters-variable.png)
    
        | **Field**      | **Value**                  |
        |----------------|----------------------------|
        | **Type**       | **other**                  |
        | **Other Type** | `map<string[]>`            |
        | **Name**       | `queryParams`              |
        | **Expression** | `request.getQueryParams()` |
    
        Click **Save**.
    
6. To capture the count the number of items ordered, add another `variable` statement as follows:

    1. Click the **+** icon below the `queryParams` variable statement you added. Then click **+** again.

        ![Add Query Parameters Variable Statement](/assets/img/tutorials/order-service-add-variable-statement-1.png)
        
    2. Click **Variable**.
    
    3. Enter information as follows:
    
        ![Count Variable](/assets/img/tutorials/order-service-count-variable.png)
    
        | **Field**      | **Value** |
        |----------------|-----------|
        | **Type**       | **var**   |
        | **Name**       | `count`   |
        | **Expression** | `-1`      |
    
        Click **Save**.
    
7. To capture the item ordered, add another `variable` statement as follows. 

    1. Click the **+** icon below the `count` variable statement you added. Then click **+** again.
    
    2. Click **Variable**, and enter information as follows.
    
        ![Item Variable](/assets/img/tutorials/order-service-item-variable.png)
    
        | **Field**      | **Value** |
        |----------------|-----------|
        | **Type**       | **var**   |
        | **Name**       | `item`    |
        | **Expression** | `""`      |
    
        Click **Save**.
    
8. Add an `if` statement as follows.

    1. Click the **+** icon below the `item` variable statement you added. Then click **+** again.
    
    2. Click **If**.
    
    3. In the **Condition** field, enter `queryParams.hasKey("count")`, and click **Save**.
    
        ![Add If statement 1](/assets/img/tutorials/order-service-add-if-statement-1.png)
        
        This checks whether the request received includes a value for the `count` parameter.
    
    4. To specify the action to be carried out if the given condition for the `if` statement is true, add a custom statement as follows: 
    
        1. Click the **+** icon below the `if` statement you added. Then click **+** again.
        
            ![Add Custom statement to If statement 1](/assets/img/tutorials/order-service-add-custom-statement-to-if-statement-1.png)
    
        2. Click **Custom Statement**
    
        3. In the **Statement** field, enter `count = check int:fromString(queryParams.get("count")[0]);`.
        
            ![If statement 1 Custom Statement](/assets/img/tutorials/order-service-custom-statement-for-if-statement-1.png)
            
            Click **Save**.
            
            This statement specifies to get the value for the `count` parameter from each request.
                      
    
9. To filter only the requests with values for the `item` parameter and then to get the value for the `item` parameter from them, add another `if` statement as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
        ![Add If statement 2](/assets/img/tutorials/order-service-add-if-statement-2.png)
    
    2. Click **If**.
    
    3. In the **Statement** field, enter `queryParams.hasKey("item")` and click **Save**.
    
        ![If statement 2](/assets/img/tutorials/order-service-if-statement-2.png)
    
    4. Click the **+** icon below the `if` statement you added. Then click **+** again.
    
        ![Add Custom statement to If statement 2](/assets/img/tutorials/order-service-add-custom-statement-for-if-statement-2.png)
    
    5. Click **Custom Statement**
    
    6. Enter `item = queryParams.get("item")[0];` as the statement and click **Save**.
    
        ![If statement 2 Custom Statement](/assets/img/tutorials/order-service-custom-statement-for-if-statement-2.png)
    
10. To connect your service to an endpoint, add an HTTP connection as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
        ![Add HTTP Connection](/assets/img/tutorials/order-service-add-http-connection.png)
    
    2. Click **Connections**, and then click **HTTP**.
    
    3. Enter information as follows:
    
        ![HTTP Connection](/assets/img/tutorials/o)
    
        | **Field** | **Value**                                                                                                    |
        |-----------|--------------------------------------------------------------------------------------------------------------|
        | **Name**  | `inventoryEndpoint`                                                                                          |
        | **URL**   | `"<The URL to the inventory service you created >?item=" + item + "&count=" + count.toString()` |   
    
    4. Select **PUT** as the operation.
    
    6. Click **Save & Next**, and then enter information in the new tab as follows.
    
        | **Field**               | **Value** |
        |-------------------------|---------  |
        | **Message**             | `""`      |
        | **Select Payload Type** | **Text**  |
    
        Click **Save**.
    
    
11. Add another `variable` statement as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
    2. Click **Variable**.
    
    3. In the **Name** field, enter `index`.
    
    4. In the **Expression** field, enter `textPayload.indexOf("Sucessfully updated the item")`.
    
    5. Click **Save**.
    
12. Add an `if` statement as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
    2. Click **If**.
    
    3. In the **Condition** field, enter `index is int && index > 0`.
    
    4. Click **Save**.
    
13. Add a new Twilio connector as follows:

    1. Click on the **+** icon just below the last If statement that you added. Then click **+** again.
    
    2. Click **Connections**, and then click **Twillio**. Then enter information as follows:
    
        | **Field**      | **Value**                             |
        |----------------|---------------------------------------|
        | **AccountSId** | `ACc055c08964b76edfxsb0a755a3edcd6f3` |
        | **AuthToken**  | `7901fea3341fd05db51451b3eedwsae4`    |
        
    3. Click **Save & Next**.
    
    4. In the **OPERATION** field, select **sendWhatsAppMessage**. Then enter information as follows:
    
        | **Field**   | **Value**        |
        |-------------|------------------|
        | **FromNo**  | `+14155238886`   |                   
        | **ToNo**    | `+94775544041`   |
        | **Message** | `"Your purchase order of " + item + " has shipped and should be delivered on tomorrow. Details: " + count.toString()` | 
        
    5. Click **Save**.
    
14. To log the WhatsApp message sent, add a `log` statement as follows:

    1. Click on the **+** icon just below the last Twillio connection that you added. Then click **+** again.
    
    2. Click **Log**.
    
    3. In the **Expression** field, enter `"WhatsApp message sent"`. 
    
    4. Click **Save**.
    
15. To send an email informing the client confirming the order, add a Gmail connection as follows:

    1. Click on the **+** icon just below the last IF statement that you added. Then click **+** again. 
    
    2. Click **Connections**, and then click **Gmail**.
    
    3. Click **Manual Connection**, and then enter information as follows:
    
        | **Field**        | **Value**                                                                                                   |
        |------------------|-------------------------------------------------------------------------------------------------------------|
        | **RefreshUrl**   | `"229137887995-hagu9n9cjhbdrmtm1udtqub45opq3jgt.apps.googleusercontent.com"`                                |
        | **RefreshToken** | `"x7avYRP-b_1SA3TklT1K7Nf1"`                                                                                |
        | **ClientId**     | `"1//04rsnB0wQBE8KCgYIARPMGAQSNwF-L9Ir4j0hVXqQ4VvxEBaQl89NWfqzYmvDkYfOBVPwngFocKzaPL9YF6fDjjbVfGWaXEwQc1g"` |
        | **ClientSecret** | `"https://oauth2.googleapis.com/token"`                                                                     |
        
        Then click **Save & Next**.
        
    4. In the **OPERATION** field, select **sendMessage**. Then add information in the fields that appear below it as follows:
    
        | **Field**         | **Value**                                                          |
        |-------------------|--------------------------------------------------------------------|
        | **UserId**        | Your user.                                                         |
        | **sender**        | `"tempApr333@gmail.com"`                                           |        
        | **recipient**     | `"tempApr333@gmail.com"`                                           |
        | **subject**       | `"Successfully received order"`                                    |
        | **messageBody**   | `"Payment of " + item + " for " + count.toString() + " received."` |
        | **contentType**   | `"text/plain"`                                                     |
        
    5. Click **Save**.
    
16. To receive a response after the service is successfully executed, add a `respond` statement as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
    2. Click **Respond**.
    
    3. In the **Respond Expression** field, enter `textPayload`.
    
    4. Click **Save**.
    
Now you have successfully created the Order Service.


    
        

    

        
              