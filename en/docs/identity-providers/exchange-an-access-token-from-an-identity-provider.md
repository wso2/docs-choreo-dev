# Exchange an Access Token from an Identity Provider (IdP)

After you have [Register an External Identity Provider (IdP)]({{base_path}}/identity-providers/register-an-external-identity-provider) you can exchange access tokens from that IdP with Choreo access tokens and use those to invoke APIs. 

Follow this procedure to exchange an access token from an external IdP in your Choreo organization.

1. Sign in to the Choreo Developer Portal at [https://devportal.choreo.dev/](https://devportal.choreo.dev/).

2. Click **Applications** tab.

3. [Create a New Application]() or use an existing application.

4. Select **OAuth2 Tokens** under the **Production Keys** on the left bar of the application.

5. If the Consumer Key and the Consumer Secret have not been generated before, the following page will be displayed. Click on **Generate Keys** to generate the Consumer Key and the Consumer Secret.

    ![OAuth2 Tokens](){.cInlineImage-full}
    
    !!! note
        The tab named **Identity Providers** will look like below if you navigate to it directly before generating the Consumer Key and the Consumer Secret. Hence, in order to view the existing IdPs available in your Choreo organization (in the next step - Step 6) you need to generate the Consumer Key and the Consumer Secret as mentioned earlier.

        ![Identity Providers Tab Before Generating Keys](){.cInlineImage-full}

6. Click on the **Identity Providers** tab of the application.

    ![Identity Providers Tab](){.cInlineImage-full}

7. Select an IdP from the dropdown under the field **Identity Provider**. Now the following screen will be displyed which includes two (2) steps.

    ![Identity Providers Tab Dropdown](){.cInlineImage-full}

8. As mentioned in the **Step 1**, first you need to obtain an access token from the external IdP.
     Refer to [Obtain an Access Token from Your External Identity Provider]() to get an access token from your choice of IdP. 
     
9. To obtain the Choreo access token as mentioned in the **Step 2** you need to paste the above obtained token from your IdP in the text box in front of **External Identity Provider Token** field. 

10. You can click on the button **CURL TO GENERATE CHOREO ACCESS TOKEN** to get the cURL command to obtain the Choreo access token or you can click on the button named **GENERATE CHOREO ACCESS TOKEN** to get the Choreo access token from the UI.

11. You can use this generated Choreo access token to invoke APIs as you wish.
