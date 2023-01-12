#!/usr/bin/env bash

############## Install Reloader
echo "--- Add OMS Agent Config"
kubectl apply -f oms/container-azm-ms-agentconfig.yaml
