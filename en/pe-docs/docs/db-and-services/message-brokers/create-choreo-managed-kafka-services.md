# Create Choreo-Managed Kafka Services

Kafka on Choreo offers fully managed, distributed message broker services across AWS, Azure, GCP, and DigitalOcean. These services are designed to handle high-throughput, fault-tolerant data streaming use cases such as real-time analytics, event sourcing, and log aggregation.

These services run on infrastructure and automation provided by Aiven, our technology partner and data platform subprocessor. For details on the partnership, SLA, and security posture, see the overview (./choreo-managed-message-brokers.md#technology-partnership).

!!! info "Note"
     - Kafka service creation is available only for paid Choreo organizations.
     - Kafka service billing will be included in your Choreo subscription, with pricing varying based on the service plan of the resources you create. For more details, see [Choreo Platform Services Billing](../../references/choreo-platform-services-billing-and-upgrades.md#platform-service-billing-information).

## Create a Choreo-managed Kafka service

Follow the steps below to create a Choreo-managed Kafka service:

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **DB & Services** and then click **Message Brokers**.
4. Click **+ Create**.
5. Specify a display name for the Kafka service and click **Next**.
6. Select your preferred cloud provider from AWS, Azure, GCP, or Digital Ocean.
   - The cloud provider provisions the computing and storage infrastructure for your Kafka service.
   - There is no functional difference between Kafka services across providers except for variations in service plans and associated costs.
7. Select a region for your Kafka service.
   - Available regions depend on the selected cloud provider. Choreo currently supports US, EU, and AU regions across all providers.
8. Select a service plan.
   - Service plans differ based on the allocation of CPU, memory (RAM), and storage for your Kafka server, as well as backup retention periods and high-availability options suited for production environments.
9. Click **Create**. This creates the Kafka service and takes you to the **Overview** tab on the service details page.

## Connect to your Choreo-managed Kafka service

- By default, Kafka services accept traffic from the internet. However, if you want to restrict access to specific IP addresses or CIDR blocks, you can configure the necessary advanced settings.

- Choreo secures Kafka connections via client certificate authentication.

- To connect to your Choreo-managed Kafka service, use the connection parameters from the **Overview** tab on the service details page.

## Disaster Recovery and High Availability

### High Availability Characteristics

Premium and Business tiers typically run multi‑node clusters and can be deployed across different availability zones where the cloud provider supports it. During node failures, the service is designed to maintain availability and keep the Service URI stable (the IP address may change).

Hobbyist and Startup tiers are single‑node and may experience downtime during maintenance or failure recovery.

### Backup and Recovery Model

Kafka uses a different backup model than traditional databases:
- No backups of message content or topic data
- Configuration (topics, users/ACLs, Schema Registry, Connect) is backed up periodically and restored automatically when needed
- Manual selection of older configuration backup points is not supported

### Monitoring and Observability

- Runtime metrics and service logs are accessible in the Choreo Console
- Native alerting for resource spikes is not currently available; contact support if you need to export metrics to third‑party monitoring

**Security Note**: All Kafka connections require TLS and client certificate authentication.
