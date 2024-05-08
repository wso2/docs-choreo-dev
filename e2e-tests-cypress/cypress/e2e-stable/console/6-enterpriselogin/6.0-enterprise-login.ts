/*

Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
This software is the property of WSO2 Inc. and its suppliers, if any.
Dissemination of any information or reproduction of any material contained
herein is strictly forbidden, unless permitted by WSO2 in accordance with
the WSO2 Commercial License available at http://wso2.com/licenses.
For specific language governing the permissions and limitations under
this license, please see the license as well as any agreement you’ve
entered into with WSO2 governing the purchase of this software and any
associated services.
*/

import { console } from "../../../support/console/console";

describe("Enterprise Login using auth0Idp", () => {
  it("Enterprise login to console", () => {
    console.enterpriseLogin();

    console.getDevPortalUrl().then((url) => {
      if (url) {
        // Since we are switching domains when navigating to enterprise DevPortal domain url we will no longer have access to the data
        // generated in the Console, so we store the DevPortal link from the Console in nodejs global state using below cy.task() to access it later
        cy.task("setData", {
          key: Cypress.spec.name, // Unique key to store the data, in this case spec name is sufficient
          value: url,
        });
      } else {
        throw new Error("Devportal link not found");
      }
    });
  });

  it("Verify devportal sso login", () => {
    cy.task("getData", Cypress.spec.name).then((url) => {
      if (url) {
        console.navigateToEnterpriseDevPortal(url as string);
      } else {
        throw new Error("Data not found");
      }
    });
  });
});
