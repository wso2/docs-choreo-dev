/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { cyLog } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { TestIds } from "../../constants/TestIds";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";

export class _Observability {
  private sideMenu = new ServiceLeftMenu();

  viewObservabilityMetrics(env: Enums.Environment) {
    this.sideMenu.navigateToObserve();
    this.verifyObservabilityMetrics("ballerina: sending metrics to Choreo");
  }


  private verifyObservabilityMetrics(text: string) {
    cy.get(TestIds.observabilityLogPanelEntry).should("be.visible").each(($e) => {
      let log = $e
        .text()
        .replace("ballerina: sending metrics to Choreo", "")
        .trim()
        .toString();

      cyLog(log);

      if (log.includes(text)) {
        const exactText = log.slice(log.indexOf("{"), log.indexOf("}") + 1);
        cyLog(exactText);
        expect(text).to.be.eq(exactText);
      }
    });
}
    


  }


  








