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

import { isOldValue, keyNamePrefix, normalizeText } from '../../common/utils';
import {
    MARKETPLACE_TEXT,
    INTEGRATIONS_TEXT,
    SERVICES_TEXT,
    APIS_TEXT,
    DEVOPS_TEXT,
    SETTINGS_TEXT,
    SETTINGS_PATH,
    APP_SVC_URL, ORG_NAME, SUCCESS_STATUS_CODE,
    GMAIL_CONNECTION_NAME,
    APIM_RESOURCE_PATH,
    PATH_SEPARATOR,
    USER_CONNECTIONS_PATH,
    GOOGLE_CALENDAR_CONNECTOR,
    USER_CONFIGURATIONS_PATH,
} from '../../common/constants';
import { getApiName } from '../../devportal/utils';

let LOCAL_STORAGE_MEMORY = {};

Cypress.Commands.add("saveLocalStorage", () => {
    Object.keys(localStorage).forEach(key => {
        LOCAL_STORAGE_MEMORY[key] = localStorage[key];
    });
});

Cypress.Commands.add("restoreLocalStorage", () => {
    Object.keys(LOCAL_STORAGE_MEMORY).forEach(key => {
        localStorage.setItem(key, LOCAL_STORAGE_MEMORY[key]);
    });
});

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
});

Cypress.Commands.add('waitTillWorkSpace', () => {
    cy.get('[data-testid="setting-up-workspace"]', { timeout: 1000 * 60 }).should('not.exist');
});

Cypress.Commands.add('createNewApp', (type: string, name: string) => {
    cy.navigateFromHomePage(type);

    cy.log("Creating a new application with name : " + name);
    cy.contains('button', 'Create').click();
    cy.get('input').type(name);
    cy.contains('button', 'Create').click();

    cy.waitTillWorkSpace();
    cy.get('.diagram-canvas').should('exist');
    cy.log("Application created successfully with name: " + name);
});

/**
 * Validating components by going through the source code view and checking the terms
 *
 * @param sourceLines
 */
Cypress.Commands.add('checkSourceCodeForValidation', (sourceLines: string) => {
    cy.get('[data-testid="code-view-btn"]').should('not.have.attr', 'disabled');
    cy.get('[data-testid="code-view-btn"]').click({ force: true });
    cy.get('.view-line').invoke('text').then((line) => {
        const normalizedText = normalizeText(line);
        expect(normalizedText).to.contain(normalizeText(sourceLines));
    })
    cy.get('[data-testid="code-view-btn"]').click({ force: true });
});

Cypress.Commands.add('configureResource', (relativePath: string | null, method: string | null, returnType: string | null) => {
    let selectedMethod = "GET";
    if (method) {
        selectedMethod = method
    }

    cy.log("Started resource configuration");
    cy.waitTillWorkSpace();
    cy.get('[data-testid="undefinedGET"]').invoke('text').then((availableText) => {
        if (!(availableText == selectedMethod)) {
            cy.get('[data-testid="undefinedGET"]').click();
            cy.get('.MuiListItem-button').contains(selectedMethod).click();
        }
    })
    cy.get('[data-testid="api-path"]').type(relativePath);
    cy.get('[data-testid="api-return-type"]').type(returnType);
    cy.get('[data-testid="advanced-path-config"]').click();
    cy.get('[data-testid="select-request-btn"]').click();
    cy.get('[data-testid="save-btn"]').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.log("Configured resource successfully");
});

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
            cy.selectScheduleTrigger();
            break;
        case "GitHub":
            cy.selectGitHubTrigger()
            break;
    }
    cy.log("selected " + type + "trigger type");
});

/**
 * Selects the GitHub Trigger Options
 *
 */
Cypress.Commands.add('selectGitHubTrigger', () => {
    cy.waitTillWorkSpace();
    cy.log("Selecting the GitHub Trigger");
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.get('.trigger-wrapper').contains('GitHub').should('be.visible').click();
    cy.get('[data-testid="zoom-out-btn"]').click();
    cy.log("Selected the GitHub trigger");
});

/**
* Configures a GitHub trigger event
*
* @param repoName - GitHub repository name to be used
* @param triggerEventType - Type of event over the repository (Eg:- issue_comment)
* @param action - Action performed on the event (Eg:- created, modified, deleted)
*/
Cypress.Commands.add('configureGitHubTrigger', (repoName: string, triggerEventType: string, triggerAction: string) => {
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.log("Configuring the GitHub trigger");

    // Selecting the manually added GitHub connection (user - testuser-choreo)
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
});

