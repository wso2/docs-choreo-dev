#!/usr/bin/env bash

echo "--- Setting up Nginx Ingress Controllers ---"
bash idp-ingress-controller/configure-ingress-controller.sh
bash internal-ingress-controller-for-analytics/configure-ingress-controller.sh
bash internal-ingress-controller-for-bcentral/configure-ingress-controller.sh
bash internal-ingress-controller-for-dp/configure-ingress-controller.sh
bash system-ingress-controller/configure-ingress-controller.sh
