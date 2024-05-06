# choreo-control-plane

## Introduction

This repository contains base configs of the choreo control plane.

## Building an environment

In order to build an environment, the following prerequisites must be satisfied.

1. [choreo-control-plane](https://github.com/wso2-enterprise/choreo-control-plane)
   and [choreo-cp-env-overlay](https://github.com/wso2-enterprise/choreo-cp-env-overlay) repositories are cloned in the
   same directory level.
2. [main-ci](https://github.com/wso2-enterprise/choreo-control-plane/tree/main-ci) branch is checked out in
   choreo-control-plane repository.
    - If you want to use the [main](https://github.com/wso2-enterprise/choreo-control-plane/tree/main) branch instead,
      please remove the `-ci` from
      the https://github.com/wso2-enterprise/choreo-cp-env-overlay/blob/dev/kustomize/dev/kustomization.yaml#L5 or
      respective environment.
3. Related environment branch (dev, stage, or prod) is checked out
   in [choreo-cp-env-overlay](https://github.com/wso2-enterprise/choreo-cp-env-overlay).

Once you have met all the prerequisites, you can build the kustomize/env branch in choreo-cp-env-overlay folder.
Ex: kustomize build kustomize/dev

## Sending changes

Please refer [this](process.md)
