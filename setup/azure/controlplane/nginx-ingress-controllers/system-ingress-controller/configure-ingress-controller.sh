#!/usr/bin/env bash

############################################################
### Install System Nginx Ingress Controller using Helm 3 ###
############################################################
echo "--- Setting up System Nginx Ingress Controller.."
echo "--- Creating namespace ${SYSTEM_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${SYSTEM_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${SYSTEM_NAMESPACE}-nginx-ingress" purpose="${SYSTEM_NAMESPACE}-ingress-traffic"

# Add LUA configuration script
kubectl create configmap "lua-log4j-migitaion-script-config-map" --from-file=../../../lua-scripts/log4j-mitigation.conf -n "${SYSTEM_NAMESPACE}-nginx-ingress"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${SYSTEM_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${SYSTEM_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ../../../netpol/"${SYSTEM_NAMESPACE}-nginx-ingress-ns.yaml"

helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/ingress-nginx --version 4.7.0

echo "--- Installing System Nginx Ingress using Helm 3..."
helm upgrade --install "${SYSTEM_NAMESPACE}" ingress-nginx-4.7.0.tgz \
  --namespace "${SYSTEM_NAMESPACE}-nginx-ingress" \
  --version 4.7.0 \
  --set controller.replicaCount=2 \
  --set controller.minAvailable=1 \
  --set controller.autoscaling.enabled=true \
  --set controller.autoscaling.maxReplicas=4 \
  --set controller.autoscaling.minReplicas=3 \
  --set controller.autoscaling.targetCPUUtilizationPercentage=75 \
  --set controller.autoscaling.targetMemoryUtilizationPercentage=75 \
  --set controller.service.loadBalancerIP="${SYSTEM_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${SYSTEM_NAMESPACE}-nginx" \
  --set controller.ingressClassResource.controllerValue="k8s.io/${SYSTEM_NAMESPACE}-nginx" \
  --set controller.ingressClassResource.enabled="true" \
  --set controller.ingressClassResource.name="${IDP_NAMESPACE}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v1.8.0" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET}"
