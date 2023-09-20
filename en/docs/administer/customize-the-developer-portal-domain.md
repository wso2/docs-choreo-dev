# Customize the Developer Portal Domain

A custom domain for your site is essential for effective branding, discoverability, and credibility. Choreo allows you to configure a custom domain for your organization's developer portal.

Follow the steps below to configure the custom domain for the Developer Portal:

## Prerequisites

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. If you are a new user, create an organization. Enter a unique organization name. For example, "Stark Industries".
    
    ![Create an organization in Choreo](../assets/img/administer/create-choreo-organization.png){.cInlineImage-small}

3. Obtain a custom domain through a domain registrar. 
4. Through your DNS administrator, create a `CNAME` type DNS record and associate it with the Choreo Developer Portal custom access domain: `customdns.devportal.choreo.dev`. 
5. Optionally, create TLS certificates and keys to enable TLS for the custom domain. For testing purposes, you can also opt to use the `let's encrypt` option. This option allows Choreo to create and manage the certificates on your behalf.

## Configure a custom domain for your Choreo Developer Portal

1. Click **Settings** on the left navigation menu. 
2. In the header, click the **Organization** list. This will open the organization level settings page. 
3. Click the **Domains** tab and then click on the **Developer Portal** tab. 
4. Click **Add Custom Domain**. 
5. Enter your domain name and click **Verify**. Once Choreo verifies the custom domain successfully, click **Next**.
6. Add the TLS certificates you created for the custom domain and click **Add**. Alternatively, you can click the **Let's Encrypt** option to let Choreo generate and manage the certificates for you.

That's it! You have successfully configured a custom domain for your organization's Choreo Developer Portal. 

