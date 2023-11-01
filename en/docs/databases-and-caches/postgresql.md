# PostgreSQL on Choreo

PostgreSQL on Choreo offers fully-managed, performant object-relational databases on AWS, Azure, GCP and Digital Ocean.

## Create a PostgreSQL database

1. Navigate to your Organization page and click on 'Cloud Storage'.

2. Click 'Create' and select 'PostgreSQL' as the database type. Provide a display name for this server and follow the instructions.

3. Select your preferred cloud provider (AWS, Azure, GCP or Digital Ocean).
  > The cloud provider is used to provision the compute and storage infrastructure for your database.
  > Apart from changes to service plans (and associated costs) there is are no functional difference between databases created on different cloud providers.
  > If you are on a Private Azure/AWS/GCP Choreo Data Plane you can select with your preferred provider.

4. Choose the region for your database.
   > Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.

5. Select the service plan.
   > Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your database and also the backup retention periods and high-availability configurations for production use-cases.

## Connecting to your PostgreSQL database on Choreo

- You can connect to PostgresQL on Choreo using any PostgreSQL driver, ORM or supported generic SQL library (may depend on the programming language).

- The connection parameters can be found in the **Overview** section in the Choreo Console under the relavent database.

- PostgreSQL databases accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.
