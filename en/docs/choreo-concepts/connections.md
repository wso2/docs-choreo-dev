# Connections

Services can exist in two main forms: standalone and integrated. By connecting services, organizations can access and benefit from the combined strength of various features and capabilities. Choreo allows you to connect services using Connections. 

Connections in Choreo allows you to integrate Choreo components, or to integrate Choreo components to external services or resources. Connections provIDe a simple and uniform way to integrate with services and resources. A Connection will include the set of parameters that the application requires to create a connection at runtime. The visibility level of the Connection and the service for which it is created determine these parameters. You can easily refer to these parameters in the application runtime.
 
You can add Connections in different visibility levels: Organization, Project, and Component. The visibility levels are described below:

## Organization Connections

Organization Connections are Connections you create to connect to services within a particular organization. These Connections **can be used by any component within the organization**.

For example, create an Organization Connection to share a database across the organization for all the projects and components to reuse. Components can refer to Organization Connections using the connection ID. 
Organization connections created to consume Choreo services under the OAuth security scheme will have their own OAuth application per connection. Any component reusing such a connection will use the same client ID and client secret.

## Project Connections

Project Connections are Connections you create to connect to services within a particular project. The Connections **can be used by any component within the project**. 

For example, if you want to share a third-party service like Twilio across the project for all the components within that project to reuse, you can create a project connection. Components can refer to Project Connections using the connection ID. 
Project connections created to consume Choreo services under the OAuth security scheme will share the same OAuth application across the project. Any component reusing such a connection will use the same client ID and client secret.

## Component Connections

Component Connections are Connections you define at the component level and **used by only that component**. 

For example, create a component connection if you want to connect a legacy service to a given component. Components can refer to the Component Connection using the connection ID. 
If your component consumes more than one Choreo service, the Component connections created to consume those Choreo services under the OAuth security scheme can share the same OAuth application by sharing the same client ID and secret between all such connections.
