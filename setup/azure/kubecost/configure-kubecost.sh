#!/usr/bin/env bash

################ Install Kubecost ########
echo "--- Creating namespace kubecost..."
kubectl create namespace kubecost --dry-run=client -o yaml | kubectl apply -f -

echo "--- Installing kubecost..."
helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}" --password "${HELM_ACR_PASSWORD}"
helm pull oci://choreocontrolplane.azurecr.io/helm/cost-analyzer --version 1.98.0

helm upgrade --install kubecost cost-analyzer-1.98.0.tgz \
 --namespace kubecost \
 --set ingress.enabled=true \
 --set ingress.className="${KUBECOST_INGRESS_CLASS_NAME}" \
 --set ingress.hosts[0]="${KUBECOST_INGRESS_HOST_NAME}"\
 --set ingress.annotations."nginx\.ingress\.kubernetes\.io/auth-realm=Authentication Required - kubecost" \
 --set ingress.annotations."nginx\.ingress\.kubernetes\.io/auth-secret=basic-auth" \
 --set ingress.annotations."nginx\.ingress\.kubernetes\.io/auth-type=basic" \
 --set kubecostModel.image="choreocontrolplane.azurecr.io/kubecost1/cost-model" \
 --set kubecostFrontend.image="choreocontrolplane.azurecr.io/kubecost1/frontend" \
 --set grafana.sidecar.image="choreocontrolplane.azurecr.io/kiwigrid/k8s-sidecar:1.19.2" \
 --set grafana.image.repository="choreocontrolplane.azurecr.io/grafana/grafana" \
 --set prometheus.kube-state-metrics.image.repository="choreocontrolplane.azurecr.io/coreos/kube-state-metrics" \
 --set prometheus.nodeExporter.image.repository="choreocontrolplane.azurecr.io/prom/node-exporter" \
 --set prometheus.server.image.repository="choreocontrolplane.azurecr.io/prometheus/prometheus" \
 --set prometheus.configmapReload.prometheus.image.repository="choreocontrolplane.azurecr.io/jimmidyson/configmap-reload"
