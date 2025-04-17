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

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Please provide component ID and namespace name as arguments"
    echo "Usage: $0 <component_id> <namespace>"
    exit 1
fi

COMPONENT_ID="$1"
NAMESPACE="$2"

kubectl port-forward svc/opensearch -n observability 9200 &
pid=$(echo $!)
sleep 5
password=$(kubectl get secret opensearch-admin-credentials-secret -o yaml -n observability | yq eval '.data["password"]' | base64 -d)

token=$(echo -n "admin:$password" | base64)

logs_first_page=$(curl --location 'https://localhost:9200/_search' --header "Authorization: Basic $token" -k --header 'Content-Type: application/json' --data-raw '{
    "query": {
        "bool": {
            "must": [
                {
                    "match": {
                        "_index": "container-logs-*"
                    }
                },
                {
                    "match": {
                        "kubernetes.namespace_name": "'$NAMESPACE'"
                    }
                },
                {
                    "match": {
                        "kubernetes.labels.component_id": "'$COMPONENT_ID'"
                    }
                }
            ]
        }
    },
    "_source": [
        "log"
    ],
    "sort": [
        { "@timestamp": "asc" }
    ],
    "size": 5000
}')
# Adjust the size according to approximate number of logs to be fetched

hits_count=$(echo $logs_first_page | jq -r '.hits.total.value')
echo "hits_count: $hits_count"
hits=$(echo $logs_first_page | jq -r '.hits.hits[]')

for hit in $hits; do
    # Append the hit to output file
    echo "$hit" >> component_${COMPONENT_ID}_logs.json
done

last_hit_sort_value=$(echo $logs_first_page | jq -r '.hits.hits[-1].sort[0]')
previous_last_hit_sort_value=555 # Initialize with a dummy value
echo "last_hit_sort_value: $last_hit_sort_value"

# recall while the last hit sort value does not change
while [ $last_hit_sort_value -ne $previous_last_hit_sort_value ]; do
    previous_last_hit_sort_value=$last_hit_sort_value
    logs_next_page=$(curl --location 'https://localhost:9200/_search' --header "Authorization: Basic $token" -k --header 'Content-Type: application/json' --data-raw '{
        "query": {
            "bool": {
                "must": [
                    {
                        "match": {
                            "_index": "container-logs-*"
                        }
                    },
                    {
                        "match": {
                            "kubernetes.namespace_name": "'$NAMESPACE'"
                        }
                    },
                    {
                        "match": {
                            "kubernetes.labels.component_id": "'$COMPONENT_ID'"
                        }
                    }
                ]
            }
        },
        "_source": [
            "log"
        ],
        "search_after": [
            '"$last_hit_sort_value"'
        ],
        "sort": [
            { "@timestamp": "asc" }
        ],
        "size": 5000
    }')

    hits=$(echo $logs_next_page | jq -r '.hits.hits[]')

    for hit in $hits; do
        # Append the hit to output file
        echo "$hit" >> component_${COMPONENT_ID}_logs.json
    done

    last_hit_sort_value=$(echo $logs_next_page | jq -r '.hits.hits[-1].sort[0]')
    echo "last_hit_sort_value: $last_hit_sort_value"
done

echo "end of fetching logs"
kill $pid
