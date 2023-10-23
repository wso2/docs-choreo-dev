# Invoke an API

You can expose a Choreo Service component (REST, gRPC, Ballerina Service, TCP, UDP, etc.) as an API. An API in Choreo is secured using OAuth 2.0. Therefore to invoke an API, the API invocation call needs to include an OAuth2.0 bearer token alongside the API request in order to authenticate the access. An API Access Token/Key is a string that is passed as an HTTP header of an API request. 

This guide will walk you through the steps you need to follow to invoke an API in Choreo  either via a cURL command or via your web application. 

## Invoke an API via cURL 

{% include "consume-an-api-via-curl.md" %}

## Invoke and API via a web application

You can consume an API via your web application. Refer the steps in the guide [Consume a Service - Consume an API via a web application ](hhttps://wso2.com/choreo/docs//choreo/docs/consuming-services/consume-a-service/#consume-an-api-via-a-web-application) to learn how you can consume the API via your web application.