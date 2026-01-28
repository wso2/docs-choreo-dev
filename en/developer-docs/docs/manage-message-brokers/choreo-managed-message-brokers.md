# {{ product_name }} Managed Message Brokers

{{ product_name }} allows you to create Apache Kafka services across major cloud providers (AWS, Azure, GCP, and DigitalOcean) as {{ product_name }}-managed platform services for your message brokering needs. These fully managed Kafka instances can seamlessly integrate with {{ product_name }} components, providing scalable messaging for real-time data pipelines and event-driven applications. {{ product_name }} offers multiple Kafka service plans, ranging from lightweight instances for development purposes to production-grade clusters with automatic backups, high-availability multi-nodes, and partitioning.

## Technology Partnership

{{ product_name }}'s managed Kafka services are powered by Aiven, our technology partner and data platform subprocessor. Aiven provides the underlying infrastructure and automation that enable these fully managed services while integrating with the {{ product_name }} ecosystem.

### Service Level Agreement (SLA)

{{ product_name }}-managed Kafka services inherit Aiven's 99.99% monthly uptime SLA. See Aiven's SLA (https://aiven.io/sla) for details.

### Security and Compliance

For the current list of security practices and certifications, see Aiven Security, Privacy & Compliance (https://aiven.io/security-compliance).

!!! info "Note"
     - Kafka service creation is available only for paid {{ product_name }} users.
     - Kafka service billing will be included in your {{ product_name }} subscription, with pricing varying based on the service plan of the resources you create. For more details, see [Choreo Platform Services Billing](../references/choreo-platform-services-billing-and-upgrades.md#platform-service-billing-information).

## Service Limitations and Considerations

### Backup and Recovery Model
Kafka services use a configuration‑backup model rather than traditional message‑data backups:
- No backups of message content or topic data
- Periodic configuration backups (topics, users/ACLs, Schema Registry, Connect) are taken and restored automatically when needed
- Manual selection of older configuration backups is not supported

### Monitoring and Observability
- Runtime metrics and service logs are available in the {{ product_name }} Console
- Native alerting for resource spikes is not currently available; contact {{ product_name }} support if you need to export metrics to third‑party monitoring

## Apache Kafka on {{ product_name }}

Apache Kafka is an open-source platform for real-time event streaming and handling large-scale, event-driven data. You can create Kafka services on {{ product_name }} as fully-managed, scalable, message brokers, ideal for handling large volumes of event-driven data.

- [Create a Choreo-managed Kafka service](./create-choreo-managed-kafka-services.md)

## Is a {{ product_name }}-Managed Kafka Service Right for You?

Use this checklist to evaluate fit:

### Good fit
- Want fully managed Kafka with minimal ops overhead
- Need high availability with automated failover (99.99% SLA)
- Comfortable with configuration‑only backups (not message data)
- Require secure connectivity and {{ product_name }} integration

### Consider alternatives if you
- Need message data backups with point‑in‑time restore
- Require manual selection of backup points
- Need built‑in alerting and advanced monitoring from day 1

### Evaluation tips
- Validate your integration patterns with existing {{ product_name }} components
- Review the configuration‑backup model against your workload
- Assess monitoring needs and metric export options
