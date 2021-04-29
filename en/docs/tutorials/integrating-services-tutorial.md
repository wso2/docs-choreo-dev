# Integrating Services

In this tutorial, you can learn how to integrate multiple services via Choreo. Here, let's consider a simple example where an online shoe store needs an application that captures online orders. For each order, the store also needs to generate a notification for the sales manager as well as update the inventory records.

## Before you begin

The following are required to try out this tutorial:

- A Google account
- A Twilio account

# Step 1: Create a service to capture and respond to orders

First, let's create a service that captures the online orders as follows:

1. Access the Choreo Development Console via the following URL.

    `https://console.choreo.dev/`
    
    Sign in using either your Google or GitHub credentials.
    
2. In the **Services** page, click **Create**.

3. Under **Create with Choreo**, enter `orders` as the name of your Choreo service.

4. In the **Configure API Trigger** form, select **PUT** as the HTTP method, and then click **Save API**.

    ![Resource Configuration](/assets/img/tutorials/order-service-api-trigger.png)

5. Add a `variable` statement as follows:

    1. Click **Variable**.

        ![Add Variable Statement](/assets/img/tutorials/order-service-add-variable-statement.png)
    
    2. Select **other** as the type of the variable. In the **Other Type** field that appears, enter `map<string[]>`
    
    3. Enter `queryParams` as the name of the variable.
    
    4. Enter `request.getQueryParams()` as the expression of the variable.
    
    5. Click **Save**.
    
6. To count the number of items ordered, add another `variable` statement as follows:

    1. Click the **+** icon below the `queryParams` variable statement you added. Then click **+** again.

        ![Add Variable Statement](/assets/img/tutorials/order-service-add-variable-statement-1.png)
        
    2. Click **Variable**.
    
    3. Leave **var** as the variable type.
    
    4. Enter `count` as the variable name.
    
    5. Enter `-1` as the variable expression.
    
    6. Click **Save**.
    
7. Add another `variable` statement as follows. 

    1. Click the **+** icon below the `count` variable statement you added. Then click **+** again.
    
    2. Click **Variable**.
    
    3. Leave **var** as the variable type.
    
    4. Enter `item` as the variable name.
    
    5. Enter `""` as the variable expression.
    
    6. Click **Save**.
    
8. Add an `if` statement as follows.

    1. Click the **+** icon below the `item` variable statement you added. Then click **+** again.
    
    2. Click **If**.
    
    3. Enter `queryParams.hasKey("count")` as the condition and click **Save**.
    
    4. Click the **+** icon below the  If statement you added. Then click **+** again.
    
    5. Click **Custom Statement**
    
    6. Enter `count = check int:fromString(queryParams.get("count")[0]);` as the statement and click **Save**.
    
9. Add another `if` statement as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
    2. Click **If**.
    
    3. Enter `(queryParams.hasKey("item"))` as the statement and click **Save**.
    
    4. Click the **+** icon below the  If statement you added. Then click **+** again.
    
    5. Click **Custom Statement**
    
    6. Enter `item = queryParams.get("item")[0];` as the statement and click **Save**.
    
10. To connect your service to an endpoint, add an HTTP connection as follows:

    1. Click the last **+** icon in the current low code view. Then click **+** again.
    
    2. Click **Connections**, and then click **HTTP**.
    
    3. In the **Name** field, enter `inventoryEndpoint`.
    
    4. In the **URL** field, enter `"https://inventoryseurud-test.chrvicev3-miyoreo.dev/inventory?item=" + item + "&count=" + count.toString()`.
    
    5. Select **PUT** as the operation.
    
    6. Click **Save & Next**.
    
    7. In the **Message** field, enter `""`.
    
    8. Enter `putResponse` as the response variable name.   
    
    9. In the **Select Payload Type** field, select **Text**.
    
    10. Click **Save**.
    
    
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
    
    3. In the **Expression* field, enter `"WhatsApp message sent"`. 
    
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

## Step 2: Create a service to manage inventory records

In this step, you are creating a service that consumes the orders captured by the `orders` service that you previously created, and then updates the inventory records based on the number of items ordered.

To create this service, follow the procedure below:

1. Access the Choreo Development Console and click **Services**. Then click **Create**.

2. Enter `inventory` as the name and click **Create**.

3. Click **PUT** and then click **Save API**.

4. Click **Variable**. Then select/enter information as follows:

    | **Field**         | **Value**                     |
    |-------------------|-------------------------------|
    | **Type**          | **string**                    |     
    | **Name**          | `responseString`              |
    | **Expression**    | `""`                    |

    Click **Save**.
    
5. To add another variable, click the **+** below the last variable statement you added, and then click **+** again. Enter information for the variable as follows:

    | **Field**         | **Value**                  |
    |-------------------|----------------------------|
    | **Type**          | **other**                  |     
    | **Other Type**    | `map<string[]>`            |
    | **Name**          | `queryParams`              |
    | **Expression**    | `request.getQueryParams()` |

    Click **Save**.
    
6. To add another variable, click the **+** below the last variable statement you added, and then click **+** again. Enter information for the variable as follows:

    | **Field**         | **Value**                    |
    |-------------------|------------------------------|
    | **Type**          | **string**                   |     
    | **Name**          | `inventoryItemId`            |
    | **Expression**    | `queryParams.get("item")[0]` |
    
    Click **Save**.
    
7. To add another variable, click the **+** below the last variable statement you added, and then click **+** again. Enter information for the variable as follows:

     | **Field**         | **Value**                                           |
     |-------------------|-----------------------------------------------------|
     | **Type**          | **int**                                             |     
     | **Name**          | `quantity`                                          |
     | **Expression**    | `check int:fromString(queryParams.get("count")[0])` |  
     
 8. To connect to the Google sheet where inventory records are maintained, add a Google Sheet connection as follows: 
 
    1. Click the **+** below the last variable statement you added, and then click **+** again.
    
    2. Click **Connections**, and then click **Google Sheets**
    
    3. Click **Manual Connection**, and enter information as follows:
    
        | **Field**        | **Value**                                             |
        |------------------|-------------------------------------------------------|
        | **RefreshUrl**   | `TOKEN_ENDPOINT_F45C681C_916E_11EB_9B52_72BF7DE7E173` |  
        | **RefreshToken** | `REFRESH_TOKEN_F45C681C_916E_11EB_9B52_72BF7DE7E173`  |
        | **ClientId**     | `CLIENT_ID_F45C681C_916E_11EB_9B52_72BF7DE7E173`      | 
        | **ClientSecret** | `CLIENT_SECRET_F45C681C_916E_11EB_9B52_72BF7DE7E173`  |
        
        Then click **Save & Next**.
        
    4. In the **OPERATION** field, select **getColumn**. Then enter information as follows in the fields that appear below:
    
        | **Field**         | **Value**                                        |
        |-------------------|--------------------------------------------------|
        | **SpreadsheetId**x| `"1-0h0mW8KoDIauCgZ5fRup3SaRqR0wv4VI7Pmz28O3DU"` |  
        | **SheetName**     | `"Sheet1"`                                       |
        | **Column**        | `"A"`                                            | 
        
        Click **Save**.
        
        This connection retrieves the number of items currently available in the inventory from column A.
        
 9. To cast the response received, add another variable statement as follows:
 
    1. Click the **+** below the Google Sheet connection you added, and then click **+** again.
    
    2. Click **Variable**
    
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
       