import { Enums, UsagePlan } from "../../../commons/enums";
import { VERY_SHORT_TIME } from "../../../commons/timeouts";
import { DEV_PORTAL_APIS_SEARCH_URL } from "../../../commons/urls";
import { TestIds } from "../../constants/TestIds";

export class Application {
  private name: string;
  private description: string;

  constructor(name: string, description: string) {
    this.name = name;
    this.description = description;
  }

  getName() {
    return this.name;
  }

  getDescription() {
    return this.description;
  }

  generateCredentials(env: Enums.Environment) {
    cy.get(TestIds.linkKeys).click();
    cy.get(TestIds.envCredentialsMenu(env)).click();
    cy.get(TestIds.generateKey).should("be.visible").click();
    cy.get(TestIds.consumerKey).invoke("val").should("not.be.empty");
  }

  addSubscription(apiName: string, plan: UsagePlan) {
    cy.get(TestIds.subscriptions).click();

    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_APIS_SEARCH_URL(),
      times: 1,
    }).as("getAllApis");

    cy.get(TestIds.createSubscription).click();
    cy.get(TestIds.devPortalBackdropLoader).should("not.exist");

    cy.wait("@getAllApis", VERY_SHORT_TIME).then(() => {
      cy.get(TestIds.apiSubscriptionSearch).should("be.visible").within(() => {
        cy.get('button[title="Open"]').should("be.visible").click();
        cy.get('input[value="Select API"]').click().type(`${apiName}`);
      });

      cy.get("ul>li").contains(apiName).click();
      cy.get(TestIds.subscriptionPolicyCard).contains(plan).click();

      cy.contains("Add Subscription").click();

      this.validateResubscribingApi(apiName);
    });
  }

  private validateResubscribingApi(apiName: string) {
    cy.log(
      "Check whether user can re-subscribe to the API -  " +
        apiName +
        " ,that has already subscribed "
    );

    cy.intercept({
      method: "GET",
      url: DEV_PORTAL_APIS_SEARCH_URL(),
      times: 1,
    }).as("getAllApisForResubscribe");

    cy.get(TestIds.createSubscription).click();
    cy.get(TestIds.devPortalBackdropLoader).should("not.exist");

    cy.wait("@getAllApisForResubscribe", VERY_SHORT_TIME).then(() => {
      cy.get(TestIds.apiSubscriptionSearch).should("be.visible").within(() => {
        cy.get('button[title="Open"]').should("be.visible").click();
        cy.get('input[value="Select API"]').click().type(`${apiName}`);
      });

      cy.contains("No options").click();
      cy.get(TestIds.subscriptionPolicyCard).should("not.exist");
      cy.contains("Add Subscription").should("be.disabled");
    });
  }
}
