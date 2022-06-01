#!/usr/bin/env bash

############### Install Certmanager ####################
echo "--- Installing Cert Manager..."
bash ../../cert-manager/install-cert-manager.sh

############### Apply PDBs #############################
echo "--- Enable PDB for Cert Manager"
kubectl apply -f ../../cert-manager/pdb.yaml
