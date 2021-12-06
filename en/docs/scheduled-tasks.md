# Scheduled Tasks

A scheduled task performs a routine task at scheduled time intervals.

e.g., You can configure a scheduled task to publish a summary of GitHub issues (i.e., with details such as the number of GitHub issues in each status) as a message in a group chat of a team every day at a specific time.

The in-built low-code and pro-code editors of Choreo allows you to easily configure scheduled tasks.

### Develop

Choreo provides strong integration capabilities that allow you incorporate logical flows into your scheduled task. You can create a scheduled task using the low-code editor that helps you focus on the logic of the process you are defining instead of on the accuracy of the code you are writing, while leveraging the capabilities of programming languages.

Let's consider the example of publishing the GitHub issue summary for a specific repository in the team group chat. To do this, you need to implement a scheduled task that self-triggers itself to derive the issue count from the relevant repository for the specified time duration at publish it in the specified Google chat at the requested time intervals.  In this scenario, Choreo manages the majority of work relating to the accuracy of the coding while you simply need to write the logic for carrying out the relevant process.

You can implement the logic and test it locally in the code server. After that you can commit and push the code changes to the GitHub repository that has been created for your Scheduled Task component, and go back to the Choreo Console to deploy it.

You can also use the connectors supported by Choreo in the scheduled task configuration.


### Deploy

A scheduled task needs to be deployed so that it can run at the specified time intervals to perform its task. When you deploy a scheduled task, Choreo checks out your latest code, builds it, creates a Docker image, and starts it in an environment managed by Choreo.

