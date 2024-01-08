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

import { Component } from "../component";
import { _ServiceBuild } from "./service-build";
import {_ServiceDeployment,
} from "./service-deployment";
import { _ServiceManagement } from "./service-management";
import { _Stats } from "../stats";
import { _ManualTriggerBuild } from "./manualTrigger-build";
import { _ManualTriggerDeployment } from "./manualTrigger-deployment";
import { _ManualTriggerExecute } from "./manualTrigger-execute";
import { Enums } from "../../../../commons/enums";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";


export class ManualTrigger extends Component {

  private build = new _ManualTriggerBuild();
  private deployment = new _ManualTriggerDeployment();
  private execute = new _ManualTriggerExecute();
  protected sideMenu = new ServiceLeftMenu();

  constructor(name: string) {
   
    super(name, "1.0");
    this.visitComponent(name);
  }

  buildComponent() {
    this.build.build(this);
  }

  deployToDevWithoutSplitButton() {
    this.deployment.deploy(this);
  }

  promoteProd() {
    this.deployment.promote(this);
  }

  executeComponent(env: Enums.Environment) {
    this.sideMenu.navigateToExecute();
    this.selectEnvironment(env);
    this.execute.execute(this);
  }
  
  selectEnvironment(env: Enums.Environment) {
    cy.get('[data-cyid="environment-picker"]').should("be.visible").scrollIntoView().click();
    cy.get(`[data-cyid="environment-picker-${env}"]`).click();
  }


}
