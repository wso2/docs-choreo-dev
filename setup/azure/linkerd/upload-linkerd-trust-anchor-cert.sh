#!/bin/bash

set -euo pipefail

usage() {
  cat <<EOF
Usage: $0 --keyvault-name <name of the Azure keyvault>

Options:
  --keyvault-name       Azure keyvault name
  -h, --help            Show this help message
EOF
}

generate_sample_spc() {
  cat <<EOF
apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: linkerd-tls
spec:
  provider: azure
  secretObjects:
    - secretName: linkerd-trust-anchor
      type: kubernetes.io/tls
      data:
        - objectName: linkerd-trust-anchor
          key: tls.key
        - objectName: linkerd-trust-anchor
          key: tls.crt
    - secretName: webhook-issuer-tls
      type: kubernetes.io/tls
      data:
        - objectName: linkerd-webhook
          key: tls.key
        - objectName: linkerd-webhook
          key: tls.crt
  parameters:
    keyvaultName: "$keyvault_name"
    objects: |
      array:
        - |
          objectName: linkerd-trust-anchor
          objectType: secret
          objectVersion: "$trust_anchor_object_version"
        - |
          objectName: linkerd-webhook
          objectType: secret
          objectVersion: "$webhook_object_version"
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
  --keyvault-name)
    if [[ -n "$2" ]] && [[ "$2" != "--"* ]]; then
      keyvault_name="$2"
      shift 2
    else
      echo "[ERROR] Argument for $1 is missing" >&2
      usage
      exit 1
    fi
    ;;
  -h | --help)
    usage
    exit 0
    ;;
  *)
    echo "[ERROR] Invalid option: $1" >&2
    usage
    exit 1
    ;;
  esac
done

if [[ -z "$keyvault_name" ]]; then
  echo "[ERROR] --keyvault-name is required" >&2
  usage
  exit 1
fi

mkdir -p linkerd-trust-anchor
mkdir -p linkerd-webhook

step certificate create root.linkerd.cluster.local linkerd-trust-anchor/ca.crt linkerd-trust-anchor/ca.key \
  --profile root-ca --no-password --insecure

step certificate create webhook.linkerd.cluster.local linkerd-webhook/ca.crt linkerd-webhook/ca.key \
  --profile root-ca --no-password --insecure --san webhook.linkerd.cluster.local

openssl pkcs12 -export -out linkerd-trust-anchor/linkerd-trust-anchor.pfx \
  -inkey linkerd-trust-anchor/ca.key -in linkerd-trust-anchor/ca.crt -passout pass:""
openssl pkcs12 -export -out linkerd-webhook/linkerd-webhook.pfx \
  -inkey linkerd-webhook/ca.key -in linkerd-webhook/ca.crt -passout pass:""

trust_anchor_output=$(az keyvault certificate import \
    --file linkerd-trust-anchor/linkerd-trust-anchor.pfx --name linkerd-trust-anchor --vault-name "$keyvault_name" --password "")
webhook_output=$(az keyvault certificate import \
    --file linkerd-webhook/linkerd-webhook.pfx --name linkerd-webhook --vault-name "$keyvault_name" --password "")

trust_anchor_id=$(echo "$trust_anchor_output" | jq '.id' | tr -d '"')
webhook_id=$(echo "$webhook_output" | jq '.id' | tr -d '"')

trust_anchor_object_version=${trust_anchor_id##*/}
webhook_object_version=${webhook_id##*/}
echo "[INFO] Successfully uploaded the trust anchor and webhook certificate to Azure Keyvault"
echo "[INFO] Use the following sample SPC to configure the SecretProviderClass for Linkerd trust anchor and webhook certs"
generate_sample_spc

rm -R linkerd-trust-anchor
rm -R linkerd-webhook
