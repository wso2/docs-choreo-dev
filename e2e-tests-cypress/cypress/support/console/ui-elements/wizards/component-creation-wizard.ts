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

import { ComponentInfo, DirectoryInfo } from "../../entities/project/project";
import { TestIds } from "../../constants/TestIds";
import { SHORT_TIME, VERY_SHORT_TIME } from "../../../commons/timeouts";
import { BuildPacks } from "../../../commons/enums";

export class _ComponentCreationWizard {
  enterServiceInfo(name: string, serviceInfo: ComponentInfo) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(serviceInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(serviceInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
      cy.get(TestIds.serviceDisplayName)
       .eq(0)
      .within(() => cy.get("input").clear().type(name));
    this.createComponent(name);
     
  }

  enterTriggerInfo(name: string, manualTriggerInfo: ComponentInfo, enterCustomInfo: () => void) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(manualTriggerInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(manualTriggerInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(manualTriggerInfo.buildPack);
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
    cy.get(TestIds.languageVersionDropDown).should("be.visible").click();
    enterCustomInfo();
    this.createComponent(name);
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

      case BuildPacks.WEBAPP:
        cy.get(TestIds.reactBuildPack).should("be.visible").click();
        break;

      case BuildPacks.DOCKER:
        cy.get(TestIds.containerizedBuildPack).should("be.visible").click();
        break;
    }
  }

  enterTestRunnerInfo(name: string, testRunnerInfo: ComponentInfo, enterCustomInfo: () => void) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(testRunnerInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(testRunnerInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(testRunnerInfo.buildPack);
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
    cy.get(TestIds.languageVersionDropDown).should("be.visible").click();
    enterCustomInfo();
    this.createComponent(name);
  }

  enterMIServiceInfo(name: string, serviceInfo: ComponentInfo) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(serviceInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(serviceInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
    this.createComponent(name);
  }

  enterMIEndpointServiceInfo(name: string, serviceInfo: ComponentInfo) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(serviceInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(serviceInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
    cy.get(".MuiAutocomplete-endAdornment > .MuiButtonBase-root").click();
    cy.wait(4000);
    if (serviceInfo.branch !== undefined) {
      cy.get("li").contains(serviceInfo.branch).click();
    }
    this.createComponent(name);
  }

  enterWebAppServiceInfo(
    name: string,
    serviceInfo: ComponentInfo,
    enterBuildPackInfo: () => void
  ) {
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));

    this.createFromGHUrl(serviceInfo.repoUrl);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    this.selectProjectDirectory(serviceInfo.directoryInfo);
    enterBuildPackInfo();
    this.createComponent(name);
  }

  enterGQLServiceInfo(name: string, serviceInfo: ComponentInfo) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(serviceInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(serviceInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
    this.createComponent(name);
  }

  enterContainerizedServiceInfo(name: string, serviceInfo: ComponentInfo) {
    cy.get(TestIds.ThirdPartyGITCard).should("be.visible").click();
    this.createFromGHUrl(serviceInfo.repoUrl);
    cy.wait(4000);
    this.selectProjectDirectory(serviceInfo.directoryInfo);
    this.handleBuildPackSelectionFromService(serviceInfo.buildPack);
    cy.get(TestIds.serviceDisplayName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
    cy.get(TestIds.projectDirectoryEdit).should("be.visible").eq(1).click();
    this.searchDockerFile("Dockerfile");
    cy.get(TestIds.continueButton).should("be.enabled").click();
    this.createComponent(name);
  }

  private searchDockerFile(dockerSearchBox: string) {
    cy.get(TestIds.dockerSearchBox).should("be.visible").type(dockerSearchBox);
    cy.get(TestIds.dockerFileSelect)
      .scrollIntoView()
      .should("be.visible")
      .click();
  }

  private selectProjectDirectory(directoryInfo?: DirectoryInfo) {
    if (directoryInfo !== undefined) {
      cy.get(TestIds.projectDirectoryEdit).should("be.enabled").eq(0).click();
      cy.get(TestIds.repoSearchBox).should("be.visible").type(directoryInfo.directoryName);

      if (directoryInfo.subDirectories !== undefined) {
        for (const subDirectory of directoryInfo.subDirectories) {
          cy.get(TestIds.repoSubPath(subDirectory)).should("be.visible").click();
        }
      }

      cy.get(TestIds.repoSubPath(directoryInfo.directoryTestid)).should("be.visible").click();
      cy.get(TestIds.continueButton).should("be.enabled").click();
    }
  }

  private createComponent(name: string) {
    cy.get(TestIds.serviceCreateButton, VERY_SHORT_TIME)
      .eq(1)
      .should("be.enabled")
      .click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist");

    this.verifyComponentCreation(name);
  }

  private verifyComponentCreation(name: string) {
    cy.get(TestIds.componentSelector, SHORT_TIME)
      .should("be.visible")
      .contains(name)
      .should("be.visible");
  }
}
