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

import { normalizeText } from './choreo-utils';

Cypress.Commands.add('preserveCookiesForTest', (cookies) => {
    cookies.map((cookie) => {
        cy.setCookie(cookie.name, cookie.value, {
            domain: cookie.domain,
            expiry: cookie.expires,
            httpOnly: cookie.httpOnly,
            path: cookie.path,
            secure: cookie.secure
        })

        Cypress.Cookies.defaults({
            preserve: cookie.name
        })
    })
})

Cypress.Commands.add('waitTillWorkSpace', () => {
    cy.get('[data-testid="setting-up-workspace"]').should('not.exist');
}),

Cypress.Commands.add('createNewApp', (type:string, name: string) => {
    cy.get('[id="backdrop-loader"').should('not.exist');

    if(type == "integration"){
        cy.get('[href="/integrations"]').click();
    } else if(type == "service"){
        cy.get('[href="/services"]').click();
    }
    cy.log("Page loaded successfully");
    if(type == "integration"){
        cy.get('[href="/integrations"]').click();
        cy.log("Integrations page loaded successfully");
    } else if(type == "service"){
        cy.get('[href="/services"]').click();
        cy.log("Services page loaded successfully");
    }

    cy.log("Creating a new application with name : ", name);
    cy.contains('button', 'Create').click();
    cy.get('input').type(name);
    cy.contains('button', 'Create').click();

    cy.waitTillWorkSpace();
    cy.get('.diagram-canvas').should('exist');
    cy.log("Application created successfully with name: " + name);
}),

/**
 * Validating components by going through the source code view and checking the terms
 *
 * @param sourceLines
 */
Cypress.Commands.add('checkSourceCodeForValidation', (sourceLines: string) => {
    cy.get('[data-testid="code-view-btn"]').should('not.have.attr', 'disabled');
    cy.get('.product-tour-code-view').click();
    cy.get('.view-line').invoke('text').then((line) => {
        const normalizedText = normalizeText(line);
        expect(normalizedText).to.contain(normalizeText(sourceLines));
    })
    cy.get('[data-testid="vertical-close-btn"]').click();
}),

Cypress.Commands.add('configureResource', (relativePath?: string, method?: string) => {
    if (!method) {
        method = "GET";
    }
    const code = 'import ballerina/http; service / on new http:Listener(8090) { resource function ' + method.toLowerCase() + ' ' + relativePath + '(http:Caller caller, http:Request request) returns error? { }} '
    cy.log("Started resource configuration");
    cy.waitTillWorkSpace();
    cy.contains('button', method).click();
    cy.get('[data-testid="api-path"]').type(relativePath);
    cy.get('[data-testid="save-btn"]').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.checkSourceCodeForValidation(code);
    cy.log("Configured resource successfully");
}),

/**
 * Selects the Application trigger
 *
 * @param type - Trigger type ("Manual", "Schedule", "Calender", "Github", "Salesforce")
 */
Cypress.Commands.add('selectTrigger', (type: string) => {
    cy.waitTillWorkSpace();
    switch (type) {
        case "Manual":
            cy.selectManualTrigger();
            break;
        case "Schedule":
            // To be implemented
            break;

    }
    cy.log("selected " + type + "trigger type");
})

Cypress.Commands.add('selectGitHubTrigger', (repoName: string, triggerEventType: string, triggerAction: string) => {
    cy.log("Configuring the GitHub Trigger");
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.get('.trigger-wrapper').contains('GitHub').should('be.visible').click();
    cy.get('[data-testid="zoom-out-btn"]').click();
    cy.contains('GitHub Connection #1').click();
    cy.get('.MuiAutocomplete-popupIndicator').eq(0).click();
    cy.contains(repoName).click();
    cy.get('.MuiAutocomplete-popupIndicator').eq(1).click();
    cy.contains(triggerEventType).click();
    cy.get('.MuiAutocomplete-popupIndicator').eq(2).click();
    cy.contains(triggerAction).click();
    cy.contains('Save').should('be.visible').click();
    cy.waitTillWorkSpace();
    cy.log("Completed configuring Github trigger");
}),

