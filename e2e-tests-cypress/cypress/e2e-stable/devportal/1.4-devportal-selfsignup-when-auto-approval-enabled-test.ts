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

describe("Self signup when auto approval enabled scenario", () => {

  const selfSignupOrg = Cypress.env("selfSignupOrgHandle");

  after(() => {
    console.logout();
  });
  
  it("Login to Console", () => {
    console.login();
  });

  it("Switch to self signup enabled org", () => {
    console.switchtOrg(selfSignupOrg).then(() => {;
      console.removePendingDevportalSelfSignupRequests();
    });
  });

  it("Enable Dev portal self signup auto approval config", () => {
    console.enableDevportalSelfSignupAutoApprovalConfig();
  });

  it("Logout from Console", () => {
    console.logout();
  });

  it("Self signup user to Dev portal", () => {
    let userDetails: UserDetails = Utils.generateUserDetails();
    devPortal.selfSignupToDevPortal(userDetails);
  });

  it("Check Dev portal access for the automatically approved self signup user", () => {
    devPortal.checkDevPortalAccessForApprovedUser();
  });
});
