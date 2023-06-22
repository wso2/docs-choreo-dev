#!/usr/bin/env bash

#########################################################
### Install IDP Nginx Ingress Controller using Helm 3 ###
#########################################################
echo "--- Setting up Webapps NGINX Ingress Controller.."
echo "--- Creating namespace ${WEBAPPS_NAMESPACE}-nginx-ingress..."
kubectl create namespace "${WEBAPPS_NAMESPACE}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add labelconfigure-ingress-controller to Nginx ingress namespace
kubectl label namespace "${WEBAPPS_NAMESPACE}-nginx-ingress" purpose="${WEBAPPS_NAMESPACE}-ingress-traffic"
kubectl label namespace "${WEBAPPS_NAMESPACE}-nginx-ingress" userapp-ingress-allowed="true"

helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/ingress-nginx --version 4.2.1

echo "--- Installing WebApps Nginx Ingress using Helm 3..."
helm upgrade --install "${WEBAPPS_NAMESPACE}" ingress-nginx-4.2.1.tgz \
  --namespace "${WEBAPPS_NAMESPACE}-nginx-ingress" \
  --version 4.2.1 \
  --set controller.replicaCount=2 \
  --set controller.minAvailable=1 \
  --set controller.autoscaling.enabled=true \
  --set controller.autoscaling.maxReplicas=4 \
  --set controller.autoscaling.minReplicas=3 \
  --set controller.autoscaling.targetCPUUtilizationPercentage=75 \
  --set controller.autoscaling.targetMemoryUtilizationPercentage=75 \
  --set controller.service.loadBalancerIP="${WEBAPPS_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${WEBAPPS_NAMESPACE}-nginx" \
  --set controller.ingressClassResource.controllerValue="k8s.io/${WEBAPPS_NAMESPACE}-nginx" \
  --set controller.ingressClassResource.enabled="true" \
  --set controller.ingressClassResource.name="${WEBAPPS_NAMESPACE}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v1.3.0" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET}"

## Apply netpols
kubectl apply -f
kubectl apply -f ../../../netpol/"${WEBAPPS_NAMESPACE}-nginx-ingress-ns.yaml"
