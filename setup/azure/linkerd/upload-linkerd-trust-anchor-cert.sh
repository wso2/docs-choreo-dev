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
  name: linkerd-trust-anchor
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
  parameters:
    keyvaultName: "$keyvault_name"
    objects: |
      array:
        - |
          objectName: linkerd-trust-anchor
          objectType: secret
          objectVersion: "$object_version"
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

step certificate create root.linkerd.cluster.local ca.crt ca.key \
  --profile root-ca --no-password --insecure

openssl pkcs12 -export -out linkerd-trust-anchor.pfx -inkey ca.key -in ca.crt -passout pass:""
output=$(az keyvault certificate import \
    --file linkerd-trust-anchor.pfx --name linkerd-trust-anchor --vault-name "$keyvault_name" --password "")

id=$(echo "$output" | jq '.id' | tr -d '"')
object_version=${id##*/}
echo "[INFO] Successfully uploaded the trust anchor certificate to Azure Keyvault"
echo "[INFO] Use the following sample SPC to configure the SecretProviderClass for Linkerd trust anchor certificate:"
generate_sample_spc

rm -f ca.crt ca.key linkerd-trust-anchor.pfx
