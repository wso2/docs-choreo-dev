# Integration Concepts
Before you start building integrations with Choreo, there are basic concepts that you need to understand. This section walks you through all the integration concepts you need to know. 
 
## Integration
Integration in Choreo is the process of connecting APIs, events, and streams to function as a single unit that can share information.

## Integration Template
An integration template in Choreo is a prebuilt sample designed to address a common integration requirement. Choreo provides a comprehensive set of integration templates for you to select and use depending on your requirement.

## Trigger
A trigger in Choreo is an event or an action that can cause a Choreo application to start executing.

Choreo supports the following trigger types:

 - **Schedule**: Allows you to trigger an application based on a schedule that you set.
 - **Manual**: Allows you to run and test an application manually.
 - **Calendar**: Allows you to trigger an application based on events in a Google Calendar.
 - **GitHub**: Allows you to trigger an application based on GitHub events and actions. Supported GitHub events and associated actions that can trigger a Choreo application are as follows:

     | Event                       | Action                 | Application Trigger                      |
     |-----------------------------|------------------------|------------------------------------------|
     | issue_comment               | created                | Add a comment to an issue                |
     |                             | edited                 | Edit a comment on an issue               |
     |                             | deleted                | Delete a comment on an issue             |
     | issues                      | assigned               | Assign an issue to a user                |
     |                             | unassigned             | Unassign an issue from a user            |
     |                             | labeled                | Add a label to an issue                  |
     |                             | unlabeled              | Remove a label from an issue             |
     |                             | opened                 | Open an issue                            |
     |                             | edited                 | Edit an issue                            |
     |                             | milestoned             | Add a milestone to an issue              |
     |                             | demilestoned           | Remove a milestone from an issue         |
     |                             | closed                 | Close an issue                           |
     |                             | reopened               | Reopen an issue                          |
     | label                       | created                | Create a label                           |
     |                             | edited                 | Edit a label                             |
     |                             | deleted                | Delete a label                           |
     | milestone                   | created                | Create a milestone                       |
     |                             | closed                 | Close a milestone                        |
     |                             | opened                 | Open a milestone                         |
     |                             | edited                 | Edit a milestone                         |
     |                             | deleted                | Delete a milestone                       |
     | pull_request                | assigned               | Assign a pull request to a user          |
     |                             | unassigned             | Unassign a pull request from a user      |
     |                             | review_requested       | Request a user to review a pull request  |
     |                             | review_request_removed | Remove a reviewer from a pull request    |
     |                             | labeled                | Add a label to a pull request            |
     |                             | unlabeled              | Remove a label from a pull request       |
     |                             | opened                 | Open a pull request                      |
     |                             | edited                 | Edit a pull request                      |
     |                             | closed                 | Close a pull request                     |
     |                             | reopened               | Reopen a pull request                    |
     | pull_request_review         | submitted              | Submit a pull request                    |
     |                             | edited                 | Edit a pull request review               |
     |                             | dismissed              | Dismiss a pull request review            |
     | pull_request_review_comment | created                | Create a pull request review comment     |
     |                             | edited                 | Edit a pull request review comment       |
     |                             | deleted                | Delete a pull request review comment     | 
 
 - **Salesforce**: Allows you to trigger an application based on topics in a Salesforce account. 

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
 - **Return**: Allows you to end the ongoing execution of a Choreo application and return control to the caller.
 - **Custom**: Allows you to specify a custom statement depending on your requirement.

## Expression Syntax
The syntax to use when you want to input values for an expression when designing an integration using Choreo. [Refer to the syntax.](/references/choreo-expression-syntax)

