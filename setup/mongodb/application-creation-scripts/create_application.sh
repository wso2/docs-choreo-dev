#!/bin/bash

show_usage_and_exit() {
   echo "Usage: $0 [-g REQUIRED_OPTION] [-b REQUIRED_OPTION] [-v REQUIRED_OPTION] [-c REQUIRED_OPTION] [-n REQUIRED_OPTION] [-k REQUIRED_OPTION] [-s REQUIRED_OPTION] [-t REQUIRED_OPTION] [-p REQUIRED_OPTION]" >&2
   echo "This script creates the application from the MongoDB Realm"
   echo "Mandatory arguments:"
   echo "     -g    id of the mongodb atlas project"
   echo "     -b    public key of the admin api"
   echo "     -v    private key of the admin api"
   echo "     -c    name of the mongodb atlas cluster"
   echo "     -n    name of resource registry db"
   echo "     -k    consumer key of devportal app"
   echo "     -s    consumer secret of devportal app"
   echo "     -t    token endpoint url"
   echo "     -p    spec populator systemapi url"
   exit 1
}

# Get the following from params
# MongoDB configs and secrets
GROUP_ID=""
Public_API_Key=""
Private_API_Key=""
CLUSTER_NAME=""
RESOURCE_REGISTRY_DB_NAME=""
# spec populator service configs
consumerKey=""
tokenEndpoint=""
specPopulatorUrl=""
# TODO - Get the secret value from key vault
consumerSecret=""

while getopts ":g:b:v:c:n:k:s:t:p" FLAG; do
    case $FLAG in
        g)
            GROUP_ID=$OPTARG
            ;;
        b)
            Public_API_Key=$OPTARG
            ;;
	      v)
            Private_API_Key=$OPTARG
            ;;
	      c)
            CLUSTER_NAME=$OPTARG
            ;;
	      n)
            RESOURCE_REGISTRY_DB_NAME=$OPTARG
            ;;
      	k)
            consumerKey=$OPTARG
            ;;
	      s)
            consumerSecret=$OPTARG
            ;;
        t)
            tokenEndpoint=$OPTARG
            ;;
        p)
            specPopulatorUrl=$OPTARG
            ;;
	      h)
            show_usage_and_exit
            ;;
	      \?)
            # Invalid option
            echo "Invalid option: -$OPTARG" >&2
            exit 1
            ;;
        :)
            # Missing argument for an option that requires one
            echo "Option -$OPTARG requires an argument" >&2
            exit 1
            ;;
    esac
done

# Constants
APP_NAME="MarketplaceApp"
FUNCTION_NAME="marketplace_assist_trigger_function"
BASE_URL="https://services.cloud.mongodb.com/api/admin/v3.0"
SOURCE_CODE_FILE_PATH="function_code.js"
CONFIG_FILE_PATH="consumerSecretValue.json"

# Following are the variables that will be set by the script
ACCESS_TOKEN=""
AUTH_HEADER=""
APP_ID=""
FUNCTION_ID=""
SERVICE_ID=""
FUNCTION_CODE=""

echo "Starting the script"

function get_function_code() {
    FUNCTION_CODE=$(awk 'BEGIN{ORS="\\n"} {gsub(/"/, "\\\\\"")} 1' "$SOURCE_CODE_FILE_PATH")
    echo "$FUNCTION_CODE"
}

function get_access_token() {
    url="$BASE_URL/auth/providers/mongodb-cloud/login"
    data='{"username": "'"$Public_API_Key"'", "apiKey": "'"$Private_API_Key"'"}'
    response=$(curl --location "$url" --header "Content-Type: application/json" --data "$data")
    ACCESS_TOKEN=$(jq -r '.access_token' <<< "$response")
    AUTH_HEADER="Authorization: Bearer $ACCESS_TOKEN"
}

function create_application() {
    url="$BASE_URL/groups/$GROUP_ID/apps"
    data='{"name": "'"$1"'"}'
    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER" --data "$data")
    APP_ID=$(jq -r '._id' <<< "$response")
    echo "$response"
}

function create_function() {
    url="$BASE_URL/groups/$GROUP_ID/apps/$1/functions"
    source_code=$(jq -Rs . < "$3")
    data=$(jq -n --arg name "$2" --argjson source "$source_code" '{
        "name": $name,
        "private": true,
        "source": $source
    }')

    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER" --data "$data")
    FUNCTION_ID=$(jq -r '._id' <<< "$response")
    echo "$response"
}

function add_configs() {
    url="$BASE_URL/groups/$GROUP_ID/apps/$1/values"
    data='{
        "name": "'"$2"'",
        "private": true,
        "value": "'"$3"'"
        }'
    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER" --data "$data")
    echo "$response"
}

function add_secrets() {
    url="$BASE_URL/groups/$GROUP_ID/apps/$1/secrets"
    data='{
        "name": "'"$2"'",
        "value": "'"$3"'"
        }'
    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER" --data "$data")
    echo "$response"
}

function link_data_source() {
    url="$BASE_URL/groups/$GROUP_ID/apps/$1/services"
    data='{
            "name": "mongodb-atlas",
            "type": "mongodb-atlas",
            "config": {
                "clusterName": "'"$2"'",
                "readPreference": "",
                "readPreferenceTagSets": [],
                "wireProtocolEnabled": true
            }
        }'
    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER" --data "$data")
    SERVICE_ID=$(jq -r '._id' <<< "$response")
    echo "$response"
}

function create_trigger() {
    url="$BASE_URL/groups/$GROUP_ID/apps/$1/triggers"
    data='{
            "name": "marketplace_assist_trigger",
            "type": "DATABASE",
            "function_id": "'"$2"'",
            "function_name": "'"$5"'",
            "disabled": false,
            "config": {
                "operation_types": ["INSERT","UPDATE","DELETE","REPLACE"],
                "database": "'"$4"'",
                "collection": "resources",
                "clusterName": "'"$6"'",
                "service_id": "'"$3"'",
                "match": {},
                "project": {},
                "full_document": true,
                "full_document_before_change": false,
                "unordered": false,
                "skip_catchup_events": false,
                "tolerate_resume_errors": false,
                "maximum_throughput": false
            }
        }
        '
    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER" --data "$data")
    echo "$response"
}

echo "Get script dependencies"
brew install jq

echo "Getting the access token"
get_access_token

echo "Creating the application"
create_application $APP_NAME

# Add configs
echo "Adding configs and secrets"
add_configs "$APP_ID" "consumerKey" "$consumerKey"
add_configs "$APP_ID" "tokenEndpoint" "$tokenEndpoint"
add_configs "$APP_ID" "specPopulatorUrl" "$specPopulatorUrl"

# Add secrets
add_secrets "$APP_ID" "consumerSecret" "$consumerSecret"

echo "Creating the function"
create_function "$APP_ID" $FUNCTION_NAME $SOURCE_CODE_FILE_PATH

echo "Linking the data source"
link_data_source "$APP_ID" "$CLUSTER_NAME"

echo "Creating the trigger"
create_trigger "$APP_ID" "$FUNCTION_ID" "$SERVICE_ID" "$RESOURCE_REGISTRY_DB_NAME" $FUNCTION_NAME "$CLUSTER_NAME"

# Add function dependencies and configs referencing the secrets
sh run_cli_commands.sh "$Public_API_Key" "$Private_API_Key" $APP_NAME $CONFIG_FILE_PATH

