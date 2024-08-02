#!/bin/bash

# -------------------------------------------------------------------------------------
#
# Copyright (c) 2024, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
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
if [[ -z "${EXCLUDED_FILE_PATH}" ]]; then
  EXCLUDED_FILE_PATH=""
fi


az_login "$CLIENT_ID" "$CLIENT_SECRET" "$TENANT_ID"
init_csv "Application ID, Application Name, Expired Date" "$OUTPUT_PATH"/expired_secrets.csv
init_csv "Application ID, Application Name, Expires On" "$OUTPUT_PATH"/expiring_secrets.csv

echo "[INFO] Getting entra application list"
mapfile -t raw_entra_apps < <(az ad app list --all --query "[].appId" | yq '.[]')
echo "[INFO] found ${#raw_entra_apps[@]} entra applications"

echo "[INFO] Getting excluded entra application list"
mapfile -t excluded_entra_apps < <(yq '.excludedEntraAppIDs[]' excluded-apps.yaml)
echo "[INFO] found ${#excluded_entra_apps[@]} entra applications to exclude"

declare -A remove_map
for entry in "${excluded_entra_apps[@]}"; do
  remove_map["$entry"]=1
done

ad_apps=()
set +u
for item in "${raw_entra_apps[@]}"; do
  if [[ -z "${remove_map[$item]}" ]]; then
    ad_apps+=("$item")
  fi
done
set -u

echo "[INFO] Getting expiry details of ${#ad_apps[@]} entra applications"
for ad_app in "${ad_apps[@]}"; do
    all_secrets_expired="true"
    mapfile -t expiry < <(az ad app credential list --id "$ad_app" --query "[].endDateTime" | yq '.[]')
    if [ "${#expiry[@]}" -eq 0 ]; then
        echo "[INFO] No secret is found for application: $ad_app"
        all_secrets_expired="false"
    else
        for exp in "${expiry[@]}"; do
            if [ "$exp" == "null" ]; then
                echo "[INFO] No expiry date is found for application: $ad_app"
            else
                expiry_date=$(date -d "$exp" +%s)
                current_date=$(date +%s)
                one_month_later=$(date -d "+1 month" +%s)
                if [ "$expiry_date" -ge "$current_date" ] && [ "$expiry_date" -le "$one_month_later" ]; then
                  echo "[WARNING] Secret is expiring within a month for application: $ad_app"
                  app_name=$(az ad app show --id "$ad_app" --query "displayName" | yq .)
                  echo "$ad_app, $app_name, $exp" >> "$OUTPUT_PATH"/expiring_secrets.csv
                  all_secrets_expired="false"
                elif [ "$expiry_date" -gt "$one_month_later" ]; then
                  all_secrets_expired="false"
                fi
            fi
        done
        if [ "$all_secrets_expired" == "true" ]; then
            echo "[ERROR] All secrets are expired for application: $ad_app"
            app_name=$(az ad app show --id "$ad_app" --query "displayName" | yq .)
            echo "$ad_app, $app_name, $exp" >> "$OUTPUT_PATH"/expired_secrets.csv
        fi
    fi
done

expiring_secret_count=$(tail -n +2 "$OUTPUT_PATH"/expiring_secrets.csv | wc -l)
expired_secret_count=$(tail -n +2 "$OUTPUT_PATH"/expired_secrets.csv | wc -l)

if [ "$expiring_secret_count" -gt 0 ]; then
  echo "[WARNING] There are $expiring_secret_count secrets expiring within a month"
  export EXPIRING_ENTRA_APPS_COUNT="$expiring_secret_count"
  export EXPIRED_ENTRA_APPS_COUNT="$expired_secret_count"

  message_body=$(envsubst < message.json)
  curl -sX POST "$WEBHOOK_URL" \
        -H 'Content-Type: application/json' \
        -d "$message_body"
  exit 1
fi
