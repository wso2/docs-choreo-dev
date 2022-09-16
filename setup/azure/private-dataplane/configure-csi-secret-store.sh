#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
echo "--- Creating namespace csi-secret-store-driver..."
kubectl create namespace csi-secret-store-driver --dry-run=client -o yaml | kubectl apply -f -

helm repo add csi-secrets-store-provider-azure https://azure.github.io/secrets-store-csi-driver-provider-azure/charts
helm repo update
helm upgrade --install csi-secrets-store-provider-azure csi-secrets-store-provider-azure/csi-secrets-store-provider-azure --namespace csi-secret-store-driver --version 1.0.0 --set secrets-store-csi-driver.linux.driver.resources.limits.memory=400Mi --set secrets-store-csi-driver.linux.driver.resources.requests.memory=200Mi

################ Install CSI Secret Store Class Secret ########

KEYVAULT_NAME=$(az keyvault list --resource-group choreo-"${CUSTOMER_NAME}"-key-vault-rg --query "[?contains(name, '${CUSTOMER_NAME}-userapps-${CLUSTER_ENV}')].name" --output tsv)

USERAPPS_CSI_KEY_VAULT_CLIENT_ID=$(az ad app list --all --query "[?contains(displayName, '${KEYVAULT_NAME}')].appId" --output tsv)

USERAPPS_CSI_KEY_VAULT_CLIENT_SECRET=$(az ad app credential reset --id "${USERAPPS_CSI_KEY_VAULT_CLIENT_ID}" --append --display-name "dataplane-${CLUSTER_ENV}" --years 2 | grep password | cut -d ":" -f2 | cut -d '"' -f 2)

kubectl create namespace "${ENV}-choreodp-system"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${USERAPPS_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${USERAPPS_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${ENV}-choreodp-system"

kubectl create namespace "${ENV}-choreo-apim"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${USERAPPS_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${USERAPPS_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${ENV}-choreo-apim"
