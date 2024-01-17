# Choreo-managed MySQL Databases

MySQL on Choreo offers fully managed, flexible relational databases on AWS, Azure, GCP, and Digital Ocean.

## Create a Choreo-managed MySQL database

Follow the steps below to create a Choreo-managed MySQL database: 

1. From the environment list on the header, located next to the **Deployment Tracks** list, select your **Organization**.
2. In the left navigation menu, click **Dependencies** and then **Databases**.
3. Click **Create** and select **MySQL** as the database type. Provide a display name for this server and follow the instructions.
4. Select your preferred cloud provider from AWS, Azure, GCP, and Digital Ocean.
  - Choreo uses the cloud provider to provision the compute and storage infrastructure for your database.
  - There is no functional difference between databases created on different cloud providers, apart from changes to service plans (and associated costs). 
5. Choose the region for your database.
  - Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.
6. Select the service plan.
  - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your database, the backup retention periods, and high-availability configurations for production use cases.

## Connect to your Choreo-managed MySQL database

To connect to your Choreo-managed MySQL database, consider the following guidelines:

- You can use any MySQL driver, ORM, or supported generic SQL library (may depend on the programming language) to connect to the database.
- You can find the connection parameters in the **Overview** section in the Choreo Console under the relevant database.
- MySQL databases accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.

## High Availability and Automatic Backups

The high availability characteristics and the automatic backup retention periods for Choreo-managed MySQL databases vary based on your service plan as explained below:

| Service Plan | High Availability                                                  | Backup Retention Time |
|--------------|--------------------------------------------------------------------|-----------------------|
| Hobbyist     | Single-node with limited availability                              | None                  |
| Startup      | Single-node with limited availability                              | 2 days                |
| Business     | Two-node (primary + standby) with higher availability              | 14 days               |
| Premium      | Three-node (primary + standby + standby) with highest availability | 30 days               |

In general, we recommend service plans for production scenarios for multiple reasons:
- Provides another physical copy of the data in case of hardware, software, or network failures.
- Typically reduces the data loss window in disaster scenarios.
- Provides a quicker time to restore with a controlled failover in case of failures, as the standby is already installed and running.

### Automatic Backups

- Choreo runs full backups daily to automatically back up Choreo-managed MySQL databases and record binary logs continuously.
Choreo encrypts all backups at rest.

- Choreo automatically handles outages and software failures by replacing broken nodes with new ones that resume correctly from the point of failure. The impact of a failure will depend on the number of available standby nodes in the database.

## Connection Limits

The maximum number of simultaneous connections to MySQL databases is fixed for each service plan and depends on how much RAM your service plan offers.

!!! note
    An `extra_connection` with a value of `1` is added for system processes for all MySQL databases, regardless of the service plan.

### For plans under 4 GiB RAM

For plans under 4 GiB of RAM, the number of allowed connections is `75` per GiB:

```
max_connections = 75 x RAM + extra_connection
```

### For plans with over 4 GiB RAM:

For plans with 4 GiB or more RAM, the number of allowed connections is `100` per GiB:

```
max_connections = 100 x RAM + extra_connection
```
