# {{ product_name }}-Managed Databases, Vector Databases, and Caches

{{ product_name }} allows you to create PostgreSQL and MySQL databases as well as {{ product_name }}-Managed Cache instances on all major cloud providers (AWS, Azure, GCP, and DigitalOcean) as fully {{ product_name }}-managed platform services.
These databases and caches can be seamlessly provisioned to offer persistence and caching capabilities for all your {{ product_name }} components. {{ product_name }} provides various service plans for each type, ranging from smaller instances for development purposes to production-grade databases with automatic backups and high-availability multi-nodes.

## Technology Partnership

{{ product_name }}'s managed data services are powered by **Aiven.io**, our trusted technology partner and data platform subprocessor. Aiven provides the underlying infrastructure, automated management, and expertise that enable {{ product_name }} to deliver fully managed data services as part of our ecosystem.

### Service Level Agreement (SLA)

{{ product_name }}-managed data services inherit Aiven's 99.99% monthly uptime SLA. See Aiven's SLA (https://aiven.io/sla) for details.

### Security and Compliance

For the current list of security practices and certifications, see Aiven Security, Privacy & Compliance (https://aiven.io/security-compliance).

!!! info "Note"
     - The capability to create {{ product_name }}-managed databases, vector databases, and cache services is available only for paid {{ product_name }} users.
     - Billing for these services will be included in your {{ product_name }} subscription, with pricing varying based on the service plan of the resources you create. For more details, see [{{ product_name }} Platform Services Billing](../../references/choreo-platform-services-billing-and-upgrades.md#platform-service-billing-information).

!!!Tip "Explore the free trial"
    {{ product_name }} provides a 7-day free trial for all database types on the 'Hobbyist' service plan, available to free-tier users.

## Support Model

{{ product_name }} provides support for managed data services directly and, when needed, in coordination with Aiven:

- Guidance and operational assistance via {{ product_name }} support
- Coordination with Aiven experts for complex technical issues
- {{ product_name }} acts as your primary contact and manages escalations with the Aiven support team as required

## PostgreSQL on {{ product_name }}

PostgreSQL (also known as Postgres), is an open-source object-relational database management system. You can create PostgreSQL databases on {{ product_name }} as fully {{ product_name }}-managed, flexible SQL databases that are ideal for both structured and unstructured data. If you want to perform an efficient vector similarity search, you can create a PostgreSQL vector database.

- [Create a PostgreSQL database on {{ product_name }}](./choreo-managed-postgresql-databases.md)

## MySQL on {{ product_name }}

MySQL is a user-friendly, flexible, open-source relational database management system with a well-established history in the SQL database realm. {{ product_name }} allows you to swiftly create fully {{ product_name }}-managed MySQL databases, enabling rapid setup and utilization.

- [Create a MySQL database on {{ product_name }}](./choreo-managed-mysql-databases.md)

## {{ product_name }}-Managed Cache (Valkey - OSS Redis Compatible)

A fully managed cache powered by Valkey that's fully compatible with Redis® OSS. A versatile, in‑memory NoSQL database that serves as a cache, database, streaming engine, and message broker. {{ product_name }}-managed Cache allows you to have fully managed instances that can be swiftly provisioned and integrated into your applications within minutes.

- [Create a {{ product_name }}-managed Cache](./choreo-managed-caches.md)

<span style="font-size: 11px; color:gray;">
 PostgreSQL, MySQL, and Redis® are trademarks and property of their respective owners. All product and service names used in this documentation are for identification purposes only. 
</span>
