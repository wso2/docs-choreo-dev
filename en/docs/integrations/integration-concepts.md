# Integration Concepts
Choreo integrations allow you to create enterprise integrations to automate workflows in a user-friendly low-code environment. Here are some basic concepts which will help you understand Choreo integrations as you develop your integrations in the [Choreo Console]({{choreo_console}}).
  
## Integration
Integration in Choreo is a process for connecting APIs, events, and streams to function as a single unit. Choreo provides an integration template which is a prebuilt sample designed to address a common integration requirement. Choreo includes a set of comprehensive integration templates for you to select and use.

## Trigger
A trigger in Choreo integrations is an event or an action that can cause an integration to start executing.

Choreo supports the following trigger types:

 - **Schedule**: This allows you to configure your integration to trigger execution according to a given schedule.
 - **Manual**: This allows you to configure your integration to trigger execution manually by clicking 'Run & Test'.
 - **Calendar**: This allows you to configure your integration to trigger execution based on events in a Google Calendar.
 - **GitHub**: This allows you to configure your integration to trigger execution based on GitHub events and actions.
 - **Salesforce**: This allows you to configure your integration to trigger execution based on Salesforce events. 

## API Call
An API call in Choreo allows you to connect to an external service through your Choreo integration. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

Following are the connections you can use to interact with generic protocol-based APIs:
  
 - **HTTP**: This allows your service to communicate with external endpoints over HTTP.   
 - **SMTP**: This allows you to set up an email client to perform operations over SMTP. 
 - **POP3**: This allows you to set up an email client to receive emails over POP3. 
 - **IMAP**: Allow you to set up an email client to retrieve emails from a mail server over a TCP/IP connection.

Following are the connections you can use to interact with application-specific APIs:

 - **Twilio**: This allows you to connect your service to Twilio API and perform operations such as sending messages, WhatsApp messages, and making voice calls.
 - **Slack**: This allows you to connect your service to Slack API to perform operations in Slack, such as sending a message to a channel, deleting a message, etc.
 - **GitHub**: This allows you to connect your service to GitHub API to perform operations in GitHub, such as creating an issue, creating a pull request, etc. 
 - **Gmail**: This allows you to connect your service to Gmail API to perform email operations such as sending an email, receiving an email, etc.
 - **Google Calendar**: This allows you to connect your service to Google Calendar API to perform operations in Google Calendar such as creating an event, deleting an event, etc.
 - **Google Sheets**: This allows you to connect your service to Google Sheets API to perform operations in Google Sheets such as reading data, formatting data, etc.
 - **Salesforce**: This allows you to connect your service to Salesforce API to perform Salesforce operations such as obtaining organization information, creating a record, etc.
 - **NetSuite**: This allows you to connect your service to Netsuite API to perform Netsuite operations such as creating a record instance, creating a sublist, etc. 

## Statement

A statement is a collection of code fragments that specifies the actions to be carried out by a Choreo integration. Choreo statements can also be composed of expressions. These expressions are written based on the [Ballerina Expression Syntax](#ballerina-expression-syntax).

Choreo supports the following statements:

 - **Log**: This allows you to log an event with an information statement or an error that occurs in your service.    
 - **Variable**: This allows you to declare a variable and a value of a preferred data type (string, integer, etc.) to use later in the logical process of the service.
 - **If**: This allows you to specify two blocks of logical components so that the system can decide which block to execute based on whether the provided condition is true or false.   
 - **ForEach**: This allows you to include a control flow statement in your service to iterate over a list of items.
 - **While**: This allows you to continuously execute a block of statements in a loop as long as the specified condition is TRUE. 
 - **Respond**: This allows you to configure the service to send a specific response back to the client. For example, if the request was processed successfully,  responding to the client with an `HTTP_OK`.
 - **Custom**: This allows you to write a single or a multiline code snippet in your service.

## Expression Syntax
The syntax to use when you want to input values for an expression when designing a service using Choreo. [Refer to the syntax.](../references/choreo-expression-syntax)

