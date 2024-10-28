# Review Workflow Approval Requests

In Choreo, administrators can [configure approvals for workflows](./configure-approvals-for-choreo-workflows.md) and designate authorized assignees to review approval requests.

If you are assigned as an authorized reviewer for a particular workflow approval request, you will receive an email notification whenever a [request is submitted for approval](../develop-components/request-for-workflow-approval.md). The email will include a summary of the request and a link to the **Approvals** page in the Choreo Console, where you can review the details and either approve or reject the request.

!!! note 
     - Other members configured as approvers within your organization will also receive the notification and may have already reviewed the request. In such cases, you will not be able to review it again.
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
