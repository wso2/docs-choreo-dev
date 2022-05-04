## Github self hosted runner configuration

### Description
Configure the  Workspace cluster to run GH self-hosted runner on top of it

### Usage
1) Initally ssh into relvant bastion and set the cluster context to Workspace

2) Export following environmental variable
    ```bash
    export GITHUB_TOKEN="test_token"
    export ENV="environment" #dev, stage or prod
    ```


3) Execute `configure-runner.sh` script
    ```bash
    bash configure-runner.sh
    ```
