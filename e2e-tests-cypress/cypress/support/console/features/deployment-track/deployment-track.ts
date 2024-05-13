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
import { Service } from "../../entities/component/service-component";

export class DeploymentTrack {
  validate(component: Component) {
    cy.get(TestIds.backdropLoader).should("not.exist");

    const version = component.getLatestVersion();

    const regex = new RegExp(`(API v${version}|^v${version})`, "gm");

    if (component instanceof Proxy) {
      cy.get(TestIds.versionPicker).contains(`v${version}`);
    } else if (component instanceof Service) {
      cy.get(TestIds.selectBranch).contains(regex);
    } else {
      cy.get(TestIds.selectBranch).contains(version);
    }
  }
}
