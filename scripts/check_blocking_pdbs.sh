#!/usr/bin/env bash

echo "Following blocking PDBs exist in the cluster :- "
echo ""
for blocking_pdb_name in $(kubectl get pdb -A -o json |  jq '.items[] | select(.status.disruptionsAllowed == 0  and .status.expectedPods > 0)| .metadata.name' | sed 's/"//g') ; do
  echo "$blocking_pdb_name"
done

CURRENT_TIME=$(date "+%Y.%m.%d-%H.%M.%S")
BLOCKING_PDBS_JSON_FILE="blocking-pdbs-${CURRENT_TIME}.json"

for blocking_pdb_json in $(kubectl get pdb -A -o json |  jq '.items[] | select(.status.disruptionsAllowed == 0  and .status.expectedPods > 0)') ; do
  echo "$blocking_pdb_json" >> "$BLOCKING_PDBS_JSON_FILE"
done

echo ""
echo "Configurations of above blocking PDBs are wriiten to following file in JSON format:-"
echo ""
echo "$BLOCKING_PDBS_JSON_FILE"
