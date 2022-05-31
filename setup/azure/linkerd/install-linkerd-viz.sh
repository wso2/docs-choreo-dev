#!/usr/bin/env bash

############### Install Linkerd2
echo "---  Installing Linkerd Viz... "
linkerd viz install | kubectl apply -f -