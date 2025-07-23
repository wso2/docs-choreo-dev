# Use a Database Connection in Your Component

Choreo allows you to share and reuse Choreo-managed databases, accelerating development and enhancing efficiency in building integrated applications through connections.

For step-by-step instructions on creating a database connection, see [Create a Connection](create-a-connection.md).

To learn more about Choreo Connections, see the documentation on [Connections](../../choreo-concepts/connections.md).

## Consume a database through a connection

To consume a Choreo-managed database via a connection, follow these steps:

### Step 1: Add connection configurations

To integrate a database into your application, click the appropriate tab below based on your current configuration file and follow the step-by-step instructions:

=== "Component.yaml file (v1.1)"

    1. Copy and paste the snippet from the  developer guide into the `component.yaml` file.

        The following is a sample snippet:

        ``` yaml

        dependencies:
            connectionReferences:
            - name: <CONNECTION_NAME>
              resourceRef: <RESOURCE_IDENTIFIER>

        ```

          | Field            | Description                                                         |
          |------------------|---------------------------------------------------------------------|
          | name             | The name given to the connection.                                   |
          | resourceRef      | A unique, readable identifier of the database being connected to.    |


    2. If you've previously added a `connectionReferences` section under `dependencies`, append this as another item under `connectionReferences`. Upon deploying the component, the necessary configurations to establish the connection will be injected into Choreo-defined environment variables.

          The following table details the Choreo-defined environment variables:

          | Configuration Key       | Choreo-Defined Environment Variable Name                       |
          |-------------------------|----------------------------------------------------------------|
          | HostName                | CHOREO_<CONNECTION_NAME\>_HOSTNAME                             |
          | Port                    | CHOREO_<CONNECTION_NAME\>_PORT                                 |
          | Username                | CHOREO_<CONNECTION_NAME\>_USERNAME                             |
          | Password                | CHOREO_<CONNECTION_NAME\>_PASSWORD                             |
          | DatabaseName            | CHOREO_<CONNECTION_NAME\>DATABASENAME                          |


          If you'd like to use custom environment variable names instead of the Choreo-defined ones, add the dependency as a service reference under `dependencies` in the same file. For more details, refer to the instructions under the `component.yaml file (v1.0)` tab.


          The following table provides details on the configuration keys associated with the connection:

          | Name           |  Type      |  Description                          |Optional       | Sensitive    |
          |----------------|------------|---------------------------------------|---------------|--------------|
          | HostName       | string     | Hostname of the database server       | false         | false        |
          | Port           | string     | Port of the database server           | false         | false        |
          | Username | string           | Username of the database server       | false         | false        |
          | Password       | string     | Password of the database server       | false         | true         |
          | DatabaseName   | string     | Name of the database                  | false         | false        |
    ### Step 2: Read configurations within the application

    Once you add the connection configuration snippet, you can proceed to read those configurations within your application. The steps to follow depend on the programming language you are using.

    The following is a sample code snippet in NodeJS:

    ``` java
    const hostName = process.env.CHOREO_<CONNECTION_NAME>_HOSTNAME;
    ```

