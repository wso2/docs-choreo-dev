#!/bin/bash

set -e  # Exit immediately if any command fails

BRANCH="gh-pages-pe-test"

echo "Deploying Platform Engineer Docs..."
mike deploy --config-file en/pe-docs/mkdocs.yml "Platform Engineer" --branch $BRANCH --push

echo "Deploying Developer Docs..."
mike deploy --config-file en/developer-docs/mkdocs.yml Developer latest --branch $BRANCH --push

echo "Setting Developer as Default Version..."
mike set-default --config-file en/developer-docs/mkdocs.yml Developer --branch $BRANCH --push

mike set-default --config-file en/developer-docs/mkdocs.yml Developer --branch $BRANCH --push

mike serve --remote origin --branch $BRANCH  
