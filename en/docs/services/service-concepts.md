# Service Concepts

Before you start creating services with Choreo, there are basic concepts that you need to understand. This section walks you through all the service concepts you need to know.
 
## Service
A service in Choreo is a logical representation of a repeatable activity that has a specified outcome.

## Service Sample
A service sample in Choreo is a prebuilt service that performs a specific activity. Choreo provides a comprehensive set of service samples for you to clone, edit, and try out, depending on your requirement. 

## API Call
An API call in Choreo services allows you to connect to and interact with an external service through your service. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

Following are the connections you can use to interact with generic protocol-based APIs:
  
 - **HTTP**: To interact with the HTTP API and communicate with an external endpoint via the HTTP protocol.   
 - **SMTP**: To send emails via the SMTP protocol.   
 - **POP3**: To receive emails from a POP3 email server.  
 - **IMAP**: To receive emails from any email server that supports the IMAP protocol. 

Following are the connections you can use to interact with application-specific APIs:

 - **Twilio**: To interact with the Twilio API and perform operations such as sending messages, WhatsApp messages, and making voice calls.
 - **Slack**: To interact with the Slack API and perform required operations. 
 - **GitHub**: To interact with the GitHub API and  perform required operations. 
 - **Gmail**: To interact with the Gmail API and perform operations such as creating, modifying, and sending emails.
 - **Google Calendar**: To interact with the Google Calendar API and perform required operations.
 - **Google Sheets**: To interact with the Google Sheets API and perform operations such as creating and listing spreadsheets.
 - **Salesforce**: To interact with the Salesforce API and perform required operations. 
 - **NetSuite**: To interact with the NetSuite API and perform required operations. 

## Statement
A statement is a syntactic unit that specifies the action to be carried out in a Choreo service. 

Choreo supports the following statements:

 - **Log**: To add an entry to the service log at runtime. You can either add an **Info** log or an **Error** log depending on your requirement.   
 - **Variable**: To declare a variable of a preferred type depending on your requirement.
 - **If**: To perform a conditional check and evaluate whether a statement is true or false.   
 - **ForEach**: To iterate over a list of items to perform control flow. 
 - **While**: To iterate over a given condition to perform control flow. 
 - **Respond**: To send a response back to the client. For example, to get an API to respond with an `HTTP_OK` indicating that the request was processed successfully.
 - **Custom**: To specify a custom statement depending on your requirement.

## Expression Syntax
The syntax to use when you want to input values for an expression when designing a service using Choreo. [Refer to the syntax.](../references/choreo-expression-syntax)