# Service Concepts

Choreo services allow you to create enterprise integration services incorporating complex business logic in a user-friendly low-code environment. Here are some basic concepts which will help you understand Choreo services as you develop your services in the [Choreo Console](https://console.choreo.dev/login/).
 
## Service
A service in Choreo is an enterprise integration service exposed via a REST API. Choreo provides a comprehensive set of pre-built service samples for you to clone, edit, and try out, depending on your requirement. 

## API call
An API call in Choreo allows you to connect to an external service through your Choreo service. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

The following lists the connections you can use to interact with generic protocol-based APIs:
  
 - **HTTP**: Communicates with external endpoints over HTTP.   
 - **SMTP**: Performs email-related operations over SMTP. 
 - **POP3**: Retrieves emails from a mail server over POP3. 
 - **IMAP**: Retrieves emails from a mail server over a TCP/IP connection.

The following lists the connections you can use to interact with application-specific APIs:

 - **Twilio**: Integrate with Twilio to perform operations such as sending messages, WhatsApp messages, and making voice calls.
 - **Slack**: Integrate with Slack to perform operations such as sending a message to a channel, deleting a message, etc.
 - **GitHub**: Integrate with GitHub to perform operations such as creating an issue, creating a pull request, etc. 
 - **Gmail**: Integrate with Gmail to perform operations such as sending an email, receiving an email, etc.
 - **Google Calendar**: Integrate with Google Calendar to perform operations such as creating an event, deleting an event, etc.
 - **Google Sheets**: Integrate with Google Sheets to perform operations such as reading data, formatting data, etc.
 - **Salesforce**: Integrate with Salesforce to perform operations such as obtaining organization information, creating a record, etc.
 - **NetSuite**: Integrate with Netsuite to perform operations such as creating a record instance, creating a sublist, etc.

## Statement
A statement is a collection of code fragments that specifies the actions to be carried out by a Choreo service. Choreo statements can also be composed of expressions. These expressions are written based on the [Ballerina Expression Syntax](#ballerina-expression-syntax).

Choreo supports the following statements:

 - **Log**: Logs an event with an information statement or an error that occurs in your service.    
 - **Variable**: Declares a variable and a value of a preferred data type (`string`, `int`, etc.) to use later in the logical process of the service.
 - **If**: Performs conditional execution. 
 - **ForEach**: Includes a control flow statement in your service to iterate over a list of items.
 - **While**: Continuously executes a block of statements in a loop based on a given `boolean` condition. 
 - **Respond**: Sends a specific response back to the client. For example, if the request was processed successfully,  responding to the client with an `HTTP_OK`.
 - **Other**: Allows to write a single or a multiline code snippet in your service.

## Ballerina expression syntax
The syntax to use when you want to input values for an expression while designing a service using Choreo. [Refer to the syntax.](../references/ballerina-expression-syntax.md)
