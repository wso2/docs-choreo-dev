# Configure Approvals for Choreo Workflows

Choreo allows you to configure approval processes for specific workflows within the platform. An approval process for a workflow ensures that critical or sensitive changes are properly managed and controlled.

Choreo currently allows you to configure approvals for environment promotion and API subscription workflows.

Configuring approvals for environment promotion allows authorized users to control components being promoted to a critical/production environment. 

Configuring approvals for the API subscription workflow allows you to create subscription plans that require approval before being activated. This feature allows you to control access to APIs by requiring administrative review and authorization of subscriptions before they become active.

## Permissions to review and respond to approval requests

Click the respective tab for details on permissions depending on the workflow for which you want to configure approvals:

=== "Environment promotion"

     To review and respond to environment promotion approval requests, a user must have the following permissions. Administrators must ensure that users designated to review and respond to approval requests have these permissions:

      - **WORKFLOW-MANAGEMENT**: 
          - Approve component promotion requests: Grants access to review and approve the promotion of components to critical environments.
      - **PROJECT-MANAGEMENT**: Grants access to view and approve workflow requests. This is the same permission used to update or delete projects.

=== "API subscription"

     To review and respond to API subscription approval requests, a user must have the following permissions. Administrators must ensure that users designated to review and respond to approval requests have these permissions:

      - **WORKFLOW-MANAGEMENT**:
          - Approve API subscriptions: Grants access to review and approve API subscription workflow requests.
      - **PROJECT-MANAGEMENT**: 
          Grants access to view and approve workflow requests. This is the same permission used to update or delete projects.

## Set up an approval process for a workflow

To set up an approval process for a workflow, follow these steps:

!!! note
     - You must have administrator privileges in Choreo to configure workflow approvals.
     - Administrators can designate specific roles and assignees to review and respond to requests associated with each workflow.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **Settings**. This opens the organization-level settings page.
4. Click the **Workflows** tab.
5. Click the edit icon corresponding to the workflow for which you want to configure an approval.
6. In the **Configure Workflow** dialog that opens, select roles and assignees to review and respond to workflow approval requests.

    - In the **Roles** field, select one or more roles depending on your preference. Any user assigned to these roles can review and respond to requests.
    - In the **Assignees** field, select specific users who can review and approve workflow requests. Assignees can be any Choreo user, even if they are not assigned to a selected role.

    !!! info "Important"
         Currently, there is no validation to ensure that the specified roles and assignees have the necessary permissions to review and respond to requests. If the [required permissions](#permissions-to-review-and-respond-to-approval-requests) are not correctly configured, some users may receive email notifications but will be unable to review the requests.
         
7. Click **Save**. This configures and enables the approval process for the workflow.

Once you enable the approval process for a workflow, see the following details on how to submit a request for approval and the approval process. Click the respective tab depending on the workflow for which you enabled the approval process:  

=== "Environment promotion"

     Once you configure an approval process for environment promotion, developers must [submit a request for approval to use the workflow](../develop-components/submit-and-manage-workflow-approval-requests.md). An authorized assignee must then [review and approve the request](./review-workflow-approval-requests.md) for a developer to proceed with the task related to the workflow.

=== "API subscription"

     Once you configure an approval process for API subscription, administrators can select the **Approval required** checkbox to create or update subscription plans to require approval. For details, see [Create API Subscription Plans](../administer/create-api-subscription-plans.md). API consumers using these plans must request approval to proceed. For details, see step 7 in [Subscribe to an API with a Subscription Plan](../api-management/manage-api-traffic/subscribe-to-an-api-with-a-subscription-plan.md). An authorized approver must then [review and approve the request](./review-workflow-approval-requests.md) before the subscription is granted.
