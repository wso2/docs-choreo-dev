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

export class RestAPITemplate {
  static selectHttpAPITemplate() {
    cy.get('[data-testid="project-template-list-httpApi"]').click();
  }

  static createApiFromScratch(
    componentName: string,
    description: string,
    fiileID: string
  ) {
    cy.get('[role="dialog"] ul>div:nth-child(1)').click();
    cy.get('[name="name"]').clear().type(componentName);
    cy.get('input[name="description"]').clear().type(description);
    cy.get('[data-testid="create-api-from-scratch-submit"]').click();
    cy.intercept(Cypress.env("appSvcURL") + "/graphql").as("proj_create");
    this.interceptProjectDetails(fiileID);
    this.interceptRevision(fiileID)
  }

  private static interceptProjectDetails(fiileID: string) {
    // eslint-disable-next-line arrow-body-style

    cy.wait("@proj_create", { timeout: 100000 }).then((e) => {
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


private static interceptRevision(fileID){
  const url = "https://sts.preview-dv.choreo.dev/api/am/publisher/v2/apis/*?organizationId=*"
  https://sts.preview-dv.choreo.dev/api/am/publisher/v2/apis/61ee75e3829e4f312e1bc946?organizationId=fec0832e-94dd-4749-aa0f-7da5ed9e0a31
  cy.intercept(url).as('revision')

  cy.wait('@revision',{timeout:1200000}).then(inc=>{
    cy.log(inc.request.url)
  })
}





}