/**
 * Setup Gmail Connection (select from existing connection created manually)
 * Flow happens after the API Call options are displayed in Low code editor
 */
Cypress.Commands.add('setupGmailConnection', () => {
    cy.log("Setting up Gmail connection");
    cy.get('[data-testid="gmail"]').click();
    // Click on the Gmail connection that is already created manually
    cy.contains('Gmail Connection #1').should('be.visible').click();
    cy.contains('Save').should('exist').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.log("Setup of Gmail connection successful");
});

/**
* Add a Gmail API call to send a message
* Should be followed by the cypress command setupGmailConnection
*
* @param plusBtnIndex - Index of the element plus icon with id `SmallPlus` in the Low code editor
* @param gmailConnectionIndex - Index of the element representing the Gmail connection with class `existing-connector-name` in the Low code editor
* @param emailAddress - Email address to be configured to the Gmail connector
* @param emailSubject - Subject of the Email to be sent
* @param emailBody - Body of the Email
*/
Cypress.Commands.add('sendGmailMessage', (plusBtnIndex: number, gmailConnectionIndex: number, emailAddress: string, emailSubject: string, emailBody: string) => {
    cy.log("Adding a Gmail connector");
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.get('[id=SmallPlus]').eq(plusBtnIndex).click({ force: true });
    cy.get('[data-testid="api-options"]').click();
    cy.get('.existing-connector-details').children().get('.existing-connector-name').eq(gmailConnectionIndex).click();
    cy.log("Added a Gmail connector");

    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.get('.MuiAutocomplete-popupIndicator').eq(0).click();
    cy.contains("sendMessage").click();

    cy.typeOnNthExpressionEditor(1, emailAddress, true);
    cy.typeOnNthExpressionEditor(2, emailSubject, true);
    cy.typeOnNthExpressionEditor(3, emailBody, true);
    cy.typeOnNthExpressionEditor(5, emailAddress, true);
    cy.typeOnNthExpressionEditor(6, "", true);
    cy.typeOnNthExpressionEditor(7, "", true);

    cy.contains('Save').should('be.visible').click();

    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.waitTillWorkSpace();
    cy.log("Configuration set to send Gmail message");
});

/**
 * Fill google calendar config form of prebuilt integrations
 *
 * @param calender - Calendar name
 */
Cypress.Commands.add('fillCalendarConfigs', (calendar: string) => {
    cy.log("Filling the calendar configuration");

    // Selecting the manually added calendar connection (user - testuser-choreo)
    cy.contains(GMAIL_CONNECTION_NAME).click();
    cy.get('[placeholder="Choose Calendar"]').siblings().children().get('.MuiAutocomplete-popupIndicator').click();
    cy.get('#combo-box-demo-popup').should('exist');
    cy.get('#combo-box-demo-popup').children().contains(calendar).click({ force: true });
    cy.get('[placeholder="Choose Calendar"]').should('have.value', calendar)
    cy.log("Completed filling calendar configuration");
});


/**
 * Fill twilio config form of prebuilt integrations
 *
 * @param accountSID - Twilio account SIO
 * @param token - Twilio auth token
 * @param senderNumber - SMS Sender's Phone Number
 * @param recipientNumber - SMS Recipient's Phone Number
 */
Cypress.Commands.add('fillTwilioConfigs',
    (accountSID: string, token: string, senderNumber: string, recipientNumber: string) => {
        cy.log("Filling the twilio configuration");

        cy.get('[placeholder="Twilio Account SID"]').type(accountSID);
        cy.get('[placeholder="Twilio Auth Token"]').type(token);
        cy.get('[placeholder="SMS Sender\'s Phone Number"]').type(senderNumber);
        cy.get('[placeholder="SMS Recipient\'s Phone Number"]').type(recipientNumber);
        cy.log("Completed filling twilio configuration");
    });

Cypress.Commands.add('selectManualTrigger', () => {
    cy.waitTillWorkSpace();
    cy.get('.trigger-wrapper').contains('Manual').click({ force: true });
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.log("selected Manual trigger");
});

