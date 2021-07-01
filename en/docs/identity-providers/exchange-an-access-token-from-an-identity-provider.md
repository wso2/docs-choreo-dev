# Exchange an Access Token from an Identity Provider (IdP)

After you have [Register an External Identity Provider (IdP)]({{base_path}}/identity-providers/register-an-external-identity-provider) you can exchange access tokens from that IdP to Choreo access tokens and use those to invoke APIs. 

Follow this procedure to exchange an access token from an external IdP in your Choreo organization.

1. Sign in to the Choreo Developer Portal at [https://devportal.choreo.dev/](https://devportal.choreo.dev/).

2. Click **Applications** tab.

3. [Create a New Application]() or use an existing application.

4. Select **OAuth2 Tokens** under the **Production Keys** on the left bar of the application.

5. If the Consumer Key and the Consumer Secret have not been generated before, the following page will be displayed. Click on **Generate Keys** to generate the Consumer Key and the Consumer Secret.

    ![OAuth2 Tokens](){.cInlineImage-full}

    !!! tip
        The **User Keys** tab will be disabled if you try to access it before generating the keys or if you try to generate the keys without enabling the **Token Exchange** grant type. Hence, you need to make sure the **Token Exchange** grant type is enabled when generating the keys. You can click on **Advanced Configurations** to check whether the **Token Exchange** grant type is enabled (By default this value will be enabled). 
        
        ![OAuth2 Tokens Expaned Advanced Configurations](){.cInlineImage-full}

6. Click on the **User Keys**.

    ![User Keys Tab](){.cInlineImage-full}

7. Select an IdP from the dropdown under the field **Identity Provider**.

    ![User Keys Tab Dropdown](){.cInlineImage-full}

8. Now the following screen will be displayed which includes two (2) steps.

    ![User Keys Full Page](){.cInlineImage-full}

9. As mentioned in **Step 1**, first you need to obtain an access token from the external IdP that you selected.
     Refer to the **documentation** pointed from the same step to get an access token from your choice of IdP.
     
10. To obtain a Choreo access token as mentioned in the **Step 2**, you need to paste the above obtained token from your IdP in the text box in front of the field **<Your_IdP_name> Token** (Example: Okta Token field). 

11. You can click on the button **Curl** to get the cURL command to obtain the Choreo access token or you can click on the button named **Generate Test Token** to get the Choreo access token from the UI.

12. You can use this generated Choreo access token to invoke APIs as you wish.
