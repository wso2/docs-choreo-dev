#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
echo "--- Creating namespace csi-secret-store-driver..."
kubectl create namespace csi-secret-store-driver --dry-run=client -o yaml | kubectl apply -f -

helm repo add csi-secrets-store-provider-azure https://raw.githubusercontent.com/Azure/secrets-store-csi-driver-provider-azure/master/charts
helm repo update
helm upgrade --install csi-secrets-store-provider-azure csi-secrets-store-provider-azure/csi-secrets-store-provider-azure --namespace csi-secret-store-driver --version 0.0.16 --set secrets-store-csi-driver.linux.driver.resources.limits.memory=400Mi --set secrets-store-csi-driver.linux.driver.resources.requests.memory=200Mi

################ Install CSI Secret Store Class Secret ########
kubectl create namespace "${SYSTEM_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${SYSTEM_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${SYSTEM_NAMESPACE}"

kubectl create namespace "${APIM_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${APIM_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${APIM_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${APIM_NAMESPACE}"

kubectl create namespace "${IDP_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${IDP_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${IDP_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${IDP_NAMESPACE}"
