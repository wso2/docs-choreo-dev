### Choreo MongoDB Atlas Cluster Setup

#### Description

Execute `run.sh` to perform the folowing.

1. Setup APIM DB (`setup_apim_db.sh`)

i.) Create `apis` collection 

ii.) Create search index named `default`

2. Setup RESOURCES_REGISTRY DB (`setup_resources_registry_db.sh`)

i.) Create `resources` collection

ii.) Create indexes

#### Usage

Get the values for following variables.

```shell
HOST            - hostname of the mongodb atlas server
USERNAME        - username of the user (`<env>_sre_write_user`)
PASSWORD        - password of the user (can be taken from `choreo-<env>-sre-writer-mongodb-password` secret in key vault)
API_PUBLIC_KEY  - public key of the admin api key (can be taken from `choreo-<env>-apim-mongo-db-atlas-public-key` secret in key vault)
API_PRIVATE_KEY - private key of the admin api key (can be taken from `choreo-<env>-apim-mongo-db-atlas-private-key` secret in key vault)
PROJECT_ID      - id of the mongodb atlas project
CLUSTER_NAME    - name of the mongodb atlas cluster
```

Execute `run.sh` script

```shell
bash run.sh -s {HOST} -u {USERNAME} -p {PASSWORD} -b {API_PUBLIC_KEY} -v {API_PRIVATE_KEY} -g {PROJECT_ID} -c {CLUSTER_NAME}
```
