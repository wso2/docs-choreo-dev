## APIM Mongo DB setup
#### Description

This script creates the MongoDB collection and search indexes required for apim service.

#### Usage

Get the values for following variables.

```shell
HOST            - hostname of the mongodb atlas server, without a trainling '/' (ex:- mongodb+srv://choreo-apim-pl-0.8i8nb.mongodb.net)
USERNAME        - username of the sre admin write user (`sre_write_user`)
PASSWORD        - password of the sre admin write user (can be taken from `sre-writer-mongodb-password` secret in key vault)
API_PUBLIC_KEY  - public key of the admin api key (can be taken from `admin-mongo-db-atlas-public-key` secret in key vault)
API_PRIVATE_KEY - private key of the admin api key (can be taken from `admin-mongo-db-atlas-private-key` secret in key vault)
PROJECT_ID      - id of the mongodb atlas project (check the tf outputs for `mongodb-atls-project-id`)
CLUSTER_NAME    - name of the mongodb atlas cluster (`choreo-apim`)
```

Execute `setup_apim_db.sh` script.

You need to execute this from a place which has private access to MongoDB cluster, ideally the bastion vm.

```shell
bash setup_apim_db.sh -s "$HOST" -u "$USERNAME" -p "$PASSWORD" -b "$API_PUBLIC_KEY" -v "$API_PRIVATE_KEY" -g "$PROJECT_ID" -c "$CLUSTER_NAME"
```
