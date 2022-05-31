#!/usr/bin/env bash

############### Install Linkerd2
echo "--- Installing linkerd2... "
linkerd install --ha | kubectl apply -f -
