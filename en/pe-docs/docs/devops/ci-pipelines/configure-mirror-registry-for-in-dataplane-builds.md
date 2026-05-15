# Configure a Mirror Registry for In-Dataplane Builds

When {{ product_name }} runs builds inside a [private data plane](../../choreo-concepts/data-planes.md#private-data-planes) (PDP) using Argo Workflows, every build step pulls container images and downloads language packages from public sources (Docker Hub, Google Buildpacks registry, npm, Maven Central, PyPI, NuGet, RubyGems, Composer, etc.). On restricted or air-gapped clusters where direct egress to these public sources is blocked, the build will fail at the image pull or dependency resolution step.

To support these environments, {{ product_name }} lets a platform engineer redirect every outbound build dependency through a mirror registry of your choice — for example, an internal Nexus, Harbor, JFrog Artifactory, or any OCI-compliant proxy — by mounting a single Kubernetes Secret into the build pod. The build then transparently pulls images and packages from your mirror instead of the public source.

!!! info
    This feature is available only on private data planes that run the in-dataplane build system on Argo Workflows. It does not apply to {{ product_name }} cloud data plane builds.

## How it works

During each build, {{ product_name }} mounts a Kubernetes Secret named `choreo-build-registry-proxy` into the build pod at `/mnt/proxy-config` (read-only, optional). Build scripts read configuration values from this directory before fetching anything from the network:

- **Container images** are rewritten so that pulls go to the configured OCI mirror instead of the public source, or replaced entirely with a customer-supplied image reference.
- **Language package managers** (npm, Maven, Gradle, NuGet, PyPI, Go, RubyGems, Composer) are reconfigured at the start of the build to fetch dependencies from the configured proxy URLs.

If the Secret is not present, the mounted directory is empty and the build proceeds with the default public sources, so the feature is fully opt-in and backwards compatible.

### Key lookup precedence

For every configuration key, the build script first looks for a build-strategy-specific override and then falls back to a global value:

1. `<strategy>.<key>` — applies only to that build strategy (for example, `buildpack.pkg-npm-url`).
2. `<key>` — applies to all build strategies (for example, `pkg-npm-url`).

The build strategies recognized by the lookup are `buildpack`, `ballerina`, `mi`, `webapp`, `prism`, `test-runner`, and `byoc`.

## Prerequisites

Before you configure the mirror registry, ensure the following:

- The PDP is configured to run in-dataplane builds on Argo Workflows.
- You have `kubectl` access to the build namespace on the PDP cluster.
- Your mirror registry (Nexus, Harbor, JFrog, or equivalent) is reachable from the build cluster and is configured with proxy repositories for each public source you want to mirror.
- Any credentials required by your mirror (basic auth username/password or token) are available.

## Step 1: Identify the values you need

Decide which sources you want to redirect. Most restricted clusters need entries for the following:

- **OCI container image mirrors** — for base images used by the build (Docker Hub, Choreo private registry, Google Cloud Buildpacks registry).
- **Language package proxies** — for the package manager(s) used by your component (npm, Maven, Gradle, NuGet, PyPI, Go, RubyGems, Composer).
- **(Optional) Full image overrides** — when you cannot mirror a public image and instead want the build to use a different image you have already pushed to your own registry.

The full key reference is in the [Configuration reference](#configuration-reference) section below.

## Step 2: Create the `choreo-build-registry-proxy` Secret

Create the Secret in the same namespace as the in-dataplane build workloads. Use one file per key, where the file **name** is the key and the file **content** is the value. For example:

```bash
mkdir -p ./proxy-config
echo -n "nexus.example.com/dockerhub-proxy" > ./proxy-config/oci-dockerhub-url
echo -n "build-user"                         > ./proxy-config/oci-dockerhub-username
echo -n "<password>"                         > ./proxy-config/oci-dockerhub-password

echo -n "https://nexus.example.com/repository/npm-proxy/" > ./proxy-config/pkg-npm-url
echo -n "<npm-token>"                                    > ./proxy-config/pkg-npm-token

echo -n "https://nexus.example.com/repository/maven-proxy/" > ./proxy-config/pkg-maven-url
echo -n "build-user"                                       > ./proxy-config/pkg-maven-username
echo -n "<password>"                                       > ./proxy-config/pkg-maven-password

kubectl -n <build-namespace> create secret generic choreo-build-registry-proxy \
  --from-file=./proxy-config
```

To restrict a value to a single build strategy, prefix the key with the strategy name and a dot. For example, to point only Ballerina builds at a Ballerina-specific Maven proxy:

```bash
echo -n "https://nexus.example.com/repository/ballerina-maven/" \
  > ./proxy-config/ballerina.pkg-maven-url
```

!!! note
    Keys are read as plain text. Do not URL-encode credentials or wrap values in quotes — the build script trims trailing newlines, but does not unescape anything else.

## Step 3: Trigger a build and verify

After the Secret is in place, trigger a build for a component on the PDP and confirm in the CI pipeline logs that:

- Image pulls reference your mirror host instead of `docker.io`, `gcr.io/buildpacks`, or `choreocontrolplane.azurecr.io`.
- Package manager output (npm, Maven, etc.) shows requests going to your proxy URLs.
- The build completes successfully.

If the build fails at an image pull or dependency download step, see [Troubleshooting](#troubleshooting).

## Configuration reference

All keys are optional. Only define the ones you need. Strategy-scoped overrides (`<strategy>.<key>`) always take precedence over the corresponding global key.

### OCI container image mirrors

Use these keys when you want to mirror an entire public OCI registry behind a single proxy host. The build rewrites the registry portion of every image reference to the configured mirror URL.

| Key | Mirrors | Notes |
|---|---|---|
| `oci-dockerhub-url`, `oci-dockerhub-username`, `oci-dockerhub-password` | Docker Hub (`index.docker.io`) | When set, the build skips the default Docker Hub login. |
| `oci-choreo-url`, `oci-choreo-username`, `oci-choreo-password` | The {{ product_name }} private registry that hosts nginx, Prism, and Go base images. | |
| `oci-buildpacks-url`, `oci-buildpacks-username`, `oci-buildpacks-password` | The Google Cloud Buildpacks registry (builder, lifecycle, run images). | |

### Full image overrides

Use these keys when mirroring is not enough — for example, when your security policy requires a customer-built golden image. The value can be either a complete image reference (`registry/repo:tag` or `registry/repo@sha256:...`) or a registry/repo prefix; in the latter case, the original tag or digest is preserved.

| Key | Replaces |
|---|---|
| `image-node-ref` | Node.js base image used by webapp and test-runner builds |
| `image-nginx-ref` | Nginx image used to serve webapp builds |
| `image-prism-ref` | Prism image used by Prism mock builds |
| `image-golang-ref` | Go image used by Prism mock builds |
| `image-buildpacks-run-ref` | Buildpacks run image |
| `image-buildpacks-lifecycle-ref` | Buildpacks lifecycle image |
| `image-buildpacks-builder-ref` | Buildpacks builder image |

### Language package proxies

Use these keys to redirect language package managers to your mirror. Where applicable, supply credentials with the matching `-username`/`-password` keys (or `-token` for npm).

| Key | Package manager | Credentials |
|---|---|---|
| `pkg-npm-url`, `pkg-npm-token` | npm | Token only |
| `pkg-maven-url`, `pkg-maven-username`, `pkg-maven-password` | Maven | Optional basic auth |
| `pkg-gradle-url`, `pkg-gradle-username`, `pkg-gradle-password` | Gradle | Optional basic auth |
| `pkg-nuget-url`, `pkg-nuget-username`, `pkg-nuget-password` | NuGet | Optional basic auth |
| `pkg-python-url`, `pkg-python-username`, `pkg-python-password` | PyPI | Optional basic auth |
| `pkg-go-url`, `pkg-go-username`, `pkg-go-password` | Go module proxy | Optional basic auth |
| `pkg-composer-url`, `pkg-composer-username`, `pkg-composer-password` | PHP Composer | Optional basic auth |
| `pkg-rubygems-url`, `pkg-rubygems-username`, `pkg-rubygems-password` | RubyGems | Optional basic auth |

### Support matrix per build strategy

Not every build strategy consumes every key. The following table summarizes which keys take effect for each strategy:

| Build strategy | OCI mirrors | Image overrides | Package proxies |
|---|---|---|---|
| Buildpack | `oci-dockerhub-url`, `oci-choreo-url`, `oci-buildpacks-url` | `image-buildpacks-run-ref`, `image-buildpacks-lifecycle-ref`, `image-buildpacks-builder-ref` | npm, Maven, Gradle, NuGet, PyPI, Go, Composer, RubyGems |
| Ballerina | `oci-buildpacks-url` | `image-buildpacks-run-ref`, `image-buildpacks-lifecycle-ref`, `image-buildpacks-builder-ref` | Maven, Gradle, NuGet, PyPI, Go, Composer, RubyGems |
| MI | `oci-buildpacks-url` | `image-buildpacks-lifecycle-ref`, `image-buildpacks-builder-ref` | Maven |
| Webapp | `oci-dockerhub-url`, `oci-choreo-url` | `image-node-ref`, `image-nginx-ref` | npm |
| Prism mock | `oci-choreo-url` | `image-prism-ref`, `image-golang-ref` | — |
| Test Runner | `oci-dockerhub-url` | `image-node-ref` | npm |
| BYOC (Dockerfile) | `oci-dockerhub-url` | — | — |

## Domain allowlisting (alternative to a forward proxy)

Some customers prefer not to introduce a mirror registry at all, and instead allow direct egress to the public sources used by the build. In that case the same outbound destinations still apply — only the routing changes. The list of domains that must be reachable depends on the build strategies you use, including:

- Docker Hub (`index.docker.io`, `registry-1.docker.io`, `auth.docker.io`, `production.cloudflare.docker.com`)
- Google Cloud Buildpacks (`us-docker.pkg.dev/gae-runtimes/runtimes-ubuntu2204/*`, `gcr.io/buildpacks/*`)
- The {{ product_name }} private registry (`choreocontrolplane.azurecr.io` or the PDP-specific ACR)
- Language package managers: `registry.npmjs.org`, `repo.maven.apache.org`, `services.gradle.org`, `api.nuget.org`, `pypi.org`/`files.pythonhosted.org`, `proxy.golang.org`/`sum.golang.org`, `rubygems.org`, `repo.packagist.org`
- Ballerina Central (`api.central.ballerina.io`, `fileserver.central.ballerina.io`, `dist.ballerina.io`)
- GitHub release CDN (`release-assets.githubusercontent.com`) for SDK and JDK downloads

Contact {{ product_name }} support for the exact, up-to-date allowlist that applies to the build strategies you have enabled.

## Troubleshooting

- **Build cannot pull an image from your mirror.** Verify that the mirror URL in the Secret does not include a scheme (`https://`) — OCI registry hosts are bare hostnames. Confirm that the configured credentials authenticate from inside the build cluster (`podman login` from a debug pod).
- **`podman login` fails for `index.docker.io` on a restricted cluster.** Make sure `oci-dockerhub-url` is set. When this key is present the build skips the default Docker Hub login; when it is absent, the build will attempt to log in to Docker Hub and fail on clusters where it is blocked.
- **Package manager still hits the public registry.** Confirm the Secret is mounted (`kubectl get pod <build-pod> -o yaml | grep proxy-config`) and that the key uses the correct name and namespace. Strategy-scoped keys (`<strategy>.<key>`) only apply to the listed strategy — set the global key as well if you want the same value used everywhere.
- **Webapp build pulls Node 18 even though a different version is selected in the console.** Set `image-node-ref` to your golden Node image. Without an explicit override, an OCI mirror cannot change the Node version that the webapp build pins.

## Related links

- [Data planes](../../infrastructure/dataplanes/data-planes.md)
- [Configure CI Pipeline](configure-ci-pipeline.md)
- [Use Your Own Container Registry](../../infrastructure/credentials/use-your-own-container-registry.md)
