export class TestView {

    static navigatTestView() {
        cy.get('#test').click()
        cy.get('[data-testid="backdrop-loader"]').should('not.exist');
    }

    static getTestURL() {
        return cy.get('[data-testid="product-tour-log-panel"] input').eq(0).invoke('attr', 'value')
    }
    static clickTestRunButton() {
        cy.get('#test-run-btn').click()
    }
    static getTestKey() {
        cy.get('[data-testid="get-test-key-btn"]').click()
    }

    static executePostmanTest(postmanKey: string) {
        cy.get('div[data-testid="postman"] > p').click()
        cy.get('[id="backdrop-loader"').should('not.exist');
        cy.get('h3').contains('Postman').should('exist')
        cy.get('[data-testid="click-here"]').click();
        cy.get('[data-testid="api-key"]').should('exist');
        cy.get('[data-testid="api-key"]').type(postmanKey);
        cy.get('[data-testid="api-key-error"]').should('exist');
        cy.log('Test phase successful!');
    }

}