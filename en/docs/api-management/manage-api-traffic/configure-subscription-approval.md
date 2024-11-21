# Configure Subscription Approval

Choreo allows you to create subscription plans that require approval before being activated. This feature enables you to control access to APIs by requiring administrative review and authorization of subscriptions before they become active.

<!-- To use a subscription plan that requires approval, follow the steps given below: -->

## Request approval for an API subscription.

**Prerequisites**:

- Ensure you have [created an API subscription plan](../../administer/create-api-subscription-plans.md) that requires approval.
- Ensure you have [assigned a subscription plan that requires approval to an API](assign-subscription-plans-to-apis.md).
- Ensure you have [subscribed to an API using a plan that requires approval](subscribe-to-an-api-with-a-subscription-plan.md).
- Ensure you have [configured the approvers](../../administer/configure-approvals-for-choreo-workflows.md).

## Request approval

1. In the left navigation menu, click **Approvals**. This opens the organization-level approvals page.
2. On the **Approvals** page, you can view the list of pending subscription requests.
3. Click on the subscription request you want to approve.
4. In the **Subscription Request Approval** pane that opens, click **Approve**.
5. The subscription is now active and available in the DevPortal.
