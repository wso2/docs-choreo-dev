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

import { TestIds } from "../../constants/TestIds";
import { Proxy } from "../../entities/component/proxy-component";
import { Component } from "../../entities/component/component";
import { ManualTrigger } from "../../entities/component/manual-trigger-component";
import { ScheduleTrigger } from "../../entities/component/schedule-trigger-component";
import { WebApp } from "../../entities/component/webapp-component";
import { Webhook } from "../../entities/component/webhook-component";
import { Test } from "mocha";
import { TestRunner } from "../../entities/component/test-runner-component";

export class DeploymentTrack {
  validate(component: Component) {
    cy.get(TestIds.backdropLoader).should("not.exist");

    const version = component.getLatestVersion();

    if (component instanceof Proxy) {
      cy.get(TestIds.versionPicker).contains(`v${version}`);
    } else if (component instanceof ManualTrigger || 
      component instanceof ScheduleTrigger ||
      component instanceof WebApp ||
      component instanceof Webhook || component instanceof TestRunner) {
      cy.get(TestIds.selectBranch).contains(version);
    } else {
      cy.get(TestIds.selectVersion).contains(`API v${version}`);
    }
  }
}

