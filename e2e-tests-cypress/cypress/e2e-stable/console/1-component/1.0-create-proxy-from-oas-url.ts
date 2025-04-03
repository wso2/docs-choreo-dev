/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Enums, SecurityScheme, UsagePlan } from "../../../support/commons/enums";
import {
  Proxy,
  ProxyMetaData,
} from "../../../support/console/entities/component/proxy-component";
import { Project } from "../../../support/console/entities/project/project";
import { console } from "../../../support/console/console";
import { OK } from "../../../support/commons/http";
import { devPortal } from "../../../support/console/devportal";
import { Utils } from "../../../support/commons/utils";
import { Application } from "../../../support/console/entities/application/application";

describe("Create proxy using existing url", () => {
  const PROJECT_DESCRIPTION = "Proxy from oas URL";
  const RESOURCE = "store/inventory";
  const NEW_RESOURCE = "pet/{petId}";
  const QUERY_PARAM = "petId";
  const QUERY_PARAM_VALUE = "1";
  const EXPECTED_VALUE = "sold";
  const NEW_RESOURCE_EXPECTED_VALUE = "id";
  const URL = "https://petstore3.swagger.io/api/v3/openapi.json";
  const ENDPOINT_URL =
    "https://9f3f5ca2-c1f2-43e7-afbe-a15714138b57-dev.e1-us-east-azure.choreoapis.dev/mgch/petstore/petstore-9f2/v1.0";
  let project: Project;
  let proxy: Proxy;
  let application: Application;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a proxy from oas url", () => {
    project
      .createProxyComponent({
        version: "1.0",
        endpointUrl: ENDPOINT_URL,
        oasUrl: URL,
      })
      .then((comp) => {
        proxy = comp;
        // Since we are switching domains when navigating to devportal url we will no longer have access to the proxy object
        // So we need to save the proxy metadata in nodejs global state using below cy.task() to access it later
        cy.task("setData", {
          key: Cypress.spec.name, // Unique key to store the data, in this case spec name is sufficient
          value: proxy.getMetaData(),
        });
      });
  });

  it("Remove additional resources and save", () => {
    proxy.removeResources([
      "panel-/pet/{petId}/uploadImage/post-header",
      "panel-/pet/post-header",
      "panel-/pet/put-header",
      "panel-/pet/findByStatus/get-header",
      "panel-/pet/findByTags/get-header",
      "panel-/pet/{petId}/get-header",
      "panel-/pet/{petId}/post-header",
      "panel-/pet/{petId}/delete-header",
    ]);

    proxy.removeResources([
      "panel-/user/createWithArray/post-header",
      "panel-/user/createWithList/post-header",
      "panel-/user/{username}/get-header",
      "panel-/user/{username}/put-header",
      "panel-/user/{username}/delete-header",
    ]);

    proxy.removeResources([
      "panel-/user/post-header",
      "panel-/user/login/get-header",
      "panel-/user/logout/get-header",
    ]);
  });

  it("Enable OAuth2 security and deploy", () => {
    proxy.enableSecuritySchemesAndDeploy([SecurityScheme.OAuth2]);
  });

  it("Promote proxy", () => {
    proxy.promote();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, RESOURCE)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      });
  });

  it("Verify test functionality using generated curl in Dev", () => {
    proxy
      .testCurl(Enums.Environment.DEVELOPMENT, Enums.HTTPMethod.GET, RESOURCE)
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.body).to.have.property(EXPECTED_VALUE);
      });
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, RESOURCE)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      });
  });

  it("Verify test functionality using generated curl in Prod", () => {
    proxy
      .testCurl(Enums.Environment.PRODUCTION, Enums.HTTPMethod.GET, RESOURCE)
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.body).to.have.property(EXPECTED_VALUE);
      });
  });

  it("Create new version of the Proxy", () => {
    proxy.addVersion();
  });

  it("Add a resource to new version", () => {
    proxy.addResources([{ path: NEW_RESOURCE, verbs: [Enums.HTTPMethod.GET] }]);
  });

  it("Deploy new version of proxy", () => {
    proxy.deploy();
  });

  it("Promote new version of proxy", () => {
    proxy.promote();
  });

  it("Verify test functionality of new version in Dev", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, RESOURCE)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      });

    proxy
      .testSwaggerConsole(
        Enums.Environment.DEVELOPMENT,
        NEW_RESOURCE,
        QUERY_PARAM,
        QUERY_PARAM_VALUE
      )
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(NEW_RESOURCE_EXPECTED_VALUE);
      });
  });

  it("Verify test functionality of new version in Prod", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, RESOURCE)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(EXPECTED_VALUE);
      });

    proxy
      .testSwaggerConsole(
        Enums.Environment.PRODUCTION,
        NEW_RESOURCE,
        QUERY_PARAM,
        QUERY_PARAM_VALUE
      )
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
        expect(res.response).to.contain(NEW_RESOURCE_EXPECTED_VALUE);
      });
  });

  it("Publish proxy to Dev portal", () => {
    proxy.publish();
  });

  it("Navigate to Dev portal", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        devPortal.loginToDevPortal();
        // Recreate Proxy object using previously saved metadata
        cy.task("getData", Cypress.spec.name).then((metaData) => {
          proxy = Proxy.fromMetaData(metaData as ProxyMetaData);
        });
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Create a consumer application", () => {
    application = proxy.createApplication_DevPortal();
  });

  it("Generate subscription credentials", () => {
    application.generateCredentials(Enums.Environment.SANDBOX);
    application.generateCredentials(Enums.Environment.PRODUCTION);
  });

  it("Add subscription", () => {
    application.addSubscription(proxy.getName(), UsagePlan.Bronze);
  });

  it("Find API in devportal custom domain", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        devPortal.searchApi(proxy.getName());
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Tryout proxy in Dev portal", () => {
    Utils.isTestConsoleOnly().then((testConsoleOnly) => {
      if (!testConsoleOnly) {
        proxy.testSwaggerConsole_DevPortal({ resource: RESOURCE, keyType: Enums.ApiTryoutKeyType.APPLICATION_KEY });
      } else {
        cy.log(`Skipping Devportal step due to testConsoleOnly: ${testConsoleOnly}`);
      }
    });
  });

  it("Stop proxy", () => {
    proxy.navigateToComponentInConsole();
    proxy.stopDeployment();
    proxy.stopPromotion();
  });
});
