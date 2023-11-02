# Choreo-managed MySQL Databases

MySQL on Choreo offers fully-managed, flexible relational databases on AWS, Azure, GCP, and Digital Ocean.

## Create a Choreo-managed MySQL database

Follow the steps below to create a Choreo-managed MySQL database: 

1. From the environment list on the header, located next to the **Deployment Tracks** list, select your **Organization**.
2. In the left navigation menu, click **Dependencies** and then **Databases**.
3. Click **Create** and select **MySQL** as the database type. Provide a display name for this server and follow the instructions.
4. Select your preferred cloud provider from AWS, Azure, GCP, and Digital Ocean.
  - The cloud provider is used to provision the compute and storage infrastructure for your database.
  - There is no functional difference between databases created on different cloud providers, apart from changes to service plans (and associated costs). 
5. Choose the region for your database.
  - Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.
6. Select the service plan.
  - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your database, the backup retention periods, and high-availability configurations for production use-cases.

## Connecting to your Choreo-managed MySQL database

To connect to your Choreo-managed MySQL database, consider the following guidelines:

- You can use any MySQL driver, ORM, or supported generic SQL library (may depend on the programming language) to connect to the database.
- The connection parameters can be found in the **Overview** section in the Choreo Console under the relavent database.
- MySQL databases accepts traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.
