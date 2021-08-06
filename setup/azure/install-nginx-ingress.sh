#!/usr/bin/env bash

############### Install System Nginx Ingress Controller using Helm 3
echo "--- Creating namespace ${SYSTEM_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${SYSTEM_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${SYSTEM_NAMESPACE}-nginx-ingress" purpose="${SYSTEM_NAMESPACE}-ingress-traffic"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${SYSTEM_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${SYSTEM_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${SYSTEM_NAMESPACE}-nginx-ingress-ns.yaml"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

echo "--- Installing nginx ingress using Helm 3..."
helm upgrade --install "${SYSTEM_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${SYSTEM_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${SYSTEM_LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${SYSTEM_INGRESS_CLASS}" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group = ${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal = true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet = ${LOADBALANCER_SUBNET}"

############### Install Userapps Nginx Ingress Controller using Helm 3
echo "--- Creating namespace ${USERAPPS_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${USERAPPS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

kubectl annotate namespace "${USERAPPS_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${USERAPPS_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

echo "--- Installing nginx ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install "${USERAPPS_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${USERAPPS_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${USERAPPS_LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${USERAPPS_INGRESS_CLASS}" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group = ${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal = true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet = ${LOADBALANCER_SUBNET}"

############### Install IDP Nginx Ingress Controller using Helm 3
echo "--- Creating namespace ${IDP_NAMESPACE}"
kubectl create namespace "${IDP_NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f -

kubectl annotate namespace "${IDP_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${IDP_NAMESPACE}" config.linkerd.io/skip-inbound-ports=443

echo "--- Installing nginx ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install "${IDP_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${IDP_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${IDP_LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${IDP_INGRESS_CLASS}" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group"="${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal"="true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet"="${LOADBALANCER_SUBNET}"