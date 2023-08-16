# Choreo Kubernetes Infrastructure Upgrade Notice

**Upgrade Date: August 28, 2023** 

An upcoming upgrade to the Choreo Kubernetes infrastructure is scheduled to take place on August 28, 2023.

## Impact on Java-based Components

This upgrade is geared towards enhancing the performance and capabilities of our system. However, it's important to note that there might be an impact on Java-based components in terms of memory usage. Specifically, **applications relying on Java runtime versions older than jdk8u372 or 11.0.16 could experience out-of-memory errors due to potential memory consumption increases resulting from the upgrade**.

## Affected Component Types:

The following Choreo component types could be affected:

- Ballerina services
- WSO2 Micro Gateway integrations
- Triggers
- Webhooks
- REST API Proxies created with mediation policies

## Action Required

- If you have created any of the above components, we strongly recommend you rebuild and redeploy them before the upgrade to ensure compatibility with the updated Java runtime version.

- If you have deployed any containerized applications that use the Java runtime, ensure that you are using the Java version: OpenJDK / HotSpot - jdk8u372, 11.0.16, 15, and later.


For more information, please refer to [this](https://kubernetes.io/blog/2022/08/31/cgroupv2-ga-1-25/#migrate-to-cgroup-v2) document. 
