import { is } from "cypress/types/bluebird";
import { cyGet, cyLog } from "../../../commons/cy";
import { MEDIUM_TIME, MENU_RENDERING_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";

/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

export class ComponentDevOpsPage {
    
  static navigateToDeploy() {
    cy.get("[data-cyid=link-deploy]")
      .should("be.visible")
      .realHover({ position: "left" })
      .wait(MENU_RENDERING_TIME)
      .click()
      .wait(MENU_RENDERING_TIME);
    cy.get('[id="backdrop-loader"]').should("not.exist");
    Utils.moveMouseAwayFromLeftMenu();
  }
}
