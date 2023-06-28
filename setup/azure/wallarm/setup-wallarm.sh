#!/usr/bin/env bash
# -------------------------------------------------------------------------------------
#
# Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

# Setup Wallarm-NGINX Ingress Controller installation

echo "----------------------------------------"
echo "| Wallarm Ingress Controller Deployment |"
echo "----------------------------------------"

# Functions

function print_usage {
    echo -e "Usage: $0 [options]\n";
    echo -e "Options:\n"
    echo "--environment           - Choreo environment (supported values: dev, stg or prod)";
    echo "--container-registry    - Azure Container Registry identifier hosting the Neuvector deployment artifacts (Defaults to \"choreocontrolplane.azurecr.io\")";
    echo "--registry-username     - Azure Container Registry login user name";
    echo "--registry-password     - Azure Container Registry login user password";
    echo "--chart-version         - Version of the Neuvector Helm chart stored in the referenced Azure Container Registry";
    echo "--wallarm-node-token    - Token of the Wallarm Node to be integrated"
    echo "--lb-ip                 - IP of the Azure Load Balancer to be set for Wallarm Ingress Controller service"
    echo "--lb-azure-subnet       - Subnet of the Azure Load Balancer to be set for Wallarm Ingress Controller service"
    echo "--lb-azure-rg           - Resource Group of the Azure Load Balancer to be set for Azure Load Balancer to be set for Wallarm Ingress Controller service"
    echo;
    echo -e "e.g. $0 --container-registry=\"sampleacr.azurecr.io\" --registry-username=\"myusername\" --registry-password=\"mypassword\" --chart-version=\"4.6.2\" --wallarm-node-token=\"xxxxxxxxxxxxxxx\" --lb-ip=\"172.18.195.100\" --lb-azure-subnet=\"loadbalancer-subnet\" --lb-azure-rg=\"aks-cluster-rg\"";
    exit 1;
}

# Logging functions
function log_info() {
    local string=$*
    echo "[$(date +'%Y-%m-%dT%H:%M:%S%z')][INFO]: ${string}" >&1
}

function log_error() {
    local string=$*
    echo "[$(date +'%Y-%m-%dT%H:%M:%S%z')][ERROR]: ${string}. Exiting !" >&1
    exit 1
}

# Global variables
environment="dev"
container_reg_identifier="choreocontrolplane.azurecr.io"
container_reg_username=""
container_reg_password=""
helm_chart_version="4.6.2"
wallarm_node_token=""
wallarm_svc_lb_ip=""
wallarm_svc_lb_subnet=""
wallarm_svc_lb_rg=""

for arg in "$@"
do
    case ${arg} in
        --environment=*)
        environment="${arg#*=}"
        shift
        ;;
        --container-registry=*)
        container_reg_identifier="${arg#*=}"
        shift
        ;;
        --registry-username=*)
        container_reg_username="${arg#*=}"
        shift
        ;;
        --registry-password=*)
        container_reg_password="${arg#*=}"
        shift
        ;;
	      --chart-version=*)
        helm_chart_version="${arg#*=}"
        shift
        ;;
        --wallarm-node-token=*)
        wallarm_node_token="${arg#*=}"
        shift
        ;;
        --lb-ip=*)
        wallarm_svc_lb_ip="${arg#*=}"
        shift
        ;;
        --lb-azure-subnet=*)
        wallarm_svc_lb_subnet="${arg#*=}"
        shift
        ;;
        --lb-azure-rg=*)
        wallarm_svc_lb_rg="${arg#*=}"
        shift
        ;;
        *)
        others+=("$1")
        print_usage
        shift
        ;;
    esac
done

# Check if the mandatory input have been provided
[[ -z "${container_reg_username}" ]] && print_usage
[[ -z "${container_reg_password}" ]] && print_usage
[[ -z "${wallarm_node_token}" ]] && print_usage
[[ -z "${wallarm_svc_lb_ip}" ]] && print_usage
[[ -z "${wallarm_svc_lb_subnet}" ]] && print_usage
[[ -z "${wallarm_svc_lb_rg}" ]] && print_usage

# Validate user input
IP_PATTERN='^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$'
! [[ -z "${wallarm_svc_lb_ip}" ]] && ! [[ "${wallarm_svc_lb_ip}" =~ ${IP_PATTERN} ]] && print_usage

# Check if cloud provider is supported
if ! [[ "$environment" = "dev" ]] && ! [[ "$environment" = "stg" ]] && ! [[ "$environment" = "prod" ]]; then
  log_error ">> Not a supported environment - $environment !"
fi

HELM_VERSION=$(helm version --short)
HELM_MAJOR_VERSION=$(echo ${HELM_VERSION} | awk '{print $NF}' | cut -d '.' -f 1)

if [[ $HELM_MAJOR_VERSION -eq "v3" ]]; then
  log_info "Helm client version used: $HELM_VERSION"
else
  log_error "Required: Helm client version needs to be 3.x.x."
fi

# Login to the Azure Container Registry hosting the Wallarm-NGINX Helm chart
if helm registry login "${container_reg_identifier}" --username "${container_reg_username}" --password "${container_reg_password}";
then
  log_info "Successful login to the Azure Container Registry ${container_reg_identifier} hosting the Helm chart"
else
  log_error "Failed to login to the Azure Container Registry ${container_reg_identifier} hosting the Helm chart"
fi

# Make a temporary copy of the custom Helm input values
cp custom-values.yaml custom-values-tmp.yaml
# Replace the environment specific details
sed -i "s/INTERNAL_LB_SUBNET/$wallarm_svc_lb_subnet/g" custom-values-tmp.yaml
sed -i "s/INTERNAL_LB_RESOURCE_GROUP/$wallarm_svc_lb_rg/g" custom-values-tmp.yaml
sed -i "s/INTERNAL_LB_IP/$wallarm_svc_lb_ip/g" custom-values-tmp.yaml
sed -i "s/WALLARM_NODE_TOKEN/$wallarm_node_token/g" custom-values-tmp.yaml

# Install/Upgrade Wallarm-NGINX Ingress Controller deployment
helm upgrade wallarm-ingress oci://choreocontrolplane.azurecr.io/helm/wallarm-ingress \
    --version "${helm_chart_version}" \
    -n wallarm-ingress \
    -f custom-values-tmp.yaml \
    --create-namespace \
    --install

if [ $? != 0 ];
then
  log_error "Failed to install/upgrade the Helm chart for Wallarm-NGINX Ingress Controller"
fi

rm custom-values-tmp.yaml

# Apply Kubernetes resource patches not supported via the Helm chart
# For Wallarm Tarantool
if kubectl patch deployments.apps wallarm-ingress-controller-wallarm-tarantool --patch-file wallarm-controller-tarantool-patch.yaml -n wallarm-ingress ;
then
  log_info "Successfully applied Kubernetes resource patches for Wallarm Tarantool"
else
  log_error "Failed to apply Kubernetes resource patches for Wallarm Tarantool"
fi

if kubectl apply --recursive -f netpols/common;
then
  log_info "Successfully deployed Kubernetes Network Policies for Wallarm-NGINX Ingress Controller"
else
  log_error "Failed to deploy Kubernetes Network Policies for Wallarm-NGINX Ingress Controller"
fi

if kubectl apply --recursive -f "netpols/common/${environment}";
then
  log_info "Successfully deployed ${environment} specific Kubernetes Network Policies for Wallarm-NGINX Ingress Controller"
else
  log_error "Failed to deploy ${environment} specific Kubernetes Network Policies for Wallarm-NGINX Ingress Controller"
fi

log_info "Wallarm-NGINX Ingress Controller successfully installed!!!"
