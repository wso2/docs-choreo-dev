# Create a Redis data store in Choreo

Redis on Choreo offers fully Choreo-managed in-memory data stores on AWS, Azure, GCP, and Digital Ocean and can be used as a cache, database, streaming engine, and a message broker.

## Create a Redis data store

Follow the steps below to create a Redis data store in Choreo: 

1. From the environment list on the header, located next to the **Deployment Tracks** list, select your **Organization**.
2. In the left navigation menu, click **Dependencies** and then **Databases**.
3. Click 'Create' and select 'Redis' as the data store type. Provide a display name for this server and follow the instructions.
4. Select your preferred cloud provider (AWS, Azure, GCP or Digital Ocean).
   - The cloud provider is used to provision the compute and storage infrastructure for your data store.
   - There is no functional difference between databases created on different cloud providers, apart from changes to service plans (and associated costs).
5. Choose the region for your data store.
   - Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.
6. Select the service plan.
   - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your data store and high-availability configurations for production use-cases.

## Connecting to your Redis data store on Choreo

To connect to your Redis data store on Choreo, consider the following guidelines:

- You can connect to Redis on Choreo using any Redis driver (in any programming language).[See recommended Redis clients on the project website.](https://redis.io/resources/clients/)
- The connection parameters can be found in the **Overview** section in the Choreo Console under the relavent database. Note that Redis on Choreo enforces TLS.
- Redis instances accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.
