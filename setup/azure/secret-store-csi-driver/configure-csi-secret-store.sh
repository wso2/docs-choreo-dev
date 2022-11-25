#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
echo "--- Creating namespace csi-secret-store-driver..."
kubectl create namespace csi-secret-store-driver --dry-run=client -o yaml | kubectl apply -f -

echo "--- Installing secret-store-csi-driver..."
helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/csi-secrets-store-provider-azure --version v1.2.0

helm upgrade --install csi-secrets-store-provider-azure csi-secrets-store-provider-azure-1.2.0.tgz \
--namespace csi-secret-store-driver \
--version 1.2.0 \
--set secrets-store-csi-driver.linux.driver.resources.limits.memory=400Mi \
--set secrets-store-csi-driver.linux.driver.resources.requests.memory=200Mi \
--set secrets-store-csi-driver.syncSecret.enabled=true \
--set linux.image.repository=choreocontrolplane.azurecr.io/oss/azure/secrets-store/provider-azure \
--set secrets-store-csi-driver.linux.image.repository=choreocontrolplane.azurecr.io/oss/kubernetes-csi/secrets-store/driver \
--set secrets-store-csi-driver.linux.registrarImage.repository=choreocontrolplane.azurecr.io/oss/kubernetes-csi/csi-node-driver-registrar \
--set secrets-store-csi-driver.linux.livenessProbeImage.repository=choreocontrolplane.azurecr.io/oss/kubernetes-csi/livenessprobe
