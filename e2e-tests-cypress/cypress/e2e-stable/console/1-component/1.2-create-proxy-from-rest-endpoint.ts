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

import { console } from "../../../support/console/console";
import { Project } from "../../../support/console/entities/project/project";
import { Proxy } from "../../../support/console/entities/component/proxy-component";
import { Enums } from "../../../support/commons/enums";
import { OK } from "../../../support/commons/http";

describe("Create Proxy from REST endpoint", () => {
  const PROJECT_DESCRIPTION = "API Proxy for REST Endpoint";
  const ENDPOINT_URL =
    "https://9f3f5ca2-c1f2-43e7-afbe-a15714138b57-dev.e1-us-east-azure.choreoapis.dev/mgch/users/endpoint-9090-803/v1.0";
  const OPERATION_USERS = "users";

  const HEADER_NAME = "x-header-test";
  const HEADER_VALUE = "test";
  const HEADER_NAME_2 = "x-header-test2";
  const HEADER_VALUE_2 = "test2";
  const HEADER_VALUE_3 = "test3";

  let project: Project;
  let proxy: Proxy;

  it("Login to Console", () => {
    console.login();
  });

  it("Creating a project", () => {
    project = console.createNewProject(PROJECT_DESCRIPTION);
  });

  it("Creating a proxy from scratch", () => {
    project
      .createProxyComponent({
        version: "1.0",
        endpointUrl: ENDPOINT_URL,
      })
      .then((comp) => {
        proxy = comp;
      });
  });

  it("Remove default resources", () => {
    proxy.removeDefaultResources();
  });

  it("Add resource to proxy", () => {
    proxy.addResources([
      { path: OPERATION_USERS, verbs: [Enums.HTTPMethod.GET] },
    ]);
  });

  it("Add first mediation policy to the resource", () => {
    proxy.addResponseFlowHeaderPolicy(
      OPERATION_USERS,
      Enums.HTTPMethod.GET,
      HEADER_NAME,
      HEADER_VALUE
    );
  });

  it("Deploy proxy with initial mediation policy", () => {
    proxy.deploy();
  });

  it("Verify test functionality using Swagger UI in Dev", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Dev", () => {
    proxy
      .testCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        OPERATION_USERS
      )
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.headers[HEADER_NAME]).to.be.equal(HEADER_VALUE);
      });
  });

  it("Promote proxy", () => {
    proxy.promote();
  });

  it("Verify test functionality using Swagger UI in Prod", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Prod", () => {
    proxy
      .testCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        OPERATION_USERS
      )
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.headers[HEADER_NAME]).to.be.equal(HEADER_VALUE);
      });
  });

  it("Verify adding second mediation policy", () => {
    proxy.addResponseFlowHeaderPolicy(
      OPERATION_USERS,
      Enums.HTTPMethod.GET,
      HEADER_NAME_2,
      HEADER_VALUE_2
    );
  });

  it("Deploy proxy with 2nd mediation policy", () => {
    proxy.deploy();
  });

  it("Verify test functionality using Swagger UI in Dev with 2nd policy", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Dev with 2nd policy", () => {
    proxy
      .testCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        OPERATION_USERS
      )
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.headers[HEADER_NAME]).to.be.equal(HEADER_VALUE);
        expect(res.headers[HEADER_NAME_2]).to.be.equal(HEADER_VALUE_2);
      });
  });

  it("Verify test functionality using Swagger UI in Prod where 2nd policy is not deployed", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Prod where 2nd policy is not deployed", () => {
    proxy
      .testCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        OPERATION_USERS
      )
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.headers[HEADER_NAME]).to.be.equal(HEADER_VALUE);
        expect(res.headers[HEADER_NAME_2]).to.be.undefined;
      });
  });

  it("Update 1st policy", () => {
    proxy.editResponseFlowHeaderPolicy(
      OPERATION_USERS,
      Enums.HTTPMethod.GET,
      0,
      HEADER_VALUE_3
    );
  });

  it("Deploy proxy with updated mediation policy", () => {
    proxy.deploy();
  });

  it("Verify test functionality using Swagger UI in Dev with updated policy", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.DEVELOPMENT, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Dev with updated policy", () => {
    proxy
      .testCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        OPERATION_USERS
      )
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.headers[HEADER_NAME]).to.be.equal(HEADER_VALUE_3);
        expect(res.headers[HEADER_NAME_2]).to.be.equal(HEADER_VALUE_2);
      });
  });

  it("Promote proxy with updated mediation policy", () => {
    proxy.promote();
  });

  it("Verify test functionality using Swagger UI in Prod with updated policy", () => {
    proxy
      .testSwaggerConsole(Enums.Environment.PRODUCTION, OPERATION_USERS)
      .then((res) => {
        expect(res.statusCode).to.be.equal(OK.toString());
      });
  });

  it("Verify test functionality using generated curl in Prod with updated policy", () => {
    proxy
      .testCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        OPERATION_USERS
      )
      .then((res) => {
        expect(res.status).equal(OK);
        expect(res.headers[HEADER_NAME]).to.be.equal(HEADER_VALUE_3);
        expect(res.headers[HEADER_NAME_2]).to.be.equal(HEADER_VALUE_2);
      });
  });

  it("Stop component", () => {
    proxy.stopDeployment();
    proxy.stopPromotion();
  });
});