Cypress.Commands.add('configureGmailConnector', (emailAddress: string, action: string, emailSubject: string, emailBody: string) => {
    cy.log("Configuring the Gmail connector");
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.get('[data-testid="api-options"]').click();
    cy.get('[data-testid="gmail"]').click();
    cy.contains('Gmail Connection #1').should('be.visible').click();
    cy.contains('Save').should('exist').click();
    cy.log("Setup of Gmail connection successful");

    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.get('[id=SmallPlus]').eq(0).click({force: true});
    cy.get('[data-testid="api-options"]').click();
    cy.get('.existing-connector-details').children().get('.existing-connector-name').eq(0).click();

    cy.get('.MuiAutocomplete-popupIndicator').eq(0).click();
    cy.contains(action).click();

    cy.typeOnNthExpressionEditor(1,emailAddress,true);
    cy.typeOnNthExpressionEditor(2,emailSubject,true);
    cy.typeOnNthExpressionEditor(3,emailBody,true);
    cy.typeOnNthExpressionEditor(5,emailAddress,true);
    cy.typeOnNthExpressionEditor(6,"",true);
    cy.typeOnNthExpressionEditor(7,"",true);

    cy.contains('Save').should('be.visible').click();

    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.waitTillWorkSpace();
    cy.log("Configured Gmail connector");
}),

Cypress.Commands.add('selectManualTrigger', () => {
    const code = `public function main() returns error? { }`;
    cy.waitTillWorkSpace();
    cy.get('.trigger-wrapper').contains('Manual').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.checkSourceCodeForValidation(code);
    cy.log("selected Manual trigger");
}),

/**
 * Selects the Manual Trigger Options
 *
 * @param type - Manual trigger category ("Statements", "Connections")
 * @param option - Option under corresponding trigger type
 */
Cypress.Commands.add('selectManualTriggerOptions', (type: string, option:string) => {
    cy.get('body').then($body => {
        let statementOptionsAvailable = ($body.find('[data-testid="statement-options"]').length > 0) ? true : false;
        if (!statementOptionsAvailable) {
            cy.get('[id="SmallPlus"]').eq(0).click();
        }
        if(type == 'Statements'){
            cy.get('[data-testid="statement-options"]').click();
            cy.log('Selected statement category');
        } else if(type == 'Connections'){
            cy.get('[data-testid="api-options"]').click();
            cy.log('Selected connections category');
        }
        cy.selectSpecificOption(option);
    })
}),

Cypress.Commands.add('selectSpecificOption', (option: string) => {
    cy.log('Selecting ' + option + ' option from options panel');
    cy.get('[data-testid="' + option + '"]').click({force: true});
    cy.log('Selected option: ', option);
}),

Cypress.Commands.add('typeOnNthExpressionEditor', (n: number, expression: string, withinQuotes: boolean, waitForEnable?: string) => {
    let expressionToType = expression;
    if (withinQuotes) {
        expressionToType = `\"${expression}\"`
    }

    cy.get('.exp-editor').get('.monaco-editor').get('.view-line').eq(n).click().type('{backspace}{backspace}' + expressionToType);

    if (waitForEnable) {
        cy.get('[data-testid="' + waitForEnable + '"').should('not.have.attr', 'disabled');
    }

    cy.get('body').type('{esc}', {force: true});
}),

Cypress.Commands.add('createVariableProperty', (type: string, name: string, expression: string) => {
    const variableSourceFields = type + ' ' + name + ' = ' + expression + ";";
    cy.log('Creating the variable with expression : '+ expression);
    cy.get('[data-testid="undefinedvar"]').invoke('text').then((availableText) => {
        if (!(availableText == type)) {
            cy.get('[data-testid="undefinedvar"]').click();
            cy.contains(type).click();
        }
    })

    cy.log('Creating variable: selected variable type');
    cy.get('[value="variable"]').click().clear().type(name);

    cy.log('Creating variable: added variable name');
    cy.typeOnNthExpressionEditor(0, expression, false, "save-btn");

    cy.log("Creating variable: added variable expression");
    cy.get('[data-testid="save-btn"]').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.checkSourceCodeForValidation(variableSourceFields);

    cy.log('Successfully created the variable with expression : ', expression);
}),

