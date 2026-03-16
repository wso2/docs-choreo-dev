# Lifecycle Management

API lifecycle management is an important aspect of API management. The API lifecycle consists of various states that an API passes through, from creation to retirement. In {{ product_name }}, there are five distinct lifecycle states: created, pre-released, published, deprecated, and retired.

By leveraging the various lifecycle states, API managers can optimize the development process and ensure that subscribers have access to the latest and most reliable APIs.

## API lifecycle states

The following lifecycle states are applicable to APIs in {{ product_name }}:

| **API lifecycle state** | **Use case** | **Corresponding action** |
|-----------------------|------------|-----------|
| **CREATED** | The API is created but is not ready for consumption.| The API is not visible to subscribers in the Developer Portal.|
| **PRE-RELEASED** | A prototype is created for early promotion and consumer testing. You can deploy a new API or a new version of an existing API as a prototype to provide subscribers with an early implementation of the API.|The API is published to the Developer Portal as a pre-release.|
| **PUBLISHED** | The API is ready for subscribers to view and subscribe to via the Developer Portal| The API is visible in the Developer Portal and is available for subscription.|
| **DEPRECATED** | The old version of an API is moved to this state when a newer version of the API is PUBLISHED.| The API is deployed and is available to existing subscribers. New subscriptions are disabled. Existing subscribers can continue to use it as usual until the API is retired. **Note:** Deprecation is irreversible. A deprecated API cannot be moved back to the Published state. An API can also become deprecated automatically when a newer minor version is published (see [Versioning impact on API lifecycle](#versioning-impact-on-api-lifecycle)).|
| **RETIRED** | The API is no longer in use when it is in this state.| The API is unpublished and deleted from the Developer Portal.|

## Versioning impact on API lifecycle

When you create a new version of an API, it directly affects the lifecycle state of existing versions:

- **Minor version upgrades** (e.g., v1.0 → v1.1): Publishing the new minor version automatically **deprecates** the previous minor version under the same major version. The deprecated version is replaced in the Developer Portal. Only the latest minor version is visible. This action is **irreversible** — the previous minor version cannot be restored to the Published state.

- **Major version upgrades** (e.g., v1 → v2): Both major versions appear as **separate entries** in the Developer Portal. The older major version remains in its current lifecycle state and is not automatically deprecated.

!!! warning "Important"
    Creating a minor version has an irreversible effect on the previous minor version's lifecycle. Before creating a new minor version, ensure you no longer need the previous minor version to be in the Published state. If you need both versions available simultaneously, use a major version upgrade instead.

## Manage the lifecycle of an API

To change the lifecycle state of an API via the {{ product_name }} Console, follow the instructions given below:

!!! tip      
     You must have publishing privileges to manage the lifecycle states of a component.

1. Sign in to the {{ product_name }} Console.
2. In the **Component Listing** pane, click on the component for which you want to manage the lifecycle.
3. In the left navigation menu, click **Manage**, and then click **Lifecycle**.
4. In the **Lifecycle Management** pane, you will see the lifecycle state transition diagram indicating the current lifecycle state of the component. The possible lifecycle states you can apply to the component are displayed just above the diagram. Click on a required lifecycle state to apply it to the component. For example, if a component is in the **Created** state, you can click either **Pre-release** or **Publish**.
