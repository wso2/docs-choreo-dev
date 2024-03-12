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

# Function to display usage instructions
usage() {
    echo "Usage: $0 <command> [arguments]"
    echo
    echo "Commands:"
    echo "  add <bot-name> <token> <environment> <skip_confirmation>  Add a single bot."
    echo "      - <bot-name>: Name of the bot to add."
    echo "      - <token>: Personal Access Token (PAT) of the bot."
    echo "      - <environment>: The environment where the bot will be added (e.g., dev, stage, prod)."
    echo "      - <skip_confirmation>: Set to 'true' to skip confirmation prompt or 'false' to require confirmation."
    echo
    echo "  list <environment>                                         List all bots in a specified environment."
    echo "      - <environment>: The environment to list bots from."
    echo
    echo "  delete <ID> <environment>                                  Delete a bot by ID in a specified environment."
    echo "      - <ID>: The unique identifier of the bot to delete."
    echo "      - <environment>: The environment where the bot will be deleted."
    echo
    echo "  bulk-add <csv_file_path> <environment>                     Add bots in bulk from a CSV file."
    echo "      - <csv_file_path>: Path to the CSV file containing bot information."
    echo "      - <environment>: The environment where bots will be added."
    echo "      CSV Format: 'bot-name,token' without header."
    echo
    echo "  bulk-delete <csv_file_path> <environment>                  Delete bots in bulk from a CSV file based on IDs."
    echo "      - <csv_file_path>: Path to the CSV file containing IDs of bots to delete."
    echo "      - <environment>: The environment where bots will be deleted."
    echo "      CSV Format: 'ID' without header."
    echo
}

# Function to handle port-forwarding and kill the process after script execution
function port_forward() {
    local namespace="${1}-choreo-system"
    kubectl port-forward svc/dp-cloud-manager -n "$namespace" 3200:80 >/dev/null 2>&1 &
    PF_PID=$!
    echo "Port-forwarding started with PID: $PF_PID"
    echo
    sleep 3 # Wait for port-forwarding to establish
    trap 'if kill -0 $PF_PID > /dev/null 2>&1; then kill $PF_PID; echo "Port-forwarding stopped."; else echo "Port-forwarding process not found."; fi' EXIT

}

function check_bot_membership() {
    local bot_username="$1"
    local github_token="$2"

    # Construct the organization name based on the environment
    local org_name
    if [[ "$environment" == "stage" ]]; then
        org_name="choreo-userapps-gitops-staging"
    else
        org_name="choreo-userapps-gitops-$environment"
    fi

    # Fetch the membership type of the bot in the organization
    local membership
    membership=$(curl -s -H "Authorization: token ${github_token}" \
        "https://api.github.com/orgs/${org_name}/memberships/${bot_username}" | jq -r '.role')

    if [[ "$membership" != "admin" ]]; then
        echo "The bot '$bot_username' is not an owner of the organization '$org_name'."
        return 1
    fi
}

