/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */
/* eslint-disable */

window.Settings = {
    clientId: '{{.CLIENT_ID}}',
    signInRedirectUrl: '{{.SIGN_IN_REDIRECT_URL}}',
    signOutRedirectUrl: '{{.SIGN_OUT_REDIRECT_URL}}',
    clientHost: '{{.CLIENT_HOST}}',
    apimHost: '{{.APIM_HOST}}',
    tokenExchangeUrl: '{{.TOKEN_EXCHANGE_URL}}',
    apimApi: '{{.APIM_API}}',
    apimGatewayHostSuffix: '{{.APIM_GATEWAY_HOST_SUFFIX}}',
    idpHost: '{{.IDP_HOST}}',
    apiServiceHost: '{{.API_SERVICE_HOST}}',
    graphqlServiceHost: '{{.GRAPHQL_SERVICE_HOST}}',
    graphqlResource: '{{.GRAPHQL_RESOURCE}}',
    azureCDNOrigin: '{{.AZURE_CDN_ORIGIN}}',
    devToolEnable: {{.DEV_TOOL_ENABLE}},
    tokenEndpoint: '{{.TOKEN_ENDPOINT}}',
    revokeEndpoint: '{{.REVOKE_ENDPOINT}}',
    enableInV2: {{.ENABLE_IN_V2}},
    choreoControlPlaneOrgHandleName: 'choreosystem',
    googleFidp: "google",
    githubFidp: "github",
    microsoftFidp: "microsoft",
    emailFidp: 'LOCAL',
    enableEmailLogin: {{.ENABLE_EMAIL_LOGIN}},
    enableMicrosoftLogin: {{.ENABLE_MICROSOFT_LOGIN}},
    overrideEndpoints: {
        authorizationEndpoint: '{{.OVERRIDE_AUTHORIZATION_ENDPOINT}}',
        tokenEndpoint: '{{.OVERRIDE_TOKEN_ENDPOINT}}',
        endSessionEndpoint: '{{.OVERRIDE_END_SESSION_ENDPOINT}}',
    },
    tokenExchangeConfig: {
        clientId: '{{.TOKEN_EXCHANGE_CLIENT_ID}}',
        grantType: 'urn:ietf:params:oauth:grant-type:token-exchange',
        subjectTokenType: 'urn:ietf:params:oauth:token-type:jwt',
        requestedTokenType: 'urn:ietf:params:oauth:token-type:jwt',
        scope: 'apim:admin apim:subscribe environments:view_prod environments:view_dev apim:prod_key_manage apim:sand_key_manage urn:choreosystem:customdomainapi:custom_domain_view urn:choreosystem:usermanagement:user_view',
    },
    azureInsightsKey: '{{.AZURE_INSIGHTS_KEY}}',
    choreoSystemOrg: '{{.CHOREO_SYSTEM_ORG}}',
    choreoSystemHostSuffix: '{{.CHOREO_SYSTEM_HOST_SUFFIX}}',
    asgardeoConsoleUrl: '{{.ASGARDEO_CONSOLE_URL}}',
    devportalHost: "{{.DEVPORTAL_HOST}}",
    isCustomDomain: {{.IS_CUSTOM_DOMAIN}},
    customDomain: "",
    selfSignupEnabledOrgs: {{.SELF_SIGNUP_ENABLED_ORGS}},
    pdpDomainCustomizedOrgs: {{.PDP_DOMAIN_CUSTOMIZED_ORGS}},
    permissionAdminPortalEnabled: {{.PERMISSION_ADMIN_PORTAL_ENABLED}},
    permissionAdminPortalUrl: '{{.PERMISSION_ADMIN_PORTAL_URL}}',
    permissionPortalOrgIdParam: 'orgId',
    sandboxKeyGenerationEnabled: {{.SANDBOX_KEY_GENERATION_ENABLED}},
    intelligentRoutingFeatureEnabled: {{.INTELLIGENT_ROUTING_FEATURE_ENABLED}},
    choreoInbuiltKeyManagerFeatureEnabled: {{.CHOREO_INBUILT_KEY_MANAGER_FEATURE_ENABLED}},
    customSubscriptionPolicyHandlingOrg: '{{.CUSTOM_SUBSCRIPTION_POLICY_HANDLING_ORG | default ""}}',
    apiTryoutTestKeyEnabled: '{{.API_TRYOUT_TEST_KEY_ENABLED | default "false"}}',
    apiKeyFeatureEnabled: '{{.API_KEY_FEATURE_ENABLED | default "false"}}',
}

const currentHost = window.location.hostname;
const isCustomDomain = currentHost !== window.Settings.devportalHost;

if (isCustomDomain) {
    window.Settings = {
        ...window.Settings,
        idpHost: '',
        apiServiceHost: '{{.CUSTOM_DOMAIN_API_SERVICE_HOST}}',
        graphqlServiceHost: '{{.CUSTOM_DOMAIN_GRAPHQL_SERVICE_HOST}}',
        graphqlResource: '/projects/1.0.0/graphql',
        overrideEndpoints: {
            ...window.Settings.overrideEndpoints,
            tokenEndpoint: '{{.CUSTOM_DOMAIN_OVERRIDE_TOKEN}}',
        },
        wellKnownEndpoint: '{{.CUSTOM_DOMAIN_WELL_KNOWN_ENDPOINT}}',
        devportalHost: "{{.CUSTOM_DOMAIN_DEVPORTAL_HOST}}",
        isCustomDomain: isCustomDomain,
        customDomain: currentHost,
        validateIDToken: false,
        permissionAdminPortalEnabled: true,
        permissionAdminPortalUrl: '{{.CUSTOM_DOMAIN_PERMISSION_ADMIN_PORTAL_URL}}',
        permissionPortalOrgIdParam: 'orgId',
        intelligentRoutingFeatureEnabled: true,
        choreoInbuiltKeyManagerFeatureEnabled: false,
        customSubscriptionPolicyHandlingOrg: '{{.CUSTOM_SUBSCRIPTION_POLICY_HANDLING_ORG | default ""}}',
        apiTryoutTestKeyEnabled: '{{.API_TRYOUT_TEST_KEY_ENABLED | default "false"}}',
        apiKeyFeatureEnabled: '{{.API_KEY_FEATURE_ENABLED | default "false"}}',
    }
}
