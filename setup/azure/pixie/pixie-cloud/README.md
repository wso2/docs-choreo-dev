### How to setup pixie cloud (in control-plane)

1. Run `create_cloud_secrets.sh`

2. Create TLS secret for `px-cloud.{ENV}.choreo.dev` domain.

    name of the secret: `{ENV}-choreo-px-cloud-wildcard-tls`

4. Apply kustomize overlay

5. Setup default admin account

    Open the url printed in the logs of create-admin-job pod and
    set a password for the default "admin@default.com" user

6. Invite other users as necessary using admin console
