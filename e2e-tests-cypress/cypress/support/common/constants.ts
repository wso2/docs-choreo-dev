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

export const DAY = 86400000;
export const MARKETPLACE_TEXT = 'marketplace';
export const INTEGRATIONS_TEXT = 'integrations';
export const SERVICES_TEXT = 'services';
export const APIS_TEXT = 'apis';
export const DEVOPS_TEXT = 'devops';
export const SETTINGS_TEXT = 'settings';
export const SETTINGS_PATH = 'user-settings/organization/members';
export const dataMapperTestURL = 'https://datamapper.choreo.dev';
export const APP_SVC_URL: string = Cypress.env("appSvcURL");
export const APIM_RESOURCE_PATH = Cypress.env("apimBasePath") + "/apim/proxy/api/am/publisher/v2/apis";
export const SELECTED_ORG_HANDLE = Cypress.env("selectedOrgHandle");
export const SUCCESS_STATUS_CODE = 200;
export const STANDARD_TIME_OUT = 6000;
export const MEDIUM_TIME_OUT = 10000;
export const LONG_TIME_OUT = 60000;
export const EX_LONG_TIME_OUT = 180000;
export const DEPLOYMENT_TIME_OUT = 120000;
export const NO_OF_RETRIES = 2;
export const FAKE_TWILIO_ACCOUNT_SID = 'ACat9e5d3a348126a5fcabb03a03f1a1bb';
export const FAKE_TWILIO_TOKEN = 'd976402933e8a4143015c4499e971a65';
export const FAKE_TWILIO_SENDER_NUMBER = '+94786941431';
export const FAKE_TWILIO_RECIPIENT_NUMBER = '+94743149897';
export const INVITATION_EMAIL= 'test.user.choreo@gmail.com';
export const PATH_SEPARATOR = '/';
export const GMAIL_CONNECTION_NAME = 'test.user.choreo@gmail.com';
export const USER_CONNECTIONS_PATH = '/orgs/' + SELECTED_ORG_HANDLE + '/connections';
export const USER_CONFIGURATIONS_PATH = '/orgs/'+ SELECTED_ORG_HANDLE + '/configurations';
export const GOOGLE_CALENDAR_CONNECTOR = 'Google Calendar';
export const OPENWEATHERMAP_APPID = '1077cad615109804aeac077793122585';

//API Management
export const DEVELOP = '/develop';
export const OVERVIEW  = '/overview';
