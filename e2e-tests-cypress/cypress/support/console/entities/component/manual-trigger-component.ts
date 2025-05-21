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

import { Component } from "./component";
import { Enums } from "../../../commons/enums";
import { mixinBuild } from "../../features/component-build/build";
import { mixinServiceDeploy } from "../../features/deploy/deploy-service";
import { mixinExecute } from "../../features/execute/execute";
import { ConfigEntryStep, createDefaultSteps } from "../../../commons/types";

export class ManualTrigger extends mixinBuild(
  mixinExecute(mixinServiceDeploy(Component))
) {
  constructor(name: string) {
    super(name, "main");
  }

  build() {
    this._build(this);
  }

  deployToDevWithoutSplitButton(numberOfSteps: number = 2) {
    this._deployTask(this, createDefaultSteps(numberOfSteps));
  }

  deployToDevWithConfigs(configs: ConfigEntryStep[]) {
    this._deployTask(this, configs);
  }

  promoteToProd() {
    this._promoteTask(this);
  }

  promoteToProdWithConfigs(configs: ConfigEntryStep[]) {
    this._promoteTask(this, configs);
  }

  executeComponent(env: Enums.Environment) {
    this._execute(this, env);
  }

  verifyObservabilityMetricsLogs(env: Enums.Environment, matchingText: string) {
    this.observability.verifyObservabilityMetricsLogs(env, matchingText);
  }
}
