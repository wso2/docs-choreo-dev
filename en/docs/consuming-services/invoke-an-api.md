# Invoke an API

Choreo Service components, whether they function through REST, gRPC, Ballerina Service, TCP, UDP, or other protocols, can be exposed as APIs. In the Choreo ecosystem, API security is implemented through OAuth 2.0. Consequently, when making calls to an API, the invocation must include an OAuth 2.0 bearer token in the HTTP header of the request. This token serves as a means of authentication, ensuring secure access to the API. Known as the API Access Token or Key, this string is an essential component that must accompany the API

This guide will walk you through the steps you need to follow to invoke an API in Choreo  either via a cURL command or via your web application. 

## Invoke an API via cURL 

{% include "consume-an-api-via-curl.md" %}

## Invoke and API via a web application

You can consume an API via your web application. Refer the steps in the guide [Consume a Service - Consume an API via a web application ](hhttps://wso2.com/choreo/docs//choreo/docs/consuming-services/consume-a-service/#consume-an-api-via-a-web-application) to learn how you can consume the API via your web application.