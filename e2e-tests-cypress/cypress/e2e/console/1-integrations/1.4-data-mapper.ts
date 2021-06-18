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
        cy.consoleUserLogin();
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

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('string', 'fName', '"John"');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('string', 'lName', '"Smith"');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('int', 'age', '30');

        cy.selectManualTriggerOptions("Statements", "addDataMapping");
 
        cy.get ('[data-testid="datamapper-variable-name"]').click().clear().type('emp');
        cy.get('[data-testid = "Select Typestring"]').click();
        cy.contains('json').click({force: true});
        cy.get ('[data-testid="datamapper-output-config-save-btn"]'). click ();

        cy.get('[data-testid="datamapper-add-json-attribute-field-btn"]').click();
        cy.get ('[data-testid = "datamapper-json-draft-field-form-txt"]').click().type('empName');
        cy.get('[data-testid = "datamapper-json-draft-field-check-btn"]').click();
        cy.contains('empName: string').should('be.visible');

        cy.get('[data-testid="datamapper-add-json-attribute-field-btn"]').click();
        cy.get('[data-testid = "datamapper-json-draft-field-form-txt"]').click().type('empAge');
        cy.get('[data-testid = "datamapper-json-draft-field-check-btn"]').click();
        cy.contains('empAge: string').should('be.visible');

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
        cy.get('.view-lines').eq(0).click().type('{backspace}{backspace}fName+lName');
        cy.get('[data-testid="datamapper-save-btn"]').should('not.have.attr', 'disabled');
        cy.get('[data-testid="datamapper-save-btn"]').click();
        cy.waitTillWorkSpace();

        //Mapping using arrow
        // cy.get('[data-testid="datamapper-source-mapping-circle2"]').click();
        // cy.get('[data-testid="datamapper-target-mapping-circle2"]').click();

        cy.get('[data-testid="datamapper-expression-box"]').eq(1).click({force: true});
        cy.get('.view-lines').eq(0).click().type('{backspace}{backspace}"age"');
        cy.get('[data-testid="datamapper-save-btn"]').should('not.have.attr', 'disabled');
        cy.get('[data-testid="datamapper-save-btn"]').click();
        cy.waitTillWorkSpace();
        
        cy.get('[data-testid="datamapper-diagram-switch"]').click();

        const loadRunTxt = "Running...";
        cy.testRunApp();
        cy.get(".product-tour-logs-panel").contains(loadRunTxt).should("exist");
        cy.get(".product-tour-logs-panel").contains(loadRunTxt).should("not.exist", 50000);      
        cy.log("DataMapper created successfully!");

        
        cy.get('g:nth-child(9) > .statement:nth-child(1) > g:nth-child(1) > g:nth-child(1) .main-process-wrapper:nth-child(1) > .process-options-wrapper:nth-child(4) > .edit-icon-wrapper:nth-child(3) #Edit-Button #EditGroup > rect:nth-child(2)').click({force:true});
        cy.get('[data-testid="datamapper-expression-box"]').eq(1).click({force: true});
        cy.get('.view-lines').eq(0).click().type('{backspace}{backspace}{backspace}"0"');
        cy.get('[data-testid="datamapper-save-btn"]').should('not.have.attr', 'disabled');
        cy.get('[data-testid="datamapper-save-btn"]').click();
        cy.waitTillWorkSpace();

        cy.get('[data-testid=product-tour-code-view]').click();
        cy.get('[data-testid="vertical-close-btn"]').click();
        cy.get('[data-testid=datamapper-diagram-switch]').click();
        cy.log("DataMapper edited successfully!");


        cy.get('g:nth-child(9) #DeleteIcon').click({force:true});
        cy.get('[data-testid="delete-logic-block-btn"] > .MuiButton-label').click({force:true});
        cy.get('[data-testid=product-tour-code-view]').click();
        cy.get('[data-testid="vertical-close-btn"]').click();
        cy.log("DataMapper deleted successfully!");
           

    });


})
