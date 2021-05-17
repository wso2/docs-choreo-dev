# Service Concepts

Choreo services allows you to create enterprise integration services incorporating complex business logic in a user-friendly low-code environment. Here are some basic concepts which will help you understand Choreo services as you develop your services in the [Choreo Console]({{choreo_console}}).
 
## Service
A service in Choreo is an enterprise integration service exposed via a REST API. Choreo provides a comprehensive set of pre-built service samples for you to clone, edit, and try out, depending on your requirement. 

## API Call
An API call in Choreo allows you to connect to an external service through your Choreo service. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

Following are the connections you can use to interact with generic protocol-based APIs:
  
 - **HTTP**: Communicates with external endpoints over HTTP.   
 - **SMTP**: Performs email related operations over SMTP. 
 - **POP3**: Retrieves emails from a mail server over POP3. 
 - **IMAP**: Retrieves emails from a mail server over a TCP/IP connection.

Following are the connections you can use to interact with application-specific APIs:

 - **Twilio**: Connects your service to Twilio API to perform operations such as sending messages, WhatsApp messages, and making voice calls.
 - **Slack**: Connects your service to Slack API to perform operations in Slack, such as sending a message to a channel, deleting a message, etc.
 - **GitHub**: Connects your service to GitHub API to perform operations in GitHub, such as creating an issue, creating a pull request, etc. 
 - **Gmail**: Connects your service to Gmail API to perform email operations such as sending an email, receiving an email, etc.
 - **Google Calendar**: Connects your service to Google Calendar API to perform operations in Google Calendar such as creating an event, deleting an event, etc.
 - **Google Sheets**: Connects your service to Google Sheets API to perform operations in Google Sheets such as reading data, formatting data, etc.
 - **Salesforce**: Connects your service to Salesforce API to perform Salesforce operations such as obtaining organization information, creating a record, etc.
 - **NetSuite**: Connects your service to Netsuite API to perform Netsuite operations such as creating a record instance, creating a sublist, etc.

## Statement
A statement is a collection of code fragments that specifies the actions to be carried out by a Choreo service. Choreo statements can also be composed of expressions. These expressions are written based on the [Ballerina Expression Syntax](#ballerina-expression-syntax).

Choreo supports the following statements:

 - **Log**: Logs an event with an information statement or an error that occurs in your service.    
 - **Variable**: Declares a variable and a value of a preferred data type (string, integer, etc.) to use later in the logical process of the service.
 - **If**: Specifies two blocks of logical components so that the system can decide which block to execute based on whether the provided condition is true or false.   
 - **ForEach**: Includes a control flow statement in your service to iterate over a list of items.
 - **While**: Continuously executes a block of statements in a loop as long as the specified condition is TRUE. 
 - **Respond**: Sends a specific response back to the client. For example, if the request was processed successfully,  responding to the client with an `HTTP_OK`.
 - **Custom**: Is used to write a single or a multiline code snippet in your service.

## Ballerina Expression Syntax
The syntax to use when you want to input values for an expression when designing a service using Choreo. [Refer to the syntax.](../references/ballerina-expression-syntax)
