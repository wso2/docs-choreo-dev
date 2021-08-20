### How to run Pipeline Variable Groups setup script

##### Prerequisites

1. You should have an access policy set to your AD account with permissions to create secrets. This can be done under "Settings/Access policies" tab in the particular Key Vault's page in Azure portal.
2. You should have whitelisted your pubic ip to access the Key Vault. This can be done in "Settings/Networking" tab in the particular Key Vault's page in Azure portal.

##### Step 1

There are several properties files related to this setup.

Below is the folder hierarchy of properties files.
```
|- control-plane-components
|  |- choreo-ai-anomaly-detector
|  |  |- secrets.properties
|
|  |- choreo-ai-capacity-planner
|  |  |- secrets.properties
|  |
|  |- choreo-ai-datamapper
|  |  |- secrets.properties
|  |
|  |- choreo-ai-deployment-optimizer
|  |  |- secrets.properties
|  |  
|  |- choreo-ai-program-analyzer
|  |  |- secrets.properties 
|  |  
|  |- choreo-ai-test-assistant
|  |  |- secrets.properties
|  | 
|  |- choreo-console
|  |  |- secrets.properties
|  |
|  |- choreo-linker
|  |  |- non-secrets.properties
|  |
|  |- choreo-logging
|  |  |- non-secrets.properties
|  |
|  |- github-choreo-cicd
|     |- non-secrets.properties
|
|- dev-control-plane-deployments
|   |- dev-end2end-tests
|     |- non-secrets.properties
|     |- secrets.properties
|
|- stage-control-plane-deployments
|   |- stg-end2end-tests
|     |- non-secrets.properties
|     |- secrets.properties
|
|- prod-control-plane-deployments
    |- periodic-e2e-test-prod
    |  |- non-secrets.properties
    |  |- secrets.properties
    |  
    |- integration-test2-prod
       |- non-secrets.properties
       |- secrets.properties
```

Above hierarchy is adhered to the following structure

```
|- <Project-Name>
  |- <Variable-Group-Name>
    |- non-secrets.properties
    |- secrets.properties
```
> non-secrets.properties : Contains non confidential variables
> secrets.properties     : Contains confidential variables

You have to select the properties file according to the variable group (and corresponding project) you are going to create.

Then the dummy value (`xxxxxxxxxxxxxxxxxxxx`) there, should be replaced with the actual value

##### Step 2

Run the bash scripts as shown as below with suitable arguments.

```bash pipeline-var-groups-setup.sh -n <variable_group_name> --org <organization_name> -p <project_name> -i <input_file> -s <true or false>```

* -n or --name
  
  Name of the variable group
  
  Here the variable group can be an existing one or a new one. If there is already a variable group with the given name new variables will be appended to that group otherwise new group will be created. 
* --org

  Name of the Azure DevOPs Organization
* -p or --project

  Name of the Azure DevOps Project
* -i or --input
  
  Path of the properties file updated in Step 1
* -s or --secret
  
  Whether the variable is secret or not. (boolean expected)


  Example :- 
  ```bash pipeline-var-groups-setup.sh -n dev-end2end-tests --org https://dev.azure.com/choreo-devops -p dev-control-plane-deployments -i /dev-control-plane-deployments/dev-end2end-tests/secrets.properties -s true```