#!/bin/bash

set -e  # Exit immediately if any command fails

BRANCH="gh-pages-pe-test-abcd"

deploy_with_retry() {
    local config_file=$1
    local version=$2
    local title=$3

    echo "Deploying $version Docs..."
    mike deploy --config-file "$config_file" "$version" -t "$title" --branch "$BRANCH" --push || {
        echo "Deploy failed, force pushing and retrying..."
        git push origin "$BRANCH" --force
        mike deploy --config-file "$config_file" "$version" -t "$title" --branch "$BRANCH" --push
    }
}

# Deploy Platform Engineer Docs
deploy_with_retry "en/pe-docs/mkdocs.yml" "platform-engineer" "Platform Engineer" 

# Deploy Developer Docs
deploy_with_retry "en/developer-docs/mkdocs.yml" "developer" "Developer"

# Set Developer as Default Version
echo "Setting Developer as Default Version..."
mike set-default --config-file en/developer-docs/mkdocs.yml developer --branch "$BRANCH" --push

# Serve documentation
mike serve --remote origin --branch "$BRANCH"
