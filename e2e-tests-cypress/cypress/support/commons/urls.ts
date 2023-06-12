/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

export const VALIDATE_USER_URL =
  Cypress.env("newAppSvcURL") + "/users-mgt/1.0.0/validate/user";
export const ORGS_URL = Cypress.env("appSvcURL") + "/orgs/*";
export const PUBLISHER_URL = Cypress.env("apimSvcURL") + "/api/am/publisher/v2";
export const PUBLISHER_API_KEYS_URL = PUBLISHER_URL + "/apis/*/environments/*/keys?organizationId=*";
export const DEV_PORTAL_URL = Cypress.env("apimSvcURL") + "/api/am/devportal/v2";
export const DEV_PORTAL_APP_TOKEN_GEN_URL = DEV_PORTAL_URL + "/applications/*/oauth-keys/*/generate-token?organizationId=*";
export const DEV_PORTAL_APP_KEY_GEN_URL =
  DEV_PORTAL_URL + "/applications/*/generate-keys?organizationId=*";
export const DEV_PORTAL_SUBSCRIPTIONS_URL =
  DEV_PORTAL_URL + "/subscriptions/?apiId=*&organizationId=*";
export const GRAPHQL_URL =
  Cypress.env("newAppSvcURL") + "/projects/1.0.0/graphql";
export const EP_USER_HOME_URL = `${Cypress.env(
  "baseUrl"
)}/organizations/${Cypress.env("epuser")}/home?profile=default`;

export const PROXY_DEPLOYER_EP = Cypress.env("proxyDeployerEP")
