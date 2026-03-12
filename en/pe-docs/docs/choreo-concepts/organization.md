# Organization

An organization in {{ product_name }} is a logical grouping of users and user resources. A first-time user must create an organization and be a member of it when signing in to {{ product_name }}. Users and resources in an organization cannot access resources in another organization unless an admin of the other organization invites them and adds them as a member of that organization. A user cannot create more than one organization.

## Switch organizations

If you are a member of more than one organization, you can switch from one organization to another when necessary. To do this, select the required organization from the **Organization** list in the {{ product_name }} Console header.

## Inviting users

!!! note
    - You must have **Send Invitations** or **Manage Invitations** permission under **USER-MANAGEMENT** permission group to invite users.

Platform engineers can invite users to the organization by assigning them specific [groups](../choreo-concepts/access-control.md#group). Invited users will receive an invitation via email. An invited user must accept the invitation in order to join the organization and access the resources.

## Manage user permission

For details on how {{ product_name }} manages user permission, see [Access Control](access-control.md).

## Organization ID

The Organization ID serves as a unique identifier for each organization. To get the organization ID, follow the steps below:

1. Go to the [{{ product_name }} Console](https://console.choreo.dev/) and sign in. This opens the project home page.
2. Click on the **Organization** list on the header and select your organization.
3. In the left navigation, click **Settings**.
4. In the header, click the **Organization** list. This will open the organization level settings page.
5. Under **Organization** click **Copy ID**.

## Organization Handle

The organization handle is a unique string that directly corresponds to your organization's name. To get the organization handle, follow the steps below:

1. Go to the [{{ product_name }} Console](https://console.choreo.dev/) and sign in. This opens the project home page.
2. Click on the **Organization** list on the header and select your organization.
3. In the left navigation, click **Settings**.
4. Under **Organization** click **Copy Handle**.
