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

import { BuildPacks, Enums } from "../../../commons/enums";
import {
  MEDIUM_TIME,
  SHORT_TIME,
  VERY_SHORT_TIME,
} from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";
import { ComponentData } from "../../../interfaces/component-data";
import { GraphQLQueryBuilder } from "../../apis/gql-query-builder";
import { ComponentDetails, GraphQL } from "../../apis/graphql";
import { console } from "../../console";
import { TestIds } from "../../constants/TestIds";
import { _ProxyCreationWizard } from "../../ui-elements/wizards/proxy-creation-wizard";
import { Service } from "../component/service-component";
import { Proxy } from "../component/proxy-component";
import { ManualTrigger } from "../component/manual-trigger-component";
import { WebappComponent } from "../../../interfaces/choreo-components/webapp-component";
import { WebApp } from "../component/webapp-component";
import { ScheduleTrigger } from "../component/schedule-trigger-component";
import { Webhook } from "../component/webhook-component";
import { TestRunnerComponent } from "../../../interfaces/choreo-components/testrunner-component";
import { TestRunner } from "../component/test-runner-component";
import { IntegrationComponentData } from "../../../interfaces/integration-component-data";
import { Byoc } from "../component/byoc-component";
import { ByocComponent } from "../../../interfaces/choreo-components/byoc-component";
import { _ComponentCreationWizard } from "../../ui-elements/wizards/component-creation-wizard";

export interface RepoInfo {
  readonly url: string;
  readonly branch: string;
  readonly subPath?: string;
  readonly dockerContext?: string;
}

export interface ProxyInfo {
  readonly version: string;
  readonly endpointUrl: string;
  readonly oasUrl?: string;
  readonly oasFilePath?: string;
  readonly isInternal?: boolean;
}

export interface ServiceInfo {
  readonly displayName: string;
  readonly repoUrl: string;
  readonly buildPack: BuildPacks;
  readonly repoName: string;
  readonly repoTestid: string;
  readonly ENDPOINT_NAME: string;
}

export interface ManualTriggerInfo {
  readonly displayName: string;
  readonly repoUrl: string;
  readonly buildPack: BuildPacks;
  readonly repoName: string;
  readonly repoTestid: string;
  readonly languageVersion: string;
}

export interface WebAppInfo {
  readonly webAppType: string;
  readonly webAppBuildCommand: string;
  readonly webAppPackageManagerVersion: string;
  readonly webAppOutputDirectory: string;
}

export interface BuildPackInfo {
  readonly buildpackId: string;
  readonly languageVersion: string;
  readonly buildContext?: string;
}

export interface WebhookInfo {
  readonly triggerChannels: string;
  readonly triggerId: string;
}

export interface ByocInfo {
  readonly dockerfilePath: string;
  readonly dockerContext: string;
}

export class Project {
  name: string;
  description: string;

  private proxyCreationWizard = new _ProxyCreationWizard();
  private serviceCreationWizard = new _ComponentCreationWizard();
 
  constructor(
    name: string,
    description: string,
    isProjectExists: boolean = false
  ) {
    this.name = name;
    this.description = description;

    if (!isProjectExists) {
      this.createProject();
    }
  }

  private createProject() {
    console.navigateToHome();
    this.checkProjectCardCreation();
    cy.get(TestIds.projectName).clear().type(this.name);
    cy.get(TestIds.projectDescription).clear().type(this.description);
    Utils.getRenderedElement(TestIds.createProject).click();
    cy.get(TestIds.createProject).should("not.exist");
    cy.get(TestIds.backdropLoader).should("not.exist");
  }

  private checkProjectCardCreation() {
    cy.url().then((url) => {
      if (url.includes("projects")) {
        Utils.getRenderedElement(TestIds.projectPicker).click();
        Utils.getRenderedElement(TestIds.createNew).click();
      } else {
        this.getCreateNewProjectPopUp();
      }
    });
  }

