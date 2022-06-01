## Configuring Lets Encrypt certificates

### Description
Create the secrets required for Lets Encrypt DNS-01 challenge in Choreo controlplane

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export DNS01_CHALLENGE_CLIENT_ID="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-lets-encrypt-certs.sh` script
    ```bash
    bash configure-lets-encrypt-certs.sh
    ```