Cypress.Commands.add('createLogProperty', (type: string, expression: string) => {
    const variableSourceFields = "log:print(\"" + expression + "\");";
    cy.log('Creating the log with expression : '+ expression);
    cy.get('[data-testid="Info"]').invoke('text').then((availableText) => {
        if (!(availableText == type)) {
            cy.get('[data-testid="Info"]').click();
            cy.contains(type).click();
        }
    })

    cy.log('Creating log: added log type');
    cy.typeOnNthExpressionEditor(0, expression, true, "log-save-btn");
    cy.log("Creating log: added log expression");
    cy.get('[data-testid="log-save-btn"]').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.checkSourceCodeForValidation(variableSourceFields);
    cy.log('Successfully created the log with expression : ', expression);
}),

Cypress.Commands.add('goBacktoAppsList', () => {
    cy.get('[data-testid="app-list-btn"]').click();
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.log('App List Page loaded successfully');
}),

Cypress.Commands.add('searchApps', (name: string) => {
    cy.get('body').then($body => {
        let searchButtonExists = ($body.find('[data-testid="search-btn"]').length > 0) ? true : false;
        if (searchButtonExists) {
            cy.get('[data-testid="search-btn"]').trigger('mouseover');
        }
    });
    cy.get('.MuiInputBase-input.MuiInput-input').eq(0).click().clear().type(name);
}),

Cypress.Commands.add('resetAppSearch', () => {
    cy.get('body').then($body => {
        let searchButtonExists = ($body.find('[data-testid="search-btn"]').length > 0) ? true : false;
        if (searchButtonExists) {
            cy.get('[data-testid="search-btn"]').trigger('mouseover');
        }
    });
    cy.get('body').then($body => {
        let searchBoxExists = ($body.find('.MuiInputBase-input.MuiInput-input').length > 0) ? true : false;
        if (searchBoxExists) {
            cy.get('.MuiInputBase-input.MuiInput-input').eq(0).click().clear();
        }
    });
})

Cypress.Commands.add('undeployApp', (type: string, name: string, strict: boolean) => {
    let listTableColumn = 1;

    // Check if apps are listed
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.get('body').then($body => {
        let appExist = ($body.find('.MuiTableRow-root.MuiTableRow-hover').length > 0) ? true : false;
        if (strict) {
            expect(appExist).to.equal(true);
        }

        if (appExist) {
            cy.searchApps(name);
            cy.get('body').then($body => {
                let appCount = $body.find('.MuiTableRow-root.MuiTableRow-hover').length
                appExist = (appCount > 0) ? true : false;
                if (strict) {
                    expect(appCount).to.equal(1, 'Only one app should exists');
                }

                if (appExist) {
                    if (strict) {
                        cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(0).should('have.text', name);
                    }
                    if (type == 'integration') {
                        listTableColumn = 2;
                    }
                    cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(listTableColumn).invoke('text').then((activeStatus) => {
                        if (activeStatus == "Active") {
                            cy.log('Undeploying the application: ', name);
                            cy.get('.MuiTableRow-root.MuiTableRow-hover').click();
                            cy.get('[data-testid="deploy"]').click();
                            cy.get('[id="backdrop-loader"').should('not.exist');
                            cy.contains('button', 'Stop').click();
                            cy.contains('Stopping').should('not.exist');
                            cy.wait(30000);
                            cy.goBacktoAppsList();

                            if (strict) {
                                cy.searchApps(name);
                                cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(listTableColumn).invoke('text').then((activeStatus) => {
                                    expect(activeStatus).not.to.equal('Active', 'App should be undeployed')
                                })
                            }
                        }
                    })
                }
            })
            cy.resetAppSearch();
        }
    })
}),

Cypress.Commands.add('deleteApp', (type:string, name: string, strict: boolean) => {
    // Undeploy the app if active
    cy.undeployApp(type, name, strict);

    // Check if apps are listed
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.get('body').then($body => {
        let appExist = ($body.find('.MuiTableRow-root.MuiTableRow-hover').length > 0) ? true : false;
        if (strict) {
            expect(appExist).to.equal(true);
        }

        if (appExist) {
            cy.searchApps(name);
            cy.get('body').then($body => {
                let appCount = $body.find('.MuiTableRow-root.MuiTableRow-hover').length
                appExist = (appCount > 0) ? true : false;
                if (strict) {
                    expect(appCount).to.equal(1, 'Only one app should exists');
                }
            })

            if (appExist) {
                if (strict) {
                    cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(0).should('have.text', name);
                }

                cy.log('Deleting the application: ', name);
                cy.get('.MuiTableRow-root.MuiTableRow-hover').trigger('mouseover');
                cy.get('[data-testid="delete-btn"]').click();
                cy.contains('h5', 'Delete').click();

                cy.get('[id="circular-loader"]').should('not.exist');
                cy.get('[id="backdrop-loader"]').should('not.exist');

                cy.get('body').then($body => {
                    let appExist = ($body.find('.MuiTableRow-root.MuiTableRow-hover').length > 0) ? true : false;

                    if (strict && appExist) {
                        cy.searchApps(name);
                        cy.get('.MuiTableRow-root.MuiTableRow-hover').should('not.exist');
                    }
                })
            }

            cy.resetAppSearch();
        }
    })
}),

