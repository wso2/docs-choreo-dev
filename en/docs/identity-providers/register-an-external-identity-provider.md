# Registering an External Identity Provider (IdP) for Choreo API Management

Choreo uses an in-built Identity Provider (IdP) to manage the OAuth clients and generate tokens that are required for Choreo APIs by default. However, it is possible to add one or more external IdP(s) for your Choreo organization, so that you can exchange access tokens from that IdP with Choreo access tokens and use those to invoke APIs (See [Exchange an Access Token from an Identity Provider]({{base_path}}/identity-providers/exchange-an-access-token-from-an-identity-provider) for more information). 

Follow this procedure to add/register an external IdP to your Choreo organization.

!!! note
    You need to be an `Admin` user to add an external IdP using the Choreo Console.

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. Go to the left menu bar and click on the **Settings** in the bottom.    

3. Go to the **API Management** tab and select the sub tab named **Identity Providers**. The existing IdPs in your organization will be listed as shown below.

    ![Identity Providers List](){.cInlineImage-full}

    !!! note
        The default IdP of Choreo is known as **in-built** and it is common to all the organizations. Hence, you cannot edit or delete it.

4. Click on the **+ Add** button and the supported IdP types by Choreo will be displayed.

    ![Identity Provider Types List](){.cInlineImage-full}

5. Click on the type of the IdP that you need to add and a form will be diplayed.

6. You have two (2) ways to fill this form. (Note that, this example will show you how to add an Okta IdP. But the steps and the fields are similar for other IdP types as well)

    1. If you know/have the `Well-known URL` enter it and click the **Import** button. The relevant fields will be automatically filled except for the `Alias` field.

    2. If you **do not know/have** the `Well-known URL` fill the other fields manually. You can refer to the table below when filling the fields.

        ![Identity Provider Adding Form](){.cInlineImage-full}

          <table>
          <tr class="header">
          <th><b>Field</b></th>
          <th><b>Description</b></th>
          <th> </th>
          </tr>
          <tr class="odd">
          <td>Name</td>
          <td>The name of the IdP.</td>
          <td>Mandatory</td>
          </tr>
          <tr class="even">
          <td>Description</td>
          <td>A brief description of the IdP.</td>
          <td>Optional</td>
          </tr>
          <tr class="odd">
          <td>Well-known URL</td>
          <td><p>The Well-known URL of the Authorization Server (IdP).
          <br/>
          If the Well-known URL is provided, other endpoints can be imported. 
          <br/> Example:</br> https://dev-599740.okta.com/oauth2/default/.well-known/oauth-authorization-server</p>
          </td>
          <td>Optional</td>
          </tr>
          <tr class="even">
          <td>Issuer</td>
          <td>The issuer of the access tokens. <br/>Example:</br> https://dev-599740.okta.com/oauth2/default</td>
          <td>Mandatory (But optional if the Well-known URL is provided)</td>
          </tr>
          <tr class="odd">
          <td>Token Endpoint</td>
          <td>The endpoint that issues the access tokens.
          <br/> Example:</br> https://dev-599740.okta.com/oauth2/default/v1/token</p> </td>
          <td>Mandatory (But optional if the Well-known URL is provided)</td>
          </tr>
          <tr class="even">
          <td>PEM</td>
          <td>Either copy and paste the certificate in PEM format or upload the PEM file.</td>
          <td>Mandatory (But optional if the Well-known URL is provided)</td>
          </tr>
          <tr class="odd">
          <td>JWKS</td>
          <td>The JSON Web Key Set (JWKS) endpoint is a read-only endpoint. This URL returns the IdP's public key set in JSON web key set format.
          This contains the signing key(s) the Relying Party (RP) uses to validate signatures from the particular IdP.
          </br>
          Example:</br> https://dev-599740.okta.com/oauth2/default/v1/keys
          </td>
          <td>Mandatory (But optional if the Well-known URL is provided)</td>
          </tr>
          <tr class="even">
          <td>Alias</td>
          <td>This should be equal to the audience (`aud`) value of the JSON Web Token (JWT) issuer
          <br/> Example:</br> api://default</p> </td>
          <td>Mandatory</td>
          </tr>
          </table>

7. Click on **Add** button after filling the details.
