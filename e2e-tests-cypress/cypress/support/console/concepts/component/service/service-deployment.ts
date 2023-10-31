import {
  BUILD_FAILED,
  DEPLOYMENT_PENDING,
  DEPLOYMENT_PROGRESSING,
  DEPLOYMENT_SUCCESS,
} from "../../../../commons/constants";
import { cyGet } from "../../../../commons/cy";
import {
  LONG_TIME,
  MEDIUM_TIME,
  SHORT_TIME,
} from "../../../../commons/timeouts";
import { Utils } from "../../../../commons/utils";
import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { Service } from "./service-component";

export enum EndpointAccessibility {
  Public = "Public",
  Project = "Project",
  Organization = "Organization",
}

export class _ServiceDeployment {
  private sideMenu = new ServiceLeftMenu();

  public deploy(
    component: Service,
    endpointVisibility: EndpointAccessibility,
    configStepsAvailable = 1
  ) {
    this.sideMenu.navigateToDeploy();

    this.waitTillReadyToDeploy();

    this.startDeployment(component);

    this.stepThroughConfigSteps(configStepsAvailable);

    this.reviewAndUpdateEndpoint(component, endpointVisibility);

    this.verifyDeploymentStatus();

    this.verifyDevAccessibility(component, endpointVisibility);
  }

