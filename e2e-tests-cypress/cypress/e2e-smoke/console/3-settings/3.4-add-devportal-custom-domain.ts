/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { DomainsComponents } from "../../../support/console/pages/component/common/domains-components";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";

describe("Add developer portal custom domain", () => {
    const CUSTOM_DOMAIN = Cypress.env("devportalCustomDomain");

    before(() => {
        LoginPage.login();
        ChoreoHomePage.navigateToSettings();

        DomainsComponents.navigateToDomainsSettings();
        DomainsComponents.navigateToDevPortalCustomDomain();
        DomainsComponents.deleteDevportalDomainIfExists(CUSTOM_DOMAIN);
    });
    after(() => {
        ChoreoHomePage.logout();
    });

    it("Create a Domain", () => {
        DomainsComponents.createDevportalDomain(CUSTOM_DOMAIN);
    });

    it("Access developer port with custom domain", () => {
        DomainsComponents.accessDevportalWithCustomDomain(CUSTOM_DOMAIN);
    });

    it("Delete created custom domain", () => {
        DomainsComponents.navigateToDomainsSettings();
        DomainsComponents.navigateToDevPortalCustomDomain();
        DomainsComponents.deleteCreatedCustomDomain(CUSTOM_DOMAIN);
    });
});
