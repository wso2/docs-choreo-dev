# What is Choreo?

**Choreo** is an AI-native Internal Developer Platform (IDP) that helps teams design, develop, deploy, and manage cloud-native applications. It simplifies infrastructure management by abstracting platform complexity, allowing developers to focus on writing code, while enabling platform engineers to maintain governance and operational control. Choreo supports the full application lifecycle, from code to production, within a unified environment.

![Diagram of Choreo capabilities](../assets/img/what-is-choreo.png){.cInlineImage-full}

## Key capabilities of Choreo

### Architecture and Design
- **Domain-Driven Design**: Group related components such as services, APIs, scheduled tasks, event handlers into Projects based on business domains.
- **Application Security Design**: Leverage [cell-based architecture](https://github.com/wso2/reference-architecture/blob/master/reference-architecture-cell-based.md), where each project is deployed as a Cell. Cells define strict boundaries for communication and access across the application lifecycle.

### Development
- **Language Flexibility**: Use your preferred programming languages to build services, APIs, tasks, and event handlers without changing tools or workflows.
- **VS Code Integration**: Collaborate and manage code using Visual Studio Code, with support for a familiar, feature-rich development environment.
- **Git Integration**: Connect to Git-based platforms such as GitHub, Bitbucket, or GitLab by linking an existing repository to develop components.

### Deployment
- **CI/CD**: Automate builds and deployments using Choreo’s integrated CI/CD pipelines. Supports customizable workflows and deployment tracks.
- **Multi-Cloud Kubernetes**: Easily deploy cloud-native applications across Azure, AWS, GCP, or your Kubernetes clusters.
- **Configuration Management**: Centralize configuration parameters, sensitive credentials, and secrets across deployment environments via an intuitive interface.

### Application Management
- **Security Controls**: Allow security teams to configure and manage access credentials, secrets, and data flow policies.
- **Autoscaling**: Applications are deployed on Kubernetes and scale automatically based on demand to optimize resource usage and cost.
- **Operational Reliability**: DevOps and SRE teams can monitor and manage deployed components with built-in observability and operational insights.

### Reusability
- **Visibility and Sharing**: Publish your digital assets to a shared marketplace for consumption by internal or external stakeholders, managed by role-based access control.
- **Internal Marketplace**: Choreo's internal marketplace provides controlled access to shared resources, allowing publishers and consumers to manage visibility and usage through fine-grained permissions.

### Observability
- **Anomaly Detection**: Get notified of unexpected behavior to help identify issues early and respond as needed.
- **Troubleshoot and Debug**: Use detailed logs and time-aligned data to investigate and resolve problems, including low-level system issues.

### Governance and Insights
- **DORA Metrics**: Monitor key DevOps metrics such as deployment frequency and lead time to assess team performance.
- **Engineering Insights**: Gain visibility into engineering insights and advanced data analytics to make data-driven decisions, optimize processes, and improve efficiency.
- **API Analytics**: Track API usage, latency, and error rates to identify bottlenecks or usage patterns.


Choreo is your all-in-one platform for building, deploying, and managing cloud-native applications with ease, security, and efficiency.
