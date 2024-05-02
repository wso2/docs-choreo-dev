## Spec Populator MongoDB Application Creation
#### Description

This script creates a MongoDB application service which will update the vector db with changes to the `RESOURCE_REGISTRY_DB` database in MongoDB.
In order to update the vector db with changes to the `RESOURCE_REGISTRY_DB` in mongo, need to create an application in mongoDB.

#### Usage

Get the values for following variables.

```shell
PROJECT_ID      - id of the mongodb atlas project
API_PUBLIC_KEY  - public key of the admin api key (can be taken from `apim-mongo-db-atlas-public-key` secret in key vault)
API_PRIVATE_KEY - private key of the admin api key (can be taken from `apim-mongo-db-atlas-private-key` secret in key vault)
CLUSTER_NAME    - name of the mongodb atlas cluster
DATABASE_NAME   - database name of `resource_registry` mongoDB
CONSUMER_KEY    - consumer key of devportal app created for spec populator
CONSUMER_SECRET - consumer secret of devportal app created for spec populator
STS_TKN_EP      - sts token endpoint of relevant environment (ex:- `https://sts.choreo.dev/oauth2/token`)
SYSTEM_API_URL  - url of spec populator system api
```

Execute `create_application.sh` script.

```shell
bash create_application.sh -g {PROJECT_ID} -b {API_PUBLIC_KEY} -v {API_PRIVATE_KEY} -c {CLUSTER_NAME} -n {DATABASE_NAME} -k {CONSUMER_KEY} -s {CONSUMER_SECRET} -t {STS_TKN_EP} -p {SYSTEM_API_URL}
```

In case of need to delete the created application execute `delete_application.sh` script.

```shell
bash delete_application.sh -g {PROJECT_ID} -b {API_PUBLIC_KEY} -v {API_PRIVATE_KEY} -c {APPLICATION_ID}
```

