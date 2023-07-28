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

import { cyGet } from "../../../commons/cy";
import { MEDIUM_TIME } from "../../../commons/timeouts";
import { Utils } from "../../../commons/utils";
import { GraphQL } from "../../apis/graphql";

export class ComponentListingPage {
  static deleteComponent(componentName: string) {
    cyGet(`tbody>tr`).should("be.visible").realHover();

    cy.get('[data-cyid="btn-contained-button"]').should("be.visible").click();
    cy.get('[data-cyid="delete-confirmation-cancel-button-button"]').should(
      "be.enabled"
    );
    cy.get('[data-cyid="confirm-name"]').within(() => {
      cy.get("input").type(componentName).type("{enter}");
    });

    cy.get('[data-cyid="delete-confirmation-dialog-content"]').should(
      "not.exist"
    );
    this.verifyDeletion();
  }

  static visitToProjectOverview() {
    cy.get('[data-testid="main-left-nav-item-Project"]')
      .should("be.visible")
      .click();
  }

  static getProductionEnvStats() {
    cy.get('[name="env"]').click();
    cy.contains("Production").click().wait(3000);
    cy.get('[data-cyid="total-apis"]').should("have.text", "1");
    cy.get('[data-cyid="total-traffic"]').should("have.text", "2");
    cy.get('[data-cyid="avg-latency"]')
      .invoke("text")
      .then((text) => {
        const avgLatency = parseInt(text);
        expect(avgLatency).to.be.greaterThan(0);
      });
    cy.get('[data-cyid="errors"]').should("have.text", "0");
  }

  static getDevelopmentEnvStats() {
    cy.get('[name="env"]').click();
    cy.contains("Development").click().wait(3000);
    cy.get('[data-cyid="total-apis"]').should("have.text", "1");
    cy.get('[data-cyid="total-traffic"]').should("have.text", "2");
    cy.get('[data-cyid="avg-latency"]')
      .invoke("text")
      .then((text) => {
        const avgLatency = parseInt(text);
        expect(avgLatency).to.be.greaterThan(0);
      });
    cy.get('[data-cyid="errors"]').should("have.text", "0");
  }

  static visitToAComponent(componentName: string) {
    cy.get('[data-cyid="listing"]').should("be.visible").click();

    cy.get("#filterByType").click().should("have.length", 1);
    cy.get(
      '[data-cyid="project-components-multi-select-all-button-button"]'
    ).click();
    cy.contains("Components Listing").click();

    cy.get("tr p").contains(componentName).should("be.visible").click();

    cy.get('[data-cyid="home"]').should("be.visible");
    Utils.saveComponentURL();
    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.get("[data-cyid=create-time]").should("be.visible");
    cy.log("Successfully visited to the component");
  }

  private static verifyDeletion() {
    const { projectId } = Cypress.env("component");
    GraphQL.getComponents(projectId).then((res) => {
      expect(res.status).to.be.equal(200);
      expect(res.components).to.be.empty;
    });
  }
}
