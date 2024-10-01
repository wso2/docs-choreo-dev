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

  addPermissionToApplication(permissionName: string) {
    cy.get(TestIds.applicationBar).should("be.visible").click();
    cy.get(TestIds.search).trigger("mouseover");
    cy.get(TestIds.searchAppText).type(this.name);
    cy.get(TestIds.applicationList(this.name)).should("be.visible").click();
    cy.get(TestIds.applicationTokenType).should("be.visible");
    cy.contains(this.name).should("be.visible");
    cy.get(TestIds.applicationEdit).should("be.visible").click();
    cy.get(TestIds.permissionsField).should("be.visible").click().wait(2000);
    cy.get('li[data-option-index="0"]')
      .contains(permissionName)
      .then((option) => {
        option[0].click();
      });
    cy.contains(permissionName).should("be.visible");
    cy.get(TestIds.createBtn).click();
    cy.get(TestIds.createBtn).should("not.exist");
    cy.contains(`Updating Application: ${this.name}`).should("be.visible");
    cy.contains(`Updating Application: ${this.name}`)
      .should("not.exist")
      .wait(5000); // Extra wait because the application is not updated immediately
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
