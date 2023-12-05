# Sharing and Reusing Services

Choreo allows you to share and reuse your services, promoting faster development and increased efficiency in building integrated applications, through Connections.

Connections in Choreo allows you to integrate Choreo components, or to integrate Choreo components to external services or resources. Connections provide a simple and uniform way to integrate with services and resources.

To learn more about Choreo Connections refer to the [documentation](../choreo-concepts/connections.md).

## Create a connection to a service

To consume a service deployed on Choreo within your service, you need to create a connection to the service you wish to consume. Follow the steps below.

1. In the Choreo Console, go to the top navigation menu, and set the visibility level as [organization](../choreo-concepts/connections.md#organization-connections), [project](../choreo-concepts/connections.md#project-connections), or [component](../choreo-concepts/connections.md#component-connections) as follows: 

    - **Organization Connection** : Select only an organization.
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

### Consuming services via the Choreo console

You can consume a Choreo service via another service you plan to deploy in Choreo as follows: 

1. In the Choreo console navigate to the project or component to view the connections in the respective visibility level.
2. In the left navigation menu, click **Dependencies** and then **Connections**. The **Create Connection** page will display the marketplace view for you to browse the available services. You can search and apply filters to find services efficiently.
3. Click on the service you wish to consume. 
4. Refer to the **Adding Connection Configuration to Application** section in your component-config file. 
5. Replace the placeholder <YOUR_ENVIRONMENT_VAR_HERE> with the environment variable names. When you deploy your component, the configured environment variables will be available with their values. Refer to the service's **Read configurations** section (exploring the service via the Choreo marketplace) to learn how to read these environment variables and use them in your code.  
6. Refer to the entire **Developer Guide** pane to learn how to obtain a token and consume the service.

### Consuming Choreo services via external applications

You can consume a service deployed on Choreo via an external application. To consume a Choreo deployed service you need to create a project-level connection. You can then consume the connection as follows: 

1. In the Choreo console, select the project to view the connections in the respective visibility level.
2. In the left navigation menu, click **Dependencies** and then **Connections**. The **Create Connection** page will display the marketplace view for you to browse the available services. You can search and apply filters to find services efficiently.
3. Click on the service you wish to consume. 
4. Refer to the **Adding Connection Configuration to Application** section in your component-config file. 
5. Replace the placeholder <YOUR_ENVIRONMENT_VAR_HERE> with the environment variable names. When you deploy your component, the configured environment variables will be available with their values. Refer to the service's **Read configurations** section (exploring the service via the Choreo marketplace) to learn how to read these environment variables and use them in your code.  
6. Refer to the entire **Developer Guide** pane to learn how to obtain a token and consume the service.
