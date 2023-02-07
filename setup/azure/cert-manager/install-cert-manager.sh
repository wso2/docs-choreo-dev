#!/usr/bin/env bash

############### Install Certmanager ####################
kubectl create ns cert-manager
kubectl label namespace cert-manager cert-manager.io/disable-validation=true

helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/cert-manager --version v1.8.0

helm upgrade --install \
  cert-manager cert-manager-v1.8.0.tgz \
  --namespace cert-manager \
  --version v1.8.0 \
  --set installCRDs=true \
  --set image.repository="choreocontrolplane.azurecr.io/jetstack/cert-manager-controller" \
  --set replicaCount=2 \
  --set webhook.image.repository="choreocontrolplane.azurecr.io/jetstack/cert-manager-webhook" \
  --set webhook.replicaCount=2 \
  --set cainjector.image.repository="choreocontrolplane.azurecr.io/jetstack/cert-manager-cainjector" \
  --set cainjector.replicaCount=2
