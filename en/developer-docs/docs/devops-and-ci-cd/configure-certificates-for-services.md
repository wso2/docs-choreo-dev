
# Configure Certificates for Services

You can mount organization-level TLS certificates to Service components to enable secure communication with external services. Certificates configured at the organization level are available for selection when deploying services. To learn how to add and manage certificates at the organization level, see [Manage Certificates](./manage-certificates.md).

!!! info
    This feature is currently only available for Ballerina, WSO2 MI, BYOI, Docker, Go, Python, Java, .NET, NodeJS, Ruby, and PHP build presets (excluding Web Applications).

The steps to configure certificates vary depending on the build preset. See the relevant section below:

## Ballerina services

### Deploy a service with certificates

To deploy a Ballerina Service component with certificates, follow the steps given below:

1. Navigate to the Service component's **Deploy** page.
2. Click **Configure & Deploy**. This opens the configuration form.
3. Click **Next** to navigate to the **Certificate Mount** page.
4. Click **Link a Certificate** and specify the following:

    - **Mount Path**: The path where the certificate will be mounted (for example, `/app/`).
    - **Certificate**: Select the certificate from the dropdown. The dropdown lists certificates available at the organization level.

5. Once the certificate is linked, you can expand it to view details such as the **File Name** and **Certificate Key Name**. You can change the file name if required.
6. To mount additional certificates, click **Link a Certificate** again and repeat the above steps.
7. Click **Next** and then click **Deploy** to deploy the component with the linked certificates.

### Promote a service with certificates

When promoting a Ballerina Service component from a lower environment to a higher environment, you can change the certificates applied to the component:

1. Navigate to the Service component's **Deploy** page.
2. Click **Promote** on the environment card of the lower environment.
3. In the promotion wizard, update the certificate configuration as needed for the target environment.
4. Complete the promotion.

### Update certificates on a deployed service

To update, link new certificates or unlink certificates on an already deployed Ballerina Service component, follow the steps given below:

1. Navigate to the Service component's **Deploy** page.
2. On the deployed environment card, click **Configurables**.
3. In the **Configurations** window that opens, click **Next** to navigate to the certificate mount page.
4. From here you can:

    - Link a new certificate by clicking **Link Certificate** and specifying the mount path and certificate.
    - Update an existing certificate mount path or certificate file name.
    - Unlink a certificate by removing it from the list.

5. Click **Update** to apply the changes.

## Other services

This section applies to WSO2 MI, BYOI, Docker, Go, Python, Java, .NET, NodeJS, Ruby, and PHP build presets (excluding Web Applications).

### Deploy a service with certificates

To deploy a Service component with certificates, follow the steps given below:

1. Navigate to the Service component's **Deploy** page.
2. Click **Configure & Deploy**. This opens the configuration form.
3. Click **Next** to navigate to the **File and Certificate Mount** page.
4. Click **Link a Certificate** and specify the following:

    - **Mount Path**: The path where the certificate will be mounted (for example, `/app/`).
    - **Certificate**: Select the certificate from the dropdown. The dropdown lists certificates available at the organization level.

5. Once the certificate is linked, you can expand it to view details such as the **File Name** and **Certificate Key Name**. You can change the file name if required.
6. To mount additional certificates, click **Link a Certificate** again and repeat the above steps.
7. Click **Next** and then click **Deploy** to deploy the component with the linked certificates.

### Promote a service with certificates

When promoting a Service component from a lower environment to a higher environment, you can change the certificates applied to the component:

1. Navigate to the Service component's **Deploy** page.
2. Click **Promote** on the environment card of the lower environment.
3. In the promotion wizard, update the certificate configuration as needed for the target environment.
4. Complete the promotion.

### Update certificates on a deployed service

To update, link new certificates or unlink certificates on an already deployed Service component, follow the steps given below:

1. Navigate to the Service component's **Deploy** page.
2. On the deployed environment card, click **Manage Configs and Secrets**.
3. Expand the **File and Certificate Mount** section.
4. From here you can:

    - Link a new certificate by clicking **Link a Certificate** and specifying the mount path and certificate.
    - Update an existing certificate mount path or certificate file name.
    - Unlink a certificate by removing it from the list.

5. Click **Save and Deploy** to apply the changes.
