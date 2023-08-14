#!/usr/bin/env bash

#############################################################################################
### Install BCENTRAL Internal Nginx Ingress Controller for BCENTRAL connection using Helm 3 ###
###############################################################################################

echo "--- Setting up BCentral Internal Nginx Ingress Controller for Exposing Choreo Control Plane internal services to BCentral"
echo "--- Creating namespace ${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

kubectl label namespace "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" choreo-ingress-purpose="-bcentral-choreo-cp-internal"

kubectl create configmap "lua-log4j-migitaion-script-config-map" --from-file=../../../lua-scripts/log4j-mitigation.conf -n "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress"

kubectl annotate namespace "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ../../../netpol/"${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress-ns.yaml"

helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/ingress-nginx --version 4.7.0

echo "--- Installing Control Plane Internal Nginx Ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}" ingress-nginx-4.7.0.tgz \
  --namespace "${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" \
  --version 4.7.0 \
  --set controller.replicaCount=2 \
  --set controller.minAvailable=1 \
  --set controller.autoscaling.enabled=true \
  --set controller.autoscaling.maxReplicas=4 \
  --set controller.autoscaling.minReplicas=3 \
  --set controller.autoscaling.targetCPUUtilizationPercentage=75 \
  --set controller.autoscaling.targetMemoryUtilizationPercentage=75 \
  --set controller.service.loadBalancerIP="${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx-ingress" \
  --set controller.ingressClassResource.controllerValue="k8s.io/${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx" \
  --set controller.ingressClassResource.enabled="true" \
  --set controller.ingressClassResource.name="${BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v1.8.0" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET}"
