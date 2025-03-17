# Submit and Manage Workflow Approval Requests

In Choreo, if an administrator [configures a workflow to require approval](../administer/configure-approvals-for-choreo-workflows.md), you must submit a request to obtain approval to perform the task.

Upon submitting a workflow approval request, Choreo notifies all authorized assignees via email about the [review request](../administer/review-workflow-approval-requests.md). When an authorized assignee approves or rejects the request, you will receive an email with details of the decision.

The approach to request approval can vary depending on the workflow. 

## Request approval for environment promotion

**Prerequisites**:

- Ensure you have a component created, built, and deployed to the development environment.
- Ensure that an approval workflow is configured for environment promotion.
- Ensure you are promoting the component to a critical environment, such as production.

To request approval to promote a component from the development environment to production, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the component you want to deploy. This takes you to the **Overview** page of the component.
3. In the left navigation menu, click **Deploy**.
4. Go to the **Development** card and click **Request to Promote**.
5. In the **Request Approval** pane that opens, enter your request details and click **Submit**. This creates a request and notifies all authorized assignees via email about the request.

    !!! note
         When an environment promotion request for a specific component is pending review, Choreo restricts other developers from making the same request until the pending request is either approved or rejected.

When an authorized assignee approves the request, you will receive a confirmation email and can proceed to promote the component to production.

## Cancel a workflow approval request

If you want to cancel a workflow approval request that is already submitted, you can do so before the request is approved or rejected. 

When you submit a workflow approval request, the **Request to Promote** button changes to **Cancel Request**. To cancel an approval request, click **Cancel Request**. Upon confirming the cancellation, all configured approvers are notified immediately, and the request will no longer be pending.
