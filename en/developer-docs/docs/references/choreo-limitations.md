# Choreo Limitations

Explore key limitations in Choreo, covering areas like HTTP request parameters, components, applications, and API definition files. You can gain insights into the limitations to enhance your understanding and optimize your use of Choreo effectively.

## API management limits

Below are key limitations when working with APIs in Choreo:

|Resource                             |  Limit                                                                                      |
|-------------------------------------|---------------------------------------------------------------------------------------------|
| Maximum request payload             |  50 MB                                                                                      |
| URL size                            |  2 KB                                                                                       |
| Request header                      | <ul><li>Request Headers total: 40 KB</li><li>Max Single Request header: 10 KB</li></ul>     |
| Total request duration              | <ul><li>Minimum: 10 seconds</li><li>Default: 1 minute</li><li>Maximum: 5 minutes</li></ul>  |
| Maximum connection duration (WebSocket APIs)  |  15  minutes                                                                      |
| Connection idle timeout (WebSocket APIs)                            |  5 minutes                                                  |
| Size for API definition (OpenAPI document)| 10 Mb                                                                                 |
| Number of APIs for PDP                 | 1000 API deployments                                                                     |
| Number of APIs per organization (free tier)                 | 5 APIs for free users                                               |
| Number of Developer Portal applications per organization (free tier)  | 10 applications for free users                            |


## Choreo cloud data plane limits

Below are key limitations when working with web applications in the Choreo cloud data plane:

| Resource                            |  Limit                                                                                     |
|------------------------------------|---------------------------------------------------------------------------------------------|
| Request size limit (including headers, cookies, and payloads)   | 256 KB                                                          |
| Response body size limit                         | 20 MB |
| Number of open ports permitted per web application| 1 <br/> While it is possible to have multiple ports open for project-level communication within a data plane, incoming internet traffic can only be directed to a single port. This contrasts with Service-type components, which allow for multiple endpoints.|
