# Submit and Manage Environment Promotion Approval Requests

In Choreo, when an administrator or platform engineer [configures the Environment Promotion workflow to require approval](../../governance/workflows/configure-approvals-for-choreo-workflows.md), you must submit a request to obtain approval to promote the deployment to a critical environment.

When you submit a promotion approval request, Choreo notifies all authorized reviewers via email. Once an authorized reviewer approves or rejects the request, you will receive an email with the decision details. See [Review Workflow Approval Requests](../../governance/approvals/review-workflow-approval-requests.md) for more information on workflow approval request reviews.


## Request approval for environment promotion

### Prerequisites

- You should have a component created, built, and deployed to the first environment configured in your CD pipeline.
- Ensure that an approval workflow is configured for environment promotion.
- You should promote the component to a critical environment, such as production.
- You must have required permissions to promote components or at least `WORKFLOW-MANAGEMENT >> Create approval requests` permission

To request approval to promote a component from one environment to another critical environment, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. From the left navigation, Click **DevOps** and then click **CD Pipelines**
4. Go to the **Development** card and click **Request to Promote**.
5. In the **Request Approval** pane, enter your request details and click **Submit**. This creates a request and notifies all authorized assignees via email about the request.

    !!! note
         When an environment promotion request for a specific component is pending review, Choreo restricts other developers from making the same request until the pending request is either approved or rejected.

When an authorized assignee approves the request, you will receive a confirmation email and can proceed to promote the component to production.

## Cancel an environment promotion approval request

If you want to cancel an environment promotion approval request that is already submitted, you can do so before the request is approved or rejected. 

When you submit a workflow approval request, the **Request to Promote** button changes to **Cancel Request**. To cancel an approval request, click **Cancel Request**. Upon confirming the cancellation, all configured approvers are notified immediately, and the request will no longer be pending.
