# Service Concepts

Choreo services allow you to create enterprise integration services incorporating complex business logic in a user-friendly low-code environment. Here are some basic concepts which will help you understand Choreo services as you develop your services in the [Choreo Console](https://console.choreo.dev/login/).
 
## Service
A service in Choreo is an enterprise integration service exposed via a REST API. Choreo provides a comprehensive set of pre-built service samples for you to clone, edit, and try out, depending on your requirement. 

## API call
An API call in Choreo allows you to connect to an external service through your Choreo integration. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

{!reusable-components/choreo-connectors-list.md!}

## Statement
A statement is a collection of code fragments that specifies the actions to be carried out by a Choreo service.

Choreo supports the following statements:

 - **Log**: Logs an event with an information statement or an error that occurs in your service.    
 - **Variable**: Declares a variable and a value of a preferred data type (`string`, `int`, etc.) to use later in the logical process of the service.
 - **Data Mapping**: Allows to create variables by visually mapping one or more variables. For more information see [Data Mapping](../references/data-mapping.md).
 - **If**: Performs conditional execution. 
 - **ForEach**: Includes a control flow statement in your service to iterate over a list of items.
 - **While**: Continuously executes a block of statements in a loop based on a given `boolean` condition. 
 - **Respond**: Sends a specific response back to the client. For example, if the request was processed successfully,  responding to the client with an `HTTP_OK`.
 - **Other**: Allows to write a single or a multiline code snippet in your service.

## Ballerina expression syntax

Choreo statements can also be composed of expressions. For the syntax to use when you want to input values for an expression while designing a service using Choreo, see [Ballerina Expression Syntax](../references/ballerina-expression-syntax.md).
