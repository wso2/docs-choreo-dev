## Setup MsSQL Databases
#### Description

This script setup Choreo MsSQL databases with their DDL schemas and initial data.

#### Usage

Get the values for following variables.

```shell
SCRIPTS_PATH                      - path to the directory contains script files or to a single script file, without any trailing '/'s
KEY_VAULT_NAME                    - name of the Azure Key Vault that conatins passwords of database users
DATABASE_SERVER_NAME              - host name of the Azure SQL Database server
DATABASE_SERVER_DDL_USER_NAME     - username of a db user who has DDL or higher priviledges
DATABASE_SERVER_DDL_USER_PASSWORD - password of the ddl db user
```

Execute `mssql_init_script.sh` script.

```shell
bash mssql_init_script.sh -S {SCRIPTS_PATH} -K {KEY_VAULT_NAME} -H {DATABASE_SERVER_NAME} -U {DATABASE_SERVER_DDL_USER_NAME} -P {DATABASE_SERVER_DDL_USER_PASSWORD}
```
