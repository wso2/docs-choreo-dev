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

import { createDefaultSteps } from "../../../commons/types";
import { mixinBuild } from "../../features/component-build/build";
import { mixinServiceDeploy } from "../../features/deploy/deploy-service";
import { Component } from "./component";
import { mixinConnections } from "../../features/component-connections/connections";


export class WebApp extends mixinBuild(mixinServiceDeploy(mixinConnections(Component))) {
  private devWebAppUrl: string = "";
  private prodWebAppUrl: string = "";

  constructor(name: string) {
    super(name, "main");
  }

  setDevWebAppUrl(url: string) {
    this.devWebAppUrl = url;
  }

  getDevWebAppUrl() {
    return this.devWebAppUrl;
  }

  setProdWebAppUrl(url: string) {
    this.prodWebAppUrl = url;
  }

  getProdWebAppUrl() {
    return this.prodWebAppUrl;
  }

  build() {
    this._build(this);
  }

  deployToDevWithAuthConfiguration(customConfig?: Map<string,string>) {
    this._deployWebapp(this, true, customConfig);
  }

  promoteToProdWithAuthConfiguration(customConfig?: Map<string,string>) {
    this._promoteWebapp(this, true, createDefaultSteps(1), customConfig);
  }

  verifyTestPageIsDisabled() {
    cy.get('[data-cyid="link-test"]').should("have.attr", "disabled");
  }

  verifyManagePageIsDisabled() {
    cy.get('[data-cyid="link-manage"]').should("have.attr", "disabled");
  }

  createConnections() {
    this._createConnections(this);
  }

  copyConnectionUrl(): Cypress.Chainable<string> {
    return this._copyConnectionUrl();
  }
}
