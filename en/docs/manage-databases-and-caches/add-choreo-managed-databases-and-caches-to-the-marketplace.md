# Add Choreo-Managed Databases and Caches to the Marketplace

When you create a Choreo-managed database or cache, you have the option to add it to the Marketplace, making it available for consumption through a connection.

!!! note 
     To add a Choreo-managed database or cache to the Marketplace, you must register at least one credential for it.

## Step 1: Register database or cache credentials

When you create a database or cache server, you get super admin credentials by default. You can use these credentials to create new user credentials. The steps to create new user credentials depend on the type of database or cache you are using.

You can register either the default super admin credentials or any custom credentials you create using the super admin credentials.

To register credentials for a database, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. Go to the Choreo Console header and select your organization from the **Organization** list.
3. In the left navigation menu, click **Dependencies** and then click **Databases**.
4. Click on a required database to view its details.
5. Click the **Databases** tab.
6. Click to expand the database you want to register credentials for, then click **Add Credentials**.
7. In the **Add Credentials** dialog, do one of the following depending on your requirements:
    - If you want to use the custom credentials you created using the super admin credentials, select **Add New Credentials** and specify appropriate values for each of the fields.
    - If you want to use the default super admin credentials, select **Add Super Admin Credentials** and specify appropriate values for each of the fields.
8. Click **Save**.

## Step 2: Add the database or cache to the Marketplace

- On the **Databases** tab, click +Add corresponding to the database you want to add to the Marketplace. 

Once the database is added to the Marketplace, it can be consumed via a connection. For details on creating a connection to a Choreo-managed database, see [Create a connection to a database](../develop-components/sharing-and-reusing/create-a-connection.md).

For details on using a database connection in your component, see [Use a Database Connection in Your Component](../develop-components/sharing-and-reusing/use-a-database-connection-in-your-component.md).
