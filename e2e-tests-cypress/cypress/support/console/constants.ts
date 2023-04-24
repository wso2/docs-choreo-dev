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

export const ONE_HOUR = 60 * 60 * 1000;
export const VERY_SHORT_TIME = 10000; // 10 seconds
export const SHORT_TIME = 60000; // 1 minute
export const MEDIUM_TIME = 180000; // 3 minutes
export const LONG_TIME = 360000; // 6 minutes
export const VERY_LONG_TIME = 600000; // 10 minutes

export const DEPLOYMENT_SUCCESS = "Active";
export const DEPLOYMENT_STOPPED = "Suspended";

export const VALIDATE_USER_URL =
  Cypress.env("newAppSvcURL") + "/validation-mgt/1.0.0/validate-user";

export const ORGS_URL = Cypress.env("appSvcURL") + "/orgs/*";

export const DEVPORTAL_URL = Cypress.env("apimSvcURL") + "/api/am/devportal/v2";

export const DEVPORTAL_APP_TOKEN_GEN_URL =
  DEVPORTAL_URL +
  "/applications/*/oauth-keys/*/generate-token?organizationId=*";

export const DEVPORTAL_APP_KEY_GEN_URL =
  DEVPORTAL_URL + "/applications/*/generate-keys?organizationId=*";
