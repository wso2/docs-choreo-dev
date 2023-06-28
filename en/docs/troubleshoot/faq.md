# Frequently Asked Questions

## General

### Q: What is Choreo?
Choreo is your SaaS application development suite designed to accelerate the creation of digital experiences. With Choreo you can build, deploy, monitor, and manage your cloud-native applications while increasing developer productivity, allowing you to focus on innovation.

Choreo consists of five profiles:

- **Internal Developer Platform**: A full lifecycle cloud-native application development platform to create, deploy, run, and govern APIs, integrations, and microservices on Kubernetes. Enable organization-wide productivity via self-service teams, governance and security, enterprise marketplaces, and business metrics.
- **API Manager**:  A complete platform for enterprise-grade API-first development to create, deploy, run, and manage APIs and services, which integrate perfectly into your development practices with multiple environments. Deploy to the Choreo cloud or any private cloud within minutes.
- **iPaaS**: A developer-friendly platform for writing real integrations as code or low-code. Create scheduled tasks, reusable APIs, or event-driven integrations. Build, deploy, run, and observe in a seamless serverless, multi-cloud, and multi-environment setup.
- **Cloud Native App Developer**: Design, develop, deploy, and operate your backend APIs, services, integrations, and more with any programming language.
- **DevOps**: Release and manage applications on Kubernetes without compromising on security, reliability, or governance, across any cloud.

### Q: What is an organization in Choreo?
An organization is a logical grouping of users and their resources. It may represent a company, community, or a single user. Users can belong to multiple organizations, and each organization can have different roles assigned to its users to control access to Choreo features.

### Q: What is a project in Choreo?
A project is a logical grouping of related components to help you to organize your work. Each project provides runtime isolation through namespaces when you deploy components.

### Q: What is a component in Choreo?
A component is a workload designed to run on Choreo. Examples of components include integrations, APIs, microservices, manual/scheduled jobs, web apps, and triggers.

### Q: What is the difference between an internal and external API?
In Choreo, you can publish an API as an internal or an external API. A user or an application can access an external API publicly over the internet, whereas an internal API is only accessible through other components within the same organization. 

### Q: What is a connector in Choreo Marketplace?
A connector is a reusable Ballerina package that simplifies connecting to external or internal systems and APIs, such as Salesforce, SAP, GitHub, and Twilio. You can use the connectors available in the Choreo marketplace to implement your integration use cases.  Connectors can be created and published by both WSO2 and Choreo users.

### Q: What is a trigger in Choreo Marketplace?
A trigger is a construct that enables users to receive known event payloads from external systems, facilitating event-driven programming.

### Q: What is a sample/template in Choreo?
A sample or template is a prebuilt Ballerina program that covers a popular integration use case or pattern. Examples include connecting Salesforce to Slack or implementing content-based routing.

