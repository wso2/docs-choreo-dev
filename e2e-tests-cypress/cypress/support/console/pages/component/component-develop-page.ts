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

export class ComponentDevelopPage {
  static currentTime = new Date();

  static futureTime = new Date(
    this.currentTime.getTime() + Cypress.env("recTime")
  );

  static getComponentURL(fileID) {
    cy.get('[data-testid="component-develop-edit-code"]',{timeout:120000})
      .should("be.visible")
      .invoke("attr", "href")
      .then((href) => {
        cy.url().then((url) => {
          cy.task("writeTestData", {
            fileName: fileID,
            key: "componentURL",
            value: url,
          });
          const accessURL = url.split("/organizations")[0] + href.replace(/ /g,'').replace(/\n/g,'');
          cy.task("writeTestData", {
            fileName: fileID,
            key: "accessURL",
            value: accessURL,
          });
        });
      });

    this.editInCodeServer(fileID);
  }

  private static editInCodeServer(fileID) {
    cy.readFile(`${Cypress.env("tempfile")}${fileID}.json`).then((data) => {
      this.startCodeServer(
        data.orgData.orgId,
        data.orgData.handle,
        data.authData.projectId,
        data.authData.id,
        data.authData.header
      );
    });
  }

  private static startCodeServer(
    orgid,
    orgHandler,
    projectId,
    componentId,
    header
  ) {
    const qry = {
      query: `mutation{ startCodeServer(orgId:${orgid},orgHandler:"${orgHandler}", projectId:"${projectId}",componentId:"${componentId}") }`,
    };
    const appSvcURL = Cypress.env("appSvcURL");

    cy.request({
      method: "POST",
      url: `${appSvcURL}/graphql`,
      body: JSON.stringify(qry),
      headers: header,
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
      cy.contains(verb.toUpperCase()).click();
      cy.wait(1000);
    });
    cy.get("body").type("{esc}");
  }

  static addLabels(labels: string[]) {
    const lblArr = [];
    cy.contains("+ Add labels",{timeout:120000}).click();
    labels.forEach((label) => {
      cy.get("#labels-filled").click();
      cy.contains(label).click();
      cy.wait(200);
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

  static verifyLatestCommit(commitMessage: string) {
    cy.get(`[title="${commitMessage}"]`).should("be.visible");
  }

  static getVersion() {
    return cy.get('[id="version-picker"]>div').then((v) => {
      return v.text().trim();
    });
  }
}
