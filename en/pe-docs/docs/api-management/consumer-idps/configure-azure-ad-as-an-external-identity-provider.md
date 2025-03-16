# Configure Azure Active Directory (Azure AD) as an External Identity Provider (IdP)

In organizations leveraging Microsoft Azure Active Directory (Azure AD) for identity and access management (IAM), integrating it with Choreo offers powerful API access control. This control hinges on the use of API scopes. That is, it enables the restriction of access to a designated group of users. This document guide you step-by-step to configure Azure AD as your external IdP.

!!! note
    - You must have **Manage Admin Operations** permission under **APIM-ADMIN** permission group to approve component promotion requests.
    - These identity provider settings are used for authentication within components deployed in your organization. They do not apply to authenticating organization members signing into the Choreo Console.


## Prerequisites

Before you try out this guide, be sure you have the following:

- An Azure Active Directory account:  If you don’t already have one,  setup an Azure Active Directory account at [https://azure.microsoft.com/en-gb/](https://azure.microsoft.com/en-gb/).

## Add Azure Active Directory as an external IdP in Choreo

Follow the steps below to add Azure AD as an IdP in Choreo:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the Choreo Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **API Management** and then click on **Consumer IdPs**.
4. Click the **Identity Providers** tab.
5. To add an identity provider, click **+ Identity Provider** 
6. Select  **Microsoft Entra ID (Azure AD)** as the Identity Provider. 
7. Provide a name and a description for the IdP. 
8. To obtain the `Well-Known URL` of your Azure AD instance, on your Azure account, under **Azure Active Directory** go to **App registrations**, and then **Endpoints**. Copy the URI under`OpenID Connect metadata document`.
    
    !!! info
        - In azure, there are two versions of access tokens available. By default, the IDP applications you create use the v1 access token. Therefore, if you intend to use the v1 access token, when providing the `Well-Known URL`, omit the v2.0 path segment from the URL. [Learn more](https://learn.microsoft.com/en-us/azure/active-directory/develop/access-tokens#token-formats)
        For example, convert `https://login.microsoftonline.com/<tenant-id>/v2.0/.well-known/openid-configuration`-> `https://login.microsoftonline.com/<tenant-id>/.well-known/openid-configuration`
        - If you intend to work with v2.0, then the IDP application's manifest should be changed as explained in the [access token documentation](https://learn.microsoft.com/en-us/azure/active-directory/develop/access-tokens#token-formats). 
        
9. Leave the **Apply to all environments** checkbox selected. However, if you want to restrict the use of the external IdP to a certain environment, you can select them from the **Environments** list.
10. Review the endpoints and click **Next**.