### Q: What are the support options in Choreo?
You can find information about our support plans, including `free`, `basic`, and `enterprise` options at [https://wso2.com/choreo/customer-support/](https://wso2.com/choreo/customer-support/).

### Q: How can I perform log monitoring or analytics for the Azure environment?
If you have a log monitoring product or service, such as Azure Monitor, you can use it together with Choreo. Note: The log monitoring tool is not included in the infrastructure cost.

### Q: What source control software does Choreo support?
Choreo currently supports GitHub and BitBucket. Support for GitLab is on our roadmap. 

### Q: What is Ballerina?
Ballerina is an open-source programming language designed for the cloud. It simplifies the process of using, combining, and creating network services. When you use Ballerina to write integrations in Choreo, you can save time and deliver 2-3x faster. To learn more, check out https://ballerina.io/.

### Q: What is Asgardeo?
Asgardeo is an identity provider (IdP) that allows developers to secure access for consumers, business partners, employees, and APIs. Asgardeo is Choreo’s default IDP. To learn more, visit https://wso2.com/asgardeo/.

## Security and Data Protection

### Q: How is data managed in Choreo?
Choreo manages data using WSO2 containers and Kubernetes clusters, which provide scalability, resilience, and security. Find out more [here](https://wso2.cachefly.net/wso2/sites/all/2023/pdf/wso2-public-cloud-data-protection-faq.pdf).

### Q: What is the WSO2 Subprocessor list?
This is a detailed list of all subprocessors used by WSO2, including their name, location, and purpose. This information is updated frequently to ensure compliance with data protection regulations and is found [here](https://wso2.cachefly.net/wso2/sites/all/2023/pdf/wso2-subprocessor-list.pdf).

### Q: How do we secure WSO2 Private and Public Clouds?
WSO2 uses a range of security controls and design patterns to protect against several threats, including internal attacks, software supply chain attacks, service and platform attacks, and more. Find out more regarding this [here](https://wso2.cachefly.net/wso2/sites/all/2023/pdf/securing-wso2-private-and-public-clouds.pdf).

## Data Planes

### Q: What is a Choreo control plane?
The Choreo control plane is a centralized management component that oversees and coordinates the workloads deployed by customers. It provides a unified point of control and visibility for the organization, allowing administrators to manage, monitor, and orchestrate the organization’s resources efficiently.

### Q: What is a data plane?
A data plane in Choreo is a computing environment designed for running customer workloads. These environments are hosted in either a dedicated cloud infrastructure owned by the customer (private data planes) or on public cloud infrastructure owned by WSO2, also known as the Choreo Data Plane.

### Q: I am a Pay-As-You-Go (PAYG) customer using the Choreo Cloud Data Plane. How many environments do I get?
As a PAYG customer, you will receive two environments at no additional cost

### Q: I am an Enterprise subscription customer using the Choreo Private Data Plane. How many environments do I get?
As an Enterprise subscription customer, the number of environments you can use is **not** limited.  However,  the more environments you use, the more resources you will consume in the data plane for the workload you deploy. This may result in higher infrastructure costs for the Private Data Plane.


### Q: Which regions support Choreo Data Planes (CDP)?
The Choreo Data Plane is currently supported in US East 2 and North Europe. However, WSO2 is planning to add support for additional regions as needed.

### Q: Which regions support Private Data Planes (PDP)?
Private Data Planes can be deployed in any region where Azure and AWS are available and meet the requirements for PDP.

### Q: If I want to use my Azure AKS instances as the private data plane, what are the minimum requirements I should consider?
To ensure high availability, we recommend you use a minimum of two (2) workload nodes. 

### Q: Are the Choreo Control Plane and Data Planes highly available? Are they running on multiple clusters?
The Choreo Control Plane and Data Plane are designed for high availability, using Azure components like AKS, MSSQL, ACR, KV, Service Bus, etc. with high availability of 99.99%, which allows at least three workload nodes. In the event of a node failure or upgrade, this setup provides reliable failover. WSO2 also has a backup and recovery strategy in place, including continuous restore drills. If you require AKS cluster-level redundancy, we can consider multiple zones. In this case, the cost will include an additional infrastructure cost.

## Billing and Support

### Q: Whom do I reach out to if I have a billing question?  
Please reach out to cloud-billing-support@wso2.com or create a support ticket via our support portal.

### Q: What's a Developer plan?
A Developer plan allows you to try out Choreo’s capabilities at no cost. It’s ideal for proof of concept (PoC)  tasks or workloads with limited transactions. This plan allows you to experiment with up to 5 components and provides $1,000/year of Choreo Data Plane (CDP) credits.

### Q: How do I calculate the infrastructure costs?
Calculating infrastructure costs depends on the type of workload you want to manage. Here are a few examples:

- **Example 1**: Managing existing APIs with Choreo API Manager as an API proxy with simple mediation; no additional infrastructure costs.
- **Example 2**: Managing existing APIs with Choreo API Manager as an API proxy with complex mediation and policies; Choreo will deploy 1 x container to handle these mediation and policies at approximately USD$57.25 per month per API.
- **Example 3**: Creating, deploying, and managing a new API or integration within Choreo; pay for 1 x component + infrastructure cost. Each container deployed will be approximately USD$57.25 per month on the default configuration provided by Choreo. Additional resources will be charged based on the type of resource required.
- **Example 4**: Creating, deploying, and managing a microservice; the same approach as example 3.

### Q: What are the component limitations? 

- **Developer plan**: Allows up to a maximum of five free components and unlimited paid components.
- **PAYG plan**: Allows unlimited paid components.
- **Enterprise plan**: Allows unlimited paid components.

### Q: How do I read the bill?
Your bill will detail the number of components used, infrastructure consumed, support plans used, and any additional services you may have purchased. If you are unsure about any charges on your bill, reach out to choreo-support@wso2.com for clarification. 

### Q: Is support included in the Choreo Enterprise plan?   
The Choreo Enterprise plan does not automatically include support; however, you can purchase support plans in addition to the Enterprise plan at any time. Find out more at https://wso2.com/choreo/customer-support/.

### Q: I am an Enterprise subscription customer who wants to use the Choreo Private Data Plane. What costs will I incur in addition to the subscription and support plan?
You can start by using a basic plan or contact us for an Enterprise Support plan.

### Q: I want to upgrade from PAYG to an Enterprise subscription. Will there be an outage during the upgrade?
No, there are no outages when upgrading a plan.

## Choreo Internal Developer Platform

### Q: What is the Choreo Internal Developer Platform?
Choreo Internal Developer Platform (IDevP) is a full-lifecycle cloud-native application development platform to create, deploy, run, and govern APIs, integrations, and microservices on Kubernetes. Enable organization-wide productivity via self-service teams, governance and security, enterprise marketplaces, and business metrics.

## Choreo API Manager

### Q: What is Choreo API Manager?
Choreo API Manager is a complete platform for enterprise-grade API-first development to create, deploy, run, and manage APIs and services, which integrate perfectly into your development practices with multiple environments. Deploy to the Choreo cloud or any private cloud within minutes.

## Choreo iPaaS

### Q: What is Choreo iPaaS?
Choreo iPaaS is a developer-friendly platform for writing real integrations as code or low-code. With Choreo iPaaS you can create scheduled tasks, reusable APIs, or event-driven integrations. Build, deploy, run, and observe in a seamless serverless, multi-cloud, and multi-environment setup.

## Choreo Cloud Native App Developer

### Q: What is Choreo Cloud Native App Developer?
Choreo Cloud Native App Developer (CNAD) allows you to design, develop, deploy, and operate your backend APIs, services, integrations, and more with any programming language.

## Choreo DevOps

### Q: What is Choreo DevOps?
Choreo DevOps allows you to release and manage applications on Kubernetes without compromising security, reliability, or governance across any cloud.
