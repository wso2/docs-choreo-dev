# Manage API Keys

To access a published API secured with an API Key, you need to generate a dedicated API Key for that specific API. This key acts as a unique identifier, enabling authorized usage while maintaining security and control over how the API is consumed.  

Once created, API Keys can be managed through two locations within the Choreo Developer Portal:

- **Credentials section of the API**: This section provides an overview of all API Keys associated with the specific API, enabling API owners to monitor and manage access.
- **Credentials section of the Application**: This section allows application owners to view and manage all API Keys linked to their application, ensuring they have control over API subscriptions and access.

From these sections, you can perform various API Key management actions, such as regenerating and deleting.

## API Key Regeneration

API Key regeneration allows you to obtain a new secret key for an existing API Key while ensuring minimal disruption to service. When an API Key is regenerated, a new secret key is generated, and the existing key remains valid for a grace period of one hour before being revoked. This ensures that applications have sufficient time to update their credentials without experiencing service interruptions.

!!! warning
    Ensure that all applications using the existing API Key are updated with the newly generated key within the grace period to prevent any disruptions in API invocations.

## API Key Deletion

API Keys can be deleted when they are no longer needed. Deleting an API Key immediately revokes its access, preventing further use of the key for API invocations. This action is irreversible and should be performed with caution, as any application relying on the deleted API Key will lose access to the API immediately.