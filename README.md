# choreo-control-plane
All Choreo control plane related scripts and configuration are in this repo.

## First time setup
Run `setup.sh`

## Change secrets
Edit a secret.properties file and run `secretgen.sh` for the relevant environment
Run `kustomize build <env> | kubectl apply-f -`

## Change config
Edit the config in the relevant kustomize.yaml file and run `kustomize build <env> | kubectl apply-f -`

## TODO:
Need to run mysql deployment only when running `local env`
We may need to introduce a local env
create PVs for each env (PVs don't have a namespace)
create service account for app service
Bring appservice into this setup
Refactor all configs into properties or YAML files
