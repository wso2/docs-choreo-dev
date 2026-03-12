
# Manage Certificates

{{ product_name }} provides centralized certificate management at the organization level, allowing you to manage TLS certificates and apply them to components during deployment. This ensures secure communication between your components and external services by maintaining trusted certificates in a single place.

## Create a certificate

!!! important
    - To create certificates, you need `Create Global Configs` or `Manage Global Configs` permission.

To create a new certificate, follow the steps given below:

1. In the [{{ product_name }} Console](https://console.choreo.dev/), go to the top navigation menu. Click **Organization** and select your organization.
2. In the left navigation menu, click **DevOps** and then click **Certificates**.
3. On the **Certificates Management** page, click **+ Create Certificate**.
4. On the **Add a Certificate** page, choose why you're adding the certificate:

    - **Verify External Server**: Use a public certificate to confirm another server's identity for secure TLS connections.
    - **Secure Website Domain** *(Coming Soon)*: Use SSL/TLS to safely secure your custom domains managed through {{ product_name }}.

5. Specify the following details:

    - **Certificate Name**: A name for the certificate.
    - **Description**: A description for the certificate (optional).
    - **Certificate File**: Upload the certificate file in `.pem` format.

6. Click **Add**.

## View certificates

!!! important
    - To view certificates, you need `View Global Configs` permission.

To view the certificates in your organization, follow the steps given below:

1. In the [{{ product_name }} Console](https://console.choreo.dev/), go to the top navigation menu. Click **Organization** and select your organization.
2. In the left navigation menu, click **DevOps** and then click **Certificates**.
3. The **Certificates Management** page lists all certificates in the organization with the following details:

    - **Identifier**: The name of the certificate.
    - **Type**: The certificate type (for example, Public Cert).
    - **Validity**: The remaining validity period (for example, Expires in 277 days).
    - **Action**: Options to manage the certificate, including delete.

4. Click a certificate to view its **Metadata** tab, which includes details such as expiry date, issuer, subject, and fingerprints.

### View certificate usage

A certificate can be used by multiple components across different projects within your organization. The **Usage** tab provides visibility into all components and the relevant environments that reference the selected certificate.

To view certificate usage:

1. Select a certificate from the list.
2. Click the **Usage** tab.
3. The tab displays all components using the certificate and the environments they are used in.

## Delete a certificate

!!! important
    - To delete certificates, you need `Delete Global Configs` or `Manage Global Configs` permission.

To delete a certificate, follow the steps given below:

!!! warning
    Deleting a certificate is a permanent action. Ensure that the certificate is not in use by any component before deleting it.

1. In the [{{ product_name }} Console](https://console.choreo.dev/), go to the top navigation menu. Click **Organization** and select your organization.
2. In the left navigation menu, click **DevOps** and then click **Certificates**.
3. In the **Certificates** list, click the delete icon next to the certificate you want to delete. This will display a confirmation dialog with details about the impact of the deletion.
4. Review the details and confirm the deletion.
