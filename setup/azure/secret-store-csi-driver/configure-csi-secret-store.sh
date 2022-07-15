#!/usr/bin/env bash

################ Install CSI Secret Store Driver ########
echo "--- Creating namespace csi-secret-store-driver..."
kubectl create namespace csi-secret-store-driver --dry-run=client -o yaml | kubectl apply -f -

echo "--- Installing secret-store-csi-driver..."
helm repo add csi-secrets-store-provider-azure https://raw.githubusercontent.com/Azure/secrets-store-csi-driver-provider-azure/master/charts
helm repo update
helm upgrade --install csi-secrets-store-provider-azure csi-secrets-store-provider-azure/csi-secrets-store-provider-azure --namespace csi-secret-store-driver --version 1.0.0 --set secrets-store-csi-driver.linux.driver.resources.limits.memory=400Mi --set secrets-store-csi-driver.linux.driver.resources.requests.memory=200Mi --set secrets-store-csi-driver.syncSecret.enabled=true