=== "Component.yaml file (v1.0)"

    !!! note
        The `component.yaml v1.0` file is considered legacy. For new components, we recommend using the latest version, `component.yaml v1.1`, which offers enhanced usability and features.

    1. Copy and paste the snippet from the  developer guide into the `component.yaml` file.

        The following is a sample snippet:

        ``` yaml

        dependencies:
            serviceReferences:
            - name: database:<DATABASE_NAME>
              connectionConfig: <CONNECTION_ID>
              env:
              - from: HostName
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: Port
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: Username
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: Password
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: DatabaseName
                to: <YOUR_ENV_VARIABLE_NAME_HERE>


        ```

          | Field            | Description                                                 |
          |------------------|-------------------------------------------------------------|
          | name             | The name of the database you are connecting to.             |
          | connectionConfig | The unique connection identifier for the connection.        |
          | env              | The environment variable mapping.                           |
          | from             | The key of the configuration entry.                         |
          | to               | The environment variable name to which Choreo will inject the value of the key.|


    2. Replace `<YOUR_ENV_VARIABLE_NAME_HERE>` with an appropriate environment variable name of your choice. If you have previously added a service reference section under `dependencies`, append this as another item under `serviceReferences`.

          Upon deploying the component, Choreo automatically populates the specified environment variables with actual values.

          The following table provides details on the configuration keys associated with the connection:

          | Name           |  Type      |  Description                          |Optional       | Sensitive    |
          |----------------|------------|---------------------------------------|---------------|--------------|
          | HostName       | string     | Hostname of the database server       | false         | false        |
          | Port           | string     | Port of the database server           | false         | false        |
          | Username | string           | Username of the database server       | false         | false        |
          | Password       | string     | Password of the database server       | false         | true         |
          | DatabaseName   | string     | Name of the database                  | false         | false        |

    <h3> Step 2: Read configurations within the application </h3>

    Once you add the connection configuration snippet, you can proceed to read those configurations within your application. The steps to follow depend on the programming language you are using.

    The following is a sample code snippet in NodeJS:

    ``` java
    const hostName = process.env.HOSTNAME;
    ```

=== "Component-config.yaml file"

    !!! note
        The `component-config.yaml` file is considered legacy. For new components, we recommend using the latest version, `component.yaml v1.1`, which offers enhanced usability and features.

    1. Copy and paste the snippet from the  developer guide into the `component-config` file under the `spec` section.

        The following is a sample snippet:

        ``` yaml

        outbound:
            serviceReferences:
            - name: database:<DATABASE_NAME>
              connectionConfig: <CONNECTION_ID>
              env:
              - from: HostName
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: Port
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: Username
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: Password
                to: <YOUR_ENV_VARIABLE_NAME_HERE>
              - from: DatabaseName
                to: <YOUR_ENV_VARIABLE_NAME_HERE>

        ```

          | Field            | Description                                                 |
          |------------------|-------------------------------------------------------------|
          | name             | The name of the database you are connecting to.              |
          | connectionConfig | The unique connection identifier for the connection.        |
          | env              | The environment variable mapping.                           |
          | from             | The key of the configuration entry.                         |
          | to               | The environment variable name to which Choreo will inject the value of the key.|


    2. Replace `<YOUR_ENV_VARIABLE_NAME_HERE>` with an appropriate environment variable name of your choice. If you have previously added an outbound service reference, append this as another item under `serviceReferences`.

          Upon deploying the component, Choreo automatically populates the specified environment variables with actual values.


          The following table provides details on the configuration keys associated with the connection:

          | Name           |  Type      |  Description                          |Optional       | Sensitive    |
          |----------------|------------|---------------------------------------|---------------|--------------|
          | HostName       | string     | Hostname of the database server       | false         | false        |
          | Port           | string     | Port of the database server           | false         | false        |
          | Username | string           | Username of the database server       | false         | false        |
          | Password       | string     | Password of the database server       | false         | true         |
          | DatabaseName   | string     | Name of the database                  | false         | false        |

    <h3> Step 2: Read configurations within the application </h3>

    Once you add the connection configuration snippet, you can proceed to read those configurations within your application. The steps to follow depend on the programming language you are using.

    The following is a sample code snippet in NodeJS:

    ``` java
    const hostName = process.env.HOSTNAME;
    ```

### Step 3: Initiate a database connection

To initiate a connection to the database, follow these steps:

In this example, you will connect to a MySQL database.

#### Step 3.1: Install the required packages

For the MySQL database, install the `mysql2` package using npm:

```bash
// Install the mysql2 package
npm install mysql2

```

#### Step 3.2: Import required packages

```java

const client = require('mysql2')

```

#### Step 3.3: Establish a connection

To establish the connection, use the environment variables for `hostName`, `username`, `password`, `databaseName`, and `port` as follows:

```java

var connection = client.createConnection({
      host: hostName,
      user: username,
      password: password,
      database: databaseName,
      port: port
});

connection.connect((err) => {
      if (err) {
          return;
      }
      // Connection is successful
});

```

By following these steps, your component can interact with the Choreo-managed database seamlessly.
