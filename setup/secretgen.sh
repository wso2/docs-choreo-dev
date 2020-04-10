#!/usr/bin/env bash

propfile=$1
[[ $# -eq 0 ]] &&
{ echo "Usage: $0 propfile [namespace] [output-dir]"; \
echo "   propfile   - secrets properties file"; \
echo "   namespace  - namespace for which sealed secrets are generated"; \
echo "   output-dir - directory where sealed secrets are written to"; exit 1; }

namespace=$2
[[ -z "${namespace}" ]] && { namespace="default"; }
outdir=$3
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