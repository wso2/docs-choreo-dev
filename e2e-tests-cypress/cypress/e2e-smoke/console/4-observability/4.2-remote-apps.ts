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

import { generateAppName, getSelectedOrgHandle } from '../../../support/common/utils';

/// <reference types="cypress" />

describe('Observability tests', () => {
  let savedCookies
  let appName: string
  const pathName = "remote-apps"

  before(() => {
    cy.consoleUserLogin().then((user) => {
      const org = user?.orgs.find((org) => org.handle === getSelectedOrgHandle(user));
      cy.clearAllTestData(org);
    });
    cy.getCookies().then((cookies) => {
      savedCookies = cookies
    })

    appName = generateAppName("app");
    cy.log('App name: ', appName);
    cy.get('[data-testid="expand-button"]').click();
    cy.get('[href="/' + pathName + '"]').click();
    cy.get('[id="backdrop-loader"]').should('not.exist');
  })

  beforeEach(() => {
    cy.preserveCookiesForTest(savedCookies);
    cy.restoreLocalStorage();
    cy.viewport(1536, 683);
    cy.get('body').then($body => {
      let appExists = ($body.find('.MuiTableRow-root.MuiTableRow-hover').length > 0);
      if (appExists) {
        cy.get('[data-testid="connect-remote-app-btn"]').should('be.visible');
        cy.get('[data-testid="connect-remote-app-btn"]').click();
      } else {
        cy.get('[data-testid="connect-first-remote-app-btn"]').should('be.visible');
        cy.get('[data-testid="connect-first-remote-app-btn"]').click();
      }
    })
  });

  afterEach(() => {
    cy.deleteApp("remote-app", appName, true);
    cy.saveLocalStorage();
  });

  after(() => {
    cy.userLogout();
  })

  it('test connect existing project', () => {

    cy.get('[data-testid="connect-remote-apps-title"]').should('have.text', 'Connect remote application');
    cy.get('[data-testid="connect-remote-apps-existing-container"]').should('be.visible');
    cy.get('[data-testid="connect-remote-apps-existing-container"]').within(() => {
      cy.contains('Connect existing app').should('be.visible');
      cy.contains('Connect your existing Ballerina project to Choreo').should('be.visible');
      cy.contains('p', 'App Name').should('be.visible');
      cy.get('[data-testid="remoteApp-existing-app-name-input"]').within(() => {
        cy.get('input').click().clear().type(appName);
      })
      cy.get('[data-testid="remoteApp-existing-app-next-button"]').should('have.text', 'Next');
    })

    cy.get('[data-testid="remoteApp-existing-app-next-button"]').click();
    cy.get('[data-testid="create-api-status"]').should('have.text', 'Copy Ballerina configurations');

    cy.contains('button', 'Done').should('be.visible');
    cy.contains('button', 'Done').click();
    cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(0).should('have.text', appName);
  })

  it('test create new project', () => {

    cy.get('[data-testid="remoteApp-new-app-name-input"]').type(appName);
    cy.get('[data-testid="remoteApp-new-app-button"]').should('be.visible');
    cy.get('[data-testid="remoteApp-new-app-button"]').click();

    const curlUnix = `curl -sL -H x-platform:Unix`;
    const curlWindows = `Invoke-Command -ScriptBlock {Invoke-Expression (curl -H @{'x-platform'='Windows'}`;
    // Testing mac
    cy.get('[data-testid="Mac-button"]').should("be.visible");
    cy.get('[data-testid="Mac-button"]').click();

    cy.get('[data-testid="app-linking-command"]').find("input").should("contain.value", curlUnix);

    // Testing Linux
    cy.get('[data-testid="Linux-button"]').should("be.visible");
    cy.get('[data-testid="Linux-button"]').click();

    cy.get('[data-testid="app-linking-command"]').find("input").should("contain.value", curlUnix);

    // Testing Windows
    cy.get('[data-testid="Windows-button"]').should("be.visible");
    cy.get('[data-testid="Windows-button"]').click();

    cy.get('[data-testid="app-linking-command"]').find("input").should("contain.value", curlWindows);

    cy.contains('button', 'Done').should('be.visible');
    cy.contains('button', 'Done').click();
    cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(0).should('have.text', appName);
  })
});
