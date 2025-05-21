Choreo is a powerful platform that enables developers to create, deploy, and consume services efficiently. The Choreo Developer Portal simplifies API discovery and usage, allowing developers to integrate APIs seamlessly into their applications.

This guide is intended for application developers (both internal and external) who wish to consume APIs published in the Developer Portal to build their applications. You will learn how to:

- Discover APIs
- Create an application and generate credentials
- Subscribe to an API
- Consume a published REST API via a web application

---

## Prerequisites

Before proceeding, ensure you have access to a published service to consume. If you do not have one, follow the [Develop a Service](../develop-components/develop-services/develop-a-service.md) guide to create and deploy a sample REST API.

---

## Discover APIs

In the Choreo Developer Portal, developers can search for APIs by name. APIs and services created and published through the Choreo Console are visible in the Developer Portal based on their visibility settings:

- **Public**: Visible to all users in the Developer Portal.
- **Private**: Accessible only to signed-in users.
- **Restricted**: Available to users with specific roles, enabling granular access control.

For more details, refer to [Control API Visibility](../api-management/control-api-visibility.md).

### Viewing Available APIs

The Developer Portal lists APIs categorized by their major versions. The API overview page displays:

- Subscribed versions of the API
- Subscription details (e.g., application name and creation date)

![Developer Portal APIs](../assets/img/consume/developer-portal-apis.png)

### Selecting the Correct API Version

> **Tip:** It is recommended to use the latest version of an API. Copy the **Endpoint(s)** from the API overview page and integrate it into your client application to ensure compatibility with the most recent updates.

---