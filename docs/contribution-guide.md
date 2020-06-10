# Contribution Guide

## Prerequisites

- Git remote `upstream` is configured to point to
  git@github.com:wso2-enterprise/choreo-control-plane.git
  ```
  git remote add upstream git@github.com:wso2-enterprise/choreo-control-plane.git
  ```
- Git remote `origin` is configured to point to
  git@github.com:<user-name>/choreo-control-plane.git
  ```
  git remote add upstream git@github.com:<user-name>/choreo-control-plane.git
  ```
- [GitHub CLI](https://cli.github.com/) is installed.


## How to request a component version upgrade to prod?

You can create a PR to the relevant environment branch with the new version of the component
(example https://github.com/wso2-enterprise/choreo-control-plane/pull/256/files).

## How to request a deployment config change to control plane?

### 1. Creating the feature request issues

Create a feature request issue to track the feature progression from dev to prod. This is used to
track current state of the feature.


### 2. Creating the feature branches

```
git fetch upstream
git branch feature-foo-master upstream/master
git branch feature-foo-dev upstream/dev
git branch feature-foo-prod upstream/prod
```

### 3. Creating the Base PR

```
git checkout feature-foo-master
# do base changes
git add <files>
git commmit
gh pr create -B master -w
```

### 4. Creating the Dev PR

```
git checkout feature-foo-dev
git merge feature-foo-master
# do dev overlay changes
git add <files>
git commmit
gh pr create -B dev -w
```

### 5. Creating the Prod PR

```
git checkout feature-foo-prod
git merge feature-foo-master
# do prod overlay changes
git add <files>
git commmit
gh pr create -B prod -w
```

### Pushing changes after review
#### Doing the changes to base

```
git checkout feature-foo-master
# do the base change
git add <files>
git commmit
git push origin feature-foo-master

git checkout feature-foo-dev
git merge feature-foo-master
git push origin feature-foo-dev

git checkout feature-foo-prod
git merge feature-foo-master
git push origin feature-foo-prod
```

#### Doing the env overlay changes

```
git checkout feature-foo-dev
# do the base change
git add <files>
git commmit
git push origin feature-foo-dev
```
