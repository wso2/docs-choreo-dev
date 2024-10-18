# Configure Approvals for Choreo Workflows

Choreo allows you to configure approval processes for specific workflows within the platform. An approval process for a workflow ensures that critical or sensitive changes are properly managed and controlled.  

Choreo currently supports configuring approvals for the environment promotion workflow. Soon, you'll also be able to configure approvals for API subscriptions.

## Configure an approval for a workflow

To configure an approval for a workflow, follow these steps: 

!!! note
     - You must have administrator privileges in Choreo to configure an approval for a workflow.
     - Administrators can define roles and assignees responsible for reviewing and approving requests for each workflow.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization. 
3. In the left navigation menu, click **Settings**. This opens the organization-level settings page.
4. Click the **Workflows** tab.
5. Click the edit icon corresponding to the workflow for which you want to configure an approval.
6. In the **Configure Workflow** dialog that opens, select roles and assignees responsible for approving the workflow.

    !!! note

         - In the **Roles** field, select one or more roles depending on your preference. Anyone assigned to these roles can review and approve requests.
         - In the **Assignees** field, select any Choreo user to review and approve requests, even if they are not assigned to a selected role.

7. Click **Save**. This configures and enables approval for the workflow. 

Once you configure an approval for a workflow, developers must [submit a request to use the workflow](../develop-components/request-for-workflow-approval.md). An authorized assignee must [review and approve the request](./review-workflow-approval-requests.md) for a developer to use the workflow.
