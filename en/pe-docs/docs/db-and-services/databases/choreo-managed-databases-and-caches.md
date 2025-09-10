# Choreo-Managed Databases, Vector Databases, and Caches

Choreo allows you to create PostgreSQL and MySQL databases as well as Choreo-Managed Cache instances on all major cloud providers (AWS, Azure, GCP, and DigitalOcean) as fully Choreo-managed platform services.
These databases and caches can be seamlessly provisioned to offer persistence and caching capabilities for all your Choreo components. Choreo provides various service plans for each type, ranging from smaller instances for development purposes to production-grade databases with automatic backups and high-availability multi-nodes.

## Technology Partnership

Choreo's managed data services are powered by **Aiven.io**, our trusted technology partner and data platform subprocessor. Aiven provides the underlying infrastructure, automated management, and expertise that enable Choreo to deliver fully managed data services as part of our ecosystem.

### Service Level Agreement (SLA)

Choreo-managed data services inherit Aiven's 99.99% monthly uptime SLA. See Aiven's SLA (https://aiven.io/sla) for details.

### Security and Compliance

For the current list of security practices and certifications, see Aiven Security, Privacy & Compliance (https://aiven.io/security-compliance).

!!! info "Note"
     - The capability to create Choreo-managed databases, vector databases, and cache services is available only for paid Choreo users.
     - Billing for these services will be included in your Choreo subscription, with pricing varying based on the service plan of the resources you create. For more details, see [Choreo Platform Services Billing](../../references/choreo-platform-services-billing-and-upgrades.md#platform-service-billing-information).

!!!Tip "Explore the free trial"
    Choreo provides a 7-day free trial for all database types on the 'Hobbyist' service plan, available to free-tier users.

## Support Model

Choreo provides support for managed data services directly and, when needed, in coordination with Aiven:

- Guidance and operational assistance via Choreo support
- Coordination with Aiven experts for complex technical issues
- Choreo acts as your primary contact and manages escalations with the Aiven support team as required

## PostgreSQL on Choreo

PostgreSQL (also known as Postgres), is an open-source object-relational database management system. You can create PostgreSQL databases on Choreo as fully Choreo-managed, flexible SQL databases that are ideal for both structured and unstructured data. If you want to perform an efficient vector similarity search, you can create a PostgreSQL vector database.

- [Create a PostgreSQL database on Choreo](./choreo-managed-postgresql-databases.md)

## MySQL on Choreo

MySQL is a user-friendly, flexible, open-source relational database management system with a well-established history in the SQL database realm. Choreo allows you to swiftly create fully Choreo-managed MySQL databases, enabling rapid setup and utilization.

- [Create a MySQL database on Choreo](./choreo-managed-mysql-databases.md)

## Choreo-Managed Cache (Valkey - OSS Redis Compatible)

A fully managed cache compatible powered by Valkey that's fully compatible with with Redis® OSS. A versatile, in‑memory NoSQL database that serves as a cache, database, streaming engine, and message broker. Choreo-managed Cache allows you to have fully managed instances that can be swiftly provisioned and integrated into your applications within minutes.

- [Create a Choreo-managed Cache](./choreo-managed-caches.md)

<span style="font-size: 11px; color:gray;">
 PostgreSQL, MySQL, and Redis® are trademarks and property of their respective owners. All product and service names used in this documentation are for identification purposes only. 
</span>
