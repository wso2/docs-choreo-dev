#!/bin/bash

# This script deletes the application from the MongoDB Realm

# Required parameters:
GROUP_ID=$2
Public_API_Key=$3
Private_API_Key=$4
CLIENT_APP_ID=$1

# constants
BASE_URL="https://services.cloud.mongodb.com/api/admin/v3.0"

# Following are the variables that will be set by the script
ACCESS_TOKEN=""
AUTH_HEADER=""
APP_ID=""

echo "Starting the script"

function get_access_token() {
    url="$BASE_URL/auth/providers/mongodb-cloud/login"
    data='{"username": "'"$Public_API_Key"'", "apiKey": "'"$Private_API_Key"'"}'
    response=$(curl --location "$url" --header "Content-Type: application/json" --data "$data")
    ACCESS_TOKEN=$(jq -r '.access_token' <<< "$response")
    AUTH_HEADER="Authorization: Bearer $ACCESS_TOKEN"
}

function get_app_id() {
    url="$BASE_URL/groups/$GROUP_ID/apps"
    response=$(curl --location "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER")
    APP_ID=$(echo "$response" | jq -r --arg client_app_id "$CLIENT_APP_ID" '.[] | select(.client_app_id==$client_app_id) | ._id')
    echo "$response"
    echo "$APP_ID"
}

function delete_application() {
    url="$BASE_URL/groups/$GROUP_ID/apps/$1"
    response=$(curl --location --request DELETE "$url" --header 'Content-Type: application/json' --header "$AUTH_HEADER")
    echo "$response"
}

echo "Script started"
echo "$CLIENT_APP_ID"

echo "Get script dependencies"
brew install jq

echo "Getting the access token"
get_access_token

echo "Getting the app id"
get_app_id "$CLIENT_APP_ID"
echo "App ID: $APP_ID"

echo "Deleting the application"
delete_application "$APP_ID"
