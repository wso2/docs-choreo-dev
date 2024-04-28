#!/bin/bash

PUBLIC_API_KEY="$1"
PRIVATE_API_KEY="$2"
APP_NAME="$3"
CONFIG_FILE="$4"

# Install the Atlas App Services CLI
echo "Installing the Atlas App Services CLI"
sudo npm install -g atlas-app-services-cli

# Login to the Atlas App Services CLI
echo "Logging in to the Atlas App Services CLI"
appservices login --api-key="$PUBLIC_API_KEY" --private-api-key="$PRIVATE_API_KEY"

# Pull the latest version of the marketplace_assist_app
echo "Pulling the latest version of $APP_NAME"
appservices pull

echo "Copy configs to the values directory"
cp "$CONFIG_FILE" "$APP_NAME"/values

# Get dependencies
echo "Installing dependencies"
npm install axios@1.6.8
tar -czf node_modules.tar.gz node_modules/
mv node_modules.tar.gz ./"$APP_NAME"/functions
cd "$APP_NAME" || exit
appservices push --include-node-modules



# Add configs
echo "Adding configs"
appservices push

