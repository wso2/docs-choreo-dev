When configuring a pipeline, you can use built-in pipeline templates provided by Choreo. These templates include pre-defined functionality that can simplify and accelerate pipeline creation.

[Choreo’s pipeline specification](https://github.com/wso2/choreo-pipeline-specification/tree/main/docs/specification) supports referencing these templates using the following syntax:

```yaml
steps:
  - name: <step name>
    template: choreo/<template name>
```

## Available Built-in Templates

!!! info "Note"
    The list of built-in templates is continuously growing. Check [complete list of Choreo built-in templates](https://github.com/wso2/choreo-pipeline-specification/blob/main/docs/specification/built-in-templates.md)

??? "choreo/buildpack-build@v1"
    Builds applications using supported buildpacks within Choreo. This is used by Choreo’s default build pipeline.

??? "choreo/dockerfile-scan@v1"
    Scans Dockerfiles for security vulnerabilities and best practices.

??? "choreo/docker-build@v1"
    Builds Docker images using a provided Dockerfile.

??? "choreo/trivy-scan@v1"
    Performs vulnerability scanning on container images using Trivy.

??? "choreo/webapp-build@v1"
    Builds web applications using supported frameworks.

??? "choreo/ballerina-build@v1"
    Builds Ballerina projects.

??? "choreo/mi-trivy-scan@v1"
    Performs Trivy-based vulnerability scanning for Micro Integrator images.

??? "choreo/integration-project-build@v1"
    Builds integration projects.

??? "choreo/test-runner-build@v1"
    Runs tests using the built-in test runner.

??? "choreo/prism-build@v1"
    Builds Prism projects.

## Publish to Choreo Marketplace

At the end of all the resource provisioning steps in the automation pipeline, to publish metadata and credentials of provisioned resources to the Choreo marketplace, use inbuilt pipeline template `choreo/marketplace-publish@v1`. This template accepts an input argument called `resource` which needs to have a specific structure. Following is an example used to publish details of a provisioned MSSQL database.

{% raw %}
```yaml
steps:
  - name: publish-marketplace
    template: choreo/marketplace-publish@v1
    arguments:
      parameters:
        - name: resource
          yamlObject:
            name: "mssql-db-$ENVIRONMENT"
            version: 1.0.0
            category: Database
            description: MSSQL Database
            tags:
              - dev
            properties:
              databaseName: "{{steps.run-terraform.outputs.parameters.sql_database_name}}"
              sqlServerFQDN: "{{steps.run-terraform.outputs.parameters.sql_server_fqdn}}"
            connectionCredentials:
              - environments:
                  - Development
                parameters:
                  - key: ConnectionString
                    value: "{{steps.run-terraform.outputs.parameters.connection_string}}"
                    isSecret: false
                  - key: DBUsername
                    value: "{{steps.run-terraform.outputs.parameters.db_user_username}}"
                    isSecret: true
                  - key: DBUserPassword
                    value: "{{steps.run-terraform.outputs.parameters.db_user_password}}"
                    isSecret: true
```
{% endraw %}
