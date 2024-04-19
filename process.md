# Introduction

This document describes the workflows associated with the control plane. The common configuration resides in the main
branch of the [choreo-control-plane](https://github.com/wso2-enterprise/choreo-control-plane/tree/main) repo and the
environment overlay changes are in the [choreo-cp-env-overlay](https://github.com/wso2-enterprise/choreo-cp-env-overlay)
repo.

# Workflows

## Releasing a component

Releasing a component is automated. Once a developer merges his changes to the relevant component repository, they will
be available in the dev environment. After tests are passed in dev, it will be moved to stage and similarly to prod. The
developer does not need to involve with this process.

## Adding environments overlay configurations

To update the environment overlays configuration for any component, clone the choreo-cp-env-overlay repository,
update/add configurations in the relevant branch and send a PR. These changes will be reflected in the deployment as
soon as the PR is merged.

## Adding common configurations

Common configurations should be added to the main branch of the *chore-control-plane* repository. However, if these
configurations need to be patched for specific environments or require additional environment-specific configurations,
those environment-specific configurations should be added before adding the common configurations. For instance, suppose
a deployment needs a new environment variable which refers to a key in a ConfigMap for the value. If the key already
exists, we just need to update the deployment file in the chore-control-plane repository. However, if the key does not
exist, we need to send 3 PRs to all the environments adding the new key, and get them merged before merging the PR sent
to the chore-control-plane repository updating the deployment resource.

Steps,

1. Add the required config (if any) to the choreo-cp-env-overlay repository
2. Create and merge a PR updating the deployment artifact referring to the new config in the repository
3. Do the required changes in the component repository to work with the new/updated configs. Or you can first change the
   component in a backward-compatible manner to work with old configs and once the deployment is in production, you can
   add new configs.
