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
import { INTEGRATIONS_TEXT } from "../../../support/common/constants";
import { it } from "mocha";

/// <reference types="cypress" />

describe("Weather Forecast data to a SMS", () => {
    let appName: string;
    const mobileNum = '"+94771234567"';
    const lat = '"6.9319"';
    const lon = '"79.8478"';
    const string1 = '+ minTemp.toString() +';
    const string2 = '+ pressure_data.toString() +';
    const string3 = '+ humidity_data.toString()';
    const weatherOutput = ' "Weather Forecast data : \\\nn "+" \\\nn Minimum Temperature = " ' +
        string1 + ' "\\\nn Pressure = " ' + string2 + '"\\\nn Humidity =" ' + string3 + '';


    before(() => {
        cy.consoleUserLogin();
    });


    after(() => {
        cy.deleteApp("integration", appName, true);
        cy.log("App deleted successfully!")
        cy.userLogout();
    });


    it("Create Manual App", () => {
        appName = generateAppName("app");
        cy.log("app name: " + appName);
        cy.createNewApp(INTEGRATIONS_TEXT, appName);

        cy.url().should("include", "app/" + appName + "/develop");
        cy.selectTrigger("Manual");

        cy.log('Creating variables');
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('decimal', 'minTemp', '2000');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('decimal', 'humidity_data', '0');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('decimal', 'pressure_data', '0');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('int', 'index', '0');

        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableProperty('decimal', 'threshold', '350');
        cy.log('Variables created successfully');

    });


    it("Create Weather API", () => {
        cy.wait(4000);
        cy.addWeatherForecastAPI(0, "openweathermapEndpoint", "result", lat, lon);
    });

    it("Retrieve data", () => {
        cy.wait(4000);
        cy.log("Retrieve openweathermap daily forecast data");
        cy.selectManualTriggerOptions("Statements", "addVariable");
        cy.createVariableOtherTypeProperty('json[]', 'daily_result', '<json[]>result?.daily.toJson()');
        cy.log("Openweathermap daily forecast data added successfully!");

    });

    it("Create while block", () => {
        cy.wait(4000);
        cy.log("Add while loop");
        cy.selectManualTriggerOptions("Statements", "addWhile");
        cy.get('.exp-editor').eq(0).type('index < daily_result.length()');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(2000);
        cy.get('[data-testid="while-save-btn" ]').click({ force: true });
        cy.log("While loop added successfully!");


        cy.log("Add other type variable");
        cy.findPlusButton(0, ['.while-wrapper']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addVariable"]').click({ force: true });
        cy.createVariableOtherTypeProperty('map<json>', 'daily_data', '<map<json>>daily_result[index]');
        cy.log("Other type variable added successfully!");


        cy.log("Add other type variable");
        cy.findPlusButton(0, ['.while-wrapper']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addVariable"]').click({ force: true });
        cy.createVariableOtherTypeProperty('map<json>', 'temp_data', '<map<json>>daily_data["temp"]');
        cy.log("Other type variable added successfully!");


        cy.log("Add if block");
        cy.findPlusButton(0, ['.while-wrapper']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addIf"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type('minTemp > <decimal>temp_data["min"]');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click();
        cy.wait(4000);
        cy.log("If block added successfully!");


        cy.log("Add other type variable inside the if block");
        cy.findPlusButton(5, ['.while-wrapper', '.if-else']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addcustom"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type('minTemp = <decimal>temp_data["min"];');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click();
        cy.wait(4000);
        cy.log("Other type variable added successfully!");


        cy.log("Add other type variable inside the if block");
        cy.findPlusButton(5, ['.while-wrapper', '.if-else']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addcustom"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type(' humidity_data = <decimal>daily_data["humidity"];');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click({ force: true });
        cy.wait(4000);
        cy.log("Other type variable added successfully!");


        cy.log("Add other type variable inside the if block");
        cy.findPlusButton(5, ['.while-wrapper', '.if-else']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addcustom"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type('pressure_data = <decimal>daily_data["pressure"];');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click();
        cy.wait(4000);
        cy.log("Other type variable added successfully!");


        cy.log("Add log inside the else block");
        cy.findPlusButton(4, ['.while-wrapper', '.if-else']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addLog"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type('"Temperature not exceeded threshold temperature"');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click({ force: true });
        cy.wait(4000);
        cy.log("Log added successfully!");
        cy.log("if-else block executed");


        cy.log("Add other type variable");
        cy.findPlusButton(0, ['.while-wrapper']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addcustom"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type(' index = index + 1;');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click({ force: true });
        cy.log("Other type variable added successfully!");
    });


    it("Create the output data and send sms", () => {
        cy.wait(5000);
        cy.log('Creating variable: weatherInfo');
        cy.get('[id="SmallPlus"]').should('exist').eq(0).click({ force: true });
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addVariable"]').click({ force: true });


        cy.get('[data-testid="undefinedvar"]').invoke('text').then((availableText) => {
            if (!(availableText == 'string')) {
                cy.get('[data-testid="undefinedvar"]').click();
                cy.get('.MuiListItem-button').contains('string').click();
            }
        })

        cy.log('Creating variable: string');
        cy.get('[data-testid="variable-name"]').click().type('weatherInfo');
        cy.log('Creating variable: added weatherInfo');
        cy.get('.exp-editor').eq(0).type(weatherOutput, { parseSpecialCharSequences: false });
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.log("Creating variable: added variable expression");
        cy.wait(4000);
        cy.get('[data-testid="save-btn"]').click();
        cy.log('Added variable:weatherInfo successfully');
        cy.wait(2000);


        cy.log("Add if block");
        cy.selectManualTriggerOptions("Statements", "addIf");
        cy.wait(2000);
        cy.get('.exp-editor').eq(0).type('minTemp < <decimal>threshold');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click();
        cy.wait(4000);
        cy.log("If block added successfully!");


        cy.wait(4000);
        cy.findPlusButton(7, ['.if-else']);
        cy.sendChoreoSMS(mobileNum, 'weatherInfo')


        cy.log("Add log inside the else block");
        cy.findPlusButton(6, ['.if-else']);
        cy.get('[data-testid="statement-options"]').click({ force: true });
        cy.get('[data-testid="addLog"]').click({ force: true });
        cy.get('.exp-editor').eq(0).type('"Temperature is over the threshold value, Message not sent"');
        cy.get('[data-testid="expr-validating-loader"]').should('not.exist');
        cy.wait(1000);
        cy.contains('button', 'Save').click({ force: true });
        cy.wait(4000);
        cy.log("Log added successfully!");
        cy.get('[data-testid=product-tour-code-view]').click({ force: true });

    });

    it("Test-run and deploy integration", () => {
        const loadRunTxt = "Running...";
        cy.testRunApp();
        cy.get(".product-tour-logs-panel").contains(loadRunTxt).should("exist");
        cy.contains('[data-testid="log-panel"]', 'Application exited', { timeout: 1000 * 60 * 10 }).should('exist');
        cy.log("Expression is logged successfully");
        cy.deployToChoreo("integration", appName);
        cy.contains('[data-testid="log-panel-entry"]', 'Application exited', { timeout: 1000 * 60 * 5 }).should('exist');
    });


    it("Undeploy app from UI", () => {
        cy.goBacktoAppsList();
        cy.undeployApp("integration", appName, true);
        cy.hideWelcomeMessage();

    });

})



