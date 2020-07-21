# Choreo Control Plane - Branching, Development & GitOps Guide

This document outlines the process related to continuous deployment of the different branches. This
repo has the following active branches:

1. master
2. dev
3. stage
4. prod

### master
The `master` branch has all the relevant Kubernetes objects related to the Choreo control plane,
including deployments, services, ingresses, HPAs and so on. This excludes any objects related to
configuration, such as ConfigMaps and Secrets. This contains the Kustomize `base` on top of which
other overlays will be applied in `dev`, `stage` & `prod`.

### dev
The `dev` branch has the `base`, which will periodically be synced with the `master`, in addition to
the `dev` & `local` Kustomize overlays. The `base` in `dev` will generally be in sync with the
master's `base`. The `dev` overlay contains the ConfigMap generators/configs as well as the secrets
relevant to the dev cluster. In addition, the image names and tags are also specified here. The
image tags should always be `latest` to ensure that the latest images under development are pulled
into this cluster.
 
The `local` overlay builds on top of `dev` and overrides some of the dev configurations as well as
the secrets. These secrets are generated against the developer's local cluster and will not be
checked in to the code repo. The developer can use the `secretgen.sh` script in the `setup`
directory to generate these secrets.

```
dev-branch
  |- kustomize
      |- base
      |- dev
      |- local
```
### stage
The `stage` branch has the `base`, which will be synced as required with a tag of the `master
branch`. i.e. The `base` in `stage` will generally be in sync with a tag of the master's `base`. The
`stage` overlay contains the ConfigMap generators/configs as well as the secrets relevant to the
stage cluster. In addition, the image names and tags are also specified here. The image tags should
*NEVER* be `latest` and only proper release tags adhering to the `v<major>.<minor>.<micro>`
conventions (e.g. v0.1.13) are allowed. Please ensure that the relevant code repos are first
properly tagged with these versions, which will allow the CI process to build and push the images to
the Azure Container Registry.

```
stage-branch 
  |- kustomize
      |- base
      |- stage
```
### prod
The `prod` branch has the `base`, which will be synced as required with a tag of the `master
branch`. i.e. The `base` in `prod` will generally be in sync with a tag of the master's `base`. The
`prod` overlay contains the ConfigMap generators/configs as well as the secrets relevant to the prod
cluster. In addition, the image names and tags are also specified here. The image tags should
*NEVER* be `latest` and only proper release tags adhering to the `v<major>.<minor>.<micro>`
conventions (e.g. v0.1.13) are allowed. Please ensure that the relevant code repos are first
properly tagged with these versions, which will allow the CI process to build and push the images to
the Azure Container Registry.

```
prod-branch 
  |- kustomize
      |- base
      |- prod
```

## Development Process

### Development
All changes have to first be tested on the `local` cluster using the latest images. You will
generally use the `dev` branch for this purpose, if the changes are related to mainly configuration.
However, if you need to change Kubernetes objects such as deployments, services and so on, first
make the change in your `master` branch and then sync that to your `dev` branch. When you are ready,
send PRs to the `master` and `dev` branches. The CD process will then deploy your changes to the dev
cluster. Continue testing with the dev cluster. Run automated & manual tests. Once successful,
proceed towards promoting these changes to `stage`. if failed, revert the PR.

### Staging
Once the development process as described above has been successfully completed, proceed to this
stage. First sync the `stage` branch with the relevant tag in the `master` in order to get the
compatible Kustomize `base`. Then make the necessary changes in your stage branch and send a PR to
the upstream stage branch. You will use tagged images in this stage and run automated & manual
tests. Once successful, proceed towards promoting these changes to prod. If failed, revert the PR.

### Production
Once staging is successful, proceed to this stage. First sync the `prod` branch with the relevant
tag in the `master` in order to get the compatible Kustomize `base`. Then make the necessary changes
in your prod branch and send a PR to the upstream prod branch. You will use tagged images in this
stage and run automated & manual tests. If tests fail, revert the PR.

## GitHub workflow when contributing

### Prerequisites

- Git remote `upstream` is configured to point to
  `git@github.com:wso2-enterprise/choreo-control-plane.git`
  ```
  git remote add upstream git@github.com:wso2-enterprise/choreo-control-plane.git
  ```
- Git remote `origin` is configured to point to
  `git@github.com:<user-name>/choreo-control-plane.git`
  ```
  git remote add origin git@github.com:<user-name>/choreo-control-plane.git
  ```
- [GitHub CLI](https://cli.github.com/) is installed.
- `git ccp` sub command is installed.
  - Add `<choreo-control-plane-repo-location>/scripts` to `$PATH`.
	```
	export PATH=/path/to/wso2-enterprise/choreo-control-plane/scripts:$PATH
	```

### How to request a component version upgrade to prod?

You can create a PR to the relevant environment branch with the new version of the component
(example https://github.com/wso2-enterprise/choreo-control-plane/pull/256/files).

### How to request a deployment config change to control plane?

#### 1. Creating the feature request issues

Create a feature request issue to track the feature progression from dev to prod. This is used to
track current state of the feature.


#### 2. Creating the feature branches

```
git ccp feature init
```

#### 3. Creating the base changes

```
# do base changes
git add <files>
git commit
git ccp feature sync feature-foo
```

#### 4. Creating the Dev changes

```
git checkout feature/dev/feature-foo
# do dev overlay changes
git add <files>
git commit
```

#### 5. Creating the Stage changes

```
git checkout feature/stage/feature-foo
# do stage overlay changes
git add <files>
git commit
```

#### 6. Creating the Prod changes

```
git checkout feature/prod/feature-foo
# do prod overlay changes
git add <files>
git commit
```

#### 7. Create a CP issue and publish PRs

```
gh issue create
git ccp feature publish feature-foo 999
```

Please use the correct issue number instead of 999 in above command.

#### Pushing changes after review
##### Doing the changes to base

```
# do base changes
git add <files>
git commit
git ccp feature sync feature-foo
```

##### Doing the env overlay changes

```
git checkout feature/<env>/feature-foo
# do the base change
git add <files>
git commmit
git push origin feature/<env>/feature-foo
```
