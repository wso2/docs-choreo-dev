# Webhook
Explore how you can create event-driven integrations with Choreo Webhooks.

## What is a webhook?

A webhook is a cause that activates an action. Webhooks in Choreo are no different and are user-defined callbacks. 

Webhooks are used by developers to implement event-driven integrations. Before event-driven solutions,  to identify an event occurrence, systems had to keep polling other systems periodically.  With event-driven solutions, systems can focus on the main business-flow, and proactively be notified when an event of interest occurs in real-time. The following are some examples of scenarios where you can create webhooks for the mentioned platforms:

- Sending a chat message in a group chat when an event gets added in a slack channel.
- Generating an alert when a new issue gets created in a specific Git repository.
- Sending a notification when an event gets scheduled in a google calendar.

Webhooks are a widely used programming element to create event-driven API architectures for real-time solutions.

## Develop a webhook

Developing a webhook is the process of creating the webhook by identifying the trigger event and implementing the logic to execute the event. You can create and design an event-driven integration using webhooks in Choreo easily. 

Choreo’s low-code editor allows developers to easily design and develop webhooks for platforms such as GitHub, Slack, and Google Calendar, etc. using the integrated pro-code or low-code editor. Creating a webhook in Choreo starts by either creating the Webhook component from scratch or by connecting an existing Ballerina repository. To create a webhook from scratch, you can start by creating a Webhook component and selecting the trigger type(e.g. GitHub).
Next, you can give the webhook a name, select the trigger channel, and select whether to manage the webhook code in a choreo-managed repository or in your own repository.

![Create a webhook](../../assets/img/webhooks/create-webhook.png){.cInlineImage-half}

 Once you have created your webhook, you can start developing the logic for the specific event(E.g. onOpened). Choreo will execute this logic when the webhook is activated. E.g. send an email when an issue gets created in GitHub.

Additionally, you can define any configurations needed to run the webhook.

### Low code mode

![Low Code](../../assets/img/webhooks/low-code-webhook.png){.cInlineImage-threeQuarter}

