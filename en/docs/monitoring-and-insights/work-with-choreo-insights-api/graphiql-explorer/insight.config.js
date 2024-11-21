window.__RUNTIME_CONFIG__ = {
    ASGARDEO_CONFIG: {
        endpoints: {
            authorizationEndpoint: 'https://api.asgardeo.io/t/a/oauth2/authorize',
            tokenEndpoint: 'https://api.asgardeo.io/t/a/oauth2/token',
            endSessionEndpoint: 'https://api.asgardeo.io/t/a/oidc/logout'
        },
        signInRedirectURL: 'https://wso2.com/choreo/docs/monitoring-and-insights/work-with-choreo-insights-api/graphiql-explorer/',
        signOutRedirectURL: 'https://wso2.com/choreo/docs/monitoring-and-insights/work-with-choreo-insights-api/graphiql-explorer/',
        clientID: 'BDXalD9exTskS5bt7xYfIK5TsnIa'
    },
    TOKEN_EXCHANGE_CONFIG: {
        tokenEndpoint: 'https://sts.choreo.dev:443/oauth2/token',
        data: {
            client_id: 'hNq2_2JV9d44DgT7Wv1OHBLTO1sa',
            grant_type: 'urn:ietf:params:oauth:grant-type:token-exchange',
            subject_token_type: 'urn:ietf:params:oauth:token-type:jwt',
            requested_token_type: 'urn:ietf:params:oauth:token-type:jwt',
            scope: 'apim:admin'
        }
    },
    INSIGHTS_API_URL: 'https://choreocontrolplane.choreo.dev/93tu/insights/1.0.0/query-api',
    VALIDATE_USER_ENDPOINT: 'https://apis.choreo.dev/user-mgt/1.0.0/validate/user'
}
