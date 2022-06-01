#!/usr/bin/env bash

############## Install Reloader
echo "--- Installing Reloader..."
kubectl create ns reloader
kubectl apply -n reloader -f ../../reloader/reloader.yaml
