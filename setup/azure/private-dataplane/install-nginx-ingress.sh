#!/usr/bin/env bash

echo "--- Setting up Routing Nginx Ingress Controller.."
echo "--- Creating namespace ${ENV}-choreo-apim-nginx-ingress..."
kubectl create namespace "${ENV}-choreo-apim-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${ENV}-choreo-apim-nginx-ingress" purpose="${ENV}-choreo-apim-ingress-traffic"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${ENV}-choreo-apim-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${ENV}-choreo-apim-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${ENV}-choreo-apim-nginx-ingress-ns.yaml"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm upgrade --install "${ENV}-choreo-apim" ingress-nginx/ingress-nginx \
  --namespace "${ENV}-choreo-apim-nginx-ingress" \
  --set controller.replicaCount=1 \
  --set controller.service.loadBalancerIP="${ROUTING_LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${ENV}-choreo-apim-nginx" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}"

