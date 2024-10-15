# Release analytics
This CLI is responsible for capturing release related metrics that will be used for analysing various statistics
related to Choreo releases. The metrics will be captured during the respective release pipeline runs and published to
Google BigQuery for further analysis.

## Pre-requisites
1. Google Cloud project with BigQuery enabled is required. The project should have the necessary 
permissions to publish data to BigQuery.
2. Azure Devops PAT needs to be generated with read scopes for Projects and Test Results with a Basic role. 

The following **environment variables** should be set in the pipeline to declare the credentials associated with the above.

- `RELEASE_ANALYTICS_GCLOUD_ACCOUNT_INFO`: The service account key file content as a base64 encoded string of the GC project.
- `RELEASE_ANALYTICS_AZURE_DEVOPS_PAT`: The Azure DevOps Personal Access Token.

## Default configuration
The default configuration for this CLI is defined in the `release-analytics/config/config.yaml` file. The configs are
as follows:


#### BigQuery configuration
- `scopes`: The scopes required by GC to write to BigQuery
- `project`: The BigQuery project name
- `dataset`: The BigQuery dataset name within the above project

#### Azure DevOps configuration
- `url`: The Azure DevOps base URL for API calls
- `user-agent`: The user agent value used when making API calls
- `max-test-results`: The maximum number of test results to fetch(This is a pagination parameter for the API, therefore
  the value should be higher than the total number of integration tests running in the pipeline)
- `dev-project`: The Azure DevOps project name for the Choreo dev environment
- `dev-definition`: The Azure DevOps Choreo dev env release pipeline definition
- `stage-project`: The Azure DevOps project name for the Choreo stage environment
- `stage-definition`: The Azure DevOps Choreo stage env release pipeline definition
- `prod-project`: The Azure DevOps project name for the Choreo prod environment
- `prod-definition`: The Azure DevOps Choreo prod env release pipeline definition

## Usage
To execute in the pipeline run the following shell script with the desired arguments. This script automates the creation
of the Python virtual environment, installing the required dependencies, executing the Python CLI and deactivating
the virtual environment at the end.

```shell
 release-analytics.sh <arg_1> ... <arg_n>
```

To get familiarised with the available arguments and their descriptions, run shell script without any values to display
the help message text.

```shell
 release-analytics.sh
```
