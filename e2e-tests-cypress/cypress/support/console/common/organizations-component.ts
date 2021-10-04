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

export class OrganizationComponent {

    static navigateToMembers() {
        cy.get('[data-testid="/user-settings/organization/members"]').click()

    }
    static navigateToGroups() {
        cy.get('[data-testid="/user-settings/organization/groups"]').click()
        cy.wait(2000)

    }
    static navigateToCustomDomains() {
        cy.get('[data-testid="/user-settings/organization/custom-domains"]').click()

    }

    static inviteMembers(email: string, ...groups) {
        cy.wait(300);
        cy.get('div [type="button"] h5').contains('Invite Members').click();
        cy.wait(300);
        cy.get('[data-testid="invite-email"] div div input').should('be.visible').type(email)
        cy.get('#demo-mutiple-checkbox').click()
        this.addGroups(groups)
        cy.get('body').type('{esc}')
        cy.get('[data-testid="invite-btn"]').click({ force: true });
        cy.get('[data-testid="invite-btn"]').should('not.exist');
        cy.log('Invitation sent successfully');
    }

    static selectPendingInvitation() {
        cy.contains('Pending Invitations').click();
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

    private static addGroups(groups: string[]) {
        groups.forEach(v => {
            cy.get('ul[aria-labelledby="demo-mutiple-checkbox-label"] >li').each(($e) => {
                if ($e.text().toLocaleLowerCase() === v.toLocaleLowerCase()) {
                    cy.wrap($e).invoke('attr', 'aria-selected').then(attr => {
                        if (!attr) {
                            cy.wrap($e).click()
                        }
                    })
                } else {
                    cy.wrap($e).invoke('attr', 'aria-selected').then(attr => {
                        if (attr) {
                            cy.wrap($e).click()
                        }
                    })
                }

            })
        })
    }
    static createGroup(groupName: string, groupDescription: string, groupTag: string) {
        cy.get('[data-testid="create-group-btn"]').click();
        cy.contains('Create Group').should("be.visible");
        cy.log("Creating a group!");
        cy.get('[id="create-group-name"]').type(groupName);
        cy.get('[id="filled-multiline-static"]').type(groupDescription);
        cy.get('[id="create-group-tag"]').type(groupTag);
        cy.get('[data-testid="create-group"]').click({ force: true });
        cy.get(`td[value="${groupName}"]`).should('be.visible');
    }
}