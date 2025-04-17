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

helm install opensearch-cluster ./opensearch-cluster -n observability --version 0.1.0

# loop until opensearch cluster health is green
while [[ $(kubectl get opensearchcluster opensearch -n observability -o jsonpath='{.status.health}') != "green" ]]; do
  echo "Waiting for opensearch cluster to be healthy..."
  sleep 5
done

# loop until all opensearch pods are running
while [[ $(kubectl get pods -n observability | grep opensearch-master | grep 1/1 | wc -l) -ne 3 ]]; do
  echo "Waiting for opensearch master pods to be ready..."
  sleep 5
done

echo "opensearch cluster is ready"
