#!/usr/bin/env bash

################ Install emberstack reflector ########
kubectl create ns cert-manager

helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"

helm pull oci://choreocontrolplane.azurecr.io/helm/reflector --version 6.1.47

helm upgrade --install \
   reflector emberstack/reflector \
   --namespace cert-manager \
   --version 6.1.47 \
   --set image.repository="choreocontrolplane.azurecr.io/emberstack/kubernetes-reflector"
