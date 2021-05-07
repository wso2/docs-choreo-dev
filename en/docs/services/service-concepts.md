# Concepts

Before you start creating services with Choreo, there are basic concepts that you need to understand. This section walks you through all the service concepts you need to know.
 
## Service
A service in Choreo is a logical representation of a repeatable activity that has a specified outcome.

## Service Sample
A service sample in Choreo is a prebuilt service that performs a specific activity. Choreo provides a comprehensive set of service samples for you to clone, edit, and try out, depending on your requirement. 

## Connection
A connection in Choreo allows you to connect to and interact with an external service through your application. You can use a connection to interact with generic protocol-based APIs and application-specific APIs.

Following are the connections you can use to interact with generic protocol-based APIs:
  
 - **HTTP**: Allows your application to interact with the HTTP API and communicate with an external endpoint via the HTTP protocol.   
 - **SMTP**: Allows your application to send emails via the SMTP protocol.   
 - **POP3**: Allows your application to receive emails from a POP3 email server.  
 - **IMAP**: Allows your application to receive emails from any email server that supports the IMAP protocol. 

Following are the connections you can use to interact with application-specific APIs:

 - **Twilio**: Allows your application to interact with the Twilio API and perform operations such as sending messages, WhatsApp messages, and making voice calls.
 - **Slack**: Allows your application to interact with the Slack API and perform required operations. 
 - **GitHub**: Allows your application to interact with the GitHub API and  perform required operations. 
 - **Gmail**: Allows your application to interact with the Gmail API and perform operations such as creating, modifying, and sending emails.
 - **Google Calendar**: Allows your application to interact with the Google Calendar API and perform required operations.
 - **Google Sheets**: Allows your application to interact with the Google Sheets API and perform operations such as creating and listing spreadsheets.
 - **Salesforce**: Allows your application to interact with the Salesforce API and perform required operations. 
 - **NetSuite**: Allows your application to interact with the NetSuite API and perform required operations. 

## Statement
A statement is a syntactic unit that specifies the action to be carried out in a Choreo application. 

Choreo supports the following statements:

 - **Log**: Allows you to add an entry to the application log at runtime. You can either add an **Info** log or an **Error** log depending on your requirement.   
 - **Variable**: Allows you to declare a variable of a preferred type depending on your requirement.
 - **If**: Allows you to perform a conditional check and evaluate whether a statement is true or false.   
 - **ForEach**: Allows you to iterate over a list of items to perform control flow. 
 - **While**: Allows you to iterate over a given condition to perform control flow. 
 - **Respond**: Allows you to send a response back to the client. For example, to get an API to respond with an `HTTP_OK` indicating that the request was processed successfully.
 - **Custom**: Allows you to specify a custom statement depending on your requirement.

## Expression Syntax
The syntax to use when you want to input values for an expression when designing a service using Choreo. [Refer to the syntax.](/references/ballerina-expression-syntax)
