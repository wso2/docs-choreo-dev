#!/bin/bash
# -------------------------------------------------------------------------------------
#
# Copyright (c) 2025, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

kubectl port-forward svc/opensearch -n observability 9200 &
pid=$!
sleep 10
password=$(kubectl get secret opensearch-admin-credentials-secret -o yaml -n observability | yq eval '.data["password"]' | base64 -d)
token=$(echo -n "admin:$password" | base64)

echo "health"
curl --location 'https://localhost:9200/_cluster/health?pretty' --header "Authorization: Basic $token" -k

echo "allocation"
curl --location 'https://localhost:9200/_cat/allocation?v' --header "Authorization: Basic $token" -k

kill "$pid"
