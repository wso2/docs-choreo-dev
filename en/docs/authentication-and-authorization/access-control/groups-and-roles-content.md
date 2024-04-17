## Groups

A group in Choreo is a collection of users, each with one or more roles assigned to them. Users within a group inherit the permissions associated with the roles assigned to that group. For instance, if a user is added to the `API Publisher` group, they will automatically receive the `API Publisher` role.

Choreo comes with predefined groups already configured with specific roles, as follows:

- **API Publisher**: A collection of users who have the API Publisher role.
- **API Subscriber**: A collection of users who have the API Subscriber role.
- **Admin** : A collection of users who have the Admin role.
- **Billing Admin** : A collection of users who have the Billing Admin role.
- **Choreo DevOps** : A collection of users who have the Choreo DevOps role.
- **External API Subscriber**: A collection of users who have the External API Subscriber role.

When creating a new group to invite members, be sure to assign a role to the group to ensure users have the required permissions.

## Roles

Choreo roles are defined as follows:

- **API Publisher**: An API publisher can discover, create, publish, delete, test, and manage an API.
- **API Subscriber**: An API subscriber is a developer in a particular organization. An API subscriber can subscribe to an application, manage subscriptions, manage applications, generate API keys, and manage API keys.
- **Admin**: An administrator is responsible for all administration tasks, including user management, customizing the Developer Portal, managing projects, enabling analytics, managing domains, etc.
- **Billing Admin**: Is responsible for billing administration that includes viewing tiers, creating and viewing organizations, managing invoices, viewing and creating subscriptions, and viewing and creating payment methods.
- **Choreo DevOps**: A Choreo DevOps user is a user with access to the Choreo DevOps portal, enabling them to actively manage, ensure dependable deployment, and monitor components.  
- **External API subscriber**: External API subscribers are API consumers who have access only to the API Developer Portal. They can join an organization with the sole purpose of consuming APIs.
- **Environment Manager (Deprecated)**: Manages deployment environments.