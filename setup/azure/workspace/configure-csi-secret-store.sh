#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
echo "--- Creating namespace csi-secret-store-driver..."
kubectl create namespace csi-secret-store-driver --dry-run=client -o yaml | kubectl apply -f -

helm repo add csi-secrets-store-provider-azure https://raw.githubusercontent.com/Azure/secrets-store-csi-driver-provider-azure/master/charts
helm repo update
helm upgrade --install csi-secrets-store-provider-azure csi-secrets-store-provider-azure/csi-secrets-store-provider-azure --namespace csi-secret-store-driver --version 0.0.16

################ Install CSI Secret Store Class Secret ########
kubectl create namespace "${DP_SYSTEM_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${SYSTEM_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${DP_SYSTEM_NAMESPACE}"
