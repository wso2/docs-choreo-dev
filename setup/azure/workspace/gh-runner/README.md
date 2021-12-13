## Github self hosted runner configuration

### Description
Configure the  Workspace cluster to run GH self-hosted runner on top of it

### Usage
1) Initally ssh into relvant bastion and set the cluster context to Workspace

2) Export following environmental variable
    ```bash
    export GITHUB_TOKEN="test_token"
    ```

3) Replace the value of following placeholders in `runner.yaml`
    ```bash
    ${INGRESS-CLASS-NAME}
    ${HOST-NAME}
    ${SECRET-NAME}
    ${MIN-REPLICAS}
    ${MAX-REPLICAS}
    ```
4) Execute `configure-runner.sh` script
    ```bash
    bash configure-runner.sh
    ```
5) Share the hostname configured in step 3 to dev team to add as the webhook URL in relevant Github org