function check_pat_permissions() {
    local github_token="$1"
    local required_permissions=("admin:org" "admin:org_hook" "notifications" "read:public_key" "read:repo_hook" "repo" "workflow")

    # Fetch the permissions associated with the PAT
    local pat_permissions
    pat_permissions=$(curl -sS -f -I -H "Authorization: token ${github_token}" https://api.github.com 2>/dev/null | grep ^x-oauth-scopes: | cut -d' ' -f2- | tr -d "[:space:]" | tr ',' '\n')

    # Convert fetched permissions and required permissions to arrays
    readarray -t fetched_permissions <<<"$pat_permissions"
    local missing_permissions=()

    # Check each required permission against fetched permissions
    for required_permission in "${required_permissions[@]}"; do
        if [[ ! " ${fetched_permissions[*]} " =~ ${required_permission} ]]; then
            # If a required permission is missing, add it to the missing permissions list
            missing_permissions+=("$required_permission")
        fi
    done

    # If any permissions are missing, return an error and the list of missing permissions
    if [ ${#missing_permissions[@]} -ne 0 ]; then
        echo "Error: Missing required permissions: ${missing_permissions[*]}"
        return 1
    fi
}


# Function to write IDs and names of bots that have been added at a given run/ time
function write_bot_to_csv() {
    local bot_id="$1"
    local name="$2"
    local datetime
    datetime=$(date "+%Y-%m-%d_%H-%M")
    local output_file="added_bots_${datetime}.csv"

    # Append bot ID and name to the file
    echo "${bot_id},${name}" >> "$output_file"
}

# Function to check if the bot already exists
function bot_exists() {
    local name_to_check="$1"
    local existing_names
    existing_names=$(list_bots | jq -r '.data[].name')
    if echo "$existing_names" | grep -q "$name_to_check"; then
        return 0 # Bot exists
    else
        return 1 # Bot does not exist
    fi
}

function add_bot() {
    local bot="$1"
    local token="$2"
    local environment="$3"
    local skip_confirmation="$4" # Controls the confirmation prompt

    # Check if environment is 'stage' and adjust org variable accordingly. This is due to Stage GitOps repo not conforming to system namespace prefix
    if [[ "$environment" == "stage" ]]; then
        local org="choreo-userapps-gitops-staging"
    else
        local org="choreo-userapps-gitops-$environment"
    fi

    local email="${bot}@wso2.com"
    local name="$bot"

    # Confirmation prompt, skipping if part of a bulk addition or if explicitly skipped
    if [[ "$skip_confirmation" != "true" ]]; then
        echo "You are about to add the following bot:"
        echo "Bot: $bot, Environment: $environment"
	echo
        read -r -p "Are you sure you want to proceed? (y/N): " confirmation
        if [[ "$confirmation" != "y" && "$confirmation" != "Y" ]]; then
            echo "Bot addition cancelled."
	    echo
            return 1
        fi
    fi

    # Check PAT permissions before adding the bot
    echo "Checking permissions for PAT associated with '$bot'..."
    if ! check_pat_permissions "$token"; then
        echo "The PAT for '$bot' lacks required permissions. Skipping this bot."
        echo
        return 1
    fi

    #Check if the bot is an owner of the organization
    if ! check_bot_membership "$bot" "$token"; then
        echo "Skipping the addition of '$bot' due to insufficient organization membership status."
        echo
        return 1
    fi

    # Check if bot exists in the backend
    if bot_exists "$name"; then
        echo
        echo "Bot with name '$name' already exists. Skipping..."
        echo
        return
    fi

    # Constructing the payload and making the API call
    local payload
    payload=$(cat <<EOF
{
    "credential": "{\"user\":\"$bot\",\"org\":\"$org\",\"token\":\"$token\",\"email\":\"$email\"}",
    "name": "$name",
    "type": "github",
    "metadata": "{\"type\":\"gitops\"}"
}
EOF
)
    local response
    response=$(curl --silent --location 'http://localhost:3200/api/v1/git/credentials' \
                    --header 'Content-Type: application/json' \
                    --header 'x-project-id: global' \
                    --header 'x-organization-id: 0' \
                    --data-raw "$payload" \
                    --write-out "\n%{http_code}")

    local body
    body=$(echo "$response" | head -n -1)
    local status
    status=$(echo "$response" | tail -n1)

    if [[ "$status" -ne 200 ]] && [[ "$status" -ne 201 ]]; then
        echo "Error adding bot: HTTP status $status"
        echo "Response: $body"
        return 1
    else
        echo "Bot $name added successfully"
        local bot_id
        bot_id=$(echo "$body" | jq -r '.data.id')
        echo "Bot ID: $bot_id"
	echo
        write_bot_to_csv "$bot_id" "$name"
        return 0
    fi
}

# Function to delete bot
function delete_bot() {
    local id="$1"
    local skip_confirmation="$2"

    # Fetch the current list of bots to get details of the bot with the given ID
    local bot_details
    bot_details=$(curl -s --location 'http://localhost:3200/api/v1/git/credentials' \
                         --header 'x-project-id: global' \
                         --header 'x-organization-id: 0' | jq '.data[] | select(.id == "'"$id"'")')

    # Check if the bot exists
    if [ -z "$bot_details" ]; then
        echo "Bot with ID $id does not exist."
	echo
        return 1
    fi

    # Conditional confirmation based on skip_confirmation flag
    if [[ "$skip_confirmation" != "true" ]]; then
        echo "You are about to delete the following bot:"
        echo "$bot_details" | jq '{id: .id, name: .name}'

        read -r -p "Are you sure you want to delete this bot? (y/N): " confirmation
        if [[ "$confirmation" != "y" && "$confirmation" != "Y" ]]; then
            echo "Deletion cancelled."
            echo
            return 1
        fi
    fi

    # Proceed with deletion
    curl -s --location --request DELETE "http://localhost:3200/api/v1/git/credentials/$id" \
         --header 'x-project-id: global' \
         --header 'x-organization-id: 0'

    echo "Bot with ID $id has been deleted."
    echo
}

# Function to list bots
function list_bots() {
    local response
    response=$(curl --silent --location 'http://localhost:3200/api/v1/git/credentials' \
                    --header 'x-project-id: global' --header 'x-organization-id: 0' \
                    --write-out "\n%{http_code}")
    local body
    body=$(echo "$response" | head -n -1)

    local status
    status=$(echo "$response" | tail -n1)

    if [[ "$status" -ne 200 ]]; then
        echo "Error listing bots: HTTP status $status"
        echo "Response: $body"
        return 1 # Indicate failure
    else
        echo "$body" | jq .
    fi
}

function bulk_delete_bots() {
    local csv_file="$1"
    echo "Fetching current bots..."
    echo
    local bots
    bots=$(list_bots | jq '.data[] | {id, name}' | jq -s .) # Fetch and structure bot data

    echo "Checking bots to be deleted against current bots..."
    local ids_to_delete=()
    local found=false # Flag to check if any bot matches

    while IFS=, read -r id name
    do
        if [ -z "$id" ]; then
            echo "Skipping empty line."
            echo
            continue
        fi
        # Check if bot ID exists in current bots
        local bot_exists
        bot_exists=$(echo "$bots" | jq --arg id "$id" 'map(select(.id == $id)) | length')

        if [[ "$bot_exists" -eq 0 ]]; then
            echo "Bot with ID $id does not exist, skipping."
            echo
        else
            ids_to_delete+=("$id") # Store IDs to delete
            found=true
            # Display bots to be deleted by matching ID for confirmation
            echo "$bots" | jq --arg id "$id" '.[] | select(.id == $id)'
        fi
    done < "$csv_file"

    if [ "$found" = false ]; then
        echo "No matching bots found to delete."
        return 0
    fi

    # Ask for confirmation
    read -r -p "Are you sure you want to delete the above bots? (y/N) " confirmation
    if [[ $confirmation =~ ^[Yy]$ ]]; then
        for id in "${ids_to_delete[@]}"; do
            echo
            echo "Deleting bot with ID: $id"
            delete_bot "$id" true
            delete_status=$?
            if [ $delete_status -ne 0 ]; then
                echo "Error deleting bot with ID $id."
                echo
            fi
        done
    else
        echo "Bot deletion cancelled."
        echo
    fi
}

function bulk_add_bots() {
    local csv_file="$1"
    local environment="$2"

    echo "You are about to add bots from $csv_file to the environment: $environment."
    echo "The following bots will be added:"

    while IFS=, read -r bot token
    do
        if [ -z "$bot" ] || [ -z "$token" ]; then
            continue # Skip empty lines or incomplete entries
        fi
        echo "- $bot"
    done < "$csv_file"
    echo

    # Use read -p to prompt for confirmation
    read -r -p "Are you sure you want to proceed with adding all bots listed above? (y/N): " confirmation
    if [[ $confirmation != [Yy] ]]; then
        echo "Bulk bot addition cancelled."
        echo
        return 1
    fi

    while IFS=, read -r bot token
    do
        if [ -z "$bot" ] || [ -z "$token" ]; then
            continue
        fi
        # Proceed with adding each bot, skipping the confirmation for individual additions
        add_bot "$bot" "$token" "$environment" true
    done < "$csv_file"
}

# Main script logic
case "$1" in
    add)
        if [ $# -ne 5 ]; then
            usage
            exit 1
        fi
        port_forward "$4"
        # Preliminary check to see if the bot exists before proceeding
        if ! bot_exists "${@:2:1}"; then
            # Proceed with adding the bot only if they don't exist
            add_bot "${@:2:3}" "$5"
        else
            echo "Bot '$2' already exists, skipping addition."
            echo
        fi
        ;;
    list)
        if [ $# -ne 2 ]; then
	    usage
            exit 1
        fi
        port_forward "$2"
        list_bots
        ;;
    delete)
        if [ $# -ne 3 ]; then
	    usage
            exit 1
        fi
        port_forward "$3"
        delete_bot "${@:2}" false
        ;;
    bulk-add)
        if [ $# -ne 3 ]; then
	    usage
            exit 1
        fi
        port_forward "$3"
        bulk_add_bots "${@:2}"
        ;;
    bulk-delete)
        if [ $# -ne 3 ]; then
            usage
            exit 1
        fi
        port_forward "$3"
        bulk_delete_bots "${@:2}"
	;;
    *)
	usage
        exit 1
        ;;
esac
