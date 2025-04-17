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

if [ -z "$1" ]; then
    echo "Please provide snapshot name as argument"
    exit 1
fi

SNAPSHOT_NAME="$1"

kubectl port-forward svc/opensearch -n observability 9200 &
pid=$(echo $!)
sleep 5
password=$(kubectl get secret opensearch-admin-credentials-secret -o yaml -n observability | yq eval '.data["password"]' | base64 -d)

token=$(echo -n "admin:$password" | base64)

echo "Restore snapshot"
curl --request POST \
     --location "https://localhost:9200/_snapshot/container-logs-automatic-snapshots/$SNAPSHOT_NAME/_restore" \
     --header 'Content-Type: application/json' \
     --header "Authorization: Basic $token" \
     -k


echo -e "\nIndices after restore"
curl --location 'https://localhost:9200/_cat/indices?v=true&expand_wildcards=all' \
     --header "Authorization: Basic $token" \
     -k

echo "Recovery status"
for i in {0..20}; do
    echo -e "health"
    curl --location 'https://localhost:9200/_cluster/health?pretty=true' \
         --header "Authorization: Basic $token" \
         -k
    echo -e "\nPending recoveries"
    curl --location 'https://localhost:9200/_cat/recovery?active_only=true&v=true' \
         --header "Authorization: Basic $token" \
         -k
    sleep 60
done

kill $pid
