/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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



export class VSExplorer {
  static sourceControllerBtn = '[aria-label*="Source Control"]';

  static terminal = ".xterm-helper-textarea";

  
  static waitTillCodespaceLoad() {
    cy.get(".monaco-highlighted-label").contains(".bal").click();
    cy.get('div[class*=".bal-name-file-icon"]  [title="Delete"]').click();
    cy.get('[aria-label*="Are you sure you want to delete"] [title="Delete"]').click();
  }

  static selectExplorer() {
    cy.get(".codicon-explorer-view-icon").click();
  }

  static selectSourceControl() {
    cy.get('ul[aria-label="Active View Switcher"] > li').eq(2).click();
  }

  static verifyVsCodeWorkspace() {
    cy.get('[title*=".bal Diagram"]');
  }

  static closeTab() {
    this.verifyVsCodeWorkspace();
    cy.get(".codicon-close").then((b) => {
      if (b.length > 0) {
        b.each(function () {
          // can not use an arrow function as this scope changes.
          this.click();
        });
      }
    }).wait(2000);
  }

  static pushChangesToChoreo(commitMessage: string) {
    cy.get('div[id="wso2.ballerina"] a').eq(1).click({ multiple: true });
    cy.get('[placeholder="Enter the commit message"]').type(`${commitMessage}{enter}`);
  }

  static clearNotifications() {
    cy.get('[title="Clear Notification (Delete)"]').click({ multiple: true });
  }

  static selectBallerinaWorkspace() {
    cy.get('[aria-label="Ballerina Low-Code"]').click();
  }

  static enterCommandInTerminal(command: string, waitTime: number = 20000) {
    cy.get("body").then((bd) => {
      if (bd.find(".xterm-helpers").length == 0) {
        cy.wrap(bd).type("{ctrl}`");
      }
    });
    cy.get(VSExplorer.terminal).click().type(`${command}{enter}`).wait(waitTime);
  }

  static creteNewBranch(branchName: string) {
    cy.get("[title*='.bal Diagram']").wait(4000);
    cy.get('[id="wso2.ballerina"]')
    cy.get('[id="status.scm"]').eq(0).click();
    cy.get(".quick-input-widget")
    cy.get('[aria-describedby="quickInput_message"]').type(`${branchName}{enter}`).wait(3000);
  }

  static commitPush(commitMessage) {
    this.enterCommandInTerminal("bash /config/workspace/.githooks/pre-commit");
    this.enterCommandInTerminal("rm /config/workspace/.githooks/pre-commit", 2000);
    this.enterCommandInTerminal("git add .", 2000);
    this.enterCommandInTerminal(`git commit -m "${commitMessage}"`, 4000);
    this.enterCommandInTerminal("git push --set-upstream origin feature", 4000);
  }

  static typeCode(fileName: string) {
    this.closeTab();
    this.waitTillCodespaceLoad();
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

  static pasteCode(fileName) {
    this.closeTab();
    this.waitTillCodespaceLoad();
    this.createFile(fileName);
    this.selectExplorer();
    cy.contains(fileName).click();
    cy.get('div[class="view-line"]').should("be.visible").click();
    cy.readFile(`cypress/fixtures/${fileName}`).then((code) => {
      cy.focused().then($destination => {
        const pasteEvent = Object.assign(new Event('paste', { bubbles: true, cancelable: true }), {
          clipboardData: {
            getData: (type = 'text') => code,
          },
        });
        $destination[0].dispatchEvent(pasteEvent);
      });
    });
  }

  private static createFile(fileName: string) {
    cy.get('[aria-label="Diagram Explorer"] .workspace-name-folder-icon').click().wait(2000);
    cy.get('[title="New File"]').eq(0).should("be.visible").click().wait(2000);
    cy.get('[aria-describedby="quickInput_message"]').type(`${fileName}{enter}`).wait(2000);
    cy.get('[aria-label="Diagram Explorer"] .monaco-icon-name-container').eq(0).click();
    cy.contains(fileName).should("be.visible");
  }

  static getCodeLense(index: number) {
    return cy.get(`[widgetId="codelens.widget-${index}"]`).eq(0);
  }

  static matchCodeLense(index: number, regex: RegExp) {
    return VSExplorer.getCodeLense(index).invoke("text").should("match", regex);
  }

  static getActiveWebview() {
    return cy.get('iframe[class="webview ready"]')
      .eq(1)
      .its("0.contentDocument")
      .should("exist")
      .its("body")
      .should("not.be.undefined")
      .then((body) => {
        return cy
          .wrap(body)
          .find("#active-frame")
          .its("0.contentDocument")
          .should("exist")
          .its("body")
          .should("not.be.undefined")
          .then((body) => {
            return cy.wrap(body);
          });
      });
  }

  static clickPerfGraph(index: number) {
    VSExplorer.getActiveWebview()
      .find(".diagram")
      .eq(0)
      .find("circle")
      .eq(index)
      .click({ force: true });
  }
}
