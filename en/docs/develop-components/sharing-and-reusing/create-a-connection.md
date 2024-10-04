# Create a Connection

Connections in Choreo allow you to integrate Choreo components, or to integrate Choreo components to external services or resources. Connections provide a simple and uniform way to integrate with services and resources.

To consume a service deployed on Choreo or a third party service within your service, you must create a connection to the service you want to consume. 

# Create a Connection to a Service Deployed in Choreo

To create a connection, follow the steps given below:

1. In the Choreo Console, go to the top navigation menu and set the visibility level as [project](../../choreo-concepts/connections.md#project-connections) or [component](../../choreo-concepts/connections.md#component-connections) as follows: 

    - **Project Connection**: Select an organization and a project in that organization. 
    - **Component Connection**: Select an organization, a project in that organization, and a component in the selected project. 

2. From the left navigation menu, click **Dependencies**  and then **Connections**. This page will list the currently available connections.
3. Click **+Create**. The **Create Connection** page opens with the marketplace view for you to browse the available services. You can search and apply filters to efficiently find a service.
4. Click on the service you want to connect to. 
5. Enter a name and a description for the connection and click **Next**. This displays the ServiceURL for the development and production environments. 
6. Click **Create**.

# Create a Connection to a Third Party Service

Follow the steps 1 to 4 in the previous section.

5. In the connection creation view, provide the name and description for the connection.
6. Expand the **Environment to Endpoint Mapping** section, verify the service endpoint assignment for Choreo environments.
7. Click **Create**.


For step-by-step instructions on how to use a connection in your service, see [Use a Connection in Your Service](./use-a-connection-in-your-service.md).

For step-by-step instructions on how to use a connection in your web application, see [Use a Connection in Your Web Application](./use-a-connection-in-your-web-application.md).
