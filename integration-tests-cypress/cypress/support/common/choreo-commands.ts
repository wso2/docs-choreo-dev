import { normalizeText } from './choreo-utils';

Cypress.Commands.add('waitTillWorkSpace', () => {
    cy.get('[data-testid="setting-up-workspace"]').should('not.exist');
}),

Cypress.Commands.add('createNewApp', (name: string) => {
    cy.get('[id="backdrop-loader"').should('not.exist');

    cy.get('[href="/services"]').click();
    cy.log("Page loaded successfully");

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

Cypress.Commands.add('selectAPITrigger', (method: string, relativePath?: string) => {
    const code = 'import ballerina/http; service / on new http:Listener(8090) { resource function ' + method.toLowerCase() + ' ' + relativePath + '(http:Caller caller, http:Request request) returns error? { }} '
    cy.waitTillWorkSpace();
    cy.contains('button', method).click();
    cy.get('[data-testid="api-path"]').type(relativePath);
    cy.get('[data-testid="save-btn"]').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.checkSourceCodeForValidation(code);
    cy.log("selected API trigger");
}),

/**
 * Selects the Application trigger
 *
 * @param type - Trigger type ("Manual", "Webhook")
 * @param [relativePath] - Relative Path to be used with Webhook Trigger
 * @param [method] - Method type ("GET", "PUT", "DELETE", "POST")
 *
 */
Cypress.Commands.add('selectTrigger', (type: string, relativePath?: string, method?: string) => {
    const webhookSourceFields = ['import ballerina/http;', 'service / on new http:Listener(8090) {',`resource function get ${relativePath}(http:Caller caller, http:Request request) returns error? {` ]
    cy.waitTillWorkSpace();
    switch (type) {
        case "Manual":
            // to be implemented
            break;
        case "API":
            if (!method) {
                method = "GET";
            }
            cy.selectAPITrigger(method, relativePath);
            break;

    }
    cy.log("selected " + type + "trigger type");
}),

Cypress.Commands.add('selectStatementOption', (option: string) => {
    cy.log('Selecting ' + option + ' option from options panel');
    cy.get('[data-testid="statement-options"]').click();
    cy.log('Selected statement options tab');
    cy.get('[data-testid="' + option + '"]').click({force: true});
    cy.log('Selected option: ', option);
}),

Cypress.Commands.add('typeOnNthExpressionEditor', (n: number, expression: string, withinQuotes: boolean, waitForEnable?: string) => {
    let expressionToType = expression;
    if (withinQuotes) {
        expressionToType = `\"${expression}\"`
    }

    cy.get('.exp-editor').get('.monaco-editor').get('.view-line').eq(n).click().type('{selectall}{backspace}' + expressionToType);

    if (waitForEnable) {
        cy.get('[data-testid="' + waitForEnable + '"').should('not.have.attr', 'disabled');
    }

    cy.get('body').type('{esc}', {force: true});
}),

Cypress.Commands.add('createProperty', (type: string, name: string, expression: string) => {
    const variableSourceFields = type + ' ' + name + ' = ' + expression + ";";
    cy.log('Creating the variable with expression : ', expression);

    cy.get('body').then($body => {
        let statementOptionsAvailable = ($body.find('[data-testid="statement-options"]').length > 0) ? true : false;
        if (!statementOptionsAvailable) {
            cy.get('[id="SmallPlus"]').eq(0).click();
            cy.get('[id=Plus_a]').eq(0).click({force: true});
        }
        cy.selectStatementOption("addVariable");

        cy.log('Creating variable: selected variable option');
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
    })
}),

Cypress.Commands.add('goBacktoAppsList', () => {
    cy.get('[data-testid="app-list-btn"]').click();
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.log('Services page loaded successfully');
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

Cypress.Commands.add('undeployApp', (name: string, strict: boolean) => {
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

                    cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(1).invoke('text').then((activeStatus) => {
                        if (activeStatus == "Active") {
                            cy.log('Undeploying the application: ', name);
                            cy.get('.MuiTableRow-root.MuiTableRow-hover').click();
                            cy.get('[data-testid="deploy"]').click();
                            cy.get('[id="backdrop-loader"').should('not.exist');
                            cy.contains('button', 'Stop').click();
                            cy.contains('Stopping').should('not.exist');

                            cy.goBacktoAppsList();

                            if (strict) {
                                cy.searchApps(name);
                                cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').eq(1).invoke('text').then((activeStatus) => {
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

Cypress.Commands.add('deleteApp', (name: string, strict: boolean) => {
    // Undeploy the app if active
    cy.undeployApp(name, strict);

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
        cy.selectStatementOption('addrespond');

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

Cypress.Commands.add('switchToDeployView', (appName: string) => {
    cy.get('[data-testid="deploy"]').click();
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.url().should('include', 'app/' + appName + '/deploy');
    cy.log('Successfully navigated to deploy view');
})

Cypress.Commands.add('deployToChoreo', (appName: string) => {

    cy.switchToDeployView(appName);

    cy.log('Deploying application...');
    cy.contains('button', 'Deploy').should('exist');
    cy.contains('button', 'Deploy').click();

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

    cy.log('Starting expose phase...');
    cy.contains('Expose').parent().siblings('[src="/images/building.svg"]').should('exist');
    cy.contains('Expose').parent().siblings('[src="/images/building.svg"]', {timeout: 600000}).should('not.exist');
    cy.get('[src="/images/failed.svg"]').should('not.exist');
    cy.get('[src="/images/check.svg"]').should('exist');
    cy.log('Expose phase successful!');
})
