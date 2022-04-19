#!/usr/bin/env bash

echo "--- Setting up Routing Nginx Ingress Controller.."
echo "--- Creating namespace ${APIM_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${APIM_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${APIM_NAMESPACE}-nginx-ingress" purpose="${APIM_NAMESPACE}-ingress-traffic"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${APIM_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${APIM_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${APIM_NAMESPACE}-nginx-ingress-ns.yaml"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm upgrade --install "${APIM_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${APIM_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=1 \
  --set controller.service.loadBalancerIP="${ROUTING_LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${APIM_NAMESPACE}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}"

