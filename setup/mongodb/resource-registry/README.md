## Resource Registry Mongo DB setup
#### Description

This script creates the MongoDB collection and indexes required for resource-registry service.

#### Usage

Get the values for following variables.

```shell
HOST            - hostname of the mongodb atlas server (ex:- mongodb+srv://choreo-apim.idz1i.mongodb.net)
USERNAME        - username of the sre write user (`sre_write_user`)
PASSWORD        - password of the sre write user (can be taken from `sre-writer-mongodb-password` secret in key vault)
```

Execute `setup_resources_registry_db.sh` script.

You need to execute this from a place which has private access to MongoDB cluster, ideally the bastion vm.

```shell
bash setup_resources_registry_db.sh -s "$HOST" -u "$USERNAME" -p "$PASSWORD"
```
