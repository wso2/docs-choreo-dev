import { ComponentTemplate } from "../enum/component-template";

export class VSExplorer {
  static sourceControllerBtn = '[aria-label*="Source Control"]';

  static terminal = ".xterm-helper-textarea";

  static count: number = 0;

  static waitTillCodespaceLoad() {
    cy.get('[aria-label="service.bal Diagram"]', { timeout: 300000 }).should(
      "be.visible"
    );
    cy.get(".monaco-highlighted-label").contains("service.bal").click();
    cy.get('div[class*="service.bal-name-file-icon"]  [title="Delete"]')
      .should("be.visible")
      .click();
    cy.get('[aria-label*="Are you sure you want to delete"] [title="Delete"]',{timeout:120000})
      .should("be.visible")
      .click();
  }

  private static waitTillCodespaceLoadForWebhook() {
    cy.get('[aria-label="webhook.bal Diagram"]').should("be.visible");
  }

  static selectExplorer() {
    cy.get('a[aria-label="Explorer (Ctrl+Shift+E)"]')
      .should("be.visible")
      .click();
  }

  static selectSourceControl() {
    cy.get('ul[aria-label="Active View Switcher"] > li')
      .eq(2)
      .should("be.visible")
      .click();
  }

  static closeTab() {
    cy.get('[title="Close (Ctrl+W)"]').then((b) => {
      if (b.length > 0) {
        b.each(function () {
          // can not use an arrow function as this scope changes.
          this.click();
        });
      }
    });
  }

  static pushChangesToChoreo(commitMessage: string) {
    cy.get('div[id="wso2.ballerina"] a').eq(1).click({ multiple: true });
    cy.get('[placeholder="Enter the commit message"]')
      .should("be.visible")
      .type(`${commitMessage}{enter}`);
  }

  static clearNotifications() {
    cy.get('[title="Clear Notification (Delete)"]').click({ multiple: true });
  }

  static selectBallerinaWorkspace() {
    cy.get('[aria-label="Ballerina Low-Code"]').should("be.visible").click();
  }

  static enterCommandInTerminal(command: string) {
    cy.get("body").then((bd) => {
      if (bd.find(".xterm-helpers").length == 0) {
        cy.wrap(bd).type("{ctrl}`");
      }
    });
    cy.get(VSExplorer.terminal,{timeout:120000}).click();
    cy.get(VSExplorer.terminal).type(`${command}{enter}`);
    cy.wait(20000);
  }

  static typeCode(fileName: string, template = ComponentTemplate.REST) {
    switch (template) {
      case ComponentTemplate.WEBHOOK:
        this.waitTillCodespaceLoadForWebhook();
        break;
      default:
        this.waitTillCodespaceLoad();
    }
    this.closeTab();
    this.createFile(fileName);
    this.selectExplorer();
    cy.contains(fileName).click();
    cy.get('div[class="view-line"]').should("be.visible").click();
    cy.readFile(`cypress/fixtures/${fileName}`).then((code) => {
      const codeArr = code.split("\n"); // create an array from the read file content.
      codeArr.forEach((element) => {
        if (element !== null && element !== "") {
          // file may content empty lines. ignore them
          cy.focused().then((e) => {
            cy.wrap(e).type(`${element}\n`).wait(5000); // add time to code format
          });
        }
      });
    });
    return cy.get(`div${VSExplorer.sourceControllerBtn}>div`).invoke("text");
  }

  static waitTillCodeSyncWithChoreo() {
    cy.get('[id="wso2.ballerina"]>a').should("not.have.attr", "style", true);
    cy.wait(10000);
  }

  private static createFile(fileName: string) {
    cy.get('[aria-label="Diagram Explorer"] .monaco-icon-name-container')
      .eq(0)
      .click();
    cy.get('[title="New File"]')
      .eq(0)
      .should("be.visible")
      .click();
      cy.wait(2000)
    cy.get('[aria-describedby="quickInput_message"]')
      .should("be.visible")
      .type(fileName);
      cy.wait(5000)
      cy.get('[aria-describedby="quickInput_message"]')
      .should("be.visible")
      .type('{enter}');
      cy.wait(5000)
    cy.get('[aria-label="Diagram Explorer"] .monaco-icon-name-container')
      .eq(0)
      .click();

    cy.contains(fileName).should("be.visible");
  }
}
