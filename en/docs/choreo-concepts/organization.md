# Organization

An organization in Choreo is a logical grouping of users and user resources. A first-time user must create an organization and be a member of it when signing in to Choreo. Users and resources in an organization cannot access resources in another organization unless an admin of the other organization invites them and adds them as a member of that organization. A user cannot create more than one organization.

## Switch organizations

If you are a member of more than one organization, you can switch from one organization to another when necessary. To do this, select the required organization from the **Organization** list in the Choreo Console header.

## Inviting members

An organization administrator can invite members to the organization by assigning them specific roles. Invited members receive an invitation via email. An invited member must accept the invitation to join the organization and access the resources of that organization.

## Roles

Choreo roles are defined as follows:

- API publisher: An API publisher can discover, create, publish, delete, test, and manage an API.
- API subscriber: An API subscriber is a developer in a particular organization. An API subscriber can subscribe to an application, manage subscriptions, manage applications, generate API keys, and manage API keys.
- Administrator: An administrator is responsible for all administration tasks, including user management, customizing the Developer Portal, managing projects, enabling analytics, managing domains, etc.
- Billing administrator: Is responsible for billing administration that includes viewing tiers, creating and viewing organizations, managing invoices, viewing and creating subscriptions, and viewing and creating payment methods.
- Developer: A user with a developer role focuses on developing components and solutions in Choreo. Developers can create, build, test, and manage any component type in Choreo: API, services, triggers, scheduled jobs, and Webhooks.
- Environment manager: Manages deployment environments.
- External API subscriber: External API subscribers are API consumers who have access only to the API Developer Portal. They can join an organization with the sole purpose of consuming APIs.

## Organization ID

The Organization ID serves as a unique identifier for each organization. To get the organization ID, follow the steps below:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. Click on the **Organization** list on the header and select your organization.
3. In the left navigation, click **Settings**.
4. Under **Organization** click **Copy ID**.

## Organization Handle

The organization handle is a unique string that directly corresponds to your organization's name. To get the organization handle, follow the steps below:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. Click on the **Organization** list on the header and select your organization.
3. In the left navigation, click **Settings**.
4. Under **Organization** click **Copy Handle**.