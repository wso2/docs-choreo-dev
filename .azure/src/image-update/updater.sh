#!/usr/bin/env bash

# -------------------------------------------------------------------------------------
#
# Copyright (c) 2025, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

set -eo pipefail
CLUSTER_MAPPING_CONFIG=$(cat "$CLUSTER_CONFIG_PATH")

update_images () {
  echo "[INFO] updating image tags"
  for imageName in "$@"; do
    echo "[INFO] updating image tag for $imageName"
    $KUSTOMIZE edit set image "$imageName"
  done

  if [ -n "$UPDATED_CONFIGS" ]
  then
    update_configs
  else
    echo "[INFO] no configs to update"
  fi
}

update_cdn_versions () {
  echo "[INFO] updating cdn-based component versions"
  for component in "$@"; do
    IFS=':' read -ra arr <<<"$component" && unset IFS
    # Prepending a '.' to the component name
    arr[0]=${arr[0]/#/.}

    # Update the version file
    name=${arr[0]} version=${arr[1]} yq -i 'eval(env(name)) = env(version)' versions.yaml
  done
}

update_configs () {
  updatedConfigs="$UPDATED_CONFIGS"
  echo "[DEBUG] $updatedConfigs"
  envFileName='common-env-variables.yaml'

  trimmedUpdatedConfigs=${updatedConfigs//$'\n'/}
  echo "[DEBUG] configs : $trimmedUpdatedConfigs"
  echo "$trimmedUpdatedConfigs" | tr ',' '\n' | while read -r updatingConfig; do
    IFS='=' read -ra config <<< "$updatingConfig"
    echo "Updating ${config[0]} config to ${config[1]}"
    count=$(grep -c "^${config[0]}=" "$envFileName" || true)
    if [[ "$count" -gt 0 ]]
    then
      sed -i "s#^${config[0]}.*#${config[0]}=${config[1]}#" "$envFileName"
    else
      echo "[ERROR] failing the build because config ${config[0]} is not found in $envFileName"
      exit 1
    fi
  done
}

update_multi_cluster_images () {
  local paths
  clusters=$(echo "$1" | jq -r 'keys[]')
  echo "$clusters"
  for cluster in $clusters; do
    mapfile -t paths < <(echo "$CLUSTER_MAPPING_CONFIG" | cluster="$cluster" yq '.[env(cluster)][]')
    UpdatedImages=$(echo "$1" | jq -r ."$cluster"[])
    echo "$UpdatedImages"

    for p in "${paths[@]}"; do
      echo "[INFO] updating image tags in $p"
      cd "$BASE_PATH/$p" || exit
      if [ "$cluster" != "cdn" ]; then
        update_images "$UpdatedImages"
      else
        update_cdn_versions "$UpdatedImages"
      fi
    done
  done
}

setup_git () {
  git config --global user.email "choreo-cicd@wso2.com"
  git config --global user.name "Choreo CI Agent"
}

function main() {
  echo "[INFO] pipeline triggered from a webhook"
  # Checks if the `updatedImageMap` array is empty
  # The first condition checks if the `updatedImageMap` is assigned
  # The second confition checks if the `updatedImageMap` array is empty
  # $(echo "W10K" | base64 -d) = []
  if [ -n "$UPDATED_IMAGE_MAP" ] && [ "$UPDATED_IMAGE_MAP" != "W10K" ]; then
    encodedImagePayload="$UPDATED_IMAGE_MAP"
    trimmedEncodedImagePayload=${encodedImagePayload//$'\n'/}
    decodedImagePayload=$(echo "$trimmedEncodedImagePayload" | base64 --decode)
    update_multi_cluster_images "$decodedImagePayload"
  elif [ -n "$UPDATED_IMAGES" ]; then
    encodedImagePayload="$UPDATED_IMAGES"
    trimmedEncodedImagePayload=${encodedImagePayload//$'\n'/}
    decodedImagePayload=$(echo "$trimmedEncodedImagePayload" | base64 --decode)
    cluster="$CLUSTER_NAME"
    mapfile -t paths < <(echo "$CLUSTER_MAPPING_CONFIG" | cluster="$cluster" yq '.[env(cluster)][]')
    UpdatedImages=$(echo "$decodedImagePayload" | jq -r .[])

    for p in "${paths[@]}"; do
      echo "[INFO] updating image tags in $p"
      cd "$BASE_PATH/$p" || exit
      if [ "$cluster" != "cdn" ]; then
        update_images "$UpdatedImages"
      else
        update_cdn_versions "$UpdatedImages"
      fi
    done

  elif [ -n "$UPDATED_CONFIGS" ]; then
    cluster="$CLUSTER_NAME"
    mapfile -t paths < <(echo "$CLUSTER_MAPPING_CONFIG" | cluster="$cluster" yq '.[env(cluster)][]')
    for p in "${paths[@]}"; do
      echo "[INFO] updating configs in $p"
      cd "$BASE_PATH/$p" || exit
      update_configs
    done
  else
    echo "No changes to update"
  fi
}

function push_changes() {
  main
  if [[ $(git status --porcelain --untracked-files=no) ]]; then
    git diff
    git commit -am "Update image tags in $BUILD_NUMBER"
    while ! git push origin HEAD:"$BRANCH"; do
      echo "[WARNING] Push failed. Fetching latest changes and retrying..."
      git fetch origin "$BRANCH"
      git reset --hard "origin/$BRANCH"
      main
    done
  else
    echo "[INFO] No changes detected"
  fi
}

echo "[DEBUG] updated configs: $UPDATED_CONFIGS"
echo "[DEBUG] image map: $UPDATED_IMAGE_MAP"
echo "[DEBUG] images: $UPDATED_IMAGES"
setup_git
push_changes
