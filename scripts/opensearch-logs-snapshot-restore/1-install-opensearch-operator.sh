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

kubectl create ns observability

helm repo add opensearch-operator https://opensearch-project.github.io/opensearch-k8s-operator/

helm repo update

helm install opensearch-operator opensearch-operator/opensearch-operator -n observability --version 2.7.0

# loop until opensearch crds are created
while [[ $(kubectl get crd | grep opensearch | wc -l) -eq 0 ]]; do
  echo "Waiting for opensearch crds to be created..."
  sleep 5
done
echo "opensearch crds created"

# loop until opensearch operator is running and both containers are ready
while [[ $(kubectl get pods -n observability | grep opensearch-operator | grep 2/2 | wc -l) -eq 0 ]]; do
  echo "Waiting for opensearch operator to be ready..."
  sleep 5
done
echo "opensearch operator is ready"