  private getCreateNewProjectPopUp(retryCount: number = 0) {
    retryCount++;
    if (retryCount > 10) {
      return;
    }

    cy.get("body").then((bdy) => {
      if (bdy.find(TestIds.projectCard).length > 0) {
        cy.get(TestIds.projectCard).click();
        cy.get(TestIds.projectCard).should("not.exist");
      } else {
        cy.log("Retry count: " + retryCount);
        this.getCreateNewProjectPopUp(retryCount);
      }
    });

    cy.log("Verify the PopUP is displayed");
    cy.get("body").then((bdy) => {
      if (bdy.find(TestIds.projectName).length > 0) {
        cy.get(TestIds.projectName).should("be.visible");
        return;
      } else {
        this.getCreateNewProjectPopUp(retryCount);
      }
    });
  }

  isComponentExists(name: string) {
    this.goToComponentListing();

    let isExists = false;

    return cy
      .get("body")
      .then((body) => {
        if (body.find(TestIds.componentTable).length > 0) {
          this.searchComponent(name);
          cy.get(TestIds.componentTable)
            .find("tbody")
            .within((tbody) => {
              if (tbody.find("tr").length > 0) {
                cy.get("tr").each((row) => {
                  cy.wrap(row).within(() => {
                    cy.get("td")
                      .eq(0)
                      .then((td) => {
                        cy.wrap(td.find("div").first())
                          .invoke("attr", "title")
                          .then((title) => {
                            if (title === name) {
                              isExists = true;
                            }
                          });
                      });
                  });
                });
              }
            });
          this.clearComponentSearch();
        }
      })
      .then(() => {
        return cy.wrap(isExists);
      });
  }

  visitComponent(name: string): string {
    this.goToComponentListing();
    cy.get("body").then((body) => {
      if (body.find(TestIds.refreshComponentListIconButton).length > 0) {
        cy.get(TestIds.refreshComponentListIconButton)
          .should("be.visible")
          .click();
      } else {
        cy.get(TestIds.ComponentUsageInsightsLink).should("be.visible").click();
        this.goToComponentListing();
      }

      cy.contains("Refetching Components...", SHORT_TIME).should("not.exist");
      this.searchComponent(name);
      cy.get(TestIds.componentTable)
        .contains(name)
        .should("be.visible")
        .click();
      cy.get('[data-cyid="home"]').should("be.visible");
      cy.get(TestIds.backdropLoader, SHORT_TIME).should("not.exist");
      cy.get(TestIds.createTime).should("be.visible");
      cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist");
      cy.log("Successfully visited to the component");
    });

    cy.url().then((url) => {
      return url;
    });

    return "";
  }

  deleteComponent(name: string) {
    this.goToComponentListing();
    this.searchComponent(name);

    cy.get(TestIds.componentTable).within(() => {
      cy.get("tbody > tr").should("be.visible").realHover();
    });

    cy.get(TestIds.componentDelete).should("be.visible").click();
    cy.get(TestIds.componentDeleteConfirm).should("be.visible");
    cy.get(TestIds.confirmName).within(() => {
      cy.get("input").type(name).type("{enter}");
    });

    cy.get(TestIds.componentDeleteConfirm).should("not.exist");
    cy.get(TestIds.backdropLoader, VERY_SHORT_TIME).should("not.exist");
    cy.contains(name).should("not.exist");
  }

  searchSampleService(searchString: string) {
    this.createComponentIfEmptyProject();
    cy.get(TestIds.viewAllSamples).scrollIntoView().click();
    cy.get(TestIds.trySample).should("be.visible").click();
    cy.get(TestIds.sampleSearch).should("be.visible").type(searchString);
    cy.get(TestIds.sampleCard(searchString)).should("be.visible");
  }

  createServiceComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    endpointName: string,
    componentName?: string,
    loggingCallback?: any
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: ComponentData = {
      componentName: componentName,
      displayType: Enums.DisplayType.ballerinaService,
      accessibility: accessibility,
      projectName: this.name,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: repoInfo.url,
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: repoInfo.subPath == undefined ? "" : repoInfo.subPath,
      sampleTemplate: "",
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery,
      loggingCallback
    ).then((componentDetails: ComponentDetails) => {
      return Promise.resolve(new Service(componentName, endpointName));
    });
  }

  createWebAppComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    webAppInfo: WebAppInfo,
    componentName?: string,
    loggingCallback?: any
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: WebappComponent = {
      name: componentName,
      displayName: componentName,
      accessibility: accessibility,
      componentType: Enums.DisplayType.byocWebAppsDockerfileLess,
      description: "Web app Component",
      labels: "",
      projectId: "",
      byocWebAppsConfig: {
        dockerContext:
          repoInfo.dockerContext == undefined ? "" : repoInfo.dockerContext,
        srcGitRepoUrl: repoInfo.url,
        srcGitRepoBranch: repoInfo.branch,
        webAppType: webAppInfo.webAppType,
        webAppBuildCommand: webAppInfo.webAppBuildCommand,
        webAppPackageManagerVersion: webAppInfo.webAppPackageManagerVersion,
        webAppOutputDirectory: webAppInfo.webAppOutputDirectory,
        isAppGatewayEnabled: true,
      },
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getWebAppComponentCreationQuery,
      loggingCallback
    ).then(() => {
      return Promise.resolve(new WebApp(componentName));
    });
  }

  createProxyComponent(proxyInfo: ProxyInfo) {
    this.createComponentIfEmptyProject();
    cy.get(TestIds.proxyBuildPack).should("be.visible").click();

    if (proxyInfo.oasUrl !== undefined) {
      this.proxyCreationWizard.createFromOASUrl(proxyInfo.oasUrl);
    } else if (proxyInfo.oasFilePath !== undefined) {
      this.proxyCreationWizard.createFromOASFile(proxyInfo.oasFilePath);
    } else {
      cy.get(TestIds.skipSource).should("be.visible").click();
    }

    const proxyName = Utils.generateComponentName("oas");
    const basePath = Utils.generateBasePath();
    const proxyEndpointUrl = this.proxyCreationWizard.enterProxyDetails(
      proxyName,
      basePath,
      proxyInfo
    );

    return cy.url().then((url) => {
      return new Proxy(
        proxyName,
        proxyInfo.version,
        basePath,
        proxyEndpointUrl,
        url,
        "",
        "",
        ""
      );
    });
  }

  createServiceComponentUI(serviceInfo: ServiceInfo): Cypress.Chainable<Service> {
    this.createComponentIfEmptyProject();
    cy.get(TestIds.serviceBuildPack).should("be.visible").click();

    const serviceName = Utils.generateComponentName();
    this.serviceCreationWizard.enterServiceInfo(
      serviceName,
      serviceInfo,
      serviceInfo.repoUrl,
      serviceInfo.repoName,
      serviceInfo.repoTestid
    );

    return cy.wrap(new Service(serviceName, serviceInfo.ENDPOINT_NAME));
  }

  createManualTriggerUI(manualTriggerInfo: ManualTriggerInfo): Cypress.Chainable<ManualTrigger> {
    this.createComponentIfEmptyProject();
    cy.get(TestIds.manualTriggerBuildPack).should("be.visible").click();

    const manualTriggerName = Utils.generateComponentName();
    this.serviceCreationWizard.enterManualTriggerInfo(
      manualTriggerName,
      manualTriggerInfo,
      manualTriggerInfo.repoUrl,
      manualTriggerInfo.repoName,
      manualTriggerInfo.repoTestid
    );

    return cy.wrap(new ManualTrigger(manualTriggerName));
  }

  createManualTriggerComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }
    let componentData: ComponentData = {
      componentName: componentName,
      displayType: Enums.DisplayType.manualTrigger,
      accessibility: accessibility,
      projectName: this.name,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: repoInfo.url,
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: repoInfo.subPath == undefined ? "" : repoInfo.subPath,
      sampleTemplate: "",
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    ).then((componentDetails: ComponentDetails) => {
      return Promise.resolve(new ManualTrigger(componentName));
    });
  }

  createScheduleTriggerComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: ComponentData = {
      componentName: componentName,
      displayType: Enums.DisplayType.scheduledTask,
      accessibility: accessibility,
      projectName: this.name,
      triggerChannels: "",
      triggerId: null,
      srcGitRepoUrl: repoInfo.url,
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: repoInfo.subPath == undefined ? "" : repoInfo.subPath,
      sampleTemplate: "",
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    ).then((componentDetails: ComponentDetails) => {
      return Promise.resolve(new ScheduleTrigger(componentName));
    });
  }

  createTestRunnerComponent(
    repoInfo: RepoInfo,
    buildPackInfo: BuildPackInfo,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: TestRunnerComponent = {
      name: componentName,
      displayName: componentName,
      accessibility: Enums.Accessibility.NONE,
      componentType: Enums.DisplayType.buildpackTestRunner,
      description: "Test runner Component",
      labels: "",
      projectId: "",
      oasFilePath: "",
      port: null,
      buildpackConfig: {
        buildContext:
          buildPackInfo.buildContext == undefined
            ? ""
            : buildPackInfo.buildContext,
        srcGitRepoUrl: repoInfo.url,
        srcGitRepoBranch: repoInfo.branch,
        languageVersion: buildPackInfo.languageVersion,
        buildpackId: buildPackInfo.buildpackId,
      },
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getTestRunnerComponentCreationQuery
    ).then(() => {
      return Promise.resolve(new TestRunner(componentName));
    });
  }

  createWebhookComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    webhookInfo: WebhookInfo,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: ComponentData = {
      componentName: componentName,
      displayType: Enums.DisplayType.webhook,
      accessibility: accessibility,
      projectName: this.name,
      srcGitRepoUrl: repoInfo.url,
      initializeAsBallerinaProject: false,
      repositoryType: Enums.RepoType.UserManagedNonEmpty,
      repositorySubPath: repoInfo.subPath == undefined ? "" : repoInfo.subPath,
      sampleTemplate: "",
      triggerChannels: webhookInfo.triggerChannels,
      triggerId: webhookInfo.triggerId,
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getRestComponentCreationQuery
    ).then((componentDetails: ComponentDetails) => {
      return Promise.resolve(new Webhook(componentName));
    });
  }

  createMIServiceComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    endpointName: string,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: IntegrationComponentData = {
      componentName: componentName,
      componentType: Enums.ComponentType.MI_API_SERVICE,
      accessibility: accessibility,
      projectName: this.name,
      srcGitRepoUrl: repoInfo.url,
      repositorySubPath: repoInfo.subPath == undefined ? "" : repoInfo.subPath,
      oasFilePath: "",
      srcGitRepoBranch: repoInfo.branch,
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getMIComponentCreationQuery
    ).then(() => {
      return Promise.resolve(new Service(componentName, endpointName));
    });
  }

  createByocComponent(
    repoInfo: RepoInfo,
    byocInfo: ByocInfo,
    oasFilePath: string,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: ByocComponent = {
      name: componentName,
      displayName: componentName,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocRestApi,
      description: "BYOC Component",
      labels: "",
      projectId: "",
      oasFilePath: oasFilePath,
      port: 8080,
      byocConfig: {
        srcGitRepoUrl: repoInfo.url,
        srcGitRepoBranch: repoInfo.branch,
        dockerfilePath: byocInfo.dockerfilePath,
        dockerContext: byocInfo.dockerContext,
      },
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getBYOCComponentCreationQuery
    ).then(() => {
      return Promise.resolve(new Byoc(componentName));
    });
  }

  createByocServiceComponent(
    repoInfo: RepoInfo,
    byocInfo: ByocInfo,
    oasFilePath: string,
    endpointName: string,
    componentName?: string
  ) {
    if (componentName === undefined) {
      componentName = Utils.generateComponentName();
    }

    let componentData: ByocComponent = {
      name: componentName,
      displayName: componentName,
      accessibility: Enums.Accessibility.EXTERNAL,
      componentType: Enums.DisplayType.byocService,
      description: "Containerized Service Component",
      labels: "",
      projectId: "",
      oasFilePath: oasFilePath,
      port: 80,
      byocConfig: {
        srcGitRepoUrl: repoInfo.url,
        srcGitRepoBranch: repoInfo.branch,
        dockerfilePath: byocInfo.dockerfilePath,
        dockerContext: byocInfo.dockerContext,
      },
    };

    return GraphQL.createComponentV2(
      this.name,
      "",
      componentData,
      GraphQLQueryBuilder.getBYOCComponentCreationQuery
    ).then(() => {
      return Promise.resolve(new Service(componentName, endpointName));
    });
  }

  verifyUsageInsights(
    env: Enums.Environment,
    options?: { expectedTraffic: number }
  ) {
    this.selectEnvironment(env);
    this.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(options?.expectedTraffic || 2);
    });
  }

  private goToComponentListing() {
    cy.get(TestIds.listing).should("be.visible").click();
    cy.contains("Create").should("exist");
  }

  private searchComponent(name: string) {
    cy.get(TestIds.searchIcon).should("be.visible").click();
    cy.get(TestIds.componentSearchBox).should("be.visible").type(name);
  }

  private clearComponentSearch() {
    cy.get(TestIds.componentSearchBox).should("be.visible").clear();
    cy.get(TestIds.clearComponentSearchButton).should("be.visible").click();
  }

  private createComponentIfEmptyProject() {
    cy.get(TestIds.backdropLoader, VERY_SHORT_TIME).should("not.exist");
    cy.get("body").then((body) => {
      if (body.find(TestIds.createComponent).length > 0) {
        cy.get(TestIds.createComponent).click();
        cy.contains("Create").should("be.visible").click();
        cy.get(TestIds.backdropLoader).should("not.exist");
      }
    });
  }

  private selectTimePeriod(timePeriod: string = "Past 15 minutes") {
    cy.get(TestIds.datePicker, MEDIUM_TIME).click();
    cy.contains(timePeriod).click();
    cy.get(TestIds.datePicker).within(() => {
      cy.contains(timePeriod).should("be.visible");
    });
  }

  private selectEnvironment(env: Enums.Environment) {
    cy.contains("Environment").should("be.visible");
    cy.contains("Environment").next().click();
    cy.contains(env).click();
  }

  private getTotalTraffic(retryCount: number = 0) {
    return cy.get("body").then((bdy) => {
      if (bdy.find(".recharts-area").length > 0) {
        cy.get(".recharts-area")
          .should("be.visible")
          .wait(VERY_SHORT_TIME.timeout); // Give some time for the other stats to load
        cy.contains("Total Traffic").should("be.visible");
        return cy.get("main").find("span>span").eq(0).invoke("text");
      } else {
        cy.reload();
        cy.get(TestIds.backdropLoader, SHORT_TIME).should("not.exist");

        retryCount++;

        if (retryCount > 6) {
          return cy.wrap(-1);
        }

        cy.wait(VERY_SHORT_TIME.timeout * retryCount);
        return this.getTotalTraffic(retryCount);
      }
    });
  }

  private getTotalErrorRequestCount() {
    return cy.get("main").find("span>span").eq(1).invoke("text");
  }

  private getAverageErrorRate() {
    return cy.get("main").find("span>span").eq(2).invoke("text");
  }
}
