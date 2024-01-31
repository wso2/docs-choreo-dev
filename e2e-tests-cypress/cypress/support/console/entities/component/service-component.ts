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

import { EndpointAccessibility, Enums } from "../../../commons/enums";
import { Component } from "./component";

import { UsagePlan } from "../../../commons/enums";
import { mixinBuild } from "../../features/component-build/build";
import { InvokeInfo, mixinTestService } from "../../features/test/test-service";
import { mixinServiceDeploy } from "../../features/deploy/deploy-service";
import { mixinManage } from "../../features/manage/manage";
import { ConfigEntryStep, createDefaultSteps } from "../../../commons/types";

export class Service extends mixinBuild(
  mixinManage(mixinServiceDeploy(mixinTestService(Component)))
) {
  private endpointName: string;

  constructor(name: string, endpointName: string) {
    super(name, "1.0");

    this.endpointName = endpointName;

    this.visitComponent(name);
  }

  getEndpointName() {
    return this.endpointName;
  }

  build() {
    this._build(this);
  }

  deployProjectLevelAccessibility(shouldModifyEndpoint: boolean = true) {
    this._deployService(
      this,
      shouldModifyEndpoint,
      EndpointAccessibility.Project,
      createDefaultSteps(1)
    );
  }

  deployPublicLevelAccessibility(shouldModifyEndpoint: boolean = true) {
    this._deployService(
      this,
      shouldModifyEndpoint,
      EndpointAccessibility.Public,
      createDefaultSteps(1)
    );
  }

  testConsole(invokeInfo: InvokeInfo) {
    return this._testConsole(this, invokeInfo);
  }

  promoteProjectLevelAccessibility(shouldModifyEndpoint: boolean = true) {
    this._promoteService(
      this,
      shouldModifyEndpoint,
      EndpointAccessibility.Project
    );
  }

  promotePublicLevelAccessibility(
    configSteps?: ConfigEntryStep[],
    shouldModifyEndpoint: boolean = true
  ) {
    this._promoteService(
      this,
      shouldModifyEndpoint,
      EndpointAccessibility.Public,
      configSteps
    );
  }

  addVersion() {
    this._addNewVersion(this, "feature", "1.1");
  }

  publish() {
    this._changeLifeCycleState(this, Enums.LifeCycleState.Publish);
  }

  updateUsagePlans(plans: UsagePlan[]) {
    this._updateUsagePlans(this, plans);
  }

  enableCors() {
    this._enableCors(this);
  }
}
