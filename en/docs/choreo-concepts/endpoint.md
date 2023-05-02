# Endpoint

An Endpoint is a network exposed function that resides within a component. Endpoints are available in service components in Choreo. A service component always exposes one or more endpoints. Each endpoint in a component can have a service contract (OpenAPI, GraphQL SDL) associated with it. This contract is used to expose the endpoint to consumers. In the absence of a contract, Choreo uses /* exposed on all HTTP verbs as the default contract to expose the service on.

Each endpoint that is exposed in a component is considered as a single API. Choreo therefore allows you to do API management per endpoint in a given component. For example, you can do lifecycle management, configure security settings and so on per endpoint in a given component.


