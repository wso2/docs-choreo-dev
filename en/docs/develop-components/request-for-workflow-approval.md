# Request for Workflow Approval

In Choreo, if an administrator [configures a workflow to require approval](../administer/configure-approvals-for-choreo-workflows.md), you must submit a request to obtain approval to perform the task.

When an approval request is made, Choreo creates a request and notifies all authorized assignees via email about the [review request](../administer/review-workflow-approval-requests.md). When an authorized assignee approves or rejects the request, you will receive an email notification regarding the decision.

The approach to request approval can vary depending on the workflow.

## Request approval for environment promotion

**Prerequisites**:

- Ensure you have a component created, built, and deployed to the development environment.
- Ensure that an approval is configured for the environment promotion workflow.
- Ensure you are promitiong to a critical environment (i.e production)

To request approval to promote a component from the development environment to production, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the component you want to deploy. This takes you to the overview page of the component.
3. In the left navigation menu, click **Deploy**.
4. Go to the **Development** card and click **Request to Promote**.
5. In the **Request Approval** pane that opens, enter your request details and click **Submit**. This creates a request and notifies all authorized assignees via email about the request.

    !!! note
         When an environment promotion request for a specific component is under review, Choreo restricts other developers from making the same request until the initial request is either approved or rejected.

When an authorized assignee approves the request, you will receive a confirmation email. After approval, you can proceed with promoting the component to production.

# Cancel Workflow Approval

When an approval request is made, it is notified to the approvers and will be immediately available for them to review. If you need to cancel the request, it can be done before configured approver approves or rejects the request. The same button you used to raise the approval request, after the requesting will turn into `cancel request`. Once you confirm the cancellation, the cancel event will be notified to all the configured approvers again.
