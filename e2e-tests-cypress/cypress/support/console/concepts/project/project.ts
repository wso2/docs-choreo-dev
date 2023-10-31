import { proxy } from "cypress/types/jquery";
import { Enums } from "../../../commons/enums";
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
import { Service } from "../component/service/service-component";
import { Proxy } from "../component/proxy/proxy-component";

export interface RepoInfo {
  readonly url: string;
  readonly branch: string;
  readonly subPath?: string;
}

export interface ProxyInfo {
  readonly version: string;
  readonly endpointUrl?: string;
  readonly oasUrl?: string;
  readonly oasFilePath?: string;
  readonly isInternal?: boolean;
}

export class Project {
  name: string;
  description: string;

  private proxyCreationWizard = new _ProxyCreationWizard();

  constructor(name: string, description: string) {
    this.name = name;
    this.description = description;

    this.createProject();
  }

  private createProject() {
    console.navigateToHome();
    this.checkProjectCardCreation();
    cy.get(TestIds.projectName).clear().type(this.name);
    cy.get(TestIds.projectDescription).clear().type(this.description);
    cy.get(TestIds.multiRepository).click();
    Utils.getRenderedElement(TestIds.createProject).click();
    cy.get(TestIds.createProject).should("not.exist");
    cy.get(TestIds.backToProjectList).should("exist");
    cy.get(TestIds.backToProjectList).should("not.exist");
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

  createServiceComponent(
    accessibility: Enums.Accessibility,
    repoInfo: RepoInfo,
    endpointName: string
  ) {
    const componentName = Utils.generateComponentName();
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
      GraphQLQueryBuilder.getRestComponentCreationQuery
    ).then((componentDetails: ComponentDetails) => {
      return Promise.resolve(
        new Service(
          componentName,
          componentDetails.id,
          componentDetails.handler,
          componentDetails.projectId,
          endpointName
        )
      );
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
    this.proxyCreationWizard.enterProxyDetails(proxyName, basePath, proxyInfo);

    return cy.url().then((url) => {
      return new Proxy(
        proxyName,
        proxyInfo.version,
        basePath,
        proxyInfo.endpointUrl,
        url
      );
    });
  }

  verifyUsageInsights(env: Enums.Environment) {
    this.selectTimePeriod();
    this.selectEnvironment(env);
    this.getTotalTraffic().should((value) => {
      expect(Number(value)).gte(2);
    });
  }

  private createComponentIfEmptyProject() {
    cy.get("body").then((body) => {
      if (body.find(TestIds.createComponent).length > 0) {
        cy.get(TestIds.createComponent).click();
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

        if (retryCount > 3) {
          return -1;
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
