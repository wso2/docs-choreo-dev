#!/usr/bin/env bash

# This script is used to generate TLS secrets required for Ingress Resources
# TODO - Opt to generating these secrets using SecretProviderClasses When the CSI Driver supports syncing to K8S Secrets

# Subscription Ingress Resource TLS Secrets
kubectl create ns "${SYSTEM_NAMESPACE}"
kubectl create secret tls "${ENV}-choreo-subscriptions-tls" \
    --cert "${SUBSCRIPTION_INGRESS_TLS_CERT_FILE_PATH}"  \
    --key "${SUBSCRIPTION_INGRESS_TLS_KEY_FILE_PATH}" \
    --namespace "${SYSTEM_NAMESPACE}"

# Internal Ingress Resource TLS Secrets
kubectl create secret tls "${ENV}-choreo-controlplane-system-internal-tls" \
   --cert "${CONTROLPLANE_SYSTEM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH}" \
   --key "${CONTROLPLANE_SYSTEM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH}" \
   --namespace "${SYSTEM_NAMESPACE}"

kubectl create ns "${APIM_NAMESPACE}"
kubectl create secret tls "${ENV}-choreo-controlplane-apim-internal-tls" \
   --cert "${CONTROLPLANE_APIM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH}" \
   --key "${CONTROLPLANE_APIM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH}" \
   --namespace "${APIM_NAMESPACE}"

kubectl create secret tls "${ENV}"-bcentral-apim-internal-tls \
   --cert "${BCENTRAL_APIM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH}" \
   --key "${BCENTRAL_APIM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH}" \
   --namespace "${APIM_NAMESPACE}"
