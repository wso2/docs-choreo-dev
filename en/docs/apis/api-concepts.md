# API Concepts
Choreo's API management capabilities allow you to create APIs, manage APIs, and expose services and external APIs as managed APIs. Here are some basic concepts which will help you understand API management in Choreo as you create and manage your APIs in the [Choreo Console]({{choreo_console}}).
 
## API
An API is an intermediate layer that acts as a communication protocol between a consumer and a service, simplifying the consumption of the service. In addition to hiding the underlying implementation details of a service, an API provides a secure, controlled, and well-documented approach to access an exposed service.

## API format
OpenAPI Specification (OAS, a.k.a Swagger) format is the underlying representation of an API in Choreo. You can import an API using a Swagger definition in the Choreo console. Additionally, a user can view, edit, import, or download an API definition in OAS format via the Choreo console.

## API resource path and HTTP methods
An API comprises one or more resources, each of which has a URI. An API Resource has a set of HTTP methods that operates on it. The supported HTTP methods are `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `HEAD`, and `OPTIONS`.

## API lifecycle
An API lifecycle comprises the stages an API goes through from creation to retirement. API lifecycle is independent of its backend service. The lifecycle states are: `Created`, `Prototyped`, `Published`, `Deprecated`, `Blocked`, `Deployed`, and `Retired`.

## Application
An Application in Choreo is the logical representation of a physical application such as a mobile app, web app, device, etc. For an application to use an API, the application should subscribe to the relevant APIs it intends to use. A subscription to an API happens over a selected business plan.  The business plan determines the usage quota the subscription gets. An application has a consumer key and a consumer secret which acts as the credentials of the application.

## Access token
Choreo uses the OAuth2.0 bearer token-based authentication mechanism to allow a consumer to access an API.  
The access token is a string that the consumer passes in an HTTP header of the API request. Choreo supports JWT formatted self-contained access tokens.

## Rate limiting
Rate limiting limits the number of permitted requests to an API within a specified timeframe. You can use a rate-limiting strategy for the following use cases:
    - Protect your APIs from common types of security attacks, such as certain types of Denial of Service (DoS) attacks 
    - Regulate traffic according 
    
## Tags
Tags allow API providers to categorize APIs that have similar attributes. When a tagged API gets published to the API Developer Portal, its tags appear as clickable links. API consumers can use the link to navigate to a category of interest. API consumers can also search APIs that match a particular tag on the Developer Portal.

## API management components
API management in Choreo is facilitated by the key components: [Choreo Console](#choreo-console), [Developer Portal](#developer-portal), [API Key Manager](#api-key-manager), [Choreo Connect](#choreo-connect), and [Traffic Manager](#traffic-manager).  

### Choreo Console
The API section in the Choreo console is designed for API creators to develop, document, secure, test, and version APIs with ease. It also caters to more API management-related tasks, such as allowing API Publishers to deploy and publish APIs and applying rate-limiting policies.

### Developer portal
The Developer Portal allows API publishers to host and advertise their APIs and API consumers to discover, evaluate, subscribe to, and consume APIs securely.

### API key manager
The API Key Manager is the identity provider for Choreo and acts as the Secure Token Service (STS). Choreo's API management supports OAuth2.0 and API-Key-based authentication mechanisms.

In Choreo, tokens are bound to an application. The Key manager provides a token API to generate access tokens. Clients can use these tokens to invoke APIs exposed by Choreo. The Key Manager also exposes a revoke token API that clients can use to revoke an access token. A client can generate an OAuth2.0 access token by invoking the token API directly or via the Developer Portal UI. Alternatively, a client can generate an API Key through the Developer Portal without calling the Key Manager. The API Key is a self-signed JWT token. When a client invokes an API with an OAuth2.0 access token or an API Key, the Gateway validates the token by validating its signature and subscription.

### Choreo connect

Choreo Connect is an API gateway designed for microservices that is cloud-native, decentralized, and developer-centric. This API gateway is a lightweight message processor for APIs that facilitates message security, transport security, routing, and other API Management-related quality of services such as throttling, rate-limiting, etc.

### Traffic manager 

The Traffic Manager helps regulate API traffic, makes APIs and applications available to consumers at different service levels, and secures APIs against security attacks. The Traffic Manager features a dynamic throttling engine that processes rate-limiting policies in real-time. 
