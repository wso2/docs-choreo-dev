#!/bin/bash

# -------------------------------------------------------------------------------------
#
# Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

# Directory where SP payloads are located
SP_DIR=$1

# Validate if directory is provided and exists
if [ -z "$SP_DIR" ] || [ ! -d "$SP_DIR" ]; then
    echo "Error: SP directory is not provided or does not exist."
    exit 1
fi

echo "Creating Service Provider: $SP_DIR"

SP_NAME="${SP_DIR##*/}"
SP_NAME=$(echo "$SP_NAME" | tr '[:lower:]' '[:upper:]')

echo -e "\033[0;32mCreating SP: $SP_NAME....................\033[0m"

# Check if common.sh is already sourced, if not do it
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Path to common.sh relative to this script
COMMON_SH_PATH="$SCRIPT_DIR/../common.sh"

if [ -z "${split_results+x}" ]; then
    source "${COMMON_SH_PATH}"
fi

check_required_common_variables

# Check if all four required payloads exist in the same directory as the script.
if [ ! -f "${SP_DIR}/create_sp_payload.xml" ] || [ ! -f "${SP_DIR}/get_sp_payload.xml" ] || [ ! -f "${SP_DIR}/create_oauth_app_payload.xml" ] || [ ! -f "${SP_DIR}/update_sp_payload.xml" ]; then
    echo "XML Payload files not found. Exiting..."
    exit 1
fi

create_sp_payload=$(cat "${SP_DIR}/create_sp_payload.xml")
get_sp_payload=$(cat "${SP_DIR}/get_sp_payload.xml")
update_sp_payload=$(cat "${SP_DIR}/update_sp_payload.xml")
create_oauth_app_payload=$(cat "${SP_DIR}/create_oauth_app_payload.xml")

check_unset_env_vars create_sp_payload update_sp_payload get_sp_payload create_oauth_app_payload

# Replace placeholders in each payload
create_sp_payload=$(replace_placeholders "$create_sp_payload")
get_sp_payload=$(replace_placeholders "$get_sp_payload")
update_sp_payload=$(replace_placeholders "$update_sp_payload")
create_oauth_app_payload=$(replace_placeholders "$create_oauth_app_payload")

# Create oauth app
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:registerOAuthApplicationData" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data "$create_oauth_app_payload" ${APIM_URL}/services/OAuthAdminService.OAuthAdminServiceHttpsSoap12Endpoint/ -k)
echo_results "$SP_NAME OAuth application added successfully" "Error while adding $SP_NAME OAuth application"

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:createApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data "$create_sp_payload" ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "$SP_NAME service provider created" "Error while creating $SP_NAME service provider"

# Get the Id of created app
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:getApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data "$get_sp_payload" ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "$SP_NAME SP id retrieved" "Error while getting $SP_NAME app Id"

appId=$(echo "$BODY" | xmllint --format - | perl -ne 'if (/applicationID/){ s/.*?>//; s/<.*//;print;}')

update_sp_payload=$(echo $update_sp_payload | sed "s#\[\APP_ID\]#$appId#g")

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:updateApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data "$update_sp_payload" ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "$SP_NAME service provider updated with OAuth2 app" "Error while updating $SP_NAME service provider with OAuth2 app"

echo -e "\033[0;32m$SP_NAME SP created successfully.\033[0m"

# Log output section
API_KEY_VAR_NAME="${SP_NAME}_API_KEY"
API_SECRET_VAR_NAME="${SP_NAME}_API_SECRET"

# Fetch values using indirect reference
API_KEY="${!API_KEY_VAR_NAME}"
API_SECRET="${!API_SECRET_VAR_NAME}"

# Output to log file in the script's execution location
echo "SP_NAME: $SP_NAME" >> "$SCRIPT_DIR/sp_out.log"
echo "API_KEY: $API_KEY" >> "$SCRIPT_DIR/sp_out.log"
echo "API_SECRET: $API_SECRET" >> "$SCRIPT_DIR/sp_out.log"
echo "-----------------------------------------------------------------------------------" >> "$SCRIPT_DIR/sp_out.log"
