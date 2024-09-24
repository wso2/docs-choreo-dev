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

import { ComponentInfo, ServiceInfo } from "../../entities/project/project";
import { TestIds } from "../../constants/TestIds";
import { SHORT_TIME } from "../../../commons/timeouts";
import { BuildPacks } from "../../../commons/enums";

export class _ComponentCreationWizard {
  
  enterServiceInfo(
    name: string,
    serviceInfo: ServiceInfo,
    repoUrl: string,
    repoName: string,
    repoTestid: string
  ) {
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));

    this.createFromGHUrl(repoUrl);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    cy.get(TestIds.projectDirectoryEdit).should("be.visible").eq(0).click();
    this.searchRepoName(repoName);
    this.selectRepo(repoTestid);
    cy.get(TestIds.continueButton).should("be.enabled").click();
    cy.get(TestIds.serviceCreateButton).should("be.enabled").eq(1).click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist");

    this.verifyComponentCreation(name);
  }


  enterManualTriggerInfo(
    name: string,
    manualTriggerInfo: ComponentInfo,
    repoUrl: string,
    repoName: string,
    repoTestid: string
  ) {
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));

    this.createFromGHUrl(repoUrl);
    this.handleBuildPackSelectionFromService(manualTriggerInfo.buildPack);
    cy.get(TestIds.projectDirectoryEdit).should("be.visible").eq(0).click();
    this.searchRepoName(repoName);
    this.selectRepoManual(repoTestid);
    cy.get(TestIds.continueButton).should("be.enabled").click();
    cy.get(TestIds.languageVersionDropDown).should("be.visible").click();
    cy.get("li").contains(manualTriggerInfo.languageVersion).click();
    cy.get(TestIds.serviceCreateButton).should("be.enabled").eq(1).click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist");

    this.verifyComponentCreation(name);
  }

  private createFromGHUrl(url: string) {
    cy.get(TestIds.serviceGHUrlEntry).should("be.visible").type(url);
  }

  private handleBuildPackSelectionFromService(buildPack: BuildPacks) {
    switch (buildPack) {
      case BuildPacks.Ballerina:
        cy.get(TestIds.ballerinaComponentCard).should("be.visible").click();
        break;

      case BuildPacks.Go:
        cy.get(TestIds.goComponentCard).should("be.visible").click();
        break;

      case BuildPacks.MI:
        cy.get(TestIds.miComponentCard).should("be.visible").click();
        break;
    }
  }

  enterTestRunnerInfo(
    name: string,
    TestRunnerInfo: ComponentInfo,
    repoUrl: string,
    repoName: string,
    repoTestid: string
  ) {
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));

    this.createFromGHUrl(repoUrl);
    this.handleBuildPackSelectionFromService(TestRunnerInfo.buildPack);
    cy.get(TestIds.projectDirectoryEdit).should("be.visible").eq(0).click();
    this.searchRepoName(repoName);
    this.selectRepoTestRunner(repoTestid);
    cy.get(TestIds.continueButton).should("be.enabled").click();
    cy.get(TestIds.languageVersionDropDown).should("be.visible").click();
    cy.get("li").contains(TestRunnerInfo.languageVersion).click();
    cy.get(TestIds.serviceCreateButton).should("be.enabled").eq(1).click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist");

    this.verifyComponentCreation(name);
  }


  enterMIServiceInfo(
    name: string,
    serviceInfo: ServiceInfo,
    repoUrl: string,
    repoName: string,
    repoTestid: string
  ) {
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));

    this.createFromGHUrl(repoUrl);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    cy.get(TestIds.projectDirectoryEdit).should("be.visible").eq(0).click();
    this.searchRepoName(repoName);
    this.selectRepoMIService(repoTestid);
    cy.get(TestIds.continueButton).should("be.enabled").click();
    cy.get(TestIds.serviceCreateButton).should("be.enabled").eq(1).click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist");

    this.verifyComponentCreation(name);
  }

  private searchRepoName(repoSearchBox: string) {
    cy.get(TestIds.repoSearchBox).should("be.visible").type(repoSearchBox);
  }

  private selectRepo(testid: string) {
    cy.get(TestIds.greetingBalServiceRepo).should("be.visible").click();
  }

  private selectRepoManual(testid: string) {
    cy.get(TestIds.HelloWorldGoManualTaskRepo).should("be.visible").click();
  }

  private selectRepoTestRunner(testid: string) {
    cy.get(TestIds.testRunnerGoRepo).should("be.visible").click();
  }

  private selectRepoMIService(testid: string) {
    cy.get(TestIds.mIServiceRepo).should("be.visible").click();
  }

  private verifyComponentCreation(name: string) {
    cy.get(TestIds.componentSelector, SHORT_TIME).should("be.visible").contains(name).should("be.visible");
  }
  
}
