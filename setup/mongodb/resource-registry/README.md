## Resource Registry Mongo DB setup
#### Description

This script creates the MongoDB collection and indexes required for resource-registry service.

#### Usage

Get the values for following variables.

```shell
HOST            - hostname of the mongodb atlas server
USERNAME        - username of the user (`<env>_sre_write_user`)
PASSWORD        - password of the user (can be taken from `choreo-<env>-sre-writer-mongodb-password` secret in key vault)
```

Execute `setup_resources_registry_db.sh` script.

```shell
bash setup_resources_registry_db.sh -s "$HOST" -u "$USERNAME" -p "$PASSWORD"
```
