#!/usr/bin/env bash

############### Install System Nginx Ingress Controller using Helm 3
echo "--- Setting up System Nginx Ingress Controller.."
echo "--- Creating namespace ${SYSTEM_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${SYSTEM_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${SYSTEM_NAMESPACE}-nginx-ingress" purpose="${SYSTEM_NAMESPACE}-ingress-traffic"

# Add LUA configuration script
kubectl create configmap "lua-log4j-migitaion-script-config-map" --from-file=../lua-scripts/log4j-mitigation.conf -n "${SYSTEM_NAMESPACE}-nginx-ingress"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${SYSTEM_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${SYSTEM_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${SYSTEM_NAMESPACE}-nginx-ingress-ns.yaml"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

echo "--- Installing System Nginx Ingress using Helm 3..."
helm upgrade --install "${SYSTEM_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${SYSTEM_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${SYSTEM_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${SYSTEM_NAMESPACE}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}" \
  --set controller.extraVolumeMounts[0].name="log4j-lua-conf-script-volume-mount" \
  --set controller.extraVolumeMounts[0].mountPath="/var/lib/lua-charts" \
  --set controller.extraVolumes[0].name="log4j-lua-conf-script-volume-mount" \
  --set controller.extraVolumes[0].configMap.name="lua-log4j-migitaion-script-config-map"

################ Install Userapps Nginx Ingress Controller using Helm 3
#echo "--- Setting up Userapps Nginx Ingress Controller.."
#echo "--- Creating namespace ${USERAPPS_NAMESPACE}-nginx-ingress..."
#kubectl create namespace "${USERAPPS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -
#
#kubectl annotate namespace "${USERAPPS_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
#kubectl annotate namespace "${USERAPPS_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443
#
#echo "--- Installing Userapps Nginx Ingress using Helm 3..."
## shellcheck disable=SC2140
#helm upgrade --install "${USERAPPS_NAMESPACE}" ingress-nginx/ingress-nginx \
#  --namespace "${USERAPPS_NAMESPACE}-nginx-ingress" \
#  --version 3.8.0 \
#  --set controller.replicaCount=2 \
#  --set controller.service.loadBalancerIP="${USERAPPS_LOADBALANCER_IP}" \
#  --set rbac.create=true \
#  --set controller.service.externalTrafficPolicy=Local \
#  --set controller.resources.requests."memory"=500Mi \
#  --set controller.resources.requests."cpu"=500m \
#  --set controller.resources.limits."cpu"=1000m \
#  --set controller.resources.limits."memory"=1Gi \
#  --set controller.ingressClass="${USERAPPS_NAMESPACE}-nginx" \
#  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
#  --set controller.image.tag="v0.41.2" \
#  --set controller.image.digest=null \
#  --set-string controller.config.server-tokens=false \
#  --set controller.admissionWebhooks.enabled=false \
#  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
#  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
#  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}"

############### Install Control Plane Internal Nginx Ingress Controller for Data Plane connection using Helm 3
echo "--- Setting up Internal Nginx Ingress Controller.."
echo "--- Creating namespace ${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

kubectl label namespace "${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress" choreo-ingress-purpose="controlplane-internal"

kubectl create configmap "lua-log4j-migitaion-script-config-map" --from-file=../lua-scripts/log4j-mitigation.conf -n "${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress"

kubectl annotate namespace "${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress-ns.yaml"

echo "--- Installing Control Plane Internal Nginx Ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install "${INTERNAL_INGRESS_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${INTERNAL_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${INTERNAL_INGRESS_NAMESPACE}-nginx-ingress" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}" \
  --set controller.extraVolumeMounts[0].name="log4j-lua-conf-script-volume-mount" \
  --set controller.extraVolumeMounts[0].mountPath="/var/lib/lua-charts" \
  --set controller.extraVolumes[0].name="log4j-lua-conf-script-volume-mount" \
  --set controller.extraVolumes[0].configMap.name="lua-log4j-migitaion-script-config-map"
###########

echo "--- Setting up Internal Nginx Ingress Controller for Exposing Choreo Control Plane internal services"
echo "--- Creating namespace ${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

kubectl label namespace "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" choreo-ingress-purpose="choreo-cp-internal"

kubectl create configmap "lua-log4j-migitaion-script-config-map" --from-file=../lua-scripts/log4j-mitigation.conf -n "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress"

kubectl annotate namespace "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress-ns.yaml"

echo "--- Installing Control Plane Internal Nginx Ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}" ingress-nginx/ingress-nginx \
  --namespace "${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}" \
  --set controller.extraVolumeMounts[0].name="log4j-lua-conf-script-volume-mount" \
  --set controller.extraVolumeMounts[0].mountPath="/var/lib/lua-charts" \
  --set controller.extraVolumes[0].name="log4j-lua-conf-script-volume-mount" \
  --set controller.extraVolumes[0].configMap.name="lua-log4j-migitaion-script-config-map"
