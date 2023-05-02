# Component

A component within a project represents a single unit of work in a cloud native application. A component is usually a single microservice, a single API, a single job/task, etc. Each component in Choreo is attached to a given directory path in a Git repository which either contains program source code or a Dockerfile with build instructions. A component is Choreo’s unit of deployment. Each component maps to a single pod in the Kubernetes cluster (data plane) at deployment time. And therefore each component in Choreo can be deployed, managed and scaled independently. 

Choreo supports different component types for various use cases. These include component types such as Services, API proxies, Integrations, WebApps and so on. Each component type hosts unique features based on its characteristics. For example, a scheduled integration component will accept a cron expression as a configuration to schedule the integration job/task.

