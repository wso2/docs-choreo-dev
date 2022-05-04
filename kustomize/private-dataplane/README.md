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
        - [choreo-nginx](#choreo-nginx)
        - [choreodp-system](#choreodp-system)
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

This is used to patch the resources from this repository. You can use namespace transformers as well. This contains
following patch.

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

The resources will then be deployed in those namespaces. The following transformer (`namespace-transformer.yaml) will add the `dev-` prefix to above
namespaces. So the resulting namespaces would be,

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


#### choreo-nginx

#### choreodp-system

The following prerequisites need to be fulfilled before preparing the overlay.

### Prerequisites

#### Preparing SecretProviderClasses

SecretProviderClasses listed in the following table are needed in the private dataplane deployment.

| Name                    | Namespace        |
| ------------------------| -----------------|
| secret-apim             | *choreo-apim     |
| secret-choreo-connect   | *choreo-apim     |
| secret-redis            | *choreo-apim     |
| secret-dp-kv-resolver   | *choreodp-system |
| secret-dp-mizzen-agent  | *choreodp-system |

##### secret-apim

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

##### secret-choreo-connect

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

##### secret-redis

This class should create 1 secret objects named as,

1. secret-redis

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
