## APIM Mongo DB setup
#### Description

This script creates the MongoDB collection and search indexe required for apim service.

#### Usage

Get the values for following variables.

```shell
HOST            - hostname of the mongodb atlas server
USERNAME        - username of the user (`<env>_sre_write_user`)
PASSWORD        - password of the user (can be taken from `choreo-<env>-sre-writer-mongodb-password` secret in key vault)
API_PUBLIC_KEY  - public key of the admin api key (can be taken from `choreo-<env>-admin-mongo-db-atlas-public-key` secret in key vault)
API_PRIVATE_KEY - private key of the admin api key (can be taken from `choreo-<env>-admin-mongo-db-atlas-private-key` secret in key vault)
PROJECT_ID      - id of the mongodb atlas project
CLUSTER_NAME    - name of the mongodb atlas cluster
```

Execute `setup_apim_db.sh` script.

```shell
bash setup_apim_db.sh -s "$HOST" -u "$USERNAME" -p "$PASSWORD" -b "$API_PUBLIC_KEY" -v "$API_PRIVATE_KEY" -g "$PROJECT_ID" -c "$CLUSTER_NAME"
```
