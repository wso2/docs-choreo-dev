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


export class Marketplace {
  static connectorResults = '[data-testid="search-results"]>a';

  static searchConnector(connectorName: string) {
    cy.get('[data-testid="search-field"]').type(connectorName);
    cy.get('[data-testid="search-button"]').click();
    return cy.get(`[data-testid*="${connectorName}"]`);
  }

  static filterByChoreo() {
    cy.get('[data-testid="choreo-filter"]').click();
    cy.get('[data-testid="choreo-filter"]').contains('Choreo').should('be.visible');
  }

  static filterByMyOrganization() {
    cy.get('[data-testid="my-organization-filter"]').click();
    cy.get('[data-testid="my-organization-filter"]').contains('My Organization')
  }

  static filterByFree() {
    cy.get('[data-testid="Free-checkbox"]').click();
    cy.get('div[role="button"] > span').contains('Free').should('be.visible');
  }

  static filterByFreemium() {
    cy.get('[data-testid="Freemium-checkbox"]').click();
    cy.get('div[role="button"] > span')
      .contains('Freemium')
      .should('be.visible');
  }

  static filterByPaid() {
    cy.get('[data-testid="Paid-checkbox"]').click();
    cy.get('div[role="button"] > span').contains('Paid').should('be.visible');
  }

  static clearSelectedFilters() {
    cy.get('[aria-label="delete-filter-chip"]').click({ multiple: true });
  }

  static collapsAndExpandPrice() {
    cy.get('ul[role="tree"] >li').contains('Price');
  }

  static filterByCategory(mainCategory: string, subCategory: string) {
    cy.get('[role="group"]>div>div').then((ele) => {
      cy.wrap(ele)
        .contains('See')
        .then((e) => {
          if (e.text() === 'See More') {
            cy.wrap(e).click();
          }
        });
    });
    cy.get(`[data-testid="${mainCategory}"]`).click();
    cy.get('div[role="button"] > span')
      .contains(`${mainCategory}`)
      .should('be.visible');
    if (subCategory) {
      cy.get(`[data-testid="${subCategory}"]`).should('be.visible').click();
      cy.get('div[role="button"] > span')
        .contains(`${subCategory}`)
        .should('be.visible');
    }
    cy.get(`[data-testid="${mainCategory}"]>div>div>svg`).click();
  }

  static navigateToConnectorOverview() {
    cy.get(this.connectorResults).click();
  }

  static getConnectorName() {
    return cy.get('[data-testid="connector-name"]').invoke('text');
  }

  static getConnectorTags() {
    const tags = [];
    return cy
      .get('[data-testid="search-results"] div>div>div>span')
      .each((v) => tags.push(v.text()));
  }

  // trigger related functions
  static navigateToTriggersTab() {
    cy.get('[data-testid="triggers-tab"]').click();
  }

  static searchTrigger(triggerName: string) {
    cy.get('[data-testid="search-field"]').clear().type(triggerName);
    cy.get('[data-testid="search-button"]').click();
    return cy.get(`[data-testid*="${triggerName}"]`);
  }

  static getTriggerName() {
    return cy.get('[data-testid="trigger-name"]').invoke('text');
  }

  static getTriggerTags() {
    const tags = [];
    return cy
      .get('[data-testid="search-results"] div>div>div>span')
      .each((v) => tags.push(v.text()));
  }

}
