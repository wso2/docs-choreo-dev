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

import { Enums } from "../../../commons/enums";
import { ConfigEntryStep, createDefaultSteps } from "../../../commons/types";
import { mixinBuild } from "../../features/component-build/build";
import { mixinServiceDeploy } from "../../features/deploy/deploy-service";
import { mixinTestProxy } from "../../features/test/test-proxy";
import { InvokeInfo, mixinTestService } from "../../features/test/test-service";
import { Component } from "./component";

export class Byoc extends mixinBuild(mixinTestProxy(mixinServiceDeploy(mixinTestService(Component)))) {
 
  
  constructor(name: string) {
    super(name, "main");

    this.visitComponent(name);
  }

  build() {
    this._build(this);
  }

  deployToDev() {
    this._deployTask(this, createDefaultSteps(2));
  }

deployToDevWithConfigs(configs: ConfigEntryStep[]) {
this._deployWebhook(this, configs);
}


  testSwaggerConsole(
    environment: Enums.Environment,
    resource: string,
    key?: string,
    value?: string
  ) {
    return this._testSwaggerConsole(this,environment, resource, key, value);
  }

  testCurl(
    environment: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  ) {
    return this._testCurl(this, environment, method, resource);
  }

  testConsole(invokeInfo: InvokeInfo) {
    return this._testConsole(this, invokeInfo);
  }

  promoteProd() {
    this._promoteTask(this, createDefaultSteps(2));
  }

  promoteWithConfigs(configs: ConfigEntryStep[]) {
    this._promoteBYOC(this, configs);
    }

}