The low-code programming model allows developers to use common programming constructs such as loops, conditions(if/else, variable declarations and assignments, logs, data transformations(visual data mapping), and much more in a graphical editor. It also allows developers to connect to any built-in or custom-developed [connectors](https://central.ballerina.io).

Choreo allows you to add multiple triggers and channels to your webhook implementation via the low-code editor. It also shows the configurations needed to deploy the Webhook component. 

The main advantage of the low-code programming mode is that it increases the developer productivity by multiple folds by eliminating the need to write complex code. As developers use constructs on the low-code editor to implement the integration logic for the webhook, the IDE automatically writes the corresponding [Ballerina](https://ballerina.io) source code of the webhook. This speeds up the developer's learning process significantly and thereby boosts overall productivity.

### Pro code mode

In addition to the low-code mode, Choreo provides a pro code mode of implementing the webhook's functionality with the ability to alternate between low-code and pro-code at any time. Choreo's online IDE allows viewing the low-code and pro-code editors side by side:

![Low-Code Pro-Code View](../../assets/img/webhooks/low-code-pro-code-webhook.png){.cInlineImage-threeQuarter}

Choreo gives the developers the flexibility to use the pro-code mode only or use it for implementing certain parts of the API and use the low-code mode for the rest (based on their preference). Choreo treats the source code (generated and handwritten) of the API as the single source of truth and therefore has no limitations or restrictions on which mode developers may choose to implement their webhook's functionality.

## Deploy a webhook

Choreo provides an easy, interactive UI to help you deploy the webhook in Choreo to the default development environment. On deployment, Choreo prompts to add the configuration values that the developer has configured.

![Deploy API](../../assets/img/webhooks/deploy-webhook.png){.cInlineImage-threeQuarter}

Choreo runs a professional, enterprise-grade CI/CD process to deploy APIs to its runtime(data plane) clusters. Under the hood, Choreo’s data plane runs on a Kubernetes stack and hence benefits from all its features such as auto-scaling, auto-healing, secret management, liveness, readiness checks, etc. When you deploy a webhook, Choreo checks out your latest code, builds it, creates a Docker image, and starts it in a Kubernetes cluster managed by Choreo. At this point, Choreo also registers the webhook with the relevant event source (e.g., GitHub, Slack, etc.). When the relevant action occurs in the event source, Choreo is notified through the callback. Choreo then activates the webhook and fires events. Choreo exposes HTTP-based webhooks as an API. Therefore, you can test, manage, and observe this API like any other API on Choreo.

Once you deploy the webhooks to the development environment, it is active in the developer environment. When you are ready to take the code to production, you can promote the deployment and enable the functionality to your consumers. 

Choreo allows you to view the deployment logs through the Deploy view. This helps you troubleshoot and view the status of the builds and deployment. 

### Choreo environments

Choreo by default provides a development environment and a production environment in two separate Kubernetes clusters. Once you have deployed the Webhook component to the development environment and its functionality has been verified, developers can then promote their webhooks to the production environment:

![Deploy View](../../assets/img/webhooks/promote-deploy-webhook.png){.cInlineImage-threeQuarter}

Once you have promoted the Webhook component to production, Choreo then shows a second URL that developers can use in production applications to invoke the webhook.

### Test your webhook

Testing your webhook is an integral part of the spec-driven API development approach. Once you deploy your webhook, it is ready to be tested. Testing your webhook in Choreo can be done in 3 ways:

- Via the inbuilt OpenAPI Console
- Via cURL
- With a Postman integration

### Integrated Console

The in-built Swagger console is an interactive UI where you can test your HTTP-based webhook easily. You can test your webhooks in the environments you deploy them by selecting the environment. Choreo, by default, uses OAuth2.0 authentication. Therefore the Swagger console provides you the option to generate your test keys to test the APIs. Once you meet the pre-requisites, you can test the different resources you defined by specifying any parameters if needed. 
![REST API Proxy - OpenAPI Console](../../assets/img/webhooks/webhook-openapi-test-console.png){.cInlineImage-threeQuarter}

### cURL

Choreo provides you with a cURL command based on the resource, method, and environment you want to test your webhook. You need to select the environment, the methods, specify the base path of the resource, and any parameters. Once you provide the information to Choreo, it will generate a cURL command with an API key in place for you to test. 

![REST API Proxy - cURL](../../assets/img/webhooks/webhook-curl.png){.cInlineImage-threeQuarter}

### Postman

Choreo also allows you to integrate Postman. To integrate Postman, you need to create a postman account, generate an API Key from Postman, and include them in the Choreo UI. Once you integrate Postman successfully, you can import Postman workspaces, or create new workspaces and continue to test it on Choreo.

![REST API Proxy - Postman](../../assets/img/webhooks/webhook-postman.png){.cInlineImage-threeQuarter}

## Manage webhooks

Choreo exposes HTTP-based webhooks as APIs. Therefore, you can leverage the API management capabilities of Choreo to manage these APIs. API Management has automatically turned on for HTTP-based webhooks(and other APIs) on Choreo. Converting an unmanaged API to a managed API is a key advantage. Developers can use its capabilities to manage the exposure of APIs to their consumers. Developers can configure the API’s security settings, set rate limits, associate usage plans, provide other documentation, and perform other related functions to govern the API. 

### Lifecycle of the API exposing the webhook 

Once you implement a webhook, it needs to be built, tested, and eventually put into production as an API. Choreo executes a CI/CD pipeline that manages the API’s Lifecycle. Choreo by default stores the source code of webhooks in a private Github repository for the user account. Optionally, a user can choose to store the code in a user-managed repository. The CI/CD pipeline in Choreo takes an API through the whole process, starting with code checkout, to compile, build, test, and finally to deployment.

![Manage API Proxies](../../assets/img/webhooks/webhook-lifecycle-management.png){.cInlineImage-threeQuarter}

### Add documentation to the API exposing the webhook

A well-designed API needs to be well-documented as well. The usability of the API is a deciding factor of its value. Many API developers find documenting an API a tedious task. Choreo makes documentation an easy task for developers by allowing different sources of documentation. Choreo allows you to add documentation to the REST API by specifying a URL, in Markdown format, importing a file, or by simply including inline documentation. 


### Settings

A well-managed API needs to have security policies and access-control policies in place. Setting rate-limiting policies will ensure:
- Your API is protected from common types of security attacks such as certain types of Denial Of Service (DOS) attacks
- Traffic is regulated and the resources are not exhausted

In addition to providing different usage plans, Choreo also allows you to set different rate-limiting policies against the API or each resource. While the API level rate-limiting will impact the whole API, resource-level rate-limiting gives you the flexibility to change the rate-limiting policy per operation based on the anticipated or analyzed usage.

![Rate Limiting](../../assets/img/api-proxies/rate-limiting.png){.cInlineImage-threeQuarter}

Choreo uses OAuth2.0 authentication.

![Security](../../assets/img/api-proxies/security-settings.png){.cInlineImage-threeQuarter}


### Consumers

Consuming the published APIs is made easy with the inbuilt Choreo Developer Portal. Application developers can discover and invoke published APIs using the Choreo Developer Portal. Learn more about the Choreo Developer Portal.

### Observe your webhook

You can assess the performance of your webhook by observing the success and failure rates of the requests sent to it. The observability view allows you to visualize the performance statistics via interactive graphs.

Learn more on how to observe your webhook from [here](../../observe-and-analyze/observe/observability-overview.md).