/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { TestIds } from "../../constants/TestIds";
import { Enums } from "../../../commons/enums";

export class OrganizationSettings {
  addUserStore(userStoreFile: string, env: Enums.Environment) {
    cy.get(TestIds.orgAppSecurity).click();

    cy.get(TestIds.builtInIdpCard).within(() => {
      cy.get(TestIds.linkBtn).click();
    });

    cy.get(TestIds.choreoIdpEnvs).should("be.visible");

    cy.get("body").then((bdy) => {
      if (bdy.find(TestIds.idpEnv(env)).length > 0) {
        cy.get(TestIds.idpEnv(env)).should("be.visible").click();
      } else {
        cy.get(TestIds.idpEnvUS(env)).should("be.visible").click();
      }
    });

    cy.get(TestIds.tableTitle).find("tbody tr").should("have.length", 1);

    cy.fixture(userStoreFile).as("users");
    cy.get(TestIds.uploadUserStoreCard).within(() => {
      cy.get('input[type="file"]').selectFile("@users", { force: true });
    });

    cy.get(TestIds.uploadUserStoreFile).click();
  }
}
