# Create a Connection

Connections in Choreo enable seamless integration between components and external services or resources. Connections provide a simple and uniform approach to integrating with services and resources.

To consume a service, whether deployed in Choreo or registered as an external service, you need to create a connection to that service.

## Create a connection to a Choreo service

Follow these steps to create a connection to a service deployed in Choreo:

1. In the Choreo Console, go to the top navigation menu and set the visibility level as [project](../../choreo-concepts/connections.md#project-connections) or [component](../../choreo-concepts/connections.md#component-connections) as follows: 

    - **Project Connection**: Select an organization and a project in that organization. 
    - **Component Connection**: Select an organization, a project in that organization, and a component in the selected project. 

2. In the left navigation menu, click **Dependencies**  and then **Connections**. This page lists the existing connections.
3. Click **+Create**. The **Create Connection** page opens with the marketplace view for you to browse and search for services. You can search and apply filters to efficiently find a service.
4. Click on the service you want to connect to. 
5. Enter a name and a description for the connection and then click **Next**. This displays the **Service URL** for both development and production environments. 
6. Click **Create**.

## Create a connection to an external service

Follow these steps to create a connection to an external service:

1. Follow steps 1–4 from the previous section.
2. Expand the **Environment to Endpoint** section and verify the service endpoints assigned to the Choreo environments.
3. Click **Create**.

For step-by-step instructions on using a connection in your service, see [Use a Connection in Your Service](./use-a-connection-in-your-service.md).

For step-by-step instructions on using a connection in your web application, see [Use a Connection in Your Web Application](./use-a-connection-in-your-web-application.md).
