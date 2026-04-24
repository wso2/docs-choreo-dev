# What is {{ product_name }}?

{{ product_name }} is an Internal Developer Platform (IDP) built to help platform engineering teams build and scale high-performing developer platforms. It provides a unified control plane and self-service portal for managing infrastructure, CI/CD, security, governance, and observability across all stages of the software delivery lifecycle.
{{ product_name }} is designed to reduce the cognitive and operational load for developers while giving platform teams full control and visibility. Built with extensibility and multi-cloud support, it accelerates how organizations ship secure, compliant, and reliable applications.

![Diagram of {{ product_name }} platform capabilities](../assets/img/what-is-choreo.png){.cInlineImage-full}

!!! info "Global Availability"
    **{{ product_name }} is now available in two regions worldwide:**

    - 🇺🇸 **US Region**: [console.choreo.dev](https://console.choreo.dev)
    - 🇪🇺 **EU Region**: [console.eu.choreo.dev](https://console.eu.choreo.dev)

    **Important**: No data is shared between regions. Resources created in one region are not available in the other region. This includes projects, components, APIs, services, configurations, secrets, user data, and monitoring data. Choose the region that meets your data residency and compliance requirements.

!!! tip "Accessible and Inclusive"
    **WSO2 Developer Platform is accessible to everyone.**

    {{ product_name }} conforms to the **Web Content Accessibility Guidelines (WCAG) 2.2 up to Level AA**, with support for keyboard navigation, screen readers, color contrast, focus management, and semantic structure throughout the console. It is built to be usable by everyone, regardless of ability.

## Key capabilities of {{ product_name }}

### Infrastructure Automation
- **Workload Management**: Deploy and manage workloads across environments and clusters with centralized visibility.
- **Flexible Deployments**: Run {{ product_name }} on your existing infrastructure with no vendor lock-in.

### Application Delivery
- **Seamless Git Integration**: Native integration with GitHub, BitBucket, GitLab and Azure DevOps to support GitOps-based workflows.
- **Automated CI/CD Pipelines**: Automates build and deployment pipelines with support for extensions using Argo Workflows.
- **Secrets & Config Management**: Securely manage secrets, configurations, and configurations for API gateways natively within the platform.

### Observability
- **Centralized Logging**: Aggregate and analyze logs seamlessly with Fluentbit and OpenSearch, enabling rapid root-cause analysis.
- **Granular Metrics**: Monitor system performance, optimize resource utilization, troubleshoot issues, and ensure reliability across deployments with Cilium and Prometheus.
- **Proactive Alerts**: Set custom thresholds to detect anomalies, automate incident responses, and prevent downtime before it impacts users.

### Security
- **Network Security**: Use eBPF, Cilium, and WireGuard for end-to-end encryption, fine-grained L3/L4 ingress and egress policies, and microsegmented architecture to minimize attack surfaces and prevent lateral movement.
- **API & Access Control**: Enforce authentication, authorization, and rate-limiting through API gateways, while Role-Based Access Control (RBAC) ensures secure, granular access to projects, environments, and resources.
- **Container & Application Security**: {{ product_name }} automatically scans container images for vulnerabilities in all CI/CD pipelines and ensures runtime protection with robust security measures, reducing risks in production environments.
- **Granular Console Access Control**: Implement role-based permissions and identity management for granular user access control for the console facilitating multiple user roles and personas.

### Developer Self-Service Portal
- **Internal Marketplace**: Provide a centralized hub for developers to discover and reuse existing services and resources  across the organization, fostering collaboration and reuse.
- **Instant Onboarding**: Connect apps from Git repos (GitHub, GitLab, Bitbucket, Azure DevOps) or container registries for easy onboarding and CI/CD-driven deployments to any environment.
- **Self-Service Observability**: Enable teams to access logs, metrics, and insights with built-in access controls, allowing real-time monitoring and issue diagnosis.

### Unified Portal for Platform Engineers
- **Integrated Ecosystem**: Combines commonly used CNCF projects like Kubernetes, Cilium, Prometheus, and Argo Workflows in a single interface.
- **Unified Management**: A single portal to manage CI/CD pipelines, API gateways, monitoring, and infrastructure provisioning.
- **Flexible Hosting**: Deploy {{ product_name }} on public clouds (GCP, AWS, Azure, Vultr) or on upstream-compatible Kubernetes clusters in private environments.

