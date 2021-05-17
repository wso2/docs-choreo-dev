# API Concepts
Choreo's API management capabilities allow you to create APIs, manage APIs, and expose services and external APIs as managed APIs. Here are some basic concepts which will help you understand API management in Choreo as you create and manage your APIs in the [Choreo Console]({{choreo_console}}).
 
## API
An API is an intermediate layer that acts as a communication protocol between a consumer and a service, simplifying the consumption of the service. In addition to hiding the underlying implementation details of a service, an API provides a secure, controlled, and well-documented approach to access an exposed service.

## API Management Components
API management in Choreo is facilitated by the key components: [API Publisher](#api-publisher), [Developer Portal](#developer-portal), [API Key Manager](#api-key-manager),and [API Gateway](#api-gateway).  

### API Publisher 
The API Publisher is designed for API creators to develop, document, secure, test, and version APIs with ease. It also caters to more API management-related tasks, such as allowing API Publishers to deploy and publish APIs and applying rate-limiting policies.

### Developer Portal
The Developer portal allows API publishers to host and advertise their APIs and API consumers to discover, evaluate, subscribe to, and consume APIs securely.

### API Key Manager
The API Key Manager is the identity provider for Choreo and acts as the Secure Token Service (STS). Choreo's API management supports OAuth2.0 and API-Key-based authentication mechanisms.

In Choreo, tokens are bound to an application. The Key manager provides a token API to generate access tokens. Clients can use these tokens to invoke APIs exposed by Choreo. The Key Manager also exposes a revoke token API that clients can use to revoke an access token. A client can generate an OAuth2.0 access token by invoking the token API directly or via the Developer Portal UI. Alternatively, a client can generate an API Key through the Developer Portal without calling the Key Manager. The API Key is a self-signed JWT token. When a client invokes an API with an OAuth2.0 access token or an API Key, the Gateway validates the token by validating its signature and subscription.

The Key Manager performs scope validation as well.

### API Gateway
API Gateway acts as the entry point for an API request. It secures, protects, manages, and scales API calls. 

The API Gateway does the JWT token validation by validating the signature, issuer, expiry time, and subscription. The subscription is validated using the in-memory map. This in-memory map includes API-related information, application-related information, subscription-related information, etc., and is updated each time an artifact (API/application) is updated.

Once the token is validated, the API Gateway acts upon the API request before sending it to the backend. It first processes the message to a pre-configured format (e.g., JSON, XML, CSV, etc.). It then applies security policies, rate-limiting policies, collects statistics, etc., via its handlers. The mediators then act upon the API payload based on the mediation logic developed. Then, the message is formatted to a pre-configured format (e.g., JSON, XML, CSV, etc.) and sent to the backend. 

## API Lifecycle
API lifecycle comprises of the stages an API goes through from creation to retirement. API lifecycle is independent of its backend service. The lifecycle states are `Created`, `Prototyped`, `Published`, `Deprecated`, `Blocked`, and `Retired`.

