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
import { isOldValue, appNamePrefix, keyNamePrefix } from '../../../support/common/utils';
import { APP_SVC_URL, ORG_NAME } from "../../../support/common/constants";

/// <reference types="cypress" />

describe('Cleaning up', () => {

    before(() => {
        cy.log("Login into Choreo");
        cy.consoleUserLogin();
    })

    after(() => {
        cy.userLogout();
    })

    it('Delete Apps that are old or created by this run', () => {
        cy.request({
            method: "GET",
            form: true,
            url: `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/`
        }).then((response) => {
            const data = response["body"];
            for (const value of data) {
                let appName = value["name"];
                if (isOldValue(appName) || appName.startsWith(appNamePrefix)) {
                    let status = String(value['status']);
                    if (status == "running") {
                        cy.undeployAppViaRESTAPICall(appName);
                    }
                    cy.cleanupApp(appName);
                }
            }
        })
    })

    it('delete on-prem keys that are old or created by this run', () => {
        cy.request({
            method: "GET",
            form: true,
            url: `${APP_SVC_URL}/orgs/${ORG_NAME}/keys/`
        }).then((response) => {
            const data = response["body"];
            for (const value of data) {
                let keyName = value["displayName"];
                if (isOldValue(keyName) || keyName.startsWith(keyNamePrefix)) {
                    cy.cleanOnPremKey(keyName);
                }
            }
        })
    })
})
