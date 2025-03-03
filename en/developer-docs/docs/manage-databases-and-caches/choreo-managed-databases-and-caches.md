# Choreo-Managed Databases, Vector Databases, and Caches

Choreo allows you to create PostgreSQL and MySQL databases as well as Choreo-Managed Cache instances on all major cloud providers (AWS, Azure, GCP, and DigitalOcean) as fully Choreo-managed platform services.
These databases and caches can be seamlessly provisioned to offer persistence and caching capabilities for all your Choreo components. Choreo provides various service plans for each type, ranging from smaller instances for development purposes to production-grade databases with automatic backups and high-availability multi-nodes.

!!! info "Note"
     - The capability to create Choreo-managed databases, vector databases, and cache services is available only for paid Choreo users.
     - Billing for these services will be included in your Choreo subscription, with pricing varying based on the service plan of the resources you create. For more details, see [Choreo Platform Services Billing](../references/choreo-platform-services-billing-and-upgrades.md#platform-service-billing-information).

!!!Tip "Explore the free trial"
    Choreo provides a 7-day free trial for all database types on the 'Hobbyist' service plan, available to free-tier users.

## PostgreSQL on Choreo

PostgreSQL (also known as Postgres), is an open-source object-relational database management system. You can create PostgreSQL databases on Choreo as fully Choreo-managed, flexible SQL databases that are ideal for both structured and unstructured data. If you want to perform an efficient vector similarity search, you can create a PostgreSQL vector database.

- [Create a PostgreSQL database on Choreo](./choreo-managed-postgresql-databases.md)

## MySQL on Choreo

MySQL is a user-friendly, flexible, open-source relational database management system with a well-established history in the SQL database realm. Choreo allows you to swiftly create fully Choreo-managed MySQL databases, enabling rapid setup and utilization.

- [Create a MySQL database on Choreo](./choreo-managed-mysql-databases.md)

## Choreo-Managed Cache

A fully-managed cache compatible with legacy Redis® OSS. A versatile, in-memory NoSQL database that serves as a cache, database, streaming engine, and message broker. Choreo-managed Cache allows you to have fully-managed instances that can be swiftly provisioned and integrated into your applications within minutes.

- [Create a Choreo-managed Cache](./choreo-managed-caches.md)

<span style="font-size: 11px; color:gray;">
 PostgreSQL, MySQL, and Redis® are trademarks and property of their respective owners. All product and service names used in this documentation are for identification purposes only. 
</span>
