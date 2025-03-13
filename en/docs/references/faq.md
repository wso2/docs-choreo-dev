# Frequently Asked Questions

## General

### Q: What is Choreo?
Choreo is an internal developer platform designed to accelerate the creation of digital experiences. With Choreo, you can effortlessly  build, deploy, monitor, and manage your cloud native applications. Our goal is to  enhance developer productivity and enable innovation.

### Q: What is an organization in Choreo?
An organization is a logical grouping of users and their resources. It may represent a company, community, or a single user. Users can belong to multiple organizations, and each organization can have different roles assigned to its users to control access to Choreo features.

### Q: What is a project in Choreo?
A project is a logical grouping of related components to help you organize your work. Each project provides runtime isolation through namespaces when you deploy components.

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

### Q: What is the maximum request payload size supported by Choreo?
Choreo allows a maximum request payload size of 50 MB.

### Q: What source control software does Choreo support?
Choreo now supports GitHub, Bitbucket and GitLab. 

### Q: Why don't I see the undeployed builds for my component in Choreo?
You are allowed to build your component any number of times. However, Choreo has a limit on retaining undeployed builds. For users on the free-tier, Choreo will retain **only one** undeployed build. For those on any other tier, Choreo will retain the **latest five** undeployed builds.

### Q: What is Ballerina?
Ballerina is an open-source programming language designed for the cloud. It simplifies the process of using, combining, and creating network services. When you use Ballerina to write integrations in Choreo, you can save time and deliver 2-3x faster. To learn more, check out https://ballerina.io/.

### Q: What is Asgardeo?
Asgardeo is an identity provider (IdP) that allows developers to secure access for consumers, business partners, employees, and APIs. Asgardeo is Choreo’s default IDP. To learn more, visit https://wso2.com/asgardeo/.

### Q: Why don’t I see the region selector on the project creation page?
If you are a Choreo cloud data plane user, you can create projects in multiple regions only if you have a paid subscription in Choreo. Otherwise, your projects will be created in the same region you selected when onboarding the organization.

If you are a private data plane user, there will be no region selector in project creation at all.

### Q: As a Cloud Data Plane user, how can I create components in multiple data planes?
When an organization admin onboards a new organization in Choreo, they can choose the preferred data plane. Choreo then sets the selected data plane as the default for the entire organization. Subsequently, users within the free tier of the cloud data plane can create components only in the set default data plane. If a free-tier user needs to create components in a different data plane, the user must get a paid subscription.

## Security and data protection

