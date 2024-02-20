# Private Data Plane Management Models

Choreo supports various management models for private data planes (PDPs), fostering collaboration between WSO2 and customers across diverse scenarios. The following sections provide insights into WSO2's fully managed solutions and shared responsibility models, allowing you to make informed decisions regarding cloud-based operations and security.

## WSO2 fully managed (infrastructure and PDP in WSO2 subscription) model

WSO2 fully managed private data planes are supported only on Azure, AWS, and GCP cloud providers.

<table border=1>
<thead>
<tr>
<th align="left">Task</th>
<th align="left">Task description</th>
<th align="left">Responsible party</th>
<th align="left">Accountable</th>
<th align="left">Consulted</th>
<th align="left">Informed</th>
</tr>
</thead>
<tbody>
<tr>
<td>Subscription prerequisites</td>
<td>- Create subscriptions</br>
    - Check quota and service limits</br>
    - Run the Choreo compatibility prerequisite script</td>
<td>WSO2</td>
<td>WSO2</td>
<td>Customer (If required)</td>
<td>Customer (If required)</td>
</tr>
<tr>
<td>Remote access for installation</td>
<td>Provide owner access</td>
<td>WSO2</td>
<td>WSO2</td>
<td>WSO2</td>
<td>WSO2</td>
</tr>
<tr>
<td>Network management</td>
<td>- Obtain customers backend CIDR in case of VPN/peering</br>
    - Check end-to-end connectivity (primary and failover)</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Firewall rules/access control</td>
<td>Set up firewall and required rules depending on the security tier</td>
<td>WSO2</td>
<td>WSO2</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure provisioning</td>
<td>- Provision Bastion</br>
    - Provision Kubernetes clusters</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>Kubernetes cluster management</td>
<td>- Manage Kubernetes versions</br>
    - Increase node pool size</td>
<td>WSO2</td>
<td>WSO2</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure monitoring</td>
<td>Set up alerts</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>DNS management for Choreo system</td>
<td>- Manage DNS infrastructure</br>
    - Manage SSL certificates for Choreo system components</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo system components deployment</td>
<td>Set up PDP agents via Helm</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>-</td>
</tr>
<tr>
<td>Choreo system components management</td>
<td>Upgrade/patch/debug versions</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>Choreo system components monitoring</td>
<td>- Set up continuous monitoring 24x7</br>
    - Provide monthly uptime reports</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo system security monitoring</td>
<td>If basic tier</br>
       - CSPM</br>
       - Apply security patches</br>
       - Manage supply chain security (Image scanning, SAST)</br>
       - Manage security incidents</br>
    If standard tier/premium tier</br>
       - CSPM</br>
       - Apply security patches</br>
       - Manage supply chain security</br>
       - Monitor runtime security alerts (Azure Defender)</br>
       - Monitor security incident and event management (SIEM) alerts</br>
       - Manage security incidents</br>
       - Adhere to compliance standards</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
</tr>
<tr>
<td>Choreo application creation/deployment</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application management</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application monitoring</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application logs</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
</tbody>
</table> 

## WSO2 fully managed (infrastructure and PDP in customer subscription) model

<table border=1>
<thead>
<tr>
<th align="left">Task</th>
<th align="left">Task description</th>
<th align="left">Responsible party</th>
<th align="left">Accountable</th>
<th align="left">Consulted</th>
<th align="left">Informed</th>
</tr>
</thead>
<tbody>
<tr>
<td>Subscription prerequisites</td>
<td>- Create subscriptions</br>
    - Check quota and service limits</br>
    - Run the Choreo compatibility prerequisite script</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>Remote access for installation</td>
<td>Provide access</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>WSO2</td>
</tr>
<tr>
<td>Network management</td>
<td>- Obtain customers backend CIDR in case of VPN/peering</br>
    - Check end-to-end connectivity (primary and failover)</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Firewall rules/access control</td>
<td>Set up firewall and required rules depending on the security tier</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure provisioning</td>
<td>- Provision Bastion</br>
    - Provision Kubernetes clusters</td>
<td>WSO2</td>
<td>WSO2</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Kubernetes cluster management</td>
<td>- Manage Kubernetes versions</br>
    - Increase node pool size</td>
