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

import { cyGet } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import { TryOut } from "../../../devportal/pages/apis/try-out";
import { generateAppName } from "../../../devportal/utils";
import { TestIds } from "../../constants/TestIds";
import { DevPortalLeftMenu } from "../../ui-elements/left-menus/dev-portal-left-menu";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { Application } from "../application/application";
import { _Stats } from "../../features/stats/stats";
import { _Observability } from "../../features/observability/observability";

/**
 * This is the base class for all the components in the Console
 */
export class Component {
  private name: string;
  private versions: string[] = [];
  protected componentUrl: string;
  private devPortalUrl: string;

  private devPortalMenu = new DevPortalLeftMenu();
  private stats = new _Stats();
  protected sideMenu = new ServiceLeftMenu();
  protected observability = new _Observability();

  constructor(name: string, version: string) {
    this.name = name;
    this.versions.push(version);
  }

  getName() {
    return this.name;
  }

  getLatestVersion() {
    return this.versions[this.versions.length - 1];
  }

  updateVersionList(version: string) {
    this.versions.push(version);
  }

  getComponentUrl() {
    return this.componentUrl;
  }

  getDevPortalUrl() {
    return this.devPortalUrl;
  }

  setDevPortalUrl(url: string) {
    this.devPortalUrl = url;
  }

  generateCredentials_DevPortal(
    env: Enums.Environment.PRODUCTION | Enums.Environment.SANDBOX
  ) {
    if (env === Enums.Environment.PRODUCTION) {
      this.devPortalMenu.navigateToProductionCredentials();
    } else {
      this.devPortalMenu.navigateToSandboxCredentials();
    }

    cy.get(TestIds.generateCredentials).should("be.visible").click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.generateCredentials).should("not.exist");
    cy.get(TestIds.removeCredentials).should("be.visible");
    cy.get("#copy-textfield").invoke("val").should("not.be.empty");
    cy.get(TestIds.generateAccessToken).should("be.visible");
  }

  createApplication_DevPortal() {
    const appName = generateAppName("-e2etest");
    const appDescription = "Application for e2e testing";

    cy.get(TestIds.applicationBar).should("be.visible").click();

    cy.get(TestIds.createApplication).click();
    cy.get(TestIds.applicationName).type(appName);
    cy.get(TestIds.applicationDescription).type(appDescription);
    cy.get(TestIds.createBtn).click({ force: true });
    cy.wait(5000);
    cy.get(TestIds.applicationDescription).should("have.text", appDescription);
    cy.get(TestIds.applicationTokenType).should("have.text", "JWT");

    return new Application(appName, appDescription);
  }

  deleteApplication_DevPortal(application: Application) {
    const appName = application.getName();
    cy.get(TestIds.applicationBar).should("be.visible").click();
    cyGet(TestIds.search).trigger("mouseover");
    cyGet(TestIds.searchAppText).type(appName);
    cy.contains(appName).trigger("mouseover");
    cyGet(TestIds.appDeleteBtn(appName)).trigger("mouseover").click();
    cyGet(TestIds.deleteDialogOk).click();
    cyGet(TestIds.createApplication, SHORT_TIME).should("be.visible");
  }

  testSwaggerConsole_DevPortal(resource: string, application?: string) {
    this.devPortalMenu.navigateToTryOut();

    if (application) {
      cy.wait(3000)
        .get(TestIds.applicationSelect)
        .should("be.visible")
        .click()
        .wait(3000);
      cy.get(TestIds.applicationSelectItem(application))
        .should("be.visible")
        .click();
    }

    // Add slight wait to account for rerendering of elements without any visible indication
    cyGet(TestIds.getTestKey)
      .should("be.enabled")
      .wait(5000)
      .get(TestIds.getTestKey)
      .should("be.enabled")
      .click();
    cy.get(TestIds.getTestKey).within(() => {
      cy.get(TestIds.progressBar).should("not.exist");
    });
    cy.get(TestIds.accessToken).should("not.be.empty");

    TryOut.SelectResource(resource);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  }

  navigateToComponentInConsole() {
    cy.log(this.getComponentUrl());
    cy.visit(this.getComponentUrl()).then(() => {
      cy.get(TestIds.backdropLoader).should("not.exist");
      cy.get(TestIds.createTime).should("be.visible");
    });
  }

  verifyUsageInsights() {
    this.stats.viewUsageInsights();
    this.stats.navigateFromComponentToProjectInsights();
  }

  stopDeployment() {
    this.sideMenu.navigateToDeploy();

    cy.get(TestIds.devEnvCard).within(() => {
      cy.get(TestIds.stop).click();
      cy.get(TestIds.stop).should("not.exist");
      cy.get(TestIds.reDeploy).should("exist");
    });
  }

  stopPromotion() {
    cy.get(TestIds.prodEnvCard).within(() => {
      cy.get(TestIds.stop).click();
      cy.get(TestIds.stop).should("not.exist");
      cy.get(TestIds.reDeploy).should("exist");
    });
  }

  _navigateToDevPortal(idp: string) {
    this.sideMenu.navigateToOverview();

    cy.get(TestIds.createTime).should("be.visible");
    cy.get(TestIds.progressBar).should("not.exist");
    cy.get(TestIds.deploymentStatusChip).should("be.visible");

    cy.contains("Requests", SHORT_TIME).should("be.visible");
    cy.contains("Errors", SHORT_TIME).should("be.visible");
    cy.contains("Average TPS", SHORT_TIME).should("be.visible");
    cy.contains("Latency", SHORT_TIME)
      .should("be.visible")
      .wait(VERY_SHORT_TIME.timeout); // Wait for the latency stats to load

    cy.get(TestIds.devPortalLink)
      .invoke("attr", "href")
      .then((href) => {
        const linkParts = href.split("?");
        const url = linkParts[0];
        const queryParams = linkParts[1].split("&amp;");

        let updatedQueryParams = "";

        for (let i = 0; i < queryParams.length; i++) {
          const keyValues = queryParams[i].split("=");

          if (keyValues[0] === "idp") {
            updatedQueryParams += `${keyValues[0]}=${idp}`;
          } else {
            updatedQueryParams += queryParams[i];
          }
        }

        this.setDevPortalUrl(`${url}?${updatedQueryParams}`);

        cy.visit(this.getDevPortalUrl()).then(() => {
          cy.get(TestIds.backdropLoader).should("not.exist");
          cy.get(TestIds.apiNameDevPortal)
            .should("be.visible")
            .contains(this.getName(), VERY_SHORT_TIME);
        });
      });
  }

  protected visitComponent(name: string): string {
    cy.get('[data-cyid="listing"]').should("be.visible").click();

    cy.get('[data-cyid="project-components-multi-select"]').should(
      "be.visible"
    );

    cy.get('[data-cyid="component-table"]')
      .contains(name)
      .should("be.visible")
      .click();

    cy.get('[data-cyid="home"]').should("be.visible");

    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.get("[data-cyid=create-time]").should("be.visible");
    cy.log("Successfully visited to the component");

    cy.url().then((url) => {
      return url;
    });

    return "";
  }
}
