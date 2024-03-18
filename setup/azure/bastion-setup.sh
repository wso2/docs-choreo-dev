#!/usr/bin/env bash

### Install Azure-CLI

echo "--- Installing Azure CLI..."
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

### Install Kubectl

echo "--- Installing Kubectl..."
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
chmod +x kubectl
mkdir -p ~/.local/bin/kubectl
mv ./kubectl ~/.local/bin/kubectl

### Install Sqlcmd

echo "--- Installing Sqlcmd..."
curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
curl https://packages.microsoft.com/config/ubuntu/22.04/prod.list | sudo tee /etc/apt/sources.list.d/msprod.list
sudo apt-get update
sudo apt-get install mssql-tools unixodbc-dev
echo "export PATH=\"$PATH:/opt/mssql-tools/bin\"" >> ~/.bash_profile
echo "export PATH=\"$PATH:/opt/mssql-tools/bin\"" >> ~/.bashrc
source ~/.bashrc
sudo ln -s /opt/mssql-tools/bin/* /usr/local/bin/

### Install jq

echo "--- Installing jq..."
sudo apt-get update
sudo apt-get install jq

### Install yq

echo "--- Installing yq..."
sudo wget https://github.com/mikefarah/yq/releases/download/v4.40.5/yq_linux_amd64.tar.gz -O - | \
tar xz && sudo mv yq_linux_amd64 /usr/bin/yq

### Install kapp

echo "--- Installing kapp..."
wget https://github.com/vmware-tanzu/carvel-kapp/releases/download/v0.60.0/kapp-linux-amd64
sudo mv kapp-linux-amd64 /usr/local/bin/kapp
sudo chmod +x /usr/local/bin/kapp
