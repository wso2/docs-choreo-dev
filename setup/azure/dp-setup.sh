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

echo "--- Setting Properties values as environmental variables"
if [[ -r ${azuredfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< "$line")
         v=$(cut -d "=" -f2 <<< "$line")
         export_command="export $k=$v"
         eval "${export_command}"
    done < "${azuredfile}"
else
    echo "File ${azuredfile} not found"; exit 1
fi

############### Install Helm 3
echo "--- Installing Helm 3..."
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


#echo "--- Creating a Kubernetes secret for the Cilium IPsec configuration to be stored..."
PSK=($(dd if=/dev/urandom count=20 bs=1 2> /dev/null | xxd -p -c 64))
kubectl create -n kube-system secret generic cilium-ipsec-keys --from-literal=keys="3 rfc4106(gcm(aes)) $PSK 128"

#echo "--- Creating secrets for DNS-01 challenge..."
# shellcheck disable=SC2154
#kubectl create secret generic "choreo-secret-azuredns-config" --from-literal=client-secret="${DNS01_CHALLENGE_CLIENT_SECRET}" -n cert-manager --dry-run=client -o yaml | kubectl apply -f -

echo "--- Creating AKS view cluster role binding to AAD"
cp conf/view-cluster-role-binding.yaml conf/view-cluster-role-binding.yaml.backup
sed -i "s/AKS_READONLY_AD_GROUP_ID/${AKS_READONLY_AD_GROUP_ID}/g" conf/view-cluster-role-binding.yaml
kubectl apply -f conf/view-cluster-role-binding.yaml
mv conf/view-cluster-role-binding.yaml.backup conf/view-cluster-role-binding.yaml


echo "--- Add OMS Agent Config"
bash dataplane/oms-agent/configure-oms-agent.sh

############ Cleanup
echo "--- Unsetting Properties values set as environmental variables"
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
