#!/usr/bin/env bash
command -v kubeseal >/dev/null 2>&1 ||
{ echo "kubeseal not installed. See https://github.com/bitnami-labs/sealed-secrets/releases"; exit 1; }
[[ $# -eq 0 ]] &&
{
    echo "Usage: $0 -d=directory -p=propfile [-n=namespace] [-o=output-dir]"; \
    echo "   -d=directory  - directory with secret properties file"; \
    echo "   -p=propfile   - secret properties file"; \
    echo "   -n=namespace  - namespace for which sealed secrets are generated"; \
    echo "   -o=output-dir - directory where sealed secrets are written to"; \
    echo; \
    echo "   e.g. $0 -p=choreo-secret.properties -n=dev-choreo-system -o=out"; \
    echo "   e.g. $0 -d=secret -n=dev-choreo-system -o=out"; \
    exit 1;
}

function gensecret {
    if [[ ! -f $1 ]]
    then
        echo "File $1 not found"; exit 1
    fi
    local secret
    # shellcheck disable=SC2001,2086
    secret=$(echo ${1##*/} | sed 's/\(.*\)\..*/\1/')
    mkdir -p "${outdir}"
    kubectl create secret generic "secret-${secret}" -n "${namespace}" --from-env-file "$1" --dry-run=client -o yaml |
            kubeseal --scope strict -o yaml - > "${outdir}/${secret}.yaml" &&
    echo "Choreo Sealed secret generated to ${outdir}"
}

function gensecretfromyaml {
    if [[ ! -f $1 ]]
    then
        echo "File $1 not found"; exit 1
    fi
    local secret
    # shellcheck disable=SC2001,2086
    secret=$(echo ${1##*/} | sed 's/\(.*\)\..*/\1/')
    mkdir -p "${outdir}"
    # shellcheck disable=SC2018,SC2019
    secretname=$(echo "$secret" | tr a-z A-Z | tr - _)
    kubectl create secret generic "secret-${secret}" -n "${namespace}" --from-file="$secretname=$1" --dry-run=client -o yaml |
            kubeseal --scope strict -o yaml - > "${outdir}/${secret}.yaml" &&
    echo "Choreo Sealed from yaml file $1 secret generated to ${outdir}"
}

while [[ $(kubectl get pods -n kube-system -l name=sealed-secrets-controller -o \
      'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do
 echo "waiting for kubeseal controller to be ready..." && sleep 10;
done

propfile=""
namespace=""
outdir=""
directory=""

# Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -d=*|--dir=*)
        directory="${arg#*=}"
        shift
        ;;
        -p=*|--propfile=*)
        propfile="${arg#*=}"
        shift
        ;;
        -n=*|--namespace=*)
        namespace="${arg#*=}"
        shift
        ;;
        -o=*|--outdir=*)
        outdir="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift # Remove generic argument from processing
        ;;
    esac
done
[[ -z "${namespace}" ]] && { namespace="default"; }
[[ -z "${outdir}" ]] && { outdir="out"; }

echo "Creating sealed secrets for namespace: ${namespace}"

[[ -z "${propfile}" ]] &&
{
for filename in "${directory}"/*.yaml; do
  gensecretfromyaml "${filename}"
done
}

[[ -z "${propfile}" ]] &&
{
for filename in "${directory}"/*.properties; do
  gensecret "${filename}"
done
}
[[ -z "${directory}" ]] &&
{
  gensecret "${propfile}"
}
