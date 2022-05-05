## Table of Contents

- [Preparing the Overlay](#Preparing-the-Overlay)
    - [Sample Overlay Structure](#Sample-Overlay-Structure)
        - [base](#base)
            - [Load balancer patch](#Load-balancer-patch)
                - [Obtaining Load Balancer Internal Subnet](#Obtaining-Load-Balancer-Internal-Subnet)
                - [Obtaining Load Balancer IP](#Obtaining-Load-balancer-IP)
            - [Namespace transformer (optional)](#namespace-transformer-optional)
            - [kustomization.yaml](#kustomizationyaml)
        - [choreo-apim](#choreo-apim)
            - [config/kustomization.yaml](#configkustomizationyaml)
            - [secretproviderclass](#apimSecretProviderClass)
                - [secret-apim](#secret-apim)
                - [secret-choreo-connect](#secret-choreo-connect)
                - [kustomization.yaml](#apimSPCKustomization)
            - [kustomization.yaml](#apimKustomization)
        - [choreo-nginx](#choreo-nginx)
          - [configs](#nginxConfigs)
            - [kustomization.yaml](#nginxConfigsKustomization)
          - [secretproviderclass](#nginxSecretProviderClass)
            - [secret-redis](#secret-redis)
            - [kustomization.yaml](#nginxSPCKustomization)
          - [kustomization.yaml](#nginxKustomization)
        - [choreodp-system](#choreodp-system)
          - [config/kustomization.yaml](#dpConfigKustomization)
          - [secretproviderclass](#dpSecretProviderClass)
            - [secret-dp-kv-resolver](#secret-dp-kv-resolver)
            - [secret-dp-mizzen-agent](#secret-dp-mizzen-agent)
            - [kustomization.yaml](#dpSPCKustomization)
    - [Prerequisites](#Prerequisites)
        - [Preparing SecretProviderClasses](#Preparing-SecretProviderClasses)
            - [secret-apim](#secret-apim)

## Preparing the Overlay

### Sample Overlay Structure

```
.
├── base
│   ├── kustomization.yaml
│   ├── load-balancer-patch.yaml
│   └── namespace-transformer.yaml
├── choreo-apim
│   ├── config
│   │   └── kustomization.yaml
│   ├── kustomization.yaml
│   └── secretproviderclass
│       ├── kustomization.yaml
│       ├── secret-apim.yaml
│       └── secret-choreo-connect.yaml
├── choreo-nginx
│   ├── configs
│   │   ├── dev.poc-dv.choreoapis.dev.conf
│   │   ├── e1-us-east-azure.poc-dv.choreoapis.dev.conf
│   │   ├── kustomization.yaml
│   │   └── poc-dv.choreoapis.dev.conf
│   ├── kustomization.yaml
│   └── secretproviderclass
│       ├── kustomization.yaml
│       └── secret-redis.yaml
├── choreodp-system
│   ├── configs
│   │   └── kustomization.yaml
│   ├── kustomization.yaml
│   └── secretproviderclass
│       ├── dp-kv-resolver.yaml
│       ├── dp-mizzen-agent.yaml
│       └── kustomization.yaml
├── kustomization.yaml
├── namespace
│   ├── choreo-apim-namespace.yaml
│   ├── choreodp-system-namespace.yaml
│   └── kustomization.yaml
├── secretproviderclass-patch.yaml
└── secretproviderclasstransformer.yaml
```

Now let's go through each folder and their contents.

#### base

This is used to overlay the resources from this repository. You can use namespace transformers as well. This contains
following overlay.

- Load balancer patch

##### Load balancer patch

You need to override the `load balancer internal subnet` and the `loadBalancerIP` fields of the `router-default-p1-lb`
service.

###### Obtaining Load Balancer Internal Subnet

###### Obtaining Load Balancer IP

Create the `load-balancer-patch.yaml` file in this directory using the below template. Remember to update the
placeholders with real values.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: router-default-p1-lb
  annotations:
    service.beta.kubernetes.io/azure-load-balancer-internal-subnet: "<lb internal subnet>"
spec:
  loadBalancerIP: <load balancer ip>
```

##### Namespace transformer (optional)

You can add a prefix or suffix of your choice to the following namespaces.

- choreo-apim
- choreodp-system

The resources will then be deployed in those namespaces. The following transformer (`namespace-transformer.yaml`) will
add the `dev-` prefix to above namespaces. So the resulting namespaces would be,

- dev-choreo-apim
- dev-choreodp-system

```yaml
apiVersion: builtin
kind: PrefixSuffixTransformer
metadata:
  name: customPrefixer
prefix: "dev-"
fieldSpecs:
  - path: metadata/namespace
```

##### kustomization.yaml

The kustomization should accumulate the above resources as well as the resources from a specific release of this
repository. you can list all the available releases here. For example if you prefer to use the 1.0.0-0.0.5 release, you
can refer to that in your `kustomization.yaml` as follows.

```yaml
resources:
  - git@github.com:Test-Organzation/central-manifests.git/?ref=1.0.0-0.0.5
```

The full kustomization will look something like below. If you didn't specify a `namespace-transformer` please ignore or
comment out the `transformers` section.

```yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - git@github.com:Test-Organzation/central-manifests.git/?ref=2.0.0

transformers:
  - namespace-transformer.yaml

patchesStrategicMerge:
  - load-balancer-patch.yaml
```

#### choreo-apim

##### config/kustomization.yaml

This needs to generate the following 3 configMaps,

- env-choreo-connect-adapter
- env-choreo-connect-enforcer
- env-choreo-connect-router

as shown below.

<table>
    <thead>
        <tr>
            <th>ConfigMap</th>
            <th>Literal/s</th>
            <th>Accepted Values/Format</th>
            <th>Choosing the Correct Value</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="2">env-choreo-connect-adapter</td>
            <td>environmentLabel</td>
            <td>
                Add line breaks properly<br>so that the cell would look normal
                <!--<ul>
                    <li>include</li>
                    <li>pointwise</li>
                </ul>-->
            </td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>localLabel</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>env-choreo-connect-enforcer</td>
            <td>ENFORCER_LABEL</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>env-choreo-connect-router</td>
            <td>ROUTER_LABEL</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
    </tbody>
</table>


The `kustomization.yaml` will look something like below

```yaml
configMapGenerator:
  - name: env-choreo-connect-adapter
    behavior: create
    literals:
      - environmentLabel=poc-dev
      - localLabel=poc-dev-p1
  - name: env-choreo-connect-enforcer
    behavior: create
    literals:
      - ENFORCER_LABEL=poc-dev
  - name: env-choreo-connect-router
    behavior: create
    literals:
      - ROUTER_LABEL=poc-dev
```

<h5 id="apimSecretProviderClass">secretproviderclass</h5>

This directory contains following 2 secretProviderClasses.

- secret-apim
- secret-choreo-connect

###### secret-apim

This class should create two secret objects named as,

1. keystore-passwords
2. apim-passwords

keystore-passwords should contain the following 7 objects as secrets.

- PRIMARY_KEYSTORE_PSWD
- PRIMARY_KEYSTORE_KEY_PSWD
- TLS_KEYSTORE_PSWD
- TLS_KEYSTORE_KEY_PSWD
- INTERNAL_KEYSTORE_PSWD
- INTERNAL_KEYSTORE_KEY_PSWD
- TRUSTSTORE_PSWD

The apim-passwords should contain the following 2 objects as secrets.

- H2_SHARED_DB_PSWD
- SUPER_ADMIN_PSWD

Apart from the above mentioned secrets, this SecretProviderClass should contain the following objects.

- APIM_PRIMARY_KEYSTORE (pfx)
- APIM_TLS_KEYSTORE (pfx)
- APIM_INTERNAL_KEYSTORE (pfx)

A sample secretproviderclass is provided below.

```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: secret-apim
spec:
  secretObjects:
    - secretName: keystore-passwords
      type: Opaque
      data:
        - objectName: PRIMARY_KEYSTORE_PSWD
          key: PRIMARY_KEYSTORE_PSWD
        - objectName: PRIMARY_KEYSTORE_KEY_PSWD
          key: PRIMARY_KEYSTORE_KEY_PSWD
        - objectName: TLS_KEYSTORE_PSWD
          key: TLS_KEYSTORE_PSWD
        - objectName: TLS_KEYSTORE_KEY_PSWD
          key: TLS_KEYSTORE_KEY_PSWD
        - objectName: INTERNAL_KEYSTORE_PSWD
          key: INTERNAL_KEYSTORE_PSWD
        - objectName: INTERNAL_KEYSTORE_KEY_PSWD
          key: INTERNAL_KEYSTORE_KEY_PSWD
        - objectName: TRUSTSTORE_PSWD
          key: TRUSTSTORE_PSWD
    - secretName: apim-passwords
      type: Opaque
      data:
        - objectName: H2_SHARED_DB_PSWD
          key: H2_SHARED_DB_PSWD
        - objectName: SUPER_ADMIN_PSWD
          key: SUPER_ADMIN_PSWD
  parameters:
    objects: |
      array:
        - |
          objectName: apim-PRIMARY-KEYSTORE-PSWD
          objectType: secret
          objectAlias: PRIMARY_KEYSTORE_PSWD
          objectVersion: ""
        - |
          objectName: apim-PRIMARY-KEYSTORE-KEY-PSWD
          objectType: secret
          objectAlias: PRIMARY_KEYSTORE_KEY_PSWD
          objectVersion: ""
        - |
          objectName: apim-TLS-KEYSTORE-PSWD
          objectType: secret
          objectAlias: TLS_KEYSTORE_PSWD
          objectVersion: ""
        - |
          objectName: apim-TLS-KEYSTORE-KEY-PSWD
          objectType: secret
          objectAlias: TLS_KEYSTORE_KEY_PSWD
          objectVersion: ""
        - |
          objectName: apim-INTERNAL-KEYSTORE-PSWD
          objectType: secret
          objectAlias: INTERNAL_KEYSTORE_PSWD
          objectVersion: ""
        - |
          objectName: apim-INTERNAL-KEYSTORE-KEY-PSWD
          objectType: secret
          objectAlias: INTERNAL_KEYSTORE_KEY_PSWD
          objectVersion: ""
        - |
          objectName: apim-TRUSTSTORE-PSWD
          objectType: secret
          objectAlias: TRUSTSTORE_PSWD
          objectVersion: ""
        - |
          objectName: apim-PRIMARY-KEYSTORE
          objectType: secret
          objectFormat: pfx
          objectEncoding: base64
          objectAlias: APIM_PRIMARY_KEYSTORE
          objectVersion: ""
        - |
          objectName: apim-TLS-KEYSTORE
          objectType: secret
          objectFormat: pfx
          objectEncoding: base64
          objectAlias: APIM_TLS_KEYSTORE
          objectVersion: ""
        - |
          objectName: apim-INTERNAL-KEYSTORE
          objectType: secret
          objectFormat: pfx
          objectEncoding: base64
          objectAlias: APIM_INTERNAL_KEYSTORE
          objectVersion: ""
        - |
          objectName: apim-H2-SHARED-DB-PSWD
          objectType: secret
          objectAlias: H2_SHARED_DB_PSWD
          objectVersion: ""
        - |
          objectName: apim-SUPER-ADMIN-PSWD
          objectType: secret
          objectAlias: SUPER_ADMIN_PSWD
          objectVersion: ""
```

###### secret-choreo-connect

This class should create 2 secret objects named as,

1. secret-asb
2. secret-mgw-analytics

The secret-asb should contain the following object as a secret.

- ASB_CONNECTION_STRING

The secret-mgw-analytics should contain the following 2 objects as secrets.

- PROD_ANALYTICS_AUTH_TOKEN
- DEV_ANALYTICS_AUTH_TOKEN

Apart from the above mentioned secrets, this SecretProviderClass should contain the following objects.

- ADAPTER_KEYSTORE_KEY
- ADAPTER_KEYSTORE_CERTIFICATE
- CONTROL_PLANE_CERTIFICATE
- ADAPTER_TRUSTSTORE_CERTIFICATE
- ENFORCER_KEYSTORE_KEY
- ENFORCER_KEYSTORE_CERTIFICATE
- ROUTER_KEYSTORE_KEY
- ROUTER_TRUSTSTORE

A sample secretproviderclass is provided below.

```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: secret-choreo-connect
spec:
  secretObjects:
    - secretName: secret-asb
      type: Opaque
      data:
        - objectName: ASB_CONNECTION_STRING
          key: ASB_CONNECTION_STRING
    - secretName: secret-mgw-analytics
      type: Opaque
      data:
        - objectName: PROD_ANALYTICS_AUTH_TOKEN
          key: PROD_ANALYTICS_AUTH_TOKEN
        - objectName: DEV_ANALYTICS_AUTH_TOKEN
          key: DEV_ANALYTICS_AUTH_TOKEN
  parameters:
    objects: |
      array:
        - |
          objectName: mgw-ADAPTER-KEYSTORE-KEY
          objectType: secret
          objectAlias: ADAPTER_KEYSTORE_KEY
          objectVersion: ""
        - |
          objectName: mgw-ADAPTER-KEYSTORE-CERTIFICATE
          objectType: secret
          objectAlias: ADAPTER_KEYSTORE_CERTIFICATE
          objectVersion: ""
        - |
          objectName: mgw-CONTROL-PLANE-CERTIFICATE
          objectType: secret
          objectAlias: CONTROL_PLANE_CERTIFICATE
          objectVersion: ""
        - |
          objectName: mgw-ADAPTER-TRUSTSTORE-CERTIFICATE
          objectType: secret
          objectAlias: ADAPTER_TRUSTSTORE_CERTIFICATE
          objectVersion: ""
        - |
          objectName: apim-ANALYTICS-AUTH-TOKEN-PROD
          objectType: secret
          objectAlias: PROD_ANALYTICS_AUTH_TOKEN
          objectVersion: ""
        - |
          objectName: apim-ANALYTICS-AUTH-TOKEN-DEV
          objectType: secret
          objectAlias: DEV_ANALYTICS_AUTH_TOKEN
          objectVersion: ""
        - |
          objectName: mgw-ENFORCER-KEYSTORE-KEY
          objectType: secret
          objectAlias: ENFORCER_KEYSTORE_KEY
          objectVersion: ""
        - |
          objectName: mgw-ENFORCER-KEYSTORE-CERTIFICATE
          objectType: secret
          objectAlias: ENFORCER_KEYSTORE_CERTIFICATE
          objectVersion: ""
        - |
          objectName: mgw-ROUTER-KEYSTORE
          objectType: secret
          objectAlias: ROUTER_KEYSTORE_KEY
          objectVersion: ""
        - |
          objectName: mgw-ROUTER-TRUSTSTORE
          objectType: secret
          objectAlias: ROUTER_TRUSTSTORE
          objectVersion: ""
        - |
          objectName: asb-CONNECTION-STRING
          objectType: secret
          objectAlias: ASB_CONNECTION_STRING
          objectVersion: ""
```

<h6 id="apimSPCKustomization">kustomization.yaml</h5>

The `kustomization.yaml` should accumulate the above 2 secretproviderclasses as shown below.

```yaml
resources:
  - secret-apim.yaml
  - secret-choreo-connect.yaml
```

<h5 id="apimKustomization">kustomization.yaml</h5>

The `kustomization.yaml` will look similar to what is provided below. Remember to use the correct namespace here. If you
have added a [namespace-transformer](#namespace-transformer-optional), you have to specify the namespace with that
prefix/suffix

```yaml
resources:
  - config
  - secretproviderclass

namespace: choreo-apim # specify with prefix/suffix; ex: dev-choreo-apim if the prefix is 'dev'

commonLabels:
  app: choreo-apim
```

#### choreo-nginx

<h5 id="nginxConfigs">configs</h5>

The following configMaps need to be generated in this directory.

- nginx-vhosts
- redis-config
- nginx-feature-flags

<table>
    <thead>
        <tr>
            <th>ConfigMap</th>
            <th>Literals/Files</th>
            <th>Accepted Values/Format</th>
            <th>Choosing the Correct Value</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="3">nginx-vhosts</td>
            <td>poc-dv.choreoapis.dev.conf</td>
            <td>
                Add line breaks properly<br>so that the cell would look normal
                <!--<ul>
                    <li>include</li>
                    <li>pointwise</li>
                </ul>-->
            </td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>dev.poc-dv.choreoapis.dev.conf</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>e1-us-east-azure.poc-dv.choreoapis.dev.conf</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td rowspan="5">redis-config</td>
            <td>REDIS_HOST</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>REDIS_PORT</td>
            <td><!--Add your description--></td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>REDIS_SSL</td>
            <td>boolean</td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>REDIS_SSL_VERIFY</td>
            <td>boolean</td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>REDIS_DATABASE</td>
            <td>int</td>
            <td><!--Add your description--></td>
        </tr>
        <tr>
            <td>nginx-feature-flags</td>
            <td>PARTITION_ENABLED</td>
            <td>boolean</td>
            <td><!--Add your description--></td>
        </tr>
    </tbody>
</table>

<h6 id="nginxConfigsKustomization">kustomization.yaml</h5>

The `kustomization.yaml` will look like below once all the above are properly configured. Be sure to add the
above `*.conf` files to the same directory.

```yaml
configMapGenerator:
  - name: nginx-vhosts
    behavior: create
    files:
      - poc-dv.choreoapis.dev.conf
      - dev.poc-dv.choreoapis.dev.conf
      - e1-us-east-azure.poc-dv.choreoapis.dev.conf
  - name: redis-config
    behavior: create
    literals:
      - REDIS_HOST=choreo-dev-global-adapter.redis.cache.windows.net
      - REDIS_PORT=6380
      - REDIS_SSL=true
      - REDIS_SSL_VERIFY=true
      - REDIS_DATABASE=0
  - name: nginx-feature-flags
    behavior: create
    literals:
      - PARTITION_ENABLED=false
```

<h5 id="nginxSecretProviderClass">secretproviderclass</h5>

This class should create 1 secret objects named as,

- secret-redis

Which should contain ‘REDIS_PASSWORD’. A sample secretproviderclass is provided below.

```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: secret-redis
spec:
  secretObjects:
    - secretName: secret-redis
      type: Opaque
      data:
        - objectName: REDIS_PASSWORD
          key: REDIS_PASSWORD
  parameters:
    objects: |
      array:
        - |
          objectName: redis-PASSWORD
          objectType: secret
          objectAlias: REDIS_PASSWORD
          objectVersion: ""
```


<h6 id="nginxSPCKustomization">kustomization.yaml</h5>

The `kustomization.yaml` should accumulate the above secretproviderclass as shown below.

```yaml
resources:
  - secret-redis.yaml
```

<h5 id="nginxKustomization">kustomization.yaml</h5>

The `kustomization.yaml` will look similar to what is provided below. Remember to use the correct namespace here. If you
have added a [namespace-transformer](#namespace-transformer-optional), you have to specify the namespace with that
prefix/suffix

```yaml
resources:
  - configs
  - secretproviderclass

namespace: dev-choreo-apim

commonLabels:
  app: choreo-routing
```

#### choreodp-system

<h5 id="dpConfigKustomization">config/kustomization.yaml</h5>

This needs to generate the following 3 configMaps,

- env-dp-mizzen-agent
- env-dp-kv-resolver

as shown below.

<table>
    <thead>
        <tr>
            <th>ConfigMap</th>
            <th>Literal/s</th>
            <th>Accepted Values/Format</th>
            <th>Choosing the Correct Value</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="2">env-dp-mizzen-agent</td>
            <td>SERVER_FQDN</td>
            <td>
                String (websocket url)<br>Eg. ws://mizzen.dev-controlplane.internal
                <!--<ul>
                    <li>include</li>
                    <li>pointwise</li>
                </ul>-->
            </td>
            <td>Provided by SRE<br>(private DNS to the private link service)</td>
        </tr>
        <tr>
            <td>ENV</td>
            <td>String<br>{development, staging, production}</td>
            <td>Depending on the connected CP environment<br>(this is only used for logging)</td>
        </tr>
        <tr>
            <td rowspan="2">env-dp-kv-resolver</td>
            <td>AZURE_KEYVAULT_NAME</td>
            <td>String</td>
            <td>Name of the UserKV mapped to the current DP<br>cluster.<br>Eg. “poc-userapps-123”</td>
        </tr>
        <tr>
            <td>MAX_CONCURRENT_RECONCILES</td>
            <td>Integer<br>(default should be 100)</td>
            <td>This is used to tweak the throughput.<br>Current recommendation is 100</td>
        </tr>
    </tbody>
</table>


The `kustomization.yaml` will look something like below

```yaml
configMapGenerator:
  - name: env-dp-mizzen-agent
    behavior: create
    literals:
      - SERVER_FQDN="ws://mizzen.dev-controlplane.internal"
      - ENV=development
  - name: env-dp-kv-resolver
    behavior: create
    literals:
      - AZURE_KEYVAULT_NAME=poc-userapps-28
      - MAX_CONCURRENT_RECONCILES=100
```

<h5 id="dpSecretProviderClass">secretproviderclass</h5>

This directory contains following 2 secretProviderClasses.

- secret-dp-kv-resolver
- secret-dp-mizzen-agent

###### secret-dp-kv-resolver

This class should create the following secret objects named as,

- secret-dp-kv-resolver

it should contain the following 3 objects as secrets.

- AZURE_KEYVAULT_CLIENT_ID
- AZURE_KEYVAULT_CLIENT_SECRET
- AZURE_KEYVAULT_TENANT_ID

A sample secretproviderclass is provided below.

```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: secret-dp-kv-resolver
spec:
  secretObjects:
    - secretName: secret-dp-kv-resolver
      type: Opaque
      data:
        - objectName: AZURE_KEYVAULT_CLIENT_ID
          key: AZURE_KEYVAULT_CLIENT_ID
        - objectName: AZURE_KEYVAULT_CLIENT_SECRET
          key: AZURE_KEYVAULT_CLIENT_SECRET
        - objectName: AZURE_KEYVAULT_TENANT_ID
          key: AZURE_KEYVAULT_TENANT_ID
  parameters:
    objects:  |
      array:
        - |
          objectName: dp-kv-resolver-AZURE-KEYVAULT-CLIENT-ID
          objectType: secret
          objectAlias: AZURE_KEYVAULT_CLIENT_ID
          objectVersion: ""
        - |
          objectName: dp-kv-resolver-AZURE-KEYVAULT-CLIENT-SECRET
          objectType: secret
          objectAlias: AZURE_KEYVAULT_CLIENT_SECRET
          objectVersion: ""
        - |
          objectName: dp-kv-resolver-AZURE-KEYVAULT-TENANT-ID
          objectType: secret
          objectAlias: AZURE_KEYVAULT_TENANT_ID
          objectVersion: ""
```

###### secret-dp-mizzen-agent

This class should create a secret objects named as,

- secret-dp-mizzen-agent

which contains `TOKEN` as the secret object.

A sample secretproviderclass is provided below.

```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1alpha1
kind: SecretProviderClass
metadata:
  name: secret-dp-mizzen-agent
spec:
  secretObjects:
    - secretName: secret-dp-mizzen-agent
      type: Opaque
      data:
        - objectName: TOKEN
          key: TOKEN
  parameters:
    objects: |
      array:
        - |
          objectName: dp-mizzen-agent-poc-TOKEN
          objectType: secret
          objectAlias: TOKEN
          objectVersion: ""
```

<h6 id="dpSPCKustomization">kustomization.yaml</h5>

The `kustomization.yaml` should accumulate the above 2 secretproviderclasses as shown below.

```yaml
resources:
  - dp-kv-resolver.yaml
  - dp-mizzen-agent.yaml
```
