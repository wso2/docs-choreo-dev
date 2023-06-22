# Utility house keeping scripts
This contains the util scripts for performing certain house keeping tasks, such as removing old test data that has not been removed due to an error encountered during the data cleanup task of the automated test run.


## 1. Removal of dangling API Proxies on the Devportal
This scenario has occurred where the API Proxy has been successfully removed from the Choreo Console but is still visible on the Devportal. This could lead to tests failures which cannot be rectified till the dangling API Proxies are removed.

- Usage

```
node api-delete.js {dev|stage|prod} <access_token>
```

_Args_

**env** - Env name that you are executing against(`dev`, `stage` or `prod`)

**access_token** - JWT returned in the response of the POST call to the STS token endpoint of the respective Chroeo account, returned in the `access_token` field.

## 2. Removal of duplicate reusable components on Console
This scenario is specific to components that are created in the _Default Project_ and subsequently reused in the following test runs. In a case where data duplication occurs due to an error in the automation tests, this script can be used for deleting the additional duplicate components which would otherwise remain, since there not cleaned up via the standard test data cleanup process.

Note - Make sure to run the script inside the utils directory.

- Usage

```
node component-duplicate-delete.js {dev|stage|prod} <access_token> <orgId> <component_name_prefix>
```

_Args_

**env** - Env name that you are executing against(`dev`, `stage` or `prod`)

**access_token** - JWT returned in the response of the POST call to the STS token endpoint of the respective Chroeo account, returned in the `access_token` field.

**orgId** - Choreo Organization Id that the cleanup is taking place for. Should be an integer(example: 123)

**component_name_prefix** - The common prefix string used by the reusable component. This can have a value such as _create-ReuseRestAPI-1.6.1_. Duplicates will contain the same value for the component displayName followed by random characters added by Choreo for uniqueness.

