# Create a Connection

You can use a connection to consume a component within another component. For example, for service integrations. 

!!! info
    From November 8, 2023, at 10:20 AM UTC, you can create Connections for new components, excluding Ballerina and MI.

To consume a service deployed on Choreo within your service, you need to create a connection to the service you wish to consume. Follow the steps below.

1. In the Choreo Console, go to the top navigation menu, and set the visibility level as [project](../../choreo-concepts/connections.md#project-connections), or [component](../../choreo-concepts/connections.md#component-connections) as follows: 

    - **Project Connection**: Select an organization and a project in that organization. 
    - **Component Connection**: Select an organization, a project in that organization, and a component in the selected project. 

2. From the left navigation menu, click **Dependencies**  and then **Connections**. This page will list the currently available connections.
3. Click **+Create**. The **Create Connection** page will display the marketplace view for you to browse the available services. You can search and apply filters to find services efficiently.
4. Click on the service to wish to connect to. 
5. Enter a name and a description for the connection and click **Next**.
6. You will receive the ServiceURL for the development and production environments. Click **Finish**.

You can consume a connection depending on the component type. Follow the documentation below to use the connection in your application: 

- [Sharing and Reusing Services](./sharing-and-reusing-services.md)
- [Sharing and Reusing Web Applications](./sharing-and-reusing-web-applications.md)