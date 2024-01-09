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

import { BUILD_SUCCESS } from "../../../commons/constants";
import { LONG_TIME } from "../../../commons/timeouts";
import { TestIds } from "../../constants/TestIds";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { Service } from "../../concepts/component/service/service-component";
import { Types } from "../../../commons/types";
import { DeploymentTrack } from "../deployment-track/deployment-track";

export interface BuildFeature {
  _build(component: Service): void;
}

export function mixinBuild<T extends Types.Constructor>(
  base: T
): Types.Constructor<BuildFeature> & T {
  return class extends base {
    private sideMenu = new ServiceLeftMenu();
    private deploymentTrack = new DeploymentTrack();

    _build(component: Service) {
      this.sideMenu.navigateToBuild();

      this.triggerBuild(component);
    }

    private triggerBuild(component: Service) {
      this.deploymentTrack.validate(component);

      cy.get(TestIds.build).should("be.enabled").click();
      cy.get(TestIds.next).should("be.visible").click();
      cy.get(TestIds.tableTitle).within(() => {
        cy.contains(BUILD_SUCCESS, LONG_TIME);
      });
    }
  };
}
