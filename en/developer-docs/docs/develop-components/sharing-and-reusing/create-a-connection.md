# Create a Connection

Connections in Choreo provide a simple and uniform approach to seamlessly integrate components with services or resources.

Choreo allows you to create connections to services deployed in Choreo or registered as external services. It also allows you to create connections to any Choreo-managed database.

To create a connection to a service or a database, follow the step-by-step instructions in the respective tab:

=== "Create a connection to a service"

    <h2>Create a connection to a Choreo service</h2>

    Follow these steps to create a connection to a service deployed in Choreo:

    1. In the Choreo Console, go to the top navigation menu and set the visibility level as [project](../../choreo-concepts/connections.md#project-connections) or [component](../../choreo-concepts/connections.md#component-connections) as follows: 

        - **Project Connection**: Select an organization and a project in that organization. 
        - **Component Connection**: Select an organization, a project in that organization, and a component in the selected project. 

    2. In the left navigation menu, click **Dependencies**  and then **Connections**. This page lists all the existing connections.
    3. Click **+Create**. This opens the Marketplace view where you can browse and search for services or databases.
    4. Click the **Services** tab. You can search and apply filters to efficiently find a service.
    5. Click on the service you want to connect to. 
    6. Enter a name and a description for the connection.
    7. Select an **Access Mode** and **Authentication Scheme** for the connection.
    8. Click **Create**.
   
    This creates the connection and displays its details for each environment, along with an inline guide on how to use the connection in your component. 

    !!! note
        During connection creation, secret values for the lowest environment are visible, allowing you to copy them for local use if necessary. Secret values for higher environments remain hidden to ensure security
     
    <h2>Create a connection to an external service</h2>

    Follow these steps to create a connection to an external service:

    1. Follow steps 1–5 from the previous section.
    2. Expand the **Environment to Endpoint** section and verify the service endpoints assigned to the Choreo environments.
    3. Click **Create**.

      For step-by-step instructions on using a connection in your service, see [Use a Connection in Your Service](./use-a-connection-in-your-service.md).

      For step-by-step instructions on using a connection in your web application, see [Use a Connection in Your Web Application](./use-a-connection-in-your-web-application.md).


=== "Create a connection to a database"

    Prerequisites:

     - Create a Choreo-managed database. For details, see [Choreo-Managed Databases and Caches](../../manage-databases-and-caches/choreo-managed-databases-and-caches.md).
     - Add the database to the Marketplace. For details, see [Add Choreo-Managed Databases and Caches to the Marketplace](../../manage-databases-and-caches/add-choreo-managed-databases-and-caches-to-the-marketplace.md).

    Follow these steps to create a connection to a Choreo-managed database:

    1. In the Choreo Console, go to the top navigation menu and set the visibility level as [project](../../choreo-concepts/connections.md#project-connections) or [component](../../choreo-concepts/connections.md#component-connections) as follows: 

        - **Project Connection**: Select an organization and a project in that organization. 
        - **Component Connection**: Select an organization, a project in that organization, and a component in the selected project. 

    2. In the left navigation menu, click **Dependencies**  and then **Connections**. This page lists all the existing connections.
    3. Click **+Create**. This opens the Marketplace view where you can browse and search for services or databases.
    4. Click the **Databases** tab. You can search and apply filters to efficiently find a database.
    5. Click on the database you want to connect to. 
    6. To create the connection, follow these steps:

        1. Enter a name and description.
        2. Under **Environment Configuration**, select credentials for each environment.

            !!! note
                 By default, the selected database is applied to all environments. To use different databases for specific environments, select the appropriate database and provide the corresponding credentials for each environment.

        3. Click **Create**.  
    
    This creates the connection and displays the database connection details for each environment, along with an inline guide on how to use the connection in your component. 

    !!! note
        During connection creation, secret values for the lowest environment are visible, allowing you to copy them for local use if necessary. Secret values for higher environments remain hidden to ensure security
    
    For step-by-step instructions on using a database in your component, see [Use a Database Connection in Your Component](./use-a-database-connection-in-your-component.md).
