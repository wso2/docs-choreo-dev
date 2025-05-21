#!/usr/bin/env bash
command -v kubeseal >/dev/null 2>&1 ||
{ echo "kubeseal not installed. See https://github.com/bitnami-labs/sealed-secrets/releases"; exit 1; }
[[ $# -eq 0 ]] &&
{
    echo "Usage: $0 -e=email -u=username -p=password"; \
    echo "   -e=email     - Your Azure email"; \
    echo "   -u=username  - Account username"; \
    echo "   -p=password  - Account password"; \
    exit 1;
}

function gensecret {
    local outdir="../kustomize/loc/secret"
    kubectl -n loc-choreo-system create secret docker-registry secret-acr \
      --docker-server choreoctrlplane.azurecr.io \
      --docker-email "${email}" \
      --docker-username "${username}" \
      --docker-password "${password}" --dry-run -oyaml |
        kubeseal --scope strict -o yaml - > "${outdir}/acr.yaml" &&
    echo "Choreo Sealed docker registry secret generated to "${outdir}
}

email=""
username=""
password=""

# Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -e=*|--email=*)
        email="${arg#*=}"
        shift
        ;;
        -u=*|--username=*)
        username="${arg#*=}"
        shift
        ;;
        -p=*|--password=*)
        password="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift # Remove generic argument from processing
        ;;
    esac
done

echo "Creating sealed docker registry secret"

gensecret
