# Configure Approvals for Choreo Workflows

Choreo allows you to configure approval processes for specific workflows within the platform. An approval process for a workflow ensures that critical or sensitive changes are properly managed and controlled.  

Choreo currently allows you to configure approvals for environment promotion workflows, with support for API subscription approvals coming soon.

## Configure an approval for a workflow

To configure an approval process for a workflow, follow these steps: 

!!! note
     - You must have administrator privileges in Choreo to configure an approval for a workflow.
     - Administrators can define roles and assignees responsible for reviewing and approving requests associated with each workflow.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization. 
3. In the left navigation menu, click **Settings**. This opens the organization-level settings page.
4. Click the **Workflows** tab.
5. Click the edit icon corresponding to the workflow for which you want to configure an approval.
6. In the **Configure Workflow** dialog that opens, select roles and assignees responsible for approving the workflow.
    - In the **Roles** field, select one or more roles depending on your preference. Any user assigned to these roles can review and approve requests.
    - In the **Assignees** field, select specific users who can review and approve workflow requests. Assignees can be any Choreo user, even if they are not assigned to a selected role.
7. Click **Save**. This configures and enables approval for the workflow. 

Once you configure an approval for a workflow, developers must [submit a request for approval to use the workflow](../develop-components/request-for-workflow-approval.md). An authorized assignee must then [review and approve the request](./review-workflow-approval-requests.md) for a developer to proceed with the task related to the workflow.
