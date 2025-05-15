#!/bin/bash

# -------------------------------------------------------------------------------------
#
# Copyright (c) 2025, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

set -euo pipefail

function az_login() {
  echo "[INFO] Logging in to Azure"
  local client_id client_secret tenant_id
  client_id="$1"
  client_secret="$2"
  tenant_id="$3"
  az login --allow-no-subscriptions \
          --service-principal -u "$client_id" -p "$client_secret" --tenant "$tenant_id"
}

function init_csv() {
  local headers path
  headers="$1"
  path="$2"
  echo "[INFO] Initializing CSV file $path"
  touch "$path"
  echo "$headers" >"$path"
}

if [[ -z "${CLIENT_ID}" ]]; then
  CLIENT_ID=""
fi
if [[ -z "${CLIENT_SECRET}" ]]; then
  CLIENT_SECRET=""
fi
if [[ -z "${TENANT_ID}" ]]; then
  TENANT_ID=""
fi
if [[ -z "${OUTPUT_PATH}" ]]; then
  OUTPUT_PATH=""
fi
if [[ -z "${BUILD_URL}" ]]; then
  BUILD_URL=""
fi
if [[ -z "${WEBHOOK_URL}" ]]; then
  WEBHOOK_URL=""
fi

az_login "$CLIENT_ID" "$CLIENT_SECRET" "$TENANT_ID"
init_csv "Key Vault Name, Certificate Name, Expired Date" "$OUTPUT_PATH"/dev_expired_certs.csv
init_csv "Key Vault Name, Certificate Name, Expires On" "$OUTPUT_PATH"/dev_expiring_certs.csv

init_csv "Key Vault Name, Certificate Name, Expired Date" "$OUTPUT_PATH"/stg_expired_certs.csv
init_csv "Key Vault Name, Certificate Name, Expires On" "$OUTPUT_PATH"/stg_expiring_certs.csv

init_csv "Key Vault Name, Certificate Name, Expired Date" "$OUTPUT_PATH"/prod_expired_certs.csv
init_csv "Key Vault Name, Certificate Name, Expires On" "$OUTPUT_PATH"/prod_expiring_certs.csv

EXPIRY_THRESHOLD_DAYS=7
CURRENT_DATE=$(date -u +%s)
CUTOFF_DATE=$(date -u -d "+${EXPIRY_THRESHOLD_DAYS} days" +%s)

check_keyvault_expiries() {

    local KEY_VAULT_NAME="$1"
    local ENV_PREFIX="$2"

    echo "Checking certificates in Key Vault: $KEY_VAULT_NAME"

    # Check certificates
    cert_names=$(az keyvault certificate list --vault-name "$KEY_VAULT_NAME" --query "[].name" -o tsv)
    for cert in $cert_names; do
        expiry=$(az keyvault certificate show --vault-name "$KEY_VAULT_NAME" --name "$cert" --query "attributes.expires" -o tsv)
        if [ -n "$expiry" ]; then
            expiry_ts=$(date -u -d "$expiry" +%s)
            readable_expiry_date=$(date -u -d "@$expiry_ts")
            if [ "$expiry_ts" -lt "$CURRENT_DATE" ]; then
                echo "$KEY_VAULT_NAME, $cert, $readable_expiry_date" >> "$OUTPUT_PATH"/"${ENV_PREFIX}_expired_certs.csv"
            elif [ "$expiry_ts" -lt "$CUTOFF_DATE" ]; then
                echo "$KEY_VAULT_NAME, $cert, $readable_expiry_date" >> "$OUTPUT_PATH"/"${ENV_PREFIX}_expiring_certs.csv"
            fi
        fi
    done

    echo "Checking secrets in Key Vault: $KEY_VAULT_NAME"

    ## Check secrets
    secret_names=$(az keyvault secret list --vault-name "$KEY_VAULT_NAME" --query "[].name" -o tsv)
    for secret in $secret_names; do
        expiry=$(az keyvault secret show --vault-name "$KEY_VAULT_NAME" --name "$secret" --query "attributes.expires" -o tsv)
        if [ -n "$expiry" ]; then
            expiry_ts=$(date -u -d "$expiry" +%s)
            readable_expiry_date=$(date -u -d "@$expiry_ts")
            if [ "$expiry_ts" -lt "$CURRENT_DATE" ]; then
                echo "$KEY_VAULT_NAME, $cert, $readable_expiry_date" >> "$OUTPUT_PATH"/"${ENV_PREFIX}_expired_secrets.csv"
            elif [ "$expiry_ts" -lt "$CUTOFF_DATE" ]; then
                echo "$KEY_VAULT_NAME, $cert, $readable_expiry_date" >> "$OUTPUT_PATH"/"${ENV_PREFIX}_expiring_secrets.csv"
            fi
        fi
    done
}

check_keyvault_expiries "dev-csi-64" "dev"
check_keyvault_expiries "dev-csi-apim-7" "dev"

check_keyvault_expiries "stg-csi-74" "stg"
check_keyvault_expiries "stg-csi-apim-55" "stg"

#check_keyvault_expiries "prod-csi-60" "prod"
#check_keyvault_expiries "prod-csi-apim-54" "prod"

dev_expiring_certs_count=$(tail -n +2 "$OUTPUT_PATH"/dev_expiring_certs.csv | wc -l)
dev_expired_certs_count=$(tail -n +2 "$OUTPUT_PATH"/dev_expired_certs.csv | wc -l)

dev_expiring_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/dev_expiring_secrets.csv | wc -l)
dev_expired_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/dev_expired_secrets.csv | wc -l)

stg_expiring_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/stg_expiring_certs.csv | wc -l)
stg_expired_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/stg_expired_certs.csv | wc -l)

stg_expiring_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/stg_expiring_secrets.csv | wc -l)
stg_expired_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/stg_expired_secrets.csv | wc -l)

#prod_expiring_certs_count=$(tail -n +2 "$OUTPUT_PATH"/prod_expiring_certs.csv | wc -l)
#prod_expired_certs_count=$(tail -n +2 "$OUTPUT_PATH"/prod_expired_certs.csv | wc -l)

#prod_expiring_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/prod_expiring_secrets.csv | wc -l)
#prod_expired_secrets_count=$(tail -n +2 "$OUTPUT_PATH"/prod_expired_secrets.csv | wc -l)

if [ "$dev_expiring_certs_count" -gt 0 ] | [ "$dev_expiring_secrets_count" -gt 0 ] | [ "$stg_expiring_certs_count" -gt 0 ] | [ "$stg_expiring_secrets_count" -gt 0 ]; then # | [ "$prod_expiring_certs_count" -gt 0 ] | [ "$prod_expiring_secrets_count" -gt 0 ]
  export DEV_EXPIRING_CERTS_COUNT="$dev_expiring_certs_count"
  export DEV_EXPIRED_CERTS_COUNT="$dev_expiring_certs_count"

  export STG_EXPIRING_CERTS_COUNT="$stg_expiring_certs_count"
  export STG_EXPIRED_CERTS_COUNT="$stg_expiring_certs_count"

  #export PROD_EXPIRING_CERTS_COUNT="$prod_expiring_certs_count"
  #export PROD_EXPIRED_CERTS_COUNT="$prod_expiring_certs_count"

  message_body=$(envsubst < message.json)
  curl -sX POST "$WEBHOOK_URL" \
        -H 'Content-Type: application/json' \
        -d "$message_body"
fi
