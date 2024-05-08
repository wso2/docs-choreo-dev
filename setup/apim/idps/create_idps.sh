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

# Base directory for all IDPs
BASE_DIR=$(dirname "$(realpath "${BASH_SOURCE[0]}")")

echo "Welcome to the IDP Management System"
while true; do
    echo "------------------------------------------------"
    echo "Enter 'list' to display all available IDPs."
    echo "Enter 'ALL' to create all IDPs."
    echo "Enter the name of a specific IDP to create it."
    echo "Enter 'exit' to quit."
    echo "------------------------------------------------"

    echo "Available IDPs:"
    for IDP_DIR in "$BASE_DIR"/*; do
        if [ -d "$IDP_DIR" ]; then
            echo " - $(basename "$IDP_DIR")"
        fi
    done

    echo "------------------------------------------------"

    read -rp "Please enter your choice: " USER_CHOICE

    case $USER_CHOICE in
        list)
            echo "Available IDPs:"
            for IDP_DIR in "$BASE_DIR"/*; do
                if [ -d "$IDP_DIR" ]; then
                    echo " - $(basename "$IDP_DIR")"
                fi
            done
            ;;
        ALL)
            for IDP_DIR in "$BASE_DIR"/*; do
                if [ -d "$IDP_DIR" ]; then
                    echo "Processing $(basename "$IDP_DIR")..."
                    "$BASE_DIR/idp_common.sh" "$IDP_DIR"
                fi
            done
            exit 0
            ;;
        exit)
            echo "Exiting the IDP Management System."
            exit 0
            ;;
        *)
            IDP_PATH="$BASE_DIR/$USER_CHOICE"
            if [ -d "$IDP_PATH" ]; then
                "$BASE_DIR/idp_common.sh" "$IDP_PATH"
            else
                echo "Error: IDP '$USER_CHOICE' does not exist."
            fi
            ;;
    esac
done
