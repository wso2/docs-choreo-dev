# Endpoint

An Endpoint is a network-exposed function that resides within a component. In Choreo, service and integration components expose one or more endpoints. Each endpoint in a component can have a service contract (OpenAPI, GraphQL SDL) associated with it. This contract is used to expose the endpoint to consumers. In the absence of a contract, Choreo uses /\* exposed on all HTTP verbs as the default contract to expose the service or the integration.

Each endpoint exposed in a component is considered a single API. Therefore, Choreo allows you to do API management per endpoint for a given component. For example, you can perform lifecycle management and configure security settings per endpoint in a given component.