Cypress.Commands.add('createRespond', (expression: string, skipSmallPlus?: boolean) => {
    const responseSourceFields = `check caller->respond(${expression});`;

    cy.get('body').then($body => {
        let statementOptionsAvailable = ($body.find('[data-testid="statement-options"]').length > 0) ? true : false;
        if (!statementOptionsAvailable) {
            cy.get('[id="SmallPlus"]').eq(0).click();
            cy.get('[id=Plus_a]').eq(0).click({force: true});
        }
        cy.selectSpecificOption('addrespond');

        cy.typeOnNthExpressionEditor(0, expression, false, "save-btn");
        cy.log('Creating respond: added expression ', expression);

        cy.get('[data-testid="save-btn"]').click();
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
        cy.checkSourceCodeForValidation(responseSourceFields);

        cy.log('Created respond action with variable : ', expression);
    })
}),

Cypress.Commands.add('callExternalEndpoint', (URL: string, attempts: number, expectedRes: string) => {
    let res = "";
    // TO DO: stop iterating when expected response is recieved
    for (let attempt = 1; attempt <= attempts; attempt++) {
        cy.request(URL).then((response) => {
            res = response.body;
            cy.log("Attempt : " + attempt + " calling the endpoint.. response: " + res);
            if (res == expectedRes) {
                cy.log('backend service response : ', res);
                expect(res).to.equal(expectedRes);
            }
        })
    }
}),

Cypress.Commands.add('testRunApp', () => {
    cy.get('[data-testid="editor-run-btn"]').should('be.visible');
    cy.get('[data-testid="editor-run-btn"]').click();
    cy.log('Started test run');
})

Cypress.Commands.add('switchToDeployView', (appName: string) => {
    cy.get('[data-testid="deploy"]').click();
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.url().should('include', 'app/' + appName + '/deploy');
    cy.log('Successfully navigated to deploy view');
})

Cypress.Commands.add('deployToChoreo', (type:string, appName: string) => {

    cy.switchToDeployView(appName);

    cy.log('Deploying application...');
    cy.get('#deploy-button').should('exist');
    cy.get('#deploy-button').click();

    cy.log('Starting initialization phase...');
    cy.contains('Initialize').parent().siblings('[src="/images/building.svg"]').should('exist');
    cy.contains('Initialize').parent().siblings('[src="/images/building.svg"]', {timeout: 600000}).should('not.exist');
    cy.get('[src="/images/failed.svg"]').should('not.exist');
    cy.contains('Initialize').parent().siblings('[src="/images/check.svg"]').should('exist');
    cy.log('Initialize phase successful!');

    cy.log('Starting build phase...');
    cy.contains('Build').parent().siblings('[src="/images/building.svg"]').should('exist');
    cy.contains('Build').parent().siblings('[src="/images/building.svg"]', {timeout: 600000}).should('not.exist');
    cy.get('[src="/images/failed.svg"]').should('not.exist');
    cy.contains('Build').parent().siblings('[src="/images/check.svg"]').should('exist');
    cy.log('Build phase successful!');

    cy.log('Starting deploy phase...');
    cy.get('[src="/images/failed.svg"]').should('not.exist');
    cy.contains(/^Deploy$/).parent().siblings('[src="/images/check.svg"]').should('exist');
    cy.log('Deploy phase successful!');

    if (type == "service") {
        cy.log('Starting expose phase...');
        cy.contains('Expose').parent().siblings('[src="/images/building.svg"]').should('exist');
        cy.contains('Expose').parent().siblings('[src="/images/building.svg"]', {timeout: 600000}).should('not.exist');
        cy.get('[src="/images/failed.svg"]').should('not.exist');
        cy.get('[src="/images/check.svg"]').should('exist');
        cy.log('Expose phase successful!');
    }
})
