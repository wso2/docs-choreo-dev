import { Enums } from "../../../commons/enums";
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

  addSubscription(apiName: string) {
    cy.get(TestIds.subscriptions).click();
    cy.get(TestIds.createSubscription).click().wait(2000);
    cy.get(
      ".MuiFormControl-root > .MuiInputBase-root > .MuiInputBase-input"
    ).type(apiName);
    cy.get(TestIds.addApiSubscription(apiName)).click();
    cy.get(TestIds.subscriptionClose).click();

    this.validateResubscribingApi(apiName);
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
    cy.get(TestIds.createSubscription).click();
    cy.wait(2000);
    cy.log("Search API " + apiName + " to subscribe");
    cy.get(
      ".MuiFormControl-root > .MuiInputBase-root > .MuiInputBase-input"
    ).type(apiName);
    cy.wait(2000);
    cy.get(TestIds.addApiSubscription(apiName)).should("be.disabled");
    cy.get(TestIds.subscriptionClose).click();
  }

  shareApplication(appName) {
    cy.intercept('POST', '**/share*').as('shareApplication');
    cy.get('[data-testid=appliation-share-btn]').click();
    cy.get('[data-testid=share-cancel-button]').click();
    cy.get('[data-cyid=app-share-dialog]').should('not.exist');
    cy.get('[data-testid=appliation-share-btn]').click();
    cy.get('[data-testid=share-button]').should('be.disabled', true);
    cy.get('[data-testid=app-share-user-email]').type("invitation.user.choreo@gmail.com{enter}");

    cy.log('Check the email address validation');
    cy.get('[data-testid=app-share-user-email]').type("errorEmail{enter}");
    cy.get('[data-testid=invalid-email]').should("exist");

    cy.get('[data-testid=app-share-user-email]').type("errorEmail@example.com{enter}");
    cy.get('[data-testid=invalid-email]').should("not.exist");
    cy.get('[data-testid=app-share-email-address]').should("have.length", 2);

    cy.log('Remove newly added email address');
    cy.get('[data-testid="emails-wrapper"]') 
      .find('[data-testid="app-share-email-address"]') 
      .eq(1)
      .find('.MuiChip-deleteIcon')
      .click(); 
    cy.get('[data-testid=app-share-email-address]').should("have.length", 1);

    cy.get('[data-testid=share-button]').should('not.be.disabled');
    cy.get('[data-testid=shared-app-name]').should('have.text', appName);
    cy.get('[data-testid=share-button]').click();
    cy.wait('@shareApplication').its('response.statusCode').should('eq', 200);
    cy.get('[data-cyid=app-share-dialog]').should('not.exist');

    cy.log("Check existing user's email for the shared application");
    cy.get('[data-testid=appliation-share-btn]').click();
    cy.get('[data-testid=app-share-email-address]').should("have.length", 1);
    cy.get('[data-testid=share-cancel-button]').click();

    cy.log("Check whether the shared emails are listed under app overview");
    cy.get('[data-testid=app-share-email-addresses]').should("have.length", 1);
  }
}
