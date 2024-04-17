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

export const USER_MGT_URL = Cypress.env("newAppSvcURL") + "/user-mgt/1.0.0";
export const USER_STORE_MGT_URL =
  Cypress.env("newAppSvcURL") + "/user-store-mgt/v1.0/user-stores";
export const VALIDATE_USER_URL = USER_MGT_URL + "/validate/user";
export const USER_ORGS_URL = USER_MGT_URL + "/orgs";
export const PUBLISHER_URL = Cypress.env("apimSvcURL") + "/api/am/publisher/v2";
export const PUBLISHER_API_KEYS_URL =
  PUBLISHER_URL + "/apis/*/environments/*/keys?organizationId=*";
export const DEV_PORTAL_URL =
  Cypress.env("apimSvcURL") + "/api/am/devportal/v2";
export const DEV_PORTAL_APIS_SEARCH_URL = (name?: string) => {
  if (name !== undefined) {
    return `${DEV_PORTAL_URL}/apis?query=name:${name}&*`;
  } else {
    return `${DEV_PORTAL_URL}/apis?organizationId=*`;
  }
};
export const DEV_PORTAL_APP_TOKEN_GEN_URL =
  DEV_PORTAL_URL +
  "/applications/*/oauth-keys/*/generate-token?organizationId=*";
export const DEV_PORTAL_APP_KEY_GEN_URL =
  DEV_PORTAL_URL + "/applications/*/generate-keys?organizationId=*";
export const DEV_PORTAL_SUBSCRIPTIONS_URL =
  DEV_PORTAL_URL + "/subscriptions/?apiId=*&organizationId=*";
export const GRAPHQL_URL =
  Cypress.env("newAppSvcURL") + "/projects/1.0.0/graphql";
export const EP_USER_HOME_URL = `${Cypress.env(
  "baseUrl"
)}/organizations/${Cypress.env("epuser")}/home?profile=default`;

export const PROXY_DEPLOYER_EP = Cypress.env("proxyDeployerEP");

export const DOMAIN_URL_MGT =
  Cypress.env("newAppSvcURL") + "/url-mgt/v1.0/domains";

export const CONNECTIONS_URL_CONFIG =
  Cypress.env("newAppSvcURL") +
  "/connections/v1/configurations/service-configs/choreo-connections?generateCreds=true";
