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

# Automatically set BASE_DIR to the directory where the script itself resides
BASE_DIR=$(dirname "$(realpath "${BASH_SOURCE[0]}")")

# Function to list all available SPs
list_sps() {
    echo "Available Service Providers:"
    for SP_DIR in "$BASE_DIR"/*; do
        if [ -d "$SP_DIR" ]; then
            echo " - $(basename "$SP_DIR")"
        fi
    done
}

# Function to create SPs based on user input
create_sp() {
    local SP_NAME=$1
    local SP_PATH="$BASE_DIR/$SP_NAME"

    if [ "$SP_NAME" == "ALL" ]; then
        echo "Creating all Service Providers..."
        for SP_DIR in "$BASE_DIR"/*; do
            if [ -d "$SP_DIR" ]; then
                echo "Processing $(basename "$SP_DIR")..."
                "$BASE_DIR/sp_common.sh" "$SP_DIR"
            fi
        done
    elif [ -d "$SP_PATH" ]; then
        echo "Creating Service Provider: $SP_NAME"
        "$BASE_DIR/sp_common.sh" "$SP_PATH"
    else
        echo "Error: Service Provider '$SP_NAME' does not exist."
        exit 1
    fi
}

# Main script execution
echo "Welcome to the Service Provider Management System"
while true; do
    echo "------------------------------------------------"
    echo "Enter 'list' to display all available SPs."
    echo "Enter 'ALL' to create all SPs."
    echo "Enter the name of a specific SP to create it."
    echo "Enter 'exit' to quit."
    echo "------------------------------------------------"
    list_sps
    echo "------------------------------------------------"

    read -r -p "Please enter your choice: " USER_CHOICE

    case $USER_CHOICE in
        list)
            list_sps
            ;;
        ALL)
            create_sp "ALL"
            exit 0
            ;;
        exit)
            echo "Exiting the Service Provider Management System."
            exit 0
            ;;
        *)
            create_sp "$USER_CHOICE"
            exit 0
            ;;
    esac
done