  public promote(
    component: Service,
    endpointVisibility: EndpointAccessibility,
    configStepsAvailable = 0
  ) {
    this.sideMenu.navigateToDeploy();

    this.startPromotion(component);

    this.stepThroughConfigSteps(configStepsAvailable);

    this.reviewAndUpdateEndpoint(component, endpointVisibility);

    this.verifyPromotionStatus();
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

  private getDevEndpointURL(endpointVisibility: EndpointAccessibility) {
    cy.get(TestIds.devEnvCard).within(() => {
      cy.get(TestIds.availableEndpoints).within(() => {
        cy.get(TestIds.viewArtifact).click();
      });
    });

    cy.get(TestIds.componentLoader).should("not.exist");

    let urlTypeRegex = /^Project URL.*/;
    let urlMatcher = /^http:\/\/.*/;

    if (endpointVisibility === EndpointAccessibility.Public) {
      urlTypeRegex = /^Public URL.*/;
      urlMatcher = /^https:\/\/.*/;
    }

    return cy
      .get(TestIds.endpointCard)
      .should("be.visible")
      .contains(urlTypeRegex)
      .next()
      .invoke("attr", "title")
      .then((url) => {
        expect(url).to.match(urlMatcher);
        return Promise.resolve(url);
      });
  }

  private waitTillReadyToDeploy() {
    Utils.getRenderedElement(TestIds.buildCard);
    cyGet(TestIds.buildCard, MEDIUM_TIME).should("be.visible").wait(1000);
    cyGet(TestIds.buildCard, MEDIUM_TIME)
      .contains("Generating Configurations", MEDIUM_TIME)
      .should("not.exist");
    cyGet(TestIds.buildCard, MEDIUM_TIME)
      .contains("Loading Configurations", MEDIUM_TIME)
      .should("not.exist");
    cyGet(TestIds.buildCard, MEDIUM_TIME)
      .contains("Loading", MEDIUM_TIME)
      .should("not.exist");
    this.retryEnvCardDataRetrieval();
  }

  private startDeployment(component: Service) {
    const version = component.getLatestVersion();

    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    cyGet(TestIds.deploySplitToggle, MEDIUM_TIME).should("be.enabled").click();

    cyGet(TestIds.configureDeploy).click();

    cyGet(TestIds.executeDeploy, MEDIUM_TIME).should("be.enabled").click();
  }

  private stepThroughConfigSteps(configStepsAvailable: number) {
    for (let i = 0; i < configStepsAvailable; i++) {
      cy.get(TestIds.next).should("be.visible").click();
    }
  }

  private reviewAndUpdateEndpoint(
    component: Service,
    endpointVisibility: EndpointAccessibility
  ) {
    cy.get(
      `[data-cyid="${component.getEndpointName()}-endpoint-accordion"]`
    ).should("be.visible");
    if (endpointVisibility === EndpointAccessibility.Public) {
      cy.get(`[data-testid="${component.getEndpointName()}-edit-btn"]`)
        .should("be.visible")
        .click();
      cy.get(TestIds.publicVisibility).should("be.visible").click();
      cy.get(TestIds.endpointSubmit).click();
    }
    cyGet(TestIds.next).click();
  }

  private verifyDeploymentStatus() {
    this.retryEnvCardDataRetrieval();

    cy.get(TestIds.devEnvCard).within(() => {
      cyGet(TestIds.stop, LONG_TIME).should("be.visible");
      cy.wait(600);
      cyGet(TestIds.stop, MEDIUM_TIME).should("be.visible");

      cy.get(TestIds.deploymentStatus, LONG_TIME)
        .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
        .should("be.visible");

      cyGet(TestIds.availableEndpoints).should("be.visible");
    });

    this.verifyEndpointIsDeployed(TestIds.devEnvCard);

    cy.get(TestIds.devEnvCard).within(() => {
      cyGet(TestIds.deploymentStatus, SHORT_TIME)
        .contains(DEPLOYMENT_PENDING, SHORT_TIME)
        .should("not.exist");
      cyGet(TestIds.deploymentStatus, SHORT_TIME)
        .contains(DEPLOYMENT_PROGRESSING, SHORT_TIME)
        .should("not.exist");
    });
  }

  private verifyPromotionStatus() {
    this.retryEnvCardDataRetrieval();

    cy.get(TestIds.prodEnvCard).within(() => {
      cyGet(TestIds.stop, LONG_TIME).should("be.visible");
      cy.wait(600);
      cyGet(TestIds.stop, MEDIUM_TIME).should("be.visible");

      cy.get(TestIds.deploymentStatus, LONG_TIME)
        .contains(DEPLOYMENT_SUCCESS, LONG_TIME)
        .should("be.visible");

      cyGet(TestIds.availableEndpoints).should("be.visible");
    });

    this.verifyEndpointIsDeployed(TestIds.prodEnvCard);

    cy.get(TestIds.prodEnvCard).within(() => {
      cyGet(TestIds.deploymentStatus, SHORT_TIME)
        .contains(DEPLOYMENT_PENDING, SHORT_TIME)
        .should("not.exist");
      cyGet(TestIds.deploymentStatus, SHORT_TIME)
        .contains(DEPLOYMENT_PROGRESSING, SHORT_TIME)
        .should("not.exist");
    });
  }

  private verifyDevAccessibility(
    component: Service,
    endpointVisibility: EndpointAccessibility
  ) {
    // Begin workaround for https://github.com/wso2-enterprise/choreo/issues/25414
    this.sideMenu.navigateToOverview();
    this.sideMenu.navigateToDeploy();
    // End workaround for https://github.com/wso2-enterprise/choreo/issues/25414

    this.getDevEndpointURL(endpointVisibility).then((endpointURL) => {
      cy.log(`Endpoint URL: ${endpointURL}`);

      // Non public endpoints are not accessible over the internet
      if (endpointVisibility !== EndpointAccessibility.Public) {
        this.sideMenu.navigateToTest();
        cy.get(TestIds.notificationBanner).should("be.visible");
        this.sideMenu.navigateToManage();
        cy.get(TestIds.noEndpointNotification).should("be.visible");
      }
    });
  }

  private verifyEndpointIsDeployed(envCardLocator: string) {
    for (var i = 0; i < 6; i++) {
      let isEndpointLoaded = false;
      cy.get(envCardLocator, { log: false }).then((envCard) => {
        if (envCard.find(TestIds.endpointStatus).length > 0) {
          cy.get(TestIds.endpointStatus, { log: false }).then(
            ($statusElement) => {
              let statusText = $statusElement.children().eq(0).text();

              const waitTime = 10000;
              if (
                statusText.includes(DEPLOYMENT_PENDING) ||
                statusText.includes(DEPLOYMENT_PROGRESSING)
              ) {
                cy.get(TestIds.commitHistory)
                  .eq(0)
                  .contains(BUILD_FAILED)
                  .should("not.exist");
                cy.log(
                  `Endpoint is ${statusText}, check back in ${
                    waitTime / 1000
                  } seconds`
                );
                cy.wait(waitTime, { log: false });
              } else {
                isEndpointLoaded = true;
              }
            }
          );
        } else {
          cy.wait(5000, { log: false });
        }
      });

      if (isEndpointLoaded) {
        break;
      }
    }

    cy.get(envCardLocator).within(() => {
      cyGet(TestIds.endpointStatus).contains(DEPLOYMENT_SUCCESS);
    });
  }

  private retryEnvCardDataRetrieval() {
    cy.contains(TestIds.progressBar).should("not.exist");

    cy.log("Checking for retry deployment");
    for (let i = 0; i < 4; i++) {
      Utils.clickOnOptionalElement(TestIds.retry, LONG_TIME.timeout);

      Utils.clickOnOptionalElement(TestIds.refresh, SHORT_TIME.timeout);

      Utils.waitIfOptionalElementPresent(TestIds.notDeployed, 3000);
    }

    cy.get(TestIds.retry).should("not.exist");
    cy.get(TestIds.refresh).should("not.exist");
  }

  private retryPromotionToProd(retryCount = 0) {
    cy.log("Checking for retry for promotion to prod");
    retryCount++;
    if (retryCount > 4) {
      return;
    }

    cy.contains(TestIds.progressBar).should("not.exist");

    cy.get("body").then((bdy) => {
      if (bdy.find(TestIds.deploymentFetchError).length > 0) {
        cy.log("Retry count: " + retryCount);
        cy.get(TestIds.deploymentFetchError).within(() => {
          cy.get(TestIds.retry).click();
          cy.wait(LONG_TIME.timeout);
        });
      } else {
        return;
      }
      this.retryPromotionToProd(retryCount);
    });
  }

  private startPromotion(component: Service) {
    const version = component.getLatestVersion();

    cy.get(TestIds.selectVersion).contains(`API v${version}`);

    this.retryPromotionToProd();
    cy.get(TestIds.promote, LONG_TIME).should("be.enabled").click();
  }

  public addNewVersion(
    component: Service,
    branch: string = "feature",
    version: string = "1.1"
  ) {
    this.sideMenu.navigateToDeploy();

    component.updateVersionList(version);

    cy.get(TestIds.selectVersion).click();
    cy.get(TestIds.createVersion).should("be.visible").click();
    cy.get(TestIds.dialog).within(() => {
      cy.get('[data-testid*="feature"]').click();
    });
    cy.get(`[data-value="${branch}"]`).click();
    cy.get(TestIds.dialog).within(() => {
      cy.get(TestIds.versionName).type(version);
      cy.get(TestIds.createDeploymentTrack).click();
      cy.get(TestIds.createDeploymentTrack).should("not.exist");
    });
  }
}
