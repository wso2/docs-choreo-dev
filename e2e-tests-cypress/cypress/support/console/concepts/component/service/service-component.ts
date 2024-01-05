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

import { Enums } from "../../../../commons/enums";
import { Component } from "../component";
import { _ServiceBuild } from "./service-build";
import {
  EndpointAccessibility,
  _ServiceDeployment,
} from "./service-deployment";
import { _ServiceManagement } from "./service-management";
import { UsagePlan } from "../../../../commons/enums";
import { _Stats } from "../stats";
import { InvokeInfo, _ServiceTest } from "./service-test";

export class Service extends Component {
  private endpointName: string;

  private build = new _ServiceBuild();
  private deployment = new _ServiceDeployment();
  private test = new _ServiceTest();
  private manage = new _ServiceManagement();

  constructor(name: string, endpointName: string) {
    super(name, "1.0");

    this.endpointName = endpointName;

    this.visitComponent(name);
  }

  getEndpointName() {
    return this.endpointName;
  }

  buildComponent() {
    this.build.build(this);
  }

  deployProjectLevelAccessibility() {
    this.deployment.deploy(this, EndpointAccessibility.Project);
  }

  deployPublicLevelAccessibility() {
    this.deployment.deploy(this, EndpointAccessibility.Public);
  }

  testConsole(invokeInfo: InvokeInfo) {
    return this.test.testConsole(this, invokeInfo);
  }

  promotePublicLevelAccessibility() {
    this.deployment.promote(this, EndpointAccessibility.Public);
  }

  addVersion() {
    this.deployment.addNewVersion(this);
  }

  publish() {
    this.manage.changeLifeCycleState(this, Enums.LifeCycleState.Publish);
  }

  updateUsagePlans(plans: UsagePlan[]) {
    this.manage.updateUsagePlans(this, plans);
  }

  enableCors() {
    this.manage.enableCors(this);
  }
}
