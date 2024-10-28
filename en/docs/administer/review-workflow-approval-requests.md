# Review Workflow Approval Requests

In Choreo, administrators can [configure approvals for workflows](./configure-approvals-for-choreo-workflows.md) and assign specific users as approvers.

To review and approve workflows, a user must have one of the following permissions:

  - **WORKFLOW-MANAGEMENT**: Grants access to view and approve workflow requests across the organization.
  - **PROJECT-MANAGEMENT**: Grants access to view and approve workflow requests within a specific project.

If you are assigned as an authorized reviewer for a particular workflow approval request, you will receive an email notification whenever a [request is submitted for approval](../develop-components/request-for-workflow-approval.md). The email will include a summary of the request and a link to the **Approvals** page in the Choreo Console, where you can review the details and either approve or reject the request.

!!! note 
     - Workflow approvals are managed at the project level. If a role is assigned in a project context, only members of the user group bound to that role within the specific project will receive notifications for requests made in that project. For example, if you are assigned the project manager role for project A, you will only be notified of workflow requests within project A.
     - Users with organization-level permission will receive notifications for all workflow requests across any project in the organization.
     - Other members configured as approvers within your organization will also receive notifications for workflow requests and may have already reviewed a request. In such cases, you will not be able to review the same request again.
     - Approval requests are made on behalf of the team, so once approved, any authorized team member can use the workflow.

## View workflow approval requests

To view workflow approval requests assigned to you, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization. 
3. In the left navigation menu, click **Approvals**. This opens the **Approvals** page where you can see all approval requests assigned to you.
4. To view details of a specific request, click **Review** corresponding to it.  

## Approve or reject an approval request

To approve or reject a request, follow these steps:

1. Follow the instructions in the [View workflow approval requests](#view-workflow-approval-requests) section to see details of the workflow you want to review. 
   Alternatively, click the Choreo Console link in the approval request email notification you received. This takes you to the **Approvals** page in the Choreo Console, where you will see details of the workflow you want to review. 
2. Review the request details and click **Approve** or **Reject** based on your decision.

!!! note "Limitation"
     Currently, authorized assignees cannot view past requests that are already approved. A feature to allow this is in development.
