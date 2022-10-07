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
/// <reference types="cypress-xpath" />

import { DevPortalHomePage } from "../../support/devportal/pages/home/home-page";
import { Apis } from "../../support/devportal/pages/apis/apis-home";
import { ApiOverview } from "../../support/devportal/pages/apis/api-overview";
import { Utils } from "../../support/console/utils";
import { LoginPage as ConsoleLoginPage } from "../../support/console/pages/login-page";
import { LoginPage as DevportalLoginPage } from "../../support/devportal/pages/login/login-page";
import { ChoreoHomePage } from "../../support/console/pages/home/home-page";
import { DevPortalHelper } from "../../support/devportal/helpers/devportal-helper";
import { ComponentOverviewPage } from "../../support/console/pages/component/component-overview-page";
import { ComponentAPILifecycle } from "../../support/console/pages/component/component-manage-page";
import { ComponentListingPage } from "../../support/console/pages/component/component-listing-page";
import { ProjectListingPage } from "../../support/console/pages/projects/projects-listing-page";


describe("Create and deploy a component to test developer portal with public apis", () => {

    before(() => {
        ConsoleLoginPage.login();
    });

    it("Create and deploy a component", () => {
        const API_Name = Utils.generateComponentName("rest");
        const description = "Sample API for testing devportal public APIs";
        cy.task('setAPIName', API_Name);
        const projectName = Utils.generateProjectName();
        DevPortalHelper.createDeployRestApiComponent(API_Name, description, projectName);
        cy.task('setChoreoProjectName', projectName);
        ComponentAPILifecycle.selectSetting();
        ComponentAPILifecycle.verifyAPIVisibility('Private');
    });

    after(() => {
        ChoreoHomePage.logout();
    });
});

describe("Visit to developer portal organization public apis", () => {
    it("Check for available public apis", () => {
        DevportalLoginPage.visitToDevportalOrgPublicApis();
        DevPortalHomePage.verifyDevportalHomePagePublicView();
        DevPortalHomePage.navigateToApisPage();
        cy.task('getAPIName').then(apiName => {
            Apis.confirmAPIUnavailability(apiName)
        })
    });
});

describe("Visit to console an make the created API visibility to public", () => {

    before(() => {
        ConsoleLoginPage.login();
    });

    it("Update the API visibility to public", () => {
        cy.task('getChoreoProjectName').then(projectName => {
            ProjectListingPage.selectProject(projectName as string);
            cy.task('getAPIName').then(apiName => {
                ComponentListingPage.visitToAComponent(apiName as string);
                ComponentOverviewPage.navigateToManage();
                ComponentAPILifecycle.selectSetting();
                ComponentAPILifecycle.updateAPIVisibility('Public');
            });
        });
    });

    after(() => {
        ChoreoHomePage.logout();
    });
});

describe("Visit to developer portal organization public apis to confirm availability", () => {
    it("Check for available public apis to confirm availability", () => {
        DevportalLoginPage.visitToDevportalOrgPublicApis();
        DevPortalHomePage.navigateToApisPage();
        cy.task('getAPIName').then(apiName => {
            Apis.navigateToApiOverview(apiName as string);
            ApiOverview.confirmPublicAPIOverview();
            ApiOverview.confirmPublicAPIResourcePage();
        })
    });
});

describe("Delete created API", () => {
    before(() => {
        ConsoleLoginPage.login();
    });

    it("Delete created component", () => {
        cy.task('getAPIName').then(an => {
            let API_Name = an as string;
            ChoreoHomePage.navigateToComponents();
            ComponentListingPage.deleteComponent(API_Name);
        })
    });

    after(() => {
        ChoreoHomePage.logout();
    });
});
