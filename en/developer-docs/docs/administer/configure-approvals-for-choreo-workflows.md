# Configure Approvals for {{ product_name }} Workflows

{{ product_name }} allows you to configure approval processes for specific workflows within the platform. An approval process for a workflow ensures that critical or sensitive changes are properly managed and controlled.

{{ product_name }} currently allows you to configure approvals for environment promotion, API subscription and URL customization workflows.

Configuring approvals for environment promotion allows authorized users to control components being promoted to a critical/production environment.

Configuring approvals for the API subscription workflow allows you to create subscription plans that require approval before being activated. This feature allows you to control access to APIs by requiring administrative review and authorization of subscriptions before they become active.

Configuring approvals for URL customization workflows allows you to control custom URL mappings by requiring review and approval before the customization is applied.

## Permissions to review and respond to approval requests

Click the respective tab for details on permissions depending on the workflow for which you want to configure approvals:

=== "Environment promotion"

     To review and respond to environment promotion approval requests, a user must have the following permissions. Administrators must ensure that users designated to review and respond to approval requests have these permissions:

      - **WORKFLOW-MANAGEMENT**:
          - Approve component promotion requests: Grants access to review and approve the promotion of components to critical environments.

=== "API subscription"

     To review and respond to API subscription approval requests, a user must have the following permissions. Administrators must ensure that users designated to review and respond to approval requests have these permissions:

      - **WORKFLOW-MANAGEMENT**:
          - Approve API subscriptions: Grants access to review and approve API subscription workflow requests.

=== "URL customization"

     To review and respond to URL customization approval requests, a user must have the following permissions. Administrators must ensure that users designated to review and respond to approval requests have these permissions:

      - **WORKFLOW-MANAGEMENT**:
          - Approve custom URL mapping requests: Grants access to review and approve custom URL mapping requests.
      - **URL-MANAGEMENT**:
          - Manage Custom Domains: Grants access to manage custom domains for the organization.

!!! note
     Approval permissions can be given to users in organization scope or project scope (by assigning user groups to roles at project level). Users having project level permissions can see and review approval requests originated from permitted projects only.

## Set up an approval process for a workflow

To set up an approval process for a workflow, follow these steps:

!!! note
     - You must have administrator privileges in {{ product_name }} to configure workflow approvals.
     - Administrators can designate specific roles and assignees to receive notifications associated with each workflow.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the {{ product_name }} Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **Settings**. This opens the organization-level settings page.
4. Click the **Workflows** tab.
5. Click the Configure icon corresponding to the workflow for which you want to configure an approval.
6. In the **Configure Workflow** dialog that opens, enable the workflow and optionally select roles and assignees to receive notifications on workflow approval requests.

    - In the **Roles** field, select one or more roles depending on your preference. All users assigned to the selected roles will be eligible to receive notifications. Scope of notification delivery depends on the role assignment level when [configuring permissions](#permissions-to-review-and-respond-to-approval-requests):
         1. Project level roles-to-group assignment: Users in these groups will receive notifications only for approval requests raised within the specific project where the role is assigned.
         2. Organization level roles-to-group assignment: Users in these groups will receive notifications for approval requests raised across all projects in the organization.
    - In the **Assignees** field, select specific users who can review and approve workflow requests. Assignees can be any {{ product_name }} user, even if they are not assigned to a selected role.

    Both Roles and Assignees are optional configuration fields. If neither is specified, the system will not dispatch any notifications. However, users with the necessary permissions can still log in to the system and review approval requests through the interface.

    !!! info "Important"
         Only roles having [relevant approval permission](#permissions-to-review-and-respond-to-approval-requests) can be selected to receive notifications, so that respective users can always review and respond to requests. However, users in Assignees field are there for notification purpose only, they may not have required priviledges to review and approve requests.


7. Click **Save**. This configures notifications and enables the approval process for the workflow.

Once you enable the approval process for a workflow, see the following details on how to submit a request for approval and the approval process. Click the respective tab depending on the workflow for which you enabled the approval process:

=== "Environment promotion"

     Once you enable an approval process for environment promotion, developers must [submit a request for approval to use the workflow](../develop-components/submit-and-manage-workflow-approval-requests.md). An authorized assignee must then [review and approve the request](./review-workflow-approval-requests.md) for a developer to proceed with the task related to the workflow.

=== "API subscription"

     Once you enable an approval process for API subscription, administrators can select the **Approval required** checkbox to create or update subscription plans to require approval. For details, see [Create API Subscription Plans](../administer/create-api-subscription-plans.md). API consumers using these plans must request approval to proceed. For details, see step 7 in [Subscribe to an API with a Subscription Plan](../api-management/manage-api-traffic/subscribe-to-an-api-with-a-subscription-plan.md). An authorized approver must then [review and approve the request](./review-workflow-approval-requests.md) before the subscription is granted.

=== "URL customization"

     Once you enable an approval process for URL customization, developers must submit a request for approval to configure a custom URL for a component. Instead of being automatically deployed, the URL mapping will go into a pending state. An authorized approver must then [review and approve the request](./review-workflow-approval-requests.md) before the custom URL is activated for the respective component.
