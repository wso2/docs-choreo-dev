# Webhooks

A webhook directs information from one application to another in real-time and triggers the receiving application to execute a specific process in response. Choreo allows you to create webhooks for platforms such as Github, Slack, and Google Calendar using the integrated pro-code or low-code editor. For example, you can design a webhook to notify a Github user via mail each time an issue is assigned to him/her.

### Develop

Choreo provides strong integration capabilities that allow you incorporate logical flows into your webhook. You can create a webhook from scratch using the low-code editor that helps you focus on the logic of the process you are defining instead of on the accuracy of the code you are writing, while leveraging the capabilities of programming languages.

Lets consider the example scenario of recording newly created github issues in a Google spreadsheet. For this, we need to implement a webhook that gets triggered whenever a new issue is created in a specific Github repository. In this scenario, Choreo manages the majority of work relating to the accuracy of the coding while you simply need to write the logic for carrying out the relevant process.

When you create a webhook component, you can select the event source (i.e. Github) and the event channel. As shown below.

![Select event source](../assets/img/webhooks/select-event-source.png)

Once you select them, Choreo creates the skeleton of the code which looks as shown below.

<TO DO: Add Image>

You can implement the logic and test it locally in the code server. After that you can commit and push the code changes to the Github repository that has been created for your webhook component, and go back to the Choreo Console to deploy it.

You can also use the Choreo built-in connectors in the webhook configuration.

### Deploy

A webhook needs to be deployed to be able to receive information that triggers the defined process. Deploying a webhook in Choreo results in checking out the API’s source from your private Github repository, building it, pushing it to a Docker container, and hosting it on Kubernetes.

### Test

Testing your webhook ensures that it functions as expected, and is therefore highly beneficial for a smooth user experience. To test a webhook, you can use the integrated API console, generate a cURL command, or integrate Postman.

### Observe

You can observe the performance of your webhook in terms of the throughput (i.e., the number of requests processed within a specific time duration) and latency (the amount of time taken to process one request). The observability view allows you to visualize the performance statistics via interactive graphs.

## Use

A project can use a webhook to receive data and activate a trigger that causes specific code to be executed based on that data.

- Sending a chat message in a group chat when an event is added in a slack channel.
- Generating an alert when a new issue is created in a specific Git repository.
- Sending a notification when an event is scheduled in a google calendar.
