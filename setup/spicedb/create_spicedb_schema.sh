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

PF_PID=$(start_port_forward "spicedb-cluster" "spicedb-operator" 50051 50051)

grpcurl -d '{"schema": "definition choreo_permissions {\nrelation allowed : choreo_roles#has\n}\ndefinition choreo_roles {\n    relation has : choreo_groups | choreo_project_groups | choreo_component_groups\n}\ndefinition choreo_groups {}\ndefinition choreo_project_groups {\n    relation related_group : choreo_groups\n}\ndefinition choreo_component_groups {}"}' -H "Authorization: Bearer $TOKEN" -plaintext localhost:50051 authzed.api.v1.SchemaService/WriteSchema

kill_port_forward "$PF_PID"
