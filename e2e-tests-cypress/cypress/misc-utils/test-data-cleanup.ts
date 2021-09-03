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

import { getSelectedOrgHandle } from "../support/common/utils";

/// <reference types="cypress" />

describe("Clean up all data introduced by E2E tests", () => {

    it("Clean up data", () => {
        cy.consoleUserLogin().then((user) => {
            const selectedOrgHandle = getSelectedOrgHandle(user);
            const org = user?.orgs.find((org) => org.handle === selectedOrgHandle);
            cy.clearAllTestData(org);
        });
    });
});
