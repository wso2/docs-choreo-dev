### grant-access.sh

#### Description

This script grants given role to a given email user for a particular MongoDB project.

#### Usage

Get the values for following variables.

```shell
API_PUBLIC_KEY  - public key of the admin api key (can be taken from `admin-mongo-db-atlas-public-key` secret in key vault)
API_PRIVATE_KEY - private key of the admin api key (can be taken from `admin-mongo-db-atlas-private-key` secret in key vault)
PROJECT_ID      - id of the mongodb atlas project (check the tf outputs for `mongodb-atls-project-id`)
ROLE            - project level role to be granted (ex:- `GROUP_OWNER`)
EMAIL_ID        - email id to which access granted (ex:- `xxxxx@wso2.com`)
```

Execute `grant-access.sh` script.

You need to execute this from a place which has private access to MongoDB cluster, ideally the bastion vm.

```shell
bash grant-access.sh -b "$API_PUBLIC_KEY " -v "$API_PRIVATE_KEY" -i "$PROJECT_ID" -r "$ROLE" -e "$EMAIL_ID"
```
