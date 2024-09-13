# Configure Subscription Approval

Choreo allows you to create subscription plans that require approval before being used. This feature enables you to control access to APIs by requiring administrative review and authorization of subscriptions before they become active.

To use a subscription plan that requires approval, follow the steps given below:

## Prerequisites

- You have [created an API subscription plan](../../administer/create-api-subscription-plans.md) that requires approval.
- You have [assigned a subscription plan that requires approval to an API](assign-subscription-plans-to-apis.md).
- You have [subscribed to an API using a plan that requires approval](subscribe-to-an-api-with-a-subscription-plan.md).

## Request approval

1. In the left navigation menu, click **Approvals**. This opens the organization-level approvals page.
2. On the **Approvals** page, you can view the list of pending subscription requests.
3. Click on the subscription request you want to approve.
4. In the **Subscription Request Approval** pane that opens, click **Approve**.

    ![Approve subscription request](../../assets/img/api-management/manage-api-traffic/approve-subscription-request.png)

5. The subscription request will be approved and the subscription will be activated.
