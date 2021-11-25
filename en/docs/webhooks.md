# Webhooks

A webhook directs information from one application to another in real-time and triggers the receiving application to execute a specific process in response. Choreo allows you to easily design, develop, and manage webhooks using the integrated pro-code or low-code editor.

## Develop and publish a webhook

### Develop your webhook

Choreo provides strong integration capabilities that allow you incorporate logical flows into your webhook. You can create a webhook from scratch using the low-code editor that helps you focus on the logic of the process you are defining instead of on the accuracy of the code you are writing, while leveraging the capabilities of programming languages.

You can also use the Choreo built-in connectors in the webhook configuration.

### Deploy your webhook

A webhook needs to be deployed to be able to receive information that triggers the defined process. Deploying a webhook in Choreo results in checking out the API’s source from your private Github repository, building it, pushing it to a Docker container, and hosting it on Kubernetes.

### Test your webhook

Testing your webhook ensures that it functions as expected, and is therefore highly beneficial for a smooth user experience. To test a webhook, you can use the integrated API console, generate a cURL command, or integrate Postman.

### Observe your webhook

You can observe the performance of your webhook in terms of the throughput (i.e., the number of requests processed within a specific time duration) and latency (the amount of time taken to process one request). The observability view allows you to visualize the performance statistics via interactive graphs.

## Use your webhook

A project can use a webhook to receive data and activate a trigger that causes specific code to be executed based on that data.

- Sending a chat message in a group chat when an event is added in a slack channel.
- Sending an email to a selected mail account when a new issue is created in a specific Git repository.
- Updating a Google sheet when a sale is recorded for a  specific product.
