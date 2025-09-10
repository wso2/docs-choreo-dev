# Choreo-managed MySQL Databases

MySQL on Choreo offers fully managed, flexible relational databases on AWS, Azure, GCP, and Digital Ocean.

These services run on infrastructure and automation provided by [Aiven](https://aiven.io), our technology partner and data platform subprocessor. For details on the partnership, SLA, and security posture, see the overview (./choreo-managed-databases-and-caches.md#technology-partnership).

## Create a Choreo-managed MySQL database

Follow the steps below to create a Choreo-managed MySQL database: 

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **DB & Services** and then **Databases**.
4. Click **Create** and select **MySQL** as the database type. Provide a display name for this server and follow the instructions.
5. Select your preferred cloud provider from AWS, Azure, GCP, and Digital Ocean.
  - Choreo uses the cloud provider to provision the compute and storage infrastructure for your database.
  - There is no functional difference between databases created on different cloud providers, apart from changes to service plans (and associated costs). 
6. Choose the region for your database.
  - Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.
7. Select the service plan.
  - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your database, the backup retention periods, and high-availability configurations for production use cases.

!!! Note -
      Once the database is created, you can add it to the marketplace so developers can discover these databases and use them in their applications. For more details, see [Add Choreo-Managed Databases and Caches to the Marketplace.](../add-choreo-managed-databases-and-caches-to-the-marketplace) 

## Connect to your Choreo-managed MySQL database

To connect to your Choreo-managed MySQL database, consider the following guidelines:

- MySQL databases accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.

- You can use any MySQL driver, ORM, or supported generic SQL library (may depend on the programming language) to connect to the database.
- You can find the connection parameters in the **Overview** section in the Choreo Console under the relevant database.

## High Availability and Automatic Backups

The high availability characteristics and the automatic backup retention periods for Choreo-managed MySQL databases vary based on your service plan as explained below:

| Service Plan | High Availability                                                  | Backup Retention Time | Multi-AZ Deployment |
|--------------|--------------------------------------------------------------------|-----------------------|---------------------|
| Hobbyist     | Single-node with limited availability                              | None                  | No                  |
| Startup      | Single-node with limited availability                              | 2 days                | No                  |
| Business     | Two-node (primary + standby) with higher availability              | 14 days               | Yes*                |
| Premium      | Three-node (primary + standby + standby) with highest availability | 30 days               | Yes*                |

*Multi‑AZ availability depends on the cloud provider and region and is enabled where supported.

In general, we recommend service plans for production scenarios for multiple reasons:
- Provides another physical copy of the data in case of hardware, software, or network failures.
- Typically reduces the data loss window in disaster scenarios.
- Provides a quicker time to restore with a controlled failover in case of failures, as the standby is already installed and running.

### Automatic Backups

- Daily full backups of MySQL databases are taken automatically
- Logs are recorded to support point‑in‑time recovery where applicable
- All backups are encrypted at rest
- Backups are managed automatically during recovery; manual backup point selection is not supported

- Choreo automatically handles outages and software failures by replacing broken nodes with new ones that resume correctly from the point of failure. The impact of a failure will depend on the number of available standby nodes in the database.

### Failure Recovery

- Minor failures (e.g., process restarts or transient network issues) are handled automatically without requiring changes to the deployment
- For severe failures (e.g., node loss), monitoring detects the issue and schedules a replacement node automatically
- In database failover scenarios, the Service URI remains the same; the IP address changes to point to the new primary node
- For single‑node tiers, the service is unavailable during restoration and some recent writes may not be recoverable

Typical outcomes in multi‑node tiers include automatic failover within minutes and recovery workflows designed to minimize data loss.

## Monitoring and Observability

- Runtime metrics (CPU, memory, disk, network) and MySQL logs are available in the Choreo Console
- Native alerting for resource spikes is not currently available; contact Choreo support if you need to export metrics to third‑party monitoring for custom alerting.

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
