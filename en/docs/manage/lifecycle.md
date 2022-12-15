# Lifecycle

Managing the API lifecycle is one of the main factors in API management. An API lifecycle has predefined states. These states represent the different stages the API transitions from creation to retirement. Choreo has six lifecycle states: created, pre-released, published, blocked, deprecated, and retired.

## API lifecycle states

The following lifecycle states are available in the default API lifecycle and applicable for APIs.

|   **API Lifecycle State** |   **Use Case** |   **Action**  |
|-----------------------|------------|-----------|
|   **CREATED**         | The API is created but is ready for consumption.| The API is not published. Therefore, it is not visible to subscribers in the Developer Portal.|
|   **PRE-RELEASED**      | A prototype is created for early promotion and consumer testing. You can deploy a new API or a new version of an existing API as a prototype using the PRE-RELEASED state. It gives subscribers an early implementation of the API.|The API is published in the Developer Portal as a pre-release.|
|   **PUBLISHED**       | The API is ready to be used by users in the Developer Portal.| The API is visible in the Developer Portal and available for subscription.|
|   **BLOCKED**         | The API is temporarily blocked from being used. The API can be PUBLISHED from the BLOCKED state.| Access to the API is temporarily blocked. Runtime calls are blocked and the API is not shown in the Developer Portal anymore.|
|   **DEPRECATED**      | The old version of the API is DEPRECATED when a newer version of the API is created and PUBLISHED.| When an API is deprecated, new subscriptions are disabled. However, the API is still deployed and is available at runtime to existing subscribers. Existing subscribers can continue to use it as usual until the API is retired.|
|   **RETIRED**         | The API is no longer in use and has been moved to the RETIRED state.| The API is unpublished and deleted from the Developer Portal.|

## Manage API lifecycle

The lifecycle of an API can be managed by the users with publishing privileges, using the Choreo Console. 

Follow the instructions below to manage the API lifecycle appropriately:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. Select and click on the API component you want to test from the components list. 

3. in the left panel, click the **Manage** icon to open the test view.

4. In the left pane, click **Lifecycle**.

5. Click on the lifecycle state change buttons to change the lifecycle state.
