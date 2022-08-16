#!/usr/bin/env bash

############### Install Certmanager ####################
kubectl create ns superset

# Login to helm registry
helm registry login choreocontrolplane.azurecr.io --username ${HELM_ACR_USERNAME}

# Pull Helm Chart
helm pull oci://choreocontrolplane.azurecr.io/helm/superset --version 0.7.1

# Install
helm upgrade --install superset superset-0.7.1.tgz \
    --set supersetNode.connections.db_pass=${SUPERSET_DB_PASS}
    --set init.adminUser.password=${SUPERSET_ADMIN_USER_PASS}
    --values superset-values.yaml \
    -n superset

# Configure netpols
kubectl apply -f superset-netpols.yaml
