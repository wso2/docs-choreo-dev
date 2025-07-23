# About API Policies

API policies are units of business logic that you can apply to modify the flow of API invocations. 

You can apply a policy to alter the  `Request`, `Response`, or `Error` flow of an API invocation before it reaches the backend or the client. For example, you can add a policy to the response flow to transform the payload from JSON to XML and add a header to the response. 

## Inbuilt mediation policies

Choreo supports a set of inbuilt mediation policies that can handle common API transformation and mediation tasks. These policies run within a single mediation service, making it straightforward to implement and manage complex mediation logic. The following inbuilt policies are available in Choreo:

- **JSON to XML**: Transforms a JSON payload in a request or response into XML format. This policy is applicable only to JSON payloads in mediation flows. Applying it to a non-JSON payload terminates the flow. This policy cannot be used more than once on the same resource because the payload will already be converted to XML.
- **XML to JSON**: Converts an XML payload in a request or response into JSON format. This policy is applicable only to XML payloads in mediation flows. Applying it to a non-XML payload terminates the flow. This policy cannot be used more than once on the same resource because the payload will already be converted to JSON.
- **Remove Query Parameter**: Removes specified query parameters from a request. You can use this policy multiple times to remove different parameters. Attempting to remove a non-existent parameter has no effect. If the parameter exists, it will be removed; otherwise, the request proceeds as usual.
- **Remove Header**: Removes specified headers from a request or response. You can attach this policy multiple times to remove multiple headers. The header name must be static, but you can use placeholders to configure different values for different environments. For example, `${headerName}`.
- **Add Query Parameter**: Adds query parameters to a request. You can attach this policy multiple times to add various parameters. Adding the same parameter multiple times creates an array of values. The parameter name and value must be static, but you can use placeholders to configure different values for different environments. For example, `${fooValue}`.
- **Add Header**: Adds headers to a request or response. If the same header is added multiple times, values are appended rather than overwritten. The header name and value must be static, but you can use placeholders to configure different values for different environments. For example, `${authzHeaderValue}`.
- **Set Header**: Sets headers in a request or response, overwriting any existing values. You can attach this policy multiple times to set multiple headers. Each time the same header is set, it replaces the previous value. The header name and value must be static, but you can use placeholders to configure different values for different environments. For example, `${authzHeaderValue}`. 
- **Rewrite Resource Path**: Modifies the resource path of an HTTP request by replacing the original path with a new relative path. You can apply this policy multiple times, but only the last instance will take effect. The new path must be static, but you can use placeholders to configure different values for different environments. For example, `${myResourcePath}`.
- **Log Message**: Logs the payload and headers of a request or response. Attaching this policy multiple times results in duplicate log entries. By default, headers and payloads are not logged. To log them, you can enable `Log Headers` and `Log Payload` parameters. To exclude specific headers when logging, you can use the `Excluded Headers` parameter, which takes a comma-separated list of header names. An error will occur if payload logging is enabled but the payload cannot be read.

These inbuilt mediation policies provide flexibility to manage API requests and responses, allowing for custom transformations and logic without requiring custom code.

For details on attaching and managing one or more policies to an API proxy component implementation via the Choreo Console, see [Attach and Manage Policies](../api-policies/attach-and-manage-policies.md).

For details on applying advanced settings on mediation policies, see [Apply Advanced Settings on Mediation Policies](../api-policies/apply-advanced-settings-on-mediation-policies.md).
