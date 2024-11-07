# Configure a Kafka Service

After creating a Kafka service, you can create topics, configure advanced settings, and manage access to ensure secure and efficient message processing.

## Create a Kafka topic

Kafka topics are logical channels used to organize and transfer messages between producers and consumers. They form the core of Kafka's architecture, enabling data streams to be written and read. Kafka topics can also be partitioned to improve scalability and fault tolerance.

To create a Kafka topic, follow these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. In the **Message Brokers** list, click the Kafka service for which you want to create a topic.
5. Click the **Topics** tab.
6. Click **+ Create**.
7. In the **Create Topic** dialog that opens, 
   1. Enter a name for the topic.
   2. To configure advanced settings, click to expand **Advanced Configurations** and apply the required settings. For details, see [Advanced topic configurations](#advanced-topic-configurations).
8. Click **Create**.

### Advanced topic configurations

Choreo allows you to customize settings for Kafka topics, including the number of partitions, replication factor, cleanup policy, retention size (bytes), retention duration (hours), and the minimum number of in-sync replicas.

**Cleanup Policy:**  Determines how messages that reach their retention limit are handled. There are three options:  

- Delete (default): Messages are removed when the retention limit is exceeded. 
- Compact: Retains only the latest record for each unique key, ensuring only the most recent data is kept.  
- Compact and Delete: Combines both approaches, where old records are deleted based on retention limits, but the latest record for each key is retained.

**Replication:** Sets the number of partition copies to ensure data durability. The default is 3.

**Partitions:** Defines the number of segments the topic should be divided into for parallel processing. The default is 1 partition.

**Retention Bytes:** Sets the maximum size of retained messages. Older messages are discarded on reaching this limit. The default is unlimited (-1).

**Retention Hours:** Defines the retention period for messages before deletion. The default is 168 hours (7 days).

**Min In-Sync Replicas:** Specifies the minimum number of replicas that must acknowledge a write for it to be considered successful. The default is 2.

## Manage service users and access control lists

Choreo-managed Kafka services use access control lists (ACLs) and user definitions to control access to topics. You can manage users and ACL entries from the corresponding tabs on the Kafka service details page.

### Manage users

To manage users for a Choreo-managed Kafka service, follow these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. In the **Message Brokers** list, click the Kafka service for which you want to manage users.
5. Click the **Users** tab.
6. Click **+ Add User**, specify a username, and then click **Add**.

By default, new users do not have any permissions. You can grant necessary permissions to users through access control lists (ACLs).

### Configure access control lists (ACLs)

An ACL entry defines access permission for a user. Each entry includes:

 - Username: The username.
 - Topic: The associated Kafka topic to grant access to the user.
 - Permission: The permission to grant the user. 

To add an ACL entry, follow these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. In the **Message Brokers** list, click the Kafka service for which you want to add an ACL entry.
5. Click the **Access Control List** tab.
6. Click **+ Add Entry**, select a username, topic, and permission.
7. Click **Add**.
