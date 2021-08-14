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
import { generateAppName } from "../../../support/common/utils";
import { INTEGRATIONS_TEXT,EX_LONG_TIME_OUT} from "../../../support/common/constants";

/// <reference types="cypress" />

describe("Data Mapper", () => {
    let appName: string;

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const selectedOrgHandle = Cypress.env("selectedOrgHandle");
            const orgId = user?.orgs.find((org) => org.handle === selectedOrgHandle).uuid;
            cy.clearAllTestData(orgId);
        });
    });


    after(() => {
        cy.userLogout();
    });

    it("Create DataMapping", () => {
        appName = generateAppName("app");
        cy.log("app name: " + appName);
        cy.createNewApp(INTEGRATIONS_TEXT, appName);
        cy.url().should("include", "app/" + appName + "/develop");
        cy.selectTrigger("Manual");
        cy.get('[data-testid="vertical-close-btn"]').click();

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('string', 'fName', '"John"');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('string', 'lName', '"Smith"');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('int', 'age', '30');


        cy.selectManualTriggerOptions("Statements", "addDataMapping");
 
        cy.get('[data-testid="datamapper-variable-name"]').find('input').first()
            .click({ force: true }).clear().type('emp');
        cy.get('[data-testid="vertical-close-btn"]').click();
        cy.get('[data-testid = "Select Typestring"]').click();
        cy.contains('json').click({force: true});
        cy.get ('[data-testid="datamapper-output-config-save-btn"]'). click ();

        cy.get('[data-testid="datamapper-add-json-attribute-field-btn"]').click({ force: true });
        cy.get ('[data-testid = "datamapper-json-draft-field-form-txt"]').click({ force: true }).type('empName');
        cy.get('[data-testid = "datamapper-json-draft-field-check-btn"]').click({ force: true });
        cy.contains('empName: json').should('be.visible');

        cy.get('[data-testid="datamapper-add-json-attribute-field-btn"]').click({ force: true });
        cy.get('[data-testid = "datamapper-json-draft-field-form-txt"]').click({ force: true }).type('empAge');
        cy.get('[data-testid = "datamapper-json-draft-field-check-btn"]').click({ force: true });
        cy.contains('empAge: json').should('be.visible');

        cy.get('[data-testid="datamapper-json-input-configure-btn"]').click();
        cy.get('[data-testid = "datamapper-input-var-select"]').click();
        cy.contains('fName string').click();
        cy.get('[data-testid="datamapper-input-var-save-btn"]').click({force: true});
        cy.contains('fName: string').should('be.visible');

        cy.get('[data-testid="datamapper-json-input-configure-btn"]').click();
        cy.get('[data-testid = "datamapper-input-var-select"]').click();
        cy.contains('lName string').click();
        cy.get('[data-testid="datamapper-input-var-save-btn"]').click({force: true});
        cy.contains('lName: string').should('be.visible');

        cy.get('[data-testid="datamapper-json-input-configure-btn"]').click();
        cy.get('[data-testid = "datamapper-input-var-select"]').click();
        cy.contains('age int').click();
        cy.get('[data-testid="datamapper-input-var-save-btn"]').click();
        cy.contains('age: int').should('be.visible');

        cy.get('[data-testid="datamapper-expression-box"]').eq(0).click({force: true});
        cy.get('.exp-editor').click().type('{backspace}{backspace}fName+lName'); 
        cy.get('[data-testid="datamapper-save-btn"]').should('not.have.attr', 'disabled');
        cy.get('[data-testid="datamapper-save-btn"]').click();
        cy.waitTillWorkSpace();

        //Mapping using arrow
        // cy.get('[data-testid="datamapper-source-mapping-circle2"]').click();
        // cy.get('[data-testid="datamapper-target-mapping-circle2"]').click();

        cy.get('[data-testid="datamapper-expression-box"]').eq(1).click({force: true});
        cy.get('.exp-editor').click().type('{backspace}{backspace}"age"');
        cy.get('[data-testid="datamapper-save-btn"]').should('not.have.attr', 'disabled');
        cy.get('[data-testid="datamapper-save-btn"]').click();
        cy.waitTillWorkSpace();
        
        cy.get('[data-testid="datamapper-diagram-switch"]').click();

        cy.testRunApp();
        cy.url().should("include", "app/" + appName + "/test");
        cy.get('[data-testid="product-tour-log-panel"]').contains('Starting application', { timeout: 60000 });
        cy.get('[data-testid="product-tour-log-panel"]').contains('Application exited');      
        cy.log("DataMapper created successfully!");

        cy.log('Switch to Develop View');
        cy.get('[data-testid="develop"]').within(()=>{
            cy.get('[id="develop"]').click({force:true});
        });
        cy.url().should("include", "app/" + appName + "/develop");
        cy.waitTillWorkSpace();
        cy.get('[data-testid="vertical-close-btn"]').click();
        
        cy.log('Editing DataMapper');
        cy.get('[data-testid="data-processor-block"]').eq(3).trigger('mouseover').within(() => {
           cy.get('[data-testid="editBtn"]').click({force:true});
        });
        cy.get('[data-testid="datamapper-expression-box"]').eq(1).click({force: true});
        cy.get('.exp-editor').click().type('{backspace}{backspace}{backspace}{backspace}{backspace}"0"');
        cy.get('[data-testid="datamapper-save-btn"]').should('not.have.attr', 'disabled');
        cy.get('[data-testid="datamapper-save-btn"]').click();
        cy.waitTillWorkSpace();

        cy.get('[data-testid=datamapper-diagram-switch]').click();
        cy.log("DataMapper edited successfully!");


        cy.log('Deleting DataMapper');
        cy.get('g:nth-child(9) #DeleteIcon').click({force:true});
        cy.get('[data-testid="delete-logic-block-btn"] > .MuiButton-label').click({force:true});
        cy.log("DataMapper deleted successfully!");
           

    });


})
