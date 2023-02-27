#!/usr/bin/env bash

echo "--- Creating secrets for DNS-01 challenge..."
DNS01_CHALLENGE_CLIENT_SECRET=$(az ad app credential reset --id "${DNS01_CHALLENGE_CLIENT_ID}" --append --display-name "${CLUSTER_NAME}" --years 2 | grep password | cut -d ":" -f2 | cut -d '"' -f 2)
kubectl create secret generic "choreo-secret-azuredns-config" --from-literal=client-secret="${DNS01_CHALLENGE_CLIENT_SECRET}" -n cert-manager --dry-run=client -o yaml | kubectl apply -f -
