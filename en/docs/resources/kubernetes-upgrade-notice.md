# Choreo Kubernetes Infrastructure Upgrade Notice

**Upgrade Date: September 4, 2023, from 3:00 a.m. to 6:00 a.m. UTC** 

An upcoming upgrade to the Choreo Kubernetes infrastructure is scheduled to take place on September 4, 2023.

## Impact on Java-based Components

This upgrade is geared towards enhancing the performance and capabilities of our platform. However, it's important to note that there might be an impact on Java-based components in terms of memory usage. Specifically, **applications relying on Java Runtime versions older than jdk8u372 or 11.0.16 could experience out-of-memory errors due to potential memory consumption increases resulting from the upgrade**.

## Affected Component Types

The following Choreo component types could be affected:

- Components created using the Ballerina preset (For example, Service, Webhook, Scheduled trigger, Manual Trigger components).
- Integration components created using the WSO2 Micro Integrator preset (For example, Integration as an API, Event-triggered integration, Manually-triggered integration, and Scheduled integration components).
- REST API Proxies that include mediation policies.
- Components created using the Dockerfile preset (Components created with containerized applications utilizing the Java Runtime).


## Action Required

**Recommended action date: Before September 4, 2023, 3:00 a.m. UTC** 

To ensure a smooth transition and compatibility with the updated Java Runtime version, follow the steps below:

- **Ballerina or Micro Integrator-based components**: If you have created components using the Ballerina preset or the MI preset, we strongly advise you to [redeploy your components](#redeploy-a-component-in-choreo) before we initiate the upgrade. In the deployment pipeline, we will build your component using the latest Java version.

- **REST API Proxy components that include mediation policies**: If you have created REST API Proxy components and attached a mediation policy, we strongly advise you to [redeploy your components](#redeploy-a-component-in-choreo) before we initiate the upgrade. In the deployment pipeline, we will build your component using the latest Java version.

- **Other Java-based containerized components**: If you have components created using the Dockerfile preset that includes containerized applications utilizing the Java Runtime, follow the steps below:

    1. Upgrade your Java version to OpenJDK / HotSpot - jdk8u372, 11.0.16, 15, or later.
    2. Rebuild your containerized application.
    3. [Redeploy your containerized component](#redeploy-a-component-in-choreo).

### Redeploy a component in Choreo 

To redeploy your component, follow the steps given below:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred login method.
2. Select your component from **Components Listing**. This will open the overview page of your component.
3. In the left navigation menu, click **Deploy**.
4. Deploy your component via the **Build Area** card. 

For more information, refer to [this](https://kubernetes.io/blog/2022/08/31/cgroupv2-ga-1-25/#migrate-to-cgroup-v2) document. 
