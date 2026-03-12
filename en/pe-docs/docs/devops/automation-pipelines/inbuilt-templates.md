# Inbuilt Pipeline Templates

When configuring a pipeline, you can use built-in pipeline templates provided by {{ product_name }}. These templates include pre-defined functionality that can simplify and accelerate pipeline creation.

[{{ product_name }}’s pipeline specification](https://github.com/wso2/choreo-pipeline-specification/tree/main/docs/specification) supports referencing these templates using the following syntax:

```yaml
steps:
  - name: <step name>
    template: {{ product_name }}/<template name>
```
Check [complete list of {{ product_name }} built-in templates](https://github.com/wso2/choreo-pipeline-specification/blob/main/docs/specification/built-in-templates.md)


## Publish to {{ product_name }} Marketplace

At the end of all the resource provisioning steps in the automation pipeline, to publish metadata and credentials of provisioned resources to the {{ product_name }} marketplace, use inbuilt pipeline template `{{ product_name }}/marketplace-publish@v1`. This template accepts an input argument called `resource` which needs to have a specific structure. Following is an example used to publish details of a provisioned MSSQL database.

{% raw %}
```yaml
steps:
  - name: publish-marketplace
    template: {{ product_name }}/marketplace-publish@v1
    arguments:
      parameters:
        - name: resource
          yamlObject:
            name: "dev-mssql-db"
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
