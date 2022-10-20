### How to setup pixie cloud (in control-plane)

1. Run `create_cloud_secrets.sh`

2. Create TLS secret for `px-cloud.{preview-dv/st/NA}.choreo.dev` domain.

    name of the secret: `{dev/stage/prod}-choreo-px-cloud-wildcard-tls`

3. Apply kustomize overlay

4. Setup default admin account

    Open the url printed in the logs of create-admin-job pod and
    set a password for the default "admin@default.com" user

5. Invite other users as necessary using admin console


### How to re-deploy pixie cloud (in case of an unrecoverable error)

1. Stop choreo deployment pipeline

2. Delete `plc` and `elastic-system` namespaces

3. Run `create_cloud_secrets.sh`

4. Make sure `{dev/stage/prod}-choreo-px-cloud-wildcard-tls` tls secret is created

5. Enable and trigger choreo deployment pipeline

6. Setup default admin account

   Open the url printed in the logs of create-admin-job pod and
   set a password for the default "admin@default.com" user

7. Invite other users as necessary using admin console
