## Github self hosted runner configuration

### Description
Configure the  Workspace cluster to run GH self-hosted runner on top of it

### Prerequisites
1) Helm 3 installed in execution environment 

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export GITHUB_TOKEN="xxxxxxxxxxxxxxxxx" # provided by DigiOps team
    export ENV="xxxxxxxxxxxxxxxxx" #dev, stage or prod
    ```


3) Execute `configure-runner.sh` script
    ```bash
    bash configure-runner.sh
    ```
   