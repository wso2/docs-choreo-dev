# Consume API

An API Consumer is typically an application developer who may be internal or external to your organization. Consuming APIs is the process by which the application developer accesses the various APIs that are exposed by you and then uses those APIs to develop one’s own software applications and products.

Choreo by default supports OAuth 2.0 authentication. Hence consumers are required  to generate an OAuth 2.0 token in order to invoke an API exposed via Choreo. Choreo comes with a comprehensive user-friendly developer portal that allows consumers to browse, test, and consume an API easily.

## Discover APIs
For an API to be visible and accessible via the developer portal, the API needs to be Published to the developer portal.

## Subscribe to APIs¶
Before using an API, the developer must first subscribe to the APIs and obtain the required authentication keys through an application.

### Applications
An application is a logical representation of a physical application such as a mobile app, web app, device, etc. An API subscription is created, authenticated, and managed through an application.

### Authentication
The subscription process is authenticated using OAuth 2.0 by default. The authentication keys are generated for each application per gateway environment (Production or Sandbox). When the subscribing developer invokes the API through an application, the access token for the relevant gateway environment should be used.

### Business Plans
Developers need to select a business plan for each API subscription. The business plan determines the number of requests that are allowed to be sent to the API per minute. Therefore, this is also the rate limit that applies to a subscription.

## Test APIs¶
Before using an API for development, the API consumer may want to test it’s capabilities. Choreo provides in-built capabilities to test the APIs using the UI.
