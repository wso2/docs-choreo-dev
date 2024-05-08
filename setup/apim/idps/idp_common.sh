#!/bin/bash

# IDP directory
IDP_DIR=$1

# Validate if directory is provided and exists
if [ -z "$IDP_DIR" ] || [ ! -d "$IDP_DIR" ]; then
    echo "Error: IDP directory is not provided or does not exist."
    exit 1
fi

# Navigate to the IDP directory
cd "$IDP_DIR" || { echo "Error: Failed to change directory to $IDP_DIR"; exit 1; }

# Get IDP name from directory name
idp_name=$(basename "$IDP_DIR")

echo -e "\033[0;32mCreating IDP: $idp_name....................\033[0m"

# Source the common utilities script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
COMMON_SH_PATH="$SCRIPT_DIR/../common.sh"

if [ -z "${split_results+x}" ]; then
  # shellcheck disable=SC1090
    source "$COMMON_SH_PATH"
fi

check_required_common_variables

create_config=$(cat "create_idp.xml")
delete_config=$(cat "delete_idp.xml")

check_unset_env_vars create_config delete_config

# Replace placeholders in each payload
create_config=$(replace_placeholders "$create_config")
delete_config=$(replace_placeholders "$delete_config")

# Attempt to delete existing IDP first
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: text/xml;charset=UTF-8" --header "SOAPAction:urn:deleteIdP" -u "${APIM_ADMIN_USERNAME}":"${APIM_ADMIN_PASSWORD}" --data "${delete_config}" "${APIM_URL}"/services/IdentityProviderMgtService.IdentityProviderMgtServiceHttpsSoap11Endpoint -k)
echo_results "$idp_name IDP deleted successfully" "Error while deleting $idp_name IDP" "Unknown error while deleting the $idp_name IDP"

# Create IDP
# shellcheck disable=SC2034
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: text/xml;charset=UTF-8" --header "SOAPAction:urn:addIdp" -u "${APIM_ADMIN_USERNAME}":"${APIM_ADMIN_PASSWORD}" --data "${create_config}" "${APIM_URL}"/services/IdentityProviderMgtService.IdentityProviderMgtServiceHttpsSoap11Endpoint -k)
echo_results "$idp_name IDP added successfully" "Error while adding $idp_name IDP" "$idp_name IDP already exists"

echo -e "\033[0;32m$idp_name IDP created successfully.\033[0m"
