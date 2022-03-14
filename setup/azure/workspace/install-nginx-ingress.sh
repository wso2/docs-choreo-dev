#!/usr/bin/env bash

############### Install Workspace Nginx Plus Ingress Controller using Helm 3
echo "--- Setting up Workspace Nginx Ingress Controller.."
echo "--- Creating namespace ${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus-ingress..."
kubectl create namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus-ingress" purpose="${WORKSPACE_INGRESS_NAMESPACE}-ingress-traffic"

#kubectl annotate namespace "${WORKSPACE_INGRESS_NAMESPACE}" linkerd.io/inject=enabled
#kubectl annotate namespace "${WORKSPACE_INGRESS_NAMESPACE}" config.linkerd.io/skip-inbound-ports=443

echo "--- Installing Workspace Nginx Ingress using Helm 3..."
# shellcheck disable=SC2140

helm upgrade --install "${WORKSPACE_INGRESS_NAMESPACE}" nginx-stable/nginx-ingress \
  --namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus-ingress" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/nginx-plus-ingress" \
  --set controller.replicaCount=1 \
  --set controller.image.tag="2.0.3-SNAPSHOT-66e69fe" \
  --set controller.nginxplus=true \
  --set controller.service.loadBalancerIP="${WORKSPACE_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${WORKSPACE_INGRESS_NAMESPACE}-nginx" \
  --set controller.enableSnippets=true \
  --set-string controller.config.server-tokens=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=\"true\"" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}"
