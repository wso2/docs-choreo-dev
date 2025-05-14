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
    clientId: {{ required "A valid .choreo-apim.apim-devportal.config.clientId entry required!" .Values.config.clientId | quote }},
    signInRedirectUrl: {{ required "A valid .choreo-apim.apim-devportal.config.signInRedirectUrl entry required!" .Values.config.signInRedirectUrl | quote }},
    signOutRedirectUrl: {{ required "A valid .choreo-apim.apim-devportal.config.signOutRedirectUrl entry required!" .Values.config.signOutRedirectUrl | quote }},
    clientHost: {{ required "A valid .choreo-apim.apim-devportal.config.clientHost entry required!" .Values.config.clientHost | quote }},
    apimHost: {{ required "A valid .choreo-apim.apim-devportal.config.apimHost entry required!" .Values.config.apimHost | quote }},
    tokenExchangeUrl: {{ required "A valid .choreo-apim.apim-devportal.config.tokenExchangeUrl entry required!" .Values.config.tokenExchangeUrl | quote }},
    apimApi: {{ required "A valid .choreo-apim.apim-devportal.config.apimApi entry required!" .Values.config.apimApi | quote }},
    apimGatewayHostSuffix: {{ required "A valid .choreo-apim.apim-devportal.config.apimGatewayHostSuffix entry required!" .Values.config.apimGatewayHostSuffix | quote }},
    idpHost: {{ required "A valid .choreo-apim.apim-devportal.config.idpHost entry required!" .Values.config.idpHost | quote }},
    apiServiceHost: {{ required "A valid .choreo-apim.apim-devportal.config.apiServiceHost entry required!" .Values.config.apiServiceHost | quote }},
    graphqlServiceHost: {{ required "A valid .choreo-apim.apim-devportal.config.graphqlServiceHost entry required!" .Values.config.graphqlServiceHost | quote }},
    graphqlResource: {{ required "A valid .choreo-apim.apim-devportal.config.graphqlResource entry required!" .Values.config.graphqlResource | quote }},
    azureCDNOrigin: {{ required "A valid .choreo-apim.apim-devportal.config.azureCDNOrigin entry required!" .Values.config.azureCDNOrigin | quote }},
    devToolEnable: {{ required "A valid .choreo-apim.apim-devportal.config.devToolEnable entry required!" .Values.config.devToolEnable }},
    tokenEndpoint: {{ required "A valid .choreo-apim.apim-devportal.config.tokenEndpoint entry required!" .Values.config.tokenEndpoint | quote }},
    revokeEndpoint: {{ required "A valid .choreo-apim.apim-devportal.config.revokeEndpoint entry required!" .Values.config.revokeEndpoint | quote }},
    enableInV2: {{ required "A valid .choreo-apim.apim-devportal.config.enableInV2 entry required!" .Values.config.enableInV2 }},
    choreoControlPlaneOrgHandleName: {{ required "A valid .global.choreosystem.orgHandle entry required!" .Values.global.choreosystem.orgHandle | quote }},
    googleFidp: "google",
    githubFidp: "github",
    microsoftFidp: "microsoft",
    emailFidp: 'LOCAL',
    enableEmailLogin: {{ required "A valid .choreo-apim.apim-devportal.config.enableEmailLogin entry required!" .Values.config.enableEmailLogin }},
    enableMicrosoftLogin: {{ required "A valid .choreo-apim.apim-devportal.config.enableMicrosoftLogin entry required!" .Values.config.enableMicrosoftLogin }},
    overrideEndpoints: {
        authorizationEndpoint: {{ required "A valid .choreo-apim.apim-devportal.config.overrideEndpoints.authorizationEndpoint entry required!" .Values.config.overrideEndpoints.authorizationEndpoint | quote }},
        tokenEndpoint: {{ required "A valid .choreo-apim.apim-devportal.config.overrideEndpoints.tokenEndpoint entry required!" .Values.config.overrideEndpoints.tokenEndpoint | quote }},
        endSessionEndpoint: {{ required "A valid .choreo-apim.apim-devportal.config.overrideEndpoints.endSessionEndpoint entry required!" .Values.config.overrideEndpoints.endSessionEndpoint | quote }},
    },
    tokenExchangeConfig: {
        clientId: {{ required "A valid .choreo-apim.apim-devportal.config.tokenExchangeConfig.clientId entry required!" .Values.config.tokenExchangeConfig.clientId | quote }},
        grantType: 'urn:ietf:params:oauth:grant-type:token-exchange',
        subjectTokenType: 'urn:ietf:params:oauth:token-type:jwt',
        requestedTokenType: 'urn:ietf:params:oauth:token-type:jwt',
        scope: {{ required "A valid .choreo-apim.apim-devportal.config.tokenExchangeConfig.scope entry required!" .Values.config.tokenExchangeConfig.scope | quote }},
    },
    azureInsightsKey: {{ required "A valid .choreo-apim.apim-devportal.config.azureInsightsKey entry required!" .Values.config.azureInsightsKey | quote }},
    choreoSystemOrg: {{ required "A valid .global.choreosystem.orgHandle entry required!" .Values.global.choreosystem.orgHandle | quote }},
    choreoSystemHostSuffix: {{ required "A valid .choreo-apim.apim-devportal.config.choreoSystemHostSuffix entry required!" .Values.config.choreoSystemHostSuffix | quote }},
    asgardeoConsoleUrl: {{ required "A valid .global.asgardeo.consoleUrl entry required!" .global.asgardeo.consoleUrl | quote }},
    devportalHost: {{ required "A valid .choreo-apim.apim-devportal.config.devportalHost entry required!" .Values.config.devportalHost | quote }},
    isCustomDomain: {{ required "A valid .choreo-apim.apim-devportal.config.customDomain.enabled entry required!" .Values.config.customDomain.enabled }},
    customDomain: "",
    selfSignupEnabledOrgs: {{ required "A valid .choreo-apim.apim-devportal.config.selfSignupEnabledOrgs entry required!" .Values.config.selfSignupEnabledOrgs | quote }},
    pdpDomainCustomizedOrgs: {{ required "A valid .choreo-apim.apim-devportal.config.pdpDomainCustomizedOrgs entry required!" .Values.config.pdpDomainCustomizedOrgs }},
    permissionAdminPortalEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.permissionAdminPortalEnabled entry required!" .Values.config.permissionAdminPortalEnabled }},
    permissionAdminPortalUrl: {{ required "A valid .choreo-apim.apim-devportal.config.permissionAdminPortalUrl entry required!" .Values.config.permissionAdminPortalUrl | quote }},
    permissionPortalOrgIdParam: 'orgId',
    sandboxKeyGenerationEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.sandboxKeyGenerationEnabled entry required!" .Values.config.sandboxKeyGenerationEnabled }},
    intelligentRoutingFeatureEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.intelligentRoutingFeatureEnabled entry required!" .Values.config.intelligentRoutingFeatureEnabled }},
    choreoInbuiltKeyManagerFeatureEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.choreoInbuiltKeyManagerFeatureEnabled entry required!" .Values.config.choreoInbuiltKeyManagerFeatureEnabled }},
    customSubscriptionPolicyHandlingOrg: {{ required "A valid .choreo-apim.apim-devportal.config.customSubscriptionPolicyHandlingOrg entry required!" .Values.config.customSubscriptionPolicyHandlingOrg | quote }},
    apiTryoutTestKeyEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.apiTryoutTestKeyEnabled entry required!" .Values.config.apiTryoutTestKeyEnabled }},
    apiKeyFeatureEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.apiKeyFeatureEnabled entry required!" .Values.config.apiKeyFeatureEnabled }},
    devPortalIdPConfigurationFeatureEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.devPortalIdPConfigurationFeatureEnabled entry required!" .Values.config.devPortalIdPConfigurationFeatureEnabled }},
    apiKeyScopeSupportEnabled: {{ required "A valid .choreo-apim.apim-devportal.config.apiKeyScopeSupportEnabled entry required!" .Values.config.apiKeyScopeSupportEnabled }},
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
