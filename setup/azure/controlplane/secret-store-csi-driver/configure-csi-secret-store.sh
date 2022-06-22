#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
bash ../../secret-store-csi-driver/configure-csi-secret-store.sh

################ Install CSI Secret Store Class Secret ########
SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET=$(az ad app credential reset --id "${SYSTEM_CSI_KEY_VAULT_CLIENT_ID}" --append --credential-description "${CLUSTER_NAME}" --years 2 | grep password | cut -d ":" -f2 | cut -d '"' -f 2)

kubectl create namespace "${SYSTEM_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${SYSTEM_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${SYSTEM_NAMESPACE}"

APIM_CSI_KEY_VAULT_CLIENT_SECRET=$(az ad app credential reset --id "${APIM_CSI_KEY_VAULT_CLIENT_ID}" --append --credential-description "${CLUSTER_NAME}" --years 2 | grep password | cut -d ":" -f2 | cut -d '"' -f 2)

kubectl create namespace "${APIM_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${APIM_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${APIM_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${APIM_NAMESPACE}"

IDP_CSI_KEY_VAULT_CLIENT_SECRET=$(az ad app credential reset --id "${IDP_CSI_KEY_VAULT_CLIENT_ID}" --append --credential-description "${CLUSTER_NAME}" --years 2 | grep password | cut -d ":" -f2 | cut -d '"' -f 2)

kubectl create namespace "${IDP_NAMESPACE}"
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${IDP_CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${IDP_CSI_KEY_VAULT_CLIENT_SECRET}" -n "${IDP_NAMESPACE}"
