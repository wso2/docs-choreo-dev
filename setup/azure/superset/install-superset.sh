#!/usr/bin/env bash
# -------------------------------------------------------------------------------------
#
# Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

# Deploy Apache Superset Kubernetes workload

SUPERSET_DB_PASS=$(az keyvault secret show --name postgresql-SUPERSET-DB-PASSWORD --vault-name "${CSI-KEYVAULT-NAME}" --query value -o tsv)
SUPERSET_ADMIN_USER_PASS=$(az keyvault secret show --name superset-ADMIN-PASSWORD --vault-name "${CSI-KEYVAULT-NAME}" --query value -o tsv)

kubectl create ns superset

# Login to helm registry
helm registry login choreocontrolplane.azurecr.io --username "${HELM_ACR_USERNAME}"

# Pull Helm Chart
helm pull oci://choreocontrolplane.azurecr.io/helm/superset --version 0.7.1

# Deploy customized configuration files + dependent service secrets via a Kubernetes Secret
kubectl apply -f superset-custom-config-secret.yaml

# Install
helm upgrade --install superset superset-0.7.1.tgz \
    --set init.adminUser.password="${SUPERSET_ADMIN_USER_PASS}" \
    --values superset-values.yaml \
    -n superset

# Deploy network policies for Superset deployment
kubectl apply -f superset-netpols.yaml
