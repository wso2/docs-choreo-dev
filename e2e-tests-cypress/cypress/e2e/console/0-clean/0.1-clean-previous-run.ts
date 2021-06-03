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

import { isOldValue, keyNamePrefix } from '../../../support/common/utils';
import { APIM_RESOURCE_PATH, APP_SVC_URL, ORG_NAME, PATH_SEPARATOR } from "../../../support/common/constants";
import { getApiName } from "../../../support/devportal/utils";

describe('Cleaning up', () => {

    before(() => {
        cy.consoleUserLogin();
    });

    after(() => {
        cy.userLogout();
    });

    it('Delete Apps that are created by previous run', () => {
        cy.request({
            method: "GET",
            form: true,
            url: `${APP_SVC_URL}/orgs/${ORG_NAME}/apps/`
        }).then((response) => {
            const data = response["body"];
            for (const value of data) {
                const appName = value["name"];
                const status = String(value['status']);
                if (status == "running") {
                    cy.undeployAppViaRESTAPICall(appName);
                }
                cy.cleanupApp(appName);
            }
        })
    });

    it('Delete on-prem keys that are created by previous run', () => {
        cy.request({
            method: "GET",
            form: true,
            url: `${APP_SVC_URL}/orgs/${ORG_NAME}/keys/`
        }).then((response) => {
            const data = response["body"];
            for (const value of data) {
                const keyName = value["displayName"];
                if (isOldValue(keyName) || keyName.startsWith(keyNamePrefix)) {
                    cy.cleanOnPremKey(keyName);
                }
            }
        })
    });

    it('Delete APIs that are created by previous run', () => {
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
                const data = response["body"]["list"];
                for (const value of data) {
                    const apiName = value["name"];
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
                        })
                    }
                }
            })
        });
    })
});