Cypress.Commands.add('selectScheduleTrigger', () => {
    cy.waitTillWorkSpace();
    cy.get('.trigger-wrapper').contains('Schedule').click();
    cy.get('[data-testid="undefinedMinute"]').invoke('text').then((availableText) => {
        cy.log("selected Input", availableText);
        if (availableText != 'Minute') {
            cy.get('.MuiInputBase-input.MuiInput-input').eq(0).click();
            cy.get('.MuiListItem-button').contains('Minute').click();
        }
    })
    cy.get('.MuiInputBase-input.MuiInput-input').eq(1).click().clear().type("1");
    cy.contains('button', 'Save').click();
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.log("selected & configured schedule trigger");
});

/**
 * Selects the Manual Trigger Options
 *
 * @param type - Manual trigger category ("Statements", "Connections")
 * @param option - Option under corresponding trigger type
 */
Cypress.Commands.add('selectManualTriggerOptions', (type: string, option: string) => {
    cy.get('body').then($body => {
        let statementOptionsAvailable = ($body.find('[data-testid="statement-options"]').length > 0) ? true : false;
        if (!statementOptionsAvailable) {
            cy.get('[id="SmallPlus"]').eq(0).click({ force: true });
        }
        if (type == 'Statements') {
            cy.get('[data-testid="statement-options"]').click();
            cy.log('Selected statement category');
        } else if (type == 'Connections') {
            cy.get('[data-testid="api-options"]').click();
            cy.log('Selected connections category');
        }
        cy.selectSpecificOption(option);
    })
});

Cypress.Commands.add('selectSpecificOption', (option: string) => {
    cy.log('Selecting ' + option + ' option from options panel');
    cy.get('[data-testid="' + option + '"]').click({ force: true });
    cy.log('Selected option: ' + option);
});

Cypress.Commands.add('typeOnNthExpressionEditor',
    (n: number, expression: string, withinQuotes: boolean, waitForEnable?: string, validExpression = true) => {
        let expressionToType = expression;
        if (withinQuotes) {
            expressionToType = `\"${expression}\"`
        }

        cy.get('.exp-editor').get('.monaco-editor').get('.view-line').eq(n).click().type('{backspace}{backspace}' + expressionToType);

        if (waitForEnable && validExpression) {
            cy.get('[data-testid="' + waitForEnable + '"]').should('not.have.attr', 'disabled');
        }

        cy.get('body').type('{esc}', { force: true });
    });

Cypress.Commands.add('createVariableProperty', (type: string, name: string, expression: string, validExpression = true) => {
    const variableSourceFields = type + ' ' + name + ' = ' + expression + ";";
    cy.log('Creating the variable with expression : ' + expression);
    cy.get('[data-testid="undefinedvar"]').invoke('text').then((availableText) => {
        if (!(availableText == type)) {
            cy.get('[data-testid="undefinedvar"]').click();
            cy.get('.MuiListItem-button').contains(type).click();
        }
    })

    cy.log('Creating variable: selected variable type');
    cy.get('[data-testid="variable-name"]').click().type(name);

    cy.log('Creating variable: added variable name');
    cy.typeOnNthExpressionEditor(0, expression, false, "save-btn", validExpression);

    if (validExpression) {
        cy.log("Creating variable: added variable expression");
        cy.get('[data-testid="save-btn"]').click();
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
        cy.checkSourceCodeForValidation(variableSourceFields);

        cy.log('Successfully created the variable with expression : ' + expression);
    }
});

Cypress.Commands.add('createLogProperty', (type: string, expression: string) => {
    cy.log('Creating the log with expression : ' + expression);
    cy.get('[data-testid="Info"]').invoke('text').then((availableText) => {
        if (!(availableText == type)) {
            cy.get('[data-testid="Info"]').click();
            cy.contains(type).click();
        }
    })

    cy.log('Creating log: added log type');
    cy.typeOnNthExpressionEditor(0, expression, true, "log-save-btn");
    cy.log("Creating log: added log expression");
    cy.get('[data-testid="log-save-btn"]').click({ force: true });
    cy.get('[data-testid="diagram-loader"]').should('not.exist');
    cy.log('Successfully created the log with expression : ' + expression);
}),

    Cypress.Commands.add('goBacktoAppsList', () => {
        cy.get('[data-testid="app-list-btn"]').click();
        cy.get('[id="backdrop-loader"').should('not.exist');
        cy.log('App List Page loaded successfully');
    });

