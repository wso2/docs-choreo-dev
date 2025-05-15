// -------------------------------------------------------------------------------------
//
// Copyright (c) 2025, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
//
// This software is the property of WSO2 LLC and its suppliers, if any.
// Dissemination of any information or reproduction of any material contained
// herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
// You may not alter or remove any copyright or other notice from copies of this content.
//
// --------------------------------------------------------------------------------------
/* eslint-disable */

window.Settings = {
    clientId: {{ .Values.config.clientId | quote }},
    signInRedirectUrl: {{ .Values.config.signInRedirectUrl | quote }},
    signOutRedirectUrl: {{ .Values.config.signOutRedirectUrl | quote }},
    clientHost: {{ .Values.config.clientHost | quote }},
    apimHost: {{ .Values.config.apimHost | quote }},
    tokenExchangeUrl: {{ .Values.config.tokenExchangeUrl | quote }},
    apimApi: {{ .Values.config.apimApi | quote }},
    apimGatewayHostSuffix: {{ .Values.config.apimGatewayHostSuffix | quote }},
    idpHost: {{ .Values.config.idpHost | quote }},
    apiServiceHost: {{ .Values.config.apiServiceHost | quote }},
    graphqlServiceHost: {{ .Values.config.graphqlServiceHost | quote }},
    graphqlResource: {{ .Values.config.graphqlResource | quote }},
    azureCDNOrigin: {{ .Values.config.azureCDNOrigin | quote }},
    devToolEnable: {{ .Values.config.devToolEnable }},
    tokenEndpoint: {{ .Values.config.tokenEndpoint | quote }},
    revokeEndpoint: {{ .Values.config.revokeEndpoint | quote }},
    enableInV2: {{ .Values.config.enableInV2 }},
    choreoControlPlaneOrgHandleName: {{ .Values.global.choreosystem.orgHandle | quote }},
    googleFidp: "google",
    githubFidp: "github",
    microsoftFidp: "microsoft",
    emailFidp: 'LOCAL',
    enableEmailLogin: {{ .Values.config.enableEmailLogin }},
    enableMicrosoftLogin: {{ .Values.config.enableMicrosoftLogin }},
    overrideEndpoints: {
        authorizationEndpoint: {{ .Values.config.overrideEndpoints.authorizationEndpoint | quote }},
        tokenEndpoint: {{ .Values.config.overrideEndpoints.tokenEndpoint | quote }},
        endSessionEndpoint: {{ .Values.config.overrideEndpoints.endSessionEndpoint | quote }},
    },
    tokenExchangeConfig: {
        clientId: {{ .Values.config.tokenExchangeConfig.clientId | quote }},
        grantType: 'urn:ietf:params:oauth:grant-type:token-exchange',
        subjectTokenType: 'urn:ietf:params:oauth:token-type:jwt',
        requestedTokenType: 'urn:ietf:params:oauth:token-type:jwt',
        scope: {{ .Values.config.tokenExchangeConfig.scope | quote }},
    },
    azureInsightsKey: {{ .Values.config.azureInsightsKey | quote }},
    choreoSystemOrg: {{ .Values.global.choreosystem.orgHandle | quote }},
    choreoSystemHostSuffix: {{ .Values.config.choreoSystemHostSuffix | quote }},
    asgardeoConsoleUrl: {{ .Values.global.asgardeo.consoleUrl | quote }},
    devportalHost: {{ .Values.config.devportalHost | quote }},
    isCustomDomain: {{ .Values.config.customDomain.enabled }},
    customDomain: "",
    selfSignupEnabledOrgs: {{ .Values.config.selfSignupEnabledOrgs | quote }},
    pdpDomainCustomizedOrgs: {{ .Values.config.pdpDomainCustomizedOrgs }},
    permissionAdminPortalEnabled: {{ .Values.config.permissionAdminPortalEnabled }},
    permissionAdminPortalUrl: {{ .Values.config.permissionAdminPortalUrl | quote }},
    permissionPortalOrgIdParam: 'orgId',
    sandboxKeyGenerationEnabled: {{ .Values.config.sandboxKeyGenerationEnabled }},
    intelligentRoutingFeatureEnabled: {{ .Values.config.intelligentRoutingFeatureEnabled }},
    choreoInbuiltKeyManagerFeatureEnabled: {{ .Values.config.choreoInbuiltKeyManagerFeatureEnabled }},
    customSubscriptionPolicyHandlingOrg: {{ .Values.config.customSubscriptionPolicyHandlingOrg | quote }},
    apiTryoutTestKeyEnabled: {{ .Values.config.apiTryoutTestKeyEnabled }},
    apiKeyFeatureEnabled: {{ .Values.config.apiKeyFeatureEnabled }},
    devPortalIdPConfigurationFeatureEnabled: {{ .Values.config.devPortalIdPConfigurationFeatureEnabled }},
    apiKeyScopeSupportEnabled: {{ .Values.config.apiKeyScopeSupportEnabled }},
}

const currentHost = window.location.hostname;
const isCustomDomain = currentHost !== window.Settings.devportalHost;

if (isCustomDomain) {
    window.Settings = {
        ...window.Settings,
        idpHost: '',
        apiServiceHost: {{ .Values.config.customDomain.apiServiceHost | quote }},
        graphqlServiceHost: {{ .Values.config.customDomain.graphqlServiceHost | quote }},
        graphqlResource: '/projects/1.0.0/graphql',
        overrideEndpoints: {
            ...window.Settings.overrideEndpoints,
            tokenEndpoint: {{ .Values.config.customDomain.tokenEndpoint | quote }},
        },
        wellKnownEndpoint: {{ .Values.config.customDomain.wellKnownEndpoint | quote }},
        devportalHost: {{ .Values.config.customDomain.devportalHost | quote }},
        isCustomDomain: isCustomDomain,
        customDomain: currentHost,
        validateIDToken: false,
        permissionAdminPortalEnabled: true,
        permissionAdminPortalUrl: {{ .Values.config.customDomain.permissionAdminPortalUrl | quote }},
        permissionPortalOrgIdParam: 'orgId',
        intelligentRoutingFeatureEnabled: true,
        choreoInbuiltKeyManagerFeatureEnabled: false,
        customSubscriptionPolicyHandlingOrg: {{ .Values.config.customDomain.permissionAdminPortalUrl | quote }},
        apiTryoutTestKeyEnabled: {{ .Values.config.customDomain.apiTryoutTestKeyEnabled }},
        apiKeyFeatureEnabled: {{ .Values.config.customDomain.apiKeyFeatureEnabled }},
        devPortalIdPConfigurationFeatureEnabled: {{ .Values.config.customDomain.devPortalIdPConfigurationFeatureEnabled }},
    }
}
