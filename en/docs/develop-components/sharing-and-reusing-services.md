# Sharing and Reusing Services

Choreo allows you to share and reuse your services, promoting faster development and increased efficiency in building integrated applications, through the use of Ballerina Private Packages.

Ballerina private packages allow developers to share the reusable code within the organization via Ballerina central without making it available to the public.
Choreo allows you to use Ballerina private packages to create components when you use the the `Ballerina` buildpack.

In order to use Ballerina private packages, you need to make sure that you are using the same organization in Choreo and Ballerina Central.
For example, if you are using `choreo` as the organization name in Choreo, following conditions should be met. 

- The `Ballerina.toml` in the private package should have the `choreo` as the organization name.
    ```
    [package]
    org = "choreo"
    name = "greeting_lib"
    ```
- The package should be published to the Ballerina Central using a token that is generated for the `choreo` organization.

    !!! tip
        You can refer to the [Publish packages to Ballerina Central](https://ballerina.io/learn/publish-packages-to-ballerina-central/) documentation to learn how to publish a Ballerina package to the Ballerina Central.
    

- The `Ballerina.toml` in the application should have the `choreo` as the organization name.
    ```
    [package]
    org = "choreo"
    name = "greeting_service"
    ```
- The component should be created inside the `choreo` organization in the Choreo.

    !!! tip
        You can use the organization settings page to retrieve the organization name.



Refer to the [Private Package Usage](https://github.com/wso2/choreo-sample-apps/tree/main/ballerina/private-package-usage) sample to learn more about using Ballerina private packages in Choreo.
