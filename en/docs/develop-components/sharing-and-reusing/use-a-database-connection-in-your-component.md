# Use a Database Connection in Your Component

Choreo allows you to share and reuse Choreo-managed databases, accelerating development and enhancing efficiency in building integrated applications through connections.

For step-by-step instructions on creating a database connection, see [Create a Connection](create-a-connection.md).

To learn more about Choreo Connections, see the documentation on [Connections](../../choreo-concepts/connections.md).

## Consume a database through a connection

To consume a Choreo-managed database via a connection, follow these steps:

### Step 1: Add connection configurations

1. Copy and paste the snippet from the inline developer guide into the `component-config` file under the `spec` section.

    The following is a sample snippet:

    ``` yaml

    outbound:
        serviceReferences:
        - name: database:hrdbProd
          connectionConfig: 01ef700c-e378-138e-a11f-6e8e5a924f54
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
      | Name             | The name of the database you are connecting to.             |
      | ConnectionConfig | The unique connection identifier for the connection.        |
      | env              | The environment variable mapping.                           |
      | from             | The key of the configuration entry.                         |
      | to               | The environment variable name to which Choreo will inject the value of the key.|


2. Replace `<YOUR_ENV_VARIABLE_NAME_HERE>` with appropriate environment variable names. If you have already added an outbound service reference, append this as another entry under `serviceReferences`. 

      The following table provides details on the configuration keys associated with the connection:

      | Name         |  Type   |  Description                                                         |Optional       | Sensitive    |
      |--------------|---------|----------------------------------------------------------------------|---------------|--------------|
      | HostName     | string  | The hostname of the database server where the database resides.      | false         | false        |
      | Port         | string  | The port number on which the database server listens for connections.| false         | false        |
      | Username     | string  | The username for database access.                                    | false         | true         |
      | Password     | string  | The password for database access.                                    | false         | false        |
      | DatabaseName | string  | The name of the database to connect.                                 | false         | false        |

### Step 2: Read configurations within the application

Once you add the connection configuration snippet, you can read the configurations within your application. The steps to follow depend on the programming language you are using.

The following is a sample code snippet in Ballerina:

``` java
const hostName = process.env.HOST_NAME;
```

### Step 3: Initiate a database connection

To initiate a connection to the database, follow these steps:

In this example, you will connect to a MySQL database.

#### Step 3.1: Install the required packages

For the MySQL database, install the `mysql2` package using npm:

``` bash
// Install the mysql2 package
npm install mysql2

```

#### Step 3.2: Import required packages        

``` java

const client = require('mysql2')

```

#### Step 3.3: Establish a connection

To establish the connection, use the environment variables for `hostName`, `username`, `password`, `databaseName`, and `port` as follows:

``` java

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
