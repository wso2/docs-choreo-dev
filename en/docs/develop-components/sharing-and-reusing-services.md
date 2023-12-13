# Sharing and Reusing Services

Choreo allows you to share and reuse your services, promoting faster development and increased efficiency in building integrated applications, through Connections.

Connections in Choreo allows you to integrate Choreo components, or to integrate Choreo components to external services or resources. Connections provide a simple and uniform way to integrate with services and resources.

To learn more about Choreo Connections refer to the [documentation](../choreo-concepts/connections.md).

## Create a connection to a service

!!! info
    From November 8, 2023, at 10:20 AM UTC, you can create Connections for new components, excluding Ballerina and MI.

To consume a service deployed on Choreo within your service, you need to create a connection to the service you wish to consume. Follow the steps below.

1. In the Choreo Console, go to the top navigation menu, and set the visibility level as [project](../choreo-concepts/connections.md#project-connections), or [component](../choreo-concepts/connections.md#component-connections) as follows: 

    - **Project Connection**: Select an organization and a project in that organization. 
    - **Component Connection**: Select an organization, a project in that organization, and a component in the selected project. 

2. From the left navigation menu, click **Dependencies**  and then **Connections**. This page will list the currently available connections.
3. Click **+Create**. The **Create Connection** page will display the marketplace view for you to browse the available services. You can search and apply filters to find services efficiently.
4. Click on the service to wish to connect to. 
5. Enter a name and a description for the connection and click **Next**.
6. You will receive the ServiceURL for the development and production environments. Click **Finish**.

Now you have created a connection to the service. Let's see how you can consume it. 

## Consuming a service through a connection

You can consume a Choreo-deployed service within another service

Consuming connections from within Choreo services is seamless and straightforward. Follow the steps below to consume a Choreo service. 

### Consuming services 

You can consume a Choreo service as follows: 

1. In the Choreo Console, navigate to the project or component to view the connections in the respective visibility level.
2. In the left navigation menu, click **Dependencies** and then **Connections**. 
3. Click on the Connection you wish to consume. 
4. Refer to the **Developer Guide** pane for instructions on how to consume the relevant service..
