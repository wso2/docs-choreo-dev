#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
# Download Required secrets

export SUPERSET_DB_PASS=$(az keyvault secret show --name postgresql-SUPERSET-DB-PASSWORD --vault-name ${CSI-KEYVAULT-NAME} --query value -o tsv)
export SUPERSET_ADMIN_USER_PASS$(az keyvault secret show --name superset-ADMIN-PASSWORD --vault-name ${CSI-KEYVAULT-NAME} --query value -o tsv)

bash ../../superset/install-superset.sh
