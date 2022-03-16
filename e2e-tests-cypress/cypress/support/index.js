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

import "cypress-file-upload";
import "cypress-xpath";
import "cypress-real-events/support";
import { GraphQL } from "../support/console/apis/graphql";
import { ChoreoHomePage } from "../support/console/pages/home/home-page";

Cypress.on("uncaught:exception", (err, runnable) => {
  return false;
});

Cypress.on("window:confirm", (err, runnable) => {
  return true;
});

Cypress.on("window:alert", (err, runnable) => {
  return true;
});

Cypress.on("window:before:load", (win) => {
  Object.defineProperty(win, "onbeforeunload", {
    value: undefined,
    writable: false,
  });
});

