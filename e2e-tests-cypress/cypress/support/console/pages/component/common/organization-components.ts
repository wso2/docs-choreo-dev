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
import { INVITATION_EMAIL } from './constants';

/// <reference types="cypress" />

export class OrganizationComponent {
  static memberEmail: string;

  static memberName: string;

  static roleName = 'E2EtestRole';

  static roleDescription = 'This Role is created by E2E test run.';

  static roleTag = 'testRoleTag';

  static navigateToMembers() {
    cy.get('[data-testid="/user-settings/organization/members"]').click();
  }

  static navigateToGroups() {
    cy.get('[data-testid="/user-settings/organization/groups"]').click();
    cy.wait(2000);
  }

  static navigateToCustomDomains() {
    cy.get(
      '[data-testid="/user-settings/organization/custom-domains"]'
    ).click();
  }

  static inviteMembers(email: string, ...roles) {
    cy.wait(300);
    cy.get('[data-cyid="invite-members"]').click();
    cy.wait(300);
    cy.get('[data-cyid="chip-email-addresses"] div div input')
      .should('be.visible')
      .type(email);
    cy.get('[data-cyid="select-roles"]').click();
    this.addRoles(roles);
    cy.get('body').type('{esc}');
    cy.get('[data-cyid="btn-invite"]').click({ force: true });
    cy.get('[data-cyid="btn-invite"]').should('not.exist');
    cy.log('Invitation sent successfully');
  }

  static selectPendingInvitation() {
    cy.get('[data-cyid="pending-invitation"]').click();
  }

  static selectMembers() {
    cy.get('data-testid="members"').click();
  }

  static deleteRecord(email: string) {
    cy.contains('td', email).trigger('mouseover');
    cy.get('[data-testid="api-delete-btn"]').click();
    cy.get('button > span > h5').should('be.visible');
    cy.get('button > span > h5').contains('Delete').click();
  }

  private static addRoles(roles: string[]) {
    roles.forEach((v) => {
      cy.get('ul[aria-labelledby="demo-mutiple-checkbox-label"] >li').each(
        ($e) => {
          if ($e.text().toLocaleLowerCase() === v.toLocaleLowerCase()) {
            cy.wrap($e)
              .invoke('attr', 'aria-selected')
              .then((attr) => {
                if (!attr) {
                  cy.wrap($e).click();
                }
              });
          } else {
            cy.wrap($e)
              .invoke('attr', 'aria-selected')
              .then((attr) => {
                if (attr) {
                  cy.wrap($e).click();
                }
              });
          }
        }
      );
    });
  }

  static createRole(
    roleName: string,
    roleDescription: string,
    roleTag: string
  ) {
    cy.get('[data-cyid="btn-create-role"]').click();
    cy.contains('Create Role').should('be.visible');
    cy.log('Creating a Role');
    cy.get('[data-cyid="text-field-role-name"]').type(roleName);
    cy.get('[data-cyid="text-field-role-description"]').type(roleDescription);
    cy.get('[data-cyid="chip-role-tag"]').type(roleTag);
    cy.get('[data-cyid="btn-role-create"]').click({ force: true });
    cy.get('[data-cyid="checkbox-role-permission-APIM-PUBLISHER"]').click();
    cy.get('[data-cyid="checkbox-role-permission-APIM-SUBSCRIBER"]').click();
    cy.log('Created Roles APIM-PUBLISHER and APIM-SUBSCRIBER');
    cy.get('[data-cyid="btn-create"]').click();
  }

  static addPermissions() {
    cy.contains('td', OrganizationComponent.roleName).should('be.visible');
    cy.contains('td', OrganizationComponent.roleTag).should('be.visible');
    cy.contains('td', OrganizationComponent.roleName).click();
    OrganizationComponent.inviteMembers(
      INVITATION_EMAIL,
      OrganizationComponent.roleName
    );
    OrganizationComponent.selectPendingInvitation();
    cy.contains('td', INVITATION_EMAIL).should('be.visible');
    OrganizationComponent.deleteRecord(INVITATION_EMAIL);
  }

  static addMembertoRole(roleName: string) {
    cy.get('[data-cyid="search-app"]').type(roleName);
    cy.get('[id="menu-appbar"]').click();
    //cy.contains('td', OrganizationComponent.roleName).click();
    cy.get('[id="tags-standard"]')
      .click()
      .type(OrganizationComponent.memberName);
    cy.contains(
      '[id="tags-standard-popup"]',
      OrganizationComponent.memberName
    ).should('be.visible');
    cy.contains(
      '[id="tags-standard-popup"]',
      OrganizationComponent.memberName
    ).click();
    cy.get('[data-testid="add-member-btn"]').click();
    cy.contains('td', OrganizationComponent.memberEmail).should('be.visible');
    cy.log('Member added to the role successfully');
    cy.log('Removing member from the role');
    cy.contains('tr', OrganizationComponent.memberEmail).within(() => {
      cy.get('[data-testid="api-delete-btn"]').click();
    });
    cy.get('[data-testid="delete-member-btn"]').click();
    cy.contains('td', OrganizationComponent.memberEmail).should('not.exist');
    cy.log('Member removed from the role successfully');
  }

  static deleteCreatedRole(roleName: string) {
    cy.get('[data-cyid="search-app"]').type(roleName);
    cy.contains('td', OrganizationComponent.roleName).should('be.visible');
    cy.contains('td', OrganizationComponent.roleName).trigger('mouseover');
    cy.get('[data-cyid="btn-delete-role"]').click();
    cy.log('Deleting the created Role');
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]').click();
    cy.contains('td', OrganizationComponent.roleName).should('not.exist');
    cy.log('Role deleted successfully'!);
  }
}
