# Kafka Topics

Kafka topics are logical channels for organizing and distributing messages between producers and consumers. They serve as the core units where data streams are written and read, and can be partitioned for scalability and fault tolerance.

## Create a Kafka Topic

To create a Kafka topic for your Kafka service:

1. Open your Kafka service by clicking on the particular service in the message brokers services list.
2. Navigate to the **Topics** tab.
3. Click Create, enter a name for the topic, and create the topic.
    - By expanding Advanced Configuration, you will be able to configure some major advanced  configurations in the topic specifying number of partitions, replication count, cleanup policy, retention bytes, retention hours, and minimum in-sync replicas.

## Advanced Topics Configurations

Choreo enables you to customize key settings for Kafka topics, including the number of partitions, replication factor, cleanup policy, retention size (bytes), retention duration (hours), and the minimum number of in-sync replicas.

**Cleanup Policy:**  Determines how old or unneeded messages are handled. There are three options:  

- Delete (default): Messages are removed once the retention limit is reached.  
- Compact: Kafka retains the latest record for each unique key, ensuring only the most recent data is kept.  
- Compact and Delete: Combines both approaches, where old records are deleted based on retention limits, but the latest record for each key is retained.

**Replication:** Defines the number of copies of each partition to ensure data durability. The default replication factor is 3.

**Partitions:** The number of segments the topic is divided into, allowing parallel processing. The default partition count is 1.

**Retention Bytes:** The maximum size of retained messages. Once this limit is reached, older messages are discarded. By default, this value is -1, meaning no limit based on size.

**Retention Hours:** The amount of time messages are retained before being deleted. The default is 168 hours (7 days).

**Min In-Sync Replicas:** The minimum number of replicas that must acknowledge a write for it to be considered successful. The default value is 2.

