# Choreo-Managed Cache

Fully compatible with legacy Redis® OSS.

Choreo-Managed Cache provides fully-managed in-memory NoSQL databases on AWS, Azure, GCP, and Digital Ocean and can be used as a cache, database, streaming engine, or message broker.

## Create a Choreo-Managed Cache

Follow the steps below to create a Choreo-Managed Cache:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Databases**.
4. Click **+ Create** and select **Choreo-Managed Cache** as the data store type. Provide a display name for this server and follow the instructions.
5. Select a preferred cloud provider (AWS, Azure, GCP, or Digital Ocean).
    - The cloud provider provisions the compute and storage infrastructure for your data store.
    - The functionality remains the same across cloud providers, though service plans and costs may differ.
6. Select a region for your data store.
    - Available regions depend on the selected cloud provider. Choreo currently supports US and EU regions across all providers.
7. Select a service plan.
    - Service plans vary in terms of dedicated CPU, memory (RAM), and storage space allocated for your data store, as well as high-availability configurations for production use cases.
8. Click **Create**.

## Connect to your Choreo-Managed Cache

To connect to your Choreo-Managed Cache, follow these guidelines:

- Use any legacy Redis® OSS compatible driver (in any programming language) to connect to your Choreo-Managed Cache.
- You can find the connection parameters in the **Overview** section in the Choreo Console under the relevant database. Note that Choreo-Managed Cache enforces TLS.
- Choreo-Managed Cache instances accept traffic from the internet by default. You can restrict access to specific IP addresses and CIDR blocks under **Advanced Settings**.

## High availability and automatic backups

The high availability and the automatic backup retention periods for a Choreo-Managed Cache can vary as follows depending on the service plan you select. 

| Service plan | High availability                                                                                                  | Backup features                          | Backup history |
| ------------ | -------------------------------------------------------------------------------------------------------------------| ---------------------------------------- | -------------- |
| Hobbyist     | Single-node with limited availability.                                                                             | Single backup only for disaster recovery | None           |
| Startup      | Single-node with limited availability.                                                                             | Single backup only for disaster recovery | 1 day          |
| Business     | Two-node (primary + standby) with higher availability (automatic failover if the primary node fails).              | Automatic backups                        | 3 days         |
| Premium      | Three-node (primary + standby + standby) with highest availability (automatic failover if the primary node fails). | Automatic backups                        | 13 days        |

In general, service plans are recommended for production scenarios due to the following reasons:

- Provides another physical copy of the data in case of hardware, software, or network failures.
- Reduces the data loss window in disaster scenarios.
- Ensures quicker restoration with controlled failover in case of failures, as the standby is already installed and running.

### Automatic backups

- Choreo runs full backups daily to automatically backup Choreo-Managed Caches and has write-ahead logs (WAL) copied at 5-minute intervals or for every new file generated.
- Choreo encrypts all backups at rest.
- Choreo automatically handles outages and software failures by replacing broken nodes with new ones that resume correctly from the point of failure. The impact of a failure will depend on the number of available standby nodes in the data store.

### Failure recovery

- **Minor failures**: Choreo automatically handles minor failures such as service process crashes or temporary loss of network access in all plans without requiring significant changes to the service deployment. Choreo automatically restores the service to normal operation once it automatically restarts the crashed process or when it restores the network access.

- **Severe failures**: Failures such as losing a node entirely in case of hardware or severe software problems, require more drastic recovery measures. The monitoring infrastructure automatically detects a failing node when the node starts reporting issues in the self-diagnostics or when it stops communicating. In such cases, the monitoring infrastructure automatically schedules a new replacement node to be created.
     - In the event of a data store failover, the service URI of your service remains the same; only the IP address will change to point to the new primary node.
     - Hobbyist and Startup plans provide a single node, and in case of failure, a new node starts up, restores its state from the latest available backup, and resumes serving traffic.
     - As there is just a single/primary node, the caching service becomes unavailable for the duration of the restoration operation. Therefore, all write operations made since the last backup will be lost.

## Limitations

### Connection limits

The number of simultaneous connections in a Choreo-Managed Cache depends on the total available memory on the server instances.

You can use the following to estimate:

```
max_number_of_connections = 4 x m
```

Here, `m` represents the memory in megabytes, where at least 10,000 connections are available, even on the smallest servers.
For example, on a server with 4GB memory (4,096 MB), the simultaneous connections are:

```
4 x 4096 = 16384 // 16k connections
```

This number is estimated by the exact available memory so it can vary between different plans and cloud providers. To see the exact maximum connections allowed, use the \* redis-cli and info command as follows:

```
echo "info" | redis-cli -u REDIS_URI | grep maxclients
```

### Restricted commands

To maintain the stability and security of a managed environment, Choreo restricts certain commands on Choreo-Managed Cache services.

!!! note "Support for Lua scripts on Choreo-Managed Cache"
     - Choreo-Managed Cache has built-in support for running Lua scripts to perform various actions directly on the server. Scripting is typically controlled using the `EVAL`, `EVALSHA`, and `SCRIPT LOAD` commands.
     - For all newly-created cache instances, `EVAL`, `EVALSHA`, and `SCRIPT LOAD` commands are enabled by default.

The following commands are disabled on Choreo:

- `bgrewriteaof`: Initiates a background append-only file rewrite.
- `cluster`: Manages Caching cluster commands.
- `command`: Provides details about all Caching commands.
- `debug`: Contains sub-commands for debugging Caching.
- `failover`: Manages manual failover of a master to a replica.
- `migrate`: Atomically transfers a key from one caching instance to another.
- `role`: Returns the role of the instance in the context of replication.
- `slaveof`: Makes the server a replica of another instance, or promotes it as master.
- `acl`: Manages caching access control lists.
- `bgsave`: Creates a snapshot of the data set into a dump file.
- `config`: Alters the configuration of a running caching server.
- `lastsave`: Returns the UNIX timestamp of the last successful save to disk.
- `monitor`: Streams back every command processed by the Caching server.
- `replicaof`: Makes the server a replica of another instance.
- `save`: Synchronously saves the dataset to disk.
- `shutdown`: Synchronously saves the dataset to disk and then shuts down the server.

The following `eval` commands are also disabled:

- `eval`: Executes a Lua script server-side.
- `eval_ro`: Read-only variant of the eval command.
- `evalsha`: Executes a script cached on the server side by its SHA1 digest.
- `evalsha_ro`: Read-only variant of the evalsha command.
- `fcall`: Calls a Caching function.
- `fcall_ro`: Read-only variant of the fcall command.
- `function`: Manages Caching functions.
- `script`: Manages the script cache.
