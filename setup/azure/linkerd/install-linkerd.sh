#!/usr/bin/env bash

############### Install Linkerd2
echo "--- Installing linkerd2... "
linkerd install --ha --registry choreocontrolplane.azurecr.io/linkerd --set-string linkerdVersion=stable-2.11.1 | kubectl apply -f -

####  command to upgrade an existing setup
#linkerd upgrade --ha --registry choreocontrolplane.azurecr.io/linkerd --set-string linkerdVersion=stable-2.11.1 | kubectl apply --prune -l linkerd.io/control-plane-ns=linkerd -f -
