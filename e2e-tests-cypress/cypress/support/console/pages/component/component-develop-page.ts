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

import { Utils } from "../../utils";

export class ComponentDevelopPage {
  static currentTime = new Date();

  static futureTime = new Date(
    this.currentTime.getTime() + Cypress.env("recTime")
  );

  static getComponentURL() {
    cy.get('[data-testid="component-develop-edit-code"]')
      .invoke("attr", "href")
      .then((href) => {
        cy.url().then((url) => {
          Utils.saveComponentURL();
          const accessURL = `${url.split("/organizations")[0]}${href
            .replace(/ /g, "")
            .replace(/\n/g, "")}`;
          Cypress.env(`accessURL`, accessURL);
        });
      });
  }

  static addResources(path: string, ...verbs) {
    cy.get('[id="backdrop-loader"').should("not.exist");
    cy.get('[data-testid="delete-all-operations-btn"]').click();
    this.addHTTPVerb(verbs);
    cy.get("#operation-target").type(path);
    cy.get('[data-testid="add-btn"]').click();
    cy.get("button > span > h5").contains("Save").click();
    cy.contains("Successfully updated the definition.").should("be.visible");
  }

  private static addHTTPVerb(verbs: string[]) {
    cy.get("#mui-component-select-verbs").click();
    verbs.forEach((verb) => {
      cy.contains(verb.toUpperCase()).click().wait(100);
    });
    cy.get("body").type("{esc}");
  }

  static addLabels(labels: string[]) {
    const lblArr = [];
    cy.contains("+ Add labels", { timeout: 120000 }).click();
    labels.forEach((label) => {
      cy.get("#labels-filled").click();
      cy.contains(label).click().wait(100);
    });
    cy.get('div[role="button"]>.MuiChip-label').should(
      "have.length",
      labels.length
    );
    cy.get('div[role="button"]>.MuiChip-label').each((e) => {
      lblArr.push(e.text());
    });
    cy.get('[data-testid="save-labels"]').click();
    return cy.wrap(lblArr);
  }


  static selectBranch(newBranch: string) {
    let branches = [];
    cy.get('[aria-label="Without label"]').click();
    cy.get("[data-value]>span").each((q) => {
      branches.push(q.text());
      if (q.text() === newBranch) {
        cy.wrap(q).click();
      }
    });
    return cy.wrap(branches);
  }

  static getVersion() {
    return cy
      .get('[data-cyid="version-picker"]>div')
      .then((v) => v.text().trim());
  }

  static refreshBranchCommit() {
    cy.get('[title="Refetch Branches"]').click();
    cy.get('[title="Refetch Commits"]').click();
  }
}
