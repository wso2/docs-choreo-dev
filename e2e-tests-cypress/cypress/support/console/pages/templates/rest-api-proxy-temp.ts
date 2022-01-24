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
export class RestAPIProxyTemplate {
  static SelectHttpProxyAPITemplate() {
    cy.get('[data-testid="project-template-list-httpProxyApi"]').click();
  }

  static designANewRESTAPI(
    apiName,
    apiVersion,
    apiBasePath,
    endpoint,
    fiileID
  ) {
    cy.get('[role="dialog"] ul>div:nth-child(1)').click();
    cy.get('[data-testid="api-name"] input').clear().type(apiName);
    cy.get('[data-testid="api-version"] input').clear().type(apiVersion);
    if (apiBasePath) {
      cy.get('[data-testid="api-basepath"] input').clear().type(apiBasePath);
    }
    cy.get('[data-testid="api-endpoint"] input').clear().type(endpoint);
    cy.get("button>span").contains("Create").click();
    cy.intercept(Cypress.env("appSvcURL") + "/graphql").as("proj_create");
    this.interceptProjectDetails(fiileID);
  }

  private static interceptProjectDetails(fiileID: string) {
    // eslint-disable-next-line arrow-body-style

    cy.wait("@proj_create", { timeout: 100000 }).then((e) => {
      cy.log(JSON.stringify(e.response.body.data.component));
      const { id, projectId, handler } = e.response.body.data.createComponent;

      const authdata = {
        header: {
          authorization: e.request.headers.authorization,
          "content-type": "application/json",
        },
        id,
        projectId,
        handler,
      };
      cy.task("writeTestData", {
        fileName: fiileID,
        key: "authData",
        value: authdata,
      });
    });
  }

  static createOpenApi(API_NAME, filepath, file_id, develop, overview) {
    cy.get('[role="dialog"] ul>div:nth-child(2)').click();
    cy.get('[data-testid="open-api-file"]').click();
    cy.get('input[type="file"]').attachFile(filepath);
    cy.get('[id="next"]').click();
    cy.get('[data-testid="api-name"]>div>input').clear().type(API_NAME);
    cy.get('[data-testid="api-basepath"]>div>input').clear().type(API_NAME);
    cy.get('[data-testid="api-endpoint"]').within(() => {
      cy.get("p").contains("Mui-error").should("not.exist");
    });

    cy.get("button>span").contains("Create").click();

    // cy.intercept(Cypress.env("appSvcURL") + "/graphql").as("createApi");

    // cy.wait("@createApi", { timeout: 80000 }).then((interception) => {
    //   const authData = { projectId: "" };
    //   authData.projectId = interception.response.body.data.createComponent.id;

    //   cy.task("writeTestData", {
    //     fileName: file_id,
    //     key: "authData",
    //     value: authData,
    //   });
    //   cy.url().should("include", develop + overview);
    //   cy.get('[data-testid="develop-resources-header"')
    //     .contains("Resources")
    //     .should("be.visible");
    //   cy.log("Successfully created API from open API specification");
    // });
  }
}
