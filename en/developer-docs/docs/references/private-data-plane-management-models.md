# Private Data Plane Management Models

{{ product_name }} supports various management models for private data planes (PDPs), fostering collaboration between Choreo Labs and customers across diverse scenarios. The following sections provide insights into Choreo Labs' fully managed solutions and shared responsibility models, allowing you to make informed decisions regarding cloud-based operations and security.

## Choreo Labs fully managed (infrastructure and PDP in Choreo Labs subscription) model

Choreo Labs fully managed private data planes are supported only on Azure, AWS, and GCP cloud providers.

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
    - Run the {{ product_name }} compatibility prerequisite script</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Customer (If required)</td>
<td>Customer (If required)</td>
</tr>
<tr>
<td>Remote access for installation</td>
<td>Provide owner access</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
</tr>
<tr>
<td>Network management</td>
<td>- Obtain customers backend CIDR in case of VPN/peering</br>
    - Check end-to-end connectivity (primary and failover)</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Firewall rules/access control</td>
<td>Set up firewall and required rules depending on the security tier</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure provisioning</td>
<td>- Provision Bastion</br>
    - Provision Kubernetes clusters</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>Kubernetes cluster management</td>
<td>- Manage Kubernetes versions</br>
    - Increase node pool size</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure monitoring</td>
<td>Set up alerts</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>DNS management for {{ product_name }} system</td>
<td>- Manage DNS infrastructure</br>
    - Manage SSL certificates for {{ product_name }} system components</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} system components deployment</td>
<td>Set up PDP agents via Helm</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>-</td>
</tr>
<tr>
<td>{{ product_name }} system components management</td>
<td>Upgrade/patch/debug versions</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>{{ product_name }} system components monitoring</td>
<td>- Set up continuous monitoring 24x7</br>
    - Provide monthly uptime reports</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} system security monitoring</td>
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
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
</tr>
<tr>
<td>{{ product_name }} application creation/deployment</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application management</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application monitoring</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application logs</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
</tbody>
</table> 

## Choreo Labs fully managed (infrastructure and PDP in customer subscription) model

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
    - Run the {{ product_name }} compatibility prerequisite script</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>Remote access for installation</td>
<td>Provide access</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
</tr>
<tr>
<td>Network management</td>
<td>- Obtain customers backend CIDR in case of VPN/peering</br>
    - Check end-to-end connectivity (primary and failover)</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Firewall rules/access control</td>
<td>Set up firewall and required rules depending on the security tier</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure provisioning</td>
<td>- Provision Bastion</br>
    - Provision Kubernetes clusters</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Kubernetes cluster management</td>
<td>- Manage Kubernetes versions</br>
    - Increase node pool size</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>Infrastructure monitoring</td>
<td>Set up alerts</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>DNS management for {{ product_name }} system</td>
<td>- Manage DNS infrastructure</br>
    - Manage SSL certificates for {{ product_name }} system components</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} system components deployment</td>
<td>Set up PDP agents via Helm</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>Customer</td>
<td>-</td>
</tr>
<tr>
<td>{{ product_name }} system components management</td>
<td>Upgrade/patch/debug versions</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer(If required)</td>
</tr>
<tr>
<td>{{ product_name }} system components monitoring</td>
<td>- Set up continuous monitoring 24x7</br>
    - Provide monthly uptime reports</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
<td>-</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} system security monitoring</td>
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
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
</tr>
<tr>
<td>{{ product_name }} application creation/deployment</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application management</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application monitoring</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application logs</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
</tbody>
</table> 

## Customer self-managed (Choreo Labs provides installation script and updates) model

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
    - Run the {{ product_name }} compatibility prerequisite script</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
</tr>
<tr>
<td>Remote access for installation</td>
<td>Provide owner access</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>Network management</td>
<td>- Obtain customers backend CIDR in case of VPN/peering</br>
    - Check end-to-end connectivity (primary and failover)</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
</tr>
<tr>
<td>Firewall rules/access control</td>
<td>Set up firewall and required rules depending on the security tier</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>Choreo Labs</td>
</tr>
<tr>
<td>Infrastructure provisioning</td>
<td>- Provision Bastion</br>
    - Provision Kubernetes clusters</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>Choreo Labs(If required)</td>
</tr>
<tr>
<td>Kubernetes cluster management</td>
<td>- Manage Kubernetes versions</br>
    - Increase node pool size</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>Choreo Labs(If required)</td>
</tr>
<tr>
<td>Infrastructure monitoring</td>
<td>Set up alerts</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>DNS management for {{ product_name }} system</td>
<td>- Manage DNS infrastructure</br>
    - Manage SSL certificates for {{ product_name }} system components</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>{{ product_name }} system components deployment</td>
<td>Set up PDP agents via Helm</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>{{ product_name }} system components management</td>
<td>Upgrade/patch/debug versions</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>{{ product_name }} system components monitoring</td>
<td>- Set up continuous monitoring 24x7</br>
    - Provide monthly uptime reports</td>
<td>Customer</td>
<td>Customer</td>
<td>Choreo Labs</td>
<td>-</td>
</tr>
<tr>
<td>{{ product_name }} system security monitoring</td>
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
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
<td>Choreo Labs/Customer</td>
</tr>
<tr>
<td>{{ product_name }} application creation/deployment</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application management</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application monitoring</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
<tr>
<td>{{ product_name }} application logs</td>
<td></td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
<td>Customer</td>
</tr>
</tbody>
</table>  
