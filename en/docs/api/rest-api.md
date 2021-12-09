# REST API
Representational State Transfer (REST) is considered to be the standard for web APIs. Choreo allows you to easily design, develop, and manage HTTP REST APIs using the integrated pro-code or low-code editor.

#### What happens when you create a REST API Component in Choreo?
When you create a REST API component, similar to all other components, it  will be created in a private GitHub repo for your user account.  You can then manage changes to your API via Git. The changes you make will be visible in the Choreo UI giving you a clear visual representation of the Git change log of your implementation. 

## Develop and Publish a REST API
Choreo’s inbuilt user-friendly low-code and pro-code editors allow you to easily design and develop REST API’s in compliance with the OpenAPI specification. 

### Design

API designing is the process of laying out the architecture or simply the blueprint of your API around the resources the API intends to expose. These resources are what allows access to the backend endpoint. A good API design should be simple, flexible, and easy to adopt. Choreo’s low-code editor helps design your REST API by giving a visual representation of the API’s resources. Choreo supports HTTP and therefore you can expose your HTTP endpoint easily through Choreo as means of a managed API. 

### Develop
Choreo’s strong API management capabilities and enterprise integration capabilities allows you to incorporate business logic to your API easily.  You can write your logic from scratch by making use of the low-code editor that helps you focus on the logic and not on the syntax or your fluency in the programming language all while leveraging the programming languages capabilities. You can also use Choreo’s in-built connectors or simply re-use any other component you have already created within the Choreo platform such as a service, trigger, scheduled task, or a webhook.

### Deploy
The REST API needs to be deployed to make your API invocable.  Deploying an API in Choreo will checkout the API’s source from your private Github repository, build it, and push it to a docker container, and host it on Kubernetes. 

### Test
Testing your API before publishing it for consumers to use, is highly beneficial to assure a smooth user experience. Choreo gives you multiple options to test your API. Apart from the integrated API console, you can either generate a cURL command, or integrate Postman. 

### Manage
Choreo allows you to manage your API’s lifecycle visually. This helps you manage your APIs between the development, testing, and maintenance phases. Furthermore, you can add documentation,  and apply rate limits to your API in Choreo. 

### Document
API documentation helps API subscribers to understand the functionality of the API and API publishers to market their APIs better and sustain competition.In Choreo,  you can add different types of documentation from various sources. All documents created in Choreo have unique URLs to help improve SEO support.

### Secure
API authentication is a way of protecting API access from unidentified or anonymous access. It ensures that the API is secured and accessible only by the consumers who prove their identity and whose identities are found within Choreo.

Choreo by default for REST APIs uses OAuth2 authentication. This means that APIs created in Choreo can be invoked using an OAuth2 token. Chore allows you to enable or disable security on a resource level giving you more flexibility to your API implementation. 

### Rate limit
Rate limiting allows you to limit the number of successful hits to an API during a given period, typically in cases such as the following:
To protect your APIs from common types of security attacks such as certain types of denial of service (DOS) attacks
To regulate traffic according to infrastructure availability
To make an API, application, or a resource available to a consumer at diffe
rent levels of service, usually for monetization purpose
Choreo includes pre-configured usage plans.  Rate limiting can be configured on an API-level or a resource-level. 

### Publish as a connector
Publishing a connector for your API is a way for you to give back to the community. 
Your API could be a common component that could be reused within another API implementation. Choreo allows you to generate and publish a connector to your API to share it with your community. You can choose to publish the connector in your organization’s private Marketplace or the public Marketplace. 

## Consume the REST API
An API Consumer is typically an application developer who may be internal or external to your organization. Consuming APIs is the process by which the application developer accesses the various APIs that are exposed by you and then uses those APIs to develop one’s own software applications and products. 

Choreo by default supports OAuth2 authentication. Hence consumers are required  to generate an OAuth2 token in order to invoke an API exposed via Choreo. Choreo comes with a comprehensive user-friendly developer portal that allows consumers to browse, test, and consume an API easily. 

### Discover APIs
For an API to be visible and accessible via the developer portal, the API needs to be Published to the developer portal.

### Subscribe to APIs¶
Before using an API, the developer must first subscribe to the APIs and obtain the required authentication keys through an application.

#### Applications
An application is a logical representation of a physical application such as a mobile app, webapp, device, etc. An API subscription is created, authenticated, and managed through an application.

#### Authentication
The subscription process is authenticated using OAuth2 by default. The authentication keys are generated for each application per gateway environment (Production or Sandbox). When the subscribing developer invokes the API through an application, the access token for the relevant gateway environment should be used.

#### Business Plans
Developers need to select a business plan for each API subscription. The business plan determines the number of requests that are allowed to be sent to the API per minute. Therefore, this is also the rate limit that applies to a subscription.

### Test APIs¶
Before using an API for development, the API consumer may want to test it’s capabilities. Choreo provides in-built capabilities to test the APIs using the UI.
