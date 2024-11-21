# Configure Subscription Approval

Choreo allows you to create subscription plans that require approval before being activated. This feature enables you to control access to APIs by requiring administrative review and authorization of subscriptions before they become active.

## Overview

There are two main roles involved in the subscription approval process:

- **Subscribers**: Users who request access to APIs through subscription plans
- **Approvers**: Administrators who review and approve subscription requests

## Prerequisites

Before proceeding, ensure you have:

- [Created an API subscription plan](../../administer/create-api-subscription-plans.md) that requires approval
- [Assigned a subscription plan](assign-subscription-plans-to-apis.md) that requires approval to an API
- [Subscribed to an API](subscribe-to-an-api-with-a-subscription-plan.md) using a plan that requires approval
- [Configured the approvers](../../administer/configure-approvals-for-choreo-workflows.md) for your organization

## Approval Process

### For Approvers

1. Navigate to **Approvals** in the left navigation menu to access the organization-level approvals page
2. Review the list of pending subscription requests
3. Click on the specific subscription request you want to process
4. In the **Subscription Request Approval** pane:
   - Click **Approve** to grant access
   - Click **Reject** to deny the request
5. Upon approval, the subscription becomes active and available in the DevPortal

### For Subscribers

1. After submitting a subscription request, you can track its status in the DevPortal
2. You will receive a notification when your request is approved or rejected
3. Once approved, you can access the API using your subscription credentials