### Q: How is data managed in Choreo?
Choreo manages data using WSO2 containers and Kubernetes clusters, which provide scalability, resilience, and security. Find out more [here](https://wso2.cachefly.net/wso2/sites/all/trust/wso2-public-cloud-data-protection-faq.pdf).

### Q: What is the WSO2 Subprocessor list?
This is a detailed list of all subprocessors used by WSO2, including their name, location, and purpose. This information is updated frequently to ensure compliance with data protection regulations and is found [here](https://wso2.cachefly.net/wso2/sites/all/trust/wso2-public-cloud-subprocessor-list.pdf).

### Q: How do we secure WSO2 Private and Public Clouds?
WSO2 uses a range of security controls and design patterns to protect against several threats, including internal attacks, software supply chain attacks, service and platform attacks, and more. For more details, see [Cloud Security Process](https://security.docs.wso2.com/en/latest/security-processes/cloud-security-process/).

### Q: How can I connect a Choreo component with a protected third-party application?
To connect a Choreo component with a third-party application, it is necessary to establish seamless communication between the component and the protected third-party application, especially when connecting to external databases like MySQL, MSSQL, PGSQL, Oracle DB, etc.
To ensure this, the requests coming from the Choreo data plane must be allowed by adding the specific data plane IP ranges to your allowlist.

- If your component is deployed in the Choreo US data plane, add the following IP range to your allowlist:
    - 20.22.170.144/28

- If your component is deployed in the Choreo EU data plane, add the following IP range to your allowlist:
    - 20.166.183.112/28

- If you are working on the [Cybertruck Challenge](https://wso2.com/cybertruck/), add the following IP range to your allowlist:
    - 20.190.30.48/28

## Data planes

### Q: What is a Choreo control plane?
The Choreo control plane is a centralized management component that oversees and coordinates the workloads deployed by customers. It provides a unified point of control and visibility for the organization, allowing administrators to manage, monitor, and orchestrate the organization’s resources efficiently.

### Q: What is a data plane?
A data plane in Choreo is a computing environment designed for running customer workloads. These environments are hosted in either a dedicated cloud infrastructure owned by the customer (private data planes) or on public cloud infrastructure owned by WSO2, also known as the Choreo data plane.

### Q: Which regions support the Choreo data plane(CDP)?
The Choreo data plane is currently supported in the US East 2 and North Europe. However, WSO2 is planning to add support for additional regions as needed.

### Q: Which regions support private data planes(PDPs)?
Private data planes can be deployed in any region where Azure and AWS are available and meet the requirements for PDPs.

### Q: If I want to use my Azure AKS instances as the private data plane, what are the minimum requirements I should meet?
We recommend using a minimum of two (2) workload nodes to ensure high availability. 

### Q: Are the Choreo control plane and data planes highly available? Are they running on multiple clusters?
The Choreo control plane and data plane are designed for high availability using Azure components like AKS, MSSQL, ACR, KV, Service Bus, and so on, with a high availability of 99.99%, which allows at least three workload nodes. In the event of a node failure or upgrade, this setup provides reliable failover. WSO2 also has a backup and recovery strategy in place, including continuous restore drills. If you require AKS cluster-level redundancy, we can consider multiple zones. In this case, the cost will include an additional infrastructure cost.

## Environments

### Q: As a Choreo cloud data plane user, why can't I create environments?
You can create environments only if you have a paid subscription in Choreo. It can be either Pay-as-you-Go (PAYG) or an Enterprise plan.

### Q: I am a Pay-As-You-Go (PAYG) customer using the Choreo cloud data plane. How many environments can I create?
You can create up to 5 environments at the organization level, including the existing Development & Production environments by default. If you have projects in both data planes (US & EU), there will be 4 environments already created in total, and you will only be allowed to create one additional environment either in the US or EU data plane.

### Q: I am an Enterprise subscription customer using the Choreo private data plane. How many environments do I get?
As an Enterprise subscription customer, the number of environments you can use is **not** limited.  However, the more environments you use, the more resources you will consume in the data plane for the workload you deploy. This may result in higher infrastructure costs for the private data plane.

### Q: As a Choreo cloud data plane user, why don’t I see both US & EU data planes in the data plane selector when creating an environment?
You will see both US & EU data planes only if you have a paid subscription and have created projects in both US & EU data planes.

### Q: I am a customer who use Choreo in a private data plane. How many environments can I create?
Initially, you will receive the requested number of environments when establishing your private data plane. Subsequently, you can create additional environments as needed.

## Billing and support

### Q: Whom do I reach out to if I have a billing question?  
You can reach out to cloud-billing-support@wso2.com or create a support ticket via our support portal.

### Q: What's a Developer plan?
A Developer plan allows you to try out Choreo’s capabilities at no cost. It’s ideal for proof of concept (PoC)  tasks or workloads with limited transactions. This plan allows you to experiment with up to 5 components and provides US$1,000/year of Choreo data plane (CDP) credits.

### Q: How do I calculate the infrastructure costs?
Calculating infrastructure costs depends on the type of workload you want to manage. Here are a few examples:

- **Example 1**: Managing existing APIs as an API proxy with simple mediation; no additional infrastructure costs.
- **Example 2**: Managing existing APIs as an API proxy with complex mediation and policies; Choreo will deploy 1 x container to handle these mediation and policies at approximately US$57.25 per month per API.
- **Example 3**: Creating, deploying, and managing a new API or integration within Choreo; pay for 1 x component + infrastructure cost. Each container deployed will be approximately US$57.25 per month on the default configuration provided by Choreo. Additional resources will be charged based on the type of resource required.
- **Example 4**: Creating, deploying, and managing a microservice; the same approach as example 3.

### Q: What are the component limitations? 

- **Developer plan**: Allows up to a maximum of five free components and unlimited paid components.
- **PAYG plan**: Allows unlimited paid components.
- **Enterprise plan**: Allows unlimited paid components.

### Q: How do I read the bill?
Your bill will detail the number of components used, infrastructure consumed, support plans used, and any additional services you may have purchased. If you are unsure about any charges on your bill, reach out to choreo-support@wso2.com for clarification. 

### Q: Is support included in the Choreo Enterprise plan?   
The Choreo Enterprise plan does not automatically include support; however, you can purchase support plans in addition to the Enterprise plan at any time. Find out more at https://wso2.com/choreo/customer-support/.

### Q: I am an Enterprise subscription customer who wants to use the Choreo private data plane. What costs will I incur in addition to the subscription and support plan?
You can start by using a basic plan or contact us for an Enterprise support plan.

### Q: I want to upgrade from PAYG to an Enterprise subscription. Will there be an outage during the upgrade?
No, there are no outages when upgrading a plan.

## Choreo CLI

### Q: How do I uninstall the CLI?
If you didn't download the binary directly, you can uninstall the CLI by deleting the `.choreo` directory in the 
home directory of your operating system.

### Q: How do I update the CLI?
You can update the CLI by running the following command:
```sh
curl -o- https://cli.choreo.dev/install.sh | bash
```

### Q: What are the supported component types in the CLI?
The Choreo CLI currently supports the following component types:
- Service
- Web Application
- Webhook
- Scheduled Task
- Manual Task

### Q: How do I get help with a specific command in the CLI?
You can get help with a specific command by running the following command:
```sh
choreo <command> --help
```

### Q: What are the build configurations required when creating components using the CLI?
You can configure the component build configurations depending on the component type as follows:

```sh
choreo create component <name> --project <name> --build-configs='key1=value1,key2=value2'
choreo create component <name> --project <name> --build-configs='key1=value1' --build-configs='key2=value2'
```

The build configurations required for existing buildpacks are as follows:

<table>
   <thead>
      <tr>
         <th>Component Type</th>
         <th>Buildpack</th>
         <th>Required Configurations</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td rowspan=10>Service</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
                  <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            <ul>
         </td>
      </tr>
      <tr>
         <td rowspan=11>Webapp</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Static website</td>
                  <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td>React</td>
         <td>
            <ul>
               <li>buildCommand: Command to be used for building the component</li>
               <li>outputDirectory: Output directory for the component build artifacts</li>
               <li>nodeVersion: Node.js version used</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Angular</td>
         <td>
            <ul>
               <li>buildCommand: Command to be used for building the component</li>
               <li>outputDirectory: Output directory for the component build artifacts</li>
               <li>nodeVersion: Node.js version used</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Vue</td>
         <td>
            <ul>
               <li>buildCommand: Command to be used for building the component</li>
               <li>outputDirectory: Output directory for the component build artifacts</li>
               <li>nodeVersion: Node.js version used</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td rowspan=9>Webhook</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td rowspan=10>Scheduled Task</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>
            <ul style="list-style-type:none">
               <li>Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td rowspan=10>Manual Task</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.JS</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            <ul >
               <li style="list-style-type:none">Not Applicable</li>
            <ul> 
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>
            <ul >
               <li style="list-style-type:none; padding: 0;">Not Applicable</li>
            <ul> 
         </td>
      </tr>
   </tbody>
</table>

</table>

