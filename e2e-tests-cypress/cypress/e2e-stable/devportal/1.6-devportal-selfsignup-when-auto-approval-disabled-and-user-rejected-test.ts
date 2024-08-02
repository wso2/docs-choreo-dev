/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
 * 
 * This software is the property of WSO2 LLC. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { UserDetails } from "../../support/commons/types";
import { Utils } from "../../support/commons/utils";
import { console } from "../../support/console/console";
import { devPortal } from "../../support/console/devportal";

const CUSTOM_DOMAIN = Cypress.env("devportalCustomDomain");

describe("Self signup when auto approval disabled and user rejected scenario", () => {

  it("Login to Console", () => {
    console.login();
  });

  it("Switch to self signup enabled org", () => {
    console.switchtOrg("choreoselfsignup");
  });

  it("Disable Dev portal self signup config", () => {
    console.disableDevportalSelfSignupAutoApprovalConfig();
  });

  it("Logout from Console", () => {
    console.logout();
  });

  it("Self signup user to Dev portal", () => {
    let userDetails: UserDetails = Utils.generateUserDetails();
    cy.task('setData', { key: 'userDetails', value: userDetails as UserDetails });

    devPortal.selfSignupToDevPortal(userDetails, CUSTOM_DOMAIN);
  });

  it("Check Dev portal access for the pending self signup user", () => {
    devPortal.checkDevPortalAccessForPendingUser();
  });

  it("Login to Console", () => {
    console.login();

    cy.task('getData', 'userDetails').then((userDetails) => {
      cy.task('setData', { key: 'userDetails', value: userDetails as UserDetails });
    });
  });

  it("Switch to self signup enabled org", () => {
    console.switchtOrg("choreoselfsignup");
  });

  it("Reject self signup request", () => {
    console.rejectDevportalSelfSignupRequest();
  });

  it("Logout from Console", () => {
    console.logout();

    cy.task('getData', 'userDetails').then((userDetails) => {
      cy.task('setData', { key: 'userDetails', value: userDetails as UserDetails });
    });
  });

  it("Attempt to signin rejected self signup user to Dev portal", () => {
    cy.task('getData', 'userDetails').then((userDetails) => {
      devPortal.signInToDevPortalWithRejectedUser(userDetails as UserDetails, CUSTOM_DOMAIN);
    });
  });
});
