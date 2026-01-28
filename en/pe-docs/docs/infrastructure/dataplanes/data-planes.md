# Data planes

The data plane in {{ product_name }} is the runtime environment where user applications are deployed based on configurations defined in the control plane. It supports a variety of applications, including services, web apps, APIs, integrations, and scheduled tasks, and allows for a polyglot approach with multiple programming languages. All runtime traffic is securely contained within the data plane, ensuring user data remains isolated and protected.

See [Data planes concept](../../choreo-concepts/data-planes.md) for more information on {{ product_name }}'s data plane architecture.

## Types of data planes

{{ product_name }} offers two types of data planes:

1. **{{ product_name }} cloud data plane**: A public data plane managed by {{ product_name }}. This cloud data plane is shared across multiple organizations but provides a secure and scalable environment for deploying applications.
2. **Private data plane**: A data plane that you can deploy and manage within your own infrastructure. Private data planes are compatible with major cloud providers like Azure, AWS, and GCP, as well as on-premises infrastructure.

## Set up a data plane

By default, when you create an organization in {{ product_name }}, a cloud data plane is automatically assigned to you. During the organization onboarding process, you can select a region for the cloud data plane. Free users are limited to creating projects in the chosen region, while paid users can create projects across multiple regions.

If you wish to set up a private data plane, you can reach out to the {{ product_name }} team for assistance. They will guide you through the setup process based on your specific requirements. For more information on the management models available for private data planes, refer to the [Private Data Plane Management Models](../../references/private-data-plane-management-models.md) documentation.

## Self Service Data Plane Management

{{ product_name }} is currently developing self-service data plane management capabilities for private data planes. This feature will enable you to create, manage, and monitor private data planes independently. Keep an eye out for updates on this upcoming functionality.
