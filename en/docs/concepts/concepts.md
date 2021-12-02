# Concepts

!!! info
    The **concepts** will be categorized under appropriate high-level topics, and we can add subsections depending on the concepts that need to be added. For example, the following concepts could be added:

### Project
A project is a virtual container that includes the integration solution of a cloud-native application. A typical integration solution in Choreo includes multiple Choreo components such as REST APIs, API proxies, triggers, WebSockets, scheduled tasks, etc. A project encapsulates these components for better organization, maintenance, and easy collaboration. You can add, modify, or delete components to the project to continuously develop the integration solution. 


### Components
Components are the building blocks in creating an integration solution in Choreo.  A component in Choreo could be a REST API, an API Proxy, a trigger, a WebSocket, or a scheduled task. Choreo’s componentization allows you to publish components to a Marketplace (currently supports only REST APIs and Triggers) and promotes component reuse. It increases productivity and makes collaborative integration development effortless.


### API call
An API call in Choreo allows you to connect to an external service through your Choreo integration. You can use an API call to interact with generic protocol-based APIs and application-specific APIs.

{!reusable-components/choreo-connectors-list.md!}

### Statement
A statement is a collection of code fragments that specifies the actions to be carried out by a Choreo service.

Choreo supports the following statements:

 - **Log:** Logs an event with an information statement or an error that occurs in your service.    
 - **Variable:** Declares a variable and a value of a preferred data type (`string`, `int`, etc.) to use later in the logical process of the service.
 - **Data Mapping:** Allows you to create variables by visually mapping one or more variables. For more information, see [Data Mapping](../references/data-mapping.md).
 - **If:** Performs conditional execution. 
 - **ForEach:** Includes a control flow statement in your service to iterate over a list of items.
 - **While:** Continuously executes a block of statements in a loop based on a given `boolean` condition. 
 - **Respond:** Sends a specific response back to the client. For example, if the request was processed successfully, it responds to the client with an `HTTP_OK`.
 - **Other:** Allows you to write a single or a multiline code snippet in your service.
