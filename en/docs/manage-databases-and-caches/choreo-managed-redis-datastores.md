# Choreo-Managed Redis Data Stores

Redis on Choreo offers fully Choreo-managed in-memory data stores on AWS, Azure, GCP, and Digital Ocean and can be used as a cache, database, streaming engine, and message broker.

## Create a Redis data store

Follow the steps below to create a Choreo-managed Redis data store: 

1. From the environment list on the header, located next to the **Deployment Tracks** list, select your **Organization**.
2. In the left navigation menu, click **Dependencies** and then **Databases**.
3. Click **Create** and select **Redis** as the data store type. Provide a display name for this server and follow the instructions.
4. Select your preferred cloud provider (AWS, Azure, GCP, or Digital Ocean).
   - The cloud provider is used to provision the compute and storage infrastructure for your data store.
   - There is no functional difference between databases created on different cloud providers, apart from changes to service plans (and associated costs).
5. Choose the region for your data store.
   - Available regions will depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.
6. Select the service plan.
   - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your data store, and high-availability configurations for production use cases.

## Connecting to your Redis data store on Choreo

To connect to your Choreo-managed Redis data store, consider the following guidelines:

- You can use any Redis driver (in any programming language) to connect to your data store.[See recommended Redis clients on the project website.](https://redis.io/resources/clients/) 
- You can find the connection parameters in the **Overview** section in the Choreo Console under the relevant database. Note that Redis on Choreo enforces TLS.
- Redis instances accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.

## High Availability and Automatic Backups

The high availability characteristics and the automatic backup retention periods for Choreo-managed Redis data stores vary based on the service plan you select as explained below:

| Service Plan | High Availability                                                                                              | Backup Features                          | Backup History |
|--------------|----------------------------------------------------------------------------------------------------------------|------------------------------------------|----------------|
| Hobbyist     | Single-node with limited availability.                                                                         | Single backup only for disaster recovery | None           |
| Startup      | Single-node with limited availability.                                                                         | Single backup only for disaster recovery | 1 days         |
| Business     | Two-node (primary + standby) with higher availability (automatic failover if the primary node fails)               | Automatic backups                        | 3 days         |
| Premium      | Three-node (primary + standby + standby) with highest availability  (automatic failover if the primary node fails) | Automatic backups                        | 13 days        |

In general, we recommend service plans for production scenarios for multiple reasons:
- Provides another physical copy of the data in case of hardware, software, or network failures.
- Typically reduces the data loss window in disaster scenarios.
- Provides a quicker time to restore with a controlled failover in case of failures, as the standby is already installed and running.

### Automatic Backups

- Choreo runs full backups daily to automatically back up Choreo-managed Redis data stores and write-ahead logs (WAL) copied at 5-minute intervals or for every new file generated. 
Choreo encrypts all backups at rest.

- Choreo automatically handles outages and software failures by replacing broken nodes with new ones that resume correctly from the point of failure. The impact of a failure will depend on the number of available standby nodes in the data store.

### Failure Recovery

- **Minor failures**:  Choreo automatically handles minor failures such as service process crashes or temporary loss of network access in all plans without requiring significant changes to the service deployment. Choreo automatically restores the service to normal operation once Choreo automatically restarts the crashed process or when Choreo restores the network access.

- **Severe failures**, such as losing a node entirely in case of hardware or severe software problems, require more drastic recovery measures. The monitoring infrastructure automatically detects a failing node when the node starts reporting issues in the self-diagnostics or when it stops communicating. In such cases, the monitoring infrastructure automatically schedules a new replacement node to be created.
> - In the event of data store failover, the Service URI of your service remains the same; only the IP address will change to point to the new primary node.
> - Hobbyist and Startup plan provide a single node, and in case of failure, a new node starts up, restores its state from the latest available backup, and resumes serving traffic.
As there is just a single/primary node, the Redis service will become unavailable for the duration of the restoration operation. All write operations made since the last backup will be lost.

## Limitations

### Connection Limits

The number of simultaneous connections in Choreo-managed Redis depends on the total available memory on the Redis server.

You can use the following to estimate:

```
max_number_of_connections = 4 x m
```

where `m` represents the memory in megabytes. With at least 10,000 connections available, even on the smallest servers. 
For example, on a server with 4GB memory (4,096 MB), the simultaneous connections are:

```
4 x 4096 = 16384 // 16k connections
```

This number is estimated by the exact available memory so it varies between different plans and cloud providers, to see the exact maximum connections allowed, use * redis-cli and info command as the following:

```
echo "info" | redis-cli -u REDIS_URI | grep maxclients
```

### Restricted Redis Commands

To maintain the stability and security of a managed Redis environment, Choreo restricts certain Redis commands on Choreo-managed Redis services.

!!! note "Support for Lua scripts on Redis"
      Redis has built-in support for running Lua scripts to perform various actions directly on the Redis server. Scripting is typically controlled using the `EVAL`, `EVALSHA`, and `SCRIPT LOAD` commands.
      For all newly-created Redis instances, `EVAL`, `EVALSHA` and `SCRIPT LOAD` commands are enabled by default.

The following Redis commands are disabled on Choreo:

- `bgrewriteaof`: Initiates a background append-only file rewrite.
- `cluster`: Manages Redis cluster commands.
- `command`: Provides details about all Redis commands.
- `debug`: Contains sub-commands for debugging Redis.
- `failover`: Manages manual failover of a master to a replica.
- `migrate`: Atomically transfers a key from a Redis instance to another one.
- `role`: Returns the role of the instance in the context of replication.
- `slaveof`: Makes the server a replica of another instance, or promotes it as master.
- `acl`: Manages Redis Access Control Lists.
- `bgsave`: Creates a snapshot of the dataset into a dump file.
- `config`: Alters the configuration of a running Redis server.
- `lastsave`: Returns the UNIX timestamp of the last successful save to disk.
- `monitor`: Streams back every command processed by the Redis server.
- `replicaof`: Makes the server a replica of another instance.
- `save`: Synchronously saves the dataset to disk.
- `shutdown`: Synchronously saves the dataset to disk and then shuts down the server.

The following eval commands are disabled as well:

- `eval`: Executes a Lua script server-side.
- `eval_ro`: Read-only variant of the eval command.
- `evalsha`: Executes a script cached on the server side by its SHA1 digest.
- `evalsha_ro`: Read-only variant of the evalsha command.
- `fcall`: Calls a Redis function.
- `fcall_ro`: Read-only variant of the fcall command.
- `function`: Manages Redis functions.
- `script`: Manages the script cache.
