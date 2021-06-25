# Integration Concepts
Choreo integrations allow you to create enterprise integrations to automate workflows in a user-friendly low-code environment. Here are some basic concepts which will help you understand Choreo integrations as you develop your integrations in the [Choreo Console](https://console.choreo.dev/login/).
  
## Integration
Integration in Choreo is a process that connects APIs, events, and streams allowing these to function as a single unit. Choreo provides an integration template which is a pre-built sample designed to address a common integration requirement. Choreo includes a set of comprehensive integration templates for you to select and use.

## Trigger
A trigger in Choreo integrations is an event or an action that causes a Choreo integration to start executing.

Choreo supports the following trigger types:

 - **Schedule**: Triggers execution according to a given schedule.
 - **Manual**: Triggers execution manually.
 - **Calendar**: Triggers execution based on events in a Google Calendar.
 - **GitHub**: Triggers execution based on GitHub events and actions.
 - **Salesforce**: Triggers execution based on Salesforce events. 

## API call
An API call in Choreo allows you to connect to an external service through your Choreo integration. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

The following lists the connections you can use to interact with generic protocol-based APIs:
  
{!reusable-components/choreo-connectors-list.md!} 

## Statement

A statement is a collection of code fragments that specifies the actions to be carried out by a Choreo integration. 

Choreo supports the following statements:

 - **Log**: Logs an event with an information statement or an error that occurs in your Choreo integration.
 - **Variable**: Declares a variable and a value of a preferred data type (`string`, `int`, etc.) to use later in the logical process of the Choreo integration.
 - **Data Mapping**: Allows to create variables by visually mapping one or more variables. For more information see [Data Mapping](../references/data-mapping.md).
 - **If**: Performs conditional execution.
 - **ForEach**: Includes a control flow statement in your Choreo integration to iterate over a list of items.
 - **While**: Continuously executes a block of statements in a loop based on a given `boolean` condition. 
 - **Respond**: Sends a specific response back to the client. For example, if the request was processed successfully,  responding to the client with an `HTTP_OK`.
 - **Other**: Allows to write a single or a multiline code snippet in your Choreo integration.

## Ballerina expression syntax

Choreo statements can also be composed of expressions. For the syntax to use when you want to input values for an expression while designing an integration using Choreo, see [Ballerina Expression Syntax](../references/ballerina-expression-syntax.md).
