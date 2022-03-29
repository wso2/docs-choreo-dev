import { EntryPlugin } from "webpack";
import { ComponentTemplate } from "../enum/component-template";

export class VSExplorer {
  static sourceControllerBtn = '[aria-label*="Source Control"]';

  static terminal = ".xterm-helper-textarea";

  static count: number = 0;

  static waitTillCodespaceLoad() {
    cy.get('[aria-label*=".bal Diagram"]', { timeout: 300000 }).should(
      "be.visible"
    );
    cy.get(".monaco-highlighted-label").contains(".bal").click();
    cy.get('div[class*=".bal-name-file-icon"]  [title="Delete"]')
      .should("be.visible")
      .click();
    cy.get('[aria-label*="Are you sure you want to delete"] [title="Delete"]', {
      timeout: 120000,
    })
      .should("be.visible")
      .click();
  }

  static selectExplorer() {
    cy.get('.codicon-explorer-view-icon')
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
    cy.get('.codicon-close').then((b) => {
      if (b.length > 0) {
        b.each(function () {
          // can not use an arrow function as this scope changes.
          this.click();
        });
      }
    });
    cy.wait(3000);
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

  static enterCommandInTerminal(command: string, waitTime: number = 20000) {
    cy.get("body").then((bd) => {
      if (bd.find(".xterm-helpers").length == 0) {
        cy.wrap(bd).type("{ctrl}`");
      }
    });
    cy.get(VSExplorer.terminal, { timeout: 120000 }).click();
    cy.get(VSExplorer.terminal).type(`${command}{enter}`);
    cy.wait(waitTime);
  }

  static createNewBranch() {
    this.closeTab();
    cy.wait(5000)
    this.waitTillCodespaceLoad();
    this.enterCommandInTerminal("git branch feature", 2000);
    this.enterCommandInTerminal("git checkout feature", 2000);
  }

  static commitPush(commitMessage) {
    this.enterCommandInTerminal("bash /config/workspace/.githooks/pre-commit");
    this.enterCommandInTerminal(
      "rm /config/workspace/.githooks/pre-commit",
      2000
    );
    this.enterCommandInTerminal("git add .", 2000);
    this.enterCommandInTerminal(`git commit -m "${commitMessage}"`, 2000);
    this.enterCommandInTerminal("git push --set-upstream origin feature", 2000);
  }

  static typeCode(fileName: string, template = ComponentTemplate.REST) {
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
            cy.wrap(e).type(`${element}\n`).wait(4000); // add time to code format
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
    cy.get(
      '[aria-label="Diagram Explorer"] .workspace-name-folder-icon'
    ).click();
    cy.wait(2000);
    cy.get('[title="New File"]').eq(0).should("be.visible").click();
    cy.wait(2000);
    cy.get('[aria-describedby="quickInput_message"]')
      .should("be.visible")
      .type(fileName);
    cy.wait(2000);
    cy.get('[aria-describedby="quickInput_message"]')
      .should("be.visible")
      .type("{enter}");
    cy.wait(2000);
    cy.get('[aria-label="Diagram Explorer"] .monaco-icon-name-container')
      .eq(0)
      .click();

    cy.contains(fileName).should("be.visible");
  }
}
