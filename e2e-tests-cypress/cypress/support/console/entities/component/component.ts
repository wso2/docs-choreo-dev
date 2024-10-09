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
import { EndpointAccessibility, Enums } from "../../../commons/enums";
import path from "path";
import {
  MEDIUM_TIME,
  SHORT_TIME,
} from "../../../commons/timeouts";
import { TryOut } from "../../../devportal/pages/apis/try-out";
import { generateAppName } from "../../../devportal/utils";
import { TestIds } from "../../constants/TestIds";
import { DevPortalLeftMenu } from "../../ui-elements/left-menus/dev-portal-left-menu";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { Application } from "../application/application";
import { _Stats } from "../../features/stats/stats";
import { _Observability } from "../../features/observability/observability";

export interface DevPortalTryOut {
  resource: string;
  application?: string;
  env?: Enums.Environment.PRODUCTION | Enums.Environment.DEVELOPMENT;
}

/**
 * This is the base class for all the components in the Console
 */
export class Component {
  private name: string;
  private versions: string[] = [];
  protected componentUrl: string = "";
  protected devPortalUrl: string = "";

  private devEndpointUrls: Map<EndpointAccessibility, string> = new Map();
  private devEndpointUrl: string = "";

  private prodEndpointUrls: Map<EndpointAccessibility, string> = new Map();
  private prodEndpointUrl: string = "";

  private devPortalMenu = new DevPortalLeftMenu();
  private stats = new _Stats();
  protected sideMenu = new ServiceLeftMenu();
  protected observability = new _Observability();

  constructor(
    name: string,
    version: string,
    componentUrl = "",
    devPortalUrl = "",
    devEndpointUrl = "",
    prodEndpointUrl = ""
  ) {
    this.name = name;
    this.versions.push(version);
    this.componentUrl = componentUrl;
    this.devPortalUrl = devPortalUrl;
    this.devEndpointUrl = devEndpointUrl;
    this.prodEndpointUrl = prodEndpointUrl;
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

  getDevEndpointUrl(endpointVisibility?: EndpointAccessibility): string {
    if (endpointVisibility === undefined) {
      return this.devEndpointUrl;
    }

    let url = this.devEndpointUrls.get(endpointVisibility);

    if (url === undefined) {
      throw new Error("Dev endpoint URL is not defined for " + endpointVisibility + " visibility");
    }

    return url;
  }

  setDevEndpointUrl(endpointVisibility: EndpointAccessibility, url: string) {
    this.devEndpointUrls.set(endpointVisibility, url);
  }

  getProdEndpointUrl(endpointVisibility?: EndpointAccessibility): string {
    if (endpointVisibility === undefined) {
      return this.prodEndpointUrl;
    }

    let url = this.prodEndpointUrls.get(endpointVisibility);

    if (url === undefined) {
      throw new Error("Prod endpoint URL is not defined for " + endpointVisibility + " visibility");
    }

    return url;
  }

  setProdEndpointUrl(endpointVisibility: EndpointAccessibility, url: string) {
    this.prodEndpointUrls.set(endpointVisibility, url);
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

  testSwaggerConsole_DevPortal(devPortalTryOut: DevPortalTryOut) {
    this.devPortalMenu.navigateToTryOut();

    if (devPortalTryOut.env !== undefined) {
      TryOut.selectEndpoint(devPortalTryOut.env);
    }

    if (devPortalTryOut.application !== undefined) {
      cy.wait(3000);
      cy.get(TestIds.applicationSelect).scrollIntoView();
      cy.get(TestIds.applicationSelect).should("be.visible").click().wait(3000);
      cy.get(TestIds.applicationSelectItem(devPortalTryOut.application))
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

    TryOut.SelectResource(devPortalTryOut.resource);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  }

  addComment_DevPortal(apiComment: string) {
    this.devPortalMenu.navigateToOverview();

    cy.get(TestIds.addCommentLink).click();

    cy.get(TestIds.addCommentBtn).should("be.visible");
    cy.get(TestIds.commentTextArea).type(apiComment).wait(200);
    cy.get(TestIds.addCommentBtn).click();
    cy.get(TestIds.commentsCount).should(
      "have.text",
      "Comments (1)",
      MEDIUM_TIME
    );
    cy.get(TestIds.noComments).should("not.exist");
  }

  deleteComment_DevPortal() {
    cy.get(TestIds.commentsTable).should("be.visible");
    cy.get(TestIds.commentsTable)
      .get("tr")
      .first()
      .within(() => {
        cy.get(TestIds.deleteComment).should("be.visible").click();
      });
    cy.get(TestIds.deleteCommentPopup)
      .should("be.visible")
      .within(() => {
        cy.contains("Yes").click();
      });

    cy.get(TestIds.deleteCommentPopup).should("not.exist");
    cy.get(TestIds.commentsCount).should("have.text", "Comments (0)");
    cy.get(TestIds.noComments).should("exist");
  }

  addRating_DevPortal(numberOfStars: number) {
    cy.get(TestIds.ratingContainer).should("be.visible");
    cy.get(TestIds.ratingContainer)
      .children()
      .first()
      .within(() => {
        cy.get("button").click();
      });

    cy.get(TestIds.ratingStars).should("be.visible").click();
    cy.get(TestIds.ratingStar(numberOfStars)).should("be.visible").click();
    cy.get(TestIds.ratingPopupRoot).click();
    cy.contains(`${numberOfStars}.0 (1)`).should("exist");
  }

  downloadSdk_DevPortal(sdkFile: string) {
    cy.get(TestIds.sdks).click();
    cy.get(TestIds.androidSdk, MEDIUM_TIME).should("be.visible").click();
    const downloadsFolder = Cypress.config("downloadsFolder");
    cy.readFile(path.join(downloadsFolder, sdkFile)).should(
      "exist",
      MEDIUM_TIME
    );
  }

  navigateToComponentInConsole() {
    cy.log(this.getComponentUrl());
    cy.visit(this.getComponentUrl()).then(() => {
      cy.get(TestIds.backdropLoader, SHORT_TIME).should("not.exist");
    });
  }

  saveEndpointUrls(endpointMatcher: Map<string, Enums.Environment>) {
    this.sideMenu.navigateToOverview();

    cy.get(TestIds.createTime).should("be.visible");
    cy.get(TestIds.progressBar).should("not.exist");
    cy.get(TestIds.deploymentStatusChip).should("be.visible");
    cy.get(TestIds.endpoint).should("be.visible");

    return cy.get(TestIds.endpoint).then(($endpoint) => {
      const endpointCount = $endpoint.length;

      for (let i = 0; i < endpointCount; i++) {
        cy.get(TestIds.endpoint)
          .eq(i)
          .within(() => {
            cy.get("input")
              .invoke("val")
              .then((text) => {
                for (let [matcher, env] of endpointMatcher) {
                  if (text === undefined) {
                    throw new Error("Endpoint URL is not defined");
                  }

                  if (text.toString().includes(matcher)) {
                    if (env === Enums.Environment.PRODUCTION) {
                      this.prodEndpointUrl = text.toString();
                    } else {
                      this.devEndpointUrl = text.toString();
                    }
                  }
                }
              });
          });
      }

      return cy.wrap({});
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

  goBackToProject() {
    cy.get(TestIds.project).should("be.visible").click();
  }

  navigateToPublicDevPortal() {
    const loginURL =
      Cypress.env("devportalLoginURL") + "/" + Cypress.env("choreoOrgHandle");
    cy.visit(loginURL);
    cy.get(TestIds.devPortalHome).should("be.visible");
    cy.get(TestIds.devPortalLoginLink).should("be.visible");
  }
}
