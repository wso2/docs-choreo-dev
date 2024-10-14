# Configure a Kafka Service

After you create a Kafka service, you can create topics, fine-tune advanced settings, and control access to ensure secure and efficient message handling. 

## Create a Kafka Topic

Kafka topics are logical channels for organizing and distributing messages between producers and consumers. They serve as the core units where data streams are written and read, and can also be partitioned for scalability and fault tolerance.

To create a Kafka topic for your Kafka service, follow these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. In the **Message Brokers** list, click the Kafka service for which you want to create a topic.
5. Click the **Topics** tab.
6. Click **+ Create**.
7. In the **Create Topic** dialog that opens, 
   1. Enter a name for the topic.
   2. If you want to apply advanced configurations for the topic, click to expand **Advanced Configurations** and apply the required configurations. For details on the configurations you can apply, see [Apply advanced topic configurations](#apply-advanced-topic-configurations).
8. Click **Create**.

### Apply advanced topic configurations

Choreo allows you to customize key settings for Kafka topics, including the number of partitions, replication factor, cleanup policy, retention size (bytes), retention duration (hours), and the minimum number of in-sync replicas.

**Cleanup Policy:**  Determines how old or unneeded messages are handled. There are three options:  

- Delete (default): Messages are removed once the retention limit is reached.  
- Compact: Kafka retains the latest record for each unique key, ensuring only the most recent data is kept.  
- Compact and Delete: Combines both approaches, where old records are deleted based on retention limits, but the latest record for each key is retained.

**Replication:** Defines the number of copies of each partition to ensure data durability. The default replication factor is 3.

**Partitions:** The number of segments the topic is divided into, allowing parallel processing. The default partition count is 1.

**Retention Bytes:** The maximum size of retained messages. On reaching this limit, older messages are discarded. By default, this value is -1, implying there is no limit based on size.

**Retention Hours:** The amount of time messages are retained before being deleted. The default is 168 hours (7 days).

**Min In-Sync Replicas:** The minimum number of replicas that must acknowledge a write for it to be considered successful. The default value is 2.

## Manage service users and access control lists

Choreo-managed Kafka services use access control lists (ACLs) and user definitions to establish individual rights to produce, consume, or manage topics. To manage users and ACL entries, go to the corresponding tabs on the service details page.

To manage users for a Choreo-managed Kafka service, go to the **Users** tab on the service details page. You can add users to the Kafka service by specifying a username. By default, new users do not have any permissions. You can grant necessary permissions through access control lists (ACLs).

An ACL entry defines the following:

- Username
- Permission (i.e., read, write, etc.)
- Associated topic

To configure access, go to the **Access Control List** tab on the service details page. You can add an entry specifying the user, permission, and relevant topic.
