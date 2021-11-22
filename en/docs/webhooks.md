# Webhooks

!!! info
    This section will describe Webhooks, its concepts, and its usage concerns/concepts in Choreo.

## Introduction

A webhook directs information from one application to another in real-time and triggers the receiving application to execute a specific process in response.

A project can use a webhook to receive information from another project or an external application in order to execute a process. A project can also trigger another project or an external application by sending information to it via a webhook.

## Design webhooks

### Prerequisites

You need to create a project to contain the webhook.

### Create webhooks

You can create a webhook by following the instructions in the UI

!!! tip "Instructions in the UI"
    
    1. In the Choreo console, open the **Projects** page, and click on the project to which you want to add a webhook.
    2. Click **Add Component**.
    3. Click **Webhook**.
    4. Add a name for the webhook and select the trigger type.
    5. Enter other information as relevant, depending on the trigger type you selected.
    6. Click **Create**.
    7. Once the webhook is created, click **Develop**.
    8. To add more information to the webhook, click **Edit with VS Code Online**.
    9. Open the code server in a new tab. In the left navigator of the web page that opens, click on **webhook.bal**. This opens the low-code diagram of the webhook.
    10. Make the required changes to the webhook by editing the diagram. If required, you can also open the file in the code view, and update it by editing the code.

## Use webhooks

A project can use a webhook to receive data and activate a trigger that causes specific code to be executed based on that data.

- Sending a chat message in a group chat when an event is added in a slack channel.
- Sending an email to a selected mail account when a new issue is created in a specific Git repository.
- Updating a Google sheet when a sale is recorded for a  specific product.
