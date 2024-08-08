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

start_port_forward() {
    local service_name=$1
    local namespace=$2
    local local_port=$3
    local dest_port=$4
    kubectl port-forward -n "$namespace" svc/"$service_name" "$local_port":"$dest_port" >/dev/null 2>&1 &
    local PF_PID=$!
    sleep 4  # Allow time for the port forwarding to establish
    echo "$PF_PID"  # Only echo the PID
}

kill_port_forward() {
    local pid=$1
    if [[ -n "$pid" ]]; then
        kill "$pid"
        echo "Port forwarding stopped for PID: $pid"
    fi
}

export TERM=xterm-256color

# Automatically set BASE_DIR to the directory where the script itself resides
BASE_DIR=$(dirname "$(realpath "${BASH_SOURCE[0]}")")

# Function to create SPs based on user input
create_sp() {
    local SP_NAME=$1
    local SP_PATH="$BASE_DIR/$SP_NAME"
    local NAMESPACE=$2


    if [ "$SP_NAME" == "ALL" ]; then
        echo "Creating all Service Providers..."
        PF_PID=$(start_port_forward "choreo-am-service" "$NAMESPACE" 9443 9443)
        for SP_DIR in "$BASE_DIR"/*; do
            if [ -d "$SP_DIR" ]; then
                echo "Processing $(basename "$SP_DIR")..."
                "$BASE_DIR/sp_common.sh" "$SP_DIR"
            fi
        done
        kill_port_forward "$PF_PID"
    elif [ -d "$SP_PATH" ]; then
        echo "Creating Service Provider: $SP_NAME"
        PF_PID=$(start_port_forward "choreo-am-service" "$NAMESPACE" 9443 9443)
        "$BASE_DIR/sp_common.sh" "$SP_PATH"
        kill_port_forward "$PF_PID"
    else
        echo "Error: Service Provider '$SP_NAME' does not exist."
        exit 1
    fi
}

if [[ -z "${APIM_ADMIN_USERNAME}" ]]; then
  APIM_ADMIN_USERNAME=""
fi
if [[ -z "${APIM_ADMIN_PASSWORD}" ]]; then
  APIM_ADMIN_PASSWORD=""
fi
if [[ -z "${APIM_URL}" ]]; then
  APIM_URL=""
fi
if [[ -z "${NAMESPACE}" ]]; then
  NAMESPACE=""
fi
if [[ -z "${SP_NAME}" ]]; then
  SP_NAME=""
fi

create_sp "$SP_NAME" "$NAMESPACE"
