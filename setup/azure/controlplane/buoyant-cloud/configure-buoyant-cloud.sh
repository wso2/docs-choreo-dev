#!/usr/bin/env bash

echo "---  Installing Buoyant Cloud... "
kubectl create -f ../../buoyant-cloud/buoyant-setup.sh
kubectl create secret generic buoyant-cloud-id -n buoyant-cloud \
  --from-literal=id="${BUOYANT_CLOUD_AGENT_ID}" \
  --from-literal=key="${BUOYANT_CLOUD_AGENT_KEY}" \
  --from-literal=downloadKey="${BUOYANT_CLOUD_AGENT_DOWNLOAD_KEY}" \
  --from-literal=name="${BUOYANT_CLOUD_NAME}"
kubectl label secret buoyant-cloud-id -n buoyant-cloud app.kubernetes.io/part-of=buoyant-cloud