Cypress.Commands.add('searchApps', (name: string) => {
    cy.get('body').then($body => {
        let searchButtonExists = ($body.find('[data-testid="search-btn"]').length > 0) ? true : false;
        if (searchButtonExists) {
            cy.get('[data-testid="search-btn"]').trigger('mouseover');
        }
    });
    cy.get('.MuiInputBase-input.MuiInput-input').eq(0).click().clear().type(name, { force: true });
});

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
});

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
                    cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').get('[data-testid="active-status"]').invoke('text').then((activeStatus) => {
                        cy.log("status: " + activeStatus);
                        if (activeStatus == "Active") {
                            cy.log('Undeploying the application: ', name);
                            cy.get('.MuiTableRow-root.MuiTableRow-hover').click();
                            cy.get('[data-testid="deploy"]').click();
                            cy.get('[id="backdrop-loader"').should('not.exist');
                            cy.contains('button', 'Stop').click();
                            cy.contains('Stopping', { timeout: 120000 }).should('not.exist');
                            cy.wait(30000);
                            cy.goBacktoAppsList();

                            if (strict) {
                                cy.searchApps(name);
                                cy.get('.MuiTableRow-root.MuiTableRow-hover').children('td').get('[data-testid="active-status"]').invoke('text').then((activeStatus) => {
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
});

Cypress.Commands.add('cleanupApp', (name: string) => {
    cy.log("Cleaning up app: " + name);
    cy.request("DELETE", `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/${name}`).then((resp) => {
        // Status code is expected to be 200
        expect(resp.status).to.eq(SUCCESS_STATUS_CODE);
        cy.log("Successfully cleaned up the app: " + name);
    });
});

Cypress.Commands.add('deleteApiByApplicationId', (id: string) => {
    let token;
    const organizationId = Cypress.env('orgs')[0].uuid;
    const query = `?organizationId=${organizationId}&query=applicationId:${id}`
    cy.getCookie('token').should('exist').then((c) => {
        token = c;
        cy.log("GET API for applicationId: " + id);
        cy.request({
            method: "GET",
            url: APP_SVC_URL + APIM_RESOURCE_PATH + query,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token.value
            },
            timeout: 60000
        }).then((res) => {
            expect(res.status).to.eq(SUCCESS_STATUS_CODE);
            const data = res["body"]["list"];
            expect(data).to.have.length(1);

            const apiId = data[0].id;
            cy.log(`Delete API id: ${apiId}`);
            if (!apiId) {
                throw new Error('API id cannot be empty');
            }

            cy.request({
                method: "DELETE",
                url: APP_SVC_URL + APIM_RESOURCE_PATH + PATH_SEPARATOR + apiId,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token.value
                },
                qs: {
                    'organizationId': organizationId,
                },
            }).then((res) => {
                expect(res.status).to.eq(SUCCESS_STATUS_CODE);
                cy.log("Successfully deleted API: ");
            });;

        })
    });
});

Cypress.Commands.add('deleteApiByApiId', (id: string) => {
    const organizationId = Cypress.env('orgs')[0].uuid;
    cy.getCookie('token').should('exist').then((token) => {
        cy.request({
            method: "DELETE",
            url: APP_SVC_URL + APIM_RESOURCE_PATH + PATH_SEPARATOR + id,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token.value
            },
            qs: {
                'organizationId': organizationId,
            }
        }).then((resp) => {
            // Status code is expected to be 200
            expect(resp.status).to.eq(SUCCESS_STATUS_CODE);
            cy.log("Successfully deleted API: " + id);
        });
    });
});

Cypress.Commands.add('deleteApp', (type: string, name: string, strict: boolean) => {
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
});

Cypress.Commands.add('deleteAppWithoutUndeploy', (name: string, strict: boolean) => {
    // Check if apps are listed
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.get('body', { timeout: 30000 }).then($body => {
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
                cy.get('[style="color: inherit; width: 35%; box-sizing: border-box; font-size: 1rem; font-family: inherit; font-weight: inherit;"]').trigger('mouseover');
                cy.getByTestId("disabled-delete-btn-msg").should('be.visible');
                cy.get('[title="Undeploy the application to delete"]').should('exist');
                cy.log("Cannot delete a deployed app");
            }
        }
    })
});

