#!/usr/bin/env bash

propfile=$1
[[ $# -eq 0 ]] &&
{ echo "Usage: $0 -p propfile [-n namespace] [-o output-dir]"; \
echo "   -p=propfile   - secrets properties file"; \
echo "   -n=namespace  - namespace for which sealed secrets are generated"; \
echo "   -o=output-dir - directory where sealed secrets are written to"; \
echo; \
echo "   e.g. $0 -p=choreo-secret.properties -n=dev-choreo-system -o=out"; \
exit 1; }

namespace=$2
outdir=$3

# Loop through arguments and process them
for arg in "$@"
do
    case $arg in
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

echo "Creating sealed secrets for namespace: "${namespace}
from_lit_str=""

if [[ -r ${propfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< $line)
         v=$(cut -d "=" -f2- <<< "$line")
         from_lit_str=${from_lit_str}" --from-literal "$k"="$v" "
    done < "$propfile"
else
    echo "File "${propfile}" not found"; exit 1
fi

mkdir -p ${outdir}
kubectl create secret generic choreo-secret -n ${namespace} ${from_lit_str} \
         --dry-run=client -o yaml > ${outdir}/choreo-secret.yaml
kubeseal --scope strict < ${outdir}/choreo-secret.yaml -o yaml  > ${outdir}/sealed-secret.yaml
echo "Choreo Sealed secret generated to "${outdir}