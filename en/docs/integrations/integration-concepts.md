# Integration Concepts
Before you start building integrations with Choreo, there are basic concepts that you need to understand. This section walks you through all the integration concepts you need to know. 
 
## Integration
Integration in Choreo is the process of connecting APIs, events, and streams to function as a single unit that can share information.

## Integration Template
An integration template in Choreo is a prebuilt sample designed to address a common integration requirement. Choreo provides a comprehensive set of integration templates for you to select and use depending on your requirement.

## Trigger
A trigger in Choreo is an event or an action that can cause an integration to start executing.

Choreo supports the following trigger types:

 - **Schedule**: To trigger an integration based on a schedule that you set.
 - **Manual**: To run and test an integration manually.
 - **Calendar**: To trigger an integration based on events in a Google Calendar.
 - **GitHub**: To trigger an integration based on GitHub events and actions.
 - **Salesforce**: To trigger an integration based on topics in a Salesforce account. 

## API Call
An API call in Choreo allows you to connect to and interact with an external service through your integration. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

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
A statement is a syntactic unit that specifies the action to be carried out in an integration. 

Choreo supports the following statements:

 - **Log**: To add an entry to the integration log at runtime. You can either add an **Info** log or an **Error** log depending on your requirement.   
 - **Variable**: To declare a variable of a preferred type depending on your requirement.
 - **If**: To perform a conditional check and evaluate whether a statement is true or false.   
 - **ForEach**: To iterate over a list of items to perform control flow. 
 - **While**: To iterate over a given condition to perform control flow. 
 - **Return**: To end the ongoing execution of an integration and return control to the caller.
 - **Custom**: To specify a custom statement depending on your requirement.

## Expression Syntax
The syntax to use when you want to input values for an expression when designing an integration using Choreo. [Refer to the syntax.](../references/choreo-expression-syntax)