Cypress.Commands.add('createRespond', (expression: string, skipSmallPlus?: boolean) => {
    const responseSourceFields = `check caller->respond(${expression});`;

    cy.get('body').then($body => {
        let statementOptionsAvailable = ($body.find('[data-testid="statement-options"]').length > 0) ? true : false;
        if (!statementOptionsAvailable) {
            cy.get('[id="SmallPlus"]').eq(0).click({ force: true });
        }
        cy.selectSpecificOption('addrespond');

        cy.typeOnNthExpressionEditor(0, expression, false, "save-btn");
        cy.log('Creating respond: added expression ', expression);

        cy.get('[data-testid="save-btn"]').click();
        cy.get('[data-testid="diagram-loader"]').should('not.exist');
        cy.checkSourceCodeForValidation(responseSourceFields);

        cy.log('Created respond action with variable : ', expression);
    })
});

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
});

Cypress.Commands.add('testRunApp', () => {
    cy.get('[data-testid="editor-run-btn"]').should('be.visible');
    cy.get('[data-testid="editor-run-btn"]').click();
    cy.log('Started test run');
});

Cypress.Commands.add('switchToDeployView', (appName: string) => {
    cy.get('[data-testid="deploy"]').should('be.visible');
    cy.get('[data-testid="deploy"]').click();
    cy.get('[id="backdrop-loader"').should('not.exist');
    cy.url().should('include', 'app/' + appName + '/deploy');
    cy.log('Successfully navigated to deploy view');
});

Cypress.Commands.add('deployToChoreo', (type: string, appName: string) => {

    cy.switchToDeployView(appName);

    cy.log('Deploying application...');
    if (type === 'schedule') {
        cy.contains('button', 'Schedule').click();
        cy.get('[data-testid="undefinedMinute"]').invoke('text').then((availableText) => {
            cy.log("selected Input", availableText);
            if (availableText != 'Minute') {
                cy.get('.MuiInputBase-input.MuiInput-input').eq(0).click();
                cy.get('.MuiListItem-button').contains('Minute').click();
            }
        })
        cy.get('.MuiInputBase-input.MuiInput-input').eq(1).click().clear().type("1");
        cy.contains('button', 'Save').click();
    } else {
        cy.get('#deploy-button').should('exist');
        cy.get('#deploy-button').click();
    }

    cy.log('Awaiting 5 minutes for the deployment to complete');
    cy.get('[data-testid="deploy-stop-button"]', { timeout: 1000 * 60 * 5 }).should('exist');
    cy.log('Deployment successful!');
});

/**
 * Navigate from the home page to a desired page
 *
 * @param pageName - page name that needs to be loaded
 */
Cypress.Commands.add('navigateFromHomePage', (pageName: string) => {
    const pageNameArray = [MARKETPLACE_TEXT, INTEGRATIONS_TEXT, SERVICES_TEXT, APIS_TEXT, DEVOPS_TEXT, SETTINGS_TEXT];
    let pathName = pageName;
    if (pageName == SETTINGS_TEXT) {
        pathName = SETTINGS_PATH;
    } else if (pageName == APIS_TEXT) {
        pathName = pageName + '/';
    }

    cy.get('[id="backdrop-loader"]').should('not.exist');
    if (pageNameArray.includes(pageName)) {
        cy.get('[href="/' + pathName + '"]').click();
        cy.url().should('include', '/' + pathName);
        cy.get('[id="backdrop-loader"]').should('not.exist');
        cy.log("Page loaded successfully");
    } else {
        cy.log('Page not found');
    }
});

Cypress.Commands.add('undeployAppViaRESTAPICall', (appName: string) => {
    cy.request({
        method: "POST",
        url: `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/${appName}/undeploy`,
        timeout: 60000,
        failOnStatusCode: false
    }).then((resp) => {
        // Status code is expected to be 200
        expect(resp.status).to.eq(SUCCESS_STATUS_CODE);
        cy.log("Successfully undeployed the app: " + appName);
    });
});

Cypress.Commands.add('cleanOnPremKey', (keyName: string) => {
    cy.log("Cleaning up on-prem key: " + keyName);
    cy.request("POST", `${APP_SVC_URL}/orgs/${ORG_NAME}/keys/${keyName}/revoke`).then((resp) => {
        // Status code is expected to be 200
        expect(resp.status).to.eq(SUCCESS_STATUS_CODE);
        cy.log("Successfully cleaned up the on-prem key: " + keyName);
    });
});

Cypress.Commands.add('hideWelcomeMessage', () => {
    localStorage.setItem("HAS_SEEN_WELCOME_MESSAGE", "YES");
});

