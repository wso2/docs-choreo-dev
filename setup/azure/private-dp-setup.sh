#!/usr/bin/env bash

helpFunction()
{
    echo "Usage: $0 -d azure-deploy.properties"; \
    echo "   -d=azuredfile  - azure deployment properties file"; \
    echo; \
    echo "   e.g. $0 -d=azure-deploy.properties"; \
    exit 1;
}

############## Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -d=*|--azuredfile=*)
        azuredfile="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift # Remove generic argument from processing
        ;;
    esac
done

echo -e "\n --- Setting Properties values as environmental variables --- \n"
if [[ -r ${azuredfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< "$line")
         v=$(cut -d "=" -f2- <<< "$line")
         export_command="export $k=$v"
         eval "${export_command}"
    done < "${azuredfile}"
else
    echo "File ${azuredfile} not found"; exit 1
fi

ENV=$(echo "${ENV}" | tr "[:upper:]" "[:lower:]")
export "${ENV?}"
echo "${ENV}"

CUSTOMER_NAME=$(echo "${CUSTOMER_NAME}" | tr "[:upper:]" "[:lower:]")
export "${CUSTOMER_NAME?}"
echo "${CUSTOMER_NAME}"

CLUSTER_ENV=$(echo "${CLUSTER_ENV}" | tr "[:upper:]" "[:lower:]")
export "${CLUSTER_ENV?}"
echo "${CLUSTER_ENV}"

if [[ -z "${CHOREO_ENV}" ]]; then
	echo "Setting up Choreo for ${CUSTOMER_NAME}"
  else
	echo "Setting up Choreo for internal use in ${CHOREO_ENV}"
	CHOREO_ENV=$(echo "${CHOREO_ENV}" | tr "[:upper:]" "[:lower:]")
	export "${CHOREO_ENV?}"
fi

echo "${ASB_CONNECTION_STRING}"

############### Install Helm 3
echo -e "\n --- Installing Helm 3 --- \n"
helm3_installed="true"
command -v helm >/dev/null 2>&1 || {
    helm3_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash
        helm3_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install helm
        helm3_installed="true"
    else
        echo "Could not install helm3. Unsupported operating system. Please manually install it.."
    fi
}

################ Install Step Cli
#echo "--- Installing Step to generate Keys..."
#step_installed="true"
#command -v step >/dev/null 2>&1 || {
#    step_installed="false"
#    if [[ "$OSTYPE" == "linux-gnu" ]]; then
#        wget https://github.com/smallstep/cli/releases/download/v0.14.6/step-cli_0.14.6_amd64.deb -O /tmp/step-cli_0.14.6_amd64.deb
#        sudo dpkg -i /tmp/step-cli_0.14.6_amd64.deb
#        step_installed="true"
#    elif [[ "$OSTYPE" == "darwin"* ]]; then
#        brew install step
#        step_installed="true"
#    else
#        echo "Could not install step. Unsupported operating system. Please manually install it.."
#    fi
#}

echo -e "\n --- Creating secrets for DNS-01 challenge --- \n"
#DNS01_CHALLENGE_CLIENT_SECRET=$(az ad app credential reset --id "${DNS01_CHALLENGE_CLIENT_ID}" --append --display-name "dataplane-${ENV}" --years 2 | grep password | cut -d ":" -f2 | cut -d '"' -f 2)
#kubectl create secret generic "choreo-secret-azuredns-config" --from-literal=client-secret="${DNS01_CHALLENGE_CLIENT_SECRET}" -n cert-manager --dry-run=client -o yaml | kubectl apply -f -

echo -e "\n --- Creating AKS view cluster role binding to AAD --- \n"
cp conf/view-cluster-role-binding.yaml conf/view-cluster-role-binding.yaml.backup

AKS_RESOURCE_ID=$(az aks show --name choreo-"${CUSTOMER_NAME}"-dataplane-"${ENV}" --resource-group choreo-"${CUSTOMER_NAME}"-dataplane-"${ENV}"-aks-rg --query "id" --output tsv)
AKS_READONLY_AD_GROUP_ID=$(az role assignment list --scope "${AKS_RESOURCE_ID}" --query "[?contains(principalName, 'choreo-${CUSTOMER_NAME}-dataplane-${ENV}-aks-rbac-reader')].{principalId:principalId}" --output tsv)

sed -i "s/AKS_READONLY_AD_GROUP_ID/${AKS_READONLY_AD_GROUP_ID}/g" conf/view-cluster-role-binding.yaml 
kubectl apply -f conf/view-cluster-role-binding.yaml
mv conf/view-cluster-role-binding.yaml.backup conf/view-cluster-role-binding.yaml

echo -e "\n --- Add OMS Agent Config --- \n"
#kubectl apply -f oms/container-azm-ms-agentconfig.yaml

# echo -e "\n --- Configure CSI Secret Store --- \n"
# bash private-dataplane/configure-csi-secret-store.sh

echo -e "\n --- Setup Nginx Ingress --- \n"
bash private-dataplane/install-nginx-ingress.sh

echo "--- Setup LetsEncrypt issuer"
#kubectl apply -f private-dataplane/certs/choreoapis-issuer.yaml

#echo "-- Setup LetsEncrypt cert"
#cp private-dataplane/certs/choreoapis-cert.yaml private-dataplane/certs/choreoapis-cert.yaml.backup

#sed -i "s/WILDCARD_DNS_NAME/${WILDCARD_DNS_NAME}/g" private-dataplane/certs/choreoapis-cert.yaml

#kubectl apply -f private-dataplane/certs/choreoapis-cert.yaml

#mv private-dataplane/certs/choreoapis-cert.yaml.backup private-dataplane/certs/choreoapis-cert.yaml

############ Cleanup
echo -e "\n --- Unsetting Properties values set as environmental variables --- \n"
if [[ -r ${azuredfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< "$line")
         unset_command="unset $k"
         eval "${unset_command}"
    done < "${azuredfile}"
else
    echo "File ${azuredfile} not found"; exit 1
fi

successful="true"
# shellcheck disable=SC2154
if [[ "${successful}" == "true" ]]; then
    echo "Choreo Data Plane cluster has been successfully configured"
fi
if [[ "${helm3_installed}" == "false" ]]; then
    echo "[FAILED] helm3 installation. See https://helm.sh/docs/intro/install/"
    helm3_installed=false
fi
#if [[ "${step_installed}" == "false" ]]; then
#    echo "[FAILED] step cli installation. See https://smallstep.com/docs/getting-started/#1-installing-step-and-step-ca"
#    step_installed=false
#fi