<td>WSO2</td>
<td>WSO2</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure monitoring</td>
<td>Set up alerts</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>DNS management for Choreo system</td>
<td>- Manage DNS infrastructure</br>
    - Manage SSL certificates for Choreo system components</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo system components deployment</td>
<td>Set up PDP agents via Helm</td>
<td>WSO2</td>
<td>WSO2</td>
<td>Customer</td>
<td>-</td>
</tr>
<tr>
<td>Choreo system components management</td>
<td>Upgrade/patch/debug versions</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>Choreo system components monitoring</td>
<td>- Set up continuous monitoring 24x7</br>
    - Provide monthly uptime reports</td>
<td>WSO2</td>
<td>WSO2</td>
<td>-</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo system security monitoring</td>
<td>If basic tier</br>
       - CSPM</br>
       - Apply security patches</br>
       - Manage supply chain security (Image scanning, SAST)</br>
       - Manage security incidents</br>
    If standard tier/premium tier</br>
       - CSPM</br>
       - Apply security patches</br>
       - Manage supply chain security</br>
       - Monitor runtime security alerts (Azure Defender)</br>
       - Monitor security incident and event management (SIEM) alerts</br>
       - Manage security incidents</br>
       - Adhere to compliance standards</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
</tr>
<tr>
<td>Choreo application creation/deployment</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application management</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application monitoring</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application logs</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
</tbody>
</table> 

## Customer self-managed (WSO2 provides installation script and updates) model

<table border=1>
<thead>
<tr>
<th align="left">Task</th>
<th align="left">Task description</th>
<th align="left">Responsible party</th>
<th align="left">Accountable</th>
<th align="left">Consulted</th>
<th align="left">Informed</th>
</tr>
</thead>
<tbody>
<tr>
<td>Subscription prerequisites</td>
<td>- Create subscriptions</br>
    - Check quota and service limits</br>
    - Run the Choreo compatibility prerequisite script</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>WSO2</td>
</tr>
<tr>
<td>Remote access for installation</td>
<td>Provide owner access</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>Network management</td>
<td>- Obtain customers backend CIDR in case of VPN/peering</br>
    - Check end-to-end connectivity (primary and failover)</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>WSO2</td>
</tr>
<tr>
<td>Firewall rules/access control</td>
<td>Set up firewall and required rules depending on the security tier</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>WSO2</td>
</tr>
<tr>
<td>Infrastructure provisioning</td>
<td>- Provision Bastion</br>
    - Provision Kubernetes clusters</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>WSO2(If required)</td>
</tr>
<tr>
<td>Kubernetes cluster management</td>
<td>- Manage Kubernetes versions</br>
    - Increase node pool size</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>WSO2(If required)</td>
</tr>
<tr>
<td>Infrastructure monitoring</td>
<td>Set up alerts</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>DNS management for Choreo system</td>
<td>- Manage DNS infrastructure</br>
    - Manage SSL certificates for Choreo system components</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>Choreo system components deployment</td>
<td>Set up PDP agents via Helm</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>Choreo system components management</td>
<td>Upgrade/patch/debug versions</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>Choreo system components monitoring</td>
<td>- Set up continuous monitoring 24x7</br>
    - Provide monthly uptime reports</td>
<td>Customer</td>
<td>Customer</td>
<td>WSO2</td>
<td>-</td>
</tr>
<tr>
<td>Choreo system security monitoring</td>
<td>If basic tier</br>
       - CSPM</br>
       - Apply security patches</br>
       - Manage supply chain security (Image scanning, SAST)</br>
       - Manage security incidents</br>
    If standard tier/premium tier</br>
       - CSPM</br>
       - Apply security patches</br>
       - Manage supply chain security</br>
       - Monitor runtime security alerts (Azure Defender)</br>
       - Monitor security incident and event management (SIEM) alerts</br>
       - Manage security incidents</br>
       - Adhere to compliance standards</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
<td>WSO2/Customer</td>
</tr>
<tr>
<td>Choreo application creation/deployment</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application management</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application monitoring</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Choreo application logs</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
</tbody>
</table>  
