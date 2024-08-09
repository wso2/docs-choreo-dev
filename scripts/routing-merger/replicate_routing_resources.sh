#!/bin/bash

## Variables ##
routing_cluster_context="<k8s context name for the routing cluster>"
dataplane_cluster_context="<k8s context name for the dataplane cluster>"

## Program Start ##
namespace="prod-choreo-apim"
# set context to routing cluster
kubectl config use-context "$routing_cluster_context"
# obtain ingress resources
kubectl get ing -n "$namespace" -o yaml >ingress.yaml
# separate the ingress resources to individual documents and remove status fields
yq '.items[] | split_doc | del(.status) | del(.metadata.uid) | del(.metadata.resourceVersion) | del(.metadata.creationTimestamp) | del(.metadata.generation)' ingress.yaml >ingress-split.yaml
# extract tls secrets from ingress resources
mapfile -t tls_secrets < <(yq '.items[] | .spec.tls[] | .secretName' ingress.yaml)

# save tls secrets to files
mkdir -p routing-tls-secrets
for tls_secret in "${tls_secrets[@]}"; do
  kubectl get secret -n "$namespace" "$tls_secret" -o yaml >routing-tls-secrets/"$tls_secret".yaml
done

# set context to dataplane cluster
kubectl config use-context "$dataplane_cluster_context"

# apply tls secrets and ingress resources to dataplane cluster
for secret in routing-tls-secrets/*.yaml; do
  kubectl apply -f "$secret"
done
kubectl apply -f ingress-split.yaml
rm -R routing-tls-secrets