Cypress.Commands.add('clearConnections', () => {
    cy.log('Deleting connections...');
    cy.request({
        method: "GET",
        url: APP_SVC_URL + USER_CONNECTIONS_PATH
    }).then((response) => {
        expect(response.status).to.eq(SUCCESS_STATUS_CODE);
        const connectionList = response["body"] as any[];
        for (const connection of connectionList) {
            const connectorName: string = connection.connectorName;
            const connectorId: string = connection.handle;

            if (connectorName != GOOGLE_CALENDAR_CONNECTOR) {
                const deleteURL = APP_SVC_URL + USER_CONNECTIONS_PATH + PATH_SEPARATOR + connectorId;
                cy.request({
                    method: "DELETE",
                    url: deleteURL
                }).then((deleteResponse) => {
                    expect(deleteResponse.status).to.eq(SUCCESS_STATUS_CODE);
                }).wait(200);;
            }
        }
    });
});

Cypress.Commands.add('clearConfigurations', () => {
    cy.log('Deleting Configurations...');
    cy.request({
        method: "GET",
        url: APP_SVC_URL + USER_CONFIGURATIONS_PATH
    }).then((response) => {
        expect(response.status).to.eq(SUCCESS_STATUS_CODE);
        const configurationList = response["body"] as any[];

        for (const conf of configurationList) {
            const confKey: string = conf.key;
            const confScope: string = conf.scope;
            const deleteConfigurationsURL = APP_SVC_URL + USER_CONFIGURATIONS_PATH + PATH_SEPARATOR + confKey;
            cy.request({
                method: "DELETE",
                url: deleteConfigurationsURL,
                qs: {
                    'scope': confScope,
                }
            }).then((deleteResponse) => {
                expect(deleteResponse.status).to.eq(SUCCESS_STATUS_CODE);
            }).wait(200);
        }
    });
});

Cypress.Commands.add('clearApps', () => {
    cy.log("Deleting apps...");
    cy.request({
        method: "GET",
        form: true,
        url: `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/`
    }).then((response) => {
        const data = response["body"] as [];
        if (data.length) {
            cy.log(`apps found : ${data.length}`);
            for (const value of data) {
                const appName = value["name"] as string;
                const status = value['status'] as string;
                if (status == "running") {
                    cy.undeployAppViaRESTAPICall(appName);
                }
                cy.cleanupApp(appName);
            }
            cy.log("Successfully deleted all apps");
        } else {
            cy.log('No apps found');
        }
    });
});

Cypress.Commands.add('clearOnPremKeys', () => {
    cy.log('Deleting on-prem keys...');
    cy.request({
        method: "GET",
        form: true,
        url: `${APP_SVC_URL}/orgs/${ORG_NAME}/keys/`
    }).then((response) => {
        const data = response["body"] as [];

        if (data.length) {
            cy.log(`on-prem keys found : ${data.length}`);
            for (const value of data) {
                const keyName = value["displayName"] as string;
                if (isOldValue(keyName) || keyName.startsWith(keyNamePrefix)) {
                    cy.cleanOnPremKey(keyName);
                }
            }
            cy.log("Successfully deleted all on-prem keys");
        } else {
            cy.log('No on-prem keys found')
        }
    });
});

Cypress.Commands.add('clearAPIs', () => {
    cy.log('Deleting APIs...');
    let token;
    const organizationId = Cypress.env('orgs')[0].uuid;
    cy.getCookie('token').should('exist').then((c) => {
        token = c;
        cy.request({
            method: "GET",
            url: APP_SVC_URL + APIM_RESOURCE_PATH,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token.value
            },
            qs: {
                'organizationId': organizationId,
                'limit': 200
            },
            timeout: 60000
        }).then((response) => {
            const data = response["body"]["list"] as [];
            if (data.length) {
                cy.log(`APIs found : ${data.length}`);
                for (const value of data) {
                    const apiName = value["name"] as string;
                    if (!apiName.includes(getApiName())) {
                        cy.request({
                            method: "DELETE",
                            url: APP_SVC_URL + APIM_RESOURCE_PATH + PATH_SEPARATOR + value["id"],
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': 'Bearer ' + token.value
                            },
                            qs: {
                                'organizationId': organizationId,
                            },
                        });
                    }
                }
                cy.log("Successfully deleted all APIs");
            } else {
                cy.log('No APIs found');
            }
        });
    });
});


Cypress.Commands.add('clearAllTestData', () => {
    cy.log('Deleting all test data...');
    cy.clearApps();
    cy.clearOnPremKeys();
    cy.clearAPIs();
    cy.clearConnections();
    cy.clearConfigurations();
});