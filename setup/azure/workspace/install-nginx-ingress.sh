#!/usr/bin/env bash

############### Install Workspace Nginx Ingress Controller using Helm 3
echo "--- Setting up Workspace Nginx Ingress Controller.."
echo "--- Creating namespace ${WORKSPACE_INGRESS_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

#kubectl annotate namespace "${WORKSPACE_INGRESS_NAMESPACE}" linkerd.io/inject=enabled
#kubectl annotate namespace "${WORKSPACE_INGRESS_NAMESPACE}" config.linkerd.io/skip-inbound-ports=443

echo "--- Installing Control Plane Internal Nginx Ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install "${WORKSPACE_INGRESS_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${WORKSPACE_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${WORKSPACE_INGRESS_NAMESPACE}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group"="${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal"="true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet"="${LOADBALANCER_SUBNET}"