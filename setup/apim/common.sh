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

generate_client_secret() {
  openssl rand -base64 48 | tr -dc 'a-zA-Z0-9-_' | head -c 43
}

#SP configs

#rudder configs
export RUDDER_SP_NAME="rudder"
export RUDDER_API_KEY="choreorudderclientid"
# shellcheck disable=SC2155
export RUDDER_API_SECRET="$(generate_client_secret)"
export RUDDER_CALL_BACK_URL="https://localhost"

#apim_service configs
export APIM_SERVICE_SP_NAME="apim"
export APIM_SERVICE_API_KEY="choreoapimserviceclientid"
# shellcheck disable=SC2155
export APIM_SERVICE_API_SECRET="$(generate_client_secret)"
export APIM_SERVICE_CALL_BACK_URL="https://localhost"

#rudder configs
export GRAPHQL_SP_NAME="graphql"
export GRAPHQL_API_KEY="choreocpgraphqlclientid"
# shellcheck disable=SC2155
export GRAPHQL_API_SECRET="$(generate_client_secret)"
export GRAPHQL_CALL_BACK_URL="https://localhost"

#app_service configs
export APP_SERVICE_SP_NAME="appService"
export APP_SERVICE_API_KEY="choreoappserviceclientid"
# shellcheck disable=SC2155
export APP_SERVICE_API_SECRET="$(generate_client_secret)"
export APP_SERVICE_CALL_BACK_URL="https://localhost"

#project_manager configs
export PROJECT_MANAGER_SP_NAME="projectManager"
export PROJECT_MANAGER_API_KEY="projectmanagerclientid"
# shellcheck disable=SC2155
export PROJECT_MANAGER_API_SECRET="$(generate_client_secret)"
export PROJECT_MANAGER_CALL_BACK_URL="https://localhost"

#connection service configs
export CONNECTION_SERVICE_SP_NAME="connectionService"
export CONNECTION_SERVICE_API_KEY="connectionserviceclientid"
# shellcheck disable=SC2155
export CONNECTION_SERVICE_API_SECRET="$(generate_client_secret)"
export CONNECTION_SERVICE_CALL_BACK_URL="https://localhost"

#delete manager configs
export DELETE_MANAGER_SP_NAME="deleteManager"
export DELETE_MANAGER_API_KEY="deletemanagerclientid"
# shellcheck disable=SC2155
export DELETE_MANAGER_API_SECRET="$(generate_client_secret)"
export DELETE_MANAGER_CALL_BACK_URL="https://localhost"

#pdp manager configs
export PDP_MANAGER_SP_NAME="pdpManager"
export PDP_MANAGER_API_KEY="pdpmanagerclientid"
# shellcheck disable=SC2155
export PDP_MANAGER_API_SECRET="$(generate_client_secret)"
export PDP_MANAGER_CALL_BACK_URL="https://localhost"

#system api pipeline configs
export SYSTEM_API_PIPELINE_SP_NAME="choreo_pdp_sync_pipeline1"
export SYSTEM_API_PIPELINE_API_KEY="systemapipelineclientid"
# shellcheck disable=SC2155
export SYSTEM_API_PIPELINE_API_SECRET="$(generate_client_secret)"
export SYSTEM_API_PIPELINE_CALL_BACK_URL="https://localhost"

#endpoint resolver configs
export ENDPOINT_RESOLVER_SP_NAME="endpointResolver"
export ENDPOINT_RESOLVER_API_KEY="endpointresolverclientid"
# shellcheck disable=SC2155
export ENDPOINT_RESOLVER_API_SECRET="$(generate_client_secret)"
export ENDPOINT_RESOLVER_CALL_BACK_URL="https://localhost"

#quota limiter configs
export QUOTA_LIMITER_SP_NAME="quotaLimiter"
export QUOTA_LIMITER_API_KEY="quotalimiterclientidclientid"
# shellcheck disable=SC2155
export QUOTA_LIMITER_API_SECRET="$(generate_client_secret)"
export QUOTA_LIMITER_CALL_BACK_URL="https://localhost"

#common functions
split_results(){
  # shellcheck disable=SC2001
  BODY=$(echo "$HTTP_RESPONSE" | sed -e 's/HTTPSTATUS\:.*//g')
  STATUS=$(echo "$HTTP_RESPONSE" | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

  export BODY
  export STATUS
}

echo_results () {
  split_results
  if [ "$STATUS" -eq 200 ]; then
    tput setaf 2;
    echo "$1"
    tput sgr0;
  elif [[ $BODY == *"already exists"* ]]; then
    tput setaf 1;
    echo "$2"
  else
    tput setaf 1;
    echo "$2 , Status code : $STATUS"
    tput sgr0;
    echo "response from server: $BODY"
  fi
  tput sgr0
}

# Function to check unset environment variables in given payload variables
check_unset_env_vars() {
    for payload_var_name in "$@"; do
        # Dereference the payload variable name to get its content
        payload=${!payload_var_name}

        echo "Checking ${payload_var_name} for unset environment variables..."
        # Use Perl to extract all unique placeholder names within the current payload
        env_vars=$(perl -nle 'print for m/\{(.*?)\}/g' <<< "${payload}" | sort -u)

        # Loop through each extracted environment variable name
        for var in $env_vars; do
            # Fetch the value of the environment variable
            var_value="${!var}"
            # Check if the environment variable is unset or set to an empty string
            if [[ -z ${var_value} ]]; then
                echo -e "  - \033[31m[NOT SET OR EMPTY]\033[0m '$var'. Please set the variable in common.sh with a non-empty value."
                exit 1
            else
                echo "  - [SET] $var (Value: '${var_value}')"
            fi
        done
    done
}

# Function to replace placeholders with their corresponding environment variable values
replace_placeholders() {
    local content="$1"  # The content with placeholders
    # Use Perl to replace all placeholders with the value of the corresponding environment variable
    # shellcheck disable=SC2005
    echo "$(perl -pe 's/\{(.*?)\}/defined $ENV{$1} ? $ENV{$1} : "{$1}"/ge' <<< "$content")"
}
