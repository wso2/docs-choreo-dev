# Redis on Choreo

Redis on Choreo offers fully-managed in-memory data stores on AWS, Azure, GCP and Digital Ocean and can be used as a cache, database, streaming engine and a message broker.

## Create a Redis data store

1. Navigate to your **Organization > Dependencies > Databases**.

2. Click 'Create' and select 'Redis' as the data store type. Provide a display name for this server and follow the instructions.

3. Select your preferred cloud provider (AWS, Azure, GCP or Digital Ocean).

   > The cloud provider is used to provision the compute and storage infrastructure for your data store.
   > Apart from changes to service plans (and associated costs) there is are no functional difference between data stores created on different cloud providers.
   > If you are on a Private Azure/AWS/GCP Choreo Data Plane you can select with your preferred provider.

4. Choose the region for your data store.

   > Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.

5. Select the service plan.
   > Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your data store and high-availability configurations for production use-cases.

## Connecting to your Redis data store on Choreo

- You can connect to Redis on Choreo using any Redis driver (in any programming language).
  [See recommended Redis clients on the project website.](https://redis.io/resources/clients/)

- The connection parameters can be found in the **Overview** section in the Choreo Console under the relavent database. Note that Redis on Choreo enforces TLS.

- Redis instances accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.
