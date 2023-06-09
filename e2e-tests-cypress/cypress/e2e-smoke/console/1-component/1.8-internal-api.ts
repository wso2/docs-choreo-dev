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

import { LoginPage } from "../../../support/console/pages/login-page";
import { RestAPIProxyTemplate } from "../../../support/console/pages/templates/rest-api-proxy-temp";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { APITest } from "../../../support/console/pages/apis/api-test";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { APIDeployment } from "../../../support/console/pages/apis/api-deployment";
import { APIDevelop } from "../../../support/console/pages/apis/api-develop";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { TryOut } from "../../../support/devportal/pages/apis/try-out";
import { Apis } from "../../../support/devportal/pages/apis/apis-home";
import { ApiCredentials } from "../../../support/devportal/pages/apis/apis-credentials";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { OK } from "../../../support/commons/http";
import { ProxyAPI } from "../../../support/interfaces/proxy-api";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { DevPortalHomePage } from "../../../support/devportal/pages/home/home-page";


before(() => {
    LoginPage.login();
});
after(() => {
    ChoreoHomePage.logout();
});


describe(`Verify internal api functionality`, () => {
    const API_NAME = Utils.generateComponentName("CYE2E");
    const API_BASE_PATH = Utils.generateBasePath();
    const API_VERSION = "1.0.0";

    const API_ENDPOINT = "https://jsonplaceholder.typicode.com";
    const OPERATION_USERS = "users";
    const PROJECT_DESCRIPTION = "sample stats project";
    const PROJECT_NAME = Utils.generateProjectName();

    let DEV_INVOKE_URL = "";
    let PROD_INVOKE_URL = "";


    const PROXY_API_NAME_DEV = Utils.generateComponentName("dev");
    const PROXY_API_VERSION_DEV = "1.0.0";
    const PROXY_API_BASEPATH_DEV = `/${PROXY_API_NAME_DEV}`;
    const PROXY_API_NAME_PROD = Utils.generateComponentName("prod");
    const PROXY_API_BASEPATH_PROD = `/${PROXY_API_NAME_PROD}`;
    const ACCESS_MODE_EXTERNAL = "External";
    const idpUser = "choreoe2etest";

    const internalProxy: ProxyAPI = {
        apiName: API_NAME,
        apiBasePath: API_BASE_PATH,
        version: API_VERSION,
        endpoint: API_ENDPOINT,
        isInternal: true,
    }

    const externalProxy: ProxyAPI = {
        apiName: PROXY_API_NAME_DEV,
        apiBasePath: PROXY_API_BASEPATH_DEV,
        version: PROXY_API_VERSION_DEV,
        endpoint: API_ENDPOINT,
        isInternal: false,
    }

    const externalProxyProd: ProxyAPI = {
        apiName: PROXY_API_NAME_PROD,
        apiBasePath: PROXY_API_BASEPATH_PROD,
        version: PROXY_API_VERSION_DEV,
        endpoint: API_ENDPOINT,
        isInternal: false,
    }


    it("Creating a project", () => {
        ProjectListingPage.createNewProject(PROJECT_NAME, PROJECT_DESCRIPTION);
    });

    it("Verify Rest API creation from existing endpoint", () => {
        ProjectOverviewPage.createHttpProxyAPI();
        RestAPIProxyTemplate.skipSource();
        RestAPIProxyTemplate.createProxyApi(internalProxy);

    });

    it("Add resource endpoints", () => {
        APIDevelop.addResources(OPERATION_USERS, Enums.HTTPMethod.GET);
    })


    it("Deploy api to dev", () => {
        ComponentOverviewPage.navigateToDeploy();
        APIDeployment.DeployToDev();
    })


    it("Verify REST API component promote to PROD", () => {
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.promoteProxyApiToProd();
    });



    it("Publish the API", () => {
        ComponentOverviewPage.navigateToManage();
        ComponentAPILifecycle.manageLifecycle();
        ComponentAPILifecycle.publishWithoutConnector();
    });

    it("Apply disable security config in DEV", () => {
        ComponentOverviewPage.navigateToManage();
        ComponentAPILifecycle.selectSetting();
        ComponentAPILifecycle.selectResources();
        ComponentAPILifecycle.selectEnvironment(Enums.Environment.DEVELOPMENT);
        ComponentAPILifecycle.selectRevision(Enums.Environment.DEVELOPMENT);
        ComponentAPILifecycle.editResource();
        ComponentAPILifecycle.selectResources();
        ComponentAPILifecycle.disableResourceSecurity(OPERATION_USERS);
        ComponentAPILifecycle.applyConfiguration();
        ComponentAPILifecycle.verifyDevRevision().should(
            "eq",
            Enums.Environment.DEVELOPMENT
        );
    });


    it("Apply disable security config in PROD", () => {
        ComponentAPILifecycle.selectEnvironment(Enums.Environment.PRODUCTION);
        ComponentAPILifecycle.selectRevision(Enums.Environment.PRODUCTION);
        ComponentAPILifecycle.editResource();
        ComponentAPILifecycle.selectResources();
        ComponentAPILifecycle.disableResourceSecurity(OPERATION_USERS);
        ComponentAPILifecycle.applyConfiguration();
    });

    it("Verify resource access without the security in DEV", () => {
        ComponentOverviewPage.navigateToTest();
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.DEVELOPMENT,
            Enums.HTTPMethod.GET,
            OPERATION_USERS
        ).then((curl) => {
            cy.get("#filled-disabled")
                .eq(0)
                .invoke("attr", "value")
                .then((invokeUrl) => {
                    DEV_INVOKE_URL = invokeUrl;
                });
            expect(Utils.isHostResolvable(curl.url) == false);
        });

    });
    it("Verify resource access without the security in PROD", () => {
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.PRODUCTION,
            Enums.HTTPMethod.GET,
            OPERATION_USERS
        ).then((curl) => {
            cy.get("#filled-disabled")
                .eq(0)
                .invoke("attr", "value")
                .then((invokeUrl) => {
                    PROD_INVOKE_URL = invokeUrl;
                });
            expect(Utils.isHostResolvable(curl.url) == false);
        });
    });

    // Proxy API with dev endpoint
    it("Verify 1st Proxy API creation using Internal API DEV endpoint", () => {
        ChoreoHomePage.navigateToHome();
        ProjectListingPage.selectProject(PROJECT_NAME);
        ProjectOverviewPage.addComponent();
        ProjectOverviewPage.createHttpProxyAPI();
        RestAPIProxyTemplate.skipSource();
        externalProxy.endpoint = DEV_INVOKE_URL;
        RestAPIProxyTemplate.createProxyApi(externalProxy);
    });

    it("Verify Add resource to 1st Proxy API", () => {
     
        APIDevelop.addResources(OPERATION_USERS, Enums.HTTPMethod.GET);
    });

    it("Verify 1st PROXY API component deployment", () => {
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.configureAndDeployProxyApiToDev();
    });

    // Invoke the Proxy API via curl, verify that Internal API is accessible to the Proxy API
    // by receiving a 200 response
    it("Verify 1st PROXY API resource access in DEV", () => {
        ComponentOverviewPage.navigateToTest();
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.DEVELOPMENT,
            Enums.HTTPMethod.GET,
            OPERATION_USERS,
        ).then((curl) => {
            Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
                expect(res.status).equal(200);
            });
        });
    });

    it("Verify 1st PROXY API component promote to PROD", () => {
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.promoteProxyApiToProd();
    });

    it("Verify 1st PROXY API resource access in PROD", () => {
        ComponentOverviewPage.navigateToTest();
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.PRODUCTION,
            Enums.HTTPMethod.GET,
            OPERATION_USERS,
        ).then((curl) => {
            Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
                expect(res.status).equal(200);
            });
        });
    });

    it("Verify 2nd Proxy API creation using Internal API Prod endpoint", () => {
        ChoreoHomePage.navigateToHome();
        ProjectListingPage.selectProject(PROJECT_NAME);
        ProjectOverviewPage.addComponent();
        ProjectOverviewPage.createHttpProxyAPI();
        RestAPIProxyTemplate.skipSource();
        externalProxyProd.endpoint = PROD_INVOKE_URL;
        RestAPIProxyTemplate.createProxyApi(externalProxyProd);
    });

    it("Verify Add resource to 2nd Proxy API", () => {
        APIDevelop.addResources(OPERATION_USERS, Enums.HTTPMethod.GET);
    });

    it("Verify 1st PROXY API component deployment", () => {
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.configureAndDeployProxyApiToDev();
    });

    // Invoke the Proxy API via curl, verify that Internal API is accessible to the Proxy API
    // by receiving a 200 response
    it("Verify 2nd PROXY API resource access in DEV", () => {
        ComponentOverviewPage.navigateToTest();
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.DEVELOPMENT,
            Enums.HTTPMethod.GET,
            OPERATION_USERS,
        ).then((curl) => {
            Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
                expect(res.status).equal(200);
            });
        });
    });

    it("Verify 2nd PROXY API component promote to PROD", () => {
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.promoteProxyApiToProd();
    });

    it("Verify 2nd PROXY API resource access in PROD", () => {
        ComponentOverviewPage.navigateToTest();
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.PRODUCTION,
            Enums.HTTPMethod.GET,
            OPERATION_USERS,
        ).then((curl) => {
            Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
                expect(res.status).equal(200);
            });
        });
    });


    it("Verify change access to Internal API to External ", () => {
        ChoreoHomePage.navigateToHome();
        ProjectListingPage.selectProject(PROJECT_NAME);
        ComponentListingPage.visitToAComponent(API_NAME);
        ComponentOverviewPage.navigateToManage();
        ComponentAPILifecycle.selectSetting();
        ComponentAPILifecycle.updateAPIAccessMode(ACCESS_MODE_EXTERNAL);
    });

    it("Verify resource access to external API in DEV", () => {
        ComponentOverviewPage.navigateToTest();
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.DEVELOPMENT,
            Enums.HTTPMethod.GET,
            OPERATION_USERS
        ).then((curl) => {
            Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
                expect(res.status).equal(200);
            });
        });
    });

    it("Verify resource access to external API in PROD", () => {
        TestHelper.testOnCurlDiscardPrevious(
            Enums.Environment.PRODUCTION,
            Enums.HTTPMethod.GET,
            OPERATION_USERS
        ).then((curl) => {
            Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
                expect(res.status).equal(200);
            });
        });
    });

    it("Verify API invocation in Devportal for external REST API component", () => {
        ComponentOverviewPage.navigateToManage();
        ComponentAPILifecycle.manageLifecycle();
        ComponentAPILifecycle.goToDeveloperPortalWithoutLogin(PROJECT_NAME,API_NAME,idpUser);
        DevPortalHomePage.navigateToApisPage();
        Apis.searchApiAndSelect(API_NAME, 1);
        ApiCredentials.navigateToEnvironment(Enums.Environment.PRODUCTION);
        ApiCredentials.generateCredentials(Enums.Environment.PRODUCTION);
        TryOut.navigateToTryOutMenu();
        TryOut.GenerateAccessToken();
        TryOut.SelectResource(OPERATION_USERS);
        TryOut.TryoutAPI();
        TryOut.ExecuteResourceFunction();
        TryOut.ValidateResponse("200");
    });

    // Suspend prod/dev deployed Internal REST API
    it("Verify suspending Internal REST API component", () => {
        LoginPage.reLoginToChoreo();
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.stopAllDeployment();
    });

    // Suspend prod/dev deployed PROXY API for dev URL
    it("Verify suspending PROXY API for DEV URL component", () => {
        ChoreoHomePage.navigateToHome();
        ProjectListingPage.selectProject(PROJECT_NAME);
        ComponentListingPage.visitToAComponent(PROXY_API_NAME_DEV);
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.stopAllDeployment();
    });

    // Suspend prod/dev deployed PROXY API for prod URL
    it("Verify suspending PROXY API for PROD URL component", () => {
        ChoreoHomePage.navigateToHome();
        ProjectListingPage.selectProject(PROJECT_NAME);
        ComponentListingPage.visitToAComponent(PROXY_API_NAME_PROD);
        ComponentOverviewPage.navigateToDeploy();
        ComponentDeployPage.stopAllDeployment();
    });

})