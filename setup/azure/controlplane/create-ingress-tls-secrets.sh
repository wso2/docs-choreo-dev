#!/usr/bin/env bash

# This script is used to generate TLS secrets required for Ingress Resources
# TODO - Opt to generating these secrets using SecretProviderClasses When the CSI Driver supports syncing to K8S Secrets

# Subscription Ingress Resource TLS Secrets
kubectl create secret tls ${ENV}-choreo-subscriptions-tls \
    --cert ${SUBSCRIPTION_INGRESS_TLS_CERT_FILE_PATH}  \
    --key ${SUBSCRIPTION_INGRESS_TLS_KEY_FILE_PATH} \
    --namespace ${SYSTEM_NAMESPACE}

# Internal Ingress Resource TLS Secrets
kubectl create secret tls ${ENV}-choreo-controlplane-internal-tls \
   --cert ${CONTROLPLANE_INTERNAL_INGRESS_TLS_CERT_FILE_PATH} \
   --key ${CONTROLPLANE_INTERNAL_INGRESS_TLS_KEY_FILE_PATH} \
   --namespace ${SYSTEM_NAMESPACE}
